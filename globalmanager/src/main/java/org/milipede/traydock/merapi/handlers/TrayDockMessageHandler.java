package org.milipede.traydock.merapi.handlers;

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.milipede.traydock.merapi.handlers;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import org.gudy.azureus2.core3.download.DownloadManager;
//import org.gudy.azureus2.core3.download.DownloadManagerListener;
//import org.gudy.azureus2.core3.global.GlobalManager;
//import org.gudy.azureus2.core3.global.impl.GlobalManagerImpl;
//import org.merapi.handlers.MessageHandler;
//import org.merapi.helper.messages.DLControlMessage;
//import org.merapi.helper.messages.DLControlRespondMessage;
//import org.merapi.helper.messages.ListUpdateMessage;
//import org.merapi.messages.IMessage;
//import org.milipede.traydock.merapi.messages.TrayDockMessage;
//import org.torrent.client.MainService;
//import org.torrent.internal.client.Main;
//
///**
// *
// * @author gurkerl
// */
//public class TrayDockMessageHandler extends MessageHandler {
//
////	private static DLControlRequestHandler instance = new DLControlRequestHandler();
////
////	/**
////	 * Statische Methode, liefert die einzige Instanz dieser Klasse zurück
////	 */
////	public static DLControlRequestHandler getInstance() {
////		return instance;
////	}
////
////	private File file;
////
////	 private MainService service;
//
//	/**
//	 * Default-Konstruktor, der nicht außerhalb dieser Klasse aufgerufen werden
//	 * kann
//	 */
//
//        private GlobalManager _gm;
//	public TrayDockMessageHandler(GlobalManager gm) {
//
//                super(TrayDockMessage.ASK_IT);
//                _gm = gm;
////		service = new Main();
//	}
//
//	@Override
//	public void handleMessage(IMessage message) {
//		if (message instanceof TrayDockMessage) {
//                    TrayDockMessage m = (TrayDockMessage) message;
//                    System.out.println("TrayMessage Received");
//                    if(m.question.equals(TrayDockMessage.PAUSE_DOWNLOADS)) {
//                        _gm.pauseDownloads();
////                        GlobalManagerImpl.core.getGlobal_manager().pauseDownloads();
//                        sendDLControlRespondMessage(TrayDockMessage.PAUSE_DOWNLOADS, TrayDockMessage.PAUSE_DOWNLOADS + " queried");
//                    } else if(m.question.equals(TrayDockMessage.RESUME_DOWNLOADS)) {
//                        _gm.resumeDownloads();
////                        GlobalManagerImpl.core.getGlobal_manager().resumeDownloads();
//                        sendDLControlRespondMessage(TrayDockMessage.RESUME_DOWNLOADS, TrayDockMessage.RESUME_DOWNLOADS + " queried");
//                    }
//                }
//	}
//
//
//	public void sendDLControlRespondMessage(String question, String answer){
//		TrayDockMessage message = new TrayDockMessage();
//                message.question=question;
//                message.answer=answer;
//                message.send();
//	}
//
//}
