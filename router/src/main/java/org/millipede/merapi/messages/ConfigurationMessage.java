package org.millipede.merapi.messages;




import org.merapi.messages.Message;
//import org.merapi.messages.IMessage;

public class ConfigurationMessage extends Message {

	//--------------------------------------------------------------------------
    //
    //  Constants
    //
    //--------------------------------------------------------------------------

    /**
     *  Message type for a SAY_IT message.
     */
    public static final String CONFIG = "config";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public ConfigurationMessage()
    {
        super();
    }


    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

//    public String action = null;
//    public String fileName = null;
//    public byte[] fileData = null;
//    public String fileReference = null;

    
//	Global Upload-Limitation
//	Max. Upload-Rate
    public String maxUpRate = null;

//	Global Download-Limitation
//	Max. Download-Rate
	public String maxDownRate = null;

//	Number of Connections
//	Maximal Global Number of Connections
	public String maxGlobalConnections = null;
//	Maximal Number of connected Peers per Torrent
	public String maxPeersPerTorrent = null;
//	Number Uploadslots per Torrent
	public String uploadSlotsPerTorrent = null;
	
}