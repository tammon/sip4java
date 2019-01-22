package net.tammon.sip;

import java.util.Properties;

import net.tammon.sip.exceptions.SipException;

/**
 * A factory for creating Sip objects.
 */
final public class SipFactory {
    
    /**
     * New instance.
     *
     * @param properties the properties
     * @return the sip connection
     * @throws SipException the sip exception
     */
    public static SipConnection newInstance(Properties properties) throws SipException {
        TCPConnection tcpConnection = new TCPConnection(properties);
        return tcpConnection;
    }

}
