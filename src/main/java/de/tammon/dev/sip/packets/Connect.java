/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets;


import de.tammon.dev.sip.packets.parts.ConnectBody;

public class Connect extends AbstractRequestPacket {

    public Connect(int transactionId, int sipVersion, int busyTimeout, int leaseTimeout) {
        super(transactionId, new ConnectBody(sipVersion, busyTimeout, leaseTimeout));
    }

    @Override
    public ConnectBody getPacketBody() {
        return (ConnectBody) this.body;
    }
}
