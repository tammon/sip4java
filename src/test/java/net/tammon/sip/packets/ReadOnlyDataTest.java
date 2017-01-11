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