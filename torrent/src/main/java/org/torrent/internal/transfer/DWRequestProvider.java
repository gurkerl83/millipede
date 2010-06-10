package org.torrent.internal.transfer;

import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.data.TorrentMetaInfo.Piece;
import org.torrent.internal.peer.connection.BTConnection;

public class DWRequestProvider extends AbstractRequestProvider {

	private final DistributionWatcher distWatcher;

	public DWRequestProvider(TorrentMetaInfo contentInfo, ContentWatcher watcher,
			int partLength, int maxRequestsPerConnection,
			DistributionWatcher distWatcher) {
		super(contentInfo, watcher, partLength, maxRequestsPerConnection);
		this.distWatcher = distWatcher;
	}

	public DWRequestProvider(TorrentMetaInfo contentInfo, ContentWatcher watcher,
			int partLength, RequestLimiter limiter,
			DistributionWatcher distWatcher) {
		super(contentInfo, watcher, partLength, limiter);
		this.distWatcher = distWatcher;
	}

	@Override
	protected void completelyRequested(org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		distWatcher.markNotNeeded(piece);
	}

	@Override
	protected org.torrent.internal.data.TorrentMetaInfo.Piece getBestIndex(BTConnection con) {
		return distWatcher.getBestPieceForRequest(con);
	}

	@Override
	protected void notCompletelyRequested(
			org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		distWatcher.markNeeded(piece);
	}

	@Override
	protected boolean hasOpenRequests(BTConnection con) {
		return distWatcher.hasPiecesForRequest(con);
	}

	@Override
	protected boolean hasNoRequiredParts(Piece piece) {
		return distWatcher.hasNoRequestsFor(piece);
	}
}
