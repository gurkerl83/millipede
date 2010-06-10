package org.merapi.helper.messages;

import org.merapi.messages.Message;

/**
 *
 * @author gurkerl
 */
public class CollaborationMessage extends Message {

    //--------------------------------------------------------------------------
    //
    //  Constants
    //
    //--------------------------------------------------------------------------
    /**
     *  Message type for a SAY_IT message.
     */
    public static final String COLLABORATION = "collaboration";

    //action constants
//    public static final String CONNECT = "connect";
//    public static final String VERIFY = "verify";
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------
    /**
     *  Constructor.
     */
    public CollaborationMessage() {
        super(COLLABORATION);
    }
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    public String action = null;
    public String category;
	
    public String url = null;

    public String providerID;
	public String username;
	public String password;
}

