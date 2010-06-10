package org.torrent.internal.transfer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.merapi.helper.handlers.BarUpdateRequestHandler;
import org.merapi.helper.messages.BarUpdateRespondMessage;
import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.io.NBDataReader;
import org.torrent.internal.io.PieceVerifier;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.protocol.message.BTMessageVisitorAdapter;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Have;
import org.torrent.internal.protocol.message.Interested;
import org.torrent.internal.service.ContentStateListener;
import org.torrent.internal.service.event.ContentStateEvent;
import org.torrent.internal.util.BTUtil;
import org.torrent.internal.util.Validator;

public abstract class Uploader extends BTSessionListenerAdapter {
	protected List<UploadSlot> slots = new ArrayList<UploadSlot>();
	private final BTSession session;
	private Timer timer;

   public Uploader(final BTSession session, ContentWatcher watcher,
			int numSlots, NBDataReader reader, TorrentMetaInfo contentInfo) {
		Validator.isTrue(numSlots >= 0, "Invalid number of slots: " + numSlots);
		Validator.nonNull(session, reader, contentInfo);
		this.session = session;
		while (numSlots-- > 0) {
			slots.add(new UploadSlot(session, reader, contentInfo));
		}

		watcher.addContentStateListener(new ContentStateListener() {
			@Override
			public void requiresPiece(ContentStateEvent evt) {
			}

			@Override
			public void verifiedPiece(ContentStateEvent evt) {
				for (BTConnection con : session.getConnections()) {
					if (con.isConnectionEstablished()) {
						con.send(new Have(evt.getPiece().getIndex()), null);
					}
				}
			}

			@Override
			public void receivedPiece(ContentStateEvent evt) {
				System.out.println(evt);
			}

			@Override
			public void stateChanged(ContentStateEvent event) {
				// TODO Auto-generated method stub
				
			}

		});

		timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				BTUtil.invokeLater(new Runnable() {
					@Override
					public void run() {
						changeSlots();
					}
				});
			}
		}, 0, 10000);
	}

	public BTSession getSession() {
		return session;
	}

	protected abstract void nextTargets(Set<BTConnection> dst);

	private void changeSlots() {
		Set<BTConnection> newSlots = new HashSet<BTConnection>();
		nextTargets(newSlots);

		for (UploadSlot slot : slots) {
			BTConnection con = slot.getSlotTarget();

			if (con != null && newSlots.contains(con)) {
				newSlots.remove(con);
			} else {
				slot.setSlotTo(null);
			}
		}
		Iterator<BTConnection> it = newSlots.iterator();
		for (UploadSlot slot : slots) {
			if (!it.hasNext()) {
				break;
			}
			if (slot.getSlotTarget() == null) {
				slot.setSlotTo(it.next());
			}
		}
	}

	@Override
	public void receivedBTMessage(final BTConnection from,
			BittorrentMessage message) {
		message.accept(new BTMessageVisitorAdapter() {
			@Override
			public void visitInterested(Interested interested) {
				for (UploadSlot slot : slots) {
					if (slot.getSlotTarget() == null) {
						slot.setSlotTo(from);
						break;
					}
				}
			}
		});
	}

	@Override
	public void closed() {
		timer.cancel();
	}
}