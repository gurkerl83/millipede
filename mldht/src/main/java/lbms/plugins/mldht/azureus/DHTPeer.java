package lbms.plugins.mldht.azureus;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;

import lbms.plugins.mldht.kad.DBItem;

import org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer;

/**
 * @author Damokles
 *
 */
public class DHTPeer implements DownloadAnnounceResultPeer {

	private static final String	PEER_SOURCE	= "DHT";

	private String				addr = "localhost";
	private int					port = 0;

	protected DHTPeer (DBItem item) {
		byte[] itemData = item.getData();
		try
		{
			if (itemData.length == 4 + 2)
			{ // ipv4
				addr = InetAddress.getByAddress(Arrays.copyOf(itemData, 4)).getHostAddress();
				port = (itemData[4] & 0xFF) << 8 | (itemData[5] & 0xFF);
			} else if (itemData.length == 16 + 2)
			{ // ipv6
				addr = InetAddress.getByAddress(Arrays.copyOf(itemData, 16)).getHostAddress();
				port = (itemData[16] & 0xFF) << 8 | (itemData[17] & 0xFF);
			}
		} catch (Exception e)
		{
			// should not happen
		}
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer#getAddress()
	 */
	public String getAddress () {
		return addr;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer#getPeerID()
	 */
	public byte[] getPeerID () {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer#getPort()
	 */
	public int getPort () {
		return port;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer#getProtocol()
	 */
	public short getProtocol () {
		return DownloadAnnounceResultPeer.PROTOCOL_NORMAL;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer#getSource()
	 */
	public String getSource () {
		return PEER_SOURCE;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer#getUDPPort()
	 */
	public int getUDPPort () {
		return 0;
	}

}
