package org.torrent.internal.util;

import java.util.Iterator;
import java.util.List;

public interface SparseArray<T> extends
		Iterable<org.torrent.internal.util.SparseArray.Entry<T>> {
	public interface Entry<T> {
		Range getRange();

		T getValue();
	}

	/**
	 * Searches a range for a value.
	 * 
	 * @param within
	 * @param value
	 * @return the intersection of the given range with the found range, or null
	 *         if none was found.
	 */
	Range findFirst(Range within, T value);

	/**
	 * Searches the array for the first occurrence of the given value.
	 * 
	 * @param value
	 * @return the range, or null if none could be found
	 */
	Range findFirst(T value);

	List<Entry<T>> getValues(Range in);

	List<Entry<T>> getValues();

	Iterator<Entry<T>> iterator(Range r);

	T get(long pos);

	void set(long pos, T value);

	/**
	 * Sets values within a range to a given value.
	 * 
	 * @param at
	 * @param value
	 */
	void put(Range at, T value);

	/**
	 * Counts the number of occurrences of a value within a range.
	 * 
	 * @param within
	 * @param value
	 * @return
	 */
	long count(Range within, T value);

	/**
	 * * Returns the range of the whole array.
	 * 
	 * @return
	 */
	Range getRange();
}
