package org.torrent.internal.service;

import org.torrent.internal.data.TorrentMetaInfo.Piece;

public interface ContentStateService {
	void addContentStateListener(ContentStateListener listener);

	void removeContentStateListener(ContentStateListener listener);

	void setValidated(Piece piece);

	void setRequired(Piece piece);

	void setAvailable(Piece piece);

	boolean isAvailable(Piece piece);

	/**
	 * Returns true if any part of the given piece is still required.
	 * 
	 * @param piece
	 * @return
	 */
	boolean isRequired(Piece piece);

	boolean isValidated(Piece piece);
}
