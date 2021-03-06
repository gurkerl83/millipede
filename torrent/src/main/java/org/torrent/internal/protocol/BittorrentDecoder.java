package org.torrent.internal.protocol;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import org.torrent.internal.peer.connection.SocketConnection.BittorrentType;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.RawMessage;
import org.torrent.internal.protocol.realtime.BTTransformRealtime;
import org.torrent.internal.protocol.realtime.BTTransformRealtimeImpl;
import org.torrent.internal.util.Validator;

public class BittorrentDecoder {
	private enum State {
		HANDSHAKE_A, HANDSHAKE_B, LENGTH, MESSAGE;
	}

	private static final int MAX_MESSAGE_SIZE = 128 * 1024;

	private final ByteBuffer msgLen = ByteBuffer.allocate(4);

	private State state = State.HANDSHAKE_A;
	private Queue<BittorrentMessage> messages = new LinkedList<BittorrentMessage>();

	private ByteBuffer buffer;
	private BTTransform protocol;
	
	public BittorrentDecoder(BittorrentType type) {
		if(type == BittorrentType.REALTIME)
			this.protocol = new BTTransformRealtimeImpl();
		else
			this.protocol = new BTTransformImpl();
//			this(new BTTransformImpl());
		
		buffer = ByteBuffer.wrap(new byte[protocol.getHandShakeASize()]);
	}

//	public BittorrentDecoder(BTTransform protocol) {
//		this.protocol = protocol;
//		buffer = ByteBuffer.wrap(new byte[protocol.getHandShakeASize()]);
//	}

	public void update(byte[] data) throws BittorrentMessageDecodingException {
		Validator.notNull(data, "Data is null!");
		update(data, 0, data.length);
	}

	public void update(byte[] data, int off, int len)
			throws BittorrentMessageDecodingException {
		Validator.notNull(data, "Data is null!");
		Validator.isTrue(off >= 0 && off + len <= data.length && len > 0,
				"Invalid offset/length: " + off + "/" + len);

		while (len > 0) {
			int t = Math.min(len, buffer.remaining());
			buffer.put(data, off, t);
			off += t;
			len -= t;

			if (!buffer.hasRemaining()) {
				buffer.flip();
				handleBuffer();
				assert buffer.hasRemaining() || buffer.limit() == 4 : "Last message: "
						+ ((LinkedList<BittorrentMessage>) messages).getLast();
			}
		}
	}

	private void handleBuffer() throws BittorrentMessageDecodingException {
		int length;
		switch (state) {
		case HANDSHAKE_A:
			messages.add(protocol.decodeHandshakeA(buffer));
			buffer = ByteBuffer.allocate(protocol.getHandShakeBSize());
			state = State.HANDSHAKE_B;
			break;
		case HANDSHAKE_B:
			messages.add(protocol.decodeHandshakeB(buffer));
			buffer = msgLen;
			buffer.clear();
			state = State.LENGTH;
			break;
		case LENGTH:
			length = buffer.getInt(0);
			if (length > MAX_MESSAGE_SIZE) {
				throw new BittorrentMessageDecodingException(
						"Message exceeds maximum size: " + length);
			}
			if (length == 0) {
				messages.add(protocol.decodeKeepAlive(buffer));
				buffer.clear();
			} else {
				buffer = ByteBuffer.wrap(new byte[4 + length]);
				buffer.putInt(length);
				state = State.MESSAGE;
			}
			break;
		case MESSAGE:
			length = buffer.getInt(0);
			if (buffer.limit() < 5) {
				throw new BittorrentMessageDecodingException(
						"Expected length to be " + (length + 4) + " but was "
								+ buffer.limit());
			}
			switch (buffer.get(4)) {
			case 0:
				messages.add(protocol.decodeChoke(buffer));
				break;
			case 1:
				messages.add(protocol.decodeUnChoke(buffer));
				break;
			case 2:
				messages.add(protocol.decodeInterested(buffer));
				break;
			case 3:
				messages.add(protocol.decodeNotInterested(buffer));
				break;
			case 4:
				messages.add(protocol.decodeHave(buffer));
				break;
			case 5:
				messages.add(protocol.decodeBitField(buffer));
				break;
			case 6:
				messages.add(protocol.decodeRequest(buffer));
				break;
			case 7:
				messages.add(protocol.decodePiece(buffer));
				break;
			case 8:
				messages.add(protocol.decodeCancel(buffer));
				break;
			case 9:
				messages.add(protocol.decodePort(buffer));
				break;
//			case 10:
//				messages.add(protocol.decodeDontHave(buffer));
//				break;
//			case 11:
//				messages.add(protocol.decodeWinUpdate(buffer));
//				break;
			default:
				messages.add(new RawMessage(buffer));
				break;
			}
			buffer = msgLen;
			buffer.clear();
			state = State.LENGTH;
			break;
		}
	}

	public BittorrentMessage nextMessage() {
		if (messages.isEmpty()) {
			return null;
		}
		return messages.remove();
	}
}
