package org.millipede.download;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.download.DownloadManagerException;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.util.IndentWriter;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
import org.gudy.azureus2.plugins.download.DownloadScrapeResult;

public abstract class AbstractDownloadManager implements DownloadManager {

    public static GlobalManager globalManager;

    public Map<String, Download> actions = new HashMap<String, Download>();

    public Map<String, DownloadManager> manager = new HashMap<String, DownloadManager>();

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getSubState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStateWaiting() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStateQueued() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void startDownload() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canForceRecheck() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void forceRecheck() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPieceCheckingEnabled(boolean enabled) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stopIt(int stateAfterStopping, boolean remove_torrent, boolean remove_data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean pause() {
        return( globalManager.pauseDownload( this ));
    }

    @Override
    public boolean isPaused() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resume() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GlobalManager getGlobalManager() {
        return globalManager;
    }

    @Override
    public TOTorrent getTorrent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void requestTrackerAnnounce(boolean immediate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void requestTrackerScrape(boolean immediate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDisplayName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getInternalName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getTorrentFileName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTorrentFileName(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public File getAbsoluteSaveLocation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public File getSaveLocation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTorrentSaveDir(String sPath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTorrentSaveDir(String parent_dir, String dl_name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isForceStart() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setForceStart(boolean forceStart) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isPersistent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDownloadComplete(boolean bIncludingDND) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getTrackerStatus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTrackerTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getTorrentComment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getTorrentCreatedBy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getTorrentCreationDate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNbPieces() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getPieceLength() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNbSeeds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNbPeers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean filesExist() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean filesExist(boolean expected_to_be_allocated) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getErrorDetails() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getPosition() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPosition(int newPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getAssumedComplete() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean requestAssumedCompleteMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHealthStatus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNATStatus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveResumeData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveDownload() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getData(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setData(String key, Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getUserData(Object key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setUserData(Object key, Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDataAlreadyAllocated() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDataAlreadyAllocated(boolean already_allocated) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSeedingRank(int rank) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getSeedingRank() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setMaxUploads(int max_slots) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMaxUploads() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getEffectiveMaxUploads() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getEffectiveUploadRateLimitBytesPerSecond() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCryptoLevel(int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getCryptoLevel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void moveDataFiles(File new_parent_dir) throws DownloadManagerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void renameDownload(String new_name) throws DownloadManagerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void moveDataFiles(File new_parent_dir, String new_name) throws DownloadManagerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void moveTorrentFile(File new_parent_dir) throws DownloadManagerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isInDefaultSaveDir() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getCreationTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCreationTime(long t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAnnounceResult(DownloadAnnounceResult result) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setScrapeResult(DownloadScrapeResult result) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isUnauthorisedOnTracker() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isTrackerError() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isExtendedMessagingEnabled() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAZMessagingEnabled(boolean enable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void destroy(boolean is_duplicate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDestroyed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean seedPieceRecheck() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addListener(org.gudy.azureus2.core3.download.DownloadManagerListener listener, boolean triggerStateChange) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addListener(org.gudy.azureus2.core3.download.DownloadManagerListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListener(org.gudy.azureus2.core3.download.DownloadManagerListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getActivationCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void generateEvidence(IndentWriter writer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canMoveDataFiles() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rename(String new_name) throws DownloadManagerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void renameTorrent(String new_name) throws DownloadManagerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void renameTorrentSafe(String name) throws DownloadManagerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void moveTorrentFile(File parent_dir, String new_name) throws DownloadManagerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTorrentFile(File new_parent_dir, String new_name) throws DownloadManagerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
