/**
 * 
 */
package org.chris.portmapper.router;

/**
 * @author chris
 * @version $Id: IRouterFactory.java 61 2009-08-15 14:58:46Z christoph $
 */
public interface IRouterFactory {
	public IRouter findRouter() throws RouterException;
}
