package org.torrent.internal.protocol.message;

import org.torrent.internal.util.Bits;
import org.torrent.internal.util.Validator;

public class BitField implements BittorrentMessage {

	private final Bits bitSet;

	public BitField(Bits bitSet) {
		Validator.notNull(bitSet, "Bitset is null!");
		this.bitSet = new Bits(bitSet);
	}

	public Bits getBitSet() {
		return bitSet.unmodifableBits();
	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitBitField(this);
	}

	@Override
	public String toString() {
		return "BitField: " + bitSet;
	}
}
