package org.torrent.internal.data;

import org.torrent.internal.util.Validator;

/**
 * Does not resolve anything unless getInetSocketAddress() is called.
 * 
 * @author dante
 * 
 */
public class InetSocketAddress {

	private final String host;
	private final int port;
	private java.net.InetSocketAddress inetSA;

	public InetSocketAddress(String host, int port) {
		Validator.notNull(host, "Host is null!");
		Validator.isTrue(port >= 0 && port <= 65535, "Port out of range: "
				+ port);
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public synchronized java.net.InetSocketAddress getInetSocketAddress() {
		if (inetSA == null) {
			inetSA = new java.net.InetSocketAddress(host, port);
		}
		return inetSA;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InetSocketAddress) {
			InetSocketAddress other = (InetSocketAddress) obj;
			return getInetSocketAddress().equals(other.getInetSocketAddress());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return -getInetSocketAddress().hashCode();
	}

	@Override
	public String toString() {
		return host + ":" + port;
	}

}
