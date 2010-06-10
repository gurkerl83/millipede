package org.torrent.internal.io;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface NBDataReader {
	public interface Callback {
		void read(ByteBuffer dst, long position);

		void caughtException(IOException e);
	}

	void read(ByteBuffer dst, long position, Callback cb);
}
