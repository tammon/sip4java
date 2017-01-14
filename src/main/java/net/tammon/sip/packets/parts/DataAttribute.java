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

package net.tammon.sip.packets.parts;

import net.tammon.sip.exceptions.TypeNotSupportedException;

import java.io.DataInput;
import java.io.IOException;
import java.util.Date;

import static net.tammon.sip.packets.parts.DataAttribute.DisplayFormat.values;

/**
 * The DataAttribute is send by the drive as a description to the sent data
 */
final class DataAttribute {
    private final int weight;
    private final DataLength dataLength;
    private final boolean isList;
    private final boolean isCommand;
    private final DisplayFormat displayFormat;
    private final byte decimalPointPosition;
    private final byte rights;
    private final Class dataType;

    /**
     * creates a new data attribute object
     * @param rawDataAttribute the 4 byte raw binary data of the body describing the data attribute
     * @throws IOException if an I/O error occurs while reading the byte array as DataStream
     */
    public DataAttribute(byte[] rawDataAttribute) throws IOException,TypeNotSupportedException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(rawDataAttribute);
        int rawWeight;
        this.weight = (rawWeight = data.readUnsignedShort()) == 0 ? 1 : rawWeight;
        byte byteBuffer = data.readByte();
        this.dataLength = DataLength.values()[(byteBuffer & 0x3)];
        this.isList = (byteBuffer & 0x4) == 0x4;
        this.isCommand = (byteBuffer & 0x8) == 0x8;
        this.displayFormat = values()[(byteBuffer & 0x70) >> 0x4];
        byteBuffer = data.readByte();
        this.decimalPointPosition = (byte)(byteBuffer & 0xF);
        this.rights = (byte)((byteBuffer & 0xF0) >> 0x4);
        this.dataType = getJavaType(displayFormat, dataLength, weight, decimalPointPosition, isList);
    }

    public int getWeight() {
        return weight;
    }

    public DataLength getDataLength() {
        return dataLength;
    }

    public boolean isList() {
        return isList;
    }

    public boolean isCommand() {
        return isCommand;
    }

    public DisplayFormat getDisplayFormat() {
        return displayFormat;
    }

    public byte getDecimalPointPosition() {
        return decimalPointPosition;
    }

    public byte getRights() {
        return rights;
    }

    public Class getJavaType() {
        return this.dataType;
    }


    /**
     * This method calculates the native data type from the different attributes of the data attribute
     * @param displayFormat display format which is used in the drive
     * @param dataLength length of a single data packet
     * @param weight weight of the data
     * @param decimalPointPosition the position of the decimal point of the number
     * @return a native data type used on the client side
     */
    private Class getJavaType(DisplayFormat displayFormat, DataLength dataLength, int weight, byte decimalPointPosition, boolean isList) throws TypeNotSupportedException {
        switch (displayFormat)
        {
            case Binary:
                switch (dataLength){
                    case oneByte:
                        return isList ? byte[].class : byte.class;
                    default:
                        return isList ? byte[][].class : byte[].class;
                }
            case UnsignedDecimal:
                switch (dataLength) {
                    case oneByte:
                        if(decimalPointPosition == 0) return isList ? short[].class : short.class;
                        else return isList ? float[].class : float.class;
                    case twoBytes:
                        if(decimalPointPosition == 0) return isList ? int[].class : int.class;
                        else return isList ? float[].class : float.class;
                    case fourBytes:
                        if(decimalPointPosition == 0) return isList ? long[].class : long.class;
                        else return isList ? double[].class : double.class;
                    case eightBytes:
                        throw new TypeNotSupportedException("eight byte unsigned decimal is currently not supported by this library! Sorry...");
                }
            case HexaDecimal:
                return isList ? byte[][].class : byte[].class;
            case SignedDecimal:
                switch (dataLength) {
                    case oneByte:
                        if(decimalPointPosition == 0) return isList ? byte[].class : byte.class;
                        else return isList ? float[].class : float.class;
                    case twoBytes:
                        if(decimalPointPosition == 0) return isList ? short[].class : short.class;
                        else return isList ? float[].class : float.class;
                    case fourBytes:
                        if(decimalPointPosition == 0) return isList ? int[].class : int.class;
                        else return isList ? double[].class : double.class;
                    case eightBytes:
                        if(decimalPointPosition == 0) return isList ? long[].class : long.class;
                        else return isList ? double[].class : double.class;
                }
            case String:
                if(dataLength.equals(DataLength.oneByte))
                    return String.class;
                break;
            case IDN:
                if(dataLength.equals(DataLength.fourBytes))
                    return isList ? String[].class : String.class;
                break;
            case Float:
                return isList ? double[].class : double.class;
            case SERCOSTime:
                return isList ? Date[].class : Date.class;
            default:
                throw new IllegalArgumentException("Unknown display format! : " + displayFormat);
        }
        throw new IllegalArgumentException("Invalid data length: " + dataLength + ". Current display format: " + displayFormat);
    }

    @Override
    public String toString() {
        return "weight: " + weight
                + "\nisList: " + isList
                + "\nisCommand: " + isCommand
                + "\nDataLength: " + dataLength
                + "\nDisplay Format: " + displayFormat
                + "\nDecimal Point Position: " + decimalPointPosition
                + "\nRights: " + rights;
    }

    /**
     * specifies the length of the single data packets
     */
    public enum DataLength {
        oneByte(1), twoBytes(2), fourBytes(4), eightBytes(8);

        private int value;

        DataLength(int value) {
            this.value = value;
        }

        public int getValue(){
            return this.value;
        }
    }

    /**
     * specifies the display format of the data in the drive
     */
    public enum DisplayFormat {
        Binary, UnsignedDecimal, SignedDecimal, HexaDecimal, String,
        IDN, Float, SERCOSTime, StructuredNode, Undefined;

        public boolean equalsAny(DisplayFormat... displayFormats){
            for(DisplayFormat displayFormat : displayFormats){
                if(this.equals(displayFormat)) return true;
            }
            return false;
        }
    }

    /**
     * Specifies the Type of an object
     */
    public enum Type {

        //     A null reference.
        Empty,

        //     A general type representing any reference or value type not explicitly represented
        //     by another Type.
        Object,

        //     A database null (column) value.
        DBNull,

        //     A simple type representing Boolean values of true or false.
        Boolean,

        //     An integral type representing unsigned 16-bit integers with values between 0
        //     and 65535. The set of possible values for the System.Type.Char type corresponds
        //     to the Unicode character set.
        Char,

        //     An integral type representing signed 8-bit integers with values between -128
        //     and 127.
        SByte,

        //     An integral type representing unsigned 8-bit integers with values between 0 and
        //     255.
        Byte,

        //     An integral type representing signed 16-bit integers with values between -32768
        //     and 32767.
        Int16,

        //     An integral type representing unsigned 16-bit integers with values between 0
        //     and 65535.
        UInt16,

        //     An integral type representing signed 32-bit integers with values between -2147483648
        //     and 2147483647.
        Int32,

        //     An integral type representing unsigned 32-bit integers with values between 0
        //     and 4294967295.
        UInt32,

        //     An integral type representing signed 64-bit integers with values between -9223372036854775808
        //     and 9223372036854775807.
        Int64,

        //     An integral type representing unsigned 64-bit integers with values between 0
        //     and 18446744073709551615.
        UInt64,

        //     A floating point type representing values ranging from approximately 1.5 x 10
        //     -45 to 3.4 x 10 38 with a precision of 7 digits.
        Single,

        //     A floating point type representing values ranging from approximately 5.0 x 10
        //     -324 to 1.7 x 10 308 with a precision of 15-16 digits.
        Double ,

        //     A simple type representing values ranging from 1.0 x 10 -28 to approximately
        //     7.9 x 10 28 with 28-29 significant digits.
        Decimal,

        //     A type representing a date and time value.
        DateTime,

        // this type is a placeholder with not functionality what so ever
        notApplicableType,

        //     A sealed class type representing Unicode character strings.
        String
    }
}
