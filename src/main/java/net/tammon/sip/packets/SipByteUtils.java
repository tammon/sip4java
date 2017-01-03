/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package net.tammon.sip.packets;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class SipByteUtils {

    public static byte[] getByteArray(short... inputShorts){
        byte[] outputArray = new byte[0];
        List<Byte> outputList = new ArrayList<>();
        for (short inputShort : inputShorts) {
            byte[] inputByteArray = SipByteUtils.getSipByteBuffer(2).putShort(inputShort).array();
            outputArray = SipByteUtils.concatenate(outputArray, inputByteArray);
        }
        return outputArray;
    }

    public static byte[] getByteArray(byte... inputBytes){
        byte[] outputArray = new byte[0];
        List<Byte> outputList = new ArrayList<>();
        for (byte inputByte : inputBytes) {
            byte[] inputByteArray = SipByteUtils.getSipByteBuffer(1).put(inputByte).array();
            outputArray = SipByteUtils.concatenate(outputArray, inputByteArray);
        }
        return outputArray;
    }

    public static byte[] getByteArray(int... inputIntegers){
        byte[] outputArray = new byte[0];
        List<Byte> outputList = new ArrayList<>();
        for (int inputInteger : inputIntegers) {
            byte[] inputByteArray = SipByteUtils.getSipByteBuffer(4).putInt(inputInteger).array();
            outputArray = SipByteUtils.concatenate(outputArray, inputByteArray);
        }
        return outputArray;
    }

    public static byte[] getByteArray(long... inputLongs){
        byte[] outputArray = new byte[0];
        List<Byte> outputList = new ArrayList<>();
        for (long inputLong : inputLongs) {
            byte[] inputByteArray = SipByteUtils.getSipByteBuffer(8).putLong(inputLong).array();
            outputArray = SipByteUtils.concatenate(outputArray, inputByteArray);
        }
        return outputArray;
    }

    public static byte[] concatenate(byte[]... inputByteArrays){
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

    public static byte[] concatenate(byte... inputByteArrays){
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

    private static ByteBuffer getSipByteBuffer (int allocation){
        ByteBuffer byteBuffer = ByteBuffer.allocate(allocation);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return byteBuffer;
    }

    public static short getSipPrimitive(short tcpShort){
        return Short.reverseBytes(tcpShort);
    }

    public static int getSipPrimitive(int tcpInt){
        return Integer.reverseBytes(tcpInt);
    }

    public static long getSipPrimitive(long tcpLong){
        return Long.reverseBytes(tcpLong);
    }

    public static DataInputStream getDataInputStreamOfRawData (byte[]... rawInputBytes){
        byte[] rawInputByteArray = SipByteUtils.concatenate(rawInputBytes);
        return new DataInputStream(new ByteArrayInputStream(rawInputByteArray));
    }
}
