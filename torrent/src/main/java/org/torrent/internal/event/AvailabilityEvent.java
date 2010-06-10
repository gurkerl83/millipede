package org.torrent.internal.event;

import java.util.EventObject;

import org.torrent.internal.transfer.AvailabilityObserver;
import org.torrent.internal.util.Validator;

public class AvailabilityEvent extends EventObject {

	private final int index;
	private final int value;

	public AvailabilityEvent(AvailabilityObserver availabilityObserver,
			int index, int value) {
		super(availabilityObserver);
		this.value = value;
		Validator.isTrue(index >= 0, "Index out of range: " + index);
		Validator.isTrue(value >= 0, "Value out of range: " + value);
		this.index = index;
	}

	public AvailabilityObserver getAvailabilityObserver() {
		return (AvailabilityObserver) getSource();
	}

	public int getIndex() {
		return index;
	}

	public int getValue() {
		return value;
	}

}
