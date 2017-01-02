/*
 * Sercos Internet Protocol (SIP) version 1 - Java Implementation
 * Copyright (c) 2017. by tammon (Tammo Schwindt)
 * This code is licensed under the GNU LGPLv2.1
 */

package de.tammon.dev.sip.packets.parts;

import de.tammon.dev.sip.packets.SipByteUtils;

import java.io.DataInputStream;

public class ExceptionBody extends AbstractBody implements ResponseBody {
    final static int messageType = 67;
    short commonErrorCode;
    int specificErrorCode;

    public ExceptionBody(byte[]... rawBodyDataArrays) throws Exception {
        DataInputStream data = SipByteUtils.getDataInputStreamOfRawData(rawBodyDataArrays);
        this.commonErrorCode = SipByteUtils.getSipPrimitive(data.readShort());
        this.specificErrorCode = SipByteUtils.getSipPrimitive(data.readInt());
    }

    public static int getMessageType() {
        return messageType;
    }

    @Override
    public byte[] getDataAsByteArray() {
        return SipByteUtils.concatenate(
                SipByteUtils.getByteArray(this.commonErrorCode),
                SipByteUtils.getByteArray(this.specificErrorCode));
    }

    public short getCommonErrorCode() {
        return commonErrorCode;
    }

    public int getSpecificErrorCode() {
        return specificErrorCode;
    }
}
