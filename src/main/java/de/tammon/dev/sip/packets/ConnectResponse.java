/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets;

import de.tammon.dev.sip.packets.parts.ConnectResponseBody;
import de.tammon.dev.sip.packets.parts.Head;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ConnectResponse extends AbstractResponsePacket implements DynamicPacket {

    @Override
    public void setData(byte[] read) throws Exception {
        System.out.println(read.length);
        this.head = new Head(ByteBuffer.wrap(Arrays.copyOfRange(read, 0, 8)).array());
        this.body = new ConnectResponseBody(ByteBuffer.wrap(Arrays.copyOfRange(read, 8, read.length -1)).array());
    }

    @Override
    public ConnectResponseBody getPacketBody() {
        return (ConnectResponseBody) this.body;
    }

    @Override
    public int getMessageType() {
        return ConnectResponseBody.getMessageType();
    }
}
