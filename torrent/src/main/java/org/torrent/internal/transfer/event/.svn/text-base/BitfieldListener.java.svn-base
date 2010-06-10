package org.torrent.internal.transfer.event;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;

import org.torrent.internal.data.BTPart;
import org.torrent.internal.data.TorrentMetaInfo.Piece;
import org.torrent.internal.service.ContentStateListener;

public interface BitfieldListener {
	/**
	 * Invoked if a single bit of a bit field might have been modified. To check
	 * if the bit really has been modified use the ChangeEvent's getter methods.
	 * 
	 * @param event
	 */
	void bitFieldBitChanged(IndexedPropertyChangeEvent event);

	void bitFieldChanged(PropertyChangeEvent event);

	void setRequired(Piece piece);

	void setValidated(Piece piece);

	boolean isAvailable(BTPart part);

	boolean isRequired(Piece piece);

	boolean isValidated(Piece piece);

	//1
	void addContentStateListener(ContentStateListener listener);

	void removeContentStateListener(ContentStateListener listener);

	void setAvailable(BTPart part);
	
}
