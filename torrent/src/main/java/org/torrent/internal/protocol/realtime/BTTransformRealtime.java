package org.torrent.internal.protocol.realtime;

import java.nio.ByteBuffer;

import org.torrent.internal.protocol.BTTransform;
import org.torrent.internal.protocol.message.realtime.DontHave;
import org.torrent.internal.protocol.message.realtime.WinUpdate;

public interface BTTransformRealtime extends BTTransform {

	ByteBuffer encode(ByteBuffer a, DontHave dontHave);
	DontHave decodeDontHave(ByteBuffer data);
	
	ByteBuffer encode(ByteBuffer a, WinUpdate winUpdate);
	WinUpdate decodeWinUpdate(ByteBuffer data);
	
}
