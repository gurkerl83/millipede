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
package org.millipede.router;

import java.util.Dictionary;
import java.util.Hashtable;

import org.merapi.helper.messages.BarUpdateRequestMessage;
import org.merapi.helper.messages.CollaborationMessage;
import org.merapi.helper.messages.DLControlMessage;
import org.merapi.helper.messages.DLControlRespondMessage;
import org.merapi.helper.messages.SystemMessage;
import org.merapi.internal.Bridge;
//import org.merapi.internal.DLControlEventHandler;
import org.millipede.merapi.messages.ProviderMessage;
import org.millipede.router.generator.EventGenerator;
import org.millipede.router.handler.DLControlEventHandler;
import org.millipede.router.handler.ProviderHandler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.torrent.internal.client.TestEventHandler;

public class Activator implements BundleActivator {

    private EventGenerator eg;
    private Thread gt;
    
    private BundleContext bctx;
	private ServiceRegistration reg;
	private ServiceRegistration reg2;

	
    public void start(BundleContext context) throws Exception {

    	//Merapi -> OSGi communication
    	eg = new EventGenerator(context);
        gt = new Thread(eg);
        gt.start();

        Bridge.getInstance().registerMessageHandler(DLControlMessage.DL_CONTROL, eg);
        Bridge.getInstance().registerMessageHandler(BarUpdateRequestMessage.REQUEST_BAR_DATA, eg);
        Bridge.getInstance().registerMessageHandler(SystemMessage.SYSTEM_MESSAGE, eg);
        Bridge.getInstance().registerMessageHandler(CollaborationMessage.COLLABORATION, eg);

        
        
        //OSGi -> Merapi communication
        Dictionary props = new Hashtable();
//		props.put(EventConstants.EVENT_TOPIC, "es/schaaf/*");
        //Wichtig muss wieder hinein
		props.put(EventConstants.EVENT_TOPIC, DLControlRespondMessage.DL_CONTROL_RESPOND+"/*");
//		props.put(EventConstants.EVENT_TOPIC, "flexspacesinfo");
        reg = bctx.registerService(EventHandler.class.getName(),new DLControlEventHandler(), props);
        
//		props = new Hashtable();
//		props.put(EventConstants.EVENT_TOPIC, ProviderMessage.INIT_PROVIDER_LIST_REQUEST + "/*");
//		reg2 = bctx.registerService(EventHandler.class.getName(),new ProviderHandler(), props);
	
    }

    public void stop(BundleContext context) throws Exception {
        eg.setRunning(false);
        gt.join();
        
        reg.unregister();
    }
}
