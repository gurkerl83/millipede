package org.torrent.internal.protocol.message;

import org.torrent.internal.util.Validator;

public class Port implements BittorrentMessage {

	private final int port;

	public Port(int port) {
		Validator.isTrue(port >= 0 && port <= 65535, "Port out of range: "
				+ port);
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitPort(this);
        }

	@Override
	public String toString() {
		return "Port (DHT): port = " + port;
	}
}
