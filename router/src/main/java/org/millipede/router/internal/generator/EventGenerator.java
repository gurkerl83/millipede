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
package org.millipede.router.internal.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.merapi.handlers.MessageHandler;
import org.merapi.helper.handlers.DLControlRequestHandler;
import org.merapi.helper.messages.ConfigurationMessage;
import org.merapi.helper.messages.DLControlMessage;
import org.merapi.helper.messages.BarUpdateRequestMessage;
import org.merapi.helper.messages.CollaborationMessage;
import org.merapi.messages.IMessage;
//import org.millipede.merapi.messages.CollaborationMessage;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.torrent.internal.client.Main;

/*
 * Generates an event every five seconds with a sequence number 
 * */
public class EventGenerator extends MessageHandler implements Runnable {

    private final int delay = 5000;
    // private final String topic = "es/schaaf/test";
    private ServiceTracker eaTrack;
    private BundleContext bctx;
    private boolean running = true;
    private Queue<Event> queue = new LinkedList<Event>();
    private static File file;

    public EventGenerator(BundleContext ctx) {
        bctx = ctx;

        eaTrack = new ServiceTracker(bctx, EventAdmin.class.getName(), null);
        eaTrack.open();

    }

    public void run() {
        int sequenceNumber = 0;
        while (running) {
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
            } catch (InterruptedException e) {
            }
        }
    }

    private EventAdmin getEventAdmin() {
        return (EventAdmin) eaTrack.getService();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    /*
     * ToDo: Replace if else if with switch case --> enum message identification
     *
     * @see
     * org.merapi.handlers.MessageHandler#handleMessage(org.merapi.messages.
     * IMessage)
     */
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
                    Logger.getLogger(DLControlRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (file.exists()) {
                    try {
                        fop.write(downloadControlMessage.fileData);
                    } catch (IOException ex) {
                        Logger.getLogger(
                                DLControlRequestHandler.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                    try {
                        fop.flush();
                    } catch (IOException ex) {
                        Logger.getLogger(
                                DLControlRequestHandler.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                    try {
                        fop.close();
                    } catch (IOException ex) {
                        Logger.getLogger(
                                DLControlRequestHandler.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                    System.out.println("The data has been written EventGenerator");
                }

                String topic = DLControlMessage.DL_CONTROL + "/" + downloadControlMessage.action;
                Dictionary props = new Hashtable();
                props.put("file", file.getAbsolutePath());
                Event e = new Event(topic, props);
                queue.offer(e);
                return;

            } else if (downloadControlMessage.action.equals(DLControlMessage.REMOTE_FILE)) {

                System.out.println("Download Remote Torrent triggered");
                // downloadRemoteTorrent(downloadControlMessage.fileReference);

                String topic = DLControlMessage.DL_CONTROL + "/" + downloadControlMessage.action;
                Dictionary props = new Hashtable();
                props.put("fileReference", downloadControlMessage.fileReference);
                Event e = new Event(topic, props);
                queue.offer(e);
                return;

            }


            // try {
            // Future<Download> dl =
            // Main.getInstance().starter(file.getAbsolutePath());
            //
            // // DownloadBridge dlmgr = (DownloadBridge) _dlmgr;
            // // dlmgr.addExternalDownload(dl);
            // // _dlmgr.resumeDownloads();
            //
            //
            // // DownloadManager dlmgr =
            // _gm.addDownloadManager(file.getAbsolutePath(),
            // "C:\\Development\\testDownloads\\");
            // // _gm.resumeDownload(dlmgr);
            //
            //
            // // Main.getInstance().startTorrentTask(file.getAbsolutePath());
            // } catch (IOException e) {
            // // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

        } else if (message instanceof BarUpdateRequestMessage) {
            BarUpdateRequestMessage barUpdateRequestMessage = (BarUpdateRequestMessage) message;
//			String topic = barUpdateRequestMessage.action;
//			Dictionary props = new Hashtable();
//			props.put("file", file.getAbsolutePath());
//			Event e = new Event(topic, props);
//			queue.offer(e);
            return;


        } else if (message instanceof ConfigurationMessage) {
            ConfigurationMessage configurationMessage = (ConfigurationMessage) message;

//			String topic = configurationMessage.action;
//			Dictionary props = new Hashtable();
//			props.put("file", file.getAbsolutePath());
//			Event e = new Event(topic, props);
//			queue.offer(e);
            return;
//        } else if (message instanceof CollaborationMessage) {
//            CollaborationMessage collaborationMessage = (CollaborationMessage) message;
//            if (collaborationMessage.action.equals(CollaborationMessage.CONNECT)) {
//                String topic = collaborationMessage.action;
//                Dictionary props = new Hashtable();
//                props.put("url", collaborationMessage.url);
//                Event e = new Event(topic, props);
//                queue.offer(e);
//                return;
//            }

        } else {
            System.out.println("Unknown message type recieved");
        }
    }
}
