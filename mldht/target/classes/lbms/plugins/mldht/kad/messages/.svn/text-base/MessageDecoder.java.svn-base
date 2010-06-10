package lbms.plugins.mldht.kad.messages;

import java.util.*;

//import org.gudy.azureus2.core3.util.BEncoder;

import lbms.plugins.mldht.kad.*;
import lbms.plugins.mldht.kad.DHT.DHTtype;
import lbms.plugins.mldht.kad.DHT.LogLevel;
import lbms.plugins.mldht.kad.messages.MessageBase.Method;

/**
 * @author Damokles
 * 
 */
public class MessageDecoder {

	public static MessageBase parseMessage (Map<String, Object> map,
			RPCServerBase srv) {

		try {
			String vn = getStringFromBytes((byte[]) map.get(DHTConstants.TYP));
			if (vn == null) {
				return null;
			}

			String version = getStringFromBytes((byte[]) map
					.get(DHTConstants.VER),true);

			MessageBase mb = null;
			if (vn.equals(DHTConstants.REQ)) {
				mb = parseRequest(map, srv);
			} else if (vn.equals(DHTConstants.RSP)) {
				mb = parseResponse(map, srv);
			} else if (vn.equals(DHTConstants.ERR)) {
				mb = parseError(map);
			}

			if (mb != null && version != null) {
				mb.setVersion(version);
			}

			return mb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param map
	 * @return
	 */
	private static MessageBase parseError (Map<String, Object> map) {
		Object error = map.get(DHTConstants.ERR);
		
		int errorCode = 0;
		String errorMsg = null;
		
		if(error instanceof byte[])
			errorMsg = getStringFromBytes((byte[])error);
		else if (error instanceof List<?>)
		{
			List<Object> errmap = (List<Object>)error;
			try
			{
				errorCode = ((Long) errmap.get(0)).intValue();
				errorMsg = getStringFromBytes((byte[]) errmap.get(1));
			} catch (Exception e)
			{
				// do nothing
			}
		}
		
		
		if (errorMsg == null || !map.containsKey(DHTConstants.TID)) {
			return null;
		}

		byte[] mtid;
		if (map.get(DHTConstants.TID) instanceof Long) {
			return null;//mtid = ((Long) map.get(DHTConstants.TID)).byteValue();
		} else if (map.get(DHTConstants.TID) instanceof byte[]) {
			byte[] ba = (byte[]) map.get(DHTConstants.TID);

			if (ba == null || ba.length < 1) {
				return null;
			}
			mtid = ba;
		} else {
			return null;
		}

		return new ErrorMessage(mtid, errorCode,errorMsg);
	}

	/**
	 * @param map
	 * @param srv
	 * @return
	 */
	private static MessageBase parseResponse (Map<String, Object> map,RPCServerBase srv) {
		
		
		Map<String, Object> args = (Map<String, Object>) map.get(DHTConstants.RSP);
		if (args == null || map.get(DHTConstants.TID) == null) {
			DHT.logDebug("ParseRsp : args || !args.getValue(id) || !dict.getValue(TID)");
			return null;
		}

		byte[] mtid;
		if (map.get(DHTConstants.TID) instanceof Long) {
			return null;//mtid = ((Long) map.get(DHTConstants.TID)).byteValue();
		} else if (map.get(DHTConstants.TID) instanceof byte[]) {
			byte[] ba = (byte[]) map.get(DHTConstants.TID);

			if (ba == null || ba.length != 2) {
				return null;
			}
			mtid = ba;
		} else {
			return null;
		}

		// find the call
		RPCCallBase c = srv.findCall(mtid);
		if (c == null) {
			DHT.logDebug("Cannot find RPC call for response: "
					+ new String(mtid));
			return null;
		}

		return parseResponse(map, c.getMessageMethod(), mtid,c);
	}

	/**
	 * @param map
	 * @param msgMethod
	 * @param mtid
	 * @return
	 */
	private static MessageBase parseResponse (Map<String, Object> map,
			Method msgMethod, byte[] mtid,RPCCallBase base) {
		Map<String, Object> args = (Map<String, Object>) map.get(DHTConstants.RSP);
		if (args == null || !args.containsKey("id")) {
			return null;
		}

		byte[] hash = (byte[]) args.get("id");

		if (hash == null || hash.length != Key.SHA1_HASH_LENGTH) {
			return null;
		}

		Key id = new Key(hash);

		switch (msgMethod) {
		case PING:
			return new PingResponse(mtid, id);
		case ANNOUNCE_PEER:
			return new AnnounceResponse(mtid, id);
		case FIND_NODE:
			if (!args.containsKey("nodes") && !args.containsKey("nodes6"))
				return null;
			
			return new FindNodeResponse(mtid, id, (byte[]) args.get("nodes"),(byte[])args.get("nodes6"));
		case GET_PEERS:
			byte[] token = (byte[]) args.get("token");
			byte[] nodes = (byte[]) args.get("nodes");
			byte[] nodes6 = (byte[]) args.get("nodes6");
			
			List<byte[]> vals = (List<byte[]>) args.get("values");
			List<DBItem> dbl = null;
			if (vals != null && vals.size() > 0)
			{
				dbl = new ArrayList<DBItem>(vals.size());
				for (int i = 0; i < vals.size(); i++)
				{
					// only accept ipv4 or ipv6 for now
					if (vals.get(i).length != 6 && vals.get(i).length != 18)
						continue;
					dbl.add(new DBItem((byte[]) vals.get(i)));
				}
			}

			if (dbl != null || nodes != null || nodes6 != null)
			{
				GetPeersResponse resp = new GetPeersResponse(mtid, id, nodes, nodes6, token);
				resp.setPeerItems(dbl);
				return resp;
			}
			DHT.logDebug("No nodes or values in get_peers response");
			return null;
 
		default:
			return null;
		}
	}

	/**
	 * @param map
	 * @return
	 */
	private static MessageBase parseRequest (Map<String, Object> map, RPCServerBase srv) {
		Object vn = map.get(DHTConstants.REQ);
		Map<String, Object> args = (Map<String, Object>) map.get(DHTConstants.ARG);
		if (vn == null || args == null) {
			return null;
		}

		if (!args.containsKey("id")) {
			return null;
		}

		if (!map.containsKey(DHTConstants.TID)) {
			return null;
		}

		byte[] hash = (byte[]) args.get("id");

		if (hash == null || hash.length != Key.SHA1_HASH_LENGTH) {
			return null;
		}

		Key id = new Key(hash);

		byte[] mtid;
		if(map.get(DHTConstants.TID) instanceof byte[]) {
			mtid = (byte[]) map.get(DHTConstants.TID);

			if (mtid == null || mtid.length < 1) {
				return null;
			}
		} else {
			return null;
		}

		MessageBase msg = null;

		String str = getStringFromBytes((byte[]) vn);
		if (str.equalsIgnoreCase("ping")) {
			msg = new PingRequest(id);
		} else if (str.equalsIgnoreCase("find_node")) {
			if (args.containsKey("target")) {
				hash = (byte[]) args.get("target");
				if (hash == null || hash.length != Key.SHA1_HASH_LENGTH) {
					return null;
				}
				
				AbstractLookupRequest req = new FindNodeRequest(id, new Key(hash));
				req.setWant4(srv.getDHT().getType() == DHTtype.IPV4_DHT );
				req.setWant6(srv.getDHT().getType() == DHTtype.IPV6_DHT );
				req.decodeWant((List<byte[]>)args.get("want"));
				
				msg = req;

			}
		} else if (str.equalsIgnoreCase("get_peers")) {
			if (args.containsKey("info_hash")) {
				hash = (byte[]) args.get("info_hash");
				if (hash == null || hash.length != Key.SHA1_HASH_LENGTH) {
					return null;
				}
				
				AbstractLookupRequest req = new GetPeersRequest(id, new Key(hash));
				List<String> want = (List<String>)args.get("want");
				req.setWant4(srv.getDHT().getType() == DHTtype.IPV4_DHT );
				req.setWant6(srv.getDHT().getType() == DHTtype.IPV6_DHT );
				req.decodeWant((List<byte[]>)args.get("want"));
				
				msg = req;
			}
		} else if (str.equalsIgnoreCase("announce_peer")) {
			if (args.containsKey("info_hash") && args.containsKey("port")
					&& args.containsKey("token")) {
				hash = (byte[]) args.get("info_hash");
				if (hash == null || hash.length != Key.SHA1_HASH_LENGTH) {
					return null;
				}
				Key infoHash = new Key(hash);

				byte[] token = (byte[]) args.get("token");

				msg = new AnnounceRequest(id, infoHash, ((Long) args
						.get("port")).intValue(), token);
			}
		} else {
			DHT.logDebug("Received unknown Message Type: " + str);
		}

		if (msg != null) {
			msg.setMTID(mtid);
		}

		return msg;
	}
	
	private static String getStringFromBytes (byte[] bytes, boolean preserveBytes) {
		if (bytes == null) {
			return null;
		}
		try {
			return new String(bytes, preserveBytes ? "ISO-8859-1" : "UTF-8");
		} catch (Exception e) {
			DHT.log(e, LogLevel.Verbose);
			return null;
		}
	}

	private static String getStringFromBytes (byte[] bytes) {
		return getStringFromBytes(bytes, false);
	}
}
