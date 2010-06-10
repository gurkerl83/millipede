/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gudy.azureus2;

import com.aelitis.azureus.core.AzureusCore;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.download.impl.DownloadManagerImpl;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author gurkerl
 */
public class Activation implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
    ServiceReference coreRef = context.getServiceReference(AzureusCore.class.getName());
    AzureusCore core = (AzureusCore) context.getService(coreRef);
        System.out.println("Core: " + core);
        ServiceReference gmRef = context.getServiceReference(GlobalManager.class.getName());
        GlobalManager gm = (GlobalManager) context.getService(gmRef);
        System.out.println("GlobalManager: " + gm);
//        context.registerService(org.gudy.azureus2.plugins.download.DownloadManager.class.getName(), new org.gudy.azureus2.pluginsimpl.locale.download.DownloadManagerImpl(core, gm), null);
        context.registerService(new String[] {org.gudy.azureus2.plugins.download.DownloadManager.class.getName(), org.gudy.azureus2.pluginsimpl.locale.download.DownloadBridge.class.getName()}, new org.gudy.azureus2.pluginsimpl.locale.download.DownloadManagerImpl(core, gm), null);


        //        ServiceReference dlmgrRef = context.getServiceReference(org.gudy.azureus2.core3.download.DownloadManager.class.getName());
//        org.gudy.azureus2.pluginsimpl.locale.download.DownloadManagerImpl.setSingleton((org.gudy.azureus2.core3.download.DownloadManager)context.getService(dlmgrRef));
    }

    @Override
    public void stop(BundleContext arg0) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
