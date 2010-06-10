package org.torrent.internal.peer.connection;

import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.util.Validator;

/**
 * Receiver proxy that synchronizes on a {@link BTMessageReceiver} object.
 * 
 * @author dante
 * 
 */
public class SynchronizingReceiver implements BTMessageReceiver {

	private final BTMessageReceiver receiver;

	public SynchronizingReceiver(BTMessageReceiver receiver) {
		Validator.notNull(receiver, "Receiver is null!");
		this.receiver = receiver;
	}

	@Override
	public void received(BittorrentMessage message) {
		synchronized (receiver) {
			receiver.received(message);
		}
	}

}
