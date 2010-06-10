/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gudy.azureus2;

import com.aelitis.azureus.core.AzureusCore;
import org.gudy.azureus2.pluginsimpl.local.dht.mainline.MainlineDHTManagerImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTManager;
/**
 *
 * @author gurkerl
 */
public class MainlineActivator implements BundleActivator {

    public void start(BundleContext context) throws Exception {
        ServiceReference coreRef = context.getServiceReference(AzureusCore.class.getName());
        AzureusCore core = (AzureusCore) context.getService(coreRef);
        context.registerService(MainlineDHTManager.class.getName(), new MainlineDHTManagerImpl(core), null);
    }

    public void stop(BundleContext context) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
