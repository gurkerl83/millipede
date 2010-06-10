package org.torrent.internal.service;

import org.merapi.helper.handlers.BarUpdateRequestHandler;
import org.merapi.helper.messages.BarUpdateRespondMessage;
import org.torrent.internal.data.TorrentMetaInfo.Piece;
import org.torrent.internal.util.Bits;

public class BasicContentStateService extends AbstractContentStateService {
	/**
	 * Encoded states: <br>
	 * 00 - Unknown<br>
	 * 01 - Available<br>
	 * 10 - Required<br>
	 * 11 - Validated
	 */
	private final Bits bits;

	public BasicContentStateService(EventDispatcherService disp, int numPieces) {
		super(disp);
		bits = new Bits(numPieces * 2);
	}

	@Override
	public boolean isAvailable(Piece piece) {
		int idx = piece.getIndex() * 2;
		return !bits.get(idx) && bits.get(idx + 1);
	}

	@Override
	public boolean isRequired(Piece piece) {
		int idx = piece.getIndex() * 2;
		return bits.get(idx) && !bits.get(idx + 1);
	}

	@Override
	public boolean isValidated(Piece piece) {
		int idx = piece.getIndex() * 2;
		return bits.get(idx) && bits.get(idx + 1);
	}

	@Override
	public void setAvailable(Piece piece) {
		int idx = piece.getIndex() * 2;
		bits.set(idx, false);
		bits.set(idx + 1, true);
		super.setAvailable(piece);
	}

	@Override
	public void setRequired(Piece piece) {
		int idx = piece.getIndex() * 2;
		bits.set(idx, true);
		bits.set(idx + 1, false);
		super.setRequired(piece);
	}

	@Override
	public void setValidated(Piece piece) {
		int idx = piece.getIndex() * 2;
		bits.set(idx, true);
		bits.set(idx + 1, true);
		super.setValidated(piece);
	}
}
