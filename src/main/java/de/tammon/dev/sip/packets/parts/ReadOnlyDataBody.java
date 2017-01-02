/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets.parts;

public class ReadOnlyDataBody extends AbstractBody {
    private static final int messageType = 71;
    private short slaveIndex;
    private int slaveExtension;
    private int idn;

    public ReadOnlyDataBody(short slaveIndex, int slaveExtension, int idn) {
        this.slaveIndex = slaveIndex;
        this.slaveExtension = slaveExtension;
        this.idn = idn;
    }

    @Override
    public byte[] getDataAsByteArray() {
        return new byte[0];
    }
}
