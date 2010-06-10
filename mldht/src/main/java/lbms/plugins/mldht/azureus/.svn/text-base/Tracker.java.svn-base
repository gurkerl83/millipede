package lbms.plugins.mldht.azureus;

import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import lbms.plugins.mldht.kad.AnnounceTask;
import lbms.plugins.mldht.kad.DBItem;
import lbms.plugins.mldht.kad.DHT;
import lbms.plugins.mldht.kad.Task;
import lbms.plugins.mldht.kad.TaskListener;
import lbms.plugins.mldht.kad.DHT.DHTtype;

import org.gudy.azureus2.plugins.download.Download;



import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
import org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer;
import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadListener;
import org.gudy.azureus2.plugins.download.DownloadManagerListener;
import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
import org.gudy.azureus2.plugins.download.DownloadTrackerListener;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

/**
 * @author Damokles
 *
 */
public class Tracker
        implements TaskListener, DownloadListener,
        DownloadAttributeListener, DownloadTrackerListener,
        DownloadManagerListener {

    public static final int MAX_CONCURRENT_ANNOUNCES = 8;
    public static final int MAX_TRACKED_TORRENTS = 30;
    public static final int TRACKER_UPDATE_INTERVAL = 30 * 1000;
    public static final int SHORT_DELAY = 60 * 1000;
    public static final int MIN_ANNOUNCE_INTERVAL = 5 * 60 * 1000;
    //actually MIN is added to this
    public static final int MAX_ANNOUNCE_INTERVAL = 20 * 60 * 1000;
    public static final String PEER_SOURCE_NAME = "DHT"; // DownloadAnnounceResultPeer.PEERSOURCE_DHT;
    private int currentAnnounces = 0;
    private MlDHTPlugin plugin;
    private boolean running;
    private Random random = new Random();
    private ScheduledFuture<?> timer;
    private TorrentAttribute ta_networks;
    private TorrentAttribute ta_peer_sources;
    private Map<Download, TrackedTorrent> trackedTorrents = new HashMap<Download, TrackedTorrent>();
    private Queue<TrackedTorrent> announceQueue = new DelayQueue<TrackedTorrent>();

    protected Tracker(MlDHTPlugin plugin) {
//		this.plugin = plugin;
//        ta_networks = plugin.getPluginInterface().getTorrentManager().getAttribute(
//                TorrentAttribute.TA_NETWORKS);
//        ta_peer_sources = plugin.getPluginInterface().getTorrentManager().getAttribute(
//                TorrentAttribute.TA_PEER_SOURCES);

    }

    protected void start() {
        if (running) {
            return;
        }
        DHT.logInfo("Tracker: starting...");
        System.out.println("Tracker: starting...");
        timer = DHT.getScheduler().scheduleAtFixedRate(new Runnable() {

            public void run() {
                checkAnnounceQueue();
            }
        }, 100 * 1000, TRACKER_UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
//	plugin.getPluginInterface().getDownloadManager().addListener(this);

        running = true;
    }

    protected void stop() {
        if (!running) {
            return;
        }
        DHT.logInfo("Tracker: stopping...");
        if (timer != null) {
            timer.cancel(false);
        }
        announceQueue.clear();
        trackedTorrents.clear();
        plugin.getPluginInterface().getDownloadManager().removeListener(this);
        Download[] downloads = plugin.getPluginInterface().getDownloadManager().getDownloads();
        for (Download dl : downloads) {
//            dl.removeAttributeListener(this, ta_networks,
//                    DownloadAttributeListener.WRITTEN);
//            dl.removeAttributeListener(this, ta_peer_sources,
//                    DownloadAttributeListener.WRITTEN);
            dl.removeListener(this);
            dl.removeTrackerListener(this);
        }

        running = false;
    }

    protected void announceDownload(Download dl) {
        if (running) {
            if (dl.getTorrent() == null) {
                return;
            }

            if (dl.getTorrent().isPrivate()) {
                DHT.logDebug("Announce for [" + dl.getName() + "] forbidden because Torrent is private.");
                return;
            }
            if (trackedTorrents.containsKey(dl)) {
                if (trackedTorrents.get(dl).isAnnouncing()) {
                    DHT.logDebug("Announce for [" + dl.getName() + "] was denied since there is already one running.");
                    return;
                }
            }
            DHT.logInfo("DHT Starting Announce for " + dl.getName());
            if (trackedTorrents.containsKey(dl)) {
                trackedTorrents.get(dl).setAnnouncing(true);
            }
            for (DHTtype type : DHTtype.values()) {
                //					dl.getTorrent().getHash(),
                AnnounceTask at = plugin.getDHT(type).announce(
                        dl.getTorrent().getInfoHash().toByteArray(),
                        plugin.getPluginInterface().getPluginconfig().getUnsafeIntParameter(
                        "TCP.Listen.Port"));
                if (at != null) {
                    currentAnnounces++;
                    at.addListener(this);
                    at.setInfo(dl.getName());
                }
            }
        }
    }

    private void scheduleAnnounce(Download dl, boolean shortDelay) {
        if (!running) {
            return;
        }
        if (trackedTorrents.containsKey(dl)) {
            TrackedTorrent t = trackedTorrents.get(dl);
            if (announceQueue.contains(t)) {
                if (shortDelay) {
                    announceQueue.remove(t);
                }
            }
            int delay = (shortDelay) ? SHORT_DELAY
                    : (MIN_ANNOUNCE_INTERVAL + random.nextInt(MAX_ANNOUNCE_INTERVAL));
            t.setDelay(delay);

            DHT.logInfo("Tracker: scheduled announce in " + t.getDelay(TimeUnit.SECONDS) + "sec for: " + dl.getName());
            announceQueue.add(t);
        }
    }

    private void checkAnnounceQueue() {
        if (!running) {
            return;
        }
        TrackedTorrent t = null;
        if (currentAnnounces < MAX_CONCURRENT_ANNOUNCES) {
            t = announceQueue.poll();
        }

        while (t != null) {
            Download dl = t.getDownload();
            if (trackedTorrents.containsKey(dl) && trackedTorrents.get(dl).isAnnouncing()) {
                scheduleAnnounce(dl, false);
            } else {
                announceDownload(dl);
            }
            if (currentAnnounces < MAX_CONCURRENT_ANNOUNCES) {
                t = announceQueue.poll();
            } else {
                break;
            }
        }
    }

    private boolean checkDownload(Download dl) {
        if (!running || dl.getTorrent() == null || dl.getTorrent().isPrivate()) {
            return false;
        }

//        String[] sources = dl.getListAttribute(ta_peer_sources);
//
//        String[] networks = dl.getListAttribute(ta_networks);

        boolean ok = false;

//        if (networks != null) {
//
//            for (int i = 0; i < networks.length; i++) {
//
//                if (networks[i].equalsIgnoreCase("Public")) {
//
//                    ok = true;
//
//                    break;
//                }
//            }
//        }

        if (!ok) {
            removeTrackedTorrent(dl, "Network is not public anymore");
            return false;
        }

        ok = false;

//        for (int i = 0; i < sources.length; i++) {
//
//            if (sources[i].equalsIgnoreCase(PEER_SOURCE_NAME)) {
//
//                ok = true;
//
//                break;
//            }
//        }

        if (!ok) {
            removeTrackedTorrent(dl, "Peer source was disabled");
            return false;
        }

        if (dl.getState() == Download.ST_DOWNLOADING || dl.getState() == Download.ST_SEEDING) {

            if (trackedTorrents.size() < MAX_TRACKED_TORRENTS) {

                //only act as backup tracker
                if (plugin.getPluginInterface().getPluginconfig().getPluginBooleanParameter(
                        "backupOnly")) {
                    DownloadAnnounceResult result = dl.getLastAnnounceResult();

                    if (result == null || result.getResponseType() == DownloadAnnounceResult.RT_ERROR) {
                        addTrackedTorrent(dl, "BackupTracker");
                        return true;
                    } else {
                        return false;
                    }

                }

                addTrackedTorrent(dl, "Normal");
                return true;
            }

        } else {
            removeTrackedTorrent(dl, "Has stopped Downloading/Seeding");
            return false;
        }

        return false;
    }

    private void addTrackedTorrent(Download dl, String reason) {
        if (!trackedTorrents.containsKey(dl)) {
            DHT.logInfo("Tracker: starting to track Torrent reason: " + reason + ", Torrent; " + dl.getName());
            trackedTorrents.put(dl, new TrackedTorrent(dl));
            scheduleAnnounce(dl, true);
        }
    }

    private void removeTrackedTorrent(Download dl, String reason) {
        if (trackedTorrents.containsKey(dl)) {
            DHT.logInfo("Tracker: stop tracking of Torrent reason: " + reason + ", Torrent; " + dl.getName());
            announceQueue.remove(trackedTorrents.get(dl));
            trackedTorrents.remove(dl);
        }
    }

    public List<TrackedTorrent> getTrackedTorrentList() {
        return new ArrayList<TrackedTorrent>(trackedTorrents.values());
    }

    //---------------------[TaskListener]---------------------------------

    /* (non-Javadoc)
     * @see lbms.plugins.mldht.kad.TaskListener#finished(lbms.plugins.mldht.kad.Task)
     */
    public void finished(Task t) {
        DHT.logDebug("DHT Task done: " + t.getClass().getSimpleName());
        if (t instanceof AnnounceTask) {
            AnnounceTask announce = (AnnounceTask) t;
            Set<DBItem> items = announce.getReturnedItems();

            byte[] infoHash = announce.getInfoHash().getHash();
            try {
                Download dl = plugin.getPluginInterface().getDownloadManager().getDownload(
                        infoHash);

                currentAnnounces--;
                TrackedTorrent tor = trackedTorrents.get(dl);
                if (tor != null) {
                    tor.setAnnouncing(false);
                }
                // schedule the next announce if there is not another one pending
                if (!announceQueue.contains(tor)) {
                    scheduleAnnounce(dl, false);
                }
                if (items.size() > 0) {
                    dl.setAnnounceResult(new DHTAnnounceResult(dl, items));
                }

                DHT.logInfo("DHT Announce finished for " + dl.getName() + " found " + items.size() + " Peers.");

            } catch (DownloadException e) {
            }
        }

    }

    //---------------------[DownloadListener]---------------------------------
	/* (non-Javadoc)
     * @see org.gudy.azureus2.plugins.download.DownloadListener#positionChanged(org.gudy.azureus2.plugins.download.Download, int, int)
     */
    public void positionChanged(Download download, int oldPosition,
            int newPosition) {
    }

    /* (non-Javadoc)
     * @see org.gudy.azureus2.plugins.download.DownloadListener#stateChanged(org.gudy.azureus2.plugins.download.Download, int, int)
     */
    public void stateChanged(Download download, int old_state, int new_state) {
        checkDownload(download);
    }

    //---------------------[DownloadAttributeListener]---------------------------------

    /* (non-Javadoc)
     * @see org.gudy.azureus2.plugins.download.DownloadAttributeListener#attributeEventOccurred(org.gudy.azureus2.plugins.download.Download, org.gudy.azureus2.plugins.torrent.TorrentAttribute, int)
     */
    public void attributeEventOccurred(Download download,
            TorrentAttribute attribute, int event_type) {
//        if (event_type == DownloadAttributeListener.WRITTEN && (attribute == ta_networks || attribute == ta_peer_sources)) {
            checkDownload(download);
//        }

    }

    //---------------------[DownloadTrackerListener]---------------------------------
	/* (non-Javadoc)
     * @see org.gudy.azureus2.plugins.download.DownloadTrackerListener#announceResult(org.gudy.azureus2.plugins.download.DownloadAnnounceResult)
     */
    public void announceResult(DownloadAnnounceResult result) {
        checkDownload(result.getDownload());
    }

    /* (non-Javadoc)
     * @see org.gudy.azureus2.plugins.download.DownloadTrackerListener#scrapeResult(org.gudy.azureus2.plugins.download.DownloadScrapeResult)
     */
    public void scrapeResult(DownloadScrapeResult result) {
        checkDownload(result.getDownload());
    }

    //---------------------[DownloadTrackerListener]---------------------------------
	/* (non-Javadoc)
     * @see org.gudy.azureus2.plugins.download.DownloadManagerListener#downloadAdded(org.gudy.azureus2.plugins.download.Download)
     */
    public void downloadAdded(Download download) {
//        download.addAttributeListener(this, ta_networks,
//                DownloadAttributeListener.WRITTEN);
//        download.addAttributeListener(this, ta_peer_sources,
//                DownloadAttributeListener.WRITTEN);
        download.addListener(this);
        download.addTrackerListener(this);
        checkDownload(download);
    }

    /* (non-Javadoc)
     * @see org.gudy.azureus2.plugins.download.DownloadManagerListener#downloadRemoved(org.gudy.azureus2.plugins.download.Download)
     */
    public void downloadRemoved(Download download) {
//        download.removeAttributeListener(this, ta_networks,
//                DownloadAttributeListener.WRITTEN);
//        download.removeAttributeListener(this, ta_peer_sources,
//                DownloadAttributeListener.WRITTEN);
        download.removeListener(this);
        download.removeTrackerListener(this);
        removeTrackedTorrent(download, "Download was removed");
    }
}
