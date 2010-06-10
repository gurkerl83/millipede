package org.torrent.internal.transfer;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.data.TorrentMetaInfo.Piece;
import org.torrent.internal.event.AvailabilityEvent;
import org.torrent.internal.event.AvailabilityListener;
import org.torrent.internal.util.Bits;
import org.torrent.internal.util.Validator;

public class AvailabilityObserver extends BTSessionListenerAdapter {
	private final int[] availability;
	private List<AvailabilityListener> listeners = new CopyOnWriteArrayList<AvailabilityListener>();

	public AvailabilityObserver(TorrentMetaInfo content) {
		Validator.nonNull(content);

		availability = new int[content.getPiecesCount()];
	}

	@Override
	public synchronized void bitFieldBitChanged(IndexedPropertyChangeEvent event) {
		if (!event.getOldValue().equals(event.getNewValue())) {
			if ((Boolean) event.getNewValue()) {
				increaseAvailability(event.getIndex());
			} else {
				decreaseAvailability(event.getIndex());
			}
		}

	}

	@Override
	public synchronized void bitFieldChanged(PropertyChangeEvent event) {
		Bits oldValue = (Bits) event.getOldValue();
		Bits newValue = (Bits) event.getNewValue();
		for (int i = 0; i < availability.length; i++) {
			if (oldValue.get(i) != newValue.get(i)) {
				if (newValue.get(i)) {
					increaseAvailability(i);
				} else {
					decreaseAvailability(i);
				}
			}
		}
	}

	private void decreaseAvailability(int i) {
		int value = --availability[i];
		AvailabilityEvent evt = new AvailabilityEvent(this, i, value);
		for (AvailabilityListener l : listeners) {
			l.decreasedAvailability(evt);
		}
	}

	private void increaseAvailability(int i) {
		int value = ++availability[i];
		AvailabilityEvent evt = new AvailabilityEvent(this, i, value);
		for (AvailabilityListener l : listeners) {
			l.increasedAvailability(evt);
		}
	}

	public synchronized int getAvailability(Piece piece) {
		
		return availability[piece.getIndex()];
	}
}
