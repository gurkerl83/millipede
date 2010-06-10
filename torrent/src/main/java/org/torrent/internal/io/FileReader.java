package org.torrent.internal.io;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.torrent.data.FileInfo;

public interface FileReader {
	void read(FileInfo file, ByteBuffer dst, long position) throws IOException;
}
