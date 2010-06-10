package org.torrent.internal.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Helper class representing a set of ranges containing values. This class
 * provides the functionality to store and find ranges containing the same data.
 * 
 * @author dante
 * 
 */
public class Partitions<T> implements SparseArray<T> {
	private Partitions<T> parent, a, b;

	private Range range;
	private T content;

	private class MyEntry implements Entry<T> {
		private T value;
		private Range range;

		public MyEntry(Range range, T value) {
			super();
			this.range = range;
			this.value = value;
		}

		@Override
		public Range getRange() {
			return range;
		}

		@Override
		public T getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "{Range: " + range + ", value: " + value + "}";
		}
	}

	/**
	 * Creates a new Partition with the given range containing the given value.
	 * 
	 * @param r
	 * @param base
	 */
	public Partitions(Range r, T base) {
		Validator.notNull(r, "Range is null!");
		range = r;
		content = base;
	}

	private Partitions(Partitions<T> p, Range range, T val) {
		parent = p;
		this.range = range;
		content = val;
	}

	private boolean isLeaf() {
		return a == null;
	}

	private boolean isRoot() {
		return parent == null;
	}

	/**
	 * Returns the range in which partitions can lie.
	 * 
	 * @return
	 */
	public Range getRange() {
		return range;
	}

	@Override
	public void put(Range r, T value) {
		Validator.notNull(r, "Range is null!");
		if (!r.intersects(range)) {
			return;
		}

		if (r.contains(range)) {
			content = value;
			a = b = null;
			if (!isRoot()) {
				parent.checkMergeChildren();
			}
			return;
		}

		// Don't create children unless necessary
		if (isLeaf() && sameValue(value, content)) {
			return;
		}
		// If this is a leaf, create children first and then propagate insert
		// (if necessary)
		if (isLeaf()) {
			long m = (range.getStart() + range.getEnd()) / 2;
			a = new Partitions<T>(this, Range.getRangeByNumbers(range
					.getStart(), m), content);
			b = new Partitions<T>(this, Range.getRangeByNumbers(m + 1, range
					.getEnd()), content);
		}

		a.put(r, value);
		// If b is null then a and b were successfully merged
		if (b != null) {
			b.put(r, value);
		}
	}

	@Override
	public Range findFirst(Range r, T val) {
		Validator.notNull(r, "Range is null!");
		if (!range.intersects(r)) {
			return null;
		}

		if (isLeaf()) {
			if (sameValue(content, val)) {
				return Range.getRangeByNumbers(Math.max(r.getStart(), range
						.getStart()), Math.min(r.getEnd(), range.getEnd()));
			} else {
				return null;
			}
		}

		Range ra = a.findFirst(r, val);
		Range rb = b.findFirst(r, val);
		if (ra == null)
			return rb;
		if (rb == null)
			return ra;
		if (ra.getEnd() + 1 == rb.getStart()) {
			return Range.getRangeByNumbers(ra.getStart(), rb.getEnd());
		}
		return ra;
	}

	private void checkMergeChildren() {
		if (a.isLeaf() && b.isLeaf() && sameValue(a.content, b.content)) {
			content = a.content;
			a = null;
			b = null;
			if (!isRoot()) {
				parent.checkMergeChildren();
			}
		}
	}

	private boolean sameValue(T a, T b) {
		return (a == null && b == null) || (a != null && a.equals(b));
	}

	/**
	 * Counts the number of occurrences of the given value in the given range.
	 * 
	 * @param r
	 * @param val
	 * @return
	 */
	public long count(Range r, T val) {
		Validator.notNull(r, "Range is null!");
		if (isLeaf()) {
			if (!sameValue(content, val)) {
				return 0;
			}
			return range.intersectionLength(r);
		}
		return a.count(r, val) + b.count(r, val);
	}

	@Override
	public Range findFirst(T value) {
		return findFirst(getRange(), value);
	}

	@Override
	public T get(long pos) {
		Validator.isTrue(range.contains(pos), "Invalid position: " + pos);
		return getLeafAt(pos).content;
	}

	private Partitions<T> getLeafAt(long pos) {
		if (isLeaf()) {
			return this;
		}
		if (a.getRange().contains(pos)) {
			return a.getLeafAt(pos);
		} else {
			return b.getLeafAt(pos);
		}
	}

	@Override
	public void set(long pos, T value) {
		Validator.isTrue(range.contains(pos), "Invalid position: " + pos);
		put(Range.getRangeByLength(pos, 1), value);
	}

	@Override
	public List<Entry<T>> getValues(Range in) {
		Validator.notNull(in, "Range is null");
		Validator.isTrue(range.contains(in), "Range out of bounds: " + in);
		List<Entry<T>> list = new ArrayList<Entry<T>>();
		for (Iterator<Entry<T>> i = iterator(in); i.hasNext();) {
			list.add(i.next());
		}
		return list;
	}

	@Override
	public List<Entry<T>> getValues() {
		return getValues(getRange());
	}

	@Override
	public Iterator<org.torrent.internal.util.SparseArray.Entry<T>> iterator(
			final Range r) {
		Validator.notNull(r, "Range is null");
		Validator.isTrue(range.contains(r), "Range out of bounds: " + r);
		return new Iterator<Entry<T>>() {
			Range remaining = r;

			@Override
			public boolean hasNext() {
				return remaining.getLength() > 0;
			}

			@Override
			public org.torrent.internal.util.SparseArray.Entry<T> next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				Partitions<T> cur = getLeafAt(remaining.getStart());
				T val = cur.content;
				Range res = cur.getRange();
				while (remaining.getLength() > 0) {
					if (cur.getRange().getEnd() >= remaining.getEnd()) {
						remaining = Range.getRangeByLength(0, 0);
						continue;
					}
					remaining = Range.getRangeByNumbers(
							cur.getRange().getEnd() + 1, remaining.getEnd());
					cur = getLeafAt(remaining.getStart());
					if (!sameValue(cur.content, val)) {
						break;
					}
					res = Range.getRangeByNumbers(res.getStart(), cur
							.getRange().getEnd());
				}
				return new MyEntry(res, val);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	@Override
	public Iterator<org.torrent.internal.util.SparseArray.Entry<T>> iterator() {
		return iterator(getRange());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Partitions [");
		boolean first = true;
		for (Entry<T> e : this) {
			if (!first) {
				builder.append(", ");
			} else {
				first = false;
			}
			builder.append(e);
		}
		builder.append(']');
		return builder.toString();
	}
}
