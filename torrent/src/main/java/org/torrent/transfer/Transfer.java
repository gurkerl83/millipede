package org.torrent.transfer;

import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.io.PieceVerifier;
import org.torrent.internal.transfer.AvailabilityObserver;
import org.torrent.internal.transfer.BTSession;
import org.torrent.internal.transfer.ContentWatcher;
import org.torrent.internal.transfer.TrafficWatcher;
import org.gudy.azureus2.plugins.download.Download;
//import org.torrent.internal.transfer.Download;

public interface Transfer extends Download {

	ContentWatcher getContentWatcher();

	BTSession getSession();

	TorrentMetaInfo getContentInfo();

	PieceVerifier getPieceVerifier();

	public TrafficWatcher getTrafficWatcher();

	AvailabilityObserver getAvailabilityObserver();

	void checkAllPieces();

}
