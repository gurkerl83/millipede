package org.torrent.internal.data;

import org.torrent.internal.util.Range;
import org.torrent.internal.util.Validator;

public class BTPart {

	private final int start;
	private final int index;
	private final int length;

	public BTPart(int index, int start, int length) {
		super();
		Validator.isTrue(index >= 0, "Index < 0");
		Validator.isTrue(length >= 0, "Length < 0");
		Validator.isTrue(start >= 0, "Start < 0");
		this.index = index;
		this.length = length;
		this.start = start;
	}

	public int getStart() {
		return start;
	}

	public int getIndex() {
		return index;
	}

	public int getLength() {
		return length;
	}

	@Override
	public int hashCode() {
		return start ^ index ^ length;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BTPart) {
			BTPart o = (BTPart) obj;
			return start == o.start && index == o.index && length == o.length;
		}
		return false;
	}

	@Override
	public String toString() {
		return "[index = " + index + ", start = " + start + ", length = "
				+ length + "]";
	}

	public Range getRange() {
		return Range.getRangeByLength(start, length);
	}
}
