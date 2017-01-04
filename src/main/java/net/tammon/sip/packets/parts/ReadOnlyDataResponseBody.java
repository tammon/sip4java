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

import java.io.DataInput;
import java.io.FilterInputStream;
import java.io.InvalidClassException;

public final class ReadOnlyDataResponseBody implements ResponseBody {
    final static int messageType = 72;
    int attribute;
    int lengthOfData;
    byte[] rawData;

    public ReadOnlyDataResponseBody(byte[] rawBodyData) throws Exception {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(rawBodyData);
        this.attribute = data.readInt();
        this.lengthOfData = data.readInt();
        this.rawData = new byte[this.lengthOfData];
        if (data instanceof FilterInputStream)
            ((FilterInputStream)data).read(this.rawData);
        else throw new InvalidClassException("DataInputStream does not extend FilterInputStream. Grabbing data as byte array ist not possible!");
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
