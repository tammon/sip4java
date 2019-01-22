/*
 * Sercos Internet Protocol (SIP) version 1
 * Copyright (c) 2017. tammon (Tammo Schwindt)
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.tammon.sip.packets;

import net.tammon.sip.exceptions.SipInternalException;
import net.tammon.sip.exceptions.TypeNotSupportedException;

import java.io.DataInput;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadOnlyDataResponse extends AbstractPacket implements Response {

    private static Logger _logger = LoggerFactory.getLogger("net.tammon.sip");
    
    private final static int messageType = ReadOnlyData.MSG_READ_ONLY_DATA+1;
    private Data data;

    /**
     * sets the data of this object's body
     * @param rawBodyData the raw binary data of the response describing the body
     * @throws IOException if an I/O error occurs on reading the binary data as DataStream
     */
    private void setBodyData(byte[] rawBodyData) throws IOException, TypeNotSupportedException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(rawBodyData);
        byte[] buffer = new byte[4];
        if (data instanceof FilterInputStream)
            ((FilterInputStream)data).read(buffer);
        else throw new InvalidClassException("DataInputStream does not extend FilterInputStream. Grabbing data as byte array ist not possible!");
        DataAttribute dataAttribute = new DataAttribute(buffer);

        int lengthOfData = data.readInt();
        byte[] rawData = new byte[lengthOfData];
        ((FilterInputStream)data).read(rawData);
        this.data = new Data(rawData, dataAttribute);
        _logger.info("ReadOnly Data: raw-len={}; data-len={}", rawBodyData.length, lengthOfData);
    }

    /**
     * Gets the messageType of the message
     * @return message Type
     */
    @Override
    public int getMessageType() {
        return messageType;
    }

    /**
     * Gets the data as raw binary
     * @return raw data as byte array
     */
    public Data getData() {
        return this.data;
    }

    @Override
    public void setData(byte[] rawData) {
        try {
            this.head = new Head(rawData);
            this.setBodyData(Arrays.copyOfRange(rawData, this.head.getMsgLength(), rawData.length));
        } catch (IOException | TypeNotSupportedException e) {
            throw new SipInternalException("Cannot set data of received S/IP packets", e);
        }
    }

    public static int getFixLength() {
        //     sizeof(attribute) + sizefo(data length)
        return (Integer.SIZE + Integer.SIZE)/8;
    }

    public static int getLengthOfData(byte[] rawReadOnlyData) throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(rawReadOnlyData);
        int lengthOfData = data.readInt(); //attribute
        lengthOfData = data.readInt();     //length 
        return lengthOfData;
    }
}
