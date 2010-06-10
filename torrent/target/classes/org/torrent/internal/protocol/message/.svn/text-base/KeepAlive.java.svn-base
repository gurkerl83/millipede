package org.torrent.internal.protocol.message;


public class KeepAlive implements BittorrentMessage {

	public static final KeepAlive KEEPALIVE = new KeepAlive();

	private KeepAlive() {
	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitKeepAlive(this);
	}

	@Override
	public String toString() {
		return "KeepAlive";
	}
}
