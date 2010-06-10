package org.torrent.internal.protocol;

import java.io.IOException;

import org.torrent.internal.protocol.message.BittorrentMessage;

public interface BTMessageReceiver {
	void received(BittorrentMessage message) throws IOException;
}
