package org.torrent.internal.io;

import java.io.IOException;
import java.util.EventObject;

import org.torrent.internal.data.Hash;
import org.torrent.internal.data.TorrentMetaInfo.Piece;

public class PieceEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private IOException exception;
	private transient Piece piece = null;
	private transient Hash hashResult;

	public PieceEvent(Object source) {
		super(source);
	}

	public PieceEvent(Object source, IOException e, Piece piece) {
		this(source);
		exception = e;
		this.piece = piece;
	}

	public PieceEvent(Object source, Piece piece, Hash result) {
		this(source);
		this.piece = piece;
		this.hashResult = result;
	}

	public IOException getException() {
		return exception;
	}

	public Piece getPiece() {
		return piece;
	}

	public Hash getHashResult() {
		return hashResult;
	}
}
