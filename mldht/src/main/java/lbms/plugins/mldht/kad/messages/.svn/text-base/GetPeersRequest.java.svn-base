package lbms.plugins.mldht.kad.messages;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lbms.plugins.mldht.kad.DHT;
import lbms.plugins.mldht.kad.DHTConstants;
import lbms.plugins.mldht.kad.Key;

//import org.gudy.azureus2.core3.util.BEncoder;

/**
 * @author Damokles
 *
 */
public class GetPeersRequest extends AbstractLookupRequest {


	/**
	 * @param id
	 * @param info_hash
	 */
	public GetPeersRequest (Key id, Key info_hash) {
		super(id,info_hash,Method.GET_PEERS);
	}

	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.messages.MessageBase#apply(lbms.plugins.mldht.kad.DHT)
	 */
	@Override
	public void apply (DHT dh_table) {
		dh_table.getPeers(this);
	}
	
	@Override
	protected String requestMethod() {
		return "get_peers";
	}
	
	@Override
	protected String targetBencodingName() {
		return "info_hash";
	}

	/**
	 * @return the info_hash
	 */
	public Key getInfoHash () {
		return target;
	}
}
