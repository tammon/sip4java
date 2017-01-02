/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets;

import de.tammon.dev.sip.packets.parts.Body;

public class Ping extends AbstractRequestPacket {
    private final static int messageType = 65;

    public Ping(int transactionId) {
        super(transactionId, messageType);
    }

    public static int getMessageType() {
        return messageType;
    }

    @Override
    public Body getPacketBody() {
        return null;
    }

    @Override
    public byte[] getTcpMsgAsByteArray() {
        return this.head.getDataAsByteArray();
    }
}
