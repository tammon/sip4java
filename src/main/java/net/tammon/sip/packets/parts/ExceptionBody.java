/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
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
