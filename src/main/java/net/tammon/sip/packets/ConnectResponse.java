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

import net.tammon.sip.packets.parts.ConnectResponseBody;
import net.tammon.sip.packets.parts.Head;

import java.util.Arrays;

public class ConnectResponse extends ResponsePacket implements DynamicPacket {

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
        this.body = new ConnectResponseBody(Arrays.copyOfRange(rawData, head.getMsgLength(), rawData.length -1));
    }
}
