package org.torrent.internal.peer.connection;

import java.io.IOException;
import java.util.Collection;

import org.torrent.internal.data.BTPart;
import org.torrent.internal.util.Bits;

/**
 * @author dante
 * 
 */
public interface BTConnection extends BTMessageSender {

	Collection<BTPart> getPendingRequests();

	BTPeerStatus getRemotePeerStatus();

	/**
	 * Chokes or unchokes the remote side. If set to choking, will forget any
	 * requests made by the remote side.
	 * 
	 * @param choke
	 * @throws IOException
	 */
	void sendChoked(boolean choke);

	void sendInterested(boolean interested);

	/**
	 * Removes all pending requests.
	 */
	void purgePendingRequests();

	/**
	 * Returns a request. The request is then removed from the internal queue.
	 * 
	 * @return null, or a PieceInfo representing a pending request
	 */
	BTPart takeRemoteRequest();

	BTPeerStatus getMyPeerStatus();

	/**
	 * Returns whether a completely handshaked connection is available. Sending
	 * messages before this returns true most likely results in breaking the
	 * connection.
	 * 
	 * @return true, if this source is connected and handshaking has been
	 *         performed
	 */
	boolean isConnectionEstablished();

	/**
	 * @return true, if this source is connected
	 */
	boolean isConnected();

	Bits getRemoteBitField();

	void close() throws IOException;

}
