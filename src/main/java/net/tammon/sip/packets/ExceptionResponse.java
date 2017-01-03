/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package net.tammon.sip.packets;

import net.tammon.sip.packets.parts.ExceptionBody;
import net.tammon.sip.packets.parts.Head;

import java.util.Arrays;

public class ExceptionResponse extends AbstractResponsePacket {

    public ExceptionResponse(byte[] rawData) throws Exception {
        this.setData(rawData);
    }

    @Override
    public ExceptionBody getPacketBody() {
        return (ExceptionBody) this.body;
    }

    @Override
    public int getMessageType() {
        return ExceptionBody.getMessageType();
    }

    @Override
    public void setData(byte[] rawData) throws Exception {
        this.head = new Head(rawData);
        this.body = new ExceptionBody(Arrays.copyOfRange(rawData, 8, rawData.length -1));
    }
}
