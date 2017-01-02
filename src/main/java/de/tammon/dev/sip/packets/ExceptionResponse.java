/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets;

import de.tammon.dev.sip.packets.parts.ExceptionBody;
import de.tammon.dev.sip.packets.parts.Head;

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
    public void setData(byte[] read) throws Exception {
        this.head = new Head(Arrays.copyOfRange(read, 0, 8));
        this.body = new ExceptionBody(Arrays.copyOfRange(read, 8, read.length -1));
    }
}
