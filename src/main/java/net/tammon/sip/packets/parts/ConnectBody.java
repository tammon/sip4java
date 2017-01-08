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

package net.tammon.sip.packets.parts;

public final class ConnectBody extends AbstractBody implements RequestBody {

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
        return Data.getByteArray(this.sipVersion, this.busyTimeOut, this.leaseTimeout);
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
