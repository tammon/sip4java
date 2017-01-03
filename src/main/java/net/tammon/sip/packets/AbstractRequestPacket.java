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

import net.tammon.sip.packets.parts.Head;
import net.tammon.sip.packets.parts.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

abstract class AbstractRequestPacket extends AbstractPacket implements Request{
    protected RequestBody body;

    public AbstractRequestPacket(int transactionId, RequestBody requestBody) {
        this.head = new Head(transactionId, requestBody.getMessageType());
        this.body = requestBody;
    }

    public AbstractRequestPacket(int transactionId, int messageType){
        this.head = new Head(transactionId, messageType);
    }

    public byte[] getTcpMsgAsByteArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(this.head.getDataAsByteArray());
            out.write(this.body.getDataAsByteArray());
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
