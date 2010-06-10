package lbms.plugins.mldht.kad;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

/**
 * @author Damokles
 *
 */
public class DHTConstants {

	/// Each item may only exist for 30 minutes
	public static final int		DHT_UPDATE_INTERVAL						= 1000;
	public static final int		MAX_ITEM_AGE							= 60 * 60 * 1000;
	public static final int		MAX_ENTRIES_PER_BUCKET					= 8;
	public static final int		MAX_ACTIVE_TASKS						= 7;
	public static final int		MAX_ACTIVE_CALLS						= 256;
	public static final int		MAX_PENDING_ENTRIES_PER_BUCKET			= 5;
	public static final int		BUCKET_REFRESH_INTERVAL					= 15 * 60 * 1000;
	public static final int		MAX_CONCURRENT_REQUESTS					= 10;
	public static final int		RECEIVE_BUFFER_SIZE						= 5 * 1024;
	public static final int		CHECK_FOR_EXPIRED_ENTRIES				= 5 * 60 * 1000;
	public static final int		RPC_CALL_TIMEOUT						= 10 * 1000;
	public static final int		TASK_TIMEOUT							= 2 * 60 * 1000;

	public static final long	KBE_QUESTIONABLE_TIME					= 15 * 60 * 1000;
	public static final int		KBE_BAD_IF_FAILED_QUERIES_LARGER_THAN	= 2;
	public static final int		KBE_BAD_IMMEDIATLY_ON_FAILED_QUERIES	= 8;
	public static final int		KBE_QUESTIONABLE_TIME_PING_MULTIPLIER	= 4;

	public static final	int		BOOTSTRAP_MIN_INTERVAL					= 4 * 60 * 1000;
	public static final int		BOOTSTRAP_IF_LESS_THAN_X_PEERS			= 30;
	public static final int		USE_BT_ROUTER_IF_LESS_THAN_X_PEERS		= 10;

	public static final int		DEFAULT_WANTED_NODE_RESPONSES_ON_NL		= MAX_ENTRIES_PER_BUCKET * 4;

	public static final int		SELF_LOOKUP_INTERVAL					= 30 * 60 * 1000;
	public static final int		RANDOM_LOOKUP_INTERVAL					= 10 * 60 * 1000;

	public static final int		CACHE_REVALIDATION_TIME					= 10 * 60 * 1000;

	/**
	 * MessageID (t)
	 */
	public static final String	TID										= "t";
	/**
	 * MessageTypeKey (y)
	 */
	public static final String	TYP										= "y";
	/**
	 * MessageType Request (q)
	 */
	public static final String	REQ										= "q";
	/**
	 * MessageType Response (r)
	 */
	public static final String	RSP										= "r";
	/**
	 * MessageType Error (e) also used as ErrorDetailsKey
	 */
	public static final String	ERR										= "e";
	/**
	 * ArgumentsKey (a)
	 */
	public static final String	ARG										= "a";
	/**
	 * VersionKey (v)
	 */
	public static final String	VER										= "v";

	public static final String[]			BOOTSTRAP_NODES							= new String[]	{ "mldht.wifi.pps.jussieu.fr", 	"router.bittorrent.com" };
	public static final int[]				BOOTSTRAP_PORTS							= new int[]		{ 6881, 						6881 };
	public static List<InetSocketAddress>	BOOTSTRAP_NODE_ADDRESSES				= Collections.EMPTY_LIST;
	private static String version = "AZ0";

	public static String getVersion() {
		return version;
	}

	public static void setVersion (int ver) {
		version = "Az" + new String(new byte[] { (byte) (ver >> 8), (byte) (ver & 0xff) });
	}

}
