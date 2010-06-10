/*
 * Â© 2001-2009, Progress Software Corporation and/or its subsidiaries or affiliates.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloader;
import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderCallBackInterface;
import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderFactory;
import org.gudy.azureus2.core3.torrentdownloader.impl.TorrentDownloaderManager;
//import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.Download;
import org.torrent.data.FileInfo;
import org.torrent.data.ProcessInfo;
import org.torrent.transfer.Transfer;
//import org.torrent.internal.transfer.Download;
import org.merapi.helper.messages.DLControlMessage;
import org.merapi.helper.messages.DLControlRespondMessage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class TestEventHandler implements EventHandler {

    public void handleEvent(Event event) {
        System.out.println("got an Event - remote: " + "   SEQ: " + event.getProperty("seq"));

        if (event.getTopic().equals(DLControlMessage.DL_CONTROL + "/" + DLControlMessage.LOCAL_FILE)) {
//            try {
//                Future<Transfer> dl = Main.getInstance().starter((String) event.getProperty("file"));
//                try {
//                    Main.getInstance().actions.put(dl.get().getContentInfo().getInfoHash().asHexString(), (Download) dl);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(TestEventHandler.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (ExecutionException ex) {
//                    Logger.getLogger(TestEventHandler.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
        } else if (event.getTopic().equals(DLControlMessage.DL_CONTROL + "/" + DLControlMessage.REMOTE_FILE)) {
            downloadRemoteTorrent((String) event.getProperty("fileReference"));
//		} else if (event.getTopic().equals(DLControlRespondMessage.DL_CONTROL_RESPOND + "/" + DLControlRespondMessage.SELECTED_CONTENT)) {
        } else if (event.getTopic().equals(DLControlMessage.DL_CONTROL + "/" + DLControlRespondMessage.SELECTED_CONTENT)) {
//			
//			Main.getInstance().map.put((String)event.getProperty("infoHash"), (List<FileInfo>) event.getProperty("files"));

//			Main.getInstance().getStatusMap().put((String)event.getProperty("infoHash"), "processed");
            ProcessInfo pi = Main.getInstance().getStatusMap().get((String) event.getProperty("infoHash"));
            pi.setStatus("processed");
            pi.setSavePath((String) event.getProperty("savePath"));
            pi.setFiles((List<FileInfo>) event.getProperty("files"));
            Main.getInstance().getStatusMap().put((String) event.getProperty("infoHash"), pi);
//        } else if (event.getTopic().equals(DLControlMessage.DL_CONTROL + "/" + DLControlMessage.START_TORRENT)) {
//        } else if (event.getTopic().equals(DLControlMessage.DL_CONTROL + "/" + DLControlMessage.PAUSE_TORRENT)) {
//        } else if (event.getTopic().equals(DLControlMessage.DL_CONTROL + "/" + DLControlMessage.STOP_TORRENT)) {
        } else if (event.getTopic().equals("flexspacesinfo")) {
        	System.out.println("OSGi Message: /flexspaces/info delivered");
        }
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
                if (state == TorrentDownloader.STATE_FINISHED) {
                    System.out.println("torrent file download complete. starting torrent");
                    TorrentDownloaderManager.getInstance().remove(inf);
//                    try {
//                        Future<Transfer> dl = Main.getInstance().starter(inf.getFile().getAbsolutePath());
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
////					downloadTorrent( inf.getFile().getAbsolutePath(), outputDir.getAbsolutePath() );
                } else {
                    TorrentDownloaderManager.getInstance().TorrentDownloaderEvent(state, inf);
                }
            }
        }, url, null, null, true);
        TorrentDownloaderManager.getInstance().add(downloader);
    }
}
