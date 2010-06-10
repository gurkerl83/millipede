package org.torrent.internal.io;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.torrent.internal.io.FileMapper.Mapping;
import org.torrent.internal.util.Validator;

public class FileMappedReader implements DataReader {

	private final FileMapper mapper;
	private final FileReader reader;

	public FileMappedReader(FileMapper mapper, FileReader reader) {
		Validator.nonNull(mapper, reader);
		this.mapper = mapper;
		this.reader = reader;
	}

	@Override
	public void read(ByteBuffer dst, long position) throws IOException {
		for (Mapping m : mapper.map(position, dst.remaining())) {
			dst.limit(dst.position() + m.getLength());
			reader.read(m.getFile(), dst, m.getPosition());
		}
	}
}
