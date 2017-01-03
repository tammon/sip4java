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

import net.tammon.sip.packets.SipByteUtils;



public class ReadOnlyDataBody extends AbstractBody implements RequestBody {
    private static final int messageType = 71;
    private final short slaveIndex;
    private final int slaveExtension;
    private final String idn;

    public ReadOnlyDataBody(short slaveIndex, int slaveExtension, String idn) throws IllegalArgumentException{
        this.slaveIndex = slaveIndex;
        this.slaveExtension = slaveExtension;
        this.idn = idn;
    }

    @Override
    public byte[] getDataAsByteArray() {
        return SipByteUtils.concatenate(
                SipByteUtils.getByteArray(this.slaveIndex),
                SipByteUtils.getByteArray(this.slaveExtension),
                SipByteUtils.getIdnAsByteArray(this.idn));
    }

    @Override
    public int getMessageType() {
        return messageType;
    }
}
