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
import net.tammon.sip.packets.parts.ExceptionBody;
import net.tammon.sip.packets.parts.Head;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.*;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TCPConnection implements SipConnection {
    private InetAddress ipAddress;
    private int maxDelay, leaseTimeout, busyTimeout, sipPort, sipVersion;
    private int transactionId = 0;
    private boolean connected = false;
    private List<Integer> supportedMessages;
    private Socket socketConnection;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public TCPConnection(String host) throws Exception {

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

        // check if the Drive responds to SIP-Ping before connecting
        try {
            if (this.respondsToPing()) this.connectSip();
        } catch (UnknownServiceException e) {
            e.printStackTrace(); //todo: use logging instead of printing the exception
        }
        this.connectSip();
    }

    public TCPConnection() throws Exception {
        this(null);
    }

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

    private synchronized int getNewTransactionId (){
        return this.transactionId++;
    }

    private synchronized Response tcpSendAndReceive(Request request, Response response) throws Exception {

        if (!Objects.isNull(this.supportedMessages) && !this.supportedMessages.contains(request.getPacketHead().getMessageType()))
            throw new UnknownServiceException("The requested operation " + request.getClass().getSimpleName() + " is not in the drive's list of supported messages"); //todo:special Exception for not supported services
        else this.dataOutputStream.write(request.getTcpMsgAsByteArray());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // todo: this read construct will probably run into an timeout if the message is 1024 byte long
        byte[] buffer = new byte[1024];
        while (true) {
            int readLength = dataInputStream.read(buffer);
            outputStream.write(buffer, 0, readLength + 1);
            if (readLength <1024) break;
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
            if (exceptionResponse.getPacketBody().getCommonErrorCode() == ExceptionBody.commonErrorCodes.UNKNOWN_MESSAGE_TYPE)
                throw new UnknownServiceException("Message type not supported: Drive does not support the requested operation " + request.getClass().getSimpleName()); //todo: create new SIP-Protocol Exception
            throw new ProtocolException("Drive threw Communication Exception."
                    + ((exceptionResponse.getPacketBody().getCommonErrorCode() == ExceptionBody.commonErrorCodes.SERVICESPECIFIC)
                    ? (" SIP-SpecificErrorCode: " + exceptionResponse.getPacketBody().getSpecificErrorCode())
                    : (" SIP-CommonErrorCode: " + exceptionResponse.getPacketBody().getCommonErrorCode())));
        }
        else if (header.getMessageType() == response.getMessageType()){
            response.setData(rawResponse);
            return response;
        } else throw new Exception("Invalid Message Type Response");
    }

    private void connectSip() throws Exception {
        Connect request = new Connect(this.getNewTransactionId(), this.sipVersion, this.busyTimeout, this.leaseTimeout);
        ConnectResponse response = (ConnectResponse) this.tcpSendAndReceive(request, new ConnectResponse());
        this.supportedMessages = IntStream.of(response.getPacketBody().getSupportedMessageTypes()).boxed().collect(Collectors.toList());
        this.connected = true;
    }

    private boolean respondsToPing(){
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

    public byte[] getParameterByIdn(int slaveIndex, int slaveExtension, String idn) throws Exception{
        ReadOnlyData request = new ReadOnlyData(this.getNewTransactionId(), (short)slaveIndex, slaveExtension, idn);
        ReadOnlyDataResponse response = (ReadOnlyDataResponse) this.tcpSendAndReceive(request, new ReadOnlyDataResponse());
        System.out.println(response.getPacketBody().getAttribute());
        return response.getPacketBody().getRawData();
    }

    public boolean isConnected() {
        return connected && this.socketConnection.isConnected();
    }

    public List<Integer> getSupportedMessages() {
        return supportedMessages;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public int getSipPort() {
        return sipPort;
    }

    public int getSipVersion() {
        return sipVersion;
    }
}
