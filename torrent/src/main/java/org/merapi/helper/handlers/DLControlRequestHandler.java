/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.merapi.helper.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderCallBackInterface;
import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderFactory;
//import org.gudy.azureus2.plugins.download.Download;
import org.torrent.data.FileInfo;
import org.torrent.transfer.Transfer;
//import org.torrent.internal.transfer.Download;
import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloader;
import org.gudy.azureus2.core3.torrentdownloader.impl.TorrentDownloaderManager;
import org.merapi.handlers.MessageHandler;
import org.merapi.helper.messages.DLControlMessage;
import org.merapi.helper.messages.DLControlRespondMessage;
import org.merapi.helper.messages.ListUpdateMessage;
import org.merapi.messages.IMessage;
import org.torrent.client.MainService;
import org.torrent.internal.client.Main;
import org.torrent.internal.data.MetaInfoFile;

//import org.gudy.azureus2.pluginsimpl.locale.download.DownloadBridge;
//import org.gudy.azureus2.pluginsimpl.locale.download.DownloadManagerImpl;



/**
 * 
 * @author gurkerl
 */
public class DLControlRequestHandler extends MessageHandler {

	private static File file;

	private MainService service;
	/**
	 * Default-Konstruktor, der nicht au�erhalb dieser Klasse aufgerufen werden
	 * kann
	 */
	private GlobalManager _gm;
	private DownloadManager _dlmgr;
	//	public DLControlRequestHandler(GlobalManager gm, DownloadManager dl) {
	//		super(DLControlMessage.DL_CONTROL);
	////		service = new Main();
	//                this._gm = gm;
	//	}

	public DLControlRequestHandler() {
		//        super(DLControlMessage.DL_CONTROL);
	}
	//    public DLControlRequestHandler(GlobalManager gm, org.gudy.azureus2.plugins.download.DownloadManager dlmgr) {
	//        super(DLControlMessage.DL_CONTROL);
	////		service = new Main();
	//                this._gm = gm;
	//                _dlmgr = dlmgr;
	//    }

	@Override
	public void handleMessage(IMessage message) {
		if (message instanceof DLControlMessage) {

			System.out.println("DLControlMessage received on backend (globalmanager)");

			DLControlMessage downloadControlMessage = (DLControlMessage) message;

			if (downloadControlMessage.action.equals(DLControlMessage.LOCAL_FILE)) {

				file = new File(downloadControlMessage.fileName);
				FileOutputStream fop = null;

				try {
					fop = new FileOutputStream(file);

				} catch (FileNotFoundException ex) {
					Logger.getLogger(DLControlRequestHandler.class.getName()).log(
							Level.SEVERE, null, ex);
				}

				if (file.exists()) {
					try {
						fop.write(downloadControlMessage.fileData);
					} catch (IOException ex) {
						Logger.getLogger(DLControlRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
					}
					try {
						fop.flush();
					} catch (IOException ex) {
						Logger.getLogger(DLControlRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
					}
					try {
						fop.close();
					} catch (IOException ex) {
						Logger.getLogger(DLControlRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
					}
					System.out.println("The data has been written");
				}

			} else if (downloadControlMessage.action.equals(DLControlMessage.REMOTE_FILE)) {

				System.out.println("Download Remote Torrent triggered");
//				downloadRemoteTorrent(downloadControlMessage.fileReference);
				return;
			}

		} else {
			System.out.println("invalid message received on backend");
			return;
		}

		System.out.println("absolute file: " + file.getAbsoluteFile());
		System.out.println("absolute path: " + file.getAbsolutePath());

		

		try {
//			Future<Download> dl = Main.getInstance().starter(file.getAbsolutePath());
			Future<Transfer> dl = Main.getInstance().starter(file.getAbsolutePath());
			
			
			//                        DownloadBridge dlmgr = (DownloadBridge) _dlmgr;
			//                        dlmgr.addExternalDownload(dl);
			//                        _dlmgr.resumeDownloads();


			//                        DownloadManager dlmgr = _gm.addDownloadManager(file.getAbsolutePath(), "C:\\Development\\testDownloads\\");
			//                        _gm.resumeDownload(dlmgr);


			//			Main.getInstance().startTorrentTask(file.getAbsolutePath());
		} catch (IOException e) {
			//            // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Override
	// public File getFile() {
	// return this.file;
	// }
	public void sendListUpdateMessage(String action, String infoHash) {
		ListUpdateMessage updateMessage = new ListUpdateMessage();

		updateMessage.action = action;
		updateMessage.infoHash = infoHash;
		updateMessage.send();
	}

        public static void sendDLControlRespondMessage(String action, String infoHash, String name, long size, int cols, int rows,
			long l, long m, long n, long o, long progress) {
		DLControlRespondMessage dlControlRespondMessage = new DLControlRespondMessage();

		dlControlRespondMessage.action = action;
		dlControlRespondMessage.infoHash = infoHash;
		dlControlRespondMessage.name = name;
		dlControlRespondMessage.size = size;
		dlControlRespondMessage.cols = cols;
		dlControlRespondMessage.rows = rows;

		//evtl nach ListUpdateMessage
		dlControlRespondMessage.ldB = l;
		dlControlRespondMessage.ldT = m;
		dlControlRespondMessage.luB = n;
		dlControlRespondMessage.luT = o;
		dlControlRespondMessage.progress = progress;
		System.out.println("Progress:" + progress);
		dlControlRespondMessage.send();

	}
	
	public static void sendDLControlRespondMessage(String action, String infoHash, ArrayList<FileInfo> files) {
		DLControlRespondMessage dlControlRespondMessage = new DLControlRespondMessage();

		dlControlRespondMessage.action = action;
		dlControlRespondMessage.infoHash = infoHash;
		dlControlRespondMessage.files = files;

		dlControlRespondMessage.send();
	}

public static void sendDLControlRespondMessage(String action, String infoHash, ArrayList<FileInfo> files, MetaInfoFile mif) {
		DLControlRespondMessage dlControlRespondMessage = new DLControlRespondMessage();

		dlControlRespondMessage.action = action;
		dlControlRespondMessage.infoHash = infoHash;
		dlControlRespondMessage.files = files;
                dlControlRespondMessage.mif = mif;
		dlControlRespondMessage.send();
	}

	/**
	 * downloads the remote torrent file. once we have downloaded the .torrent file, we
	 * pass the data to the downloadTorrent() method for further processing
	 * @param url
	 * @param outputDir
	 */
	private void downloadRemoteTorrent(String url) {
		TorrentDownloader downloader = TorrentDownloaderFactory.create(new TorrentDownloaderCallBackInterface() {
			public void TorrentDownloaderEvent(int state, TorrentDownloader inf) {
				if( state == TorrentDownloader.STATE_FINISHED )
				{
					System.out.println("torrent file download complete. starting torrent");
					TorrentDownloaderManager.getInstance().remove(inf);
					try {
//						Future<Download> dl = Main.getInstance().starter(inf.getFile().getAbsolutePath());
						Future<Transfer> dl = Main.getInstance().starter(inf.getFile().getAbsolutePath());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					downloadTorrent( inf.getFile().getAbsolutePath(), outputDir.getAbsolutePath() );
				}
				else
					TorrentDownloaderManager.getInstance().TorrentDownloaderEvent(state, inf);
			}
		}, url, null, null, true);
		TorrentDownloaderManager.getInstance().add(downloader);
	}
}
