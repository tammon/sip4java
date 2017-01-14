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
 * An implementation of {@link DataOutput} that uses little-endian byte ordering for writing
 * {@code char}, {@code short}, {@code int}, {@code float}, {@code double}, and {@code long} values.
 * <p>
 * <p><b>Note:</b> This class intentionally violates the specification of its supertype
 * {@code DataOutput}, which explicitly requires big-endian byte order.
 *
 * @author Chris Nokleberg
 * @author Keith Bottner
 * @since 8.0
 */
public final class LittleEndianDataOutputStream extends FilterOutputStream implements DataOutput {

    /**
     * Creates a {@code LittleEndianDataOutputStream} that wraps the given stream.
     *
     * @param out the stream to delegate to
     */
    public LittleEndianDataOutputStream(OutputStream out) {
        super(new DataOutputStream(out));
    }

    private static byte[] toByteArray(long value) {
        byte[] result = new byte[8];

        for (int i = 7; i >= 0; --i) {
            result[i] = (byte) ((int) (value & 255L));
            value >>= 8;
        }

        return result;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // Override slow FilterOutputStream impl
        out.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        ((DataOutputStream) out).writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        ((DataOutputStream) out).writeByte(v);
    }

    /**
     * @deprecated The semantics of {@code writeBytes(String s)} are considered dangerous. Please use
     * {@link #writeUTF(String s)}, {@link #writeChars(String s)} or another write method instead.
     */
    @Deprecated
    @Override
    public void writeBytes(String s) throws IOException {
        ((DataOutputStream) out).writeBytes(s);
    }

    /**
     * Writes a char as specified by {@link DataOutputStream#writeChar(int)}, except using
     * little-endian byte order.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void writeChar(int v) throws IOException {
        writeShort(v);
    }

    /**
     * Writes a {@code String} as specified by {@link DataOutputStream#writeChars(String)}, except
     * each character is written using little-endian byte order.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void writeChars(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            writeChar(s.charAt(i));
        }
    }

    /**
     * Writes a {@code double} as specified by {@link DataOutputStream#writeDouble(double)}, except
     * using little-endian byte order.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    /**
     * Writes a {@code float} as specified by {@link DataOutputStream#writeFloat(float)}, except using
     * little-endian byte order.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    /**
     * Writes an {@code int} as specified by {@link DataOutputStream#writeInt(int)}, except using
     * little-endian byte order.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void writeInt(int v) throws IOException {
        out.write(0xFF & v);
        out.write(0xFF & (v >> 8));
        out.write(0xFF & (v >> 16));
        out.write(0xFF & (v >> 24));
    }

    /**
     * Writes a {@code long} as specified by {@link DataOutputStream#writeLong(long)}, except using
     * little-endian byte order.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void writeLong(long v) throws IOException {
        byte[] bytes = toByteArray(Long.reverseBytes(v));
        write(bytes, 0, bytes.length);
    }

    /**
     * Writes a {@code short} as specified by {@link DataOutputStream#writeShort(int)}, except using
     * little-endian byte order.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void writeShort(int v) throws IOException {
        out.write(0xFF & v);
        out.write(0xFF & (v >> 8));
    }

    @Override
    public void writeUTF(String str) throws IOException {
        ((DataOutputStream) out).writeUTF(str);
    }

    // Overriding close() because FilterOutputStream's close() method pre-JDK8 has bad behavior:
    // it silently ignores any exception thrown by flush(). Instead, just close the delegate stream.
    // It should flush itself if necessary.
    @Override
    public void close() throws IOException {
        out.close();
    }
}