/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets;

abstract class AbstractResponsePacket extends AbstractPacket implements Response {

    public AbstractResponsePacket (){
        this.body = null;
        this.head = null;
    }
}
