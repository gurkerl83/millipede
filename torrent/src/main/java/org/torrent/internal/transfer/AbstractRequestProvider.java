package org.torrent.internal.transfer;

import org.torrent.internal.data.BTPart;
import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.data.TorrentMetaInfo.Piece;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.service.ContentStateListener;
import org.torrent.internal.service.event.ContentStateEvent;
import org.torrent.internal.util.Bits;
import org.torrent.internal.util.Validator;

public abstract class AbstractRequestProvider implements RequestProvider {
	private Bits[] pieces;

	private final int partLength;
	private final RequestLimiter limiter;
	private final TorrentMetaInfo contentInfo;

	public AbstractRequestProvider(TorrentMetaInfo contentInfo,
			ContentWatcher watcher, int partLength,
			final int maxRequestsPerConnection) {
		this(contentInfo, watcher, partLength, new RequestLimiter() {

			@Override
			public boolean allowRequestFor(BTConnection con) {
				return con.getPendingRequests().size() < maxRequestsPerConnection;
			}

		});
		Validator.isTrue(maxRequestsPerConnection >= 0,
				"Invalid max requests: " + maxRequestsPerConnection);
	}

	public AbstractRequestProvider(TorrentMetaInfo contentInfo,
			ContentWatcher watcher, int partLength, RequestLimiter limiter) {
		Validator.isTrue(partLength > 0, "Invalid part length: " + partLength);
		Validator.notNull(limiter, "RequestLimiter is null!");
		Validator.nonNull(contentInfo);

		this.contentInfo = contentInfo;
		this.partLength = partLength;
		this.limiter = limiter;

		pieces = new Bits[contentInfo.getPiecesCount()];
		for (Piece p : contentInfo) {
			pieces[p.getIndex()] = createBits(p);
		}

		watcher.addContentStateListener(new ContentStateListener() {

			@Override
			public void receivedPiece(ContentStateEvent evt) {
				assert hasNoRequiredParts(evt.getPiece());
				pieces[evt.getPiece().getIndex()] = null;
				System.out.println(evt);
			}

			@Override
			public void requiresPiece(ContentStateEvent evt) {
				Piece pi = evt.getPiece();
				Bits b = pieces[pi.getIndex()];
				if (b == null) {
					b = pieces[pi.getIndex()] = createBits(AbstractRequestProvider.this.contentInfo
							.getPiece(pi.getIndex()));
					for (int i = 0; i < b.size(); i++) {
						b.set(i, true);
					}
				}
				for (int i = 0; i < b.size(); i++) {
					if (pi.asBTPart().getRange().contains(
							i * AbstractRequestProvider.this.partLength)) {
						b.set(i, false);
					}
				}
				assert b.firstIndexOf(false) >= 0 : b + " on " + pi;
				notCompletelyRequested(AbstractRequestProvider.this.contentInfo
						.getPiece(pi.getIndex()));
				System.out.println(evt);
			}

			@Override
			public void verifiedPiece(ContentStateEvent evt) {
				pieces[evt.getPiece().getIndex()] = null;
				System.out.println(evt);
			}

			@Override
			public void stateChanged(ContentStateEvent event) {
				// TODO Auto-generated method stub
				System.out.println(event);
			}

		});
	}

	protected abstract boolean hasNoRequiredParts(Piece piece);

	@Override
	public boolean couldAllocateRequest(Download download, BTConnection con) {
		if (!limiter.allowRequestFor(con)) {
			return false;
		}
		return hasOpenRequests(con);
	}

	@Override
	public BTPart allocateRequest(Download download, BTConnection con) {
		if (!limiter.allowRequestFor(con)) {
			return null;
		}

		org.torrent.internal.data.TorrentMetaInfo.Piece best = null;

		for (int i = 0; i < contentInfo.getPiecesCount(); i++) {
			Bits parts = pieces[i];
			if (parts != null && con.getRemoteBitField().get(i)
					&& parts.firstIndexOf(true) >= 0
					&& parts.firstIndexOf(false) >= 0) {
				best = contentInfo.getPiece(i);
				break;
			}
		}
		if (best == null) {
			best = getBestIndex(con);
		}
		if (best == null) {
			return null;
		}

		assert con.getRemoteBitField().get(best.getIndex());

		Bits parts = pieces[best.getIndex()];
		assert parts != null;

		int req = parts.firstIndexOf(false);
		parts.set(req, true);
		BTPart result = new BTPart(best.getIndex(), req * partLength, Math.min(
				best.getLength() - req * partLength, partLength));
		if (parts.firstIndexOf(false) < 0) {
			completelyRequested(best);
		}
		assert result.getLength() <= partLength;
		return result;
	}

	private Bits createBits(org.torrent.internal.data.TorrentMetaInfo.Piece forPiece) {
		return new Bits((forPiece.getLength() + partLength - 1) / partLength);
	}

	/**
	 * Mark the given piece as completely requested
	 * 
	 * @param best
	 */
	protected abstract void completelyRequested(
			org.torrent.internal.data.TorrentMetaInfo.Piece best);

	protected abstract org.torrent.internal.data.TorrentMetaInfo.Piece getBestIndex(
			BTConnection con);

	protected abstract boolean hasOpenRequests(BTConnection con);

	@Override
	public void cancelAllRequests(Download download, BTConnection con) {
		for (BTPart pi : con.getPendingRequests()) {
			cancelRequest(download, con, pi);
		}
	}

	@Override
	public void cancelRequest(Download download, BTConnection con, BTPart part) {
		TorrentMetaInfo.Piece piece = contentInfo.getPiece(part.getIndex());
		Bits parts = pieces[piece.getIndex()];

		parts.set(part.getStart() / partLength, false);
		assert parts.firstIndexOf(false) >= 0;
		notCompletelyRequested(piece);
	}

	protected abstract void notCompletelyRequested(TorrentMetaInfo.Piece piece);

	public TorrentMetaInfo getContentInfo() {
		return contentInfo;
	}

}
