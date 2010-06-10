package org.torrent.internal.client;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.RouterException;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadEventNotifier;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.download.DownloadManagerListener;
import org.gudy.azureus2.plugins.download.DownloadManagerStats;
import org.gudy.azureus2.plugins.download.DownloadWillBeAddedListener;
//import org.torrent.internal.transfer.Download;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.download.savelocation.DefaultSaveLocationManager;
import org.gudy.azureus2.plugins.download.savelocation.SaveLocationManager;
import org.gudy.azureus2.plugins.torrent.Torrent;
import org.merapi.BridgeService;
import org.merapi.helper.handlers.BarUpdateRequestHandler;
import org.merapi.helper.handlers.ConfigurationRequestHandler;
import org.merapi.helper.handlers.DLControlRequestHandler;
import org.merapi.helper.handlers.DLControlRequestHandler_;
import org.merapi.helper.messages.BarUpdateRequestMessage;
import org.merapi.helper.messages.BarUpdateRespondMessage;
import org.merapi.helper.messages.DLControlMessage;
import org.merapi.helper.messages.DLControlRespondMessage;
import org.merapi.internal.Bridge;
import org.milipede.portmapper.PortMapperService;
import org.milipede.portmapper.PortMapper;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.torrent.client.MainService;
import org.torrent.data.ProcessInfo;
import org.torrent.internal.data.MetaInfoFile;
import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.transfer.Transfer;

import net.sbbi.upnp.messages.ActionResponse;

//import org.millipede.download.AbstractDownloadManager;


public class Main  implements BundleActivator {
//	extends AbstractDownloadManager
//	implements MainService,
    //private BridgeService bridge;
    //private DLControlService controlHandler;
    
//    public void setDinnerService(DLControlService ds) {
//    	controlHandler = ds;
//    }

	
	
	private static Main instance = new Main();
	public static EventGenerator eg;

	/**
	 * Statische Methode, liefert die einzige Instanz dieser Klasse zur�ck
	 */
	public static Main getInstance() {
		return instance;
	}

	private File file;

	 private MainService service;

//	/**
//	 * Default-Konstruktor, der nicht au�erhalb dieser Klasse aufgerufen werden
//	 * kann
//	 */
//	public Main() {
////		service = new Main_();
//	}
	
	 
	public Map<String, MetaInfoFile> map = new HashMap<String, MetaInfoFile>();
//	private Map<String, String> statusMap = new HashMap<String, String>();
	private Map<String, ProcessInfo> statusMap = new HashMap<String, ProcessInfo>();

	
	public Map<String, ProcessInfo> getStatusMap() {
		return statusMap;
	}

	public void setStatusMap(Map<String, ProcessInfo> statusMap) {
		this.statusMap = statusMap;
	}

	public static ConcurrentMap<String, Callable<?>> queue = new ConcurrentHashMap<String, Callable<?>>();
	
	//Executors.newFixedThreadPool(2);
	private static ExecutorService executor = new ScheduledThreadPoolExecutor(10);
//	static Future<Download> future;
	static Future<Transfer> future;
	
//	public static Future<Download> getFuture() {
//		return future;
//	}
//
//	public static void setFuture(Future<Download> future) {
//		Main.future = future;
//	}

	public static Future<Transfer> getFuture() {
		return future;
	}

	public static void setFuture(Future<Transfer> future) {
		Main.future = future;
	}

	/**
	 * @param args
	 */
	public static void main(String [] args) {
		// TODO Auto-generated method stub

        //addTorrentTask("DJ Serm  Southern Alliance Mixtape Present  City Lights Vol1  Samhoody.com [mininova].torrent");
		//startTorrentTask("C:\\Development\\testDownloads\\[GoGoAnime.com]  Naruto Shippuuden -  122 Eng Sub [480p].avi [mininova].torrent");
//		Main_.getInstance().addTorrentTask("C:\\Development\\testDownloads\\StargateAtlantis-Staffel05-Folge04@www.torrent.to.torrent");
//		Main_.getInstance().addTorrentTask("C:\\Development\\testDownloads\\The.Daily.Show.2009.08.20.(PDTV-FQM)[VTV].torrent");
		
//		Main_.getInstance().startTorrentTask("C:\\Development\\testDownloads\\StargateAtlantis-Staffel05-Folge04@www.torrent.to.torrent");
//		Main_.getInstance().startTorrentTask("C:\\Development\\testDownloads\\The.Daily.Show.2009.08.20.(PDTV-FQM)[VTV].torrent");
//		new Main().go();
	}

//    @Override
//    public void go() {
////        Bridge.open();
//        
////        this.addTorrentTask("C:\\Development\\testDownloads\\StargateAtlantis-Staffel05-Folge04@www.torrent.to.torrent");
////        this.addTorrentTask("C:\\Development\\testDownloads\\The.Daily.Show.2009.08.20.(PDTV-FQM)[VTV].torrent");
//        
////        try {
////			this.start("C:\\Development\\testDownloads\\The.Daily.Show.2009.08.20.(PDTV-FQM)[VTV].torrent");
////		} catch (IOException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
////        try {
////			this.start("C:\\Development\\testDownloads\\[Eclipse] Fullmetal Alchemist Brotherhood - 24 (1280x720 h264) [1867BF95].mkv [mininova].torrent");
////		} catch (IOException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
////        
////		try {
////			this.start("C:\\Development\\testDownloads\\RentBoss.v3.40.0.54.Cracked-DJiNN.torrent");
////		} catch (IOException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
//
////		DLControlRequestHandler_.getInstance();
//        BarUpdateRequestHandler.getInstance();
//		
//        ConfigurationRequestHandler.getInstance();
//
//        
////        PortMapperService mapper = PortMapper.getInstance();
////        mapper.loadSettings();
////        //mapper.setCustomConfigDir();
////        System.out.println("local host address: " + mapper.getLocalHostAddress());
////        
////        try {
////			
////        	boolean connected = mapper.connectRouter();
////        	if (connected == true) {
////        		System.out.println(mapper.getRouter().getExternalIPAddress());
////        		
////        		
////        		PortMapping mapping = new PortMapping(Protocol.TCP, null, 6991, mapper.getLocalHostAddress(), 6991, "torrent");
////        		mapper.getRouter().addPortMapping(mapping);
////        		
////        		
////        	}
////		} catch (RouterException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		
//        //        
////        PortMapper mapper = PortMapper.getInstance();
////        try {
////			boolean connected = mapper.connectRouter();
////		} catch (RouterException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//        
////        UPnPPortForward UPNP = new UPnPPortForward(6991);
////		UPNP.start();
//		
//        //addTorrentTask(controlHandler.getFile().getAbsolutePath());
//
//        //addTorrentTask("DJ Serm  Southern Alliance Mixtape Present  City Lights Vol1  Samhoody.com [mininova].torrent");
//		//addTorrentTask("[GoGoAnime.com]  Naruto Shippuuden -  122 Eng Sub [480p].avi [mininova].torrent");
//        
//    }

    private Callable<?> callable;
    
//    @Override
//	public void addTorrentTask(String key) {
//		callable = new Client(key);
//		queue.put(key, callable );
//		startTorrentTask(key);
//	}
//    
//    @Override
//	public void removeTorrentTask(String key) {
//		queue.remove(key);
//		//shutdownTorrentTask(key);
//	}
	
//    public Future<Download> startTorrentTask(String key) {
    public Future<Transfer> startTorrentTask(String key) {

		Callable<?> callable = new Client(key);
		queue.put(key, callable );
//		future = (Future<Download>) executor.submit(queue.get(key));
		future = (Future<Transfer>) executor.submit(queue.get(key));
		
		try {
			System.out.println("Future value: " + future.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return future;
	}

	
	public void shutdownTorrentTask(String key) {
	}

	public void shutdownDownloadProgress() {
		executor.shutdown();
	}

	
//	 public Future<Download> starter(String key) throws IOException {
	public Future<Transfer> starter(String key) throws IOException {
//		    int i=0;
//		    while (!executor.isShutdown())
		      future = executor.submit(new Client(key));
		      
//		      new Thread((Runnable) executor.submit(new Client<Object>(key)));

         return future;
         }

		  public void shutdown() throws InterruptedException {
		    executor.shutdown();
		    executor.awaitTermination(30, TimeUnit.SECONDS);
		    executor.shutdownNow();
		  }

//    public static GlobalManager gm;

    private BundleContext bctx;
	private ServiceRegistration reg;
	
	@Override
    public void start(BundleContext context) throws Exception {
        
//        DLControlRequestHandler.getInstance();
        ServiceReference gmRef = context.getServiceReference(GlobalManager.class.getName());
        GlobalManager gm = (GlobalManager) context.getService(gmRef);
//
//        ServiceReference dlmgrRef = context.getServiceReference(org.gudy.azureus2.plugins.download.DownloadManager.class.getName());
//        DownloadManager dlmgr = (DownloadManager) context.getService(dlmgrRef);

//        Bridge.getInstance().registerMessageHandler(DLControlMessage.DL_CONTROL, new DLControlRequestHandler(gm, dlmgr));

//    	replaced with osgi admin
//        Bridge.getInstance().registerMessageHandler(DLControlMessage.DL_CONTROL, new DLControlRequestHandler());
//        Bridge.getInstance().registerMessageHandler(BarUpdateRequestMessage.REQUEST_BAR_DATA, new BarUpdateRequestHandler());

//    	context.registerService(MainService.class.getName(), this, null);
    	
    	
    		bctx = context;
    		Dictionary props = new Hashtable();
//    		props.put(EventConstants.EVENT_TOPIC, "es/schaaf/*");
    		props.put(EventConstants.EVENT_TOPIC, DLControlMessage.DL_CONTROL+"/*");
//    		props.put(EventConstants.EVENT_TOPIC, "flexspacesinfo");
    		reg = bctx.registerService(EventHandler.class.getName(),new TestEventHandler(), props);
    	
//    	ConfigurationRequestHandler.getInstance();
        
        PortMapperService mapper = PortMapper.getInstance();
        mapper.loadSettings();
        //mapper.setCustomConfigDir();
        System.out.println("local host address: " + mapper.getLocalHostAddress());

        try {

        	boolean connected = mapper.connectRouter();
        	if (connected == true) {
        		System.out.println(mapper.getRouter().getExternalIPAddress());


        		PortMapping mapping = new PortMapping(Protocol.TCP, null, 33333, mapper.getLocalHostAddress(), 33333, "torrent");
        		
        		mapper.getRouter().addPortMapping(mapping);

//        		mapping = new PortMapping(Protocol.UDP, null, 33333, mapper.getLocalHostAddress(), 33333, "torrent");
//        		mapper.getRouter().addPortMapping(mapping);

        	}
		} catch (RouterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Registers a new EventGenerator used in torrent
			eg = new EventGenerator(context);
			eg.start();
		
		
		
    }

    @Override
    public void stop(BundleContext arg0) throws Exception {
//    	reg.unregister();
    	eg.setRunning(false);
		eg.join();
	
    	
    }
//    public void setControlHandler(DLControlService dlControlMessageHandler) {
//        this.controlHandler = dlControlMessageHandler;
//    }
//
//    public DLControlService getControlHandler() {
//        return this.controlHandler;
//    }


//	public void setStatusMap(Map<String, String> statusMap) {
//		this.statusMap = statusMap;
//	}
//
//	public Map<String, String> getStatusMap() {
//		return statusMap;
//	}
}