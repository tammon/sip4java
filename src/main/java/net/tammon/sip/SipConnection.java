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

package net.tammon.sip;

import net.tammon.sip.packets.Data;

import java.net.InetAddress;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface SipConnection.
 */
public interface SipConnection {

    /**
     * Checks whether or not the TCP connection to the sercos slave is still connected.
     *
     * @return true if the connection is alive
     */
    boolean isConnected();

    /**
     * Returns the IP address of the sercos device which is used by this TCP connection.
     *
     * @return IP address of sercos device as {@link InetAddress}
     */
    InetAddress getIpAddress();

    /**
     * Returns the used sercos device port of the current sip connection.
     *
     * @return used sercos device port
     */
    int getSipPort();

    /**
     * Returns the used version of the Sercos Internet Protocol.
     *
     * @return version of Sercos Internet Protocol
     */
    int getSipVersion();

    /**
     * Returns a list of the supported message types of the sercos device.
     * This list is initially sent by the device during the sip connection process.
     *
     * @return the list of supported message types
     */
    List<Integer> getSupportedMessages();

    /**
     * Stops the keep alive loop and closes the socket connection to the sercos device.
     */
    void disconnect();

    /**
     * Single Request
     * Read data.
     *
     * @param slaveIndex the slave index
     * @param slaveExtension the slave extension
     * @param idn the idn
     * @return the data
     * @throws Exception the exception
     */
    Data readData(int slaveIndex, int slaveExtension, String idn) throws Exception;
    
    /**
     * Multiple Requests
     * Read datas.
     *
     * @param jobs the jobs
     * @return the sip job state[]
     * @throws Exception the exception
     */
    SipJobState[] readDatas(SipJob [] jobs)  throws Exception;
}
