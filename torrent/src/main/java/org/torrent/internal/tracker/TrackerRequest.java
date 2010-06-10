package org.torrent.internal.tracker;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

public final class TrackerRequest {
	public enum Event {
		UPDATE(null), STARTED("started"), STOPPED("stopped"), COMPLETED(
				"completed");
		private String urlArg;

		Event(String arg) {
			urlArg = arg;
		}

		public String urlArg() {
			return urlArg;
		}
	}

	private String announceURL;
	private byte[] info_hash;
	private byte[] peer_id;
	private int port;
	private long uploadAmount;
	private long downloadAmount;
	private long leftAmount;
	private boolean compact = true;
	private boolean no_peer_id;
	private Event event;
	private InetAddress ip;
	private Integer numwant;
	private String key;
	private String trackerID;

	private URL cachedURL;

	public TrackerRequest(String announceURL, byte[] info_hash, byte[] peer_id,
			int port, long uploadAmount, long downloadAmount, long leftAmount,
			Event event) {
		this.announceURL = announceURL;
		this.info_hash = Arrays.copyOf(info_hash, info_hash.length);
		this.peer_id = Arrays.copyOf(peer_id, peer_id.length);
		this.port = port;
		this.uploadAmount = uploadAmount;
		this.downloadAmount = downloadAmount;
		this.leftAmount = leftAmount;
		this.event = event;
	}

	public TrackerRequest(String announceURL, byte[] info_hash, byte[] peer_id,
			int port, long uploadAmount, long downloadAmount, long leftAmount,
			boolean compact, boolean no_peer_id, Event event, InetAddress ip,
			Integer numwant, String key, String trackerID) {
		this.announceURL = announceURL;
		this.info_hash = Arrays.copyOf(info_hash, info_hash.length);
		this.peer_id = Arrays.copyOf(peer_id, peer_id.length);
		this.port = port;
		this.uploadAmount = uploadAmount;
		this.downloadAmount = downloadAmount;
		this.leftAmount = leftAmount;
		this.compact = compact;
		this.no_peer_id = no_peer_id;
		this.event = event;
		this.ip = ip;
		this.numwant = numwant;
		this.key = key;
		this.trackerID = trackerID;
	}

	public void setCompact(boolean compact) {
		this.compact = compact;
		cachedURL = null;
	}

	public void setAnnounceURL(String announceURL) {
		this.announceURL = announceURL;
		cachedURL = null;
	}

	public URL toURL() throws MalformedURLException {
		if (cachedURL != null) {
			return cachedURL;
		}
		StringBuilder b = new StringBuilder();
		b.append(announceURL);
		b.append('?');
		try {
			urlAdd(b, "info_hash", URLEncoder.encode(new String(info_hash,
					"ISO-8859-1"), "ISO-8859-1"));
			urlAdd(b, "peer_id", URLEncoder.encode(new String(peer_id,
					"ISO-8859-1"), "ISO-8859-1"));
			urlAdd(b, "port", Integer.toString(port));
			urlAdd(b, "uploaded", Long.toString(uploadAmount));
			urlAdd(b, "downloaded", Long.toString(downloadAmount));
			urlAdd(b, "left", Long.toString(leftAmount));
			urlAdd(b, "compact", compact ? "1" : "0");
			urlAdd(b, "no_peer_id", no_peer_id ? "1" : "0");
			if (event != Event.UPDATE) {
				urlAdd(b, "event", event.urlArg());
			}
			if (ip != null) {
				urlAdd(b, "ip", ip.getHostAddress());
			}
			if (numwant != null) {
				urlAdd(b, "numwant", numwant.toString());
			}
			if (key != null) {
				urlAdd(b, "key", key);
			}
			if (trackerID != null) {
				urlAdd(b, "trackerid", trackerID);
			}
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
		cachedURL = new URL(b.toString());
		return cachedURL;
	}

	private void urlAdd(StringBuilder b, String param, String value)
			throws UnsupportedEncodingException {
		if (b.charAt(b.length() - 1) != '?') {
			b.append('&');
		}
		b.append(param);
		if (value != null) {
			b.append('=').append(value);
		}
	}
}
