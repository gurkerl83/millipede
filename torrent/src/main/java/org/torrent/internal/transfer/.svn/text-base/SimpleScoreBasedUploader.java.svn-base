package org.torrent.internal.transfer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.logging.Logger;

import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.io.NBDataReader;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.util.Validator;

public class SimpleScoreBasedUploader extends Uploader {

	private static final Logger LOG = Logger
			.getLogger(SimpleScoreBasedUploader.class.getName());
	private final SimpleScoreKeeper scorer;

	public SimpleScoreBasedUploader(BTSession session, ContentWatcher watcher,
			SimpleScoreKeeper scorer, NBDataReader reader,
			TorrentMetaInfo contentInfo, int numSlots) {
		super(session, watcher, numSlots, reader, contentInfo);
		Validator.nonNull(scorer);
		this.scorer = scorer;
	}

	@Override
	protected void nextTargets(Set<BTConnection> dst) {
		assert dst != null;
		BTConnection[] con = getSession().getConnections().toArray(
				new BTConnection[0]);
		Arrays.sort(con, new Comparator<BTConnection>() {

			@Override
			public int compare(BTConnection o1, BTConnection o2) {
				Long s1 = scorer.getScoreOf(o1);
				Long s2 = scorer.getScoreOf(o2);
				if (s1 == null && s2 == null) {
					return o1.hashCode() - o2.hashCode();
				}
				if (s1 == null) {
					return 1;
				}
				if (s2 == null) {
					return -1;
				}
				if (s1.equals(s2)) {
					return 0;
				}
				return s1 > s2 ? -1 : 1;
			}
		});

		for (BTConnection c : con) {
			if (c.getRemotePeerStatus().isInterested()) {
				LOG.finest(dst.size() + " / " + slots.size() + "Chosen peer: "
						+ c + " with score " + scorer.getScoreOf(c));
				dst.add(c);
				if (dst.size() >= slots.size()) {
					break;
				}
			}
		}
	}

	@Override
	public void addedConnection(BTConnection con) {
	}

	@Override
	public void removedConnection(BTConnection con) {
	}

	@Override
	public void sentBTMessage(BTConnection from, BittorrentMessage message) {
	}

	@Override
	public void connectionEstablished(BTConnection connection) {
	}
}
