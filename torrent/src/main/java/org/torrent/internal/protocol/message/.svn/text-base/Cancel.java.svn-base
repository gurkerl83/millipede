package org.torrent.internal.protocol.message;

import org.torrent.internal.data.BTPart;
import org.torrent.internal.util.Validator;

public class Cancel implements BittorrentMessage {

	private BTPart pieceInfo;

	public Cancel(int index, int start, int length) {
		this(new BTPart(index, start, length));
	}

	public Cancel(BTPart pi) {
		Validator.notNull(pi, "PieceInfo is null!");
		pieceInfo = pi;
	}

	public int getIndex() {
		return pieceInfo.getIndex();
	}

	public int getStart() {
		return pieceInfo.getStart();
	}

	public int getLength() {
		return pieceInfo.getLength();
	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitCancel(this);
	}

	public BTPart getPieceInfo() {
		return pieceInfo;
	}

	@Override
	public String toString() {
		return "Cancel: " + pieceInfo;
	}
}
