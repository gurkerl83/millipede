package org.torrent.internal.transfer;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.data.TorrentMetaInfo.Piece;
import org.torrent.internal.peer.connection.BTConnection;

public class DefaultDistributionWatcher extends DistributionWatcher {
	public DefaultDistributionWatcher(TorrentMetaInfo contentInfo) {
		super(contentInfo);
	}

	@Override
	protected Coverage createCoverage(BTConnection con) {
		return new Coverage() {
			// Prevent piece request jumping
			private Piece lastRequired;
			Set<Piece> set = new LinkedHashSet<Piece>();
			private Comparator<Piece> pieceComparator;

			@Override
			public Piece bestRequired() {
				if (set.isEmpty()) {
					return null;
				}
				if (lastRequired != null && set.contains(lastRequired)) {
					return lastRequired;
				}
				return lastRequired = Collections.min(set, pieceComparator);
			}

			@Override
			public void setComparator(Comparator<Piece> comparator) {
				assert comparator != null;
				pieceComparator = comparator;
			}

			@Override
			public void markAsRequired(Piece piece) {
				set.add(piece);
			}

			@Override
			public void markNotRequired(Piece piece) {
				set.remove(piece);
			}

			@Override
			public boolean hasRequired() {
				return !set.isEmpty();
			}
		};
	}
}
