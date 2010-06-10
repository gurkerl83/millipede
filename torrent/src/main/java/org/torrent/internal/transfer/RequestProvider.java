/**
 * 
 */
package org.torrent.internal.transfer;

import org.torrent.internal.data.BTPart;
import org.torrent.internal.peer.connection.BTConnection;

public interface RequestProvider {
	/**
	 * Invoked to generate a part to request. The returned part should be unique
	 * for the given connection, that is - any given uncanceled part shouldn't
	 * be allocated more than once per connection.
	 * 
	 * @param download
	 * @param con
	 * @return
	 */
	BTPart allocateRequest(Download download, BTConnection con);

	/**
	 * Called to check if a call to allocateRequest would succeed. This method
	 * is called due to performance reasons and is expected to return faster
	 * than allocateRequest.
	 * 
	 * @param download
	 * @param con
	 * @return
	 */
	boolean couldAllocateRequest(Download download, BTConnection con);

	/**
	 * Invoked to cancel a request previously allocated.
	 * 
	 * @param download
	 * @param con
	 * @param pieceInfo
	 */
	void cancelRequest(Download download, BTConnection con, BTPart pieceInfo);

	/**
	 * Cancels all remaining requests of a connection.
	 * 
	 * @param download
	 * @param con
	 */
	void cancelAllRequests(Download download, BTConnection con);
}