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

package org.merapi.messages;

/**
 *  The <code>IMessage</code> interface describes a 'message' sent from 
 *  the org.merapi bridge.
 * 
 *  @see org.merapi.Bridge;
 *  @see org.merapi.messages.Message;
 */
public interface IMessage {

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

	//----------------------------------
	//  type
	//----------------------------------
	
	/**
	 *  The message type.
	 */		
	String getType();
	
	//----------------------------------
	//  data
	//----------------------------------
	
	/**
	 *  The data of the message.
	 */		
	Object getData();
		
}