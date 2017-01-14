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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ReadOnlyDataTest {

    @Test
    void getPacketBody() {
        ReadOnlyData readOnlyData = new ReadOnlyData(2, (short)1, (short)0, "S-0-0100.1.0");
        assertArrayEquals(new byte[]{0x2, 0x0, 0x0, 0x0, 0x47, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0x64, 0x0, 0x0, 0x1},
                readOnlyData.getTcpMsgAsByteArray());
        readOnlyData = new ReadOnlyData(1, (short)1, (short)0, "S-0-0010");
        assertArrayEquals(new byte[]{0x1, 0x0, 0x0, 0x0, 0x47, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0xA, 0x0, 0x0, 0x0},
                readOnlyData.getTcpMsgAsByteArray());
        readOnlyData = new ReadOnlyData(4, (short)1, (short)0, "P-1-0010");
        assertArrayEquals(new byte[]{0x4, 0x0, 0x0, 0x0, 0x47, 0x0, 0x0, 0x0, 0x1, 0x0, 0x0, 0x0, 0xA, (byte)0x90, 0x0, 0x0},
                readOnlyData.getTcpMsgAsByteArray());
    }

}