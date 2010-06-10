package org.torrent.internal.protocol.message;

import org.torrent.internal.data.BTPart;
import org.torrent.internal.util.Validator;

public class Request implements BittorrentMessage {
	private BTPart pieceInfo;

	public Request(int index, int start, int length) {
		this(new BTPart(index, start, length));
	}

	public Request(BTPart pi) {
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
		visitor.visitRequest(this);
	}

	public BTPart getPieceInfo() {
		return pieceInfo;
	}

	@Override
	public String toString() {
		return "Request: " + pieceInfo;
	}
}
