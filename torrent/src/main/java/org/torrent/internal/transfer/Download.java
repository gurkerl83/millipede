package org.torrent.internal.transfer;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.merapi.helper.handlers.BarUpdateRequestHandler;
import org.merapi.helper.messages.BarUpdateRespondMessage;
import org.torrent.internal.data.BTPart;
import org.torrent.internal.event.DownloadEvent;
import org.torrent.internal.event.DownloadEventListener;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.protocol.message.BTMessageVisitorAdapter;
import org.torrent.internal.protocol.message.BitField;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Choke;
import org.torrent.internal.protocol.message.Have;
import org.torrent.internal.protocol.message.Piece;
import org.torrent.internal.protocol.message.Request;
import org.torrent.internal.protocol.message.UnChoke;
import org.torrent.internal.service.ContentStateListener;
import org.torrent.internal.service.event.ContentStateEvent;
import org.torrent.internal.util.BTUtil;
import org.torrent.internal.util.Time;
import org.torrent.internal.util.Validator;

public class Download extends TrafficWatcher implements BTDownload { //BTSessionListenerAdapter {
	private static final Logger LOG = Logger
			.getLogger(Download.class.getName());
	private final BTSession session;
	private RequestProvider reqProv;
	private Collection<org.torrent.internal.data.TorrentMetaInfo.Piece> tasks = new LinkedHashSet<org.torrent.internal.data.TorrentMetaInfo.Piece>();
	private Collection<DownloadEventListener> listeners = new CopyOnWriteArrayList<DownloadEventListener>();

	public Download(BTSession session, RequestProvider requestProvider,
			ContentWatcher contentWatcher) {
		Validator.nonNull(session, requestProvider);
		this.session = session;
		reqProv = requestProvider;
		
		contentWatcher.addContentStateListener(new ContentStateListener() {

			@Override
			public void requiresPiece(ContentStateEvent evt) {
				update();
			}

			@Override
			public void verifiedPiece(ContentStateEvent evt) {
				update();
//out to test message overflow
//				BarUpdateRequestHandler.sendUpdateBarData(BarUpdateRespondMessage.UPDATE_BAR_DATA, evt.getPiece().getContentInfo().getInfoHash().asHexString(), evt.getPiece().getIndex(), 1);
			}

			@Override
			public void receivedPiece(ContentStateEvent evt) {
				synchronized (tasks) {
					if (tasks.contains(evt.getPiece())) {
						firePieceDownloaded(evt.getPiece());
					}
				}
//				BarUpdateRequestHandler.getInstance().sendUpdateBarData(BarUpdateRespondMessage.UPDATE_BAR_DATA, evt.getPiece().getContentInfo().getInfoHash().asHexString(), evt.getPiece().getIndex(), 1);
				
			}

			@Override
			public void stateChanged(ContentStateEvent evt) {
				// TODO Auto-generated method stub
//				BarUpdateRequestHandler.getInstance().sendUpdateBarData(BarUpdateRespondMessage.UPDATE_BAR_DATA, evt.getPiece().getContentInfo().getInfoHash().asHexString(), evt.getPiece().getIndex(), 1);
			}

		});
	}

	@Override
	public void addedConnection(BTConnection con) {
		Validator.notNull(con, "Connection is null!");
		update(con);
	}

	private void update(BTConnection con) {
		synchronized (con) {
			if (!con.isConnectionEstablished()) {
				return;
			}

			Time t = new Time();
			if (!reqProv.couldAllocateRequest(this, con)) {
				if (con.getMyPeerStatus().isInterested()
						&& con.getPendingRequests().isEmpty()) {
					con.sendInterested(false);
				}
			} else {
				if (con.getRemotePeerStatus().isChoking()) {
					if (!con.getMyPeerStatus().isInterested()) {
						con.sendInterested(true);
					}
				} else {
					assert reqProv.couldAllocateRequest(this, con);
					BTPart request = reqProv.allocateRequest(this, con);
					assert request != null : reqProv.couldAllocateRequest(this,
							con);
					LOG.finest("Got request " + request + " for " + con);
					do {
						con.send(new Request(request), null);
					} while ((request = reqProv.allocateRequest(this, con)) != null);
					if (t.delta() > 100) {
						LOG.warning("Allocation and sending took " + t.delta()
								+ "ms for " + this);
					}
				}
			}
		}
	}

	@Override
	public void removedConnection(BTConnection con) {
		reqProv.cancelAllRequests(this, con);
		update();
	}

	private void update() {
		for (BTConnection c : session.getConnections()) {
			update(c);
		}
	}

	@Override
	public void receivedBTMessage(final BTConnection from,
			BittorrentMessage message) {
		LOG.finest("Received " + message + " from " + from);
		message.accept(new BTMessageVisitorAdapter() {
			@Override
			public void visitHave(Have have) {
				update(from);
			}

			@Override
			public void visitBitField(BitField bitField) {
				LOG.finest("Received Bitfield: " + bitField);
				update(from);
			}

			@Override
			public void visitUnChoke(UnChoke unChoke) {
				update(from);
			}

			@Override
			public void visitChoke(Choke choke) {
				reqProv.cancelAllRequests(Download.this, from);
				from.purgePendingRequests();
				update();
			}

			@Override
			public void visitPiece(Piece piece) {
				update(from);
			}
		});
	}

	@Override
	public void connectionEstablished(BTConnection connection) {
		update(connection);
	}

	@Override
	public void performDownload(
			org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		
		synchronized (tasks) {
			if (!tasks.contains(piece)) {
					
				tasks.add(piece);
//				BarUpdateRequestHandler.getInstance().sendUpdateBarData(BarUpdateRespondMessage.UPDATE_BAR_DATA, piece.getContentInfo().getInfoHash().asHexString(), piece.getIndex(), 1);
				
				fireDownloadAdded(piece);
				
			}
		}
	}

	private void fireDownloadAdded(
			final org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		BTUtil.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				DownloadEvent evt = new DownloadEvent(Download.this, piece);
				for (DownloadEventListener l : listeners) {
					l.downloadAdded(evt);
				}
//				BarUpdateRequestHandler.getInstance().sendUpdateBarData(BarUpdateRespondMessage.UPDATE_BAR_DATA, piece.getContentInfo().getInfoHash().asHexString(), piece.getIndex(), 1);
//				
			}

		});
	}

	@Override
	public void abortDownload(
			org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		synchronized (tasks) {
			if (tasks.remove(piece)) {
				fireDownloadAborted(piece);
			}
		}
	}

	private void firePieceDownloaded(
			final org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		BTUtil.invokeLater(new Runnable() {

			@Override
			public void run() {
				DownloadEvent evt = new DownloadEvent(Download.this, piece);
				for (DownloadEventListener l : listeners) {
					l.downloadCompleted(evt);
//					BarUpdateRequestHandler.getInstance().sendUpdateBarData(BarUpdateRespondMessage.UPDATE_BAR_DATA, piece.getContentInfo().getInfoHash().asHexString(), piece.getIndex(), 1);
					
				}
			}

		});
	}

	private void fireDownloadAborted(
			final org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		BTUtil.invokeLater(new Runnable() {

			@Override
			public void run() {
				DownloadEvent evt = new DownloadEvent(Download.this, piece);
				for (DownloadEventListener l : listeners) {
					l.downloadAborted(evt);
				}
			}

		});
	}

}
