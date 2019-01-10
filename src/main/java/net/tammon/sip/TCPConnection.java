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

import net.tammon.sip.exceptions.*;
import net.tammon.sip.packets.*;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The TCPConnection class implements the SipConnection Interface and creates a
 * sip connection via the TCP/IP protocol
 */
public class TCPConnection implements SipConnection {
	private InetAddress ipAddress;
	private int maxDelay, leaseTimeout, busyTimeout, sipPort, sipVersion;
	private int transactionId = 0;
	private boolean connected = false;
	private List<Integer> supportedMessages;
	private Socket socketConnection;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	private ScheduledExecutorService executorService;

	public TCPConnection(Properties properties) throws SipException {
		connect(properties);
	}

	private void connect(Properties properties) throws SipSocketTimeoutException, SipException {
		try {
			String host = null;
			if (properties.containsKey("host")) {
				host = properties.getProperty("host");
			}
			this.ipAddress = InetAddress.getByName((host == null) ? properties.getProperty("driveIp") : host);
		} catch (UnknownHostException e) {
			throw new SipInternalException(
					"Cannot resolve hostname. This is probably due to a misspelled hostname or bad dns configuration of host",
					e);

		}

		this.sipPort = Integer.parseInt(properties.getProperty("sipPort"));
		this.leaseTimeout = Integer.parseInt(properties.getProperty("leaseTimeout"));
		this.busyTimeout = Integer.parseInt(properties.getProperty("busyTimeout"));
		this.maxDelay = Integer.parseInt(properties.getProperty("maxDelay"));
		this.sipVersion = Integer.parseInt(properties.getProperty("sipVersion"));

		this.connectSocket();
		this.connectSip();

		boolean keepAlive = (boolean) properties.get("keepAlive");
		if (keepAlive)
			this.restartKeepAliveTimer();
	}

	/**
	 * Establishes a TCP connection to a sercos device with given IP Address
	 * <p>
	 * This connection can have a keepAlive flag which will maintain the SIP
	 * connection even if no request gets sent for a longer duration than the
	 * leaseTimeout. This will prevent the drive of closing the socket connection
	 * after the lease timeout. If a keep alive is used it is mandatory to call the
	 * disconnect() method to stop the keep alive functionality. If disconnect() is
	 * not called the tcp connection will prevent your program from exiting.
	 *
	 * @param host
	 *            domain name or IP Address of the drive
	 * @param keepAlive
	 *            flag if the connection should stay alive even if no request are
	 *            sent
	 * @throws SipException
	 *             in case of communication problems
	 */
	public TCPConnection(String host, boolean keepAlive) throws SipException {
		Properties properties = getDefaultProperties();
		properties.put("keepAlive", keepAlive);
		if (host != null) {
			properties.put("host", host);
		}
		connect(properties);
	}

	/**
	 * Establishes a TCP connection to a sercos device with the standard IP address
	 * of an IndraDrive (192.168.0.1) This connection has no keepAlive and will
	 * timeout if no input request comes in for longer then the standard
	 * leaseTimeout of 10s
	 *
	 * @throws SipException
	 *             in case of communication problems
	 */
	public TCPConnection() throws SipException {
		this(null, false);
	}

	/**
	 * Establishes a TCP connection to a sercos device with the given IP address
	 * This connection has no keepAlive and will timeout if no input request comes
	 * in for longer then the standard leaseTimeout of 10s
	 *
	 * @param host
	 *            domain name or IP Address of the drive
	 * @throws SipException
	 *             in case of communication problems
	 */
	public TCPConnection(String host) throws SipException {
		this(host, false);
	}

	/**
	 * @return sipDefault properties file as {@link Properties} Object
	 */
	private Properties getDefaultProperties() {
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("sipDefault.properties");
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			throw new SipInternalException("Problem occurred while trying to load sipDefault.properties", e);
		}
		return properties;
	}

	/**
	 * Establishes a new sip connection by reconnecting the socket and the sercos
	 * device. Resets the list of supported messages
	 *
	 * @throws SipSocketTimeoutException
	 *             in case of a socket timeout
	 */
	private synchronized void connectSocket() throws SipSocketTimeoutException {
		this.transactionId = 0;
		this.socketConnection = new Socket();
		try {
			this.socketConnection.connect(new InetSocketAddress(this.ipAddress, this.sipPort), busyTimeout);
			this.dataOutputStream = new DataOutputStream(this.socketConnection.getOutputStream());
			this.dataInputStream = new DataInputStream(this.socketConnection.getInputStream());
			this.supportedMessages = null;
		} catch (SocketTimeoutException e) {
			throw new SipSocketTimeoutException(
					// "Drive does not respond to socket request. Probably wrong IP or not on
					// network...");
					"Drive does not respond. Probably Drive is not online.");
		} catch (IOException e) {
			// throw new SipInternalException("An internal S/IP Exception occured", e);
			throw new SipInternalException(
					"Probably Drive is not online or the IP:'" + this.ipAddress.toString() + "' is wrong.", e);
		}
	}

	/**
	 * Generates synchronized a new transaction id for a new sip interaction by
	 * increasing the current transaction id by one.
	 *
	 * @return new transaction id
	 */
	private synchronized int getNewTransactionId() {
		return this.transactionId++;
	}

	/**
	 * This method is the general abstraction for all sip tcp communication between
	 * the library and the sercos device. It takes tcp request and response packets
	 * of the sip library. It sends the request and returns the response. The tcp
	 * send and receive logic is fully synchronized to avoid multiple instances and
	 * therefor requests at a time.
	 *
	 * @param request
	 *            sip request tcp packet
	 * @param response
	 *            sip response tcp packet type (generally empty initialized object)
	 * @return sip response tcp packet of the given type with the packet data set to
	 *         the object
	 * @throws SipException
	 *             in case of communication problems
	 */
	private synchronized Response getTcpResponse(Request request, Class response) throws SipException {
		if (this.isSupported(request.getMessageType()))
			throw new SipServiceNotSupportedException("The requested operation " + request.getClass().getSimpleName()
					+ " is not in the drive's list of supported messages");

		sendDataToServer(request.getTcpMsgAsByteArray());

		byte[] rawResponse = getRawResponseFromSocket();
		return getResponse(rawResponse, request, response);
	}

	/**
	 * Returns an instantiated object of specified response class with the data of
	 * the raw response. In addition to that this method checks if the data is valid
	 * and matches the request.
	 *
	 * @param rawResponse
	 *            raw data from socket
	 * @param request
	 *            the request object that belongs to the response
	 * @param responseClass
	 *            specifies the response class which will be used as instantiated
	 *            object for return type
	 * @return response with data from type responseClass
	 * @throws SipProtocolException
	 *             in case the sercos device threw an communication exception (e.g.
	 *             invalid request) or in case of a wrong transaction id
	 * @throws SipServiceNotSupportedException
	 *             in case the sercos device does not support the requested message
	 *             type
	 */
	private Response getResponse(byte[] rawResponse, Request request, Class responseClass)
			throws SipProtocolException, SipServiceNotSupportedException {
		try {

			Head header = new Head(rawResponse);

			// Check if we got the right response to our request
			if (header.getTransactionId() != request.getTransactionId())
				throw new SipProtocolException(
						"The response transaction ID " + header.getTransactionId()
								+ " doesn't match the request transaction ID " + request.getTransactionId());

			// Check if Drive threw an communication exception
			if (header.getMessageType() == 67) {
				ExceptionResponse exceptionResponse = new ExceptionResponse(rawResponse);
				if (exceptionResponse.getCommonErrorCode() == CommonErrorCodes.SERVICESPECIFIC)					 
					throw new SipProtocolException("Drive threw Communication Exception."
						+ ((exceptionResponse.getCommonErrorCode() == CommonErrorCodes.SERVICESPECIFIC)
								? (" SIP-SpecificErrorCode: " + exceptionResponse.getSpecificErrorCode())
								: (" SIP-CommonErrorCode: " + exceptionResponse.getCommonErrorCode())));
				
				if (exceptionResponse.getCommonErrorCode() == CommonErrorCodes.UNKNOWN_MESSAGE_TYPE)
					throw new SipProtocolException("Service not supported.");					
			}			

			Response response = (Response) responseClass.newInstance();

			// TODO busy response einfügen

			if (header.getMessageType() == response.getMessageType())
				response.setData(rawResponse);
			else
				throw new SipInternalException("Invalid Message Type Response");
			return response;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SipInternalException("Invalid Response Class Type. Cannot instantiate object.", e);
		} catch (IOException e) {
			throw new SipInternalException(
					"An internal error occurred during conversion of raw data to response object.", e);
		}
	}

	/**
	 * Reads the data from the open Socket
	 *
	 * @return the raw data of the socket as byte array
	 * @throws SipCommunicationException
	 *             in case of any problem occurs during socket communication
	 */
	private byte[] getRawResponseFromSocket() throws SipCommunicationException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[10024];
			int readLength;
			do {
				readLength = dataInputStream.read(buffer);
				if (readLength >= 0) {
					outputStream.write(buffer, 0, readLength + 1);
				}
			} while (readLength >= 10024);

			return outputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();// hinzugefügt von Philip Weis
			throw new SipCommunicationException("Cannot read from Socket", e);
		}
	}

	/**
	 * sends a byte array via the open socket of this Sip connection
	 *
	 * @param data
	 *            the raw data to send
	 * @throws SipCommunicationException
	 *             in case of any problem occurs during socket communication
	 */
	private void sendDataToServer(byte[] data) throws SipCommunicationException {
		try {
			this.dataOutputStream.write(data);
			this.dataOutputStream.flush();
		} catch (IOException e) {
			throw new SipCommunicationException("Cannot write output stream data to S/IP device", e);
		}
	}

	/**
	 * checks whether a message type is supported by the connected S/IP device
	 *
	 * @param messageType
	 *            message type to check
	 * @return message type supported
	 */
	private boolean isSupported(int messageType) {
		return !Objects.isNull(this.supportedMessages) && !this.supportedMessages.contains(messageType);
	}

	/**
	 * This method sends a sip connection request to the sercos device and handles
	 * the incoming response
	 *
	 * @throws SipException
	 *             in case of communication problems
	 */
	private void connectSip() throws SipException {
		Connect request = new Connect(this.getNewTransactionId(), this.sipVersion, this.busyTimeout, this.leaseTimeout);
		ConnectResponse response = (ConnectResponse) this.getTcpResponse(request, ConnectResponse.class);
		synchronized (this) {
			this.supportedMessages = IntStream.of(response.getSupportedMessageTypes()).boxed().collect(Collectors.toList());
			this.connected = true;
		}
	}

	/**
	 * Checks if the sercos device responds to a ping sip message
	 *
	 * @return true if the device responds, false if it doesn't
	 */
	private boolean respondsToPing() {
		Ping ping = new Ping(this.getNewTransactionId());
		try {
			this.getTcpResponse(ping, Pong.class);
			return true; // hinzugefügt von Philip Weis
			// } catch (SipException e) {
		} catch (Exception e) { // abgeändert von Philip Weis
			e.printStackTrace(); // hinzugefügt von Philip Weis
			return false; // hinzugefügt von Philip Weis
		}
		// return true; //abgeändert von Philip Weis
	}

	/**
	 * This method is the general abstraction of all public ReadOnlyData methods. It
	 * actually creates the request and response bodies and triggers the TCP send
	 * and receive.
	 *
	 * @param slaveIndex
	 *            the slave index of the sercos device (default: 0)
	 * @param slaveExtension
	 *            the slave extentension of the sercos device (default: 0)
	 * @param idn
	 *            the 16-bit or 32-bit identifier of the parameter one wants to read
	 *            (e.g. "P-0-0100" or "S-0-0100.1.1")
	 * @return the {@link ReadOnlyDataResponse} which is received after the tcp
	 *         request
	 * @throws SipException
	 *             if any communication or data handling problem occurs
	 */
	public Data readData(int slaveIndex, int slaveExtension, String idn) throws SipException {
		ReadOnlyData request = new ReadOnlyData(this.getNewTransactionId(), (short) slaveIndex, (short) slaveExtension,
				idn);
		ReadOnlyDataResponse response = (ReadOnlyDataResponse) this.getTcpResponse(request, ReadOnlyDataResponse.class);
		
		return response.getData();
	}

	/**
	 * Checks whether or not the TCP connection to the sercos slave is still
	 * connected
	 *
	 * @return true if the connection is alive
	 */
	@Override
	public boolean isConnected() {
		synchronized (this) {
			return connected && this.socketConnection.isConnected();
		}
	}

	/**
	 * Returns a list of the supported message types of the sercos device. This list
	 * is initially sent by the device during the sip connection process.
	 *
	 * @return the list of supported message types
	 */
	@Override
	public List<Integer> getSupportedMessages() {
		synchronized (this) {
			return supportedMessages;
		}
	}

	/**
	 * Returns the IP address of the sercos device which is used by this TCP
	 * connection.
	 *
	 * @return IP address of sercos device as {@link InetAddress}
	 */
	@Override
	public InetAddress getIpAddress() {
		return ipAddress;
	}

	/**
	 * Returns the used sercos device port of the current sip connection
	 *
	 * @return used sercos device port
	 */
	@Override
	public int getSipPort() {
		return sipPort;
	}

	/**
	 * Returns the used version of the Sercos Internet Protocol
	 *
	 * @return version of Sercos Internet Protocol
	 */
	@Override
	public int getSipVersion() {
		return sipVersion;
	}

	private void restartKeepAliveTimer() {
		if (Objects.isNull(this.executorService))
			this.executorService = Executors.newScheduledThreadPool(1);
		else
			this.executorService.shutdownNow();
		final long period = Math.round(this.leaseTimeout * 0.7);
		executorService.scheduleAtFixedRate(this::respondsToPing, 0, period, TimeUnit.MILLISECONDS);
	}

	/**
	 * Stops the keep alive loop and closes the socket connection to the sercos
	 * device
	 */
	@Override
	public void disconnect() {
	  
	  close();

		if (executorService != null) {
			try {
				executorService.shutdownNow();
			} catch (Exception e) {
				// No exception handling here
			}
		}

		try {
		  synchronized (this) {
			  if (socketConnection != null) {
				  socketConnection.close();
				  socketConnection = null;
			  }
		  }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

  private void close() {
    try {
      if (dataInputStream != null) {
        dataInputStream.close();
        dataInputStream = null;
      }
      if (dataOutputStream != null) {
        dataOutputStream.close();
        dataOutputStream = null;
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
}
