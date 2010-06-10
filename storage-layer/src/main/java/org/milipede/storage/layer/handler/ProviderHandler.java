/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.milipede.storage.layer.handler;

import ch.cyberduck.BrowserController;
import ch.cyberduck.MainController;
import ch.cyberduck.TransferController;
import ch.cyberduck.TransferPromptController;
import ch.cyberduck.UploadPromptController;
import ch.cyberduck.UserDefaultsPreferences;
import ch.cyberduck.WindowController;
import ch.cyberduck.core.AbstractPath;
import ch.cyberduck.core.AttributedList;
import ch.cyberduck.core.DownloadTransfer;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.LocalFactory;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathAttributes;
import ch.cyberduck.core.PathFactory;
import ch.cyberduck.core.Permission;
import ch.cyberduck.core.Preferences;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.SessionFactory;
import ch.cyberduck.core.SyncTransfer;
import ch.cyberduck.core.Transfer;
import ch.cyberduck.core.TransferAction;
import ch.cyberduck.core.TransferOptions;
import ch.cyberduck.core.TransferPrompt;
import ch.cyberduck.core.UploadTransfer;
import ch.cyberduck.core.service.DownloadTransferService;
import ch.cyberduck.handler.ResultContext;
import ch.cyberduck.handler.ResultHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.merapi.internal.Bridge;
import org.millipede.merapi.messages.ProviderMessage;
import org.openmole.misc.exception.InternalProcessingError;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import org.milipede.storage.layer.PersistenceControllerService;
import org.milipede.storage.layer.internal.PersistenceController;
import org.milipede.storage.layer.internal.ProviderController;

import ch.cyberduck.service.BrowserControllerService;
import ch.cyberduck.service.LoginController;
import ch.cyberduck.service.TransferControllerService;
//import ch.cyberduck.ui.cocoa.foundation.NSDictionary;
//import ch.cyberduck.ui.cocoa.foundation.NSDictionary;

import java.beans.XMLEncoder;
import java.io.FileOutputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.merapi.messages.Message;
import org.millipede.router.vo.ProviderVO;

import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.DatabaseException;

import ch.cyberduck.i18n.BundleLocale;
import ch.cyberduck.model.FinderLocal;
import ch.cyberduck.serializer.PlistSerializer;
import ch.cyberduck.serializer.PlistDeserializer;

/**
 * 
 * @author gurkerl
 */
public class ProviderHandler implements EventHandler, ResultHandler {

	private LoginController loginService;

	public LoginController getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginController loginService) {
		this.loginService = loginService;
	}

	// private BrowserController browserController;
//	private BrowserControllerService browserController;
//
//	public BrowserControllerService getBrowserController() {
//		return browserController;
//	}
//
//	public void setBrowserController(BrowserControllerService browserController) {
//		this.browserController = browserController;
//	}

	private TransferControllerService transferController;
	private DownloadTransferService downloadTransfer;

	// private PersistenceService persistentceController;
	// private PersistenceController persistenceController;

	// public PersistenceControllerService getPersistentceController() {
	// return persistentceController;
	// }
	//

	// public PersistenceController getPersistenceController() {
	// return persistenceController;
	// }
	//
	// public void setPersistenceController(PersistenceController
	// persistenceController) {
	// this.persistenceController = persistenceController;
	// }

	
	private Session s = null;
//	private BrowserController browserController = new BrowserController();
	BrowserController doc = null;
	
	@Override
	public void handleEvent(Event event) {
		try {
			PersistenceController.getInstance().startup();
		} catch (BasicFileOperationDatabaseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (DatabaseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// if
		// (event.getTopic().equals(ProviderMessage.INIT_PROVIDER_LIST_REQUEST+"/"+"test"))
		// {
		
		
		if (event.getTopic().equals("collaboration/init")) {
			ProviderMessage respondMessage = new ProviderMessage();
			// respondMessage.setType(ProviderMessage.PROVIDER_MESSAGE);
			respondMessage.processType = ProviderMessage.INIT_PROVIDER_LIST_RESPOND;

			
			if (event.getProperty("providerType").equals("storage")) {
				respondMessage.providerType = "storage";
				respondMessage.provider = ProviderController.getInstance()
						.getStorage();
			} else if (event.getProperty("providerType").equals("mail")) {
				respondMessage.providerType = "mail";
				respondMessage.provider = ProviderController.getInstance()
						.getMail();
				
				int rows = doc.getSelectedBrowserView().numberOfRowsInTableView();
				
		        Path selected = doc.getSelectedPath();
		        if(null == selected || !selected.attributes.isDirectory()) {
		            selected = doc.workdir();
		        }
		        BrowserController c = new BrowserController();
//		        c.cascade();
//		        c.window().makeKeyAndOrderFront(null);
//		        final Host host = new Host(doc.getSession().getHost().getAsDictionary());
		        final Host host = new Host(doc.getSession().getHost().<Dictionary<Object, Object>>getAsDictionary());
		        host.setDefaultPath(selected.getAbsolute());
		        c.mount(host);
		        
			} else if (event.getProperty("providerType").equals("hoster")) {
				respondMessage.providerType = "hoster";
				respondMessage.provider = ProviderController.getInstance()
						.getHoster();
			} else {
				return;
			}
			respondMessage.send();

			// // respondMessage.convertToProviderVO((ArrayList<Object>)
			// event.getProperty("provider"));
			// try {
			// Bridge.getInstance().sendMessage(respondMessage);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// } else if
			// (event.getTopic().equals(ProviderMessage.LOGIN_PROVIDER_REQUEST))
			// {
			// StartUp the correct Plugin
			// if (event.getProperty("plugin").equals("storage")) {
			// if (event.getProperty("providerType").equals("storage")) {

			// try {
			// AccountActivator.pluginMgr.load("D:\\Development\\Java\\pluginmanager\\org.apache.commons.collections15-4.01.jar");
			// AccountActivator.pluginMgr.load("C:\\Users\\gurkerl\\Documents\\NetBeansProjects\\downloader\\jmilipede\\target\\jmilipede-1.0.0.jar");
			// AccountActivator.pluginMgr.load("D:\\Development\\Java\\workspace\\millipede\\cyberduck\\target\\cyberduck-1.0.0.jar");
			// AccountActivator.pluginMgr.load("http://millipede.me/libs/cyberduck-1.0.0.jar");
			//
			// } catch (InternalProcessingError ex) {
			// Logger.getLogger(ProviderHandler.class.getName()).log(Level.SEVERE,
			// null, ex);
//		} else if (event.getTopic().equals("collaboration/*")) {
			//funzt
//		} else if (event.getTopic().equals("collaboration/test")) {
			
	        FinderLocal.register();
			UserDefaultsPreferences.register();
			BundleLocale.register();

			PlistDeserializer.register();
			PlistSerializer.register();
			
//			int delay = 1000;
//			int period = 10000;
//			Timer timer = new Timer();
//			timer.scheduleAtFixedRate(new TimerTask() {
//
//				public void run() {
//					if (loginService == null) {
//						System.out.println("LoginService wasn't registered.");
//					} else {
//						System.out.println("LoginService is registered.");
//					}
//				}
//			}, delay, period);

//			if (event.getProperty("type").equals("add")) {

//				final Host h = Host.parse((String) event.getProperty("url"));
			final Host h = Host.parse("https://mydisk.se/kirk23/");
//			final Host h = Host.parse("https://webdav.mydrive.ch");
//			final Host h = Host.parse("https://docs.live.net//3fd0e6207b4a7918//Public");
//			final Host h = Host.parse("http://docs.live.net//cid-3fd0e6207b4a7918//Public");
//			final Host h = Host.parse("https://e9rtcw.docs.live.net/3fd0e6207b4a7918/test");
			// h.setCredentials((String) event.getProperty("user"), (String)
				// event.getProperty("password"));

				try {
					handleResult(h);
				} catch (BasicFileOperationDatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				h.setCredentials("kirk23", "123456");
//				h.setCredentials("te_ster44@hotmail.com", "tester44");
//				Session s = null;
				if (StringUtils.isNotEmpty(h.getDefaultPath())) {
//					if (!h.getDefaultPath().endsWith(Path.DELIMITER)) {
						this.s = SessionFactory.createSession(h);
						this.s.setLoginController(new ch.cyberduck.LoginController());
						final Path p = PathFactory.createPath(s, h
								.getDefaultPath(), Path.FILE_TYPE);
						if (StringUtils.isNotBlank(p.getExtension())) {
//							 TransferController.instance().startTransfer(new
//							 DownloadTransfer(p));
							getTransferController().startTransfer(
									new DownloadTransfer(p));
							return;
						}
//					}
				}
//				 BrowserController doc = MainController.newDocument();
				this.doc = MainController.newDocument(true);
//				 MainController.newDocument(true);
				// doc.mount(h);

				// browserController.unmount();
//				browserController.mount(h);
				
				doc.mount(h);
				
				
//				int rows = doc.getSelectedBrowserView().numberOfRowsInTableView();
//				doc.setWorkdir(doc.getForwardPath());
				
//				doc.getSession().getHost();
				
//				Local loc = h.getDownloadFolder();
				
//				getTransferController().startTransfer(new UploadTransfer(PathFactory.createPath(s, "D:\\Development\\", new Local(new File("D:\\Development\\merapi-java-1.0.0.jar")))));//LocalFactory.createLocal(new File("D:\\Development\\merapi-java-1.0.0.jar")))));
//				DownloadTransfer dt = new DownloadTransfer(PathFactory.createPath(this.s, this.s.getHost().getDefaultPath(), 1));
				//			}
		}
		// } else if (event.getProperty("plugin").equals("mail")) {
		// // AccountActivator.pluginMgr.load(null);
		// } else if (event.getProperty("plugin").equals("hoster")) {
		// // AccountActivator.pluginMgr.load(null);
		// } else
		// System.out.println("No such plugin available: " +
		// event.getProperty("plugin"));
		// }

		else if (event.getTopic().equals("collaboration/initUpload")) {
//f			browserController.setWorkdir(browserController.getForwardPath());
//			try {
//				browserController.getSession().workdir().download();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			browserController.getSession().getHost().setDownloadFolder("Development\\testDownloads");
//f			Local local = browserController.getSession().getHost().getDownloadFolder();
			
//			try {
//				AttributedList<AbstractPath> list = browserController.getSession().workdir().childs();
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
////			browserController.getSession().cache().encodeToXML();
////				 try {
////					java.util.ListIterator<AbstractPath> iterator = browserController.getSession().workdir().childs().listIterator();
////				} catch (IOException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
//				
////			s.getHost().parse("https://mydisk.se/kirk23/").getDownloadFolder();
//			
////		UploadTransfer ut = new UploadTransfer(PathFactory.createPath(s, s.getHost().getDefaultPath(), 1));
//		
////		TransferPromptController.create(null, new UploadTransfer(PathFactory.createPath(s, s.getHost().getDefaultPath(), 1)));
//		
//			TransferOptions options = new TransferOptions();
//	        options.closeSession = false;
//	        
////			Path file = null;
////			try {
////				file = PathFactory.createPath(browserController.getSession(), browserController.getSession().workdir().getAbsolute(),
////				        LocalFactory.createLocal(new File("D:\\Charon.rar")));
////			} catch (IOException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////
////                    UploadTransfer upload = new UploadTransfer(file);
////                    upload.start(new TransferPrompt() {
////                        public TransferAction prompt() {
////                            return TransferAction.ACTION_OVERWRITE;
////                        }
////                    }, options);
//			
////			try {
////				browserController.getSession().workdir().download();
////			} catch (IOException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//			
//			
////			public List<Path> getRoots() {
////		        return this.roots;
////		    }
////
////		    protected void setRoots(List<Path> roots) {
////		        this.roots = roots;
////		    }
////			Transfer _delegateDownload = new DownloadTransfer(this.getRoots());
//			Transfer _delegateDownload = new DownloadTransfer(browserController.getBackHistory());
//			_delegateDownload.start(new TransferPrompt() {
//                        public TransferAction prompt() {
//                            return TransferAction.ACTION_OVERWRITE;
//                        }
//                    }, options);
			
		
			
		}
	}
	private void handleResult(Host h) throws DatabaseException {
		ProviderMessage msg = new ProviderMessage();
		boolean inserted = false;
		// if (context.equals("Login succeeded")) {

		List<ProviderVO> providers = new ArrayList<ProviderVO>();
		// for (Host host : h) {
		ProviderVO provider = new ProviderVO();
		provider.setTitle(h.getHostname());
		provider.setCategory("storage");
		providers.add(provider);
		// getPersistenceController().addProvider(provider);
		inserted = PersistenceController.getInstance().addProvider(provider);
		// serialize provider to xml
		// TODO: transport to global repository
		String file = provider.getTitle();
		// Create file output stream.
		FileOutputStream fstream = null;
		try {
			fstream = new FileOutputStream(file);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(ProviderHandler.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		try {
			// Create XML encoder.
			XMLEncoder ostream = new XMLEncoder(fstream);

			try {
				// Write object.
				ostream.writeObject(provider);
				ostream.flush();
			} finally {
				// Close object stream.
				ostream.close();
			}
		} finally {
			try {
				// Close file stream.
				fstream.close();
			} catch (IOException ex) {
				Logger.getLogger(ProviderHandler.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
		// }

		if (inserted == true)
			msg.processType = ProviderMessage.VERIFICATION_SUCCEEDED;
		else
			msg.processType = ProviderMessage.VERIFICATION_SUCCEEDED_ALREADY_ADDED;

		msg.providerType = "storage";
		msg.provider = providers;

		msg.send();

		// String topic = "login/succeeded";
		// Dictionary props = new Hashtable();
		// Event e = new Event(topic, props);
		// queue.offer(e);
		return;
		// } else if (context.equals("Login failed")) {
		// msg.processType = ProviderMessage.VERIFICATION_FAILED;
		// msg.send();
		// // String topic = "login/failed";
		// // Dictionary props = new Hashtable();
		// // Event e = new Event(topic, props);
		// // queue.offer(e);
		// return;
		// } else {
		// System.out.println("not handled message");
		// }
	}

	/**
	 * @return the downloadTransfer
	 */
	public DownloadTransferService getDownloadTransfer() {
		return downloadTransfer;
	}

	/**
	 * @param downloadTransfer
	 *            the downloadTransfer to set
	 */
	public void setDownloadTransfer(DownloadTransferService downloadTransfer) {
		this.downloadTransfer = downloadTransfer;
	}

	public void setTransferController(
			TransferControllerService transferController) {
		this.transferController = transferController;
	}

	public TransferControllerService getTransferController() {
		return transferController;
	}

	@Override
	public void handleResult(ResultContext context, List<Host> h) {
		ProviderMessage msg = new ProviderMessage();

		if (context.equals("Login succeeded")) {

			List<ProviderVO> providers = new ArrayList<ProviderVO>();
			for (Host host : h) {
				ProviderVO provider = new ProviderVO();
				provider.setTitle(host.getHostname());
				provider.setCategory("storage");
				providers.add(provider);
				// getPersistentceController().addProvider(provider);
				// PersistenceController.getInstance().addProvider(provider);
				// serialize provider to xml
				// TODO: transport to global repository
				String file = provider.getTitle();
				// Create file output stream.
				FileOutputStream fstream = null;
				try {
					fstream = new FileOutputStream(file);
				} catch (FileNotFoundException ex) {
					Logger.getLogger(ProviderHandler.class.getName()).log(
							Level.SEVERE, null, ex);
				}

				try {
					// Create XML encoder.
					XMLEncoder ostream = new XMLEncoder(fstream);

					try {
						// Write object.
						ostream.writeObject(provider);
						ostream.flush();
					} finally {
						// Close object stream.
						ostream.close();
					}
				} finally {
					try {
						// Close file stream.
						fstream.close();
					} catch (IOException ex) {
						Logger.getLogger(ProviderHandler.class.getName()).log(
								Level.SEVERE, null, ex);
					}
				}
			}

			msg.processType = ProviderMessage.VERIFICATION_SUCCEEDED;
			msg.providerType = "storage";
			msg.provider = providers;
			msg.send();

			// String topic = "login/succeeded";
			// Dictionary props = new Hashtable();
			// Event e = new Event(topic, props);
			// queue.offer(e);
			return;
		} else if (context.equals("Login failed")) {
			msg.processType = ProviderMessage.VERIFICATION_FAILED;
			msg.send();
			// String topic = "login/failed";
			// Dictionary props = new Hashtable();
			// Event e = new Event(topic, props);
			// queue.offer(e);
			return;
		} else {
			System.out.println("not handled message");
		}
	}

	// /**
	// * @return the persistentceController
	// */
	// public PersistenceService getPersistentceController() {
	// return persistentceController;
	// }
	//
	// /**
	// * @param persistentceController the persistentceController to set
	// */
	// public void setPersistentceController(PersistenceService
	// persistentceController) {
	// this.persistentceController = persistentceController;
	// }
}
