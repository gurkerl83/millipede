package org.torrent.internal.peer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.torrent.internal.bencoding.BMap;
import org.torrent.internal.bencoding.BTypeException;
import org.torrent.internal.data.Hash;
import org.torrent.internal.data.InetSocketAddress;
import org.torrent.internal.util.Validator;

public class PeerInfo {

	private static final String PEER_ID = "peer id";
	private static final String IP = "ip";
	private static final String PORT = "port";

	private final InetSocketAddress address;
	private final Hash peerID;

	public static PeerInfo fromAddress(Hash peerID, InetSocketAddress addr) {
		return getPeerInfo(peerID, addr);
	}

	public static PeerInfo fromBMap(BMap peer) throws BTypeException {
		Validator.notNull(peer, "Peer map is null!");

		return getPeerInfo(
				new Hash((byte[]) peer.get(PEER_ID), Hash.Type.ID),
				new InetSocketAddress(peer.getString(IP), peer.getInteger(PORT)));
	}

	private PeerInfo(Hash peerID, InetSocketAddress addr) {
		Validator.notNull(addr, "Address is null!");
		this.peerID = peerID;
		this.address = addr;
	}

	public static PeerInfo fromRawIP(byte[] array, int off, int len) {
		Validator.notNull(array, "Array is null!");
		Validator.isTrue(off >= 0 && off + len <= array.length,
				"Out of bounds: off=" + off + " len=" + len + " array.length="
						+ array.length);
		try {
			return getPeerInfo(null, new InetSocketAddress(
					InetAddress.getByAddress(
							Arrays.copyOfRange(array, off, off + len - 2))
							.getHostAddress(),
					((array[off + len - 2] & 0xff) << 8)
							| (array[off + len - 1] & 0xff)));
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	/**
	 * Returns the ID of the peer if available.
	 * 
	 * @return
	 */
	public Hash getPeerID() {
		return peerID;
	}

	@Override
	public int hashCode() {
		return address.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PeerInfo) {
			PeerInfo other = (PeerInfo) obj;
			if (peerID != null && !peerID.equals(other.peerID)) {
				return false;
			}
			return address.equals(other.address);
		}
		return false;
	}

	@Override
	public String toString() {
		return "[Peer: " + peerID + ", address: " + address + "]";
	}

	/**
	 * Helper method (maybe doing flyweight pattern later on)
	 */
	private static PeerInfo getPeerInfo(Hash peerID, InetSocketAddress addr) {
		return new PeerInfo(peerID, addr);
	}
}
