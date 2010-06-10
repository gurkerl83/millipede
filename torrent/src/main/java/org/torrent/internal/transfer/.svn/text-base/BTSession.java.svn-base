package org.torrent.internal.transfer;

import java.util.Collection;
import java.util.List;

import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Piece;
import org.torrent.internal.transfer.event.BitfieldListener;

public interface BTSession extends BitfieldListener {
	void addConnection(BTConnection connection);

	void removeConnection(BTConnection connection);

	Collection<BTConnection> getConnections();

	void sent(BTConnection from, BittorrentMessage msg);

	void received(BTConnection from, BittorrentMessage msg);

	void connectionEstablished(BTConnection connection);

	List<BTSessionListener> getListenerChain();

	void close();

	void unrequestedPiece(BTConnection connection, Piece piece);

	void addBTSessionListener(BTSessionListener listener);
}
