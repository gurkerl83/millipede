package lbms.plugins.mldht.kad.messages;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lbms.plugins.mldht.kad.DHT;
import lbms.plugins.mldht.kad.DHTConstants;
import lbms.plugins.mldht.kad.Key;

import org.gudy.azureus2.core3.util.BEncoder;

/**
 * @author Damokles
 *
 */
public class AnnounceRequest extends GetPeersRequest {

	protected int		port;
	protected byte[]	token;

	/**
	 * @param id
	 * @param info_hash
	 * @param port
	 * @param token
	 */
	public AnnounceRequest (Key id, Key info_hash, int port, byte[] token) {
		super(id, info_hash);
		this.port = port;
		this.token = token;
		this.method = Method.ANNOUNCE_PEER;
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.messages.GetPeersRequest#apply(lbms.plugins.mldht.kad.DHT)
	 */
	@Override
	public void apply (DHT dh_table) {
		dh_table.announce(this);
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.messages.MessageBase#encode()
	 */
	@Override
	public byte[] encode () throws IOException {
		Map<String, Object> base = new HashMap<String, Object>();
		Map<String, Object> inner = new HashMap<String, Object>();
		base.put(DHTConstants.ARG, inner);
		inner.put("id", id.getHash());
		inner.put("info_hash", target.getHash());
		inner.put("port", port);
		inner.put("token", token);

		base.put(DHTConstants.REQ, "announce_peer");
		base.put(DHTConstants.TID, mtid);
		base.put(DHTConstants.TYP, DHTConstants.REQ);
		base.put(DHTConstants.VER, DHTConstants.getVersion());
		return BEncoder.encode(base);
        }

	/**
	 * @return the token
	 */
	public byte[] getToken () {
		return token;
	}
}
