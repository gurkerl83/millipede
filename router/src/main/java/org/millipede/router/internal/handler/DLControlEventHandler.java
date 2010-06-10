package org.millipede.router.internal.handler;

import java.io.IOException;
import java.util.concurrent.Future;

import org.merapi.helper.messages.DLControlMessage;
import org.merapi.helper.messages.DLControlRespondMessage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.torrent.internal.client.Main;

import org.milipede.modules.list.model.vo.ListVO;

public class DLControlEventHandler implements EventHandler {

	@Override
	public void handleEvent(Event event) {
		if (event.getTopic().equals(DLControlRespondMessage.DL_CONTROL_RESPOND+"/"+DLControlRespondMessage.ITEM_ADDED)) {
			sendDLControlRespondMessage(DLControlRespondMessage.ITEM_ADDED, (ListVO) event.getProperty("listVO"));
		} else if (event.getTopic().equals(DLControlRespondMessage.DL_CONTROL_RESPOND+"/"+DLControlRespondMessage.ITEM_UPDATED)) {
//			sendDLControlRespondMessage(DLControlRespondMessage.ITEM_ADDED, event.getProperty("infoHash"), event.getProperty("name"), event.getProperty("size"), event.getProperty("cols"), event.getProperty("rows"), event.getProperty("l"), event.getProperty("m"), event.getProperty("n"), event.getProperty("o"), event.getProperty("progress"));
			sendDLControlRespondMessage(DLControlRespondMessage.ITEM_UPDATED, (ListVO) event.getProperty("listVO"));
		}

	}
	
//	public void sendDLControlRespondMessage(String action, String infoHash, String name, long size, int cols, int rows,
//			long l, long m, long n, long o, long progress) {
//		DLControlRespondMessage dlControlRespondMessage = new DLControlRespondMessage();
//
//		dlControlRespondMessage.action = action;
//		dlControlRespondMessage.infoHash = infoHash;
//		dlControlRespondMessage.name = name;
//		dlControlRespondMessage.size = size;
//		dlControlRespondMessage.cols = cols;
//		dlControlRespondMessage.rows = rows;
//
//		//evtl nach ListUpdateMessage
//		dlControlRespondMessage.ldB = l;
//		dlControlRespondMessage.ldT = m;
//		dlControlRespondMessage.luB = n;
//		dlControlRespondMessage.luT = o;
//		dlControlRespondMessage.progress = progress;
//		System.out.println("Progress:" + progress);
//		dlControlRespondMessage.send();
//
//	}
	
	public void sendDLControlRespondMessage(String action, ListVO listVO) {
		DLControlRespondMessage dlControlRespondMessage = new DLControlRespondMessage();
		
		System.out.println("infoHash: " + listVO.getInfoHash() + 
				"   transfered: " + listVO.getLdT());
		dlControlRespondMessage.action = action;
//		dlControlRespondMessage.list = listVO;

//		dlControlRespondMessage.action = action;
////		dlControlRespondMessage.infoHash = listVO.getInfoHash();
////		dlControlRespondMessage.name = listVO.getName();
////		dlControlRespondMessage.size = listVO.getSize();
////		dlControlRespondMessage.cols = 0;
////		dlControlRespondMessage.rows = 0;
////
////		//evtl nach ListUpdateMessage
////		dlControlRespondMessage.ldB = listVO.getLdB();
////		dlControlRespondMessage.ldT = listVO.getLdT();
////		dlControlRespondMessage.luB = listVO.getLuB();
////		dlControlRespondMessage.luT = listVO.getLuT();
////		dlControlRespondMessage.progress = listVO.getProgress();

                dlControlRespondMessage.list = listVO;

		dlControlRespondMessage.send();
		
	}
}
