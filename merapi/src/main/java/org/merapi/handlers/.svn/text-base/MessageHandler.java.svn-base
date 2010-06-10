////////////////////////////////////////////////////////////////////////////////
//
//  This program is free software; you can redistribute it and/or modify 
//  it under the terms of the GNU Lesser General Public License as published 
//  by the Free Software Foundation; either version 3 of the License, or (at 
//  your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
//  License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License 
//  along with this program; if not, see <http://www.gnu.org/copyleft/lesser.html>.
//
////////////////////////////////////////////////////////////////////////////////

package org.merapi.handlers;

import org.merapi.internal.Bridge;
import org.merapi.messages.IMessage;


public class MessageHandler implements IMessageHandler
{
    //--------------------------------------------------------------------------
    //
    //  Constructors
    //
    //--------------------------------------------------------------------------
	
	/**
	 *  The default constructor
	 */
	public MessageHandler()
	{
		super();
	}
	
	/**
	 *  Automatically registers the handler for message type <code>type</code>.
	 */
	public MessageHandler( String type )
	{
		addMessageType( type );
	}
	
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  Handles an <code>IMessage</code> dispatched from the Bridge.
	 */
	public void handleMessage( IMessage message ) {}

	/**
	 *  Adds another message type to be listened for by this instance of MessageHandler.
	 */
	public void addMessageType( String type )
	{
		Bridge.getInstance().registerMessageHandler( type, this );
	}
	
	/**
	 *  Removes the handling of message type <code>type</code>.
	 */
	public void removeMessageType( String type )
	{
		Bridge.getInstance().unRegisterMessageHandler( type, this );
	}
	
}
