/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gudy.azureus2.pluginsimpl.local;

import com.aelitis.azureus.core.AzureusCore;
import com.aelitis.azureus.core.AzureusCoreOperation;

/**
 *
 * @author gurkerl
 */
public class PluginInitializer {

    private AzureusCore	azureus_core;
    private AzureusCoreOperation core_operation;

    protected
  PluginInitializer(
  	AzureusCore 			_azureus_core,
  	AzureusCoreOperation	_core_operation )
  {
  	azureus_core	= _azureus_core;

//  	AEDiagnostics.addEvidenceGenerator( this );
//
//  	azureus_core.addLifecycleListener(
//	    	new AzureusCoreLifecycleAdapter()
//			{
//	    		public void
//				componentCreated(
//					AzureusCore					core,
//					AzureusCoreComponent		comp )
//	    		{
//	    			if ( comp instanceof GlobalManager ){
//
//	    				GlobalManager	gm	= (GlobalManager)comp;
//
//	    				gm.addListener( PluginInitializer_.this );
//	    			}
//	    		}
//			});
//
  	core_operation 	= _core_operation;
//
//    UpdateManagerImpl.getSingleton( azureus_core );	// initialise the update manager
//
//    plugin_manager = PluginManagerImpl.getSingleton( this );
//
//    String	dynamic_plugins = System.getProperty( "azureus.dynamic.plugins", null );
//
//    if ( dynamic_plugins != null ){
//
//    	String[]	classes = dynamic_plugins.split( ";" );
//
//    	for ( String c: classes ){
//
//    		try{
//    			queueRegistration( Class.forName( c ));
//
//    		}catch( Throwable e ){
//
//    			Debug.out( "Registration of dynamic plugin '" + c + "' failed", e );
//    		}
//    	}
//    }
//
//    UpdaterUtils.checkBootstrapPlugins();
  }

    protected AzureusCore
  getAzureusCore()
  {
  	return( azureus_core );
  }
}
