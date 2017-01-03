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
