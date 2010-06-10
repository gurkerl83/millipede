package org.gudy.azureus2.core3.global.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.download.DownloadManagerFactory;
import org.gudy.azureus2.core3.download.DownloadManagerInitialisationAdapter;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.core3.global.GlobalManagerDownloadRemovalVetoException;
import org.gudy.azureus2.core3.global.GlobalManagerDownloadWillBeRemovedListener;
import org.gudy.azureus2.core3.global.GlobalManagerListener;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.torrent.TOTorrentException;
import org.gudy.azureus2.core3.util.AEMonitor;
import org.gudy.azureus2.core3.util.Constants;
import org.gudy.azureus2.core3.util.FileUtil;
import org.gudy.azureus2.core3.util.HashWrapper;
import org.gudy.azureus2.core3.util.SystemTime;
import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider;
import org.gudy.azureus2.plugins.download.Download;
import org.merapi.helper.messages.DLControlMessage;
import org.millipede.download.AbstractDownloadManager;
import org.millipede.download.TestEventHandler;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.torrent.data.ProcessInfo;
import org.torrent.internal.client.Client;
import org.torrent.internal.data.MetaInfoFile;
import org.torrent.transfer.Transfer;

public class GlobalManagerImpl implements GlobalManager, BundleActivator {
//	private Map<String, String> statusMap = new HashMap<String, String>();

    private Map<String, MetaInfoFile> map = new HashMap<String, MetaInfoFile>();
    private Map<String, ProcessInfo> statusMap = new HashMap<String, ProcessInfo>();
    private Map<String, AbstractDownloadManager> dlmanager = new HashMap<String, AbstractDownloadManager>();
    private static ExecutorService executor = new ScheduledThreadPoolExecutor(10);
    Future<Transfer> future;
    /** Whether loading of existing torrents is done */
    boolean loadingComplete = false;
    private volatile boolean needsSaving = false;
    private List<DownloadManager> managers_cow = new ArrayList<DownloadManager>();
    private AEMonitor managers_mon = new AEMonitor("GM:Managers");
    private static GlobalManager instance = new GlobalManagerImpl();

    /**
     * Statische Methode, liefert die einzige Instanz dieser Klasse zur�ck
     */
    public static GlobalManager getInstance() {
        return instance;
    }

    /**
     * @return the map
     */
    public Map<String, MetaInfoFile> getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(Map<String, MetaInfoFile> map) {
        this.map = map;
    }

    /**
     * @return the statusMap
     */
    public Map<String, ProcessInfo> getStatusMap() {
        return statusMap;
    }

    /**
     * @param statusMap the statusMap to set
     */
    public void setStatusMap(Map<String, ProcessInfo> statusMap) {
        this.statusMap = statusMap;
    }

    /**
     * @return the dlmanager
     */
    public Map<String, AbstractDownloadManager> getDlmanager() {
        return dlmanager;
    }

    /**
     * @param dlmanager the dlmanager to set
     */
    public void setDlmanager(Map<String, AbstractDownloadManager> dlmanager) {
        this.dlmanager = dlmanager;
    }
    private BundleContext bctx;
    private ServiceRegistration reg;
    private ServiceRegistration global;

    @Override
    public void start(BundleContext context) throws Exception {
        global = context.registerService(GlobalManager.class.getName(), new GlobalManagerImpl(), null);
        loadDownloads();

        ServiceReference gmRef = context.getServiceReference(GlobalManager.class.getName());
        GlobalManager gm = (GlobalManager) context.getService(gmRef);

        bctx = context;
        Dictionary props = new Hashtable();
//    		props.put(EventConstants.EVENT_TOPIC, "es/schaaf/*");
        props.put(EventConstants.EVENT_TOPIC, DLControlMessage.DL_CONTROL + "/*");
//    		props.put(EventConstants.EVENT_TOPIC, DLControlRespondMessage.DL_CONTROL_RESPOND+"/*");
        reg = bctx.registerService(EventHandler.class.getName(), new TestEventHandler(gm), props);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        stopGlobalManager();
        global.unregister();
        reg.unregister();
    }

    @Override
    public void addDownloadManager(String fileName, AbstractDownloadManager dlmanager) {

        if ((getDownloadManager(fileName) == null) && (dlmanager == null)) {

            DownloadManager new_manager = DownloadManagerFactory.create(this);
            this.getDlmanager().put(fileName, (AbstractDownloadManager) new_manager);

            //Provider external DownloadManager created by another DownloadManagerFactory
        } else if ((getDownloadManager(fileName) == null) && (dlmanager != null)) {
            this.getDlmanager().put(fileName, dlmanager);
        }
    }

    @Override
    public Client addDownloadManager(String fileName) {
        DownloadManager new_manager = DownloadManagerFactory.create(this, fileName);
        this.getDlmanager().put(fileName, (AbstractDownloadManager) new_manager);
        return (Client) new_manager;
    }

    @Override
    public AbstractDownloadManager getDownloadManager(String fileName) {
        return this.getDlmanager().get(fileName);
    }

    @Override
    public DownloadManager addDownloadManager(String fileName, String savePath) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DownloadManager addDownloadManager(String fileName,
            byte[] optionalHash, String savePath, int initialState,
            boolean persistent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DownloadManager addDownloadManager(String fileName,
            byte[] optionalHash, String savePath, String saveFile,
            int initialState, boolean persistent, boolean forSeeding,
            DownloadManagerInitialisationAdapter adapter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DownloadManager addDownloadManager(String fileName,
            byte[] optionalHash, String savePath, int initialState,
            boolean persistent, boolean forSeeding,
            DownloadManagerInitialisationAdapter adapter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addDownloadWillBeRemovedListener(
            GlobalManagerDownloadWillBeRemovedListener l) {
        // TODO Auto-generated method stub
    }

    @Override
    public void addListener(GlobalManagerListener l) {
        // TODO Auto-generated method stub
    }

    @Override
    public MainlineDHTProvider getMainlineDHTProvider() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean pauseDownload(DownloadManager dm) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void pauseDownloads() {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeDownloadManager(DownloadManager dm)
            throws GlobalManagerDownloadRemovalVetoException {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeDownloadWillBeRemovedListener(
            GlobalManagerDownloadWillBeRemovedListener l) {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeListener(GlobalManagerListener l) {
        // TODO Auto-generated method stub
    }

    @Override
    public void resumeDownload(DownloadManager dm) {
        resumeDownload((Client) dm);
    }

    private Future<Transfer> resumeDownload(Client dm) {
        future = executor.submit(dm);
        return future;
    }

    @Override
    public void resumeDownloads() {

        Collection<AbstractDownloadManager> c = this.dlmanager.values();
        //obtain an Iterator for Collection
        Iterator itr = c.iterator();
        //iterate through HashMap values iterator
        while (itr.hasNext()) {
            try {
                ((Client) itr.next()).call();
            } catch (IOException ex) {
                Logger.getLogger(GlobalManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(GlobalManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(GlobalManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void setMainlineDHTProvider(MainlineDHTProvider provider) {
        // TODO Auto-generated method stub
    }

    private void loadDownloads() {
        try {

            ArrayList downloadsAdded = new ArrayList();

            try {

                Map map = FileUtil.readResilientConfigFile("downloads.config");

                boolean debug = Boolean.getBoolean("debug");

                Iterator iter = null;
                // v2.0.3.0+ vs older mode
                List downloads = (List) map.get("downloads");
                int nbDownloads;
                if (downloads == null) {
                    // No downloads entry, then use the old way
                    iter = map.values().iterator();
                    nbDownloads = map.size();
                } else {
                    // New way, downloads stored in a list
                    iter = downloads.iterator();
                    nbDownloads = downloads.size();
                }
                int currentDownload = 0;
                while (iter.hasNext()) {
                    currentDownload++;
                    Map mDownload = (Map) iter.next();
                    try {
                        byte[] torrent_hash = (byte[]) mDownload.get("torrent_hash");

                        Long lPersistent = (Long) mDownload.get("persistent");

                        boolean persistent = lPersistent == null
                                || lPersistent.longValue() == 1;

                        String fileName = new String((byte[]) mDownload.get("torrent"), Constants.DEFAULT_ENCODING);

                        // if(progress_listener != null &&
                        // SystemTime.getCurrentTime() - lastListenerUpdate >
                        // 100) {
                        // lastListenerUpdate = SystemTime.getCurrentTime();
                        //
                        // String shortFileName = fileName;
                        // try {
                        // File f = new File(fileName);
                        // shortFileName = f.getName();
                        // } catch (Exception e) {
                        // // TODO: handle exception
                        // }
                        //
                        // progress_listener.reportPercent(100 * currentDownload
                        // / nbDownloads);
                        // //
                        // progress_listener.reportCurrentTask(MessageText.getString("splash.loadingTorrent")
                        // // + " " + currentDownload + " "
                        // // + MessageText.getString("splash.of") + " " +
                        // nbDownloads
                        // // + " : " + shortFileName );
                        // }

                        // migration from using a single savePath to a separate
                        // dir and file entry
                        String torrent_save_dir;
                        String torrent_save_file;

                        byte[] torrent_save_dir_bytes = (byte[]) mDownload.get("save_dir");

                        if (torrent_save_dir_bytes != null) {

                            byte[] torrent_save_file_bytes = (byte[]) mDownload.get("save_file");

                            torrent_save_dir = new String(
                                    torrent_save_dir_bytes,
                                    Constants.DEFAULT_ENCODING);

                            if (torrent_save_file_bytes != null) {

                                torrent_save_file = new String(
                                        torrent_save_file_bytes,
                                        Constants.DEFAULT_ENCODING);
                            } else {

                                torrent_save_file = null;
                            }
                        } else {

                            byte[] savePathBytes = (byte[]) mDownload.get("path");
                            torrent_save_dir = new String(savePathBytes,
                                    Constants.DEFAULT_ENCODING);
                            torrent_save_file = null;
                        }

                        int state = DownloadManager.STATE_WAITING;
                        // if (debug){
                        //
                        // state = DownloadManager.STATE_STOPPED;
                        //
                        // }else {
                        //
                        if (mDownload.containsKey("state")) {
                            state = ((Long) mDownload.get("state")).intValue();
                            // if (state != DownloadManager.STATE_STOPPED &&
                            // state != DownloadManager.STATE_QUEUED &&
                            // state != DownloadManager.STATE_WAITING)
                            //
                            // state = DownloadManager.STATE_QUEUED;
                        }
                        // }else{
                        //
                        // int stopped = ((Long)
                        // mDownload.get("stopped")).intValue();
                        //
                        // if (stopped == 1){
                        //
                        // state = DownloadManager.STATE_STOPPED;
                        // }
                        // }
                        // }
                        //
                        Long seconds_downloading = (Long) mDownload.get("secondsDownloading");

                        boolean has_ever_been_started = seconds_downloading != null
                                && seconds_downloading.longValue() > 0;
                        //
                        // if (torrent_hash != null) {
                        // saved_download_manager_state.put(new
                        // HashWrapper(torrent_hash),
                        // mDownload);
                        // }

                        // for non-persistent downloads the state will be picked
                        // up if the download is re-added
                        // it won't get saved unless it is picked up, hence dead
                        // data is dropped as required

                        if (persistent) {

                            List file_priorities = (List) mDownload.get("file_priorities");

                            final DownloadManager dm = DownloadManagerFactory.create(this, torrent_hash, fileName,
                                    torrent_save_dir,
                                    torrent_save_file, state, true,
                                    true, has_ever_been_started,
                                    file_priorities);

                            // if (addDownloadManager(dm, false, false) == dm) {
                            // downloadsAdded.add(dm);
                            //
                            // if (downloadsAdded.size() >= triggerOnCount) {
                            // triggerOnCount *= 2;
                            // triggerAddListener(downloadsAdded);
                            // downloadsAdded.clear();
                            // }
                            // }
                        }
                    } catch (UnsupportedEncodingException e1) {
                        // Do nothing and process next.
                    } catch (Throwable e) {
                        // Logger.log(new LogEvent(LOGID,
                        // "Error while loading downloads.  " +
                        // "One download may not have been added to the list.",
                        // e));
                    }
                }

                // // This is set to true by default, but once the downloads
                // have been loaded, we have no reason to ever
                // // to do this check again - we only want to do it once to
                // upgrade the state of existing downloads
                // // created before this code was around.
                // COConfigurationManager.setParameter("Set Completion Flag For Completed Downloads On Start",
                // false);
                //
                // //load pause/resume state
                // ArrayList pause_data = (ArrayList)map.get( "pause_data" );
                // if( pause_data != null ) {
                // try { paused_list_mon.enter();
                // for( int i=0; i < pause_data.size(); i++ ) {
                // Object pd = pause_data.get(i);
                //
                // byte[] key;
                // boolean force;
                //
                // if ( pd instanceof byte[]){
                // // old style, migration purposes
                // key = (byte[])pause_data.get( i );
                // force = false;
                // }else{
                // Map m = (Map)pd;
                //
                // key = (byte[])m.get("hash");
                // force = ((Long)m.get("force")).intValue() == 1;
                // }
                // paused_list.add( new Object[]{ new HashWrapper( key ), new
                // Boolean( force )} );
                // }
                // }
                // finally { paused_list_mon.exit(); }
                // }

                // Someone could have mucked with the config file and set weird
                // positions,
                // so fix them up.
                // fixUpDownloadManagerPositions();
                // Logger.log(new LogEvent(LOGID, "Loaded " +
                // managers_cow.size()
                // + " torrents"));

            } catch (Throwable e) {
                // there's been problems with corrupted download files stopping
                // AZ from starting
                // added this to try and prevent such foolishness
                // Debug.printStackTrace( e );
            } finally {
                loadingComplete = true;
                // triggerAddListener(downloadsAdded);

                // loadingSem.releaseForever();
            }

        } finally {
            // DownloadManagerStateFactory.discardGlobalStateCache();
        }
    }

    protected void saveDownloads(boolean immediate) {
        // if ( !immediate ){
        //
        // needsSaving = true;
        //
        // return;
        // }
        //
        // if (!loadingComplete) {
        // needsSaving = true;
        // return;
        // }

        // if(Boolean.getBoolean("debug")) return;

        needsSaving = false;
        // if (this.cripple_downloads_config) {
        // return;
        // }

        try {
            managers_mon.enter();

            Collections.sort(managers_cow, new Comparator() {

                public final int compare(Object a, Object b) {
                    return ((DownloadManager) a).getPosition()
                            - ((DownloadManager) b).getPosition();
                }
            });

            // if (Logger.isEnabled())
            // Logger.log(new LogEvent(LOGID, "Saving Download List ("
            // + managers_cow.size() + " items)"));
            Map map = new HashMap();
            List list = new ArrayList(managers_cow.size());
            for (int i = 0; i < managers_cow.size(); i++) {
                DownloadManager dm = (DownloadManager) managers_cow.get(i);

                // DownloadManagerStats dm_stats = dm.getStats();
                Map dmMap = new HashMap();
                TOTorrent torrent = dm.getTorrent();

                if (torrent != null) {
                    try {
                        dmMap.put("torrent_hash", torrent.getHash());

                    } catch (TOTorrentException e) {
                        // Debug.printStackTrace(e);
                    }
                }

                File save_loc = dm.getAbsoluteSaveLocation();
                dmMap.put("persistent", new Long(dm.isPersistent() ? 1 : 0));
                dmMap.put("torrent", dm.getTorrentFileName());
                dmMap.put("save_dir", save_loc.getParent());
                dmMap.put("save_file", save_loc.getName());

                // dmMap.put("maxdl", new Long(
                // dm_stats.getDownloadRateLimitBytesPerSecond() ));
                // dmMap.put("maxul", new Long(
                // dm_stats.getUploadRateLimitBytesPerSecond() ));

                int state = dm.getState();

                // if (state == DownloadManager.STATE_ERROR ){
                //
                // // torrents in error state always come back stopped
                //
                // state = DownloadManager.STATE_STOPPED;
                //
                // }else if ( dm.getAssumedComplete() && !dm.isForceStart() &&
                // state != DownloadManager.STATE_STOPPED) {
                //
                // state = DownloadManager.STATE_QUEUED;
                //
                // }else if ( state != DownloadManager.STATE_STOPPED &&
                // state != DownloadManager.STATE_QUEUED &&
                // state != DownloadManager.STATE_WAITING){
                //
                // state = DownloadManager.STATE_WAITING;
                //
                // }

                dmMap.put("state", new Long(state));
                dmMap.put("position", new Long(dm.getPosition()));
                // dmMap.put("downloaded", new
                // Long(dm_stats.getTotalDataBytesReceived()));
                // dmMap.put("uploaded", new
                // Long(dm_stats.getTotalDataBytesSent()));
                // dmMap.put("completed", new
                // Long(dm_stats.getDownloadCompleted(true)));
                // dmMap.put("discarded", new Long(dm_stats.getDiscarded()));
                // dmMap.put("hashfailbytes", new
                // Long(dm_stats.getHashFailBytes()));
                // dmMap.put("forceStart", new Long(dm.isForceStart() &&
                // (dm.getState() != DownloadManager.STATE_CHECKING) ? 1 : 0));
                // dmMap.put("secondsDownloading", new
                // Long(dm_stats.getSecondsDownloading()));
                // dmMap.put("secondsOnlySeeding", new
                // Long(dm_stats.getSecondsOnlySeeding()));

                // although this has been migrated, keep storing it to allow
                // regression for a while
                dmMap.put("uploads", new Long(dm.getMaxUploads()));

                dmMap.put("creationTime", new Long(dm.getCreationTime()));

                // save file priorities

                dm.saveDownload();

                List file_priorities = (List) dm.getData("file_priorities");
                if (file_priorities != null) {
                    dmMap.put("file_priorities", file_priorities);
                }

                dmMap.put("allocated", new Long(
                        dm.isDataAlreadyAllocated() == true ? 1 : 0));

                list.add(dmMap);
            }

            map.put("downloads", list);

            // save pause/resume state
            // try { paused_list_mon.enter();
            // if( !paused_list.isEmpty() ) {
            // ArrayList pause_data = new ArrayList();
            // for( int i=0; i < paused_list.size(); i++ ) {
            // Object[] data = (Object[])paused_list.get(i);
            //
            // HashWrapper hash = (HashWrapper)data[0];
            // Boolean force = (Boolean)data[1];
            //
            // Map m = new HashMap();
            //
            // m.put( "hash", hash.getHash());
            // m.put( "force", new Long(force.booleanValue()?1:0));
            //
            // pause_data.add( m );
            // }
            // map.put( "pause_data", pause_data );
            // }
            // }
            // finally { paused_list_mon.exit(); }

            FileUtil.writeResilientConfigFile("downloads.config", map);
        } finally {

            managers_mon.exit();
        }
    }

    /*
     * Puts GlobalManager in a stopped state. Used when closing down Azureus.
     */
    public void stopGlobalManager() {
//		try {
//			managers_mon.enter();
//
//			if (isStopping) {
//
//				return;
//			}
//
//			isStopping = true;
//
//		} finally {
//
//			managers_mon.exit();
//		}

        // stats.save();
        //
        // informDestroyInitiated();
        //
        // if ( host_support != null ){
        // host_support.destroy();
        // }
        //
        // torrent_folder_watcher.destroy();

        // kick off a non-daemon task. This will ensure that we hang around
        // for at least LINGER_PERIOD to run other non-daemon tasks such as
        // writing
        // torrent resume data...

        // try{
        // NonDaemonTaskRunner.run(
        // new NonDaemonTask()
        // {
        // public Object
        // run()
        // {
        // return( null );
        // }
        //
        // public String
        // getName()
        // {
        // return( "Stopping global manager" );
        // }
        // });
        // }catch( Throwable e ){
        // Debug.printStackTrace( e );
        // }

//		checker.stopIt();

//		if (COConfigurationManager
//				.getBooleanParameter("Pause Downloads On Exit")) {
//
//			pauseDownloads(true);
//
//			// do this before save-downloads so paused state gets saved
//
//			stopAllDownloads(true);
//
//			saveDownloads(true);
//
//		} else {

        saveDownloads(true);

//			stopAllDownloads(true);
//		}

        // if ( stats_writer != null ){
        //
        // stats_writer.destroy();
        // }

        // DownloadManagerStateFactory.saveGlobalStateCache();

        managers_cow = new ArrayList();

//		manager_map.clear();

//		informDestroyed();
    }
}
