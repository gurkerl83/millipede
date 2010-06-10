package lbms.plugins.mldht.kad;

import java.net.InetSocketAddress;

import lbms.plugins.mldht.kad.messages.MessageBase;

/**
 * @author Damokles
 *
 */
public interface RPCServerBase {

	public void start ();

	public void stop ();

	/**
	 * Do a RPC call.
	 * @param msg The message to send
	 * @return The call object
	 */
	public RPCCall doCall (MessageBase msg);

	/**
	 * Send a message, this only sends the message, it does not keep any call
	 * information. This should be used for replies.
	 * @param msg The message to send
	 */
	public void sendMessage (MessageBase msg);

	/**
	 * A call was timed out.
	 * @param mtid mtid of call
	 */
	public void timedOut (byte[] mtid);

	/**
	 * Ping a node, we don't care about the MTID.
	 * @param addr The address
	 */
	public void ping (Key our_id, InetSocketAddress addr);

	/**
	 * Find a RPC call, based on the mtid
	 * @param mtid The mtid
	 * @return The call
	 */
	public RPCCallBase findCall (byte[] mtid);

	/// Get the number of active calls
	public int getNumActiveRPCCalls ();

	public int getNumReceived ();

	public int getNumSent ();

	public RPCStats getStats ();

	public DHT getDHT();
}