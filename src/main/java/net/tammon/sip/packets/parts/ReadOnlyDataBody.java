/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package net.tammon.sip.packets.parts;

import net.tammon.sip.packets.SipByteUtils;



public class ReadOnlyDataBody extends AbstractBody implements RequestBody {
    private static final int messageType = 71;
    private final short slaveIndex;
    private final int slaveExtension;
    private final String idn;

    public ReadOnlyDataBody(short slaveIndex, int slaveExtension, String idn) throws IllegalArgumentException{
        this.slaveIndex = slaveIndex;
        this.slaveExtension = slaveExtension;
        this.idn = idn;
    }

    @Override
    public byte[] getDataAsByteArray() {
        return SipByteUtils.concatenate(
                SipByteUtils.getByteArray(this.slaveIndex),
                SipByteUtils.getByteArray(this.slaveExtension),
                SipByteUtils.getIdnAsByteArray(this.idn));
    }

    @Override
    public int getMessageType() {
        return messageType;
    }
}
