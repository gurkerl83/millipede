package org.milipede.storage.layer.merapi.handlers;
//package org.milipede.storage.layer.merapi.handlers;
//
//import java.util.ArrayList;
//
//import org.merapi.handlers.MessageHandler;
//import org.merapi.internal.Bridge;
//import org.merapi.internal.systemexecute.handlers.SystemExecuteMessageHandler;
//import org.merapi.messages.IMessage;
//import org.milipede.storage.layer.StorageConsole;
//import org.milipede.storage.layer.domain.AccountVO;
//import org.milipede.storage.layer.internal.AccountController;
//import org.milipede.storage.layer.internal.AccountControllerEvent;
//import org.milipede.storage.layer.internal.AccountControllerListener;
//import org.milipede.storage.layer.internal.Assistant;
//import org.milipede.storage.layer.internal.common.logging.Logger;
//import org.milipede.storage.layer.merapi.messages.ProviderMessage;
//import org.milipede.storage.layer.internal.Storage;
//import com.sun.cloud.api.storage.webdav.WebDavClient;
//import org.milipede.storage.layer.domain.Account;
//
//public class ProviderHandler extends MessageHandler { // implements AccountControllerListener {
//
//	/**
//	 * Logger for this class.
//	 */
//	private static final Logger logger = Logger.getLogger(ProviderHandler.class);
////	private static ProviderHandler instance = new ProviderHandler();
//
//	/**
//	 * Statische Methode, liefert die einzige Instanz dieser Klasse zurueck
//	 */
////	public static ProviderHandler getInstance() {
////		return instance;
////	}
//
//	/**
//	 * Default-Konstruktor, der nicht ausserhalb dieser Klasse aufgerufen werden
//	 * kann
//	 */
//	public ProviderHandler() {
//		super(ProviderMessage.PROVIDER_MESSAGE);
////		works for systemexecutemessage
////		this.addMessageType(ProviderMessage.PROVIDER_MESSAGE);
////		super(ProviderMessage.PROVIDER_MESSAGE);
////		service = new Main();
//	}
//
////	@Override
////	public void handleMessage(IMessage message) {
////		if (message.getType() == "RefreshMessage") {
////			System.out.println("RefreshMessage received");
////		}
////
////		else if (message.getType() == "LoginMessage") {
////			System.out.println("LoginMessage received");
////		}
////
////		else if (message.getType() == "LogoutMessage") {
////			System.out.println("LogoutMessage received");
////		} else {
////			System.out.println("No defined message type received " + message.getType());
////		}
////	}
//
////	@Override
////	public void handleMessage(IMessage message) {
////		System.out.println(message);
////		if (message instanceof SystemExecuteMessage) {
////			ProviderMessage providerMessage = (ProviderMessage) message;
////			System.out.println("ProviderMessage received:");
////			System.out.println("Content: " + providerMessage.getData().toString());
////		}
////
////	}
//	/**
//	 *  Handles an <code>IMessage</code> dispatched from the Bridge.
//	 */
//	@Override
//	public void handleMessage( IMessage message )
//	{
//
//		if ( message instanceof ProviderMessage )
//		{
//
//
//			ProviderMessage sem = (ProviderMessage)message;
//
//			//  Use the args passed in the message to do a shell exec
//			try
//			{
////				if(detectMultipleSendings(sem.getUid().concat())) {
////					break;
////				} else {
////
////				}
////				Object[] args = sem.getArgs();
////				System.out.println("Length: " + sem.getArgs().length);
////				ProviderVO vo = (ProviderVO) args[0];
////				ProviderVO vo = (ProviderVO) sem.getData();
//				System.out.println("Message Uid: " + sem.getUid() + " HashCode: " + sem.getType());
//			}
//			catch ( Exception e )
//			{
//				System.out.println( MessageHandler.class );
//				e.printStackTrace();
//			}
//
//			//Respond in reaction to an ProviderMessage of processType ProviderMessage.INIT_PROVIDER_LIST_REQUEST
//			if(sem.processType.equals(ProviderMessage.INIT_PROVIDER_LIST_REQUEST)) {
//				logger.info(ProviderMessage.INIT_PROVIDER_LIST_REQUEST + " Message received");
//
//                                AccountController.getInstance().getAllAccounts("test");
//
//				ProviderMessage respondMessage = new ProviderMessage();
//				respondMessage.setType(ProviderMessage.PROVIDER_MESSAGE);
//				respondMessage.processType = ProviderMessage.INIT_PROVIDER_LIST_RESPOND;
//
//				if(sem.providerType.equals("storage")) {
//					System.out.println("storage");
//					respondMessage.providerType = "storage";
////					respondMessage.provider = (ArrayList<AccountVO>) Assistant.getInstance().getProviderList();
//                                        respondMessage.convertToAccountVOAccount(AccountController.getInstance().getAllAccounts("storage"));
//					try {
//						Bridge.getInstance().sendMessage(respondMessage);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				else if(sem.providerType.equals("mail")) {
//					respondMessage.providerType = "mail";
//					System.out.println("mail");
////					respondMessage.provider = (ArrayList<AccountVO>) Assistant.getInstance().getProviderList();
//                                        respondMessage.convertToAccountVOAccount(AccountController.getInstance().getAllAccounts("mail"));
//					try {
//						Bridge.getInstance().sendMessage(respondMessage);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				else if(sem.providerType.equals("hoster")) {
//					respondMessage.providerType = "hoster";
//					System.out.println("hoster");
////					respondMessage.provider = (ArrayList<AccountVO>) Assistant.getInstance().getProviderList();
//					respondMessage.convertToAccountVOAccount(AccountController.getInstance().getAllAccounts("hoster"));
//                                        try {
//						Bridge.getInstance().sendMessage(respondMessage);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//
//			else if(sem.processType.equals(ProviderMessage.LOGIN_PROVIDER_REQUEST)) {
//
//                            ArrayList<Account> accounts = sem.convertToAccount();
//
//                                if(sem.providerType.equals("storage")) {
//                                    for (Account a: accounts) {
//                                        //webdav login
//                                        Storage storage = new Storage();
//                                        storage.setWebDavClient(new WebDavClient(null, a.getUser(), a.getPass(), null));
//                                        a.setEnabled(true);
//                                        AccountController.getInstance().updateAccountInfo(a.getHoster(), a, true);
//                                    }
//                                }
//				else if(sem.providerType.equals("mail")) {
//                                    for (Account a: accounts) {
////                                        a.setEnabled(true);
////                                        AccountController.getInstance().updateAccountInfo(a.getHoster(), a, true);
//                                    }
//                                }
//				else if(sem.providerType.equals("hoster")) {
//                                    for (Account a: accounts) {
////                                        a.setEnabled(true);
////                                        AccountController.getInstance().updateAccountInfo(a.getHoster(), a, true);
//                                    }
//				}
//
//				//test if login parameter was correct
//				logger.info(ProviderMessage.LOGIN_PROVIDER_REQUEST + " Message received");
////				AccountController.getInstance().addAccount(null, sem.provider);
//
//
//				ProviderMessage respondMessage = new ProviderMessage();
//				respondMessage.setType(ProviderMessage.PROVIDER_MESSAGE);
//				respondMessage.processType = ProviderMessage.LOGIN_PROVIDER_RESPOND;
//
//				try {
//					Bridge.getInstance().sendMessage(respondMessage);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//			else if(sem.processType.equals(ProviderMessage.LOGOUT_PROVIDER_REQUEST)){
//
////				backend system has to be logged out from selected provider
//				logger.info(ProviderMessage.LOGOUT_PROVIDER_REQUEST + " Message received");
//
//				ProviderMessage respondMessage = new ProviderMessage();
////				respondMessage.setUid(sem.getUid());
//				respondMessage.processType = ProviderMessage.LOGOUT_PROVIDER_RESPOND;
//				respondMessage.setType(ProviderMessage.PROVIDER_MESSAGE);
//				//Provider id
////				respondMessage.setData(Assistant.getInstance().getProviderList());
//				try {
//					Bridge.getInstance().sendMessage(respondMessage);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			else {
//				logger.info("no such processType received");
//			}
//		}
//	}
//
//	//this handler receives unpredictatable (more than one messages from the as3 bridge,
//	//this is a known problem,
//	//workaround
//	public static String lastUidTime = null;
//	public boolean detectMultipleSendings(String uIdTime) {
//		if (lastUidTime != uIdTime) {
//			lastUidTime = uIdTime;
//			return false;
//		}
//		else
//		return true;
//
//	}
//
////	@Override
////	public void onAccountControllerEvent(AccountControllerEvent event) {
////		// TODO Auto-generated method stub
////	}
//}
