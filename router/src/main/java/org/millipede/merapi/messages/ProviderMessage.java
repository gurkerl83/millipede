package org.millipede.merapi.messages;

import java.util.ArrayList;

import java.util.List;
import org.merapi.messages.Message;
import org.millipede.router.vo.ProviderVO;


public class ProviderMessage extends Message {

	//--------------------------------------------------------------------------
    //
    //  Constants
    //
    //--------------------------------------------------------------------------

    /**
     *  Message type for a SAY_IT message.
     */
    public static final String PROVIDER_MESSAGE = "providerMessage";
    //send out from flex to java after successfully logon in the system
    public static final String INIT_PROVIDER_LIST_REQUEST = "initProviderListRequest";
    public static final String INIT_PROVIDER_LIST_RESPOND = "initProviderListRespond";
    
    public static final String LOGIN_PROVIDER_REQUEST = "loginProviderRequest";
    public static final String LOGIN_PROVIDER_RESPOND = "loginProviderRespond";
    
    public static final String LOGOUT_PROVIDER_REQUEST = "logoutProviderRequest";
    public static final String LOGOUT_PROVIDER_RESPOND = "logoutProviderRespond";

    public static final String VERIFICATION_SUCCEEDED = "verificationSucceeded";
    public static final String VERIFICATION_FAILED = "verificationFailed";

    public static final String UPLOAD = "upload";
    
    public static final String VERIFICATION_SUCCEEDED_ALREADY_ADDED = "verificationSucceededAlreadyAdded";
    
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    public ProviderMessage() {
    	super(PROVIDER_MESSAGE);
    }
    
    
    public String processType = null;
    public String providerType;
//    public ArrayList<AccountVO> provider = null;
//    public ArrayList<ProviderVO> provider = new ArrayList<ProviderVO>();
    public List<ProviderVO> provider = new ArrayList<ProviderVO>();

	
//    public void convertToAccountVOAccount(ArrayList<Account> acc) {
//        for(Account a: acc) {
//            AccountVO aVO = new AccountVO();
//            aVO.setProviderName(a.getHoster());
//            aVO.setEnabled(a.isEnabled());
//
//            provider.add(aVO);
//        }
//    }

    //funzt
//    public void convertToProviderVO(ArrayList<HostPluginWrapper> acc) {
//        for (HostPluginWrapper wrapper : acc) {
//            ProviderVO pVO = new ProviderVO();
//            pVO.setTitle(wrapper.getHost());
//
//            provider.add(pVO);
//
////        for(Account a: acc) {
////            AccountVO aVO = new AccountVO();
////            aVO.setProviderName(a.getHoster());
////            aVO.setEnabled(a.isEnabled());
////            provider.add(aVO);
////        }
//        }
//    }

  public void convertToProviderVO(ArrayList<Object> acc) {
  for (Object wrapper : acc) {
      ProviderVO pVO = new ProviderVO();
//      pVO.setTitle(wrapper.getHost());

      provider.add(pVO);

//  for(Account a: acc) {
//      AccountVO aVO = new AccountVO();
//      aVO.setProviderName(a.getHoster());
//      aVO.setEnabled(a.isEnabled());
//      provider.add(aVO);
//  }
  }
}

//    public ArrayList<Account> convertToAccount() {
//        ArrayList<Account> accounts = new ArrayList<Account>();
//        for(AccountVO vo : provider) {
//            ArrayList<Account> temp = AccountController.getInstance().getAllAccounts(vo.getProviderName());
//            accounts.addAll(temp);
//        }
//        return accounts;
//    }


}