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


import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

final class SipByteUtils {

    static byte[] getByteArray(Number... numbers) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutput data = DataStreamFactory.getLittleEndianDataOutputStream(byteArrayOutputStream);
        try {
            for(Number number : numbers){
                if (Byte.class.isInstance(number)) data.writeByte((byte)number);
                else if (Short.class.isInstance(number)) data.writeShort((short)number);
                else if (Integer.class.isInstance(number)) data.writeInt((int)number);
                else if (Long.class.isInstance(number)) data.writeLong((long)number);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static byte[] concatenate(byte[]... inputByteArrays){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            for (byte[] inputByteArray : inputByteArrays) {
                outputStream.write(inputByteArray);
            }
            return outputStream.toByteArray();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    static byte[] concatenate(byte... inputByteArrays){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            for (byte inputByteArray : inputByteArrays) {
                outputStream.write(inputByteArray);
            }
            return outputStream.toByteArray();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    static byte[] getIdnAsByteArray(String idn) throws IllegalArgumentException {
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
            byte byte3 = (byte)((Byte.parseByte(idn.substring(2,3)) | ((idn.charAt(0) == 'P') ? (0x01 << 3) : 0x00)) << 4);
            byte[] parameterNo = SipByteUtils.getByteArray(Short.parseShort(idn.substring(4,8)));
            byte3 = (byte) (byte3 | parameterNo[1]);
            byte byte4 = parameterNo[0];
            return SipByteUtils.concatenate((byte) 0, (byte) 0, byte4, byte3);
        } else throw new IllegalArgumentException("The specified idn is not a valid drive Parameter");
    }
}
