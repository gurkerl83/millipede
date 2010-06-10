package org.torrent.internal.util;

public final class Validator {
	private Validator() {

	}

	public static void notNull(Object obj, String message) {
		if (obj == null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isTrue(boolean check, String message) {
		if (!check) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void nonNull(Object... args) {
		for (int i = 0; i < args.length; i++) {
			notNull(args[i], "Element no. " + i + " is null!");
		}
	}
}
