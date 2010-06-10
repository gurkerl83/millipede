package org.merapi;

import org.merapi.handlers.IMessageHandler;
import org.merapi.messages.IMessage;

public interface BridgeService {

	/**
	 *  Dispatches an <code>IMessage</code> to registered listeners.
	 */
	public abstract void dispatchMessage(IMessage message);

	/**
	 *  Registers an <code>IMessageHandler</code> to be notified when messages of type
	 *  <code>type<code> are dispatched from the <code>Bridge</code>.
	 */
	public abstract void registerMessageHandler(String type,
			IMessageHandler handler);

	/**
	 *  Sends a <code>message</code> to the Flex side of the org.merapi bridge.
	 */
	public abstract void sendMessage(IMessage message) throws Exception;

	/**
	 *  Unregisters a given handler.
	 */
	public abstract void unRegisterMessageHandler(String type,
			IMessageHandler handler);

}