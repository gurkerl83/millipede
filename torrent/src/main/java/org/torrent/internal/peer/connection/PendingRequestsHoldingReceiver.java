package org.torrent.internal.peer.connection;

import org.torrent.internal.protocol.message.BittorrentMessage;

public class PendingRequestsHoldingReceiver extends
		AbstractPendingRequestsHolder implements BTMessageReceiver {

	@Override
	public void received(BittorrentMessage message) {
		handleMessage(message);
	}

}
