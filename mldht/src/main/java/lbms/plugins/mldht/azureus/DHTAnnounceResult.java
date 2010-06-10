package lbms.plugins.mldht.azureus;

import java.net.URL;
import java.util.Collection;
import java.util.Map;

import lbms.plugins.mldht.kad.DBItem;

import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
import org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer;


/**
 * @author Damokles
 *
 */
public class DHTAnnounceResult implements DownloadAnnounceResult {

	private Download						dl;
	private Collection<DBItem>				peers;
	private DownloadAnnounceResultPeer[]	resultPeers;

	public DHTAnnounceResult (Download dl, Collection<DBItem> peers) {
		this.dl = dl;
		this.peers = peers;
	}

	/**
	 * Converts the DBItems into DHTPeers
	 */
	private void convertPeers () {
		resultPeers = new DownloadAnnounceResultPeer[peers.size()];
		
		
		int i = 0;		
		for(DBItem it : peers)
			resultPeers[i++] = new DHTPeer(it);
		
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResult#getDownload()
	 */
	public Download getDownload () {
		return dl;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResult#getError()
	 */
	public String getError () {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResult#getExtensions()
	 */
	public Map getExtensions () {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResult#getNonSeedCount()
	 */
	public int getNonSeedCount () {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResult#getPeers()
	 */
	public DownloadAnnounceResultPeer[] getPeers () {
		if (resultPeers == null) {
			convertPeers();
		}
		return resultPeers;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResult#getReportedPeerCount()
	 */
	public int getReportedPeerCount () {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResult#getResponseType()
	 */
	public int getResponseType () {
		return DownloadAnnounceResult.RT_SUCCESS;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResult#getSeedCount()
	 */
	public int getSeedCount () {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResult#getTimeToWait()
	 */
	public long getTimeToWait () {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.gudy.azureus2.plugins.download.DownloadAnnounceResult#getURL()
	 */
	public URL getURL () {
		return null;
	}

}
