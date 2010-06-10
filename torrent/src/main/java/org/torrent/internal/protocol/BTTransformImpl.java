package org.torrent.internal.protocol;

import java.nio.ByteBuffer;

import org.torrent.internal.data.Hash;
import org.torrent.internal.protocol.message.BTMessageVisitor;
import org.torrent.internal.protocol.message.BitField;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Cancel;
import org.torrent.internal.protocol.message.Choke;
import org.torrent.internal.protocol.message.HandShakeA;
import org.torrent.internal.protocol.message.HandShakeB;
import org.torrent.internal.protocol.message.Have;
import org.torrent.internal.protocol.message.Interested;
import org.torrent.internal.protocol.message.KeepAlive;
import org.torrent.internal.protocol.message.NotInterested;
import org.torrent.internal.protocol.message.Piece;
import org.torrent.internal.protocol.message.Port;
import org.torrent.internal.protocol.message.RawMessage;
import org.torrent.internal.protocol.message.Request;
import org.torrent.internal.protocol.message.UnChoke;
import org.torrent.internal.util.Bits;
import org.torrent.internal.util.Validator;

public class BTTransformImpl implements BTTransform {
	public enum MessageID {
		KEEPALIVE(null), CHOKE(0), UNCHOKE(1), INTERESTED(2), NOT_INTERESTED(3), HAVE(
				4), BITFIELD(5), REQUEST(6), PIECE(7), CANCEL(8), PORT(9), HANDSHAKE_A(
				null), HANDSHAKE_B(null), DONT_HAVE(10), WIN_UPDATE(11);

		public final Byte id;

		private MessageID(Integer id) {
			this.id = id != null ? id.byteValue() : null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.torrent.internal.protocol.BittorrentProtocol#decodeBitField(ByteBuffer)
	 */
	public BitField decodeBitField(ByteBuffer data) {
		Validator.notNull(data, "Data is null!");
		ByteBuffer buf = partialDecode(data, data.remaining() - 4,
				MessageID.BITFIELD);
		Bits set = new Bits(data.remaining() * 8);
		int i = 0;
		while (buf.hasRemaining()) {
			byte b = buf.get();
			for (int j = 128; j > 0; j >>= 1) {
				set.set(i++, (b & j) != 0);
			}
		}
		return new BitField(set);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.torrent.internal.protocol.BittorrentProtocol#decodeCancel(ByteBuffer)
	 */
	public Cancel decodeCancel(ByteBuffer data) {
		Validator.notNull(data, "Data is null!");
		ByteBuffer buf = partialDecode(data, 13, MessageID.CANCEL);
		int i = buf.getInt();
		int s = buf.getInt();
		int l = buf.getInt();
		return new Cancel(i, s, l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.torrent.internal.protocol.BittorrentProtocol#decodeChoke(ByteBuffer)
	 */
	public Choke decodeChoke(ByteBuffer data) {
		partialDecode(data, 1, MessageID.CHOKE);
		return Choke.CHOKE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.torrent.internal.protocol.BittorrentProtocol#decodeHandshakeA(ByteBuffer)
	 */
	public HandShakeA decodeHandshakeA(ByteBuffer buf)
			throws BittorrentMessageDecodingException {
		Validator.notNull(buf, "Buffer is null");
		Validator.isTrue(buf.remaining() >= 48, "Expected 48 bytes, got "
				+ buf.remaining());
		ByteBuffer proto = HandShakeA.getPROTOCOL();
		if (buf.get() != proto.remaining()) {
			throw new BittorrentMessageDecodingException(
					"Handshake length invalid: " + buf.get(buf.position() - 1));
		}
		ByteBuffer tmp = buf.duplicate();
		tmp.limit(tmp.position() + proto.remaining());
		buf.position(buf.position() + proto.remaining());

		if (!proto.equals(tmp)) {
			throw new BittorrentMessageDecodingException(
					"Handshake protocol invalid!");
		}
		byte res[] = new byte[8];
		buf.get(res);
		byte[] tmp2 = new byte[20];
		buf.get(tmp2);
		Hash info = new Hash(tmp2, Hash.Type.SHA1);
		HandShakeA hs = new HandShakeA(info, res);
		return hs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.torrent.internal.protocol.BittorrentProtocol#decodeHandshakeB(ByteBuffer)
	 */
	public HandShakeB decodeHandshakeB(ByteBuffer data) {
		Validator.notNull(data, "Data is null!");
		Validator.isTrue(data.remaining() >= 20, "Invalid peer id size");
		byte[] id = new byte[20];
		data.get(id);
		return new HandShakeB(new Hash(id, Hash.Type.ID));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.torrent.internal.protocol.BittorrentProtocol#decodeHave(ByteBuffer)
	 */
	public Have decodeHave(ByteBuffer data) {
		Validator.notNull(data, "Data is null!");
		ByteBuffer buf = partialDecode(data, 5, MessageID.HAVE);
		return new Have(buf.getInt());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.torrent.internal.protocol.BittorrentProtocol#decodeInterested(ByteBuffer)
	 */
	public Interested decodeInterested(ByteBuffer data) {
		partialDecode(data, 1, MessageID.INTERESTED);
		return Interested.INTERESTED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.torrent.internal.protocol.BittorrentProtocol#decodeKeepAlive(ByteBuffer)
	 */
	public KeepAlive decodeKeepAlive(ByteBuffer data) {
		Validator.notNull(data, "Data is null!");
		validateEmptyMessage(data);
		return KeepAlive.KEEPALIVE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#decodeNotInterested(ByteBuffer)
	 */
	public NotInterested decodeNotInterested(ByteBuffer data) {
		partialDecode(data, 1, MessageID.NOT_INTERESTED);
		return NotInterested.NOTINTERESTED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.torrent.internal.protocol.BittorrentProtocol#decodePiece(ByteBuffer)
	 */
	public Piece decodePiece(ByteBuffer data) {
		Validator.notNull(data, "Data is null!");
		ByteBuffer buf = partialDecode(data, data.remaining() - 4,
				MessageID.PIECE);
		int i = buf.getInt();
		int s = buf.getInt();
		data = data.duplicate();
		data.position(13);
		return new Piece(i, s, data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.torrent.internal.protocol.BittorrentProtocol#decodePort(ByteBuffer)
	 */
	public Port decodePort(ByteBuffer data) {
		Validator.notNull(data, "Data is null!");
		ByteBuffer buf = partialDecode(data, 3, MessageID.PORT);
		int port = buf.getShort() & 0xFFFF;


//							System.out.println("Message of type port");
//							System.out.println("Requesting piece index: " + port);

//                                                        if (!ml_dht_enabled) {return;}
//                                                        MainlineDHTProvider provider = getDHTProvider();
//                                                        if (provider == null) {return;}
//
//                                                        try {provider.notifyOfIncomingPort(socket.getInetAddress().getHostAddress(), ((Port) msg).getPort());}
//                                                        catch (Throwable t) {t.printStackTrace();}

//							con.send(new Request(((Have) msg).getPieceIndex(), 0, info.getPieceLength()), null);
//							//bitFieldSender(bitProv, (BTSocketConnection) con));
//

                return new Port(port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.torrent.internal.protocol.BittorrentProtocol#decodeRequest(ByteBuffer)
	 */
	public Request decodeRequest(ByteBuffer data) {
		Validator.notNull(data, "Data is null!");
		ByteBuffer buf = partialDecode(data, 13, MessageID.REQUEST);
		int i = buf.getInt();
		int s = buf.getInt();
		int l = buf.getInt();
		return new Request(i, s, l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.torrent.internal.protocol.BittorrentProtocol#decodeUnChoke(ByteBuffer)
	 */
	public UnChoke decodeUnChoke(ByteBuffer data) {
		partialDecode(data, 1, MessageID.UNCHOKE);
		return UnChoke.UNCHOKE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .BitField)
	 */
	public ByteBuffer encode(ByteBuffer a, BitField bitField) {
		Bits set = bitField.getBitSet();
		ByteBuffer buf = prepareMessage(a, (set.size() + 7) / 8,
				MessageID.BITFIELD);
		for (int i = 0; i < set.size();) {
			byte data = 0;
			for (int j = 128; i < set.size() && j > 0; j >>= 1, i++) {
				if (set.get(i)) {
					data |= j;
				}
			}
			buf.put(data);
		}
		assert buf.remaining() == 0;
		return buf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .Cancel)
	 */
	public ByteBuffer encode(ByteBuffer a, Cancel cancel) {
		ByteBuffer buf = prepareMessage(a, 12, MessageID.CANCEL);
		buf.putInt(cancel.getIndex());
		buf.putInt(cancel.getStart());
		buf.putInt(cancel.getLength());
		return buf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .Choke)
	 */
	public ByteBuffer encode(ByteBuffer a, Choke choke) {
		return prepareMessage(a, 0, MessageID.CHOKE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .HandShakeA)
	 */
	public ByteBuffer encode(ByteBuffer a, HandShakeA hs) {
		ByteBuffer buf = ByteBuffer.allocate(48);
		ByteBuffer proto = HandShakeA.getPROTOCOL();
		buf.put((byte) proto.remaining());
		buf.put(proto);
		buf.put(new byte[8]);
		buf.put(hs.getInfoHash().toByteArray());
		return buf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .HandShakeB)
	 */
	public ByteBuffer encode(ByteBuffer a, HandShakeB hs) {
		byte[] id = hs.getPeerID().toByteArray();
		if (a == null || a.remaining() < id.length) {
			a = ByteBuffer.allocate(id.length);
		}
		a.put(id);
		return a;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .Have)
	 */
	public ByteBuffer encode(ByteBuffer a, Have have) {
		ByteBuffer buf = prepareMessage(a, 4, MessageID.HAVE);
		buf.putInt(have.getPieceIndex());
		return buf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .Interested)
	 */
	public ByteBuffer encode(ByteBuffer a, Interested interested) {
		return prepareMessage(a, 0, MessageID.INTERESTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .KeepAlive)
	 */
	public ByteBuffer encode(ByteBuffer a, KeepAlive keepAlive) {
		return prepareMessage(a, 0, MessageID.KEEPALIVE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .NotInterested)
	 */
	public ByteBuffer encode(ByteBuffer a, NotInterested notInterested) {
		return prepareMessage(a, 0, MessageID.NOT_INTERESTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .Piece)
	 */
	public ByteBuffer encode(ByteBuffer a, Piece piece) {
		ByteBuffer buf = prepareMessage(a, 8 + piece.getLength(),
				MessageID.PIECE);
		buf.putInt(piece.getIndex());
		buf.putInt(piece.getStart());
		buf.put(piece.getData());
		return buf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .Port)
	 */
	public ByteBuffer encode(ByteBuffer a, Port port) {
		ByteBuffer buf = prepareMessage(a, 2, MessageID.PORT);
		buf.putShort((short) (port.getPort() & 0xffff));
		return buf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .Request)
	 */
	public ByteBuffer encode(ByteBuffer a, Request request) {
		ByteBuffer buf = prepareMessage(a, 12, MessageID.REQUEST);
		buf.putInt(request.getIndex());
		buf.putInt(request.getStart());
		buf.putInt(request.getLength());
		return buf;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.torrent.internal.protocol.BittorrentProtocol#encode(org.torrent.internal.protocol.message
	 * .UnChoke)
	 */
	public ByteBuffer encode(ByteBuffer a, UnChoke unChoke) {
		return prepareMessage(a, 0, MessageID.UNCHOKE);
	}

	/**
	 * @param data
	 * @return
	 */
	public ByteBuffer partialDecode(ByteBuffer buf, int len, MessageID msgID) {
		int length = buf.getInt();
		Validator.isTrue(length == len, "Invalid length: " + length
				+ " expected " + len);
		byte type = buf.get();
		Validator.isTrue(type == msgID.id, "Invalid type: " + type);
		return buf;
	}

	protected ByteBuffer prepareMessage(ByteBuffer a, int payloadLength,
			MessageID id) {
		assert id != null;
		ByteBuffer buffer;
		if (id.id != null) {
			buffer = a != null && a.remaining() >= payloadLength + 5 ? a
					: ByteBuffer.wrap(new byte[payloadLength + 5]);
		} else {
			buffer = a != null && a.remaining() >= payloadLength + 4 ? a
					: ByteBuffer.wrap(new byte[payloadLength + 4]);
		}
		buffer.putInt(buffer.capacity() - 4);
		if (id.id != null) {
			buffer.put(id.id);
		}
		return buffer;
	}

	/**
	 * @param data
	 */
	private void validateEmptyMessage(ByteBuffer buf) {
		int length = buf.getInt();
		Validator.isTrue(length == 0, "Not a valid keep-alive message!");
	}

	@Override
	public int getHandShakeASize() {
		return 29 + HandShakeA.getPROTOCOL().remaining();
	}

	@Override
	public int getHandShakeBSize() {
		return 20;
	}

	@Override
	public ByteBuffer encodeMessage(final ByteBuffer a, BittorrentMessage msg) {
		final ByteBuffer[] result = new ByteBuffer[1];
		msg.accept(new BTMessageVisitor() {

			@Override
			public void visitBitField(BitField bitField) {
				result[0] = encode(a, bitField);
			}

			@Override
			public void visitCancel(Cancel cancel) {
				result[0] = encode(a, cancel);
			}

			@Override
			public void visitChoke(Choke choke) {
				result[0] = encode(a, choke);
			}

			@Override
			public void visitHandShakeA(HandShakeA handShakeA) {
				result[0] = encode(a, handShakeA);
			}

			@Override
			public void visitHandShakeB(HandShakeB handShakeB) {
				result[0] = encode(a, handShakeB);
			}

			@Override
			public void visitHave(Have have) {
				result[0] = encode(a, have);
			}

			@Override
			public void visitInterested(Interested interested) {
				result[0] = encode(a, interested);
			}

			@Override
			public void visitKeepAlive(KeepAlive keepAlive) {
				result[0] = encode(a, keepAlive);
			}

			@Override
			public void visitNotInterested(NotInterested notInterested) {
				result[0] = encode(a, notInterested);
			}

			@Override
			public void visitPiece(Piece piece) {
				result[0] = encode(a, piece);
			}

			@Override
			public void visitPort(Port port) {
				result[0] = encode(a, port);
			}

			@Override
			public void visitRequest(Request request) {
				result[0] = encode(a, request);
			}

			@Override
			public void visitUnChoke(UnChoke unChoke) {
				result[0] = encode(a, unChoke);
			}

			@Override
			public void visitRawMessage(RawMessage rawMessage) {
				result[0] = rawMessage.getBuffer();
			}

//			@Override
//			public void visitDontHave(DontHave dontHave) {
//				result[0] = encode(a, dontHave);
//				
//			}
//
//			@Override
//			public void visitWinUpdate(WinUpdate winUpdate) {
//				result[0] = encode(a, winUpdate);
//				
//			}

		});
		return result[0];
	}

//	@Override
//	public DontHave decodeDontHave(ByteBuffer data) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public WinUpdate decodeWinUpdate(ByteBuffer data) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ByteBuffer encode(ByteBuffer a, DontHave dontHave) {
////		Bits set = bitField.getBitSet();
////		ByteBuffer buf = prepareMessage(a, (set.size() + 7) / 8,
////				MessageID.BITFIELD);
////		for (int i = 0; i < set.size();) {
////			byte data = 0;
////			for (int j = 128; i < set.size() && j > 0; j >>= 1, i++) {
////				if (set.get(i)) {
////					data |= j;
////				}
////			}
////			buf.put(data);
////		}
////		assert buf.remaining() == 0;
////		return buf;
//		
//		ByteBuffer buf = prepareMessage(a, 5, MessageID.DONT_HAVE);
//		buf.putInt(dontHave.getIndex());
////		buf.putInt(dontHave.getStart());
////		buf.putInt(dontHave.getLength());
//		return buf;
//	}
//
//	@Override
//	public ByteBuffer encode(ByteBuffer a, WinUpdate winUpdate) {
//		ByteBuffer buf = prepareMessage(a, 5, MessageID.WIN_UPDATE);
//		buf.putInt(winUpdate.getIndex());
////		buf.putInt(dontHave.getStart());
////		buf.putInt(dontHave.getLength());
//		return buf;
//	}
//	
////	ssize_t btStream::Send_DontHave(size_t idx)
////	{
////	    char msg[H_LEN + H_DONTHAVE_LEN];
////
////	    msg_Dbg( p_sys->p_btc, "GoalbitParser|DONT_HAVE_OUT|%d|%lld", p_sys->p_btc->i_object_id, mdate());
////
////	    set_nl(msg, H_DONTHAVE_LEN);
////	    msg[H_LEN] = (char)M_DONT_HAVE;
////	    set_nl(msg + H_LEN + H_BASE_LEN, idx);
////
////	    return out_buffer.Put(sock, msg, H_LEN + H_DONTHAVE_LEN);
////	}
////
////
////	ssize_t btStream::Send_WinUpdate(size_t idx)
////	{
////	    char msg[H_LEN + H_WIN_UPDATE];
////
////	    msg_Dbg( p_sys->p_btc, "GoalbitParser|WIN_UPDATE_OUT|%d|%lld", p_sys->p_btc->i_object_id, mdate());
////
////	    set_nl(msg, H_WIN_UPDATE);
////	    msg[H_LEN] = (char)M_WIN_UPDATE;
////	    set_nl(msg + H_LEN + H_BASE_LEN, idx);
////
////	    return out_buffer.Put(sock,msg,H_LEN + H_WIN_UPDATE);
////	}
}
