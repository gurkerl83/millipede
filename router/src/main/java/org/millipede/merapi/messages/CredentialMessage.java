package org.millipede.merapi.messages;

import java.util.ArrayList;

import java.util.List;
import org.merapi.messages.Message;

public class CredentialMessage extends Message {

    //--------------------------------------------------------------------------
    //
    //  Constants
    //
    //--------------------------------------------------------------------------
    /**
     *  Message type for a SAY_IT message.
     */
    public static final String CREDENTIAL_MESSAGE = "credentialMessage";
    //send out from flex to java after successfully logon in the system
    public static final String REGISTER_REQUEST = "registerRequest";
    public static final String REGISTER_RESPOND = "registerRespond";
    public static final String LOGIN_REQUEST = "loginRequest";
    public static final String LOGIN_RESPOND = "loginRespond";
    public static final String LOGOUT_REQUEST = "logoutRequest";
    public static final String LOGOUT_RESPOND = "logoutRespond";
    public static final String SUCCEEDED = "succeeded";
    public static final String FAILED = "failed";


    public String processType;
    public String status;
    public String username;
    public String password;
    public String mail;
}
