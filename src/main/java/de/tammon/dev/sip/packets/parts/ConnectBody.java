/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets.parts;

import de.tammon.dev.sip.packets.SipByteUtils;

public class ConnectBody extends AbstractBody implements RequestBody {

    private static final int messageType = 63;
    private int sipVersion;
    private int busyTimeOut;
    private int leaseTimeout;

    public ConnectBody(int sipVersion, int busyTimeOut, int leaseTimeout) {
        this.sipVersion = sipVersion;
        this.busyTimeOut = busyTimeOut;
        this.leaseTimeout = leaseTimeout;
    }

    @Override
    public byte[] getDataAsByteArray() {
        return SipByteUtils.getByteArray(this.sipVersion, this.busyTimeOut, this.leaseTimeout);
    }

    public int getSipVersion() {
        return sipVersion;
    }

    public int getBusyTimeOut() {
        return busyTimeOut;
    }

    public int getLeaseTimeout() {
        return leaseTimeout;
    }

    @Override
    public int getMessageType() {
        return messageType;
    }

}
