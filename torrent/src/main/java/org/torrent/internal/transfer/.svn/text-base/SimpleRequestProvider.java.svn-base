package org.torrent.internal.transfer;

import java.util.Comparator;

import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.data.TorrentMetaInfo.Piece;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.util.Bits;

public class SimpleRequestProvider extends AbstractRequestProvider {
	private final Bits needed;
	private Comparator<Piece> pieceComparator = new Comparator<Piece>() {

		@Override
		public int compare(Piece o1, Piece o2) {
			return o1.getIndex() - o2.getIndex();
		}

	};

	public SimpleRequestProvider(TorrentMetaInfo contentInfo,
			ContentWatcher watcher, int partLength, int maxRequestsPerConnection) {
		super(contentInfo, watcher, partLength, maxRequestsPerConnection);
		needed = new Bits(contentInfo.getPiecesCount());
	}

	public SimpleRequestProvider(TorrentMetaInfo contentInfo,
			ContentWatcher watcher, int partLength, RequestLimiter limiter) {
		super(contentInfo, watcher, partLength, limiter);
		needed = new Bits(contentInfo.getPiecesCount());
	}

	@Override
	protected Piece getBestIndex(BTConnection con) {
		Bits remote = con.getRemoteBitField();
		Piece result = null;
		for (int i = needed.firstIndexOf(true); i >= 0; i = needed
				.firstIndexOf(true, i + 1)) {
			if (remote.get(i)) {
				Piece tmp = getContentInfo().getPiece(i);
				if (result == null || pieceComparator.compare(tmp, result) < 0) {
					result = tmp;
				}
			}
		}
		System.out.println("getBestIndex Result: " + result.toString());
		return result;
	}

	public void setPieceComparator(Comparator<Piece> pieceComparator) {
		this.pieceComparator = pieceComparator;
	}

	@Override
	protected void completelyRequested(Piece piece) {
		needed.set(piece.getIndex(), false);
	}

	@Override
	protected void notCompletelyRequested(Piece piece) {
		needed.set(piece.getIndex(), true);
	}

	@Override
	protected boolean hasOpenRequests(BTConnection con) {
		Bits remote = con.getRemoteBitField();
		for (int i = needed.firstIndexOf(true); i >= 0; i = needed
				.firstIndexOf(true, i + 1)) {
			if (remote.get(i)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasNoRequiredParts(Piece piece) {
		return !needed.get(piece.getIndex());
	}
}
