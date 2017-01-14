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

import java.io.*;

/**
 * An implementation of {@link DataInput} that uses little-endian byte ordering for reading
 * {@code short}, {@code int}, {@code float}, {@code double}, and {@code long} values.
 *
 * <p><b>Note:</b> This class intentionally violates the specification of its supertype
 * {@code DataInput}, which explicitly requires big-endian byte order.
 *
 * @author Chris Nokleberg
 * @author Keith Bottner
 * @since 8.0
 */
public final class LittleEndianDataInputStream extends FilterInputStream implements DataInput {

    /**
     * Creates a {@code LittleEndianDataInputStream} that wraps the given stream.
     *
     * @param in the stream to delegate to
     */
    public LittleEndianDataInputStream(InputStream in) {
        super(in);
    }

    private static int intFromBytes(byte b1, byte b2, byte b3, byte b4) {
        return b1 << 24 | (b2 & 255) << 16 | (b3 & 255) << 8 | b4 & 255;
    }

    public static long longFromBytes(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
        return ((long)b1 & 255L) << 56 | ((long)b2 & 255L) << 48 | ((long)b3 & 255L) << 40 | ((long)b4 & 255L) << 32 | ((long)b5 & 255L) << 24 | ((long)b6 & 255L) << 16 | ((long)b7 & 255L) << 8 | (long)b8 & 255L;
    }

    /**
     * This method will throw an {@link UnsupportedOperationException}.
     */
    @Override
    public String readLine() {
        throw new UnsupportedOperationException("readLine is not supported");
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        throw new UnsupportedOperationException("readFully is not supported");
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        throw new UnsupportedOperationException("readFully is not supported");
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return (int) in.skip(n);
    }

    @Override
    public int readUnsignedByte() throws IOException {
        int b1 = in.read();
        if (0 > b1) {
            throw new EOFException();
        }

        return b1;
    }

    /**
     * Reads an unsigned {@code short} as specified by {@link DataInputStream#readUnsignedShort()},
     * except using little-endian byte order.
     *
     * @return the next two bytes of the input stream, interpreted as an unsigned 16-bit integer in
     *     little-endian byte order
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int readUnsignedShort() throws IOException {
        byte b1 = readAndCheckByte();
        byte b2 = readAndCheckByte();

        return intFromBytes((byte) 0, (byte) 0, b2, b1);
    }

    /**
     * Reads an integer as specified by {@link DataInputStream#readInt()}, except using little-endian
     * byte order.
     *
     * @return the next four bytes of the input stream, interpreted as an {@code int} in little-endian
     *     byte order
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int readInt() throws IOException {
        byte b1 = readAndCheckByte();
        byte b2 = readAndCheckByte();
        byte b3 = readAndCheckByte();
        byte b4 = readAndCheckByte();

        return intFromBytes(b4, b3, b2, b1);
    }

    /**
     * Reads a {@code long} as specified by {@link DataInputStream#readLong()}, except using
     * little-endian byte order.
     *
     * @return the next eight bytes of the input stream, interpreted as a {@code long} in
     *     little-endian byte order
     * @throws IOException if an I/O error occurs
     */
    @Override
    public long readLong() throws IOException {
        byte b1 = readAndCheckByte();
        byte b2 = readAndCheckByte();
        byte b3 = readAndCheckByte();
        byte b4 = readAndCheckByte();
        byte b5 = readAndCheckByte();
        byte b6 = readAndCheckByte();
        byte b7 = readAndCheckByte();
        byte b8 = readAndCheckByte();

        return longFromBytes(b8, b7, b6, b5, b4, b3, b2, b1);
    }

    /**
     * Reads a {@code float} as specified by {@link DataInputStream#readFloat()}, except using
     * little-endian byte order.
     *
     * @return the next four bytes of the input stream, interpreted as a {@code float} in
     *     little-endian byte order
     * @throws IOException if an I/O error occurs
     */
    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * Reads a {@code double} as specified by {@link DataInputStream#readDouble()}, except using
     * little-endian byte order.
     *
     * @return the next eight bytes of the input stream, interpreted as a {@code double} in
     *     little-endian byte order
     * @throws IOException if an I/O error occurs
     */
    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public String readUTF() throws IOException {
        return new DataInputStream(in).readUTF();
    }

    /**
     * Reads a {@code short} as specified by {@link DataInputStream#readShort()}, except using
     * little-endian byte order.
     *
     * @return the next two bytes of the input stream, interpreted as a {@code short} in little-endian
     *     byte order.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public short readShort() throws IOException {
        return (short) readUnsignedShort();
    }

    /**
     * Reads a char as specified by {@link DataInputStream#readChar()}, except using little-endian
     * byte order.
     *
     * @return the next two bytes of the input stream, interpreted as a {@code char} in little-endian
     *     byte order
     * @throws IOException if an I/O error occurs
     */
    @Override
    public char readChar() throws IOException {
        return (char) readUnsignedShort();
    }

    @Override
    public byte readByte() throws IOException {
        return (byte) readUnsignedByte();
    }

    @Override
    public boolean readBoolean() throws IOException {
        return readUnsignedByte() != 0;
    }

    /**
     * Reads a byte from the input stream checking that the end of file (EOF) has not been
     * encountered.
     *
     * @return byte read from input
     * @throws IOException if an error is encountered while reading
     * @throws EOFException if the end of file (EOF) is encountered.
     */
    private byte readAndCheckByte() throws IOException {
        int b1 = in.read();

        if (-1 == b1) {
            throw new EOFException();
        }

        return (byte) b1;
    }
}