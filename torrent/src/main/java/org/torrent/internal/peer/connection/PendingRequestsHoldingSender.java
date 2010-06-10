package org.torrent.internal.peer.connection;

import java.io.IOException;

import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.util.Validator;

public class PendingRequestsHoldingSender extends AbstractPendingRequestsHolder
		implements BTMessageSender {

	private final BTMessageSender sender;

	public PendingRequestsHoldingSender(BTMessageSender sender) {
		Validator.notNull(sender, "Sender is null!");
		this.sender = sender;
	}

	@Override
	public void send(BittorrentMessage msg, BTMessageSenderCallback callback) {
		handleMessage(msg);
		sender.send(msg, callback);
	}

	@Override
	public void close() throws IOException {
		sender.close();
	}

	@Override
	public boolean hasNoPending() {
		return sender.hasNoPending();
	}

}
