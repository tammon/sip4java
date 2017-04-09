/*
 * Sercos Internet Protocol (SIP) version 1
 * Copyright (c) 2017. tammon (Tammo Schwindt)
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.tammon.sip;

import net.tammon.sip.exceptions.SipException;
import net.tammon.sip.exceptions.SipInternalException;
import net.tammon.sip.exceptions.SipServiceNotSupportedException;
import net.tammon.sip.exceptions.SipSocketTimeoutException;
import net.tammon.sip.packets.*;

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
     * <p>
     * This connection can have a keepAlive flag which will maintain the SIP connection even if no request gets sent
     * for a longer duration than the leaseTimeout. This will prevent the drive of closing the socket connection after
     * the lease timeout. If a keep alive is used it is mandatory to call the disconnect() method to stop the keep alive
     * functionality. If disconnect() is not called the tcp connection will prevent your program from exiting.
     *
     * @param host      domain name or IP Address of the drive
     * @param keepAlive flag if the connection should stay alive even if no request are sent
     * @throws Exception in case of communication problems
     */
    public TCPConnection(String host, boolean keepAlive) throws SipException {
        try {
            // Load Default Properties from properties file and initialize
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("sipDefault.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            this.ipAddress = InetAddress.getByName((host == null) ? properties.getProperty("driveIp") : host);
            this.sipPort = new Integer(properties.getProperty("sipPort"));
            this.leaseTimeout = new Integer(properties.getProperty("leaseTimeout"));
            this.busyTimeout = new Integer(properties.getProperty("busyTimeout"));
            this.maxDelay = new Integer(properties.getProperty("maxDelay"));
            this.sipVersion = new Integer(properties.getProperty("sipVersion"));

            // Create new Socket Connection
            this.refreshSocketConnection();
            this.connectSip();
            if (keepAlive) this.restartKeepAliveTimer();
        } catch (UnknownHostException whe) {
            throw new SipException("Cannot resolve hostname. This is probably due to a misspelled hostname or bad dns configuration of host", whe);
        } catch (IOException e) {
            throw new SipInternalException("A problem occurred while trying to start Sip TCP Connection", e);
        }
    }

    /**
     * Establishes a TCP connection to a sercos device with the standard IP address of an IndraDrive (192.168.0.1)
     * This connection has no keepAlive and will timeout if no input request comes in for longer then the standard leaseTimeout of 10s
     *
     * @throws SipException in case of communication problems
     */
    public TCPConnection() throws SipException {
        this(null, false);
    }

    /**
     * Establishes a TCP connection to a sercos device with the given IP address
     * This connection has no keepAlive and will timeout if no input request comes in for longer then the standard leaseTimeout of 10s
     *
     * @param host domain name or IP Address of the drive
     * @throws Exception in case of communication problems
     */
    public TCPConnection(String host) throws SipException {
        this(host, false);
    }

    /**
     * Establishes a new sip connection by reconnecting the socket and the sercos device.
     * Resets the list of supported messages
     *
     * @throws SocketTimeoutException in case of a socket timeout
     * @throws SipInternalException in case of any not foreseen exception occurs
     */
    private synchronized void refreshSocketConnection() throws SipSocketTimeoutException {
        this.transactionId = 0;
        this.socketConnection = new Socket();
        try {
            this.socketConnection.connect(new InetSocketAddress(this.ipAddress, this.sipPort), leaseTimeout);
            this.dataOutputStream = new DataOutputStream(this.socketConnection.getOutputStream());
            this.dataInputStream = new DataInputStream(this.socketConnection.getInputStream());
            this.supportedMessages = null;
        } catch (SocketTimeoutException e) {
            throw new SipSocketTimeoutException("Drive does not respond to socket request. Probably wrong IP or not on network...");
        } catch (IOException e) {
            throw new SipInternalException("An internal S/IP Exception occured", e);
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
     * @throws SipException in case of communication problems
     */
    private synchronized void tcpSendAndReceive(Request request, Response response) throws IOException {
        if (!Objects.isNull(this.supportedMessages)
                && !this.supportedMessages.contains(request.getPacketHead().getMessageType()))
            throw new SipServiceNotSupportedException("The requested operation " + request.getClass().getSimpleName() + " is not in the drive's list of supported messages");
        else try {
            this.dataOutputStream.write(request.getTcpMsgAsByteArray());
        } catch (IOException e) {
            throw new SipInternalException("Cannot write output stream data to S/IP device", e);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int readLength;
        do {
            readLength = dataInputStream.read(buffer);
            outputStream.write(buffer, 0, readLength + 1);
        } while (readLength >= 1024);

        byte[] rawResponse = outputStream.toByteArray();
        Head header = null;
        header = new Head(rawResponse);
        // Check if we got the right response to our request
        if (header.getTransactionId() != request.getTransactionId())
            throw new SipException("The response transaction ID "
                    + header.getTransactionId()
                    + " doesn't match the request transaction ID "
                    + request.getTransactionId());

        // Check if Drive threw an communication exception
        if (header.getMessageType() == 67) {
            this.refreshSocketConnection();
            ExceptionResponse exceptionResponse = new ExceptionResponse(rawResponse);
            if (exceptionResponse.getCommonErrorCode() == CommonErrorCodes.UNKNOWN_MESSAGE_TYPE)
                throw new SipServiceNotSupportedException("Message type not supported: Drive does not support the requested operation " + request.getClass().getSimpleName());
            throw new SipException("Drive threw Communication Exception."
                    + ((exceptionResponse.getCommonErrorCode() == CommonErrorCodes.SERVICESPECIFIC)
                    ? (" SIP-SpecificErrorCode: " + exceptionResponse.getSpecificErrorCode())
                    : (" SIP-CommonErrorCode: " + exceptionResponse.getCommonErrorCode())));
        } else if (header.getMessageType() == response.getMessageType()) {
            response.setData(rawResponse);
        } else throw new SipInternalException("Invalid Message Type Response");
    }

    /**
     * This method sends a sip connection request to the sercos device and handles the incoming response
     *
     * @throws Exception in case of communication problems
     */
    private void connectSip() {
        Connect request = new Connect(this.getNewTransactionId(), this.sipVersion, this.busyTimeout, this.leaseTimeout);
        ConnectResponse response = new ConnectResponse();
        // todo: Remove statement by more meaningful exception
        try {
            this.tcpSendAndReceive(request, response);
        } catch (IOException e) {
            throw new SipException("communication problem", e);
        }
        this.supportedMessages = IntStream.of(response.getSupportedMessageTypes()).boxed().collect(Collectors.toList());
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
     * This method is the general abstraction of all public ReadOnlyData methods.
     * It actually creates the request and response bodies and triggers the TCP send and receive.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the {@link ReadOnlyDataResponse} which is received after the tcp request
     * @throws Exception if any communication or data handling problem occurs
     */
    public Data readData(int slaveIndex, int slaveExtension, String idn) throws Exception {
        ReadOnlyData request = new ReadOnlyData(this.getNewTransactionId(), (short) slaveIndex, (short) slaveExtension, idn);
        ReadOnlyDataResponse response = new ReadOnlyDataResponse();
        this.tcpSendAndReceive(request, response);
        return response.getData();
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

    private void restartKeepAliveTimer() {
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
    @Override
    public void disconnect() {
        try {
            this.keepAliveTimer.cancel();
            this.keepAliveTimer.purge();
            this.socketConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
