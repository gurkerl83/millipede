package org.torrent.internal.protocol.message;


public class NotInterested implements BittorrentMessage {

	public static final NotInterested NOTINTERESTED = new NotInterested();

	private NotInterested() {
	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitNotInterested(this);
	}

	@Override
	public String toString() {
		return "Not Interested";
	}
}
