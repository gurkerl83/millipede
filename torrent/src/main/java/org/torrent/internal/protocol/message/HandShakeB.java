package org.torrent.internal.protocol.message;

import org.torrent.internal.data.Hash;
import org.torrent.internal.util.Validator;

public class HandShakeB implements BittorrentMessage {

	private final Hash peer_id;

	public HandShakeB(Hash peer_id) {
		Validator.notNull(peer_id, "Peer id is null!");
		Validator.isTrue(peer_id.getType() == Hash.Type.ID,
				"Expected peer id to be of type ID, but is "
						+ peer_id.getType());
		Validator.isTrue(peer_id.getHashLength() == 20,
				"Expected hash length 20, but got " + peer_id.getHashLength());
		this.peer_id = peer_id;
	}

	public Hash getPeerID() {
		return peer_id;
	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitHandShakeB(this);
	}

	@Override
	public String toString() {
		return "HandShakeB: peerID = " + peer_id;
	}
}
