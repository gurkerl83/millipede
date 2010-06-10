package org.torrent.internal.event;

import java.util.EventObject;

import org.torrent.internal.data.TorrentMetaInfo.Piece;
import org.torrent.internal.transfer.BTDownload;

public class DownloadEvent extends EventObject {

	private final Piece piece;

	public DownloadEvent(BTDownload download, Piece piece) {
		super(download);
		this.piece = piece;
	}

	public BTDownload getDownload() {
		return (BTDownload) getSource();
	}

	public Piece getPiece() {
		return piece;
	}
}
