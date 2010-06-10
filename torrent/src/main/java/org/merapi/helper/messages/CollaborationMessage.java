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

    public static final String CONNECT = "connect";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------
    /**
     *  Constructor.
     */
    public CollaborationMessage() {
        super();
    }
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    public String action = null;
    public String url = null;

}
