package org.torrent.internal.client;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

//import org.milipede.upnp.UPnPService;
//import org.milipede.upnp.internal.UPnPPortForward;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.core3.download.DownloadManagerInitialisationAdapter;
import org.gudy.azureus2.plugins.download.Download;
import org.merapi.helper.handlers.BarUpdateRequestHandler;
import org.merapi.helper.handlers.DLControlRequestHandler;
import org.merapi.helper.handlers.DLControlRequestHandler_;
import org.merapi.helper.messages.BarUpdateRespondMessage;
import org.merapi.helper.messages.DLControlRespondMessage;
import org.merapi.helper.messages.DetailInfoMessage;
import org.milipede.modules.list.model.vo.ListVO;
import org.millipede.download.AbstractDownloadManager;
import org.torrent.internal.bencoding.BDecoder;
import org.torrent.internal.bencoding.BDecodingException;
import org.torrent.internal.bencoding.BMap;
import org.torrent.internal.bencoding.BTypeException;
import org.torrent.internal.bencoding.MetaInfoFileIO;
import org.torrent.internal.data.BTPart;
import org.torrent.data.FileInfo;
import org.torrent.data.ProcessInfo;
import org.torrent.internal.data.Hash;
import org.torrent.internal.data.MetaInfoFile;
import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.io.DataReader;
import org.torrent.internal.io.DataWriter;
import org.torrent.internal.io.DigesterService;
import org.torrent.internal.io.FileMappedReader;
import org.torrent.internal.io.FileMappedWriter;
import org.torrent.internal.io.FileMapper;
import org.torrent.internal.io.FileReader;
import org.torrent.internal.io.FileWriter;
import org.torrent.internal.io.NBDataReader;
import org.torrent.internal.io.NBDataWriter;
import org.torrent.internal.peer.PeerInfo;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.peer.connection.BitfieldProvider;
import org.torrent.internal.peer.connection.SocketConnection;
import org.torrent.internal.peer.connection.SocketConnection.BandwidthLimiter;
import org.torrent.internal.protocol.message.BTMessageVisitorAdapter;
import org.torrent.internal.protocol.message.BitField;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Piece;
import org.torrent.internal.protocol.message.Request;
import org.torrent.internal.tracker.TrackerRequest;
import org.torrent.internal.tracker.TrackerResponse;
import org.torrent.internal.tracker.TrackerRequest.Event;
import org.torrent.internal.transfer.BTSession;
import org.torrent.internal.transfer.BTSessionListenerAdapter;
import org.torrent.internal.transfer.ContentWatcher;
import org.torrent.internal.transfer.TrafficWatcher;
import org.torrent.internal.transfer.TransferBuilder;
import org.torrent.internal.util.Bits;
import org.torrent.transfer.Transfer;

//import org.milipede.scheduler.SchedulerProviderService;
import org.torrent.internal.protocol.message.Port;
public class Client<Download> extends AbstractDownloadManager implements Callable<Download> {

//    public
//	Client(
//		GlobalManager 							_gm,
//		byte[]									_torrent_hash,
//		String 									_torrentFileName,
//		String 									_torrent_save_dir,
//		String									_torrent_save_file,
//		int   									_initialState,
//		boolean									_persistent,
//		boolean									_recovered,
//		boolean									_open_for_seeding,
//		boolean									_has_ever_been_started,
//		List									_file_priorities,
//		DownloadManagerInitialisationAdapter	_initialisation_adapter ) {
//
//    }
    public
	Client(
		GlobalManager 							_gm) {

    }

    private static final int OUT_BPS_LIMIT = 5000;
	private static final int PORT = 33333;
	// Linux directory
	// private static final String DIR =
	// "/home/gurkerl/Development/testDownloads/";
	// Windows directory
	public static final String DIR = "D:\\Development\\testDownloads\\";

	private String TORRENT = null;
	private String TORRENTDIR = null;

	private final ScheduledExecutorService service = Executors
			.newSingleThreadScheduledExecutor();
	private final ExecutorService ioService = Executors.newCachedThreadPool();
	private MetaInfoFile mif;
        private String infoHash;


	private Hash peerID;

	private final Set<PeerInfo> peers = new HashSet<PeerInfo>();
	private final Set<PeerInfo> connected = Collections
			.synchronizedSet(new HashSet<PeerInfo>());

	private DigesterService pieceChecker;

	private DataReader breader;
	private DataWriter bwriter;

	private NBDataReader reader;
	private NBDataWriter writer;

	private int outLimit = OUT_BPS_LIMIT;
	protected volatile int nextUpdate;
	private int pendingCons;
	private Transfer transfer;

//	private SchedulerProviderService prov;
	
	public Client(String torrent) {
		this.TORRENT = torrent;
		
//		UPnPPortForward UPNP = new UPnPPortForward(6991);
//		UPNP.start();
//		
	}

    BandwidthLimiter outLimiter = new BandwidthLimiter() {

		@Override
		public void allocate(int amount) throws InterruptedException {
			synchronized (Client.this) {
				while (outLimit < 0) {
					Client.this.wait();
				}
				outLimit -= amount;
			}
		}

	};

	BitfieldProvider bitProv = new BitfieldProvider() {

		@Override
		public Bits getBitField() {
			return getTransfer().getContentWatcher().getBits();
		}

	};

//  public void c() throws IOException, InterruptedException,
	@Override
	public Download call() throws IOException, InterruptedException,
			NoSuchAlgorithmException {
		// TODO Auto-generated method stub

		//Client c = new Client();

		byte[] id = new byte[20];
		new Random().nextBytes(id);
		this.peerID = new Hash(id, Hash.Type.ID);

		/**
		 * Sole entry point to the class and application.
		 * 
		 * @param args
		 *            Array of String arguments.
		 */
		/*
		 * byte[] fileArray = null;
		 * 
		 * try { fileArray = getBytesFromFile(new File(DIR +
		 * "MerlinundderKriegderDrachen@www.torrent.to.torrent")); } catch
		 * (IOException e) { e.printStackTrace(); } try { c.mif = (MetaInfoFile)
		 * BDecoder.bdecode(fileArray); } catch (BDecodingException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		try {
                    MetaInfoFile localmif = MetaInfoFileIO.load(new FileInputStream(TORRENT));
//			this.mif = MetaInfoFileIO.load(new FileInputStream(TORRENT));
//			Main.map.put(this.mif.getInfoHash().asHexString(), this.mif);

                    Main.getInstance().map.put(localmif.getInfoHash().asHexString(), localmif);
//                    Main.getInstance().getStatusMap().put(localmif.getInfoHash().asHexString(), "process");
                    ProcessInfo pi = Main.getInstance().getStatusMap().get(localmif.getInfoHash().asHexString());
                    pi.setStatus("process");
                    Main.getInstance().getStatusMap().put(localmif.getInfoHash().asHexString(), pi);
                    infoHash = localmif.getInfoHash().asHexString();


		} catch (IOException io) {
			io.printStackTrace();

		}
		this.run();

//		return null;
		return (Download) this.getTransfer();
	}
	
	public long getChecksumValue(File aFile) {
		Checksum checksum = new CRC32();
		try {
			BufferedInputStream is = new BufferedInputStream(
					new FileInputStream(aFile));
			byte[] bytes = new byte[1024];
			int len = 0;
			while ((len = is.read(bytes)) >= 0) {
				checksum.update(bytes, 0, len);
			}
			is.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return checksum.getValue();
	}

	public void flatten(File directory, File destination) throws IOException {
		if (directory == null || ! directory.exists()) {
			throw new IOException("The source directory is null or does not exist.");
		}
		if (destination == null) {
			destination = directory;
		}

		for (File aFile: directory.listFiles()) {
			if (aFile.isDirectory()) {
				this.flatten(aFile, destination);
				aFile.delete();
			} else {
				// is the file already at the top level of the hierarchy?
				if (! aFile.getParentFile().getAbsolutePath().equals(destination.getAbsolutePath())) {
					// is a file by the same name already there?
					File movedFile = new File(destination, aFile.getName());
					if (movedFile.exists()) {
						String filename = aFile.getName();
						String checksum = String.valueOf(getChecksumValue(aFile));
						if (aFile.getName().contains(".")) {
							filename = aFile.getName().split("[.]")[0] + "_" + checksum + "." + aFile.getName().split("[.]")[1];
						} else {
							filename = filename += "_" + checksum;
						}
						movedFile = new File(destination, filename);
					}
					if (! aFile.renameTo(movedFile)) {
						throw new IOException("Could not move file from: " + aFile.getAbsolutePath() + " to: " + destination.getAbsolutePath());
					}
				}
			}
		}
	}
	
//    @Override
//	public Download call() throws IOException, InterruptedException,
	public void run() throws IOException, InterruptedException,

        NoSuchAlgorithmException {
            
		while(Main.getInstance().getStatusMap().get(infoHash).equals("process")) {
			System.out.println("Wait state");
			Thread.sleep(2000);
		}
//		if(Main.getInstance().getStatusMap().get(infoHash).equals("process")) {
//                Thread.sleep(1000);
//                System.out.println("Wait state");
////                return null;
//            } else if (Main.getInstance().getStatusMap().get(infoHash).equals("processed")) {
            	this.mif = Main.getInstance().map.get(infoHash);

//		BarUpdateRequestHandler.getInstance().sendDetailInfoMessage(DetailInfoMessage.ADD_DETAIL_INFO, DetailInfoMessage.ADD_DETAIL_TRACKER_INFO);
//		
		try {
			if (mif.getAnnounceTiers() != null) {
				System.out.println("This torrent has announce tiers:");
				for (List<String> tier : mif.getAnnounceTiers()) {
					System.out.println("Tier:");
					for (String url : tier) {
						System.out.println(url);
					}
				}
			}
			if (mif.getDataInfo().getFiles().size() > 1) {
				System.err.println("More than one file!");
				// return;
			}
			TorrentMetaInfo contentInfo = new TorrentMetaInfo(
					mif.getInfoHash(), mif.getDataInfo().getPieceHashes()
							.toArray(new Hash[0]), mif.getDataInfo()
							.getDataSize(), mif.getDataInfo().getPieceLength());
			
			// peers.add(PeerInfo.fromAddress(null, new InetSocketAddress(
			// "93.209.80.220", 5555)));

			// peers.add(PeerInfo.fromAddress(null, new InetSocketAddress(
			// "93.209.80.220", 34243)));

			Logger.getLogger("org.torrent").setLevel(Level.FINER);
			// Logger.getLogger("org.torrent.internal.peer").setLevel(Level.FINEST);
			for (Handler h : Logger.getLogger("").getHandlers()) {
				h.setLevel(Level.ALL);
			}

			new Timer(true).scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					synchronized (Client.this) {
						outLimit = Math.min(OUT_BPS_LIMIT, outLimit
								+ OUT_BPS_LIMIT / 4);
						Client.this.notifyAll();
					}
				}
			}, 0, 250);

			
			
			final Map<FileInfo, RandomAccessFile> files = new HashMap<FileInfo, RandomAccessFile>();
			
			try {

				
				TORRENTDIR = "D:\\Development\\testDownloads\\";
				
				if (mif.getDataInfo().getBaseDir() == null) {
					//Single-Torrent File
					new File(TORRENTDIR).mkdir();
					for (FileInfo fi : mif.getDataInfo().getFiles()) {

						File temp = new File(TORRENTDIR,fi.getFileName());
						temp.createNewFile();
						RandomAccessFile raf = new RandomAccessFile(temp, "rw");
						raf.setLength(fi.getLength());
						
						files.put(fi, raf);
					}
				}
				else
				{
					//Multi-Torrent File
					new File(TORRENTDIR.concat(mif.getDataInfo().getBaseDir())).mkdir();
					
					for(FileInfo file : mif.getDataInfo().getFiles()) {
						StringBuffer buf = new StringBuffer(file.getFileName());
						File temp = null;
						while (buf.indexOf("\\") == -1) {
						
							temp = new File(TORRENTDIR.concat(mif.getDataInfo().getBaseDir()), file.getFileName());
							temp.createNewFile();
							RandomAccessFile raf = new RandomAccessFile(temp, "rw");
							raf.setLength(file.getLength());
							files.put(file, raf);
							break;

						}
						
						while (buf.indexOf("\\") > 0) {
							int index = buf.indexOf("\\");
							if(index > 0) {
								
								new File(TORRENTDIR.concat(mif.getDataInfo().getBaseDir()).concat("\\").concat(buf.substring(0, index))).mkdir();
								buf = buf.delete(0, index);
							}
								temp = new File(TORRENTDIR.concat(mif.getDataInfo().getBaseDir()).concat("\\").concat(file.getFileName()));
								temp.createNewFile();	
								
								RandomAccessFile raf = new RandomAccessFile(temp, "rw");
								raf.setLength(file.getLength());
								files.put(file, raf);
								break;
						}
					
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			
			FileMapper mapper = new FileMapper(mif.getDataInfo().getFiles());
			
			FileReader freader = new FileReader() {
				
				@Override
				public void read(FileInfo file, ByteBuffer dst, long position)
						throws IOException {
					RandomAccessFile raf = files.get(file);
					synchronized (raf) {
						while (dst.hasRemaining()) {
							int read = raf.getChannel().read(dst, position);
							if (read < 0) {
								throw new EOFException();
							}
							position += read;
						}
					}
				}

			};
			
			FileWriter fwriter = new FileWriter() {

				@Override
				public void write(FileInfo file, ByteBuffer src, long position)
						throws IOException {
					RandomAccessFile raf = files.get(file);
					synchronized (raf) {
						while (src.hasRemaining()) {
							int written = raf.getChannel().write(src, position);
							position += written;
						}
					}
				}

			};

			breader = new FileMappedReader(mapper, freader);

			reader = new NBDataReader() {

				@Override
				public void read(final ByteBuffer dst, final long position,
						final Callback cb) {
					ioService.execute(new Runnable() {
						@Override
						public void run() {
							try {
								breader.read(dst, position);
								cb.read(dst, position);
							} catch (IOException e) {
								cb.caughtException(e);
							}
						}
					});
				}
			};

			bwriter = new FileMappedWriter(mapper, fwriter);

			writer = new NBDataWriter() {

				@Override
				public void write(final ByteBuffer src, final long position,
						final Callback cb) {
					ioService.execute(new Runnable() {

						@Override
						public void run() {
							try {
								bwriter.write(src, position);
								cb.written(src, position);
							} catch (IOException e) {
								cb.caughtException(e);
							}
						}

					});
				}

			};

			pieceChecker = new DigesterService("SHA1");
			
			TransferBuilder builder = new TransferBuilder();
			setTransfer(builder.createTransfer(contentInfo, reader, writer, pieceChecker, breader));
			
			
			final BTSession session = getTransfer().getSession();

			session.addBTSessionListener(new BTSessionListenerAdapter() {
				@Override
				public void sentBTMessage(final BTConnection from,
						final BittorrentMessage message) {

                                    message.accept(new BTMessageVisitorAdapter() {
						@Override
						public void visitRequest(Request request) {
							System.err
									.println("Requesting "
											+ request.getIndex()
											+ " with availability "
											+ getTransfer()
													.getAvailabilityObserver()
													.getAvailability(
															getTransfer()
																	.getContentInfo()
																	.getPiece(
																			request
																					.getIndex())));
							int min = getTransfer().getAvailabilityObserver()
									.getAvailability(
											getTransfer().getContentInfo().getPiece(
													0));
							int max = getTransfer().getAvailabilityObserver()
									.getAvailability(
											getTransfer().getContentInfo().getPiece(
													0));
							for (org.torrent.internal.data.TorrentMetaInfo.Piece p : getTransfer()
									.getContentInfo()) {
								if (from.getRemoteBitField().get(p.getIndex())) {
									min = Math.min(min, getTransfer()
											.getAvailabilityObserver()
											.getAvailability(p));
									max = Math.max(min, getTransfer()
											.getAvailabilityObserver()
											.getAvailability(p));
								}
							}
							System.err.println("minmax: " + min + " " + max);
						}

                                              
					});
					System.err.println("(out) " + message + " to " + from);
				}

				@Override
				public void receivedBTMessage(BTConnection from,
						BittorrentMessage message) {
					System.err.println("(in) " + message + " in " + from);
					if (message instanceof BitField) {
						boolean mad = false;
						BitField bf = (BitField) message;
						for (int i = getTransfer().getContentInfo().getPiecesCount(); i < bf
								.getBitSet().size(); i++) {
							mad |= bf.getBitSet().get(i);
						}
						if (mad) {
							System.err
									.println(from
											+ " sent me a bitfield with some invalid bits set!");
						}
					}
//                                        else if (message instanceof Port) {
//
//							System.out.println("Message of type port");
//							System.out.println("Requesting piece index: " +
//							((Port) message).getPort());

//                                                        if (!ml_dht_enabled) {return;}
//                                                        MainlineDHTProvider provider = getDHTProvider();
//                                                        if (provider == null) {return;}

//                                                        try {provider.notifyOfIncomingPort(socket.getInetAddress().getHostAddress(), ((Port) msg).getPort());}
//                                                        catch (Throwable t) {t.printStackTrace();}

//							con.send(new Request(((Have) msg).getPieceIndex(), 0, info.getPieceLength()), null);
//							bitFieldSender(bitProv, (BTSocketConnection) con));
					
//
//						 }
				}

				@Override
				public void receivedUnrequestedPiece(BTConnection from,
						Piece piece) {
					System.err.println(from + " SENT ME AN UNREQUESTED PIECE:"
							+ piece);
				}
				
				
			});

			service.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					if (nextUpdate <= 0) {
						try {
							pokeTracker(Event.UPDATE);
						} catch (BTypeException e) {
							e.printStackTrace();
						}
					} else {
						nextUpdate--;
					}
				}

			}, 0, 1, TimeUnit.SECONDS);

			getTransfer().checkAllPieces();

			Thread acceptor = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ServerSocket sock = new ServerSocket(PORT);
						while (true) {
							Socket s = sock.accept();
							System.out.println("Accepting " + s);
							SocketConnection.accept(getTransfer().getSession(),
									getTransfer().getContentInfo(), outLimiter,
									null, null, bitProv, s, peerID);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}, "Hosting");
			acceptor.setDaemon(true);
			acceptor.start();

			Thread checkerThread = new Thread(pieceChecker, "Piece checker");
			checkerThread.setDaemon(true);
			checkerThread.start();
			
//			System.out.println("Start connections to " + peers.size()
//					+ " peers.");

			// session.addConnection(connect(PeerInfo.fromAddress(null,
			// new InetSocketAddress("192.168.2.2", 34243)), transfer));

			ExecutorService connector = Executors.newFixedThreadPool(10,
					new ThreadFactory() {

						@Override
						public Thread newThread(Runnable r) {
							Thread t = new Thread(r, "Connector");
							t.setDaemon(true);
							return t;
						}

					});
			long time = System.currentTimeMillis();
			while (true) {
				synchronized (peers) {
					for (final PeerInfo peer : peers) {
						if (connected.contains(peer)) {
							continue;
						}
						connected.add(peer);
						pendingCons++;
						connector.execute(new Runnable() {
							@Override
							public void run() {
								BTConnection connection;
								try {
									System.err.println("Connecting to " + peer);
									connection = connect(peer, getTransfer());
//                                                                        connection.send(new Port(49001), null);
//									System.out.println(connection.toString());



								} catch (IOException e) {
									System.err.println("Failed to connect to "
											+ peer + ": " + e.getMessage());
								} finally {
									synchronized (peers) {
										pendingCons--;
									}
								}
							}

						});
					}
					if (pendingCons == 0
							&& session.getConnections().size() < 50) {
						nextUpdate = Math.min(nextUpdate, 10);
					}
					peers.clear();
					peers.wait(2000);

					TrafficWatcher trafficWatcher = getTransfer()
							.getTrafficWatcher();
//out to test message overflow
//					replaced throu osgi event admin
//					DLControlRequestHandler.sendDLControlRespondMessage(DLControlRespondMessage.ITEM_UPDATED,
//							transfer.getContentInfo().getInfoHash().asHexString(), mif.getDataInfo().getBaseDir(), mif.getDataInfo().getDataSize(), 0, 0,
//							trafficWatcher.getDownloaded() * 1000L / (System.currentTimeMillis() - time),
//							trafficWatcher.getDownloaded(),
//							trafficWatcher.getUploaded() * 1000L / (System.currentTimeMillis() - time),
//							trafficWatcher.getUploaded(),
//							(trafficWatcher.getDownloaded() * 100L /mif.getDataInfo().getDataSize()));
//					BarUpdateRequestHandler.getInstance().sendDetailInfoMessage(DetailInfoMessage.ADD_DETAIL_INFO, DetailInfoMessage.ADD_DETAIL_TRACKER_INFO);
					
					// System.out.println("Downloaded: " +
					// transfer.getTrafficWatcher().getDownloaded());
					
					Main.eg.sendMessage(DLControlRespondMessage.ITEM_UPDATED, new ListVO(
							getTransfer().getContentInfo().getInfoHash().asHexString(),
							mif.getDataInfo().getBaseDir(), 
							mif.getDataInfo().getDataSize(), 
							"G", 
							false,
							trafficWatcher.getUploaded() * 1000L / (System.currentTimeMillis() - time),
							trafficWatcher.getUploaded(),
							trafficWatcher.getDownloaded() * 1000L / (System.currentTimeMillis() - time),
							trafficWatcher.getDownloaded(),
							(trafficWatcher.getDownloaded() * 100L /mif.getDataInfo().getDataSize()), 0, 0));
					
					
					System.err.println(getTransfer().getContentWatcher().getBits()
							.count(true)
							+ " vs "
							+ getTransfer().getContentWatcher().getBits().count(
									false) + " " + outLimit);
					System.err.println(trafficWatcher.getUploaded()
							+ " bytes uploaded, "
							+ trafficWatcher.getDownloaded()
							+ " bytes downloaded");
					System.err.println(trafficWatcher.getUploaded() * 1000L
							/ (System.currentTimeMillis() - time)
							+ " bytes/s upload, "
							+ trafficWatcher.getDownloaded() * 1000L
							/ (System.currentTimeMillis() - time)
							+ " bytes/s download");
				}
				// System.out.println("Sessions: "
				// + session.getConnections().size());

				// System.out.println("Sessions: " + session.getConnections());

				if (getTransfer().getContentWatcher().isCompleted()
						&& session.getConnections().isEmpty()) {
					break;
				}
			}
			System.out.println("Done downloading");
			for (Map.Entry<FileInfo, RandomAccessFile> me : files.entrySet()) {
				me.getValue().close();
			}
		} finally {
			System.out.println("Shutting down service");
			service.shutdown();
			if (service.awaitTermination(5, TimeUnit.SECONDS)) {
				System.out.println("DONE.");
			} else {
				System.out.println("FAILED!");
				service.shutdownNow();
				System.exit(1);
			}
		}
//                 return (Download) this.transfer;
//	}
//        return (Download) this.transfer;
//            return null;
    }

	private BTConnection connect(PeerInfo peer, final Transfer transfer)
			throws IOException {
		final Socket s = new Socket();

		s.connect(peer.getAddress().getInetSocketAddress(), 5000);
		BTConnection con = SocketConnection.connect(transfer.getSession(),
				transfer.getContentInfo(), outLimiter, null, null, bitProv, s,
				peerID);
                return con;
	}

	protected void pokeTracker(Event event) throws BTypeException {
		TrafficWatcher tw = getTransfer().getTrafficWatcher();
		ContentWatcher cw = getTransfer().getContentWatcher();
		TrackerRequest request = new TrackerRequest(mif.getAnnounce(), mif
				.getInfoHash().toByteArray(), peerID.toByteArray(), PORT, tw
				.getUploaded(), tw.getDownloaded(), (long) cw.getBits().count(
				false)
				* getTransfer().getContentInfo().getPieceLength(), event);
		try {
			URL URL = request.toURL();
			TrackerResponse response = new TrackerResponse((BMap) BDecoder
					.bdecode(URL.openConnection().getInputStream()));
			System.out.println("Tracker update with URL: " + URL);
			int interval;

			if (response.hasFailed()) {
				System.out.println("Failed: " + response.getFailureReason());
				interval = 30;
			} else {
				synchronized (peers) {
					peers.addAll(response.getPeerList());
					peers.notifyAll();

					System.out.println(peers.size()
							+ " known peers after update!");
				}
				System.out.println("Seeds: " + response.getComplete());
				System.out.println("Leechers: " + response.getIncomplete());

				interval = response.getInterval();
				if (response.getMinInterval() != null) {
					interval = response.getMinInterval();
				}
			}
			nextUpdate = interval;
		} catch (MalformedURLException e) {
			System.err.println(e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BDecodingException e) {
			e.printStackTrace();
		}
	}

    /**
     * @return the transfer
     */
    public Transfer getTransfer() {
        return transfer;
    }

    /**
     * @param transfer the transfer to set
     */
    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }


	// /**
	// * Returns the contents of the file in a byte array
	// * @param file File this method should read
	// * @return byte[] Returns a byte[] array of the contents of the file
	// */
	//    
	// private static byte[] getBytesFromFile(File file) throws IOException {
	//
	// InputStream is = new FileInputStream(file);
	// System.out.println("\nDEBUG: FileInputStream is " + file);
	//
	// // Get the size of the file
	// long length = file.length();
	// System.out.println("DEBUG: Length of " + file + " is " + length + "\n");
	//
	// /*
	// * You cannot create an array using a long type. It needs to be an int
	// * type. Before converting to an int type, check to ensure that file is
	// * not loarger than Integer.MAX_VALUE;
	// */
	// if (length > Integer.MAX_VALUE) {
	// System.out.println("File is too large to process");
	// return null;
	// }
	//
	// // Create the byte array to hold the data
	// byte[] bytes = new byte[(int)length];
	//
	// // Read in the bytes
	// int offset = 0;
	// int numRead = 0;
	// while ( (offset < bytes.length)
	// &&
	// ( (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) ) {
	//
	// offset += numRead;
	//
	// }
	//
	// // Ensure all the bytes have been read in
	// if (offset < bytes.length) {
	// throw new IOException("Could not completely read file " +
	// file.getName());
	// }
	//
	// is.close();
	// return bytes;
	//
	// }

}