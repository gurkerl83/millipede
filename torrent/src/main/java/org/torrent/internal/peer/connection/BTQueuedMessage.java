package org.torrent.internal.peer.connection;

import org.torrent.internal.protocol.message.BittorrentMessage;

public interface BTQueuedMessage {

	BittorrentMessage getBittorrentMessage();

}
