package org.torrent.internal.transfer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Piece;

public class TrafficWatcher extends BTSessionListenerAdapter {

	private volatile long downloaded;
	private volatile long uploaded;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public void addedConnection(BTConnection con) {
	}

	@Override
	public void receivedBTMessage(BTConnection from, BittorrentMessage message) {
		if (message instanceof Piece) {
			long old = downloaded;
			downloaded += ((Piece) message).getLength();
			pcs.firePropertyChange("Downloaded", old, downloaded);
		}
	}

	@Override
	public void removedConnection(BTConnection con) {
	}

	@Override
	public void sentBTMessage(BTConnection from, BittorrentMessage message) {
		if (message instanceof Piece) {
			long old = uploaded;
			uploaded += ((Piece) message).getLength();
			pcs.firePropertyChange("Uploaded", old, uploaded);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public long getDownloaded() {
		return downloaded;
	}

	public long getUploaded() {
		return uploaded;
	}
}
