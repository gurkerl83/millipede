package org.torrent.internal.peer.connection;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;

import org.torrent.internal.protocol.message.BitField;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Have;
import org.torrent.internal.transfer.event.BitfieldListener;
import org.torrent.internal.util.Bits;
import org.torrent.internal.util.Validator;

public class AbstractBitfieldHolder implements BitfieldHolder {
	private Bits bitfield;
	private final BitfieldListener callback;

	public AbstractBitfieldHolder(Bits bitfield, BitfieldListener callback) {
		Validator.notNull(bitfield, "Initial bitfield is null!");
		this.bitfield = bitfield;
		this.callback = callback;
	}

	private void updateIndex(int index, boolean value) {
		boolean old = bitfield.get(index);
		bitfield.set(index, value);

		if (callback != null) {
			callback.bitFieldBitChanged(new IndexedPropertyChangeEvent(this,
					"bitfield", old, value, index));
		}
	}

	private void updateBitfield(Bits bits) {
		Bits old = bitfield;
		bitfield = new Bits(bits);
		bitfield.setSize(old.size());
		if (callback != null) {
			callback.bitFieldChanged(new PropertyChangeEvent(this, "bitfield",
					old, bitfield));
		}
	}

	protected void handleMessage(BittorrentMessage message) {
		if (message instanceof Have) {
			System.out.println("handle HAVE message " + ((Have) message).getPieceIndex());
			
			updateIndex(((Have) message).getPieceIndex(), true);
			
		} else if (message instanceof BitField) {
			updateBitfield(((BitField) message).getBitSet());
		}
	}

	@Override
	public Bits getBitField() {
		return bitfield.unmodifableBits();
	}
}
