/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gudy.azureus2;

import com.aelitis.azureus.core.AzureusCore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.gudy.azureus2.plugins.download.DownloadManager;
/**
 *
 * @author gurkerl
 */
public class MessageActivator implements BundleActivator {


    @Override
    public void start(BundleContext context) throws Exception {
          ServiceReference coreRef = context.getServiceReference(AzureusCore.class.getName());
          AzureusCore core = (AzureusCore) context.getService(coreRef);

          ServiceReference DLmgr = context.getServiceReference(org.gudy.azureus2.plugins.download.DownloadManager.class.getName());
          DownloadManager dlmgr = (DownloadManager)context.getService(DLmgr);

          context.registerService(org.gudy.azureus2.plugins.messaging.MessageManager.class.getName(), new org.gudy.azureus2.pluginsimpl.locale.messaging.MessageManagerImpl(core, dlmgr), null);

    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }
}
