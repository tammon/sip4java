/*
 * Sercos Internet Protocol (SIP) version 1
 * Copyright (C) 2017. tammon (Tammo Schwindt)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.tammon.sip;

import net.tammon.sip.packets.*;
import net.tammon.sip.packets.parts.CommonErrorCodes;
import net.tammon.sip.packets.parts.Head;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The TCPConnection class implements the SipConnection Interface and creates a sip connection via the TCP/IP protocol
 */
public class TCPConnection implements SipConnection {
    private InetAddress ipAddress;
    private int maxDelay, leaseTimeout, busyTimeout, sipPort, sipVersion;
    private int transactionId = 0;
    private boolean connected = false;
    private List<Integer> supportedMessages;
    private Socket socketConnection;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private Timer keepAliveTimer;

    /**
     * Establishes a TCP connection to a sercos device with given IP Address
     *
     * This connection can have a keepAlive flag which will maintain the SIP connection even if no request gets sent
     * for a longer duration than the leaseTimeout. This will prevent the drive of closing the socket connection after
     * the lease timeout. If a keep alive is used it is mandatory to call the disconnect() method to stop the keep alive
     * functionality. If disconnect() is not called the tcp connection will prevent your program from exiting.
     *
     * @param host domain name or IP Address of the drive
     * @param keepAlive flag if the connection should stay alive even if no request are sent
     * @throws Exception in case of communication problems
     */
    public TCPConnection(String host, boolean keepAlive) throws Exception {

        // Load Default Properties from properties file and initialize
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("sipDefault.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        this.sipPort = new Integer(properties.getProperty("sipPort"));
        this.ipAddress = InetAddress.getByName((host == null) ? properties.getProperty("driveIp") : host);
        this.leaseTimeout = new Integer(properties.getProperty("leaseTimeout"));
        this.busyTimeout = new Integer(properties.getProperty("busyTimeout"));
        this.maxDelay = new Integer(properties.getProperty("maxDelay"));
        this.sipVersion = new Integer(properties.getProperty("sipVersion"));

        // Create new Socket Connection
        this.refreshSocketConnection();
        this.connectSip();
        if(keepAlive) this.restartKeepAliveTimer();
    }

    /**
     * Establishes a TCP connection to a sercos device with the standard IP address of an IndraDrive (192.168.0.1)
     * This connection has no keepAlive and will timeout if no input request comes in for longer then the standard leaseTimeout of 10s
     *
     * @throws Exception in case of communication problems
     */
    public TCPConnection() throws Exception {
        this(null, false);
    }

    /**
     * Establishes a TCP connection to a sercos device with the given IP address
     * This connection has no keepAlive and will timeout if no input request comes in for longer then the standard leaseTimeout of 10s
     *
     * @param host domain name or IP Address of the drive
     * @throws Exception in case of communication problems
     */
    public TCPConnection(String host) throws Exception {
        this(host, false);
    }

    /**
     * Establishes a new sip connection by reconnecting the socket and the sercos device.
     * Resets the list of supported messages
     *
     * @throws Exception in case of communication problems
     */
    private synchronized void refreshSocketConnection() throws Exception {
        this.transactionId = 0;
        this.socketConnection = new Socket();
        try {
            this.socketConnection.connect(new InetSocketAddress(this.ipAddress, this.sipPort), leaseTimeout);
            this.dataOutputStream = new DataOutputStream(this.socketConnection.getOutputStream());
            this.dataInputStream = new DataInputStream(this.socketConnection.getInputStream());
            this.supportedMessages = null;
        } catch (SocketTimeoutException e) {
            throw new SocketTimeoutException("Drive does not respond to socket request. Probably wrong IP or not on network...");
        }
    }

    /**
     * Generates synchronized a new transaction id for a new sip interaction by increasing the current transaction id by one.
     *
     * @return new transaction id
     */
    private synchronized int getNewTransactionId() {
        return this.transactionId++;
    }

    /**
     * This method is the general abstraction for all sip tcp communication between the library and the sercos device.
     * It takes tcp request and response packets of the sip library. It sends the request and returns the response.
     * The tcp send and receive logic is fully synchronized to avoid multiple instances and therefor requests at a time.
     *
     * @param request  sip request tcp packet
     * @param response sip response tcp packet type (generally empty initialized object)
     * @return sip response tcp packet of the given type with the packet data set to the object
     * @throws Exception in case of communication problems
     */
    private synchronized Response tcpSendAndReceive(Request request, Response response) throws Exception {
        if (!Objects.isNull(this.supportedMessages) && !this.supportedMessages.contains(request.getPacketHead().getMessageType()))
            throw new UnknownServiceException("The requested operation " + request.getClass().getSimpleName() + " is not in the drive's list of supported messages");
        else this.dataOutputStream.write(request.getTcpMsgAsByteArray());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        while (true) {
            int readLength = dataInputStream.read(buffer);
            outputStream.write(buffer, 0, readLength + 1);
            if (readLength < 1024) break;
        }

        byte[] rawResponse = outputStream.toByteArray();
        Head header = new Head(rawResponse);

        // Check if we got the right response to our request
        if (header.getTransactionId() != request.getTransactionId()) throw new Exception("The response transaction ID "
                + header.getTransactionId()
                + " doesn't match the request transaction ID "
                + request.getTransactionId());

        // Check if Drive threw an communication exception
        if (header.getMessageType() == 67) {
            this.refreshSocketConnection();
            ExceptionResponse exceptionResponse = new ExceptionResponse(rawResponse);
            if (exceptionResponse.getPacketBody().getCommonErrorCode() == CommonErrorCodes.UNKNOWN_MESSAGE_TYPE)
                throw new UnknownServiceException("Message type not supported: Drive does not support the requested operation " + request.getClass().getSimpleName());
            throw new ProtocolException("Drive threw Communication Exception."
                    + ((exceptionResponse.getPacketBody().getCommonErrorCode() == CommonErrorCodes.SERVICESPECIFIC)
                    ? (" SIP-SpecificErrorCode: " + exceptionResponse.getPacketBody().getSpecificErrorCode())
                    : (" SIP-CommonErrorCode: " + exceptionResponse.getPacketBody().getCommonErrorCode())));
        } else if (header.getMessageType() == response.getMessageType()) {
            response.setData(rawResponse);
            return response;
        } else throw new Exception("Invalid Message Type Response");
    }

    /**
     * This method sends a sip connection request to the sercos device and handles the incoming response
     *
     * @throws Exception in case of communication problems
     */
    private void connectSip() throws Exception {
        Connect request = new Connect(this.getNewTransactionId(), this.sipVersion, this.busyTimeout, this.leaseTimeout);
        ConnectResponse response = (ConnectResponse) this.tcpSendAndReceive(request, new ConnectResponse());
        this.supportedMessages = IntStream.of(response.getPacketBody().getSupportedMessageTypes()).boxed().collect(Collectors.toList());
        this.connected = true;
    }


    /**
     * Checks if the sercos device responds to a ping sip message
     *
     * @return true if the device responds, false if it doesn't
     */
    private boolean respondsToPing() {
        Ping ping = new Ping(this.getNewTransactionId());
        try {
            this.tcpSendAndReceive(ping, new Pong());
            return true;
        } catch (SocketTimeoutException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public String readDataAsString(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toString();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public byte readDataAsByte(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toByte();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public short readDataAsShort(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toShort();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public int readDataAsInt(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toInt();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public long readDataAsLong(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toLong();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public float readDataAsFloat(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toFloat();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public double readDataAsDouble(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toDouble();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public byte[] readDataAsByteArray(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toByteArray();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public short[] readDataAsShortArray(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toShortArray();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public int[] readDataAsIntArray(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toIntArray();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public long[] readDataAsLongArray(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toLongArray();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public float[] readDataAsFloatArray(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toFloatArray();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public double[] readDataAsDoubleArray(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().toDoubleArray();
    }

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     * It reads out the raw data of the response as raw data byte array.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    @Override
    public byte[] readDataAsRawByteArray(int slaveIndex, int slaveExtension, String idn) throws Exception {
        return this.readData(slaveIndex, slaveExtension, idn).getPacketBody().getData().getRawData();
    }

    /**
     * This method is the general abstraction of all public ReadOnlyData methods.
     * It actually creates the request and response bodies and triggers the TCP send and receive.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the {@link ReadOnlyDataResponse} which is received after the tcp request
     * @throws Exception if any communication or data handling problem occurs
     */
    private ReadOnlyDataResponse readData(int slaveIndex, int slaveExtension, String idn) throws Exception {
        ReadOnlyData request = new ReadOnlyData(this.getNewTransactionId(), (short) slaveIndex, (short) slaveExtension, idn);
        return (ReadOnlyDataResponse) this.tcpSendAndReceive(request, new ReadOnlyDataResponse());
    }

    /**
     * Checks whether or not the TCP connection to the sercos slave is still connected
     *
     * @return true if the connection is alive
     */
    @Override
    public boolean isConnected() {
        return connected && this.socketConnection.isConnected();
    }

    /**
     * Returns a list of the supported message types of the sercos device.
     * This list is initially sent by the device during the sip connection process.
     *
     * @return the list of supported message types
     */
    @Override
    public List<Integer> getSupportedMessages() {
        return supportedMessages;
    }

    /**
     * Returns the IP address of the sercos device which is used by this TCP connection.
     *
     * @return IP address of sercos device as {@link InetAddress}
     */
    @Override
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    /**
     * Returns the used sercos device port of the current sip connection
     *
     * @return used sercos device port
     */
    @Override
    public int getSipPort() {
        return sipPort;
    }

    /**
     * Returns the used version of the Sercos Internet Protocol
     *
     * @return version of Sercos Internet Protocol
     */
    @Override
    public int getSipVersion() {
        return sipVersion;
    }

    private void restartKeepAliveTimer(){
        this.keepAliveTimer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                respondsToPing();
            }
        };
        this.keepAliveTimer.schedule(timerTask, Math.round(this.leaseTimeout * 0.7));
    }

    /**
     * Stops the keep alive loop and closes the socket connection to the sercos device
     */
    public void disconnect(){
        try {
            this.keepAliveTimer.cancel();
            this.keepAliveTimer.purge();
            this.socketConnection.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
