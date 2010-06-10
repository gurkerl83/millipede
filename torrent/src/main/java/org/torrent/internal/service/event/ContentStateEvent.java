package org.torrent.internal.service.event;

import java.util.EventObject;

import org.torrent.internal.data.TorrentMetaInfo.Piece;
import org.torrent.internal.service.ContentState;
import org.torrent.internal.service.ContentStateService;
import org.torrent.internal.util.Validator;

public class ContentStateEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Piece piece;
	private final ContentState state;

	public ContentStateEvent(ContentStateService service, Piece piece,
			ContentState newState) {
		super(service);
		Validator.nonNull(service, piece, newState);
		this.state = newState;
		this.piece = piece;
	}

	public Piece getPiece() {
		return piece;
	}

	public ContentState getState() {
		return state;
	}

}
