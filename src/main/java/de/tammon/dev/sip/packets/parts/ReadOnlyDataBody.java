/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets.parts;

import de.tammon.dev.sip.packets.SipByteUtils;

import java.util.Arrays;

public class ReadOnlyDataBody extends AbstractBody implements RequestBody {
    private static final int messageType = 71;
    private final short slaveIndex;
    private final int slaveExtension;
    private final byte[] idn;
    private final String str_idn;

    public ReadOnlyDataBody(short slaveIndex, int slaveExtension, String idn) throws IllegalArgumentException{
        this.slaveIndex = slaveIndex;
        this.slaveExtension = slaveExtension;
        this.str_idn = idn;
        this.idn = this.getEidn(idn);
    }

    @Override
    public byte[] getDataAsByteArray() {
        return SipByteUtils.concatenate(
                SipByteUtils.getByteArray(this.slaveIndex),
                SipByteUtils.getByteArray(this.slaveExtension),
                this.idn);
    }

    @Override
    public int getMessageType() {
        return messageType;
    }

    private byte[] getEidn (String idn) throws IllegalArgumentException {
        //todo: maybe some optimization without duplicate code
        if (idn.matches("^[SP]-\\d-\\d\\d\\d\\d[.]\\d[.]\\d$")){
            //decode byte[4]
            byte byte1 = Byte.parseByte(idn.substring(9,10));
            byte byte2 = Byte.parseByte(idn.substring(11));
            byte byte3 = (byte)(Byte.parseByte(idn.substring(2,3)) | ((idn.charAt(0) == 'P') ? (0x01 << 3) : 0x00));
            byte[] intermediateByte4 = SipByteUtils.getByteArray(Short.parseShort(idn.substring(4,8)));
            byte[] byte4 = Arrays.copyOfRange(intermediateByte4, 0, 3);
            return SipByteUtils.concatenate(byte4,
                    SipByteUtils.concatenate(byte3, byte2, byte1));
        } else if (idn.matches("^[SP]-\\d-\\d\\d\\d\\d$")){
            //decode byte[2]
            byte byte3 = (byte)(Byte.parseByte(idn.substring(2,3)) | ((idn.charAt(0) == 'P') ? (0x01 << 3) : 0x00));
            byte[] intermediateByte4 = SipByteUtils.getByteArray(Short.parseShort(idn.substring(4,8)));
            byte[] byte4 = Arrays.copyOfRange(intermediateByte4, 0, 3);
            return SipByteUtils.concatenate(byte4,
                    SipByteUtils.concatenate(byte3));
        } else throw new IllegalArgumentException("The specified idn is not a valid drive Parameter");
    }
}
