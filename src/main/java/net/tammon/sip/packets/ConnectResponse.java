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

import net.tammon.sip.packets.parts.ConnectResponseBody;
import net.tammon.sip.packets.parts.Head;

import java.util.Arrays;

public class ConnectResponse extends AbstractResponsePacket implements DynamicPacket {

    @Override
    public ConnectResponseBody getPacketBody() {
        return (ConnectResponseBody) this.body;
    }

    @Override
    public int getMessageType() {
        return ConnectResponseBody.getMessageType();
    }

    @Override
    public void setData(byte[] rawData) throws Exception {
        this.head = new Head(rawData);
        this.body = new ConnectResponseBody(Arrays.copyOfRange(rawData, 8, rawData.length -1));
    }
}
