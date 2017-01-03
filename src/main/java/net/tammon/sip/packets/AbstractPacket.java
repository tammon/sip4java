/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package net.tammon.sip.packets;

import net.tammon.sip.packets.parts.Body;
import net.tammon.sip.packets.parts.Head;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

abstract class AbstractPacket implements Packet {
    protected Head head;
    protected Body body;

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

    public Integer getPacketSize() {
        return this.getTcpMsgAsByteArray().length;
    }

    public int getTransactionId(){
        return this.head.getTransactionId();
    }

    public Head getPacketHead() {
        return this.head;
    }
}
