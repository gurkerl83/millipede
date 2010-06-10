package org.torrent.internal.peer.connection;

import java.util.LinkedList;
import java.util.Queue;

import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.util.Validator;

public class BufferedReceiver implements BTMessageReceiver {
	private final BTMessageReceiver receiver;
	private Queue<BittorrentMessage> messageQueue;

	public BufferedReceiver(BTMessageReceiver receiver) {
		Validator.notNull(receiver, "Receiver is null!");
		this.receiver = receiver;
	}

	@Override
	public void received(BittorrentMessage message) {
		if (messageQueue == null) {
			receiver.received(message);
		} else {
			messageQueue.add(message);
		}
	}

	public void setEnableBuffering(boolean value) {
		if (value) {
			if (messageQueue == null) {
				messageQueue = new LinkedList<BittorrentMessage>();
			}
		} else {
			messageQueue = null;
		}
	}

	public void flush() {
		for (BittorrentMessage m : messageQueue) {
			receiver.received(m);
		}
		messageQueue.clear();
	}
}
