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

import net.tammon.sip.exceptions.IllegalTypeConversionException;
import net.tammon.sip.exceptions.TypeNotSupportedException;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * This class holds data of sip data request and response bodies
 */
public final class Data {

    private final DataAttribute dataAttribute;
    private final byte[] rawData;

    /**
     * Creates a new instance of data by given rawData as byte array read from the packet
     *
     * @param rawData raw byte array from packet
     * @param dataAttribute data attribute which is provided in the packet
     */
    public Data(byte[] rawData, DataAttribute dataAttribute) {
        this.rawData = rawData;
        this.dataAttribute = dataAttribute;
    }

    /**
     * Returns a little endian byte array of the given numbers
     *
     * @param numbers numbers to be converted
     * @return numbers as little endian byte array
     */
    public static byte[] getByteArray(Number... numbers) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutput data = DataStreamFactory.getLittleEndianDataOutputStream(byteArrayOutputStream);
        try {
            for (Number number : numbers) {
                if (Byte.class.isInstance(number)) data.writeByte((byte) number);
                else if (Short.class.isInstance(number)) data.writeShort((short) number);
                else if (Integer.class.isInstance(number)) data.writeInt((int) number);
                else if (Long.class.isInstance(number)) data.writeLong((long) number);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a concatenated byte array of given byte arrays
     *
     * @param inputByteArrays to be concatenated byte arrays
     * @return concatenated byte array
     */
    public static byte[] concatenate(byte[]... inputByteArrays) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            for (byte[] inputByteArray : inputByteArrays) {
                outputStream.write(inputByteArray);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a concatenated byte array of given bytes
     *
     * @param inputByteArrays to be concatenated bytes
     * @return concatenated byte array
     */
    public static byte[] concatenate(byte... inputByteArrays) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            for (byte inputByteArray : inputByteArrays) {
                outputStream.write(inputByteArray);
            }
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String addZerosIfNeeded(String number, int neededLength) {
        while (number.length() <= neededLength)
            number = new StringBuilder(number).insert(number.charAt(0) == '-' ? 1 : 0, '0').toString();
        return number;
    }

    private static int[] convertToIntArray(short[] shorts) {
        int[] output = new int[shorts.length];
        for (int i = 0; i < shorts.length; i++) output[i] = shorts[i];
        return output;
    }

    private static long[] convertToLongArray(int[] ints) {
        long[] output = new long[ints.length];
        for (int i = 0; i < ints.length; i++) output[i] = ints[i];
        return output;
    }

    private static double[] convertToDoubleArray(float[] floats) {
        double[] output = new double[floats.length];
        for (int i = 0; i < floats.length; i++) output[i] = floats[i];
        return output;
    }

    private static short[] convertToShortArray(byte[] bytes) {
        short[] output = new short[bytes.length];
        for (int i = 0; i < bytes.length; i++) output[i] = bytes[i];
        return output;
    }

    /**
     * Converts the raw data of the Data object to type byte
     *
     * @return the converted data to short
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     */
    public byte toByte() throws IllegalTypeConversionException, IOException {
        if (this.dataAttribute.getJavaType().equals(byte.class))
            return this.getDataAsByte();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), byte.class);
    }

    private byte getDataAsByte() throws IllegalTypeConversionException {
        return rawData[0];
    }

    /**
     * Converts the raw data of the Data object to type short
     *
     * @return the converted data to short
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException                    if a problem occurs while reading the raw data stream
     */
    public short toShort() throws IllegalTypeConversionException, IOException {
        if (this.dataAttribute.getJavaType().equals(byte.class)) return this.toByte();
        if (this.dataAttribute.getJavaType().equals(short.class)) return this.getDataAsShort();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), short.class);
    }

    private short getDataAsShort() throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(this.rawData);
        return this.isSignedDecimal() ? data.readShort() : (short) Byte.toUnsignedInt(data.readByte());
    }

    /**
     * Converts the raw data of the Data object to type int
     *
     * @return the converted data to int
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException                    if a problem occurs while reading the raw data stream
     */
    public int toInt() throws IllegalTypeConversionException, IOException {
        if (this.dataAttribute.getJavaType().equals(byte.class)) return this.getDataAsByte();
        if (this.dataAttribute.getJavaType().equals(short.class)) return this.getDataAsShort();
        if (this.dataAttribute.getJavaType().equals(int.class)) return this.getDataAsInt();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), int.class);
    }

    private int getDataAsInt() throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(this.rawData);
        return this.isSignedDecimal() ? data.readInt() : Short.toUnsignedInt(data.readShort());
    }

    /**
     * Converts the raw data of the Data object to type long
     *
     * @return the converted data to long
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException                    if a problem occurs while reading the raw data stream
     */
    public long asLong() throws IllegalTypeConversionException, IOException {
        if (this.dataAttribute.getJavaType().equals(byte.class)) return this.toByte();
        if (this.dataAttribute.getJavaType().equals(short.class)) return this.toShort();
        if (this.dataAttribute.getJavaType().equals(int.class)) return this.toInt();
        if (this.dataAttribute.getJavaType().equals(long.class)) return this.getDataAsLong();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), long.class);
    }

    private long getDataAsLong() throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(this.rawData);
        return this.isSignedDecimal() ? data.readLong() : Integer.toUnsignedLong(data.readInt());
    }

    /**
     * Converts the raw data of the Data object to type long
     *
     * @return the converted data to long
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException                    if a problem occurs while reading the raw data stream
     */
    public float asFloat() throws IllegalTypeConversionException, TypeNotSupportedException, IOException {
        if (this.dataAttribute.getJavaType().equals(float.class)) {
            switch (this.dataAttribute.getDataLength()) {
                case oneByte:
                    return this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.UnsignedDecimal)
                            ? this.getDataAsFloatWithDecPoint(this.getDataAsShort())
                            : this.getDataAsFloatWithDecPoint(this.getDataAsByte());
                case twoBytes:
                    return this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.UnsignedDecimal)
                            ? this.getDataAsFloatWithDecPoint(this.getDataAsInt())
                            : this.getDataAsFloatWithDecPoint(this.getDataAsShort());
                default:
                    throw new TypeNotSupportedException("Java data type of data attribute does not match the criteria for float. This is probably due to wrong interpretation of the java type in the data attribute");
            }
        }
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), float.class);
    }

    private float getDataAsFloatWithDecPoint(int rawNumber) throws IOException {
        String floatString = Data.addZerosIfNeeded(Integer.toString(rawNumber), this.dataAttribute.getDecimalPointPosition());
        return Float.parseFloat(
                new StringBuilder(floatString)
                        .insert(floatString.length() - this.dataAttribute.getDecimalPointPosition(), '.')
                        .toString());
    }

    /**
     * Converts the raw data of the Data object to type long
     *
     * @return the converted data to long
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException                    if a problem occurs while reading the raw data stream
     */
    public double asDouble() throws IllegalTypeConversionException, IOException, TypeNotSupportedException {
        if (this.dataAttribute.getJavaType().equals(float.class)) return this.asFloat();
        if (this.dataAttribute.getJavaType().equals(double.class)) {
            if (this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.Float))
                throw new TypeNotSupportedException("Display format float is currently not supported");
            switch (this.dataAttribute.getDataLength()) {
                case fourBytes:
                    return this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.UnsignedDecimal)
                            ? this.getDataAsDoubleWithDecPoint(this.getDataAsLong())
                            : this.getDataAsDoubleWithDecPoint(this.getDataAsInt());
                case eightBytes:
                    return this.getDataAsDoubleWithDecPoint(this.getDataAsLong());
            }
        }
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), double.class);
    }

    private double getDataAsDoubleWithDecPoint(long rawNumber) throws IOException {
        String doubleString = Data.addZerosIfNeeded(Long.toString(rawNumber), this.dataAttribute.getDecimalPointPosition());
        return Double.parseDouble(
                new StringBuilder(doubleString)
                        .insert(doubleString.length() - this.dataAttribute.getDecimalPointPosition(), '.')
                        .toString());
    }

    /**
     * Converts the raw data of the Data object to type byte array
     *
     * @return the converted data to byte array
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     */
    public byte[] asByteArray() throws IllegalTypeConversionException {
        if (this.dataAttribute.getJavaType().equals(byte[].class)) return rawData;
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), byte.class);
    }

    public byte[][] toBinaryArray() throws IllegalTypeConversionException {
        if (this.dataAttribute.getJavaType().equals(byte[][].class)) {
            byte[][] output = new byte[this.rawData.length / this.dataAttribute.getDataLength().getValue()][this.dataAttribute.getDataLength().getValue()];
            for (int i = 0; i < output.length; i++) {
                int k = this.dataAttribute.getDataLength().getValue() - 1;
                for (int j = 0; j < this.dataAttribute.getDataLength().getValue(); j++) {
                    output[i][j] = this.rawData[i * this.dataAttribute.getDataLength().getValue() + k];
                    k--;
                }
            }
            return output;
        }
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), byte[][].class);
    }

    public List<byte[]> asListOfBinaryArray() throws IllegalTypeConversionException {
        return Arrays.asList(this.toBinaryArray());
    }

    /**
     * Converts the raw data of the Data object to type short array
     *
     * @return the converted data to short array
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException                    if a problem occurs while reading the raw data stream
     */
    public short[] asShortArray() throws IllegalTypeConversionException, IOException {
        if (this.dataAttribute.getJavaType().equals(short[].class)) return this.getDataAsShortArray();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), short[].class);
    }

    private short[] getDataAsShortArray() throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(this.rawData);
        short[] output = new short[this.rawData.length / this.dataAttribute.getDataLength().getValue()];

        if (this.isSignedDecimal())
            for (int i = 0; i < output.length; i++) output[i] = data.readShort();
        else
            for (int i = 0; i < output.length; i++) output[i] = (short) Byte.toUnsignedInt(data.readByte());

        return output;
    }

    /**
     * Converts the raw data of the Data object to type int array
     *
     * @return the converted data to int array
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException                    if a problem occurs while reading the raw data stream
     */
    public int[] asIntArray() throws IllegalTypeConversionException, IOException {
        if (this.dataAttribute.getJavaType().equals(short[].class)) {
            short[] shortArray = this.getDataAsShortArray();
            int[] output = new int[shortArray.length];
            for (int i = 0; i < shortArray.length; i++) output[i] = shortArray[i];
            return output;
        }
        if (this.dataAttribute.getJavaType().equals(int[].class)) return this.getDataAsIntArray();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), int[].class);
    }

    private int[] getDataAsIntArray() throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(this.rawData);
        int[] output = new int[this.rawData.length / this.dataAttribute.getDataLength().getValue()];

        if (this.isSignedDecimal())
            for (int i = 0; i < output.length; i++) output[i] = data.readInt();
        else
            for (int i = 0; i < output.length; i++) output[i] = Short.toUnsignedInt(data.readShort());

        return output;
    }

    /**
     * Converts the raw data of the Data object to type int
     *
     * @return the converted data to int
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException                    if a problem occurs while reading the raw data stream
     */
    public long[] asLongArray() throws IllegalTypeConversionException, IOException {
        if (this.dataAttribute.getJavaType().equals(short[].class)) {
            short[] shortArray = this.getDataAsShortArray();
            long[] output = new long[shortArray.length];
            for (int i = 0; i < shortArray.length; i++) output[i] = shortArray[i];
            return output;
        }
        if (this.dataAttribute.getJavaType().equals(int[].class))
            return Arrays.stream(this.getDataAsIntArray()).mapToLong(i -> i).toArray();
        if (this.dataAttribute.getJavaType().equals(long[].class)) return this.getDataAsLongArray();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), long[].class);
    }

    private long[] getDataAsLongArray() throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(this.rawData);
        long[] output = new long[this.rawData.length / this.dataAttribute.getDataLength().getValue()];

        if (this.isSignedDecimal())
            for (int i = 0; i < output.length; i++) output[i] = data.readLong();
        else
            for (int i = 0; i < output.length; i++) output[i] = Integer.toUnsignedLong(data.readInt());

        return output;
    }

    /**
     * Converts the raw data of the Data object to type float array
     *
     * @return the converted data to float array
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException                    if a problem occurs while reading the raw data stream
     */
    public float[] asFloatArray() throws IllegalTypeConversionException, IOException, TypeNotSupportedException {
        if (this.dataAttribute.getJavaType().equals(float[].class)) {
            switch (this.dataAttribute.getDataLength()) {
                case oneByte:
                    return this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.UnsignedDecimal)
                            ? this.getDataAsFloatArrayWithDecPoint(
                            Data.convertToIntArray(this.getDataAsShortArray()))
                            : this.getDataAsFloatArrayWithDecPoint(
                            Data.convertToIntArray(
                                    Data.convertToShortArray(this.getRawData())));
                case twoBytes:
                    return this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.UnsignedDecimal)
                            ? this.getDataAsFloatArrayWithDecPoint(this.getDataAsIntArray())
                            : this.getDataAsFloatArrayWithDecPoint(
                            Data.convertToIntArray(this.getDataAsShortArray()));
                default:
                    throw new TypeNotSupportedException("Java data type of data attribute does not match the criteria for float array. This is probably due to wrong interpretation of the java type in the data attribute");
            }
        }
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), float[].class);
    }

    private float[] getDataAsFloatArrayWithDecPoint(int[] intArray) throws IOException {
        int decPoint = this.dataAttribute.getDecimalPointPosition();
        float[] output = new float[intArray.length];
        for (int i = 0; i < output.length; i++) {
            String floatString = Data.addZerosIfNeeded(Integer.toString(intArray[i]), this.dataAttribute.getDecimalPointPosition());
            output[i] = Float.parseFloat(
                    new StringBuilder(floatString)
                            .insert(floatString.length() - decPoint, '.')
                            .toString());
        }
        return output;
    }

    /**
     * Converts the raw data of the Data object to type long
     *
     * @return the converted data to long
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException                    if a problem occurs while reading the raw data stream
     */
    public double[] asDoubleArray() throws IllegalTypeConversionException, IOException, TypeNotSupportedException {
        if (this.dataAttribute.getJavaType().equals(float[].class))
            return Data.convertToDoubleArray(this.asFloatArray());
        if (this.dataAttribute.getJavaType().equals(double[].class)) {
            if (this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.Float))
                throw new TypeNotSupportedException("Display format float is currently not supported");
            switch (this.dataAttribute.getDataLength()) {
                case fourBytes:
                    return this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.UnsignedDecimal)
                            ? this.getDataAsDoubleArrayWithDecPoint(this.getDataAsLongArray())
                            : this.getDataAsDoubleArrayWithDecPoint(
                            Data.convertToLongArray(this.getDataAsIntArray()));
                case eightBytes:
                    return this.getDataAsDoubleArrayWithDecPoint(this.getDataAsLongArray());
                default:
                    throw new TypeNotSupportedException("Java data type of data attribute does not match the criteria for float array. This is probably due to wrong interpretation of the java type in the data attribute");
            }
        }
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), double[].class);
    }

    private double[] getDataAsDoubleArrayWithDecPoint(long[] longArray) throws IOException {
        int decPoint = this.dataAttribute.getDecimalPointPosition();
        double[] output = new double[longArray.length];
        for (int i = 0; i < output.length; i++) {
            String floatString = Data.addZerosIfNeeded(Long.toString(longArray[i]), this.dataAttribute.getDecimalPointPosition());
            output[i] = Float.parseFloat(
                    new StringBuilder(floatString)
                            .insert(floatString.length() - decPoint, '.')
                            .toString());
        }
        return output;
    }

    /**
     * Converts the raw data of the Data object to type String array
     *
     * @return the converted data to String array
     */
    public String[] asStringArray() throws IOException {
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(this.rawData));
        String[] output = new String[this.rawData.length / 4];
        byte[] buffer = new byte[4];

        for (int i = 0; i < output.length; i++) {
            data.read(buffer);
            output[i] = (new Idn(buffer)).getIdn();
        }

        return output;
    }

    /**
     * Converts the raw data of the Data object to type String
     *
     * @return the converted data to String
     */
    public String asString() {
        try {
            if (this.dataAttribute.getJavaType().equals(byte.class)) return Byte.toString(this.toByte());
            if (this.dataAttribute.getJavaType().equals(short.class)) return Short.toString(this.toShort());
            if (this.dataAttribute.getJavaType().equals(int.class)) return Integer.toString(this.toInt());
            if (this.dataAttribute.getJavaType().equals(long.class)) return Long.toString(this.asLong());
            if (this.dataAttribute.getJavaType().equals(float.class)) return Float.toString(this.asFloat());
            if (this.dataAttribute.getJavaType().equals(double.class)) return Double.toString(this.asDouble());
            if (this.dataAttribute.getJavaType().equals(byte[].class)) {
                StringBuilder stringBuilder = new StringBuilder();
                if (this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.Binary)) {
                    stringBuilder.append("0b");
                    for (byte bytes : this.asByteArray())
                        stringBuilder.insert(2, Data.addZerosIfNeeded(Integer.toBinaryString(bytes), 8));
                } else if (this.dataAttribute.getDisplayFormat().equalsAny(DataAttribute.DisplayFormat.HexaDecimal)) {
                    stringBuilder.append("0x");
                    for (byte bytes : this.asByteArray())
                        stringBuilder.insert(2, Integer.toHexString(bytes));
                } else return Arrays.toString(this.asByteArray());
            }
            if (this.dataAttribute.getJavaType().equals(byte[][].class)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append('[');
                if (this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.Binary)) {
                    for (byte[] bytes : this.asListOfBinaryArray()) {
                        stringBuilder.append("0b");
                        for (byte rawByte : bytes)
                            stringBuilder.append(Data.addZerosIfNeeded(Integer.toBinaryString(rawByte), 8));
                        stringBuilder.append(",");
                    }
                } else if (this.dataAttribute.getDisplayFormat().equalsAny(DataAttribute.DisplayFormat.HexaDecimal)) {
                    for (byte[] bytes : this.asListOfBinaryArray()) {
                        stringBuilder.append("0x");
                        for (byte rawByte : bytes) stringBuilder.append(Integer.toHexString(rawByte));
                        stringBuilder.append(",");
                    }
                }
                stringBuilder.deleteCharAt(stringBuilder.toString().length() - 1);
                return stringBuilder.append(']').toString();
            }
            if (this.dataAttribute.getJavaType().equals(short[].class)) return Arrays.toString(this.asShortArray());
            if (this.dataAttribute.getJavaType().equals(int[].class)) return Arrays.toString(this.asIntArray());
            if (this.dataAttribute.getJavaType().equals(long[].class)) return Arrays.toString(this.asLongArray());
            if (this.dataAttribute.getJavaType().equals(float[].class)) return Arrays.toString(this.asFloatArray());
            if (this.dataAttribute.getJavaType().equals(double[].class)) return Arrays.toString(this.asDoubleArray());
            if (this.dataAttribute.getJavaType().equals(String[].class)) return Arrays.toString(this.asStringArray());
            if (this.dataAttribute.getJavaType().equals(String.class)) {
                DataInputStream data = new DataInputStream(new ByteArrayInputStream(this.rawData));
                switch (this.dataAttribute.getDisplayFormat()) {
                    case String:
                        return new String(this.rawData, 0, this.rawData.length, "ASCII");
                    case IDN:
                        byte[] buffer = new byte[4];
                        return (new Idn(buffer)).getIdn();
                    default:
                        throw new TypeNotSupportedException("Java data type of data attribute does not match the criteria for float array. This is probably due to wrong interpretation of the java type in the data attribute");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isSignedDecimal() {
        return this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.SignedDecimal);
    }

    /**
     * Returns the raw data of the packet as byte array
     *
     * @return raw data as byte array
     */
    public byte[] getRawData() {
        return this.rawData;
    }

    /**
     * @return whether or not the data is a list or array
     */
    public boolean isList() {
        return this.dataAttribute.isList();
    }

    /**
     * @return whether or not the data is a command
     */
    public boolean isCommand() {
        return this.dataAttribute.isCommand();
    }

    /**
     * @return the corresponding java data type to the sercos device display format and length of the data
     */
    public Class getJavaType() {
        return this.dataAttribute.getJavaType();
    }
}
