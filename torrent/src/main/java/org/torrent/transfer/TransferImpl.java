/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.torrent.transfer;

import org.gudy.azureus2.plugins.download.DownloadActivationEvent;
import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
import org.gudy.azureus2.plugins.download.DownloadListener;
import org.gudy.azureus2.plugins.download.DownloadPeerListener;
import org.gudy.azureus2.plugins.download.DownloadTrackerListener;
import org.gudy.azureus2.plugins.peers.PeerManager;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.io.PieceVerifier;
import org.torrent.internal.transfer.AvailabilityObserver;
import org.torrent.internal.transfer.BTSession;
import org.torrent.internal.transfer.ContentWatcher;
import org.torrent.internal.transfer.TrafficWatcher;
import org.gudy.azureus2.core3.download.*;
import org.gudy.azureus2.plugins.download.Download;
//import org.torrent.internal.transfer.Download;
import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;

/**
 *
 * @author gurkerl
 */
public class TransferImpl implements Transfer {

    private DownloadManager download_manager;
//    private DownloadStatsImpl		download_stats;
    private int latest_state = ST_STOPPED;
    private boolean latest_forcedStart;
    private DownloadActivationEvent activation_state;

//    protected
    public TransferImpl(
            DownloadManager _dm) {
        download_manager = _dm;
//		download_stats		= new DownloadStatsImpl( download_manager );

        activation_state =
                new DownloadActivationEvent() {

                    public Download getDownload() {
                        return (TransferImpl.this);
                    }

                    public int getActivationCount() {
                        return (download_manager.getActivationCount());
                    }
                };

//		download_manager.addListener( this );

        latest_forcedStart = download_manager.isForceStart();
    }

    @Override
    public ContentWatcher getContentWatcher() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BTSession getSession() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TorrentMetaInfo getContentInfo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PieceVerifier getPieceVerifier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TrafficWatcher getTrafficWatcher() {
    	return this.getTrafficWatcher();
    }

    @Override
    public AvailabilityObserver getAvailabilityObserver() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void checkAllPieces() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TorrentMetaInfo getTorrent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isForceStart() {
        return download_manager.isForceStart();
    }

    public void setForceStart(boolean forceStart) {
        download_manager.setForceStart(forceStart);
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getListAttribute(TorrentAttribute attribute) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAnnounceResult(DownloadAnnounceResult result) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DownloadAnnounceResult getLastAnnounceResult() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PeerManager getPeerManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] getDownloadPeerId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isMessagingEnabled() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMessagingEnabled(boolean enabled) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addListener(DownloadListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListener(DownloadListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addTrackerListener(DownloadTrackerListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addTrackerListener(DownloadTrackerListener l, boolean immediateTrigger) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeTrackerListener(DownloadTrackerListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addPeerListener(DownloadPeerListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removePeerListener(DownloadPeerListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addAttributeListener(DownloadAttributeListener l, TorrentAttribute attr, int event_type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeAttributeListener(DownloadAttributeListener l, TorrentAttribute attr, int event_type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
