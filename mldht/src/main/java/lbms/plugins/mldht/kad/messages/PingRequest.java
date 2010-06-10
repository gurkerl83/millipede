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
public class PingRequest extends MessageBase {

	/**
	 * @param id
	 */
	public PingRequest (Key id) {
		super(new byte[] {(byte) 0xFF}, Method.PING, Type.REQ_MSG, id);
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.messages.MessageBase#apply(lbms.plugins.mldht.kad.DHT)
	 */
	@Override
	public void apply (DHT dh_table) {
		dh_table.ping(this);
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

		base.put(DHTConstants.REQ, "ping");
		base.put(DHTConstants.TID, mtid);
		base.put(DHTConstants.TYP, DHTConstants.REQ);
		base.put(DHTConstants.VER, DHTConstants.getVersion());

		return BEncoder.encode(base);
	}
}
