package org.torrent.internal.data;

import java.util.Arrays;

import org.torrent.internal.util.Validator;

public final class Hash {
	private static final String HEX = "0123456789ABCDEF";
	private final byte[] hash;
	private final Type type;

	public enum Type {
		ID(-1),
		SHA1(20);
		
		private final int length;

		Type(int length) {
			this.length = length;
		}
	}

	public Hash(byte[] hash, Type type) {
		Validator.nonNull(hash, type);
		Validator.isTrue(type.length < 0 || type.length == hash.length, "Invalid hash size for " + type);
		this.hash = Arrays.copyOf(hash, hash.length);
		this.type = type;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(hash) ^ type.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != Hash.class) {
			return false;
		}
		Hash other = (Hash) obj;
		return other.type == type && Arrays.equals(hash, other.hash);
	}
	
	public String asHexString() {
		StringBuilder b = new StringBuilder(hash.length * 2);
		for (int i = 0; i < hash.length; i++) {
			b.append(HEX.charAt((hash[i] & 0xff) >> 4));
			b.append(HEX.charAt(hash[i] & 15));
		}
		return b.toString();
	}
	
	@Override
	public String toString() {
		return "HASH, type " + type + " with value " + asHexString();
	}

	public byte[] toByteArray() {
		return Arrays.copyOf(hash, hash.length);
	}

	public int getHashLength() {
		return hash.length;
	}
	
	public Type getType() {
		return type;
	}
}
