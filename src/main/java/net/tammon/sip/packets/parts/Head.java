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

public class Head extends AbstractPart {
    private int transactionId;
    private int messageType;

    public Head(int transactionId, int messageType) {
        this.transactionId = transactionId;
        this.messageType = messageType;
    }

    public Head(byte[]... rawHeadData) throws IOException {
        DataInputStream data = SipByteUtils.getDataInputStreamOfRawData(rawHeadData);

        this.transactionId = SipByteUtils.getSipPrimitive(data.readInt());
        this.messageType = SipByteUtils.getSipPrimitive(data.readInt());
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public byte[] getDataAsByteArray (){
        return SipByteUtils.getByteArray(this.transactionId, this.messageType);
    }
}
