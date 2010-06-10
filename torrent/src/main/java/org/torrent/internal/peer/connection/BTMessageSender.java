package org.torrent.internal.peer.connection;

import java.io.IOException;

import org.torrent.internal.protocol.message.BittorrentMessage;

public interface BTMessageSender {
	boolean hasNoPending();

	/**
	 * Requests a message to be sent and returns immediately. The callback will
	 * be notified on successful transfer or in case of an error.
	 * 
	 * @param msg
	 *            the message to be sent.
	 * @param callback
	 *            if null, nothing happens, otherwise the callback to be
	 *            notified.
	 */
	void send(BittorrentMessage msg, BTMessageSenderCallback callback);

	void close() throws IOException;
}
