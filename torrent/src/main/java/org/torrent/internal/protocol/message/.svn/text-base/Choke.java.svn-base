package org.torrent.internal.protocol.message;


public class Choke implements BittorrentMessage {
	public static final Choke CHOKE = new Choke();

	private Choke() {

	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitChoke(this);
	}

	@Override
	public String toString() {
		return "Choke";
	}
}
