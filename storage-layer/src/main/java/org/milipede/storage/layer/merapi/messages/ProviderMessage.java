//package org.milipede.storage.layer.merapi.messages;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.merapi.internal.systemexecute.messages.SystemExecuteMessage;
//import org.merapi.messages.Message;
//import org.milipede.storage.layer.domain.Account;
//import org.milipede.storage.layer.domain.AccountVO;
//import org.milipede.storage.layer.internal.AccountController;
//
//public class ProviderMessage extends Message {
//
//	//--------------------------------------------------------------------------
//    //
//    //  Constants
//    //
//    //--------------------------------------------------------------------------
//
//    /**
//     *  Message type for a SAY_IT message.
//     */
//    public static final String PROVIDER_MESSAGE = "providerMessage";
//
//    //send out from flex to java after successfully logon in the system
//    public static final String INIT_PROVIDER_LIST_REQUEST = "initProviderListRequest";
//    public static final String INIT_PROVIDER_LIST_RESPOND = "initProviderListRespond";
//
//    public static final String LOGIN_PROVIDER_REQUEST = "loginProviderRequest";
//    public static final String LOGIN_PROVIDER_RESPOND = "loginProviderRespond";
//
//    public static final String LOGOUT_PROVIDER_REQUEST = "logoutProviderRequest";
//    public static final String LOGOUT_PROVIDER_RESPOND = "logoutProviderRespond";
//
//    //--------------------------------------------------------------------------
//    //
//    //  Constructor
//    //
//    //--------------------------------------------------------------------------
//
//
//
//    public String processType = null;
//    public String providerType;
//    public ArrayList<AccountVO> provider = null;
//
//    public void convertToAccountVOAccount(ArrayList<Account> acc) {
//        for(Account a: acc) {
//            AccountVO aVO = new AccountVO();
//            aVO.setProviderName(a.getHoster());
//            aVO.setEnabled(a.isEnabled());
//
//            provider.add(aVO);
//        }
//    }
//
//    public ArrayList<Account> convertToAccount() {
//        ArrayList<Account> accounts = new ArrayList<Account>();
//        for(AccountVO vo : provider) {
//            ArrayList<Account> temp = AccountController.getInstance().getAllAccounts(vo.getProviderName());
//            accounts.addAll(temp);
//        }
//        return accounts;
//    }
//
//}