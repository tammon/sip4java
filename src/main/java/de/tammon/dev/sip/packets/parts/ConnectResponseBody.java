/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets.parts;

import de.tammon.dev.sip.packets.SipByteUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ConnectResponseBody extends AbstractBody implements ResponseBody {

    private static final int messageType = 64;
    private int sipVersion, busyTimeout, leaseTimeout, noSupportedMessageTypes;
    private int[] supportedMessageTypes;

    public ConnectResponseBody(int sipVersion, int busyTimeout, int leaseTimeout, int noSupportedMessageTypes, int[] supportedMessageTypes) {
        this.sipVersion = sipVersion;
        this.busyTimeout = busyTimeout;
        this.leaseTimeout = leaseTimeout;
        this.noSupportedMessageTypes = noSupportedMessageTypes;
        this.supportedMessageTypes = supportedMessageTypes;
    }

    public ConnectResponseBody(byte[]... rawBodyData) throws IllegalArgumentException, IOException {
        byte[] rawByteData = SipByteUtils.concatenate(rawBodyData);
        if (rawBodyData == null) throw new IllegalArgumentException("Data of TCP response Body must not be null!");

        DataInputStream rawData = new DataInputStream(new ByteArrayInputStream(rawByteData));

        this.sipVersion = SipByteUtils.getSipPrimitive(rawData.readInt());
        this.busyTimeout = SipByteUtils.getSipPrimitive(rawData.readInt());
        this.leaseTimeout = SipByteUtils.getSipPrimitive(rawData.readInt());
        this.noSupportedMessageTypes = SipByteUtils.getSipPrimitive(rawData.readInt());
        this.supportedMessageTypes = new int[noSupportedMessageTypes];
        //todo: optimize messageTypeMapping
        for (int i = 0; i < noSupportedMessageTypes; i++){
            try {
                this.supportedMessageTypes[i] = SipByteUtils.getSipPrimitive(rawData.readInt());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getMessageType(){
        return messageType;
    }

    //todo:implement
    @Override
    public byte[] getDataAsByteArray() {
        return new byte[0];
    }

    public int getSipVersion() {
        return sipVersion;
    }

    public int getBusyTimeout() {
        return busyTimeout;
    }

    public int getLeaseTimeout() {
        return leaseTimeout;
    }

    public int getNoSupportedMessageTypes() {
        return noSupportedMessageTypes;
    }

    public int[] getSupportedMessageTypes() {
        return supportedMessageTypes;
    }
}
