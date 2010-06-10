package org.torrent.internal.io;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface NBDataWriter {
	public interface Callback {
		void written(ByteBuffer src, long position);

		void caughtException(IOException e);
	}

	void write(ByteBuffer src, long position, Callback cb);

}
