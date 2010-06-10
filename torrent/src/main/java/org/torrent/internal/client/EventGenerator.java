/*
 * © 2001-2009, Progress Software Corporation and/or its subsidiaries or affiliates.  
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  
 * */
package org.torrent.internal.client;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

import org.merapi.helper.messages.DLControlMessage;
import org.merapi.helper.messages.DLControlRespondMessage;
import org.milipede.modules.list.model.vo.ListVO;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;


/*
 * Generates an event every five seconds with a sequence number 
 * */
public class EventGenerator extends Thread{

	private final int delay = 1000;
    // private final String topic = "es/schaaf/test";
	
    private ServiceTracker eaTrack;
    private BundleContext bctx;
    private boolean running = true;
    private Queue<Event> queue = new LinkedList<Event>();
    
	
	public EventGenerator(BundleContext ctx) {
		bctx = ctx;
		
		eaTrack = new ServiceTracker(bctx,EventAdmin.class.getName(),null);
		eaTrack.open();
	}

	public void run() {
		int sequenceNumber = 0;
		while(running){
			EventAdmin ea = getEventAdmin();
			if (ea != null) {
                // Dictionary props = new Hashtable();
                // props.put("es.schaaf.distribute", new Boolean(true));
                // props.put("seq", new Integer(sequenceNumber));
                // Event e = new Event(topic, props);
                while (!queue.isEmpty()) {
                    ea.postEvent(queue.poll());
                }
                System.out.println("EventGenerator:  posted event "
                        + sequenceNumber);
                ++sequenceNumber;
            } else {
                System.out.println("!... no EventAdmin ...!");
            }
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {}
		}
	}

	private EventAdmin getEventAdmin(){
		return (EventAdmin)eaTrack.getService();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
//	public void sendMessage(String action, String infoHash, String name, long size, int cols, int rows,
//			long l, long m, long n, long o, long progress) {
//		String topic = DLControlRespondMessage.DL_CONTROL_RESPOND+"/"+action;
//        Dictionary props = new Hashtable();
//        props.put("infoHash", infoHash);
//        props.put("name", name);
//        props.put("size", size);
//        props.put("cols", cols);
//        props.put("rows", rows);
//        props.put("l", l);
//        props.put("m", m);
//        props.put("n", n);
//        props.put("o", o);
//        props.put("progress", progress);
//        Event e = new Event(topic, props);
//        queue.add(e);
//        return;
//	}
	
	public void sendMessage(String action, ListVO listVO) {
		String topic = DLControlRespondMessage.DL_CONTROL_RESPOND+"/"+action;
        Dictionary props = new Hashtable();
        props.put("listVO", listVO);
        Event e = new Event(topic, props);
        queue.add(e);
        return;
	}
}
