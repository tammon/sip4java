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
import java.io.IOException;
import java.io.InvalidClassException;

/**
 * This class is the response body to a ReadOnlyData message
 */
public final class ReadOnlyDataResponseBody implements ResponseBody {
    private final static int messageType = 72;
    private  DataAttribute attribute;
    private byte[] rawData;

    /**
     * creates a new response body object for ReadOnlyData messages
     * @param rawBodyData the raw binary data of the response describing the body
     * @throws IOException if an I/O error occurs on reading the binary data as DataStream
     */
    public ReadOnlyDataResponseBody(byte[] rawBodyData) throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(rawBodyData);
        byte[] buffer = new byte[4];
        if (data instanceof FilterInputStream)
            ((FilterInputStream)data).read(buffer);
        else throw new InvalidClassException("DataInputStream does not extend FilterInputStream. Grabbing data as byte array ist not possible!");
        this.attribute = new DataAttribute(buffer);
        int lengthOfData = data.readInt();
        this.rawData = new byte[lengthOfData];
        ((FilterInputStream)data).read(this.rawData);
    }

    /**
     * Gets the messageType of the message
     * @return message Type
     */
    public static int getMessageType() {
        return messageType;
    }

    /**
     * Gets the data attribute as {@link DataAttribute} object
     * @return {@link DataAttribute} object
     */
    public DataAttribute getAttribute() {
        return attribute;
    }

    /**
     * Gets the data as raw binary
     * @return raw data as byte array
     */
    public byte[] getRawData() {
        return rawData;
    }
}
