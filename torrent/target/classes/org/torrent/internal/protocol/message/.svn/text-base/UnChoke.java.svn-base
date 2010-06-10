package org.torrent.internal.protocol.message;


public class UnChoke implements BittorrentMessage {

	public static final UnChoke UNCHOKE = new UnChoke();

	private UnChoke() {
	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitUnChoke(this);
	}

	@Override
	public String toString() {
		return "UnChoke";
	}
}
