package org.torrent.internal.protocol;

import java.nio.ByteBuffer;

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
import org.torrent.internal.protocol.message.Request;
import org.torrent.internal.protocol.message.UnChoke;

public interface BTTransform {

	BitField decodeBitField(ByteBuffer data);

	Cancel decodeCancel(ByteBuffer data);

	Choke decodeChoke(ByteBuffer data);

	HandShakeA decodeHandshakeA(ByteBuffer data)
			throws BittorrentMessageDecodingException;

	HandShakeB decodeHandshakeB(ByteBuffer data);

	Have decodeHave(ByteBuffer data);

	Interested decodeInterested(ByteBuffer data);

	/**
	 * Decodes a keep-alive message. This message checks the argument. Since the
	 * message does not contain any payload, this method could be overridden by
	 * returning a KeepAlive object without checking the argument.
	 * 
	 * @param data
	 * @return
	 */
	KeepAlive decodeKeepAlive(ByteBuffer data);

	NotInterested decodeNotInterested(ByteBuffer data);

	Piece decodePiece(ByteBuffer data);

	Port decodePort(ByteBuffer data);

	Request decodeRequest(ByteBuffer data);

	UnChoke decodeUnChoke(ByteBuffer data);

	ByteBuffer encode(ByteBuffer a, BitField bitField);

	ByteBuffer encode(ByteBuffer a, Cancel cancel);

	ByteBuffer encode(ByteBuffer a, Choke choke);

	ByteBuffer encode(ByteBuffer a, HandShakeA hs);

	ByteBuffer encode(ByteBuffer a, HandShakeB hs);

	ByteBuffer encode(ByteBuffer a, Have have);

	ByteBuffer encode(ByteBuffer a, Interested interested);

	ByteBuffer encode(ByteBuffer a, KeepAlive keepAlive);

	ByteBuffer encode(ByteBuffer a, NotInterested notInterested);

	ByteBuffer encode(ByteBuffer a, Piece piece);

	ByteBuffer encode(ByteBuffer a, Port port);

	ByteBuffer encode(ByteBuffer a, Request request);

	ByteBuffer encode(ByteBuffer a, UnChoke unChoke);

	int getHandShakeASize();

	int getHandShakeBSize();

	ByteBuffer encodeMessage(ByteBuffer a, BittorrentMessage msg);

//	ByteBuffer encode(ByteBuffer a, DontHave dontHave);
//	DontHave decodeDontHave(ByteBuffer data);
//	
//	ByteBuffer encode(ByteBuffer a, WinUpdate winUpdate);
//	WinUpdate decodeWinUpdate(ByteBuffer data);
	
}