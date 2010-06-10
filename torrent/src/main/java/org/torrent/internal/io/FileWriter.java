package org.torrent.internal.io;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.torrent.data.FileInfo;

public interface FileWriter {
	void write(FileInfo file, ByteBuffer src, long position) throws IOException;
}
