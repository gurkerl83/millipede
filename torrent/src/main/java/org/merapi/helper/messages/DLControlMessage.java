package org.merapi.helper.messages;

import org.merapi.messages.Message;
//import org.merapi.messages.IMessage;

public class DLControlMessage extends Message {

    //--------------------------------------------------------------------------
    //
    //  Constants
    //
    //--------------------------------------------------------------------------
    /**
     *  Message type for a SAY_IT message.
     */
    public static final String DL_CONTROL = "dlcontrol";
    public static final String LOAD_LOCAL_TORRENT = "loadLocalTorrent";
    public static final String LOAD_REMOTE_TORRENT = "loadRemoteTorrent";
    public static final String REMOVE_TORRENT = "removeTorrent";
    public static final String START_TORRENT = "startTorrent";
    public static final String PAUSE_TORRENT = "pauseTorrent";
    public static final String STOP_TORRENT = "stopTorrent";
    public static final String LOCAL_FILE = "localFile";
    public static final String REMOTE_FILE = "remoteFile";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------
    /**
     *  Constructor.
     */
    public DLControlMessage() {
        super();
    }
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
    public String action = null;
    public String fileName = null;
    public byte[] fileData = null;
    public String fileReference = null;
    public String hashValue = null;
}
