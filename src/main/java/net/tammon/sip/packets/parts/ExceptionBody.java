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

import java.io.DataInputStream;

public class ExceptionBody extends AbstractBody implements ResponseBody {
    private final static int messageType = 67;
    private short rawCommonErrorCode;
    private int specificErrorCode;
    private commonErrorCodes commonErrorCode;
    public ExceptionBody(byte[]... rawBodyDataArrays) throws Exception {
        DataInputStream data = SipByteUtils.getDataInputStreamOfRawData(rawBodyDataArrays);
        this.rawCommonErrorCode = SipByteUtils.getSipPrimitive(data.readShort());
        this.specificErrorCode = SipByteUtils.getSipPrimitive(data.readInt());
        this.commonErrorCode = commonErrorCodes.values()[this.rawCommonErrorCode-1];
    }

    public static int getMessageType() {
        return messageType;
    }

    public commonErrorCodes getCommonErrorCode() {
        return commonErrorCode;
    }

    public int getSpecificErrorCode() {
        return specificErrorCode;
    }

    public enum commonErrorCodes {
        CONNECTION_ERROR, TIMEOUT, UNKNOWN_MESSAGE_TYPE, SERVICESPECIFIC, PDU_TOO_LARGE, PDU_PROTOCOL_MISMATCH
    }
}
