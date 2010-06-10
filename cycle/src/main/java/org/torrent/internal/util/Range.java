package org.torrent.internal.util;

import java.io.Serializable;

/**
 * This class represents an interval.
 * 
 * @author dante
 * 
 */
public final class Range implements Serializable {
	private static final long serialVersionUID = 1L;

	private final long start, length;

	/**
	 * Creates a range with the given parameters
	 * 
	 * @param start
	 *            the beginning of the range
	 * @param length
	 *            the length of the range
	 * @return a range
	 */
	public static Range getRangeByLength(long start, long length) {
		Validator.isTrue(length >= 0, "Invalid length: " + length);
		return new Range(start, length);
	}

	/**
	 * Creates the smallest possible range containing the given numbers.
	 * 
	 * @param number1
	 *            number to be within the range
	 * @param number2
	 *            number to be within the range
	 * @return
	 */
	public static Range getRangeByNumbers(long number1, long number2) {
		long s = Math.min(number1, number2);
		return new Range(s, Math.max(number1, number2) - s + 1);
	}

	private Range(long start, long length) {
		assert length >= 0;

		this.start = start;
		this.length = length;
	}

	/**
	 * @param range
	 * @return true if the given range is contained within this range
	 */
	public boolean contains(Range range) {
		return getStart() <= range.getStart() && getEnd() >= range.getEnd();
	}

	/**
	 * @param pos
	 * @return true if the given point is contained within this range
	 */
	public boolean contains(long pos) {
		return getStart() <= pos && getEnd() >= pos;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Range) {
			Range r = (Range) obj;
			return getStart() == r.getStart() && getLength() == r.getLength();
		}
		return false;
	}

	/**
	 * @return the last index contained in this range
	 */
	public long getEnd() {
		return start + length - 1;
	}

	/**
	 * @return the length of this range
	 */
	public long getLength() {
		return length;
	}

	/**
	 * @return the first index contained in this range
	 */
	public long getStart() {
		return start;
	}

	@Override
	public int hashCode() {
		return (int) ((getStart() * 13) & (getEnd() * 137));
	}

	/**
	 * Creates a range which contains only the indices contained in the
	 * intersection of this range and the given range.
	 * 
	 * @param range
	 *            the range to intersect with
	 * @return the intersected range or null if the ranges don't overlap
	 */
	public Range intersection(Range range) {
		if (!intersects(range)) {
			return null;
		}
		return getRangeByNumbers(Math.max(getStart(), range.getStart()), Math
				.min(getEnd(), range.getEnd()));
	}

	/**
	 * Returns the number of indices which are in this range and the given
	 * range.
	 * 
	 * @param r
	 * @return 0 if the ranges don't overlap, the length of the intersection
	 *         between them otherwise
	 */
	public long intersectionLength(Range r) {
		if (!intersects(r)) {
			return 0;
		}
		return Math.min(getEnd(), r.getEnd())
				- Math.max(getStart(), r.getStart()) + 1;
	}

	/**
	 * @param range
	 *            the range to intersect test with
	 * @return true if the ranges overlap
	 */
	public boolean intersects(Range range) {
		return getStart() <= range.getEnd() && getEnd() >= range.getStart();
	}

	@Override
	public String toString() {
		return "[" + getStart() + " - " + getEnd() + "]";
	}
}
