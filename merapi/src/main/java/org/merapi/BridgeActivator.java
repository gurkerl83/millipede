package org.merapi;

import org.merapi.helper.messages.CollaborationMessage;
import org.merapi.helper.messages.DLControlMessage;
import org.merapi.helper.messages.DLControlRespondMessage;
import org.merapi.internal.Bridge;
import org.merapi.internal.PolicyServer;
import org.millipede.merapi.messages.ProviderMessage;
import org.millipede.router.generator.EventGenerator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class BridgeActivator implements BundleActivator {
			 
	private EventGenerator eventGenerator;
	private Thread gt;
	@Override
	public void start(BundleContext context) throws Exception {
		(new PolicyServer(12344, new String[]{"*:12345"})).start();
		Bridge.open();

                eventGenerator = new EventGenerator(context);
		gt = new Thread(eventGenerator);
		gt.start();
		
		Bridge.getInstance().registerMessageHandler(DLControlMessage.DL_CONTROL, eventGenerator);
		//      Bridge.getInstance().registerMessageHandler(BarUpdateRequestMessage.REQUEST_BAR_DATA, eventGenerator);
		//      Bridge.getInstance().registerMessageHandler(SystemMessage.SYSTEM_MESSAGE, eventGenerator);
		Bridge.getInstance().registerMessageHandler(DLControlRespondMessage.DL_CONTROL_RESPOND, eventGenerator);
		Bridge.getInstance().registerMessageHandler(CollaborationMessage.COLLABORATION, eventGenerator);
		Bridge.getInstance().registerMessageHandler(ProviderMessage.PROVIDER_MESSAGE, eventGenerator);
		Bridge.getInstance().registerMessageHandler("loginMessageType", eventGenerator);
		Bridge.getInstance().registerMessageHandler("/flexspaces/info", eventGenerator);
		Bridge.getInstance().registerMessageHandler(ProviderMessage.UPLOAD, eventGenerator);
	}

	@Override
	public void stop(BundleContext context) throws Exception {

		Bridge.close();
        //Bridge Singleton instance unregistered from ServiceRegister
//        serviceRegistration.unregister();
//        System.out.println("UNREGISTERED org.merapi.BridgeService");

        //Ausgelagert in router
        eventGenerator.setRunning(false);
        gt.join();
//        reg.unregister();
	}

}
