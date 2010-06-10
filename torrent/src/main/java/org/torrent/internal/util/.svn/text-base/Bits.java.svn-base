package org.torrent.internal.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Bits implements Iterable<Boolean>, Cloneable {
	private int size;
	private int[] bits;

	public Bits(int size) {
		this.size = size;
		bits = new int[(size + 31) >> 5];
	}

	public Bits(Bits bitSet) {
		size = bitSet.size;
		bits = Arrays.copyOf(bitSet.bits, bitSet.bits.length);
	}

	@Override
	public Object clone() {
		try {
			Bits b = (Bits) super.clone();
			b.bits = Arrays.copyOf(bits, bits.length);
			return b;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.getLocalizedMessage());
		}
	}

	public Bits(int size, int[] bits) {
		this.size = size;
		this.bits = Arrays.copyOf(bits, bits.length);
	}

	public Bits unmodifableBits() {
		return new Bits(size, bits) {
			@Override
			public void set(int i, boolean b) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setSize(int size) {
				throw new UnsupportedOperationException();
			}
		};
	}

	public int size() {
		return size;
	}

	public boolean get(int i) {
		Validator.isTrue(i >= 0 && i < size, "Index out of bounds: " + i);
		return (bits[i >> 5] & (1 << (i & 0x1f))) != 0;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(bits);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("[Bitset size: ");
		b.append(size);
		for (int i = 0; i < 8 && i < size; i++) {
			b.append(' ');
			b.append(get(i));
		}
		if (size > 8) {
			b.append("...");
		}
		b.append(']');
		return b.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Bits) {
			Bits o = (Bits) obj;
			return Arrays.equals(bits, o.bits);
		}
		return false;
	}

	@Override
	public Iterator<Boolean> iterator() {
		return new Iterator<Boolean>() {
			private int index = 0;
			private int j = 1;
			private int bitidx = 0;

			@Override
			public boolean hasNext() {
				return bitidx < size;
			}

			@Override
			public Boolean next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				bitidx++;
				boolean res = (bits[index] & j) != 0;
				j <<= 1;
				if (j == 0) {
					j = 1;
					index++;
				}
				return res;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	public void set(int i, boolean b) {
		Validator.isTrue(i >= 0 && i < size, "Index out of bounds: " + i);
		int j = i & 0x1f;
		i >>= 5;
		if (b) {
			bits[i] |= ((1 << j));
		} else {
			bits[i] &= (~(1 << j));
		}
	}

	public void setSize(int size) {
		Validator.isTrue(size >= 0, "Invalid size: " + size);
		this.size = size;
		bits = Arrays.copyOf(bits, (size + 31) >> 5);
	}

	public void set(Bits toField) {
		Validator.notNull(toField, "Bitfield is null!");
		size = toField.size;
		bits = Arrays.copyOf(toField.bits, toField.bits.length);
	}

	public int firstIndexOf(boolean b, int start) {
		Validator
				.isTrue(start >= 0 && start <= size, "Invalid start: " + start);
		for (int i = start; i < size; i++) {
			if ((bits[i >> 5] & (1 << (i & 0x1f))) == 0 ^ b) {
				return i;
			}
		}
		return -1;
	}

	public int firstIndexOf(boolean value) {
		return firstIndexOf(value, 0);
	}

	public int count(boolean b) {
		int sum = 0;
		for (int i = 0; i < size; i++) {
			if (get(i) == b) {
				sum++;
			}
		}
		return sum;
	}

	public static Bits filledBits(int piecesCount) {
		Bits b = new Bits(piecesCount);
		for (int i = 0; i < b.size; i++) {
			b.set(i, true);
		}
		return b;
	}
}
