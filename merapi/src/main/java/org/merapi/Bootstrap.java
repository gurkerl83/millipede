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

package org.merapi;

import org.merapi.handlers.MessageHandler;
import org.merapi.internal.Bridge;
import org.merapi.internal.PolicyServer;
import org.merapi.messages.IMessage;



/**
 *  The Bootstrap class is used to execute the Merapi Java process.
 */
public class Bootstrap 
{
	
	//--------------------------------------------------------------------------
	//
	//  Class Methods
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  The main method that starts the bridge.
	 */		
	public static void main( String[] args )
	{
		
//		funzt excluded for local testing - funzt auch ohne - auf server testen
//		( new PolicyServer( 12344, new String[] { "millipede.me:80", "127.0.0.1:12345" } ) ).start();

	}

}
