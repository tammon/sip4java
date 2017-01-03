/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package net.tammon.sip.packets;

import net.tammon.sip.packets.parts.Head;
import net.tammon.sip.packets.parts.ReadOnlyDataResponseBody;

import java.util.Arrays;

public class ReadOnlyDataResponse extends AbstractResponsePacket {

    @Override
    public void setData(byte[] rawData) throws Exception {
        this.head = new Head(rawData);
        this.body = new ReadOnlyDataResponseBody(Arrays.copyOfRange(rawData, 8, rawData.length - 1));
    }

    @Override
    public int getMessageType() {
        return ReadOnlyDataResponseBody.getMessageType();
    }

    @Override
    public ReadOnlyDataResponseBody getPacketBody() {
        return (ReadOnlyDataResponseBody) this.body;
    }
}
