package org.milipede.merapi.handlers;

//package org.milipede.merapi.handlers;
//
//import com.aelitis.azureus.core.AzureusCore;
//import java.util.ArrayList;
//
//import org.merapi.handlers.MessageHandler;
//import org.merapi.internal.Bridge;
//import org.merapi.messages.IMessage;
//import org.milipede.merapi.messages.LifecycleMessage;
////import org.milipede.storage.layer.StorageConsole;
////import org.milipede.storage.layer.domain.AccountVO;
////import org.milipede.storage.layer.internal.AccountController;
////import org.milipede.storage.layer.internal.AccountControllerEvent;
////import org.milipede.storage.layer.internal.AccountControllerListener;
////import org.milipede.storage.layer.internal.Assistant;
////import org.milipede.storage.layer.internal.common.logging.Logger;
////import org.milipede.storage.layer.merapi.messages.ProviderMessage;
////import org.milipede.storage.layer.internal.Storage;
////import com.sun.cloud.api.storage.webdav.WebDavClient;
//
//public class LifecycleMessageHandler extends MessageHandler { // implements AccountControllerListener {
//
//    /**
//     * Logger for this class.
//     */
////	private static final Logger logger = Logger.getLogger(ProviderHandler.class);
////	private static CredentialMessageHandler instance = new CredentialMessageHandler();
////
////	/**
////	 * Statische Methode, liefert die einzige Instanz dieser Klasse zurueck
////	 */
////	public static CredentialMessageHandler getInstance() {
////		return instance;
////	}
//    /**
//     * Default-Konstruktor, der nicht ausserhalb dieser Klasse aufgerufen werden
//     * kann
//     */
//    private AzureusCore _core;
//
//    public LifecycleMessageHandler(AzureusCore core) {
//        super(LifecycleMessage.LIFECYCLE_MESSAGE);
//        this._core = core;
//    }
//
//    /**
//     *  Handles an <code>IMessage</code> dispatched from the Bridge.
//     */
//    @Override
//    public void handleMessage(IMessage message) {
//
//        if (message instanceof LifecycleMessage) {
//            System.out.println("credential message received");
//
//            LifecycleMessage sem = (LifecycleMessage) message;
//            LifecycleMessage respondMessage = new LifecycleMessage();
//            respondMessage.setType(LifecycleMessage.LIFECYCLE_MESSAGE);
//
//
//            //Respond in reaction to an ProviderMessage of processType ProviderMessage.INIT_PROVIDER_LIST_REQUEST
//            if (sem.processType.equals(LifecycleMessage.START_REQUEST)) {
//                System.out.println("start message received");
//
//                respondMessage.processType = LifecycleMessage.START_RESPOND;
//
////                this._core.start();
//                respondMessage.status = LifecycleMessage.SUCCEEDED;
////                if (success) {
////                    respondMessage.status = CredentialMessage.SUCCEEDED;
////                } else {
////                    respondMessage.status = CredentialMessage.FAILED;
////                }
//
//            } else if (sem.processType.equals(LifecycleMessage.RESTART_REQUEST)) {
//                System.out.println("restart message received");
//
//                respondMessage.processType = LifecycleMessage.RESTART_RESPOND;
//
////                this._core.restart();
//                respondMessage.status = LifecycleMessage.SUCCEEDED;
////                if (success) {
////                    respondMessage.status = CredentialMessage.SUCCEEDED;
////                } else {
////                    respondMessage.status = CredentialMessage.FAILED;
////                }
//            } else if (sem.processType.equals(LifecycleMessage.SHUTDOWN_REQUEST)) {
//                System.out.println("shutdown message received");
//
//                respondMessage.processType = LifecycleMessage.SHUTDOWN_RESPOND;
//
////                this._core.stop();
//                respondMessage.status = LifecycleMessage.SUCCEEDED;
////                if (success) {
////                    respondMessage.status = CredentialMessage.SUCCEEDED;
////                } else {
////                    respondMessage.status = CredentialMessage.FAILED;
////                }
//            }
//
//            try {
//                Bridge.getInstance().sendMessage(respondMessage);
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
//}
