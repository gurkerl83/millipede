package ch.cyberduck.core.service;

import ch.cyberduck.core.Transfer;

public interface TransferCollectionService {

	public abstract boolean add(Transfer o);

	/**
	 * Saves the collection after adding the new item
	 *
	 * @param row
	 * @param o
	 * @see #save()
	 */
	public abstract void add(int row, Transfer o);

	/**
	 * Does not save the collection after modifiying
	 *
	 * @param row
	 * @return the element that was removed from the list.
	 * @see #save()
	 */
	public abstract Transfer remove(int row);

	public abstract void save();

	public abstract void load();

	/**
	 * @return
	 */
	public abstract int numberOfRunningTransfers();

	/**
	 * @return
	 */
	public abstract int numberOfQueuedTransfers();

}