/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aelitis.azureus.core.impl;

import com.aelitis.azureus.core.AzureusCore;
import com.aelitis.azureus.core.AzureusCoreException;
import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.global.GlobalManager;
//import org.merapi.helper.handlers.BarUpdateRequestHandler;
//import org.merapi.helper.handlers.ConfigurationRequestHandler;
//import org.merapi.helper.handlers.DLControlRequestHandler;
//import org.merapi.internal.Bridge;
//import org.milipede.merapi.handlers.LifecycleMessageHandler;
//import org.milipede.merapi.messages.LifecycleMessage;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author gurkerl
 */
public class AzureusCoreImpl implements AzureusCore, BundleActivator {
        protected static AzureusCore		singleton;

        private GlobalManager		global_manager;


    private volatile boolean				started;
	private volatile boolean				stopped;
	private volatile boolean				restarting;


	public static AzureusCore
	create()

		throws AzureusCoreException
	{
		try{
//			class_mon.enter();

			if ( singleton != null ){

				throw( new AzureusCoreException( "Azureus core already instantiated" ));
			}

			singleton	= new AzureusCoreImpl();

			return( singleton );

		}finally{

//			class_mon.exit();
		}
	}

        public static boolean
	isCoreAvailable()
	{
		return( singleton != null );
	}

	public static boolean
	isCoreRunning()
	{
		return( singleton != null && singleton.isStarted() );
	}

	public static AzureusCore
	getSingleton()

		throws AzureusCoreException
	{
		if ( singleton == null ){

			throw( new AzureusCoreException( "core not instantiated"));
		}

		return( singleton );
	}

    @Override
    public void start() throws AzureusCoreException {

//		AEThread2.setOurThread();

//        DLControlRequestHandler.getInstance();
//        BarUpdateRequestHandler.getInstance();
//
//        ConfigurationRequestHandler.getInstance();
    
		try{
//			this_mon.enter();

			if ( started ){

				throw( new AzureusCoreException( "Core: already started" ));
			}

			if ( stopped ){

				throw( new AzureusCoreException( "Core: already stopped" ));
			}

			started	= true;

		}finally{

//			this_mon.exit();
		}

//		// If a user sets this property, it is an alias for the following settings.
//		if ("1".equals(System.getProperty("azureus.safemode"))) {
//			if (Logger.isEnabled())
//				Logger.log(new LogEvent(LOGID, "Safe mode enabled"));
//
//			Constants.isSafeMode = true;
//			System.setProperty("azureus.loadplugins", "0");
//			System.setProperty("azureus.disabledownloads", "1");
//			System.setProperty("azureus.skipSWTcheck", "1");
//
//			// Not using localised text - not sure it's safe to this early.
//			Logger.log(new LogAlert(LogAlert.UNREPEATABLE, LogEvent.LT_WARNING,
//				"You are running " + Constants.APP_NAME + " in safe mode - you " +
//					"can change your configuration, but any downloads added will " +
//					"not be remembered when you close " + Constants.APP_NAME + "."
//			));
//		}
//
//	   /**
//	    * test to see if UI plays nicely with a really slow initialization
//	    */
//	   String sDelayCore = System.getProperty("delay.core", null);
//	   if (sDelayCore != null) {
//	  	 try {
//	  		 long delayCore = Long.parseLong(sDelayCore);
//	  		 Thread.sleep(delayCore);
//	  	 } catch (Exception e) {
//	  		 e.printStackTrace();
//	  	 }
//	   }
//
//
//		// run plugin loading in parallel to the global manager loading
//		AEThread2 pluginload = new AEThread2("PluginLoader",true)
//		{
//			public void run() {
//				if (Logger.isEnabled())
//					Logger.log(new LogEvent(LOGID, "Loading of Plugins starts"));
//				pi.loadPlugins(AzureusCoreImpl_.this, false, !"0".equals(System.getProperty("azureus.loadplugins")), true, true);
//				if (Logger.isEnabled())
//					Logger.log(new LogEvent(LOGID, "Loading of Plugins complete"));
//			}
//		};
//
//		if (LOAD_PLUGINS_IN_OTHER_THREAD) {
//			pluginload.start();
//		}
//		else {
//			pluginload.run();
//		}
//
//
//
//
//
//		// Disable async loading of existing torrents, because there are many things
//		// (like hosting) that require all the torrents to be loaded.  While we
//		// can write code for each of these cases to wait until the torrents are
//		// loaded, it's a pretty big job to find them all and fix all their quirks.
//		// Too big of a job for this late in the release stage.
//		// Other example is the tracker plugin that is coded in a way where it must
//		// always publish a complete rss feed
//
//		global_manager = GlobalManagerFactory.create(
//				this,
//				new GlobalMangerProgressListener()
//				{
//					public void
//					reportCurrentTask(
//						String currentTask )
//					{
//						initialisation_op.reportCurrentTask( currentTask );
//					}
//
//					public void
//					reportPercent(
//						int percent )
//					{
//						initialisation_op.reportPercent( percent );
//					}
//				}, 0);
//
		if (stopped) {
			System.err.println("Core stopped while starting");
			return;
		}

//		// wait until plugin loading is done
//		if (LOAD_PLUGINS_IN_OTHER_THREAD) {
//			pluginload.join();
//		}
//
//		if (stopped) {
//			System.err.println("Core stopped while starting");
//			return;
//		}
//
//		triggerLifeCycleComponentCreated(global_manager);
//
//		pi.initialisePlugins();
//
//		if (stopped) {
//			System.err.println("Core stopped while starting");
//			return;
//		}
//
//		if (Logger.isEnabled())
//			Logger.log(new LogEvent(LOGID, "Initializing Plugins complete"));
//
//		try{
//			PluginInterface dht_pi 	= getPluginManager().getPluginInterfaceByClass( DHTPlugin.class );
//
//			if ( dht_pi != null ){
//
//				dht_pi.addEventListener(
//					new PluginEventListener()
//					{
//						private boolean	first_dht = true;
//
//						public void
//						handleEvent(
//							PluginEvent	ev )
//						{
//							if ( ev.getType() == DHTPlugin.EVENT_DHT_AVAILABLE ){
//
//								if ( first_dht ){
//
//									first_dht	= false;
//
//									DHT 	dht = (DHT)ev.getValue();
//
//									speed_manager.setSpeedTester( dht.getSpeedTester());
//
//									global_manager.addListener(
//											new GlobalManagerAdapter()
//											{
//												public void
//												seedingStatusChanged(
//													boolean seeding_only_mode,
//													boolean	b )
//												{
//													checkConfig();
//												}
//											});
//
//									COConfigurationManager.addAndFireParameterListeners(
//										new String[]{	TransferSpeedValidator.AUTO_UPLOAD_ENABLED_CONFIGKEY,
//														TransferSpeedValidator.AUTO_UPLOAD_SEEDING_ENABLED_CONFIGKEY },
//										new ParameterListener()
//										{
//											public void
//											parameterChanged(
//												String parameterName )
//											{
//												checkConfig();
//											}
//										});
//
//								}
//							}
//						}
//
//						protected void
//						checkConfig()
//						{
//							String	key = TransferSpeedValidator.getActiveAutoUploadParameter( global_manager );
//
//							speed_manager.setEnabled( COConfigurationManager.getBooleanParameter( key ));
//						}
//
//					});
//			}
//		}catch( Throwable e ){
//		}
//
	   if ( COConfigurationManager.getBooleanParameter( "Resume Downloads On Start" )){

		   global_manager.resumeDownloads();
	   }
//
//	   VersionCheckClient.getSingleton().initialise();
//
//	   instance_manager.initialize();
//
//	   NetworkManager.getSingleton().initialize(this);
//
//	   Runtime.getRuntime().addShutdownHook( new AEThread("Shutdown Hook") {
//	     public void runSupport() {
//			Logger.log(new LogEvent(LOGID, "Shutdown hook triggered" ));
//			AzureusCoreImpl_.this.stop();
//	     }
//	   });
//
//
//	   DelayedTask delayed_task =
//	   		UtilitiesImpl.addDelayedTask(
//	   			"Core",
//	   			new Runnable()
//	   			{
//	   				public void
//	   				run()
//	   				{
//	   					new AEThread2( "core:delayTask", true )
//	   					{
//	   						public void
//	   						run()
//	   						{
//			   					AEDiagnostics.checkDumpsAndNatives();
//
//			   					COConfigurationManager.setParameter( "diags.enable.pending.writes", true );
//
//			   					AEDiagnostics.flushPendingLogs();
//
//			   					NetworkAdmin na = NetworkAdmin.getSingleton();
//
//			   					na.runInitialChecks(AzureusCoreImpl.this);
//
//			   					na.addPropertyChangeListener(
//			   							new NetworkAdminPropertyChangeListener()
//			   							{
//			   								private String	last_as;
//
//			   								public void
//			   								propertyChanged(
//			   										String		property )
//			   								{
//			   									NetworkAdmin na = NetworkAdmin.getSingleton();
//
//			   									if ( property.equals( NetworkAdmin.PR_NETWORK_INTERFACES )){
//
//			   										boolean	found_usable = false;
//
//			   										NetworkAdminNetworkInterface[] intf = na.getInterfaces();
//
//			   										for (int i=0;i<intf.length;i++){
//
//			   											NetworkAdminNetworkInterfaceAddress[] addresses = intf[i].getAddresses();
//
//			   											for (int j=0;j<addresses.length;j++){
//
//			   												if ( !addresses[j].isLoopback()){
//
//			   													found_usable = true;
//			   												}
//			   											}
//			   										}
//
//			   										// ignore event if nothing usable
//
//			   										if ( !found_usable ){
//
//			   											return;
//			   										}
//
//			   										Logger.log(	new LogEvent(LOGID, "Network interfaces have changed (new=" + na.getNetworkInterfacesAsString() + ")"));
//
//			   										announceAll( false );
//
//			   									}else if ( property.equals( NetworkAdmin.PR_AS )){
//
//			   										String	as = na.getCurrentASN().getAS();
//
//			   										if ( last_as == null ){
//
//			   											last_as = as;
//
//			   										}else if ( !as.equals( last_as )){
//
//			   											Logger.log(	new LogEvent(LOGID, "AS has changed (new=" + as + ")" ));
//
//			   											last_as = as;
//
//			   											announceAll( false );
//			   										}
//			   									}
//			   								}
//			   							});
//	   						}
//	   					}.start();
//	   				}
//	   			});
//
//	   delayed_task.queue();
//
//			if (stopped) {
//				System.err.println("Core stopped while starting");
//				return;
//			}
//
//	   PairingManagerFactory.getSingleton();
//
//	   Object[] runningListeners;
//	   mon_coreRunningListeners.enter();
//	   try {
//	  	 if (coreRunningListeners == null) {
//	  		 runningListeners = new Object[0];
//	  	 } else {
//	  		 runningListeners = coreRunningListeners.toArray();
//	  		 coreRunningListeners = null;
//	  	 }
//
//	   } finally {
//	  	 mon_coreRunningListeners.exit();
//	   }
//
//		// Trigger Listeners now that core is started
//		new AEThread2("Plugin Init Complete", false )
//		{
//			public void
//			run()
//			{
//				Iterator	it = lifecycle_listeners.iterator();
//
//				while( it.hasNext()){
//
//					try{
//						AzureusCoreLifecycleListener listener = (AzureusCoreLifecycleListener)it.next();
//
//						if ( !listener.requiresPluginInitCompleteBeforeStartedEvent()){
//
//							listener.started( AzureusCoreImpl.this );
//						}
//					}catch( Throwable e ){
//
//						Debug.printStackTrace(e);
//					}
//				}
//
//				pi.initialisationComplete();
//
//				it = lifecycle_listeners.iterator();
//
//				while( it.hasNext()){
//
//					try{
//						AzureusCoreLifecycleListener listener = (AzureusCoreLifecycleListener)it.next();
//
//						if ( listener.requiresPluginInitCompleteBeforeStartedEvent()){
//
//							listener.started( AzureusCoreImpl.this );
//						}
//					}catch( Throwable e ){
//
//						Debug.printStackTrace(e);
//					}
//				}
//			}
//		}.start();
//
//		for (Object l : runningListeners) {
//			try {
//				((AzureusCoreRunningListener) l).azureusCoreRunning(this);
//			} catch (Throwable t) {
//				Debug.out(t);
//			}
//		}
//
//		// Debug.out("Core Start Complete");
//
    }

    @Override
    public boolean isStarted()
	{
//	   mon_coreRunningListeners.enter();
	   try {
//	  	 return( started && coreRunningListeners == null );
               return( started);
	   } finally {
//	  	 mon_coreRunningListeners.exit();
	   }
	}

    @Override
    public void
	stop()

		throws AzureusCoreException
	{
//		runNonDaemon(new AERunnable() {
//			public void runSupport() {
////				if (Logger.isEnabled())
////					Logger.log(new LogEvent(LOGID, "Stop operation starts"));
//
//				stopSupport(true);
//			}
//		});
	}

    private void
	stopSupport(
		boolean		apply_updates )

		throws AzureusCoreException
	{
//		AEDiagnostics.flushPendingLogs();

		try{
//			this_mon.enter();

			if ( stopped ){

					// ensure config is saved as there may be pending changes to persist and we've got here
					// via a shutdown hook

				COConfigurationManager.save();

//				Logger.log(new LogEvent(LOGID, "Waiting for stop to complete"));
//
//				stopping_sem.reserve();

				return;
			}

			stopped	= true;

			if ( !started ){

//				Logger.log(new LogEvent(LOGID, "Core not started"));

					// might have been marked dirty due to core being created to allow functions to be used but never started...

//				if ( AEDiagnostics.isDirty()){
//
//					AEDiagnostics.markClean();
//				}

//				stopping_sem.releaseForever();

				return;
			}

		}finally{

//			this_mon.exit();
		}

//		List	sync_listeners 	= new ArrayList();
//		List	async_listeners	= new ArrayList();
//
//		Iterator it = lifecycle_listeners.iterator();
//
//		while( it.hasNext()){
//			AzureusCoreLifecycleListener	l = (AzureusCoreLifecycleListener)it.next();
//
//			if ( l.syncInvokeRequired()){
//				sync_listeners.add( l );
//			}else{
//				async_listeners.add( l );
//			}
//		}
//
//		try{
//			if (Logger.isEnabled())
//				Logger.log(new LogEvent(LOGID, "Invoking synchronous 'stopping' listeners"));
//
//			for (int i=0;i<sync_listeners.size();i++){
//				try{
//					((AzureusCoreLifecycleListener)sync_listeners.get(i)).stopping( this );
//
//				}catch( Throwable e ){
//
//					Debug.printStackTrace(e);
//				}
//			}
//
//			if (Logger.isEnabled())
//				Logger.log(new LogEvent(LOGID, "Invoking asynchronous 'stopping' listeners"));
//
//				// in case something hangs during listener notification (e.g. version check server is down
//				// and the instance manager tries to obtain external address) we limit overall dispatch
//				// time to 10 seconds
//
//			ListenerManager.dispatchWithTimeout(
//					async_listeners,
//					new ListenerManagerDispatcher()
//					{
//						public void
//						dispatch(
//							Object		listener,
//							int			type,
//							Object		value )
//						{
//							((AzureusCoreLifecycleListener)listener).stopping( AzureusCoreImpl.this );
//						}
//					},
//					10*1000 );
//
//
//			if (Logger.isEnabled())
//				Logger.log(new LogEvent(LOGID, "Stopping global manager"));
//
//			if (global_manager != null) {
//				global_manager.stopGlobalManager();
//			}
//
//			if (Logger.isEnabled())
//				Logger.log(new LogEvent(LOGID, "Invoking synchronous 'stopped' listeners"));
//
//			for (int i=0;i<sync_listeners.size();i++){
//				try{
//					((AzureusCoreLifecycleListener)sync_listeners.get(i)).stopped( this );
//
//				}catch( Throwable e ){
//
//					Debug.printStackTrace(e);
//				}
//			}
//
//			if (Logger.isEnabled())
//				Logger.log(new LogEvent(LOGID, "Invoking asynchronous 'stopped' listeners"));
//
//			ListenerManager.dispatchWithTimeout(
//					async_listeners,
//					new ListenerManagerDispatcher()
//					{
//						public void
//						dispatch(
//							Object		listener,
//							int			type,
//							Object		value )
//						{
//							((AzureusCoreLifecycleListener)listener).stopped( AzureusCoreImpl.this );
//						}
//					},
//					10*1000 );
//
//			if (Logger.isEnabled())
//				Logger.log(new LogEvent(LOGID, "Waiting for quiescence"));
//
//			NonDaemonTaskRunner.waitUntilIdle();
//
//				// shut down diags - this marks the shutdown as tidy and saves the config
//
//			AEDiagnostics.markClean();
//
//			if (Logger.isEnabled())
//				Logger.log(new LogEvent(LOGID, "Stop operation completes"));
//
//				// if any installers exist then we need to closedown via the updater
//
//			if ( 	apply_updates &&
//					getPluginManager().getDefaultPluginInterface().getUpdateManager().getInstallers().length > 0 ){
//
//				AzureusRestarterFactory.create( this ).restart( true );
//			}
//
//			try {
//	      Class c = Class.forName( "sun.awt.AWTAutoShutdown" );
//
//	      if (c != null) {
//		      c.getMethod( "notifyToolkitThreadFree", new Class[]{} ).invoke( null, new Object[]{} );
//	      }
//			} catch (Throwable t) {
//			}
//
//			try{
//				ThreadGroup	tg = Thread.currentThread().getThreadGroup();
//
//				Thread[]	threads = new Thread[tg.activeCount()+32];
//
//				tg.enumerate( threads );
//
//				for (int i=0;i<threads.length;i++){
//
//					final Thread	t = threads[i];
//
//					if ( t != null && t.isAlive() && t != Thread.currentThread() && !t.isDaemon() && !AEThread2.isOurThread( t )){
//
//						new AEThread2( "VMKiller", true )
//						{
//							public void
//							run()
//							{
//								try{
//									Thread.sleep(10*1000);
//
//									Debug.out( "Non-daemon thread found '" + t.getName() + "', force closing VM" );
//
//									SESecurityManager.exitVM(0);
//
//								}catch( Throwable e ){
//
//								}
//							}
//						}.start();
//
//						break;
//					}
//				}
//			}catch( Throwable e ){
//			}
//		}finally{
//
//			stopping_sem.releaseForever();
//		}
	}

    @Override
    public void
	requestStop()

		throws AzureusCoreException
	{
		if (stopped)
			return;

//		runNonDaemon(new AERunnable() {
//			public void runSupport() {
//
//				Iterator it = lifecycle_listeners.iterator();
//
//				while( it.hasNext()){
//
//					if (!((AzureusCoreLifecycleListener)it.next())
//							.stopRequested(AzureusCoreImpl_.this)) {
//						if (Logger.isEnabled())
//							Logger.log(new LogEvent(LOGID, LogEvent.LT_WARNING,
//									"Request to stop the core has been denied"));
//
//						return;
//					}
//				}
//
				stop();
//			}
//		});
	}

    @Override
    public void checkRestartSupported() throws AzureusCoreException {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void restart() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void requestRestart() throws AzureusCoreException {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRestarting() {
//        throw new UnsupportedOperationException("Not supported yet.");
        return false;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        
        ServiceReference ref;
        ServiceReference refCore;
        ServiceRegistration reg;
        reg = context.registerService(AzureusCore.class.getName(), new AzureusCoreImpl(), null);
        ref = context.getServiceReference(GlobalManager.class.getName());
        GlobalManager gm = (GlobalManager) context.getService(ref);

//        Bridge.getInstance().registerMessageHandler(LifecycleMessage.LIFECYCLE_MESSAGE, new LifecycleMessageHandler(this));
        refCore = reg.getReference();
        AzureusCore ac = (AzureusCore) context.getService(refCore);
        ac.setGlobal_manager(gm);
    }

    @Override
    public void stop(BundleContext arg0) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the global_manager
     */
    public GlobalManager getGlobal_manager() {
        return global_manager;
    }

    /**
     * @param global_manager the global_manager to set
     */
    public void setGlobal_manager(GlobalManager global_manager) {
        this.global_manager = global_manager;
    }

    @Override
    public GlobalManager getGlobalManager() throws AzureusCoreException {
        return this.global_manager;
    }

}
