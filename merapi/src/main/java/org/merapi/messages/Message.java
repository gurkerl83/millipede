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

import org.merapi.internal.Bridge;
import org.apache.log4j.Logger;

import java.util.UUID;

/**
 * The <code>Message</code> class implements IMessage, a 'message' sent from the org.merapi
 * Flex bridge.
 *
 * @see org.merapi.Bridge;
 * @see org.merapi.messages.IMessage;
 */
public class Message implements IMessage
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public Message() 
    {
        super();

        this.setUid( UUID.randomUUID().toString() );
    }

    /**
     * Constructor.
     */
    public Message( String type ) 
    {
        this();

        this.setType( type );
    }

    /**
     * Constructor.
     */
    public Message( String type, Object data ) 
    {
        this( type );

        this.setData( data );
    }


    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  type
    //----------------------------------

    /**
     * The type the message.
     */
    public String getType() 
    {
        return __type;
    }
    public void setType( String val ) 
    {
        __type = val;
    }

    //----------------------------------
    //  uid
    //----------------------------------

    /**
     * A unique ID for this message.
     */
    public String getUid() 
    {
        return __uid;
    }
    public void setUid( String val ) 
    {
        __uid = val;
    }

    //----------------------------------
    //  data
    //----------------------------------

    /**
     * The data carried by this message.
     */
    public Object getData() 
    {
        return __data;
    }
    public void setData( Object val ) 
    {
        __data = val;
    }


    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Sends this message across the bridge.
     */
    public void send() 
    {
        try 
        {
            Bridge.getInstance().sendMessage( this );
        }
        catch ( Exception e ) 
        {
            __logger.error( e.getMessage() );
        }
    }


    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     * Used by the getters/setters.
     */
    private String __type 			= null;
    private String __uid 			= null;
    private Object __data 			= null;
    
    private Logger __logger         = Logger.getLogger(Message.class);
}