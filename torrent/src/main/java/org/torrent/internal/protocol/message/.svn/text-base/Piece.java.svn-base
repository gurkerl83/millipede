package org.torrent.internal.protocol.message;

import java.nio.ByteBuffer;

import org.torrent.internal.data.BTPart;
import org.torrent.internal.util.Validator;

public class Piece implements BittorrentMessage {

	private final ByteBuffer data;
	private final BTPart pieceInfo;

	public Piece(int index, int start, ByteBuffer data) {
		Validator.isTrue(index >= 0, "Invalid index: " + index);
		Validator.isTrue(start >= 0, "Invalid start: " + start);
		pieceInfo = new BTPart(index, start, data.remaining());
		Validator.notNull(data, "Data is null");
		this.data = data;
	}

	public Piece(BTPart pi, ByteBuffer data) {
		this(pi.getIndex(), pi.getStart(), data);
		Validator.isTrue(pi.getLength() == data.remaining(),
				"Length and data.length differ!");
	}

	public ByteBuffer getData() {
		return data.duplicate();
	}

	public int getLength() {
		return data.remaining();
	}

	public int getIndex() {
		return pieceInfo.getIndex();
	}

	public int getStart() {
		return pieceInfo.getStart();
	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitPiece(this);
	}

	public BTPart getPieceInfo() {
		return pieceInfo;
	}

	@Override
	public String toString() {
		return "Piece: " + pieceInfo;
	}
}
