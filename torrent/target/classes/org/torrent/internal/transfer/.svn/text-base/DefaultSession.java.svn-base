package org.torrent.internal.transfer;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.merapi.helper.handlers.BarUpdateRequestHandler;
import org.merapi.helper.messages.BarUpdateRespondMessage;
import org.torrent.internal.data.BTPart;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Piece;
import org.torrent.internal.service.ContentStateListener;
import org.torrent.internal.util.BTUtil;
import org.torrent.internal.util.Time;
import org.torrent.internal.util.Validator;

public class DefaultSession implements BTSession {

	protected static final Logger LOG = Logger.getLogger(DefaultSession.class
			.getName());

	private Collection<BTConnection> connections = new HashSet<BTConnection>();

	private List<BTSessionListener> listeners = new CopyOnWriteArrayList<BTSessionListener>();

	public DefaultSession() {
	}

	@Override
	public synchronized void addConnection(BTConnection connection) {
		Validator.notNull(connection, "Connection is null!");
		if (connections.contains(connection)) {
			throw new IllegalStateException("Already added connection: "
					+ connection);
		}
		connections.add(connection);
		fireAddedConnection(connection);
	}

	@Override
	public synchronized void removeConnection(BTConnection connection) {
		Validator.notNull(connection, "Connection is null!");
		if (connections.remove(connection)) {
			fireRemovedConnection(connection);
		}
	}

	private void fireRemovedConnection(final BTConnection connection) {
		BTUtil.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (BTSessionListener l : listeners) {
					l.removedConnection(connection);
				}
			}

		});
	}

	private void fireAddedConnection(final BTConnection connection) {
		BTUtil.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (BTSessionListener l : listeners) {
					l.addedConnection(connection);
				}
			}

		});
	}

	@Override
	public synchronized void received(BTConnection from,
			BittorrentMessage message) {
		fireMessageReceived(from, message);
	}

	private void fireMessageReceived(final BTConnection from,
			final BittorrentMessage message) {
		BTUtil.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (BTSessionListener l : listeners) {
					Time t = new Time();
					l.receivedBTMessage(from, message);
					if (t.delta() > 100) {
						LOG.warning("Listener " + l + " took " + t.delta()
								+ "ms to handle " + message);
					}
				}
			}

		});
	}

	/**
	 * Returns the connections, currently managed by this session. This method
	 * is potentially dangerous. If you're accessing it in the event loop, keep
	 * in mind that it's possible for connections to be added/removed with the
	 * event still yet to be dispatched.
	 * 
	 * @return the current managed connections
	 */
	@Override
	public synchronized Collection<BTConnection> getConnections() {
		return new ArrayList<BTConnection>(connections);
	}

	@Override
	public void sent(BTConnection from, BittorrentMessage msg) {
		fireMessageSent(from, msg);
	}

	private void fireMessageSent(final BTConnection from,
			final BittorrentMessage msg) {
		BTUtil.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (BTSessionListener l : listeners) {
					l.sentBTMessage(from, msg);
				}
			}

		});
	}

	// @Override
	// public List<BTSessionListener> getListenerChain() {
	// return listenerChain;
	// }

	@Override
	public void connectionEstablished(BTConnection connection) {
		fireConnectionEstablished(connection);
	}

	private void fireConnectionEstablished(final BTConnection connection) {
		BTUtil.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (BTSessionListener l : listeners) {
					l.connectionEstablished(connection);
				}
			}

		});
	}

	@Override
	public void bitFieldBitChanged(IndexedPropertyChangeEvent event) {
		fireBitFieldChanged(event);
	}

	private void fireBitFieldChanged(final IndexedPropertyChangeEvent event) {
		BTUtil.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (BTSessionListener l : listeners) {
					l.bitFieldBitChanged(event);
				}
			}

		});
	}

	@Override
	public void bitFieldChanged(PropertyChangeEvent event) {
		fireBitFieldChanged(event);
	}

	private void fireBitFieldChanged(final PropertyChangeEvent event) {
		BTUtil.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (BTSessionListener l : listeners) {
					l.bitFieldChanged(event);
				}
			}

		});
	}

	@Override
	public void close() {
		fireSessionClosed();
	}

	private void fireSessionClosed() {
		BTUtil.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (BTSessionListener l : listeners) {
					l.closed();
				}
			}

		});
	}

	@Override
	public void unrequestedPiece(BTConnection connection, Piece piece) {
		fireUnrequestedPiece(connection, piece);
	}

	private void fireUnrequestedPiece(final BTConnection connection,
			final Piece piece) {
		BTUtil.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (BTSessionListener l : listeners) {
					l.receivedUnrequestedPiece(connection, piece);
				}
			}

		});
	}

	@Override
	public void addBTSessionListener(BTSessionListener listener) {
		Validator.notNull(listener, "Listener is null!");
		listeners.add(listener);
	}

	@Override
	public boolean isAvailable(BTPart part) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequired(org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValidated(org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeContentStateListener(ContentStateListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAvailable(BTPart part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRequired(org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValidated(org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<BTSessionListener> getListenerChain() {
		// TODO Auto-generated method stub
		return null;
	}

	//1
	@Override
	public void addContentStateListener(ContentStateListener listener) {
		// TODO Auto-generated method stub
		
	}
}
