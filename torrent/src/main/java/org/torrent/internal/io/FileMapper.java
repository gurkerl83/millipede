package org.torrent.internal.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.torrent.data.FileInfo;
import org.torrent.internal.util.Validator;

public class FileMapper {
	public static class Mapping {
		private FileInfo file;
		private long position;
		private int length;

		public Mapping(FileInfo fileInfo, long position, int len) {
			this.file = fileInfo;
			this.position = position;
			this.length = len;
		}

		public FileInfo getFile() {
			return file;
		}

		public long getPosition() {
			return position;
		}

		public int getLength() {
			return length;
		}
	}

	private final FileInfo[] files;

	public FileMapper(Collection<FileInfo> files) {
		Validator.notNull(files, "Files is null!");
		this.files = files.toArray(new FileInfo[0]);
	}

	public List<Mapping> map(long position, int length) {
		List<Mapping> map = new ArrayList<Mapping>();
		int index = 0;
		while (length > 0) {
			if (position >= files[index].getLength()) {
				position -= files[index].getLength();
			} else {
				int len = Math.min((int) (files[index].getLength() - position),
						length);
				map.add(new Mapping(files[index], position, len));
				position = 0;
				length -= len;
			}
			index++;
		}
		return map;
	}
}
