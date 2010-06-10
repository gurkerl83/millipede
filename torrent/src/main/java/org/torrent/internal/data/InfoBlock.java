package org.torrent.internal.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.torrent.internal.bencoding.BEncoder;
import org.torrent.internal.bencoding.BList;
import org.torrent.internal.bencoding.BMap;
import org.torrent.internal.bencoding.BTypeException;
import org.torrent.internal.util.Validator;
import org.torrent.data.FileInfo;

public class InfoBlock {
	private static final String PIECE_LENGTH = "piece length";
	private static final String PIECES = "pieces";
	private static final String NAME = "name";
	private static final String LENGTH = "length";
	private static final String FILES = "files";
	private static final String PATH = "path";
	private static final String PRIVATE = "private";

	private final int pieceLength;
	private final List<Hash> hashList;
//	private final List<FileInfo> files;
	private final ArrayList<FileInfo> files;
	
	private Hash infoHash;
	private final Boolean privTracker;
	private final boolean singleFileMode;
	private final String baseDir;

	private long dataSize;

	public static InfoBlock singleFile(FileInfo fileInfo, List<Hash> hashes,
			int pieceLength, Boolean priv) {
		Validator.nonNull(fileInfo, hashes);
		Validator.isTrue(pieceLength > 0, "Illegal piece length: "
				+ pieceLength);

		InfoBlock data = new InfoBlock(Arrays
				.asList(new FileInfo[] { fileInfo }), hashes, pieceLength,
				priv, null, true);
		Map<String, Object> info = data.createBasicInfo();

		info.put(NAME, fileInfo.getFileName());
		info.put(LENGTH, fileInfo.getLength());
		data.infoHash = data.calculateInfoHash(info);
		return data;
	}

	public static InfoBlock multiFile(String baseDir, List<FileInfo> files,
			List<Hash> hashes, int pieceLength, Boolean priv) {
		Validator.nonNull(baseDir, files, hashes);
		Validator.isTrue(pieceLength > 0, "Illegal piece length: "
				+ pieceLength);

		InfoBlock data = new InfoBlock(files, hashes, pieceLength, priv,
				baseDir, false);
		Map<String, Object> info = data.createBasicInfo();

		info.put(NAME, baseDir);
		List<Map<String, Object>> flst = new ArrayList<Map<String, Object>>(
				files.size());
		for (FileInfo file : files) {
			Map<String, Object> fmap = new HashMap<String, Object>();
			flst.add(fmap);

			fmap.put(LENGTH, file.getLength());
			List<String> path = fileToPath(file);
			fmap.put(PATH, path);
		}
		data.infoHash = data.calculateInfoHash(info);
		return data;
	}

	/**
	 * @param file
	 * @return
	 */
	private static List<String> fileToPath(FileInfo file) {
		List<String> path = new LinkedList<String>();
		StringTokenizer tok = new StringTokenizer(file.getFileName(),
				File.separator);
		while (tok.hasMoreTokens()) {
			path.add(tok.nextToken());
		}
		return path;
	}

	private Map<String, Object> createBasicInfo() {
		Map<String, Object> info = new HashMap<String, Object>();
		info.put(PIECE_LENGTH, pieceLength);

		ByteArrayOutputStream bout = new ByteArrayOutputStream(
				hashList.size() * 20);
		for (Hash h : hashList) {
			try {
				bout.write(h.toByteArray());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		info.put(PIECES, bout.toByteArray());
		if (privTracker != null) {
			info.put(PRIVATE, privTracker ? 1 : 0);
		}
		return info;
	}

	private InfoBlock(List<FileInfo> files, List<Hash> hashes, int pieceLength,
			Boolean priv, String baseDir, boolean singleFileMode) {
		assert files.size() == 1 || !singleFileMode;
		assert baseDir != null || singleFileMode;

		this.pieceLength = pieceLength;
		privTracker = priv;
		this.singleFileMode = singleFileMode;
		this.files = new ArrayList<FileInfo>(files);
		hashList = new ArrayList<Hash>(hashes);
		this.baseDir = baseDir;

		calculateSize();

		int reqHashes = (int) ((dataSize + pieceLength - 1) / pieceLength);
		Validator.isTrue(reqHashes == hashList.size(),
				"No. of hashes required: " + reqHashes + " but got "
						+ hashList.size());
	}

	private void calculateSize() {
		dataSize = 0;
		for (FileInfo fi : files) {
			dataSize += fi.getLength();
		}

	}

	public InfoBlock(BMap info) throws BTypeException {
		Validator.notNull(info, "Info dictionary is null!");
		pieceLength = ((BigInteger) info.get(PIECE_LENGTH)).intValue();

		byte[] hashes = (byte[]) info.get(PIECES);
		Validator.notNull(hashes, "Pieces entry in info dictionary is null!");
		Validator.isTrue(hashes.length % 20 == 0,
				"Pieces entry in info dictionary is invalid (length "
						+ hashes.length + " not multiple of 20)!");

		hashList = new ArrayList<Hash>(hashes.length / 20);
		int off = 0;
		while (off < hashes.length) {
			hashList.add(new Hash(Arrays.copyOfRange(hashes, off, off + 20),
					Hash.Type.SHA1));
			off += 20;
		}

		files = new ArrayList<FileInfo>();

		String base = info.getString(NAME);
		Validator.notNull(base, "Missing non-optional entry " + NAME);
		if (info.containsKey(LENGTH)) {
			singleFileMode = true;
			baseDir = null;
			// Single file mode
			files.add(new FileInfo(base, ((BigInteger) info.get(LENGTH))
					.longValue()));
		} else {
			singleFileMode = false;
			baseDir = base;

			BList fList = info.getList(FILES);
			for (int i = 0; i < fList.size(); i++) {
				BMap file = fList.getMap(i);
				StringBuilder path = new StringBuilder();
				BList elements = file.getList(PATH);
				for (int j = 0; j < elements.size(); j++) {
					String element = elements.getString(j);
					if (path.length() > 0) {
						path.append(File.separatorChar);
					}
					path.append(element);
				}
				files.add(new FileInfo(path.toString(), ((BigInteger) file
						.get(LENGTH)).longValue()));
			}
		}

		infoHash = calculateInfoHash(info);
		privTracker = Integer.valueOf(1).equals(info.getInteger(PRIVATE));

		calculateSize();
	}

	/**
	 * @param info
	 * @return
	 */
	private Hash calculateInfoHash(Map<String, ?> info) {
		try {
			MessageDigest sha1 = MessageDigest.getInstance("sha1");
			sha1.update(BEncoder.bencode(info));
			return new Hash(sha1.digest(), Hash.Type.SHA1);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public int getPieceLength() {
		return pieceLength;
	}

	public List<Hash> getPieceHashes() {
		return Collections.unmodifiableList(hashList);
	}

	public int getPiecesCount() {
		return hashList.size();
	}

	public ArrayList<FileInfo> getFiles() {
//		return Collections.unmodifiableList(files);
		return files;
	}

	public Hash getInfoHash() {
		return infoHash;
	}

	public boolean isPrivate() {
		return privTracker != null && privTracker;
	}

	/**
	 * Determines if this DataInfo was created with a bencoded SingleFileMode
	 * structure.
	 * 
	 * @return
	 */
	public boolean isSingleFileMode() {
		return singleFileMode;
	}

	public String getBaseDir() {
		return baseDir;
	}

	public Map<String, ?> asDictionary() {
		Map<String, Object> infoMap = createBasicInfo();
		if (singleFileMode) {
			infoMap.put(NAME, files.get(0).getFileName());
			infoMap.put(LENGTH, files.get(0).getLength());
		} else {
			infoMap.put(NAME, baseDir);
			List<Map<String, Object>> tfiles = new LinkedList<Map<String, Object>>();
			for (FileInfo file : files) {
				Map<String, Object> entry = new HashMap<String, Object>();
				entry.put(PATH, fileToPath(file));
				entry.put(LENGTH, file.getLength());
				tfiles.add(entry);
			}
			infoMap.put(FILES, tfiles);
		}
		return infoMap;
	}

	public long getDataSize() {
		return dataSize;
	}
}
