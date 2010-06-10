package org.torrent.bencoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.torrent.internal.util.Validator;

/**
 * Helper class to "BEncode" data.
 * 
 * @author Dennis "Bytekeeper" Waldherr
 * 
 */
public final class BEncoder {
	static final Comparator<String> BYTE_COMPARATOR = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			byte b1[] = bytesOf(o1);
			byte b2[] = bytesOf(o2);
			int len = Math.min(b1.length, b2.length);
			for (int i = 0; i < len; i++) {
				if (b1[i] > b2[i]) {
					return 1;
				}
				if (b1[i] < b2[i]) {
					return -1;
				}
			}
			return b1.length - b2.length;
		}
	};

	/**
	 * Converts a string into the raw byte array representation used to store
	 * into bencoded data.
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] bytesOf(String s) {
		try {
			return s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

	private BEncoder() {
	}

	/**
	 * @param obj
	 * @return
	 */
	public static byte[] bencode(Object obj) {
		Validator.notNull(obj, "Object to encode is null!");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			bencode(out, obj);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out.toByteArray();
	}

	/**
	 * BEncodes data and outputs it to an OutputStream.
	 * 
	 * @param out
	 * @param obj
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void bencode(OutputStream out, Object obj) throws IOException {
		Validator.notNull(obj, "Object to encode is null!");
		if (obj instanceof String) {
			bencodeString(out, (String) obj);
		} else if (obj instanceof byte[]) {
			bencodeString(out, (byte[]) obj);
		} else if (obj instanceof Integer) {
			bencodeInteger(out, BigInteger.valueOf((Integer) obj));
		} else if (obj instanceof Long) {
			bencodeInteger(out, BigInteger.valueOf((Long) obj));
		} else if (obj instanceof BigInteger) {
			bencodeInteger(out, (BigInteger) obj);
		} else if (obj instanceof Collection) {
			bencodeList(out, (Collection<?>) obj);
		} else if (obj instanceof Map) {
			bencodeDictionary(out, (Map<String, ?>) obj);
		} else {
			throw new IllegalArgumentException("Type " + obj.getClass()
					+ " is not bencodable.");
		}
	}

	private static void bencodeDictionary(OutputStream out, Map<String, ?> dict)
			throws IOException {
		assert out != null;
		assert dict != null;

		out.write('d');
		List<String> sorted = new ArrayList<String>(dict.keySet());
		Collections.sort(sorted, BYTE_COMPARATOR);
		for (String key : sorted) {
			bencode(out, key);
			bencode(out, dict.get(key));
		}
		out.write('e');
	}

	private static void bencodeList(OutputStream out, Collection<?> list)
			throws IOException {
		assert out != null;
		assert list != null;
		out.write('l');
		for (Object child : list) {
			bencode(out, child);
		}
		out.write('e');
	}

	private static void bencodeInteger(OutputStream out, BigInteger integer)
			throws IOException {
		assert out != null;
		assert integer != null;
		out.write('i');
		out.write(bytesOf(integer.toString()));
		out.write('e');
	}

	private static void bencodeString(OutputStream out, String string)
			throws IOException {
		assert out != null;
		assert string != null;
		byte[] bytes = bytesOf(string);
		bencodeString(out, bytes);
	}

	private static void bencodeString(OutputStream out, byte[] data)
			throws IOException {
		assert out != null;
		assert data != null;
		out.write(bytesOf(Integer.toString(data.length)));
		out.write(':');
		out.write(data);
	}

}
