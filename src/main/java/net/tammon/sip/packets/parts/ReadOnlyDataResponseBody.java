/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package net.tammon.sip.packets.parts;

import net.tammon.sip.packets.SipByteUtils;

import java.io.DataInputStream;

public class ReadOnlyDataResponseBody extends AbstractBody implements ResponseBody {
    final static int messageType = 72;
    final int attribute;
    final int lengthOfData;
    final byte[] rawData;

    public ReadOnlyDataResponseBody(byte[]... rawBodyData) throws Exception {
        DataInputStream data = SipByteUtils.getDataInputStreamOfRawData(rawBodyData);
        this.attribute = SipByteUtils.getSipPrimitive(data.readInt());
        this.lengthOfData = SipByteUtils.getSipPrimitive(data.readInt());
        this.rawData = new byte[this.lengthOfData];
        final int read = data.read(this.rawData);
    }

    public static int getMessageType() {
        return messageType;
    }

    public int getAttribute() {
        return attribute;
    }

    public byte[] getRawData() {
        return rawData;
    }
}
