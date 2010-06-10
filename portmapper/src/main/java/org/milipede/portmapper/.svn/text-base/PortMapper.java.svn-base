package org.milipede.portmapper;

import java.io.File;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.chris.portmapper.Settings;
import org.chris.portmapper.logging.LogMessageWriter;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.IRouterFactory;
import org.chris.portmapper.router.RouterException;

public class PortMapper implements PortMapperService {

	/**
	 * The name of the system property which will be used as the directory where
	 * all configuration files will be stored.
	 */
	private static final String CONFIG_DIR_PROPERTY_NAME = "portmapper.config.dir";

	/**
	 * The file name for the settings file.
	 */
	private static final String SETTINGS_FILENAME = "settings.xml";

//	private final Log logger = LogFactory.getLog(this.getClass());
        private final static Logger logger = Logger.getLogger( PortMapper.class );

	private IRouter router;
	private Settings settings;
	private LogMessageWriter logWriter;

	
	
	/**
	 * Read the system property with name
	 * {@link PortMapperApp#CONFIG_DIR_PROPERTY_NAME} and change the local
	 * storage directory if the property is given and points to a writable
	 * directory. If there is a directory named <code>PortMapperConf</code> in
	 * the current directory, use this as the configuration directory.
	 */
	public void setCustomConfigDir() {
		String customConfigurationDir = System
				.getProperty(CONFIG_DIR_PROPERTY_NAME);
		File portableAppConfigDir = new File("PortMapperConf");

		// the property is set: check, if the given directory can be used
		if (customConfigurationDir != null) {
			File dir = new File(customConfigurationDir);
			if (!dir.isDirectory()) {
				logger.error("Custom configuration directory '"
						+ customConfigurationDir + "' is not a directory.");
				System.exit(1);
			}
			if (!dir.canRead() || !dir.canWrite()) {
				logger
						.error("Can not read or write to custom configuration directory '"
								+ customConfigurationDir + "'.");
				System.exit(1);
			}
			logger.info("Using custom configuration directory '"
					+ dir.getAbsolutePath() + "'.");
//			getContext().getLocalStorage().setDirectory(dir);

			// check, if the portable app directory exists and use this one
		} else if (portableAppConfigDir.isDirectory()
				&& portableAppConfigDir.canRead()
				&& portableAppConfigDir.canWrite()) {
			logger.info("Found portable app configuration directory '"
					+ portableAppConfigDir.getAbsolutePath() + "'.");
//			getContext().getLocalStorage().setDirectory(portableAppConfigDir);

			// use the default configuration directory
		} else {
			logger.info("Using default configuration directory '"
//					+ getContext().getLocalStorage().getDirectory()
//							.getAbsolutePath() + "'.");
					);
		}
	}

	/**
	 * Load the application settings from file
	 * {@link PortMapperApp#SETTINGS_FILENAME} located in the configuration
	 * directory.
	 */
	public void loadSettings() {
		logger.debug("Loading settings from file " + SETTINGS_FILENAME);
//		try {
//			settings = (Settings) getContext().getLocalStorage().load(
//					SETTINGS_FILENAME);
//		} catch (IOException e) {
//			logger.warn("Could not load settings from file", e);
//		}

		if (settings == null) {
			logger
					.debug("Settings were not loaded from file: create new settings");
			settings = new Settings();
		} else {
			logger.debug("Got settings " + settings);
			this.setLogLevel(settings.getLogLevel());
		}
	}


	
	
	public static PortMapperService getInstance() {
		
		return new PortMapper();
	}
	
	/* (non-Javadoc)
	 * @see org.milipede.portmapper.internal.PortMapperService#connectRouter()
	 */
	public boolean connectRouter() throws RouterException {
		if (this.router != null) {
			logger
					.warn("Already connected to router. Cannot create a second connection.");
			return false;
		}

		IRouterFactory routerFactory;
		try {
			routerFactory = createRouterFactory();
		} catch (RouterException e) {
			logger.error("Could not create router factory", e);
			return false;
		}
		System.out.println("Searching for router...");
		this.router = routerFactory.findRouter();

		if (router == null) {
			throw new RouterException("Did not find a router");
		}

		try {
			System.out.println("Connected to router " + router.getName());
		} catch (RouterException e) {
			throw new RouterException("Could not get router name", e);
		}

		boolean isConnected = this.router != null;
//		this.getView().fireConnectionStateChange();
		return isConnected;
	}

	@SuppressWarnings("unchecked")
	private IRouterFactory createRouterFactory() throws RouterException {
		Class<IRouterFactory> routerFactoryClass;
		logger.info("Creating router factory for class "
				+ settings.getRouterFactoryClassName());
		try {
			routerFactoryClass = (Class<IRouterFactory>) Class.forName(settings
					.getRouterFactoryClassName());
		} catch (ClassNotFoundException e1) {
			throw new RouterException(
					"Did not find router factory class for name "
							+ settings.getRouterFactoryClassName(), e1);
		}

		IRouterFactory routerFactory;
		logger.debug("Creating a new instance of the router factory class "
				+ routerFactoryClass);
		try {
			routerFactory = routerFactoryClass.newInstance();
		} catch (Exception e) {
			throw new RouterException(
					"Could not create a router factory for name "
							+ settings.getRouterFactoryClassName(), e);
		}
		logger.debug("Router factory created");
		return routerFactory;
	}

	/* (non-Javadoc)
	 * @see org.milipede.portmapper.internal.PortMapperService#disconnectRouter()
	 */
	public boolean disconnectRouter() {
		if (this.router == null) {
			logger.warn("Not connected to router. Can not disconnect.");
			return false;
		}

		this.router.disconnect();
		this.router = null;
//		this.getView().fireConnectionStateChange();

		return true;
	}

	/* (non-Javadoc)
	 * @see org.milipede.portmapper.internal.PortMapperService#getRouter()
	 */
	public IRouter getRouter() {
		return router;
	}

	/* (non-Javadoc)
	 * @see org.milipede.portmapper.internal.PortMapperService#getSettings()
	 */
	public Settings getSettings() {
		return settings;
	}

	/* (non-Javadoc)
	 * @see org.milipede.portmapper.internal.PortMapperService#isConnected()
	 */
	public boolean isConnected() {
		return this.getRouter() != null;
	}

	/* (non-Javadoc)
	 * @see org.milipede.portmapper.internal.PortMapperService#getLocalHostAddress()
	 */
	public String getLocalHostAddress() {
		logger.debug("Get IP of localhost...");
		if (router != null) {
			try {
				return router.getLocalHostAddress();
			} catch (RouterException e) {
				logger.warn("Could not get address of localhost.", e);
				logger
						.warn("Could not get address of localhost. Please enter it manually.");
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.milipede.portmapper.internal.PortMapperService#setLogLevel(java.lang.String)
	 */
	public void setLogLevel(String logLevel) {
		Logger.getLogger("org.chris.portmapper").setLevel(
				Level.toLevel(logLevel));
	}


}
