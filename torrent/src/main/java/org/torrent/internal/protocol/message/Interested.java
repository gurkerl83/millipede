package org.torrent.internal.protocol.message;


public class Interested implements BittorrentMessage {
	public static final Interested INTERESTED = new Interested();

	private Interested() {

	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitInterested(this);
	}

	@Override
	public String toString() {
		return "Interested";
	}
}
