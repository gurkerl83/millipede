/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.milipede.storage.layer;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import org.milipede.storage.layer.domain.Account;
import org.milipede.storage.layer.domain.AccountInfo;
import org.millipede.router.vo.ProviderVO;

/**
 *
 * @author gurkerl
 */
public interface AccountControllerService {

    void actionPerformed(ActionEvent arg0);

    void addAccount(ProviderVO ProviderVO, Account account);

//    void addListener(AccountControllerListener l);

    ArrayList<Account> getAllAccounts(ProviderVO ProviderVO);

    ArrayList<Account> getAllAccounts(String host);

    String getHosterName(Account account);

    long getUpdateTime();

    Account getValidAccount(ProviderVO ProviderVO);

    boolean hasAccounts(String host);

    boolean removeAccount(String hostname, Account account);

    boolean removeAccount(ProviderVO ProviderVO, Account account);

//    void removeListener(AccountControllerListener l);

    void saveAsync();

    void saveSync();

    void saveSyncnonThread();

    void setUpdateTime(long time);

    void throwUpdateEvent(ProviderVO ProviderVO, Account account);

    AccountInfo updateAccountInfo(ProviderVO host, Account account, boolean forceupdate);

    AccountInfo updateAccountInfo(String host, Account account, boolean forceupdate);

    int validAccounts();

}
