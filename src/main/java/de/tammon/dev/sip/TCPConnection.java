/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip;

import de.tammon.dev.sip.packets.*;
import de.tammon.dev.sip.packets.parts.Head;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

public class TCPConnection implements SipConnection {
    private InetAddress ipAddress;
    private int maxDelay, leaseTimeout, busyTimeout, sipPort, sipVersion;
    private int transactionId = 0;
    private boolean connected = false;
    private int[] supportedMessages;
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
        this.socketConnection = new Socket();
        this.socketConnection.connect(new InetSocketAddress(this.ipAddress, this.sipPort), leaseTimeout);

        this.dataOutputStream = new DataOutputStream(this.socketConnection.getOutputStream());
        this.dataInputStream = new DataInputStream(this.socketConnection.getInputStream());

        this.connectSip();
    }

    public TCPConnection() throws Exception {
        this(null);
    }

    private synchronized int getNewTransactionId (){
        return this.transactionId++;
    }

    private synchronized Response tcpSendAndReceive(Request request, Response response) throws Exception {
        this.dataOutputStream.write(request.getTcpMsgAsByteArray());

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
            ExceptionResponse exceptionResponse = new ExceptionResponse(rawResponse);
            throw new Exception("Drive threw Communication Exception. SIP-CommonErrorCode: "
                    + exceptionResponse.getPacketBody().getCommonErrorCode()
                    + "SIP-SpecificErrorCode: "
                    + exceptionResponse.getPacketBody().getSpecificErrorCode());
        }
        else if (header.getMessageType() == response.getMessageType()){
            response.setData(rawResponse);
            return response;
        } else throw new Exception("Invalid Message Type Response");
    }

    private void connectSip() throws Exception {
        Connect request = new Connect(this.getNewTransactionId(), this.sipVersion, this.busyTimeout, this.leaseTimeout);
        ConnectResponse response = (ConnectResponse) this.tcpSendAndReceive(request, new ConnectResponse());
        this.supportedMessages = response.getPacketBody().getSupportedMessageTypes();
        this.connected = true;
    }

    public boolean isConnected() {
        return connected && this.socketConnection.isConnected();
    }

    public int[] getSupportedMessages() {
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
