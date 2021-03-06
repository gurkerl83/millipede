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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import java.util.logging.Level;
//import org.merapi.internal.config.BridgeConfig;
import org.merapi.handlers.IMessageHandler;
import org.merapi.internal.io.amf.AMF3Reader;
import org.merapi.internal.io.amf.AMF3Writer;
import org.merapi.internal.io.reader.IReader;
import org.merapi.internal.io.writer.IWriter;
import org.merapi.messages.IMessage;
import org.merapi.internal.systemexecute.handlers.SystemExecuteMessageHandler;

import org.apache.log4j.Logger;
import org.merapi.BridgeService;
//import org.springframework.context.ApplicationContext;

import org.merapi.io.utils.MessageLengthWriter;


/**
 * The <code>Bridge</code> class is a singleton gateway to the Flex Merapi bridge.
 * IMessages are exchanged between this Java bridge object and the Flex bridge object.
 *
 * @see org.merapi.messages.IMessage;
 */
public class Bridge implements Runnable, BridgeService
{
	//--------------------------------------------------------------------------
	//
	//  Class Constants
	//
	//--------------------------------------------------------------------------
	
	public static final String CONFIG_PATH 			= "./config/merapi-native-config.xml";
	public static final String READER_STRING 		= "reader";
	public static final String WRITER_STRING 		= "writer";
	public static final String PORT_STRING	 		= "port";
	
	
	//--------------------------------------------------------------------------
	//
	//  Class Properties
	//
	//--------------------------------------------------------------------------

	/**
	 * The port used to connect Merapi
	 */
	public static int PORT                         = 12345;
	
	
	//--------------------------------------------------------------------------
	//
	//  Class Methods
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  The single instance of the Merapi <code>Bridge</code>
	 */	
    public static Bridge getInstance()
    {
    	if ( instance == null )
    	{
			instance = new Bridge();
			instance.registerHandlers();
    	}
    	
    	return instance;
    }
    
    /**
     *  Opens the Merapi server socket
     */
    public static void open() 
    {
    	if ( thread == null )
    	{
    		Bridge.thread 			= new Thread( Bridge.getInstance() );
    		Bridge.thread.start();
    		
    		Bridge.isRunning		= true;
    	}
    }	
    
    /**
     *  Closes the Merapi server socket
     */    
    public static void close()
    {
    	Bridge.isRunning			= false;
    	Bridge.thread 				= null;
    	
    	try
    	{
    		instance.__server.close();
    	}
    	catch ( IOException exception )
    	{
    		Bridge.instance.__logger.error( exception );
    	}
    }

    
	//--------------------------------------------------------------------------
	//
	//  Class Variables
	//
	//--------------------------------------------------------------------------
	
    private static Bridge 	instance	= null;
    private static Thread	thread		= null;
    private static boolean	isRunning 	= true;
    

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Constructor.
     */
    private Bridge() 
    {
//    	super();
    	
    	readConfig();
    	
    	__handlers = new HashMap<String, ArrayList<IMessageHandler>>();
    }
	

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------
    
    /**
     *  Called by start() from the Threading API.
     */
    public void run()
    {
    	try 
    	{
    		//  Open the server socket
    		__server = new ServerSocket( PORT );
//                __server = new ServerSocket(PORT, 1, InetAddress.getByName("127.0.0.1"));


    		__logger.info( "Merapi started on port: " + PORT );
    		System.out.println("LocalSocketAddress: "  + __server.getLocalSocketAddress() + "   InetAddress: " + __server.getInetAddress() + "   Port: " + __server.getLocalPort());

                if(Bridge.isRunning == true)
                    System.out.println("Bridge is running");
                else
                    System.out.println("Bridge is not running");

                while( Bridge.isRunning == true )
    		{
    			//  Get the first client that connects
    			Socket temp = __server.accept();
    			__client    = temp;

                        System.out.println("Local Socket Address: " + __client.getLocalSocketAddress());
                        System.out.println("Remote Socket Address: " + __client.getRemoteSocketAddress());

                        System.out.println("InetAddress: " + __client.getInetAddress());
                        
                        System.out.println("Reader: " + __reader.toString());

//outcommented in funzt
//                        BufferedInputStream bin = new BufferedInputStream(__client.getInputStream());
// int b;
// while ( ( b = bin.read() ) != -1 )
// {
//
//     char c = (char)b;
//
//     System.out.print(""+(char)b); //This prints out content that is unreadable.
//                                   //Isn't it supposed to print out html tag?
// }
//outcommented in funzt

    			//  Instantiate a listener thread that will listen and read the input stream
    			//  of the first client that connects and start the thread
    			__clientListenerThread = new BridgeListenerThread( __client.getInputStream(), __reader );
                        System.out.println("Reader: " + __clientListenerThread.toString() + "   StackTrace: " + __clientListenerThread.getStackTrace());
    			__clientListenerThread.start();

//outcommented in funzt    			
//                try {
//                    Thread.sleep(250);
//                } catch (InterruptedException ex) {
//                    java.util.logging.Logger.getLogger(Bridge.class.getName()).log(Level.SEVERE, null, ex);
//                }
//outcommented in funzt
    		}
    	}
    	catch ( IOException e )
    	{
            __logger.error( e.getMessage() );
    	}
    }

	/**
	 *  Dispatches an <code>IMessage</code> to registered listeners.
	 */
	public void dispatchMessage( IMessage message )
	{
		//  Get the list of handlers registered for the event type
		ArrayList<IMessageHandler> list = __handlers.get( message.getType() );
		System.out.println("Message type received: (before Message Creation) --- " + message.getType());
		//  If the list is not null and not empty notify the registered event handlers 
		if ( list != null && list.size() > 0 )
		{
			for ( IMessageHandler handler : list )
			{
				if ( handler != null )
				{
					handler.handleMessage( message );
				}
			}
		}
	}
	
	/**
	 *  Registers an <code>IMessageHandler</code> to be notified when messages of type
	 *  <code>type<code> are dispatched from the <code>Bridge</code>.
	 */
	public void registerMessageHandler( String type, IMessageHandler handler )
	{
		//  Get the list of handlers registered for the event type
		ArrayList<IMessageHandler> list = __handlers.get( type );
		
		//  If the list is null, create a new list to add 'handler' to
		if ( list == null )
		{
			list = new ArrayList<IMessageHandler>();
			__handlers.put( type, list );
		}
		
		//  Add the handler to the list
		list.add( handler );
	}	
	
    /**
     *  Sends a <code>message</code> to the Flex side of the Merapi bridge.
     */
    public void sendMessage( IMessage message ) throws Exception 
    {
        if ( __client == null || __client.isClosed() ) return;

        byte[] bytes = __writer.write( message );

        System.out.println( "Sending " + bytes.length + " bytes." );
        
        //  Send the length of the message first
//        __client.getOutputStream().write( bytes.length >> 24 );
//        __client.getOutputStream().write( bytes.length >> 16 );
//        __client.getOutputStream().write( bytes.length >> 8 );
//        __client.getOutputStream().write( bytes.length );
        
        __messageLengthWriter.write(__client, bytes.length);

        //  Send the message
        __client.getOutputStream().write( bytes );
    }
    
    /**
	 *  Unregisters a given handler.
	 */		
	public void unRegisterMessageHandler( String type, IMessageHandler handler )
	{
		//  Get the list of handlers registered for the event type
		ArrayList<IMessageHandler> list = __handlers.get( type );
		
		//  If the list is not null and not empty, look for handler in the list and remove it
		//  if a match is found
		if ( list != null && list.size() > 0 )
		{
			for ( IMessageHandler activeHandler : list )
			{
				if ( activeHandler == handler )
				{
					list.remove( handler );
				}
			}
		}
	}
    
    /**
	 *  @protected
	 *  
	 *  Instantiates the framework <code>IMessageHandlers</code>.
	 */
	protected void registerHandlers()
	{
		//  Registers SystemExecuteHandler as the IMessageHandler of the 
		//  SystemExecuteMessage.SYSTEM_EXECUTE message type.
		new SystemExecuteMessageHandler();
	}
	
    /**
     *  @protected
     *  
     *  Loads the Bridge config via the BridgeConfig object.
     */
    protected void readConfig()
    {
//        try
//        {
//            BridgeConfig bridgeConfig 	= new BridgeConfig( CONFIG_PATH );
//            Properties config 			= bridgeConfig.getProps();
//            ApplicationContext ctx 		= bridgeConfig.getContext();
//
//            __reader 					= (IReader) ctx.getBean( config.getProperty( READER_STRING ) );
//            __writer					= (IWriter) ctx.getBean( config.getProperty( WRITER_STRING ) );
//
//            PORT 						= Integer.parseInt( config.getProperty( PORT_STRING ) );
//
//            bridgeConfig 				= null;
//        }
//
//        catch ( Exception e )
//        {
//            __logger.error( e.getMessage() );
//        }
        
        if ( __reader == null ) __reader = new AMF3Reader();
        if ( __writer == null ) __writer = new AMF3Writer();
    }


	
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 *  
	 *  The socket that open connections to the Flex Merapi bridge.
	 */
	private ServerSocket 			__server 				= null;
	
	/**
	 *  @private
	 *  
	 *  The socket that connected to the Flex Merapi bridge.
	 */
	private Socket 					__client 				= null;
		
	/**
	 *  @private
	 *  
	 *  The thread that listens for messages from the Flex client socket.
	 */
	private BridgeListenerThread 	__clientListenerThread 	= null;
		
	/**
	 *  @private
	 *  
	 *  The <code>IWriter</code> used to serialize data sent across the bridge to Flex.
	 */
	private IWriter 				__writer 				= null;
	
	/**
	 *  @private
	 *  
	 *  The <code>IReader</code> used to deserialize data that comes across the bridge from Flex.
	 */
	private IReader 				__reader 				= null;
	
    /**
     *  @private 
     * 
     *  An instance of the log4j logger to handle the logging.
     */
    private Logger 					__logger 				= Logger.getLogger( Bridge.class );
    
    /**
     *  @private
     *  
     *  A HashMap of registered event handlers.
     */
    private HashMap<String, ArrayList<IMessageHandler>> __handlers = null;

    private MessageLengthWriter __messageLengthWriter = new MessageLengthWriter();
}