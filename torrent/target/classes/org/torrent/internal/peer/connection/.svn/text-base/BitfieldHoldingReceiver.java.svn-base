package org.torrent.internal.peer.connection;

import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.transfer.event.BitfieldListener;
import org.torrent.internal.util.Bits;
import org.torrent.internal.util.Validator;

public class BitfieldHoldingReceiver extends AbstractBitfieldHolder implements
		BTMessageReceiver {

	private final BTMessageReceiver receiver;

	public BitfieldHoldingReceiver(BTMessageReceiver receiver,
			Bits initialBitfield, BitfieldListener callback) {
		super(initialBitfield, callback);
		Validator.notNull(receiver, "Receiver is null!");
		this.receiver = receiver;
	}

	@Override
	public void received(BittorrentMessage message) {
		handleMessage(message);
		receiver.received(message);
	}
}
