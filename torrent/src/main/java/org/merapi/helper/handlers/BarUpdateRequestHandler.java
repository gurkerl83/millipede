/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.merapi.helper.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.merapi.handlers.MessageHandler;
import org.merapi.helper.messages.BarUpdateRequestMessage;
import org.merapi.helper.messages.BarUpdateRespondMessage;
import org.merapi.helper.messages.DLControlMessage;
import org.merapi.helper.messages.DLControlRespondMessage;
import org.merapi.helper.messages.DetailInfoMessage;
import org.merapi.helper.messages.ListUpdateMessage;
import org.merapi.helper.vo.TrackerVO;
import org.merapi.internal.Bridge;
import org.merapi.messages.IMessage;
import org.merapi.messages.Message;
import org.torrent.client.MainService;
import org.torrent.internal.client.Main;
import org.torrent.internal.data.MetaInfoFile;
import org.torrent.internal.data.TorrentMetaInfo;

/**
 * 
 * @author gurkerl
 */
public class BarUpdateRequestHandler extends MessageHandler {

	private static BarUpdateRequestHandler instance = new BarUpdateRequestHandler();

	/**
	 * Statische Methode, liefert die einzige Instanz dieser Klasse zurück
	 */
	public static BarUpdateRequestHandler getInstance() {
		return instance;
	}

	private File file;

	 private MainService service;

	 
	/**
	 * Default-Konstruktor, der nicht außerhalb dieser Klasse aufgerufen werden
	 * kann
	 */
	public BarUpdateRequestHandler() {
		super(BarUpdateRequestMessage.REQUEST_BAR_DATA);
//		service = new Main();
	}

	@Override
	public void handleMessage(IMessage message) {
		if (message instanceof BarUpdateRequestMessage) {

			System.out.println("BarUpdateRequestMessage received on backend");

			BarUpdateRequestMessage barUpdateRequestMessage = (BarUpdateRequestMessage) message;

			//get and fill data corresponding to infoHash
			Main.queue.containsKey(barUpdateRequestMessage.infoHash);
		
//			BarUpdateRequestHandler.sendAddBarData(BarUpdateRespondMessage.ADD_BAR_DATA, barUpdateRequestMessage.infoHash);
			
			MetaInfoFile tmi = Main.getInstance().map.get(barUpdateRequestMessage.infoHash);
			
			BarUpdateRequestHandler.sendUpdateBarData(BarUpdateRespondMessage.ADD_BAR_DATA, tmi.getInfoHash().asHexString(), tmi.getDataInfo().getPiecesCount(), 1, barUpdateRequestMessage.lostCol);
		} else {
			System.out.println("invalid message received on backend");
		}
	}

	// @Override
	// public File getFile() {
	// return this.file;
	// }

	public static void sendAddBarData(String action, String infoHash) {
		BarUpdateRespondMessage barUpdate = new BarUpdateRespondMessage();
		barUpdate.action = action;
		barUpdate.infoHash = infoHash;
		barUpdate.send();
	}
	
	public static void sendUpdateBarData(String action, String infoHash, int cols, long rows, int lostCol) {
			BarUpdateRespondMessage barUpdate = new BarUpdateRespondMessage();
			barUpdate.action = action;
			barUpdate.infoHash = infoHash;
			barUpdate.cols = cols;
			barUpdate.rows = rows;
			
			barUpdate.lostCol = lostCol;
			barUpdate.send();
	}
	
	public void sendAddDetailedData(String action) {
		BarUpdateRespondMessage barUpdate = new BarUpdateRespondMessage();
		barUpdate.action = action;
		barUpdate.dateienPfad = "testPfad";
		barUpdate.peersFlags = "testFlags";
		barUpdate.peersIP = "testIP";
		barUpdate.peersProgramm = "testProgramm";
		barUpdate.trackerName = "testTrackerName";
		barUpdate.trackerStatus = "trackerStatus";
		barUpdate.send();
	}
	
	public void sendUpdateDetailedData(String action) {
		BarUpdateRespondMessage barUpdate = new BarUpdateRespondMessage();
		barUpdate.action = action;
		
		barUpdate.send();
	}
	
	public void sendDetailInfoMessage(String action, String subAction) {
		DetailInfoMessage detailInfoMessage = new DetailInfoMessage();
		detailInfoMessage.action = action;
		detailInfoMessage.subAction = subAction;
		
		detailInfoMessage.trackerName = "testTrackerName";
		detailInfoMessage.trackerStatus = "trackerStatus";
		detailInfoMessage.trackerGeladen = "trackerGeladen";
		detailInfoMessage.trackerNaechstUpdate = "trackerNaechstUpdate";
		detailInfoMessage.trackerPeers = 10;
		detailInfoMessage.trackerSeeds = 15;
		
////	detailInfoMessage.trackerVO = new TrackerVO("testTracker", "testStatus", "testNaechstUpdate", 0, 0, "testTrackerGeladen");
		detailInfoMessage.send();
	
//		Message msg = new Message();
//        msg.setType("messageFromJava");
//        msg.setData(new TrackerVO("testTracker", "testStatus", "testNaechstUpdate", 0, 0, "testTrackerGeladen"));
//        try{
//                Bridge.getInstance().sendMessage(msg);
//        }catch(Exception e){
//                System.out.println(e);
//        } 
	}
	
//	public void sendListUpdateMessage(String action, String infoHash) {
//		ListUpdateMessage updateMessage = new ListUpdateMessage();
//
//		updateMessage.action = action;
//		updateMessage.infoHash = infoHash;
//		updateMessage.send();
//	}
	
//	public void sendDLControlRespondMessage(String action, String infoHash){
//		DLControlRespondMessage dlControlRespondMessage = new DLControlRespondMessage();
//		
//		dlControlRespondMessage.action = action;
//		dlControlRespondMessage.infoHash = infoHash;
//		dlControlRespondMessage.send();
//	}

	// updateMessage.setUid(downloadControlMessage.getUid());
	// updateMessage.action = ListUpdateMessage.ITEM_ADDED;
	// Random RNG = new Random();
	// Integer next = RNG.nextInt();
	// String[] array = new String[] {"1", "tester", "12", "2", "true"};
	// array[0] = next.toString();
	// updateMessage.setArgs(array);
	// updateMessage.send();

}
