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

package net.tammon.sip;

import java.net.InetAddress;
import java.util.List;

public interface SipConnection {
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
}
