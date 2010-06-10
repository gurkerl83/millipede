package org.torrent.internal.transfer;

import java.util.HashMap;
import java.util.Map;

import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Piece;

public class SimpleScoreKeeper extends BTSessionListenerAdapter {
	private Map<BTConnection, Long> scores = new HashMap<BTConnection, Long>();

	@Override
	public synchronized void addedConnection(BTConnection con) {
		scores.put(con, 0L);
	}

	@Override
	public void receivedBTMessage(BTConnection from, BittorrentMessage message) {
		if (message instanceof Piece) {
			Piece piece = (Piece) message;
			changeScore(from, piece.getLength());
		}
	}

	@Override
	public void receivedUnrequestedPiece(BTConnection from, Piece piece) {
		// Wasting our bandwidth will decrease a peers score (We could have
		// received valid data instead)
		// This is problematic if we canceled a request which was underway. (Aka
		// endgame mode)
		changeScore(from, -piece.getLength());
	}

	@Override
	public synchronized void removedConnection(BTConnection con) {
		scores.remove(con);
	}

	@Override
	public void sentBTMessage(BTConnection from, BittorrentMessage message) {
		if (message instanceof Piece) {
			Piece piece = (Piece) message;
			changeScore(from, -piece.getLength());
		}
	}

	private synchronized void changeScore(BTConnection from, int add) {
		scores.put(from, scores.get(from) - add);
	}

	public synchronized void checkFailedPenalty(BTConnection con,
			int amountReceived) {
		// Idea: Subtract 2 times the received amount, once to remove the
		// awarded points when receiving the pieces and twice for making us
		// download the hole thing again
		changeScore(con, -2 * amountReceived);
	}

	public synchronized Long getScoreOf(BTConnection con) {
		return scores.get(con);
	}
}
