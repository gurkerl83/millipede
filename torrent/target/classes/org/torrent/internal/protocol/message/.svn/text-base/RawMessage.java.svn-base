package org.torrent.internal.protocol.message;

import java.nio.ByteBuffer;

import org.torrent.internal.util.Validator;

public class RawMessage implements BittorrentMessage {

	private final ByteBuffer buffer;
	private final int id;

	public RawMessage(ByteBuffer buffer) {
		Validator.notNull(buffer, "Buffer is null!");
		this.buffer = buffer;
		if (buffer.remaining() > 4) {
			this.id = buffer.get(4) & 0xFF;
		} else {
			id = -1;
		}
	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitRawMessage(this);
	}

	public ByteBuffer getBuffer() {
		return buffer.duplicate();
	}

	/**
	 * Returns the message id which was parsed.
	 * 
	 * @return -1 or an ID number which might represent an unknown message type
	 */
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Unidentified message: id = " + id + ", content-length = "
				+ buffer.remaining();
	}
}
