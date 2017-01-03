/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
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
