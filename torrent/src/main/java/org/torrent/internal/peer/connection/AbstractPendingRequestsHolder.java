package org.torrent.internal.peer.connection;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.torrent.internal.data.BTPart;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Cancel;
import org.torrent.internal.protocol.message.Request;

public class AbstractPendingRequestsHolder implements BTPendingRequestsHolder {
	private Queue<BTPart> pendingRequests = new ConcurrentLinkedQueue<BTPart>();

	private void enqueuePendingRequest(BTPart part) {
		pendingRequests.add(part);
	}

	@Override
	public BTPart takePendingRequest() {
		return pendingRequests.poll();
	}

	@Override
	public void clearPendingRequests() {
		pendingRequests.clear();
	}

	protected void handleMessage(BittorrentMessage message) {
		if (message instanceof Request) {
			enqueuePendingRequest(((Request) message).getPieceInfo());
		} else if (message instanceof Cancel) {
			dequeuePendingRequest(((Cancel) message).getPieceInfo());
		}
	}

	@Override
	public void dequeuePendingRequest(BTPart pieceInfo) {
		pendingRequests.remove(pieceInfo);
	}

	@Override
	public Collection<BTPart> getPendingRequests() {
		return pendingRequests;
	}
}
