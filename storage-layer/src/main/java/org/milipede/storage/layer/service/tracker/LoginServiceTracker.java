package org.milipede.storage.layer.service.tracker;

import ch.cyberduck.service.LoginController;
import org.milipede.storage.layer.handler.ProviderHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

//import ch.cyberduck.core.LoginController;

public class LoginServiceTracker extends ServiceTracker {

	public LoginServiceTracker(BundleContext context, ProviderHandler loginServiceConsumer) {
		super(context, LoginController.class.getName(), null);
		this.handler = loginServiceConsumer;
	}

	private ProviderHandler handler;
	
////	public LoginServiceTracker(BundleContext context,
////            ProviderHandler loginServiceConsumer) {
////		super(context, LoginController.class.getName(), null);
////		this.handler = loginServiceConsumer;
////	}
//
//
	@Override
	public Object addingService(ServiceReference reference) {
        System.out.println("hello");
        LoginController loginService = (LoginController)context.getService(reference);
        this.handler.setLoginService(loginService);
        return loginService;
    }

    @Override
    public void modifiedService(ServiceReference sr, Object o) {
        this.handler.setLoginService(null);
    	LoginController loginService = (LoginController)context.getService(sr);
        this.handler.setLoginService(loginService);
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        LoginController loginService = (LoginController)service;
        //for clean up
        this.handler.setLoginService(null);
        context.ungetService(reference);
    }

}
