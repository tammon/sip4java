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

import java.io.DataInput;
import java.io.IOException;

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
    private final Type type;

    /**
     * creates a new data attribute object
     * @param rawDataAttribute the 4 byte raw binary data of the body describing the data attribute
     * @throws IOException if an I/O error occurs while reading the byte array as DataStream
     */
    public DataAttribute(byte[] rawDataAttribute) throws IOException {
        DataInput data = DataStreamFactory.getLittleEndianDataInputStream(rawDataAttribute);
        int rawWeight;
        this.weight = (rawWeight = data.readUnsignedShort()) == 0 ? 1 : rawWeight;
        byte byteBuffer = data.readByte();
        this.dataLength = DataLength.values()[(byteBuffer & 0x3)];
        this.isList = (byteBuffer & 0x4) == 0x4;
        this.isCommand = (byteBuffer & 0x8) == 0x8;
        this.displayFormat = DisplayFormat.values()[(byteBuffer & 0x70) >> 0x4];
        byteBuffer = data.readByte();
        this.decimalPointPosition = (byte)(byteBuffer & 0xF);
        this.rights = (byte)((byteBuffer & 0xF0) >> 0x4);
        this.type = getType(displayFormat, dataLength, weight, decimalPointPosition);
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

    public Type getType() {
        return type;
    }

    /**
     * This method calculates the native data type from the different attributes of the data attribute
     * @param displayFormat display format which is used in the drive
     * @param dataLength length of a single data packet
     * @param weight weight of the data
     * @param decimalPointPosition the position of the decimal point of the number
     * @return a native data type used on the client side
     */
    private Type getType(DisplayFormat displayFormat, DataLength dataLength, int weight, byte decimalPointPosition) {
        switch (displayFormat)
        {
            case Binary:
            case UnsignedDecimal:
            case HexaDecimal:
                switch (dataLength)
                {
                    case oneByte:
                        return (int) decimalPointPosition != 0 ? Type.Single : Type.Byte;
                    case twoBytes:
                        if ((int) decimalPointPosition != 0)
                            return Type.Single;
                        return weight != 1 ? Type.UInt32 : Type.UInt16;
                    case fourBytes:
                        if ((int) decimalPointPosition != 0)
                            return Type.Double;
                        return weight != 1 ? Type.UInt64 : Type.UInt32;
                    case eightBytes:
                        return (int) decimalPointPosition == 0 && weight == 1 ? Type.UInt64 : Type.Decimal;
                }
            case SignedDecimal:
                switch (dataLength)
                {
                    case oneByte:
                        return (int) decimalPointPosition != 0 ? Type.Single : Type.SByte;
                    case twoBytes:
                        if ((int) decimalPointPosition != 0)
                            return Type.Single;
                        return weight != 1 ? Type.Int32 : Type.Int16;
                    case fourBytes:
                        if ((int) decimalPointPosition != 0)
                            return Type.Double;
                        return weight != 1 ? Type.Int64 : Type.Int32;
                    case eightBytes:
                        return (int) decimalPointPosition == 0 && weight == 1 ? Type.Int64 : Type.Decimal;
                }
            case String:
                if (dataLength == DataLength.oneByte)
                    return Type.String;
                break;
            case IDN:
                if (dataLength == DataLength.fourBytes)
                    return Type.UInt32;
                break;
            case Float:
                switch (dataLength)
                {
                    case fourBytes:
                        return Type.Single;
                    case eightBytes:
                        return Type.Double;
                }
            case SERCOSTime:
                if (dataLength == DataLength.eightBytes)
                    return Type.DateTime;
                break;
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
                + "\nRights: " + rights
                + "\ntype: " + type;
    }

    /**
     * specifies the length of the single data packets
     */
    public enum DataLength {
        oneByte, twoBytes, fourBytes, eightBytes
    }

    /**
     * specifies the display format of the data in the drive
     */
    public enum DisplayFormat {
        Binary, UnsignedDecimal, SignedDecimal, HexaDecimal, String,
        IDN, Float, SERCOSTime, StructuredNode, Undefined
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
