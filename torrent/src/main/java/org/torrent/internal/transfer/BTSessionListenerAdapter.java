package org.torrent.internal.transfer;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;

import org.torrent.internal.data.BTPart;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Piece;
import org.torrent.internal.service.ContentStateListener;

public class BTSessionListenerAdapter implements BTSessionListener {

	@Override
	public void addedConnection(BTConnection con) {
	}

	@Override
	public void connectionEstablished(BTConnection connection) {
	}

	@Override
	public void receivedBTMessage(BTConnection from, BittorrentMessage message) {
	}

	@Override
	public void removedConnection(BTConnection con) {
	}

	@Override
	public void sentBTMessage(BTConnection from, BittorrentMessage message) {
	}

	@Override
	public void bitFieldBitChanged(IndexedPropertyChangeEvent event) {
	}

	@Override
	public void bitFieldChanged(PropertyChangeEvent event) {
	}

	@Override
	public void closed() {
	}

	@Override
	public void receivedUnrequestedPiece(BTConnection from, Piece piece) {
	}

	public void setRequired(org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		// TODO Auto-generated method stub
		return;
	}

	public void setValidated(org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		// TODO Auto-generated method stub
		return;
	}

	public boolean isAvailable(BTPart part) {
		// TODO Auto-generated method stub
		return false;
//		return true;
	}

	public boolean isRequired(org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		// TODO Auto-generated method stub
		return false;
//		return true;
	}

	public boolean isValidated(org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
		// TODO Auto-generated method stub
		return false;
//		return true;
	}

	public void removeContentStateListener(ContentStateListener listener) {
		// TODO Auto-generated method stub
		return;
	}

	public void setAvailable(BTPart part) {
		// TODO Auto-generated method stub
		return;
	}

	//1
	@Override
	public void addContentStateListener(ContentStateListener listener) {
		// TODO Auto-generated method stub
		
	}

}
