/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets.parts;

import de.tammon.dev.sip.packets.SipByteUtils;

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

        DataInputStream data = SipByteUtils.getDataInputStreamOfRawData(rawBodyData);

        this.sipVersion = SipByteUtils.getSipPrimitive(data.readInt());
        this.busyTimeout = SipByteUtils.getSipPrimitive(data.readInt());
        this.leaseTimeout = SipByteUtils.getSipPrimitive(data.readInt());
        this.noSupportedMessageTypes = SipByteUtils.getSipPrimitive(data.readInt());
        this.supportedMessageTypes = new int[noSupportedMessageTypes];
        //todo: optimize messageTypeMapping
        for (int i = 0; i < noSupportedMessageTypes; i++){
            try {
                this.supportedMessageTypes[i] = SipByteUtils.getSipPrimitive(data.readInt());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getMessageType(){
        return messageType;
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
