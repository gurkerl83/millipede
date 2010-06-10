package org.torrent.internal.event;

public interface AvailabilityListener {

	void decreasedAvailability(AvailabilityEvent evt);

	void increasedAvailability(AvailabilityEvent evt);

}
