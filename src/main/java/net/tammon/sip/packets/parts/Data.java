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

import net.tammon.sip.exceptions.IllegalTypeConversionException;
import net.tammon.sip.exceptions.TypeNotSupportedException;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public final class Data {

    private final DataAttribute dataAttribute;
    private final byte[] rawData;

    public Data(byte[] rawData, DataAttribute dataAttribute) {
        this.rawData = rawData;
        this.dataAttribute = dataAttribute;
    }

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
    
    private static String addZerosIfNeeded(String number, int decimalPoint){
        while(number.length() <= decimalPoint) number = new StringBuilder(number).insert(0, '0').toString();
        return number;
    }

    private static int[] convertToIntArray(short[] shorts){
        int[] output = new int[shorts.length];
        for (int i = 0; i < shorts.length; i++) output[i] = shorts[i];
        return output;
    }

    private static long[] convertToLongArray(int[] ints){
        long[] output = new long[ints.length];
        for (int i = 0; i < ints.length; i++) output[i] = ints[i];
        return output;
    }

    private static double[] convertToDoubleArray(float[] floats){
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
     * @return the converted data to short
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException if a problem occurs while reading the raw data stream
     */
    public short toShort() throws IllegalTypeConversionException, IOException {
        if(this.dataAttribute.getJavaType().equals(byte.class)) return this.toByte();
        if(this.dataAttribute.getJavaType().equals(short.class)) return this.getDataAsShort();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), short.class);
    }

    private short getDataAsShort() throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(this.rawData);
        return this.isSignedDecimal() ? data.readShort() : (short)Byte.toUnsignedInt(data.readByte());
    }

    /**
     * Converts the raw data of the Data object to type int
     * @return the converted data to int
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException if a problem occurs while reading the raw data stream
     */
    public int toInt() throws IllegalTypeConversionException, IOException {
        if(this.dataAttribute.getJavaType().equals(byte.class)) return this.getDataAsByte();
        if(this.dataAttribute.getJavaType().equals(short.class)) return this.getDataAsShort();
        if(this.dataAttribute.getJavaType().equals(int.class)) return this.getDataAsInt();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), int.class);
    }

    private int getDataAsInt() throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(this.rawData);
        return this.isSignedDecimal() ? data.readInt() : Short.toUnsignedInt(data.readShort());
    }

    /**
     * Converts the raw data of the Data object to type long
     * @return the converted data to long
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException if a problem occurs while reading the raw data stream
     */
    public long toLong() throws IllegalTypeConversionException, IOException {
        if(this.dataAttribute.getJavaType().equals(byte.class)) return this.toByte();
        if(this.dataAttribute.getJavaType().equals(short.class)) return this.toShort();
        if(this.dataAttribute.getJavaType().equals(int.class)) return this.toInt();
        if(this.dataAttribute.getJavaType().equals(long.class)) return this.getDataAsLong();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), long.class);
    }

    private long getDataAsLong() throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(this.rawData);
        return this.isSignedDecimal() ? data.readLong() : Integer.toUnsignedLong(data.readInt());
    }

    /**
     * Converts the raw data of the Data object to type long
     * @return the converted data to long
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException if a problem occurs while reading the raw data stream
     */
    public float toFloat() throws IllegalTypeConversionException, TypeNotSupportedException, IOException {
        if(this.dataAttribute.getJavaType().equals(float.class)) {
            switch (this.dataAttribute.getDataLength()){
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
     * @return the converted data to long
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException if a problem occurs while reading the raw data stream
     */
    public double toDouble () throws IllegalTypeConversionException, IOException, TypeNotSupportedException {
        if (this.dataAttribute.getJavaType().equals(float.class)) return this.toFloat();
        if (this.dataAttribute.getJavaType().equals(double.class)){
            if (this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.Float))
                throw new TypeNotSupportedException("Display format float is currently not supported");
            if(this.isSignedDecimal()){
                switch (this.dataAttribute.getDataLength()){
                    case fourBytes:
                        return this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.UnsignedDecimal)
                                ? this.getDataAsDoubleWithDecPoint(this.getDataAsLong())
                                : this.getDataAsDoubleWithDecPoint(this.getDataAsInt());
                    case eightBytes:
                        return this.getDataAsDoubleWithDecPoint(this.getDataAsLong());
                }
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
     * @return the converted data to byte array
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     */
    public byte[] toByteArray() throws IllegalTypeConversionException {
        if(this.dataAttribute.getJavaType().equals(byte[].class)) return rawData;
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), byte.class);
    }

    /**
     * Converts the raw data of the Data object to type short array
     * @return the converted data to short array
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException if a problem occurs while reading the raw data stream
     */
    public short[] toShortArray() throws IllegalTypeConversionException, IOException {
        if(this.dataAttribute.getJavaType().equals(short[].class)) return this.getDataAsShortArray();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), short[].class);
    }

    private short[] getDataAsShortArray() throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(this.rawData);
        short[] output = new short[this.rawData.length / this.dataAttribute.getDataLength().getValue()];

        if(this.isSignedDecimal())
            for (int i = 0; i < output.length; i++) output[i] = data.readShort();
        else
            for (int i = 0; i < output.length; i++) output[i] = (short)Byte.toUnsignedInt(data.readByte());

        return output;
    }

    /**
     * Converts the raw data of the Data object to type int array
     * @return the converted data to int array
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException if a problem occurs while reading the raw data stream
     */
    public int[] toIntArray() throws IllegalTypeConversionException, IOException {
        if(this.dataAttribute.getJavaType().equals(short[].class)){
            short[] shortArray = this.getDataAsShortArray();
            int[] output = new int[shortArray.length];
            for (int i = 0; i < shortArray.length; i++) output[i] = shortArray[i];
            return output;
        }
        if(this.dataAttribute.getJavaType().equals(int[].class)) return this.getDataAsIntArray();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), int[].class);
    }

    private int[] getDataAsIntArray() throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(this.rawData);
        int[] output = new int[this.rawData.length / this.dataAttribute.getDataLength().getValue()];

        if(this.isSignedDecimal())
            for (int i = 0; i < output.length; i++) output[i] = data.readInt();
        else
            for (int i = 0; i < output.length; i++) output[i] = Short.toUnsignedInt(data.readShort());

        return output;
    }

    /**
     * Converts the raw data of the Data object to type int
     * @return the converted data to int
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException if a problem occurs while reading the raw data stream
     */
    public long[] toLongArray() throws IllegalTypeConversionException, IOException {
        if(this.dataAttribute.getJavaType().equals(short[].class)){
            short[] shortArray = this.getDataAsShortArray();
            long[] output = new long[shortArray.length];
            for (int i = 0; i < shortArray.length; i++) output[i] = shortArray[i];
            return output;
        }
        if(this.dataAttribute.getJavaType().equals(int[].class))
            return Arrays.stream(this.getDataAsIntArray()).mapToLong(i -> i).toArray();
        if(this.dataAttribute.getJavaType().equals(long[].class)) return this.getDataAsLongArray();
        throw new IllegalTypeConversionException(this.dataAttribute.getJavaType(), long[].class);
    }

    private long[] getDataAsLongArray() throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(this.rawData);
        long[] output = new long[this.rawData.length / this.dataAttribute.getDataLength().getValue()];

        if(this.isSignedDecimal())
            for (int i = 0; i < output.length; i++) output[i] = data.readLong();
        else
            for (int i = 0; i < output.length; i++) output[i] = Integer.toUnsignedLong(data.readInt());

        return output;
    }

    /**
     * Converts the raw data of the Data object to type float array
     * @return the converted data to float array
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException if a problem occurs while reading the raw data stream
     */
    public float[] toFloatArray() throws IllegalTypeConversionException, IOException, TypeNotSupportedException {
        if(this.dataAttribute.getJavaType().equals(float[].class)) {
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
     * @return the converted data to long
     * @throws IllegalTypeConversionException if the data has a type that is not compatible to the return type of this function
     * @throws IOException if a problem occurs while reading the raw data stream
     */
    public double[] toDoubleArray() throws IllegalTypeConversionException, IOException, TypeNotSupportedException {
        if(this.dataAttribute.getJavaType().equals(float[].class)) return Data.convertToDoubleArray(this.toFloatArray());
        if(this.dataAttribute.getJavaType().equals(double[].class)){
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


    // --- STATIC METHODS ----

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
     * Converts the raw data of the Data object to type long
     * @return the converted data to long
     */
    public String[] toStringArray(){
        return null;
    }

    /**
     * Converts the raw data of the Data object to type long
     * @return the converted data to long
     */
    @Override
    public String toString() {
        return super.toString();
    }

    private boolean isSignedDecimal(){
        return this.dataAttribute.getDisplayFormat().equals(DataAttribute.DisplayFormat.SignedDecimal);
    }

    public byte[] getRawData(){
        return this.rawData;
    }

    public boolean isList(){
        return this.dataAttribute.isList();
    }

    public boolean isCommand(){
        return this.dataAttribute.isCommand();
    }

    public Class getJavaType(){
        return this.dataAttribute.getJavaType();
    }
}
