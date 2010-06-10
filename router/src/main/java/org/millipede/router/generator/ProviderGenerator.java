package org.millipede.router.generator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.merapi.helper.messages.DLControlMessage;
import org.merapi.messages.IMessage;
import org.millipede.merapi.messages.ProviderMessage;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;

public class ProviderGenerator extends EventGenerator {

	public ProviderGenerator(BundleContext ctx) {
		super(ctx);
	}

	@Override
	public void handleMessage(IMessage message) {

		if ( message instanceof ProviderMessage)
		{
			ProviderMessage sem = (ProviderMessage)message;

			//Respond in reaction to an ProviderMessage of processType ProviderMessage.INIT_PROVIDER_LIST_REQUEST
			if(sem.processType.equals(ProviderMessage.INIT_PROVIDER_LIST_REQUEST)) {

				String topic = ProviderMessage.INIT_PROVIDER_LIST_REQUEST + "/" + sem.providerType;
				Dictionary props = new Hashtable();
				props.put("providerType", sem.providerType);
        		Event e = new Event(topic, props);
				queue.offer(e);
				return;
			}
		}
	}
}