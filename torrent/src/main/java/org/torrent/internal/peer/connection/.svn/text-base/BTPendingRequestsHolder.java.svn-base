package org.torrent.internal.peer.connection;

import java.util.Collection;

import org.torrent.internal.data.BTPart;

public interface BTPendingRequestsHolder {

	BTPart takePendingRequest();

	void clearPendingRequests();

	Collection<BTPart> getPendingRequests();

	void dequeuePendingRequest(BTPart pieceInfo);
}
