/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package net.tammon.sip.packets;

import net.tammon.sip.packets.parts.ResponseBody;

abstract class AbstractResponsePacket extends AbstractPacket implements Response {
    protected ResponseBody body;

    public AbstractResponsePacket (){
        this.body = null;
        this.head = null;
    }
}
