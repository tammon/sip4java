/*
 * Sercos Internet Protocol (SIP) version 1
 * Copyright (C) 2017. tammon (Tammo Schwindt)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.tammon.sip.packets.parts;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Holds a SIP Identifier for parameter access.
 */
public final class Idn {
    private final byte[] eIdn;
    private final String idn;

    /**
     * creates an SIP Idn Object from an Idn-String
     * @param idn 16-bit or 32-bit idn as String
     */
    public Idn(String idn) {
        this.idn = idn;
        this.eIdn = Idn.getIdnAsByteArray(idn);
    }

    /**
     * creates an SIP Idn Object from an eIdn as raw byte array
     * @param eIdn 32-bit idn as byte-array
     */
    public Idn(byte[] eIdn) throws IOException {
        this.eIdn = eIdn;
        this.idn = getIdnAsString(eIdn);
    }

    /**
     * converts a idn String to a 32-bit byte array
     * @param idn 16-bit or 32-bit idn as String
     * @return 32-bit byte array eIdn
     * @throws IllegalArgumentException if the specified String is not a valid Idn
     */
    static byte[] getIdnAsByteArray(String idn) throws IllegalArgumentException {
        if (idn.matches("^[SP]-\\d-\\d\\d\\d\\d[.]\\d[.]\\d$")){
            byte byte1 = Byte.parseByte(idn.substring(9,10));
            byte byte2 = Byte.parseByte(idn.substring(11));
            return Data.concatenate(getIdnAs16BitByteArray(idn),
                    Data.concatenate(byte2, byte1));
        } else if (idn.matches("^[SP]-\\d-\\d\\d\\d\\d$")){
            return Data.concatenate(getIdnAs16BitByteArray(idn),
                    Data.concatenate((byte) 0, (byte) 0));
        } else throw new IllegalArgumentException("The specified idn is not a valid drive Parameter");
    }

    private static byte[] getIdnAs16BitByteArray(String idn) {
        byte byte3 = (byte)((Byte.parseByte(idn.substring(2,3)) | ((idn.charAt(0) == 'P') ? (0x01 << 3) : 0x00)) << 4);
        byte[] parameterNo = Data.getByteArray(Short.parseShort(idn.substring(4,8)));
        byte3 = (byte) (byte3 | parameterNo[1]);
        byte byte4 = parameterNo[0];
        return Data.concatenate(byte4, byte3);
    }

    public static String getIdnAsString(byte[] eIdn) throws IOException {
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(eIdn));
        byte[] buffer = new byte[4];
        data.read(buffer);
        char parType = (buffer[1] & 0x80) == 0x80 ? 'P' : 'S';
        int num1 = (buffer[1] & 0x70) >> 0x4;
        short num2 = ByteBuffer.wrap(new byte[]{(byte)(buffer[1] & 0xF), buffer[0]}).getShort();
        int num3 = buffer[3];
        int num4 = buffer[2];
        return (new StringBuilder())
                .append(parType)
                .append('-')
                .append(num1)
                .append('-')
                .append(addZerosTillLength(Integer.toString(num2), 4))
                .append('.')
                .append(num3)
                .append('.')
                .append(num4)
                .toString();
    }

    private static String addZerosTillLength(String string, int neededLength){
        for (int length = string.length(); length < neededLength; length++) string = new StringBuilder(string).insert(0, '0').toString();
        return string;
    }

    public byte[] getIdnAsByteArray() {
        return eIdn;
    }

    public String getIdn() {
        return idn;
    }
}
