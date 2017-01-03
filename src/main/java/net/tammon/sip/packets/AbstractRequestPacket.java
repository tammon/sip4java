/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package net.tammon.sip.packets;

import net.tammon.sip.packets.parts.Head;
import net.tammon.sip.packets.parts.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

abstract class AbstractRequestPacket extends AbstractPacket implements Request{
    protected RequestBody body;

    public AbstractRequestPacket(int transactionId, RequestBody requestBody) {
        this.head = new Head(transactionId, requestBody.getMessageType());
        this.body = requestBody;
    }

    public AbstractRequestPacket(int transactionId, int messageType){
        this.head = new Head(transactionId, messageType);
    }

    public byte[] getTcpMsgAsByteArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(this.head.getDataAsByteArray());
            out.write(this.body.getDataAsByteArray());
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
