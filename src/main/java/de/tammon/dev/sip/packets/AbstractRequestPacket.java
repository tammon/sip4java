/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets;

import de.tammon.dev.sip.packets.parts.Head;
import de.tammon.dev.sip.packets.parts.RequestBody;

abstract class AbstractRequestPacket extends AbstractPacket implements Request{

    public AbstractRequestPacket(int transactionId, RequestBody requestBody) {
        this.head = new Head(transactionId, requestBody.getMessageType());
        this.body = requestBody;
    }
}
