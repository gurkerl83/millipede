package org.milipede.portmapper;

import org.chris.portmapper.Settings;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;

public interface PortMapperService {

	public boolean connectRouter() throws RouterException;

	/**
	 * @return
	 */
	public boolean disconnectRouter();

	public IRouter getRouter();

	public Settings getSettings();

	public boolean isConnected();

	/**
	 * Get the IP address of the local host.
	 * 
	 * @return IP address of the local host or <code>null</code>, if the address
	 *         could not be determined.
	 * @throws RouterException
	 */
	public String getLocalHostAddress();

	public void setLogLevel(String logLevel);

	public void setCustomConfigDir();
	
	public void loadSettings();
}