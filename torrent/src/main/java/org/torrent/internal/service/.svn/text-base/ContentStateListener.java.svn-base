package org.torrent.internal.service;

import org.torrent.internal.service.event.ContentStateEvent;

public interface ContentStateListener {
	void stateChanged(ContentStateEvent event);

	void receivedPiece(ContentStateEvent evt);

	void requiresPiece(ContentStateEvent evt);

	void verifiedPiece(ContentStateEvent evt);
}
