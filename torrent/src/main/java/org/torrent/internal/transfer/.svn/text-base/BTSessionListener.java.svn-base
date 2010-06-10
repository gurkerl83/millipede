package org.torrent.internal.transfer;

import org.torrent.internal.data.BTPart;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Piece;
import org.torrent.internal.service.ContentStateListener;
import org.torrent.internal.transfer.event.BitfieldListener;

public interface BTSessionListener extends BitfieldListener {
	void addedConnection(BTConnection con);

	void removedConnection(BTConnection con);

	void connectionEstablished(BTConnection connection);

	void receivedBTMessage(BTConnection from, BittorrentMessage message);

	void sentBTMessage(BTConnection from, BittorrentMessage message);

	void closed();

	void receivedUnrequestedPiece(BTConnection from, Piece piece);

	void setRequired(org.torrent.internal.data.TorrentMetaInfo.Piece piece);

	void setValidated(org.torrent.internal.data.TorrentMetaInfo.Piece piece);

	boolean isAvailable(BTPart part);

	boolean isRequired(org.torrent.internal.data.TorrentMetaInfo.Piece piece);

	boolean isValidated(org.torrent.internal.data.TorrentMetaInfo.Piece piece);

	//1
	void addContentStateListener(ContentStateListener listener);
	
	void removeContentStateListener(ContentStateListener listener);

	void setAvailable(BTPart part);
}
