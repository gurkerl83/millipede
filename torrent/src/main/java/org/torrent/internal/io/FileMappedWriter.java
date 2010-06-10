package org.torrent.internal.io;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.torrent.internal.io.FileMapper.Mapping;
import org.torrent.internal.util.Validator;

public class FileMappedWriter implements DataWriter {

	private final FileMapper mapper;
	private final FileWriter writer;

	public FileMappedWriter(FileMapper mapper, FileWriter writer) {
		Validator.nonNull(mapper, writer);
		this.mapper = mapper;
		this.writer = writer;
	}

	@Override
	public void write(ByteBuffer src, long position) throws IOException {
		for (Mapping m : mapper.map(position, src.remaining())) {
			src.limit(src.position() + m.getLength());
			writer.write(m.getFile(), src, m.getPosition());
		}
	}
}
