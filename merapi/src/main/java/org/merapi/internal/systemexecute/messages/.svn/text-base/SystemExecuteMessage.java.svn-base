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

package org.merapi.internal.systemexecute.messages;

import org.merapi.messages.Message;

/**
 *  The <code>SystemExecuteMessage</code> class is a sub class of <code>Message</code> 
 *  that signals a system execute request from the UI layer.
 * 
 *  @see org.merapi.Bridge;
 *  @see org.merapi.messages.IMessage;
 *  @see org.merapi.messages.Message
 */
public class SystemExecuteMessage extends Message
{
    //--------------------------------------------------------------------------
    //
    //  Constants
    //
    //--------------------------------------------------------------------------

    /**
     *  Message type for a system execute command.
     */
    public static final String SYSTEM_EXECUTE = "systemExecute";
     
     		
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */		
	public SystemExecuteMessage() 
	{
		super();
	}
	
	
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  args
    //----------------------------------

    /**
     *  A set of args to use as the system execute parameters
     */		
	public String[] getArgs() 
	{ 
		Object[] data = (Object[])getData();
		String[] args = new String[ data.length ];
		for ( int i = 0; i < data.length; i++ )
		{
			args[ i ] = (String)data[ i ];
		}
		return args; 
	}
} 
