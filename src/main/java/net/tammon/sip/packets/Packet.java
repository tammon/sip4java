/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package net.tammon.sip.packets;

import net.tammon.sip.packets.parts.Body;
import net.tammon.sip.packets.parts.Head;

interface Packet {
    byte[] getTcpMsgAsByteArray();
    Integer getPacketSize();
    Body getPacketBody();
    Head getPacketHead();
    int getTransactionId();
}
