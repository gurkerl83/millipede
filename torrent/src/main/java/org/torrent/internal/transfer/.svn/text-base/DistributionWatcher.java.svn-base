package org.torrent.internal.transfer;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.data.TorrentMetaInfo.Piece;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.util.Bits;
import org.torrent.internal.util.Validator;

public abstract class DistributionWatcher extends BTSessionListenerAdapter {
	protected ConcurrentMap<BTConnection, Coverage> coverages = new ConcurrentHashMap<BTConnection, Coverage>();
	private volatile Comparator<Piece> pieceComparator;
	private final Bits required;
	private final TorrentMetaInfo contentInfo;

	public interface Coverage {

		void markAsRequired(Piece piece);

		void markNotRequired(Piece piece);

		Piece bestRequired();

		void setComparator(Comparator<Piece> comparator);

		boolean hasRequired();
	}

	public DistributionWatcher(TorrentMetaInfo contentInfo) {
		Validator.notNull(contentInfo, "ContentInfo is null!");
		this.contentInfo = contentInfo;
		required = new Bits(contentInfo.getPiecesCount());
	}

	private void addCoverage(BTConnection con) {
		Coverage cov = createCoverage(con);
		cov.setComparator(pieceComparator);
		coverages.put(con, cov);
		for (int i = 0; i < required.size(); i++) {
			if (con.getRemoteBitField().get(i)) {
				setNeeded(con, i, required.get(i));
			}
		}
	}

	private void removeCoverage(BTConnection con) {
		coverages.remove(con);
	}

	@Override
	public void connectionEstablished(BTConnection connection) {
		addCoverage(connection);
	}

	@Override
	public void removedConnection(BTConnection con) {
		removeCoverage(con);
	}

	@Override
	public void bitFieldBitChanged(IndexedPropertyChangeEvent event) {
		setNeeded((BTConnection) event.getSource(), event.getIndex(), required
				.get(event.getIndex())
				&& event.getNewValue().equals(Boolean.TRUE));
	}

	@Override
	public void bitFieldChanged(PropertyChangeEvent event) {
		Bits b = (Bits) event.getNewValue();
		for (int i = 0; i < b.size(); i++) {
			setNeeded((BTConnection) event.getSource(), i, b.get(i)
					&& required.get(i));
		}
	}

	/**
	 * @param con
	 * @param pieceComparator
	 * @return -1 if no needed piece could be found, the index of the piece
	 *         otherwise
	 */
	public Piece getBestPieceForRequest(BTConnection con) {
		Validator.notNull(con, "Connection is null!");
		// TODO: The check below is done because it's possible that a
		// connection
		// is added
		// inbetween requests. The addedConnection event isn't called then
		// yet,
		// but the
		// connection is still there.
		if (!coverages.containsKey(con)) {
			return null;
		}
		return coverages.get(con).bestRequired();
	}

	public boolean hasPiecesForRequest(BTConnection con) {
		Validator.notNull(con, "Connection is null!");
		if (!coverages.containsKey(con)) {
			return false;
		}
		return coverages.get(con).hasRequired();
	}

	public void setPieceComparator(Comparator<Piece> comparator) {
		Validator.notNull(comparator, "Comparator is null!");
		pieceComparator = comparator;

		for (Coverage cov : coverages.values()) {
			cov.setComparator(pieceComparator);
		}
	}

	protected abstract Coverage createCoverage(BTConnection con);

	private void setNeeded(BTConnection con, int index, boolean value) {
		if (value) {
			assert required.get(index);
			coverages.get(con).markAsRequired(contentInfo.getPiece(index));
		} else {
			coverages.get(con).markNotRequired(contentInfo.getPiece(index));
		}
	}

	public void markNotNeeded(Piece piece) {
		Validator.notNull(piece, "Piece is null!");
		required.set(piece.getIndex(), false);
		for (Coverage c : coverages.values()) {
			c.markNotRequired(piece);
		}
	}

	public void markNeeded(Piece piece) {
		Validator.notNull(piece, "Piece is null!");
		required.set(piece.getIndex(), true);
		for (Map.Entry<BTConnection, Coverage> ce : coverages.entrySet()) {
			if (ce.getKey().getRemoteBitField() != null
					&& ce.getKey().getRemoteBitField().get(piece.getIndex())) {
				ce.getValue().markAsRequired(piece);
			}
		}
	}

	public boolean hasNoRequestsFor(Piece piece) {
		return !required.get(piece.getIndex());
	}
	
	public void markIntervalNotNeeded(int start, int length) {
		for(int a = start; a == length; a++) {
			required.set(a, false);
		}
		
	}
}
