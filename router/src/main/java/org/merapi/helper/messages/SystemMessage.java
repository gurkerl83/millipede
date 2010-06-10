package org.merapi.helper.messages;

import java.util.ArrayList;

import java.util.List;
import org.merapi.messages.Message;

public class SystemMessage extends Message {

    //--------------------------------------------------------------------------
    //
    //  Constants
    //
    //--------------------------------------------------------------------------
    /**
     *  Message type for a SAY_IT message.
     */
    public static final String SYSTEM_MESSAGE = "systemMessage";

    //processType
    public static final String FS_REQUEST = "fsRequest";
    public static final String FS_RESPOND = "fsRespond";

    //mode
    public static final String OPEN = "open";
    public static final String SAVE = "save";

    //status
    public static final String SUCCEEDED = "succeeded";
    public static final String FAILED = "failed";

    public String processType;
    public String mode;

    public String status;
    public String variable;
}
