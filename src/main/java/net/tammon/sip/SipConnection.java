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

import java.net.InetAddress;
import java.util.List;

public interface SipConnection {

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     * It reads out the raw data of the response as raw data byte array.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    byte[] readDataAsRawByteArray(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Checks whether or not the TCP connection to the sercos slave is still connected
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
     * Returns the used sercos device port of the current sip connection
     *
     * @return used sercos device port
     */
    int getSipPort();

    /**
     * Returns the used version of the Sercos Internet Protocol
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
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    String readDataAsString(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    byte readDataAsByte(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    short readDataAsShort(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    int readDataAsInt(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    long readDataAsLong(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    float readDataAsFloat(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    double readDataAsDouble(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    byte[] readDataAsByteArray(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    short[] readDataAsShortArray(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    int[] readDataAsIntArray(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    long[] readDataAsLongArray(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    float[] readDataAsFloatArray(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Sends a ReadOnlyDataRequest to the sercos device and handles the response.
     *
     * @param slaveIndex     the slave index of the sercos device (default: 0)
     * @param slaveExtension the slave extentension of the sercos device (default: 0)
     * @param idn            the 16-bit or 32-bit identifier of the parameter one wants to read (e.g. "P-0-0100" or "S-0-0100.1.1")
     * @return the data of the response in the specified format
     * @throws Exception if any communication or data handling problem occurs
     */
    double[] readDataAsDoubleArray(int slaveIndex, int slaveExtension, String idn) throws Exception;

    /**
     * Stops the keep alive loop and closes the socket connection to the sercos device
     */
    void disconnect();
}
