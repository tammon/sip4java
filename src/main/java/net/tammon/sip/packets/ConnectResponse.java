/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package net.tammon.sip.packets;

import net.tammon.sip.packets.parts.ConnectResponseBody;
import net.tammon.sip.packets.parts.Head;

import java.util.Arrays;

public class ConnectResponse extends AbstractResponsePacket implements DynamicPacket {

    @Override
    public ConnectResponseBody getPacketBody() {
        return (ConnectResponseBody) this.body;
    }

    @Override
    public int getMessageType() {
        return ConnectResponseBody.getMessageType();
    }

    @Override
    public void setData(byte[] rawData) throws Exception {
        this.head = new Head(rawData);
        this.body = new ConnectResponseBody(Arrays.copyOfRange(rawData, 8, rawData.length -1));
    }
}
