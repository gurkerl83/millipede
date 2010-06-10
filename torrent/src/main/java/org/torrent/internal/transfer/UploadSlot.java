package org.torrent.internal.transfer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.torrent.internal.data.BTPart;
import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.io.NBDataReader;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.protocol.message.BTMessageVisitorAdapter;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Piece;
import org.torrent.internal.protocol.message.Request;
import org.torrent.internal.util.Validator;

public class UploadSlot extends BTSessionListenerAdapter {
	private static final Logger LOG = Logger.getLogger(UploadSlot.class
			.getName());
	private BTConnection activeUpload;
	private final NBDataReader reader;
	private final TorrentMetaInfo contentInfo;

	public UploadSlot(BTSession session, NBDataReader reader,
			TorrentMetaInfo contentInfo) {
		Validator.nonNull(session, reader, contentInfo);
		this.reader = reader;
		this.contentInfo = contentInfo;
		session.addBTSessionListener(this);
	}

	@Override
	public synchronized void receivedBTMessage(final BTConnection from,
			BittorrentMessage message) {
		if (!from.equals(activeUpload)) {
			return;
		}
		message.accept(new BTMessageVisitorAdapter() {
			@Override
			public void visitRequest(final Request request) {
				send();
			}
		});
	}

	private synchronized void send() {
		BTPart item = activeUpload.takeRemoteRequest();
		if (item == null) {
			return;
		}
		final BTPart info = item;
		final BTConnection target = activeUpload;
		reader.read(ByteBuffer.allocate(info.getLength()), contentInfo
				.getAbsoluteStart(info), new NBDataReader.Callback() {

			@Override
			public void caughtException(IOException e) {
				LOG.info(e.getLocalizedMessage());
			}

			@Override
			public void read(final ByteBuffer dst, long position) {
				dst.flip();

				target.send(new Piece(info, dst), null);
			}

		});
	}

	public synchronized BTConnection getSlotTarget() {
		return activeUpload;
	}

	public synchronized void setSlotTo(BTConnection con) {
		if (activeUpload != null) {
			if (activeUpload.isConnectionEstablished()
					&& !activeUpload.getMyPeerStatus().isChoking()) {
				activeUpload.sendChoked(true);
			}
		}
		activeUpload = con;
		if (activeUpload != null) {
			LOG.finer("Now uploading to " + con);
			if (activeUpload.getMyPeerStatus().isChoking()) {
				activeUpload.sendChoked(false);
			}
			if (activeUpload != null) {
				send();
			}
		}
	}

	@Override
	public synchronized void sentBTMessage(BTConnection from,
			BittorrentMessage message) {
		if (from == activeUpload && from.hasNoPending()) {
			send();
		}
	}
}
