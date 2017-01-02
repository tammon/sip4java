/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets;

import de.tammon.dev.sip.packets.parts.Body;
import de.tammon.dev.sip.packets.parts.Head;

public class Pong extends AbstractResponsePacket {
    private final static int messageType = 66;

    @Override
    public Body getPacketBody() {
        return null;
    }

    @Override
    public int getMessageType() {
        return messageType;
    }

    @Override
    public void setData(byte[] rawData) throws Exception {
        this.head = new Head(rawData);
    }
}
