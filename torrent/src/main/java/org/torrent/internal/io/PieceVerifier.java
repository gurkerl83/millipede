package org.torrent.internal.io;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.merapi.helper.handlers.BarUpdateRequestHandler;
import org.merapi.helper.messages.BarUpdateRespondMessage;
import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.util.BTUtil;
import org.torrent.internal.util.Validator;

public abstract class PieceVerifier {
	public interface PieceVerifierEventListener {
		void verified(PieceEvent evt);

		void falsified(PieceEvent evt);

		void caughtException(PieceEvent evt);
	}

	private List<PieceVerifierEventListener> listeners = new CopyOnWriteArrayList<PieceVerifierEventListener>();

	public abstract void checkPiece(TorrentMetaInfo.Piece piece);

	protected void firePieceVerified(final PieceEvent evt) {
		BTUtil.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (PieceVerifierEventListener l : listeners) {
					l.verified(evt);
				}
			}
		});
	}

	protected void firePieceFalsified(final PieceEvent evt) {
		BTUtil.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (PieceVerifierEventListener l : listeners) {
					l.falsified(evt);
				}
			}
		});
	}

	protected void fireCaughtException(final PieceEvent evt) {
		BTUtil.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (PieceVerifierEventListener l : listeners) {
					l.caughtException(evt);
				}
			}
		});
	}

	public void addPieceVerifierEventListener(
			PieceVerifierEventListener listener) {
		Validator.nonNull(listener);
		listeners.add(listener);
	}

	public void removePieceVerifierEventListener(
			PieceVerifierEventListener listener) {
		Validator.nonNull(listener);
		listeners.remove(listener);
	}
}
