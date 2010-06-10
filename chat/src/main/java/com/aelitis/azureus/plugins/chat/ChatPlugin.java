/*
 * Created on 28 févr. 2005
 * Created by Olivier Chalouhi
 * 
 * Copyright (C) 2004 Aelitis SARL, All rights Reserved
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * AELITIS, SARL au capital de 30,000 euros,
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
package com.aelitis.azureus.plugins.chat;

import com.aelitis.azureus.core.AzureusCore;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.graphics.ImageData;
//import org.gudy.azureus2.plugins.Plugin;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.config.ConfigParameter;
import org.gudy.azureus2.plugins.config.ConfigParameterListener;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.savelocation.SaveLocationChange;
import org.gudy.azureus2.plugins.torrent.Torrent;
//import org.gudy.azureus2.plugins.ui.UIInstance;
//import org.gudy.azureus2.plugins.ui.UIManager;
//import org.gudy.azureus2.plugins.ui.UIManagerListener;
//import org.gudy.azureus2.plugins.ui.config.Parameter;
//import org.gudy.azureus2.plugins.ui.config.PluginConfigUIFactory;
//import org.gudy.azureus2.plugins.ui.tables.TableColumn;
//import org.gudy.azureus2.plugins.ui.tables.TableManager;
//import org.gudy.azureus2.plugins.utils.Formatters;
//import org.gudy.azureus2.plugins.utils.LocaleUtilities;
//import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;

import com.aelitis.azureus.plugins.chat.messaging.MessageListener;
import com.aelitis.azureus.plugins.chat.peer.PeerController;
import com.aelitis.azureus.plugins.chat.peer.impl.PeerControllerImpl;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.plugins.messaging.MessageManager;
import org.gudy.azureus2.plugins.torrent.TorrentException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
//import com.aelitis.azureus.plugins.chat.ui.ChatPanelsManager;
//import com.aelitis.azureus.plugins.chat.ui.MyTorrentsActivityIndicator;

import org.gudy.azureus2.pluginsimpl.locale.utils.FormattersImpl;

import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.download.savelocation.SaveLocationManager;
import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentManagerImpl;
import org.torrent.internal.client.Client;


public class ChatPlugin implements MessageListener, ConfigParameterListener, BundleActivator {
	
	public static final String ID_VIEW_TITLE = "chat.config.title";
  
  private static final String COLUMN_ID_CHAT_ACTIVITY = "ChatActivityColumn";
  
//  private PluginInterface plugin_interface;
//  private UISWTInstance swtui;
  private PeerController  controller;
//  private LoggerChannel   logger;
  
  //Listeners for the UI
  private ArrayList listeners;
  // 1 to 1 relationship with listeners (cheap bidi map) 
  private ArrayList listenersDownload;
  
  private String nick;
  private boolean active;
  
//  private String  resInactive = "com/aelitis/azureus/plugins/chat/ui/icons/red.png";
//  public  Image   imgInactive;
//
//  private String  resActive = "com/aelitis/azureus/plugins/chat/ui/icons/dgreen.png";
//  public  Image   imgActive;
//
//  private String  resActivity = "com/aelitis/azureus/plugins/chat/ui/icons/lgreen.png";
//  public  Image   imgActivity;
  

//  private String resTorrent = "com/aelitis/azureus/plugins/chat/resources/channel.torrent";

private String resTorrent = "C:\\channel.torrent";
//    private String resTorrent = "C:\\Development\\testDownloads\\test.torrent";
  public Torrent genericTorrent;

  private static FormattersImpl formatters = null;
  
  public MessageManager mgr;
  public AzureusCore core;
  public DownloadManager dlmgr;
  
  public void
  initialize()
  {
//    plugin_interface  = _pi;
    
    formatters = new FormattersImpl();//plugin_interface.getUtilities().getFormatters();
    
    
//    genericTorrent = loadTorrent(resTorrent);
    genericTorrent = this.loadTorrent(resTorrent);
    System.out.println("generic Torrent: " + genericTorrent);
    this.addChannel("Download");
//    PluginConfigUIFactory factory = plugin_interface.getPluginConfigUIFactory();
//    Parameter parameters[] = new Parameter[2];
//    parameters[0] = factory.createBooleanParameter("enable","chat.config.enable",true);
//    parameters[1] = factory.createStringParameter("nick","chat.config.nick","");
//    parameters[1].addConfigParameterListener(this);
//    plugin_interface.addConfigUIParameters(parameters,ID_VIEW_TITLE);
    
//    nick = plugin_interface.getPluginconfig().getPluginStringParameter("nick","Guest" + (int) (Math.random() * 100000));
//    active = plugin_interface.getPluginconfig().getPluginBooleanParameter("enable",true);
    nick = "tester";
    active = true;
    if(active) {    
      listeners = new ArrayList();
      listenersDownload = new ArrayList();
      
      controller = new PeerControllerImpl(this);    
      controller.addMessageListener(this);
      controller.initialize();
      controller.startPeerProcessing();


      
//  	plugin_interface.getUIManager().addUIListener(
//			new UIManagerListener()
//			{
//				public void
//				UIAttached(
//					UIInstance		instance )
//				{
//					if ( instance instanceof UISWTInstance ){
//
//						swtui = (UISWTInstance)instance;
//
//					    imgInactive = loadImage(resInactive);
//					    imgActive   = loadImage(resActive);
//					    imgActivity = loadImage(resActivity);
//
//					  ChatPanelsManager cpm = new ChatPanelsManager(ChatPlugin.this);
//						swtui.addView(UISWTInstance.VIEW_MYTORRENTS, "Chat", cpm);
//						swtui.addView(UISWTInstance.VIEW_MAIN, "CreateChat", cpm);
//
//						addMyTorrentsColumn();
//					}
//				}
//
//				public void
//				UIDetached(
//					UIInstance		instance )
//				{
//
//				}
//			});
//
    }
  }
  
//  public PluginInterface getPluginInterface() {
//    return plugin_interface;
//  }
  
//  public LocaleUtilities getLocaleUtils() {
//  	return plugin_interface.getUtilities().getLocaleUtilities();
//  }
//
//  public String getTitle() {
//    LocaleUtilities localeUtils = getLocaleUtils();
//  	return localeUtils.getLocalisedMessageText(ChatPlugin.ID_VIEW_TITLE);
//  }
  
  public void addMessageListener(MessageListener listener, Download download) {
    synchronized (listeners) {
      listeners.add(listener);
      listenersDownload.add(download);
    }

    listener.downloadAdded(download);
    if(controller.isDownloadActive(download)) {
      listener.downloadActive(download);
    } else {
      listener.downloadInactive(download);
    }
  }
  
  public void removeMessageListener(MessageListener listener) {
    synchronized (listeners) {
    	int idx = listeners.indexOf(listener);
    	if (idx >= 0) {
        listenersDownload.remove(idx);
        listeners.remove(listener);
    	}
    }
    
  }
  
  public void downloadAdded(Download download) {
    synchronized (listeners) {
    	for (int i = 0; i < listenersDownload.size(); i++) {
    		Download lDownload = (Download) listenersDownload.get(i);
    		if (download.equals(lDownload)) {
      		MessageListener listener = (MessageListener) listeners.get(i);
      		listener.downloadAdded(download);
    		}
    	}
    }    
  }
  
  public void downloadRemoved(Download download) {
    synchronized (listeners) {
    	for (int i = 0; i < listenersDownload.size(); i++) {
    		Download lDownload = (Download) listenersDownload.get(i);
    		if (download.equals(lDownload)) {
      		MessageListener listener = (MessageListener) listeners.get(i);
      		listener.downloadRemoved(download);
    		}
    	}
    }    
  }
  
  public void downloadActive(Download download) {   
    synchronized (listeners) {
    	for (int i = 0; i < listenersDownload.size(); i++) {
    		Download lDownload = (Download) listenersDownload.get(i);
    		if (download.equals(lDownload)) {
      		MessageListener listener = (MessageListener) listeners.get(i);
      		listener.downloadActive(download);
    		}
    	}
    }    
  }
  
  public void downloadInactive(Download download) {   
    synchronized (listeners) {
    	for (int i = 0; i < listenersDownload.size(); i++) {
    		Download lDownload = (Download) listenersDownload.get(i);
    		if (download.equals(lDownload)) {
      		MessageListener listener = (MessageListener) listeners.get(i);
      		listener.downloadInactive(download);
    		}
    	}
    }    
  }
  
  public void messageReceived(Download download,byte[] sender,String nick,String text) {
    synchronized (listeners) {
    	for (int i = 0; i < listenersDownload.size(); i++) {
    		Download lDownload = (Download) listenersDownload.get(i);
    		if (download.equals(lDownload)) {
      		MessageListener listener = (MessageListener) listeners.get(i);
      		listener.messageReceived(download, sender, nick, text);
    		}
    	}
    }    
  }
  
  public void sendMessage(Download download,String text) {
  	if (download == null)
  		return;

    byte[] peerID = download.getDownloadPeerId();
    if(peerID != null) {
      controller.sendMessage(download,peerID,nick,text);      
    } else {
      messageReceived(download,null,"System","/me : Torrent isn't running, message can't be delivered");
    }
  }
  
  public void configParameterChanged(ConfigParameter param) {
//    nick = plugin_interface.getPluginconfig().getPluginStringParameter("nick","Guest" + (int) (Math.random() * 100000));
    nick="tester";
      if(nick.startsWith("System") || nick.startsWith("system")) {
      nick = "Guest" + (int) (Math.random() * 100000);
    }
  }
  
//  private Image loadImage(String res) {
//    InputStream is = this.getClass().getClassLoader().getResourceAsStream(res);
//    if (is != null) {
//        ImageData imageData = new ImageData(is);
//        return new Image(swtui.getDisplay(), imageData);
//    }
//    return null;
//  }
  
  private Torrent loadTorrent(String res) {
        try {
//    InputStream is = this.getClass().getClassLoader().getResourceAsStream(res);
//    if (is != null) {
//      try {
            File file = new File(res);

            System.out.println("FileRef: " + file.exists());
            return TorrentManagerImpl.getSingleton().createFromBEncodedFile(file);
//            return TorrentManagerImpl.getSingleton().createFromBEncodedInputStream(is);
//          return plugin_interface.getTorrentManager().createFromBEncodedInputStream(is);
//      } catch(Exception e) {
//        e.printStackTrace();
//        return null;
        } catch (TorrentException ex) {
            Logger.getLogger(ChatPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
//            return TorrentManagerImpl.getSingleton().createFromBEncodedInputStream(is);
//          return plugin_interface.getTorrentManager().createFromBEncodedInputStream(is);
//      } catch(Exception e) {
//        e.printStackTrace();
        return null;
      }

//  private Torrent loadTorrent(String res) {
////    InputStream is = this.getClass().getClassLoader().getResourceAsStream(res);
////    InputStream is = getClass().getResourceAsStream(res);
//      ClassLoader cl = getClass().getClassLoader();
//      System.out.println("ClassLoader: " + cl);
//    InputStream is = cl.getResourceAsStream(res);
//      System.out.println("InputStream: " + is);
//    if (is != null) {
//      try {
//        return TorrentManagerImpl.getSingleton().createFromBEncodedInputStream(is);
//      } catch(Exception e) {
//        e.printStackTrace();
//        return null;
//      }
//
//    }
//    return null;
//  }


    
    
  
  
//  private void addMyTorrentsColumn() {
//
//    MyTorrentsActivityIndicator activityIndicator = new MyTorrentsActivityIndicator(this, controller, swtui);
//
//    addIndicatorToTable(TableManager.TABLE_MYTORRENTS_INCOMPLETE,activityIndicator);
//    addIndicatorToTable(TableManager.TABLE_MYTORRENTS_COMPLETE,activityIndicator);
//  }
//
//  private void addIndicatorToTable(String tableID,MyTorrentsActivityIndicator activityIndicator) {
//    UIManager uiManager = plugin_interface.getUIManager();
//    TableManager tableManager = uiManager.getTableManager();
//    TableColumn activityColumn = tableManager.createColumn(tableID, COLUMN_ID_CHAT_ACTIVITY);
//
//    activityColumn.setAlignment(TableColumn.ALIGN_CENTER);
//    activityColumn.setPosition(2);
//    activityColumn.setRefreshInterval(TableColumn.INTERVAL_GRAPHIC);
//    activityColumn.setType(TableColumn.TYPE_GRAPHIC);
//
//    activityColumn.addCellRefreshListener(activityIndicator);
//
//    tableManager.addColumn(activityColumn);
//  }

  /**
   * @return Returns the nick.
   */
  public String getNick() {
    return nick;
  }
  

  /**
   * @param nick The nick to set.
   */
//  public void setNick(String nick) {
//    this.nick = nick;
//    plugin_interface.getPluginconfig().setPluginParameter("nick",nick);
//  }
  
  public void addIgnore(String nick) {
    if(controller != null) controller.ignore(nick);
  }
  
  public Torrent getChannelTorrent(String channelName) {
    try {
    Map genericMap = genericTorrent.writeToMap();
    Map info = (Map) genericMap.get("info");
    info.put("name",channelName.getBytes());
    info.put("name.utf8",channelName.getBytes("UTF-8"));
    genericMap.put("info",info);
//
////    byte[] channelTorrent = plugin_interface.getUtilities().getFormatters().bEncode(genericMap);
    byte[] channelTorrent =  new FormattersImpl().bEncode(genericMap);
//    Torrent result = plugin_interface.getTorrentManager().createFromBEncodedData(channelTorrent);
    Torrent result = TorrentManagerImpl.getSingleton().createFromBEncodedData(channelTorrent);
    result.setAnnounceURL(new URL("dht://chat.dht/announce"));
    return result;
    } catch(Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public Download addChannel(String channelName) {
    Torrent torrent = getChannelTorrent(channelName);
//    String savePath = plugin_interface.getPluginDirectoryName();
    
    try {
//      File saveDir = new File(savePath,"channels" +  File.separator );
      File saveDir = new File(Client.DIR,"channels" +  File.separator );
      saveDir.mkdir();
//      Download dl = plugin_interface.getDownloadManager().addDownload(torrent,null,saveDir);

//      dlmgr.setSaveLocationManager(new SaveLocationManager() {
//
//                @Override
//                public SaveLocationChange onInitialization(Download download, boolean for_move, boolean on_event) {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//
//                @Override
//                public SaveLocationChange onCompletion(Download download, boolean for_move, boolean on_event) {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//
//                @Override
//                public SaveLocationChange onRemoval(Download download, boolean for_move, boolean on_event) {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//
//      });


//      Download dl = dlmgr.addDownload(torrent, null, saveDir);
      Download dl = dlmgr.addDownload(torrent, null, saveDir);
      dl.setForceStart(true);
      System.out.println("Download added: " + dl.getName());
      return dl;
    } catch(Exception e) {
      e.printStackTrace();
    }

    return null;
  }
  
//  public UISWTInstance getSWTUI() {
//  	return swtui;
//  }
  
  public static byte[] bEncode(Map map) {
  	if (formatters == null) {
  		return new byte[0];
  	}
  	try {
			return formatters.bEncode(map);
		} catch (IOException e) {
			e.printStackTrace();
  		return new byte[0];
		}
  }

  public static Map bDecode(byte[] bytes) {
  	if (formatters == null) {
  		return new HashMap();
  	}
  	try {
			return formatters.bDecode(bytes);
		} catch (IOException e) {
			e.printStackTrace();
  		return new HashMap();
		}
  }
    
    public void start(BundleContext context) throws Exception {
        ServiceReference messageManager = context.getServiceReference(MessageManager.class.getName());
        mgr = (MessageManager) context.getService(messageManager);
        ServiceReference azureuCore = context.getServiceReference(AzureusCore.class.getName());
        core = (AzureusCore) context.getService(azureuCore);

//        ServiceReference gmRef = context.getServiceReference(GlobalManager.class.getName());
//        gm = (GlobalManager) context.getService(gmRef);

        ServiceReference dlmgrRef = context.getServiceReference(DownloadManager.class.getName());
        dlmgr = (DownloadManager) context.getService(dlmgrRef);

        initialize();



    }

    public void stop(BundleContext arg0) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
