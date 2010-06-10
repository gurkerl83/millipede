//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.milipede.storage.layer.internal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

import javax.swing.Timer;

//import org.milipede.api.config.Configuration;
//import org.milipede.api.config.SubConfiguration;
//import org.milipede.api.event.JDBroadcaster;
//import jd.gui.swing.components.Balloon;
import org.milipede.storage.layer.AccountControllerService;
import org.milipede.storage.layer.domain.Account;
import org.milipede.storage.layer.domain.AccountInfo;
import org.millipede.router.vo.ProviderVO;
//import org.milipede.api.plugins.ProviderVO;
//import jd.utils.JDTheme;
//import org.milipede.api.utils.MilipedeUtilities;
//import org.milipede.api.controlling.MilipedeLogger;
//import org.milipede.controller.internal.MilipedeController;
//import org.milipede.jm.event.JDBroadcaster;


public class AccountController implements AccountControllerService { //extends SubConfiguration implements ActionListener, AccountControllerListener, AccountControllerService {

    private static final long serialVersionUID = -7560087582989096645L;

//     Account <-- uses AccountInfo (specific to JDownloader)
//     Create a AccountInfo for every storagetype
    private static TreeMap<String, ArrayList<Account>> hosteraccounts = null;

//    private static AccountControllerService INSTANCE = null;

//     JDBroadCaster dep eleminated through OSGi Event Mechanism
//    private JDBroadcaster<AccountControllerListener, AccountControllerEvent> broadcaster = new JDBroadcaster<AccountControllerListener, AccountControllerEvent>() {
//
//        @Override
//        protected void fireEvent(AccountControllerListener listener, AccountControllerEvent event) {
//            listener.onAccountControllerEvent(event);
//        }
//
//    };

    private Timer asyncSaveIntervalTimer;

    private boolean saveinprogress = false;

    private long lastballoon = 0;

    private long waittimeAccountInfoUpdate = 15 * 60 * 1000l;

    private final long ballooninterval = 30 * 60 * 1000l;

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#getUpdateTime()
	 */
    public long getUpdateTime() {
        return waittimeAccountInfoUpdate;
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#setUpdateTime(long)
	 */
    public void setUpdateTime(long time) {
        this.waittimeAccountInfoUpdate = time;
    }

    private static Comparator<Account> COMPARE_MOST_TRAFFIC_LEFT = new Comparator<Account>() {

        public int compare(Account o1, Account o2) {
            AccountInfo ai1 = o1.getAccountInfo();
            AccountInfo ai2 = o2.getAccountInfo();
            if (ai1 != null && ai2 != null) {
                if (ai1.getTrafficLeft() < ai2.getTrafficLeft()) {
                    return -1;
                } else {
                    return 1;
                }
            }
            return 0;
        }

    };

    public AccountController() {
//    private AccountController() {
//        super("AccountController");
//        asyncSaveIntervalTimer = new Timer(2000, this);
        asyncSaveIntervalTimer.setInitialDelay(2000);
        asyncSaveIntervalTimer.setRepeats(false);
        hosteraccounts = loadAccounts();
//        broadcaster.addListener(this);
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#updateAccountInfo(org.milipede.api.plugin.ProviderVO, org.milipede.storage.layer.domain.Account, boolean)
	 */
    public AccountInfo updateAccountInfo(ProviderVO host, Account account, boolean forceupdate) {
        return updateAccountInfo(host.getTitle(), account, forceupdate);
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#updateAccountInfo(java.lang.String, org.milipede.storage.layer.domain.Account, boolean)
	 */
    public AccountInfo updateAccountInfo(String host, Account account, boolean forceupdate) {
        String hostname = host != null ? host : getHosterName(account);
        if (hostname == null) {
            account.setAccountInfo(null);
//            logger.severe("Cannot update AccountInfo, no Hostername available!");
            return null;
        }
//        ProviderVO plugin = MilipedeUtilities.getNewProviderVOInstance(hostname);
        ProviderVO plugin = null;
        if (plugin == null) {
            account.setAccountInfo(null);
//            logger.severe("Cannot update AccountInfo, no HosterPlugin available!");
            return null;
        }
        AccountInfo ai = account.getAccountInfo();
        if (!forceupdate) {
            if (account.lastUpdateTime() != 0 && ai != null && ai.isExpired()) {
                /* account is expired, no need to update */
                return ai;
            }
            if (!account.isValid() && account.lastUpdateTime() != 0) {
                /* account is invalid, no need to update */
                return ai;
            }
            if ((System.currentTimeMillis() - account.lastUpdateTime()) < waittimeAccountInfoUpdate) {
                /*
                 * account was checked before, timeout for recheck not reached,
                 * no need to update
                 */
                return ai;
            }
        }
        try {
            account.setUpdateTime(System.currentTimeMillis());
            /* not every plugin sets this info correct */
            account.setValid(true);
//            ai = plugin.fetchAccountInfo(account);
            if (ai == null) {
                // System.out.println("plugin no update " + hostname);
                /* not every plugin has fetchAccountInfo */
                account.setAccountInfo(null);
//                this.broadcaster.fireEvent(new AccountControllerEvent(this, AccountControllerEvent.ACCOUNT_UPDATE, hostname, account));
                return null;
            }
//            synchronized (ACCOUNT_LOCK) {
//                account.setAccountInfo(ai);
//            }
            if (ai.isExpired()) {
                account.setEnabled(false);
//                this.broadcaster.fireEvent(new AccountControllerEvent(this, AccountControllerEvent.ACCOUNT_EXPIRED, hostname, account));
            } else if (!account.isValid()) {
                account.setEnabled(false);
//                this.broadcaster.fireEvent(new AccountControllerEvent(this, AccountControllerEvent.ACCOUNT_INVALID, hostname, account));
            }
//            else {
//                this.broadcaster.fireEvent(new AccountControllerEvent(this, AccountControllerEvent.ACCOUNT_UPDATE, hostname, account));
//            }
//        } catch (IOException e) {
//            logger.severe("AccountUpdate: " + host + " failed!");
        }
        catch (Exception e) {
//            logger.severe("AccountUpdate: " + host + " failed!");
//            MilipedeLogger.exception(e);
            account.setAccountInfo(null);
            account.setEnabled(false);
            account.setValid(false);
//            this.broadcaster.fireEvent(new AccountControllerEvent(this, AccountControllerEvent.ACCOUNT_INVALID, hostname, account));
        }
        return ai;
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#getHosterName(org.milipede.storage.layer.domain.Account)
	 */
    public String getHosterName(Account account) {
        if (account.getHoster() != null) return account.getHoster();
        synchronized (hosteraccounts) {
            for (String host : hosteraccounts.keySet()) {
                if (hosteraccounts.get(host).contains(account)) {
                    account.setHoster(host);
                    return host;
                }
            }
        }
        return null;
    }

//    public synchronized static AccountControllerService getInstance() {
//        if (INSTANCE == null) INSTANCE = new AccountController();
//        return INSTANCE;
//    }

//    public void addListener(AccountControllerListener l) {
//        broadcaster.addListener(l);
//    }

//    public void removeListener(AccountControllerListener l) {
//        broadcaster.removeListener(l);
//    }

    private TreeMap<String, ArrayList<Account>> loadAccounts() {
//        return getGenericProperty("accountlist", new TreeMap<String, ArrayList<Account>>());
        return null;
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#addAccount(org.milipede.api.plugin.ProviderVO, org.milipede.storage.layer.domain.Account)
	 */
    public void addAccount(ProviderVO ProviderVO, Account account) {
        String host = ProviderVO.getTitle();
        addAccount(host, account);
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#hasAccounts(java.lang.String)
	 */
    public boolean hasAccounts(String host) {
        return !getAllAccounts(host).isEmpty();
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#getAllAccounts(org.milipede.api.plugin.ProviderVO)
	 */
    public ArrayList<Account> getAllAccounts(ProviderVO ProviderVO) {
        if (ProviderVO == null) return new ArrayList<Account>();
        return this.getAllAccounts(ProviderVO.getTitle());
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#getAllAccounts(java.lang.String)
	 */
    public ArrayList<Account> getAllAccounts(String host) {
        if (host == null) return new ArrayList<Account>();
        synchronized (hosteraccounts) {
            if (hosteraccounts.containsKey(host)) {
                return hosteraccounts.get(host);
            } else {
                ArrayList<Account> haccounts = new ArrayList<Account>();
                hosteraccounts.put(host, haccounts);
                return haccounts;
            }
        }
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#validAccounts()
	 */
    public int validAccounts() {
        int count = 0;
        synchronized (hosteraccounts) {
            for (ArrayList<Account> accs : hosteraccounts.values()) {
                for (Account acc : accs) {
                    if (acc.isEnabled()) count++;
                }
            }
        }
        return count;
    }

    private void addAccount(String host, Account account) {
        if (host == null) return;
        if (account == null) return;
        synchronized (hosteraccounts) {
            if (hosteraccounts.containsKey(host)) {
                ArrayList<Account> haccounts = hosteraccounts.get(host);
                synchronized (haccounts) {
                    boolean b = haccounts.contains(account);
                    if (!b) {
                        boolean b2 = false;
                        ArrayList<Account> temp = new ArrayList<Account>(haccounts);
                        for (Account acc : temp) {
                            if (acc.equals(account)) {
                                b2 = true;
                                break;
                            }
                        }
                        if (!b2) {
                            haccounts.add(account);
                            b = true;
                        }
                    }
//                    if (b) this.broadcaster.fireEvent(new AccountControllerEvent(this, AccountControllerEvent.ACCOUNT_ADDED, host, account));
                }
            } else {
                ArrayList<Account> haccounts = new ArrayList<Account>();
                haccounts.add(account);
                hosteraccounts.put(host, haccounts);
//                this.broadcaster.fireEvent(new AccountControllerEvent(this, AccountControllerEvent.ACCOUNT_ADDED, host, account));
            }
        }
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#removeAccount(java.lang.String, org.milipede.storage.layer.domain.Account)
	 */
    public boolean removeAccount(String hostname, Account account) {
        if (account == null) return false;
        String host = hostname;
        if (host == null) host = getHosterName(account);
        if (host == null) return false;
        synchronized (hosteraccounts) {
            if (!hosteraccounts.containsKey(host)) return false;
            ArrayList<Account> haccounts = hosteraccounts.get(host);
            synchronized (haccounts) {
                boolean b = haccounts.remove(account);
                if (!b) {
                    ArrayList<Account> temp = new ArrayList<Account>(haccounts);
                    for (Account acc : temp) {
                        if (acc.equals(account)) {
                            account = acc;
                            b = haccounts.remove(account);
                            break;
                        }
                    }
                }
//                if (b) this.broadcaster.fireEvent(new AccountControllerEvent(this, AccountControllerEvent.ACCOUNT_REMOVED, host, account));
                return b;
            }
        }
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#removeAccount(org.milipede.api.plugin.ProviderVO, org.milipede.storage.layer.domain.Account)
	 */
    public boolean removeAccount(ProviderVO ProviderVO, Account account) {
        if (account == null) return false;
        if (ProviderVO == null) return removeAccount((String) null, account);
        return removeAccount(ProviderVO.getTitle(), account);
    }

    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == asyncSaveIntervalTimer) {
            saveSync();
        }
    }

//    public void onAccountControllerEvent(AccountControllerEvent event) {
//        switch (event.getID()) {
//        case AccountControllerEvent.ACCOUNT_ADDED:
//            MilipedeUtilities.getConfiguration().setProperty(Configuration.PARAM_USE_GLOBAL_PREMIUM, true);
//            MilipedeUtilities.getConfiguration().save();
//            saveAsync();
//            break;
//        case AccountControllerEvent.ACCOUNT_REMOVED:
//        case AccountControllerEvent.ACCOUNT_UPDATE:
//        case AccountControllerEvent.ACCOUNT_EXPIRED:
//        case AccountControllerEvent.ACCOUNT_INVALID:
//            saveAsync();
//            break;
//        default:
//            break;
//        }
//    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#throwUpdateEvent(org.milipede.api.plugin.ProviderVO, org.milipede.storage.layer.domain.Account)
	 */
    public void throwUpdateEvent(ProviderVO ProviderVO, Account account) {
        if (ProviderVO != null) {
//            this.broadcaster.fireEvent(new AccountControllerEvent(this, AccountControllerEvent.ACCOUNT_UPDATE, ProviderVO.getHost(), account));
        } else {
//            this.broadcaster.fireEvent(new AccountControllerEvent(this, AccountControllerEvent.ACCOUNT_UPDATE, null, account));
        }
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#saveAsync()
	 */
    public void saveAsync() {
        if (saveinprogress == true) return;
        asyncSaveIntervalTimer.restart();
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#saveSync()
	 */
    public void saveSync() {
        if (saveinprogress == true) return;
        new Thread() {
            @Override
            public void run() {
                saveSyncnonThread();
            }
        }.start();
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#saveSyncnonThread()
	 */
    public void saveSyncnonThread() {
        asyncSaveIntervalTimer.stop();
//        String id = MilipedeController.requestDelayExit("accountcontroller");
        synchronized (hosteraccounts) {
            saveinprogress = true;
//            save();
            saveinprogress = false;
        }
//        MilipedeController.releaseDelayExit(id);
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.AccountControllerService#getValidAccount(org.milipede.api.plugin.ProviderVO)
	 */
    public Account getValidAccount(ProviderVO ProviderVO) {
        Account ret = null;
        synchronized (hosteraccounts) {
            ArrayList<Account> accounts = new ArrayList<Account>(getAllAccounts(ProviderVO));

//            if (getBooleanProperty(PROPERTY_ACCOUNT_SELECTION, true)) Collections.sort(accounts, COMPARE_MOST_TRAFFIC_LEFT);

            for (int i = 0; i < accounts.size(); i++) {
                Account next = accounts.get(i);
                if (!next.isTempDisabled() && next.isEnabled() && next.isValid()) {
                    ret = next;
                    break;
                }
            }
        }
//        if (ret != null && !MilipedeUtilities.getConfiguration().getBooleanProperty(Configuration.PARAM_USE_GLOBAL_PREMIUM, true)) {
//            if (System.currentTimeMillis() - lastballoon > ballooninterval) {
//                lastballoon = System.currentTimeMillis();
////                Balloon.show(JDL.L("gui.ballon.accountmanager.title", "Accountmanager"), JDTheme.II("gui.images.accounts", 32, 32), JDL.L("gui.accountcontroller.globpremdisabled", "Premiumaccounts are globally disabled!<br/>Click <a href='http://jdownloader.org/knowledge/wiki/gui/premiummenu'>here</a> for help."));
//            }
//            ret = null;
//        }
        return ret;
    }

	
}
