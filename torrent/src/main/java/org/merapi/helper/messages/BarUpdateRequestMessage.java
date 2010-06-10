package org.merapi.helper.messages;




import org.merapi.messages.Message;
//import org.merapi.messages.IMessage;

public class BarUpdateRequestMessage extends Message {

	//--------------------------------------------------------------------------
    //
    //  Constants
    //
    //--------------------------------------------------------------------------

    /**
     *  Message type for a SAY_IT message.
     */
    public static final String REQUEST_BAR_DATA = "requestBarData";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public BarUpdateRequestMessage()
    {
        super();
    }


    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    public String infoHash = null;
    public int lostCol = 0;
}