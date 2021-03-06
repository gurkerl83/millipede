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

package org.merapi.internal;

import org.merapi.handlers.MessageHandler;
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
		Bridge.open();
		//funzt excluded for local testing
//		( new PolicyServer( 12344, new String[] { "millipede.me:80", "127.0.0.1:12345" } ) ).start();
		
//		new MessageHandler( "test" )
//		{
//			public void handleMessage( IMessage m )
//			{
//				System.out.println( m );
//				
//				try {
//					Bridge.getInstance().sendMessage( m );
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		};
	}

}
