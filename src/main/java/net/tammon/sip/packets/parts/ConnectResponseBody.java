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

import net.tammon.sip.packets.SipByteUtils;

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
