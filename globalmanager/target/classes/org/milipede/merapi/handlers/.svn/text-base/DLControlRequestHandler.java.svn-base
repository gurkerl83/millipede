package org.milipede.merapi.handlers;

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.milipede.merapi.handlers;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import org.gudy.azureus2.core3.download.DownloadManager;
//import org.gudy.azureus2.core3.global.GlobalManager;
//import org.merapi.handlers.MessageHandler;
//import org.merapi.helper.messages.DLControlMessage;
//import org.merapi.helper.messages.DLControlRespondMessage;
//import org.merapi.helper.messages.ListUpdateMessage;
//import org.merapi.messages.IMessage;
//import org.torrent.client.MainService;
//import org.torrent.internal.client.Main;
//
//import org.gudy.azureus2.pluginsimpl.locale.download.DownloadBridge;
//
///**
// *
// * @author gurkerl
// */
//public class DLControlRequestHandler extends MessageHandler {
//
//	private File file;
//
//	 private MainService service;
//
//	/**
//	 * Default-Konstruktor, der nicht auﬂerhalb dieser Klasse aufgerufen werden
//	 * kann
//	 */
//        private GlobalManager _gm;
//	public DLControlRequestHandler(GlobalManager gm) {
//		super(DLControlMessage.DL_CONTROL);
////		service = new Main();
//                this._gm = gm;
//	}
//
//	@Override
//	public void handleMessage(IMessage message) {
//		if (message instanceof DLControlMessage) {
//
//			System.out.println("DLControlMessage received on backend (globalmanager)");
//
//			DLControlMessage downloadControlMessage = (DLControlMessage) message;
//
//			file = new File(downloadControlMessage.fileName);
//			FileOutputStream fop = null;
//
//			try {
//				fop = new FileOutputStream(file);
//
//			} catch (FileNotFoundException ex) {
//				Logger.getLogger(DLControlRequestHandler.class.getName()).log(
//						Level.SEVERE, null, ex);
//			}
//
//			if (file.exists()) {
//				try {
//					fop.write(downloadControlMessage.fileData);
//				} catch (IOException ex) {
//					Logger.getLogger(DLControlRequestHandler.class.getName())
//							.log(Level.SEVERE, null, ex);
//				}
//				try {
//					fop.flush();
//				} catch (IOException ex) {
//					Logger.getLogger(DLControlRequestHandler.class.getName())
//							.log(Level.SEVERE, null, ex);
//				}
//				try {
//					fop.close();
//				} catch (IOException ex) {
//					Logger.getLogger(DLControlRequestHandler.class.getName())
//							.log(Level.SEVERE, null, ex);
//				}
//				System.out.println("The data has been written");
//			}
//		} else {
//			System.out.println("invalid message received on backend");
//		}
//
//		System.out.println("absolute file: " + file.getAbsoluteFile());
//		System.out.println("absolute path: " + file.getAbsolutePath());
//
////		service.addTorrentTask(file.getAbsolutePath());
////		Main.getInstance().addTorrentTask(file.getAbsolutePath());
////		try {
////			Main.getInstance().start(file.getAbsolutePath());
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//
//		try {
//			Main.getInstance().starter(file.getAbsolutePath());
//
//                        DownloadManager dlmgr = _gm.addDownloadManager(file.getAbsolutePath(), "C:\\Development\\testDownloads\\");
//
//
//                        _gm.resumeDownload(dlmgr);
//
//
////			Main.getInstance().startTorrentTask(file.getAbsolutePath());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	// @Override
//	// public File getFile() {
//	// return this.file;
//	// }
//
//	public void sendListUpdateMessage(String action, String infoHash) {
//		ListUpdateMessage updateMessage = new ListUpdateMessage();
//
//		updateMessage.action = action;
//		updateMessage.infoHash = infoHash;
//		updateMessage.send();
//	}
//
//	public void sendDLControlRespondMessage(String action, String infoHash, String name, int cols, int rows,
//			long l, long m, long n, long o, int progress){
//		DLControlRespondMessage dlControlRespondMessage = new DLControlRespondMessage();
//
//		dlControlRespondMessage.action = action;
//		dlControlRespondMessage.infoHash = infoHash;
//		dlControlRespondMessage.name = name;
//		dlControlRespondMessage.cols = cols;
//		dlControlRespondMessage.rows = rows;
//
//		//evtl nach ListUpdateMessage
//		dlControlRespondMessage.ldB = l;
//		dlControlRespondMessage.ldT = m;
//		dlControlRespondMessage.luB = n;
//		dlControlRespondMessage.luT = o;
//		dlControlRespondMessage.progress = progress;
//
//		dlControlRespondMessage.send();
//
//	}
//
//	// updateMessage.setUid(downloadControlMessage.getUid());
//	// updateMessage.action = ListUpdateMessage.ITEM_ADDED;
//	// Random RNG = new Random();
//	// Integer next = RNG.nextInt();
//	// String[] array = new String[] {"1", "tester", "12", "2", "true"};
//	// array[0] = next.toString();
//	// updateMessage.setArgs(array);
//	// updateMessage.send();
//
//}
