/*
 * Sercos Internet Protocol (SIP) version 1
 * Copyright (c) 2017. tammon (Tammo Schwindt)
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.tammon.sip.packets;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Holds a SIP Identifier for parameter access.
 */
final class Idn {
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
        if (idn.matches("^[SP]-\\d-\\d\\d\\d\\d\\.([1-9]?\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.([1-9]?\\d|1\\d\\d|2[0-4]\\d|25[0-5])$")) {
            String idnPointPart = idn.substring(9);
            String[] idnPointSeparatedParts = idnPointPart.split("\\.");

            byte byte1 = Data.parseUnsignedByte(idnPointSeparatedParts[0]);
            byte byte2 = Data.parseUnsignedByte(idnPointSeparatedParts[1]);

            return Data.concatenate(getIdnAs16BitByteArray(idn),
                    Data.concatenate(byte2, byte1));
        } else if (idn.matches("^[SP]-\\d-\\d\\d\\d\\d$")){
            return Data.concatenate(getIdnAs16BitByteArray(idn),
                    Data.concatenate((byte) 0, (byte) 0));
        } else throw new IllegalArgumentException("The specified idn is not a valid drive Parameter");
    }

    private static byte[] getIdnAs16BitByteArray(String idn) {
        String idnDashPart = idn.substring(0,8);
        String[] idnDashSeparatedParts = idnDashPart.split("-");

        byte byte3 = (byte)((Data.parseUnsignedByte(idnDashSeparatedParts[1]) |
                ((idnDashSeparatedParts[0].equals("P")) ? (0x01 << 3) : 0x00)) << 4);
        byte[] parameterNo = Data.getByteArray(Short.parseShort(idnDashSeparatedParts[2]));

        assert parameterNo != null;
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
