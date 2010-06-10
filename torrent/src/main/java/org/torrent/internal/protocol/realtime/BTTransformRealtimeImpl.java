package org.torrent.internal.protocol.realtime;

import java.nio.ByteBuffer;

import org.torrent.internal.protocol.BTTransformImpl;
import org.torrent.internal.protocol.BTTransformImpl.MessageID;
import org.torrent.internal.protocol.message.Have;
import org.torrent.internal.protocol.message.realtime.DontHave;
import org.torrent.internal.protocol.message.realtime.WinUpdate;
import org.torrent.internal.util.Validator;

/*
 * To do:	- override super methods to be compatible with goalbit
 * 			- dynamic enumeration at runtime		
 */

public class BTTransformRealtimeImpl extends BTTransformImpl implements
		BTTransformRealtime {
	
	public BTTransformRealtimeImpl() {
		super();
	}
	

	@Override
	public ByteBuffer encode(ByteBuffer a, DontHave dontHave) {
		ByteBuffer buf = prepareMessage(a, 4, MessageID.DONT_HAVE);
		buf.putInt(dontHave.getIndex());
		return buf;
	}

	
	@Override
	public DontHave decodeDontHave(ByteBuffer data) {
		Validator.notNull(data, "Data is null!");
		ByteBuffer buf = partialDecode(data, 5, MessageID.DONT_HAVE);
		return new DontHave(buf.getInt());
	}

	@Override
	public ByteBuffer encode(ByteBuffer a, WinUpdate winUpdate) {
		ByteBuffer buf = prepareMessage(a, 4, MessageID.WIN_UPDATE);
		buf.putInt(winUpdate.getIndex());
		return buf;
	}

	@Override
	public WinUpdate decodeWinUpdate(ByteBuffer data) {
		ByteBuffer buf = partialDecode(data, 5, MessageID.WIN_UPDATE);
		return new WinUpdate(buf.getInt());
	}

	
	@Override
	public ByteBuffer encode(ByteBuffer a, Have have) {
		ByteBuffer buf = prepareMessage(a, 4, MessageID.HAVE);
		buf.putInt(have.getPieceIndex());
		buf.putInt(8, have.getAib());
		return buf;
	}
	
	@Override
	public Have decodeHave(ByteBuffer data) {
		Validator.notNull(data, "Data is null!");
		ByteBuffer buf = partialDecode(data, 5, MessageID.HAVE);
		return new Have(buf.getInt(), buf.getInt(9));
	}
}
