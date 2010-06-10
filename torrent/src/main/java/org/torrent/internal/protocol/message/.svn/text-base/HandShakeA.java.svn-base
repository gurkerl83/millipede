package org.torrent.internal.protocol.message;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.torrent.internal.data.Hash;
import org.torrent.internal.util.Validator;

public class HandShakeA implements BittorrentMessage {
	private static final ByteBuffer PROTOCOL;

	static {
		try {
			byte[] data = "BitTorrent protocol".getBytes("ISO-8859-1");
			PROTOCOL = ByteBuffer.allocate(data.length);
			PROTOCOL.put(data);
			PROTOCOL.flip();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private final Hash infoHash;
	private final byte[] reserved;

	public HandShakeA(Hash infoHash) {
		this(infoHash, new byte[8]);
	}

	public HandShakeA(Hash infoHash, byte[] reserved) {
		Validator.notNull(infoHash, "Info hash is null!");
		Validator.notNull(reserved, "Reserved bytes are null!");
		Validator.isTrue(infoHash.getType() == Hash.Type.SHA1,
				"Wrong hash type: " + infoHash.getType());
		Validator.isTrue(reserved.length == 8, "Wrong size of reserved bytes!");
		this.infoHash = infoHash;
		this.reserved = Arrays.copyOf(reserved, reserved.length);
	}

	public Hash getInfoHash() {
		return infoHash;
	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitHandShakeA(this);
	}

	public byte[] getReserved() {
		return Arrays.copyOf(reserved, reserved.length);
	}

	@Override
	public String toString() {
		return "HandShakeA: InfoHash = " + infoHash + ", reserved bytes = "
				+ Arrays.toString(reserved);
	}

	public static ByteBuffer getPROTOCOL() {
		return PROTOCOL.asReadOnlyBuffer();
	}
}
