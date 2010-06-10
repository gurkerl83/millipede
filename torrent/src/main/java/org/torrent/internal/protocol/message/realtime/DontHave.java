package org.torrent.internal.protocol.message.realtime;

import org.torrent.internal.data.BTPart;
import org.torrent.internal.protocol.message.BTMessageVisitor;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.util.Validator;

public class DontHave implements BittorrentMessage {

	private BTPart pieceInfo;

	public DontHave(int index) {
		//maybe not converting in a BTPart
		this(new BTPart(index, 0, 0));
	}

	public DontHave(BTPart pi) {
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
		BTMessageVisitorRealtime rtVisitor = (BTMessageVisitorRealtime) visitor;
		rtVisitor.visitDontHave(this);
	}

	public BTPart getPieceInfo() {
		return pieceInfo;
	}

	@Override
	public String toString() {
		return "DontHave: " + pieceInfo;
	}
}
