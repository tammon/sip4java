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
