/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cyberduck;

import ch.cyberduck.core.Collection;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.LoginCanceledException;
import ch.cyberduck.core.Preferences;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.Transfer;
import ch.cyberduck.core.TransferAdapter;
import ch.cyberduck.core.TransferCollection;
import ch.cyberduck.core.TransferListener;
import ch.cyberduck.core.TransferOptions;
import ch.cyberduck.core.threading.AbstractBackgroundAction;
import ch.cyberduck.handler.ResultContext;
import ch.cyberduck.handler.ResultHandler;
import ch.cyberduck.service.TransferControllerService;
import ch.cyberduck.threading.AlertRepeatableBackgroundAction;
import ch.cyberduck.threading.WindowMainAction;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author gurkerl
 */
public class TransferController extends WindowController implements TransferControllerService {

    private static Logger log = Logger.getLogger(TransferController.class);
//    private static TransferController instance = null;
//
//    private TransferController() {
//        this.loadBundle();
//    }
//
//    public static TransferController instance() {
////	        synchronized(NSApplication.sharedApplication()) {
//        if (null == instance) {
//            instance = new TransferController();
//        }
//        return instance;
////	        }
//    }
    /**
	 * @uml.property  name="resultHandler"
	 * @uml.associationEnd  
	 */
    private ResultHandler resultHandler;

    /**
	 * @return
	 * @uml.property  name="resultHandler"
	 */
    public ResultHandler getResultHandler() {
		return resultHandler;
	}

	/**
	 * @param resultHandler
	 * @uml.property  name="resultHandler"
	 */
	public void setResultHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}

	public TransferController() { //ResultHandler resultHandler) {
        this.loadBundle();
//        this.resultHandler = resultHandler;
        setQueueTable();
    }

    @Override
    protected String getBundleName() {
        return "Transfer";
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
    /**
	 * @uml.property  name="urlField"
	 */
    private String urlField;

    /**
	 * @param urlField
	 * @uml.property  name="urlField"
	 */
    public void setUrlField(String urlField) {
        this.urlField = urlField;
//        this.urlField.setAllowsEditingTextAttributes(true);
//        this.urlField.setSelectable(true);
    }
    /**
	 * @uml.property  name="localField"
	 */
    private String localField;

    /**
	 * @param localField
	 * @uml.property  name="localField"
	 */
    public void setLocalField(String localField) {
        this.localField = localField;
    }
//    @Outlet
//    private NSImageView iconView;
//
//    public void setIconView(final NSImageView iconView) {
//        this.iconView = iconView;
//    }
//    @Outlet
//    private NSStepper queueSizeStepper;
//
//    public void setQueueSizeStepper(final NSStepper queueSizeStepper) {
//        this.queueSizeStepper = queueSizeStepper;
//        this.queueSizeStepper.setTarget(this.id());
//        this.queueSizeStepper.setAction(Foundation.selector("queueSizeStepperChanged:"));
//    }
    /**
	 * @uml.property  name="filterField"
	 */
    private String filterField;

    /**
	 * @param filterField
	 * @uml.property  name="filterField"
	 */
    public void setFilterField(String filterField) {
        this.filterField = filterField;
//        NSNotificationCenter.defaultCenter().addObserver(this.id(),
//                Foundation.selector("filterFieldTextDidChange:"),
//                NSControl.NSControlTextDidChangeNotification,
//                this.filterField);
    }

//    public void filterFieldTextDidChange(NSNotification notification) {
//        transferTableModel.setFilter(filterField.stringValue());
//        this.reloadData();
//    }
    @Override
    protected void invalidate() {
//        transferTableModel.invalidate();
        super.invalidate();
    }

    /**
     * Remove this item form the list
     *
     * @param transfer
     */
    public void removeTransfer(final Transfer transfer) {
        TransferCollection.instance().remove(transfer);
//        this.reloadData();
    }

    /**
     * Add this item to the list; select it and scroll the view to make it visible
     *
     * @param transfer
     */
    public void addTransfer(final Transfer transfer) {
        TransferCollection.instance().add(transfer);
        final int row = TransferCollection.instance().size() - 1;
//        this.reloadData();
//        final NSInteger index = new NSInteger(row);
//        transferTable.selectRowIndexes(NSIndexSet.indexSetWithIndex(index), false);
//        transferTable.scrollRowToVisible(index);
    }

    /**
     * @param transfer
     */
    public void startTransfer(final Transfer transfer) {
        this.startTransfer(transfer, false, false);
    }

    /**
     * @param transfer
     * @param resumeRequested
     * @param reloadRequested
     */
    private void startTransfer(final Transfer transfer, final boolean resumeRequested, final boolean reloadRequested) {
        if (!TransferCollection.instance().contains(transfer)) {
            this.addTransfer(transfer);
        }
        if (Preferences.instance().getBoolean("queue.orderFrontOnStart")) {
//            this.window.makeKeyAndOrderFront(null);
        }
        this.background(new AlertRepeatableBackgroundAction(this) {

            private boolean resume = resumeRequested;
            private boolean reload = reloadRequested;
            private TransferListener tl;

            @Override
            public boolean prepare() {
                transfer.addListener(tl = new TransferAdapter() {

                    @Override
                    public void transferQueued() {
//                        validateToolbar();
                    }

                    @Override
                    public void transferResumed() {
//                        validateToolbar();
                    }

                    @Override
                    public void transferWillStart() {
//                        validateToolbar();
                    }

                    @Override
                    public void transferDidEnd() {
//                        validateToolbar();
                    }
                });
//                if(transfer.getSession() instanceof ch.cyberduck.core.sftp.SFTPSession) {
//                    ((ch.cyberduck.core.sftp.SFTPSession) transfer.getSession()).setHostKeyVerificationController(
//                            new HostKeyController(TransferController.this));
//                }
//                transfer.getSession().setLoginController(new LoginController(TransferController.this));
                transfer.getSession().setLoginController(new LoginController(TransferController.this) {

                    @Override
                    public void prompt(final Host host, final String reason, final String message)
                            throws LoginCanceledException {

                        ResultContext result = new ResultContext();
                        result.nextResultObject(reason);
                        List<Host> hosts = new ArrayList<Host>();
                        resultHandler.handleResult(result, hosts);
//
//                        if (reason.equals("Login succeeded")) {
//                            result.nextResultObject(reason);
//                            resultHandler.handleResult(result);
//
//                        } else if (reason.equals("Login failed")) {
//
//                        } else {
//                            log.debug("not handled message");
////                	}
                    }
                });

                return super.prepare();
            }

            public void run() {
                final TransferOptions options = new TransferOptions();
                options.reloadRequested = reload;
                options.resumeRequested = resume;
//                transfer.start(TransferPromptController.create(TransferController.this, transfer), options);
            }

            @Override
            public void finish() {
                super.finish();
//                if(transfer.getSession() instanceof ch.cyberduck.core.sftp.SFTPSession) {
//                    ((ch.cyberduck.core.sftp.SFTPSession) transfer.getSession()).setHostKeyVerificationController(null);
//                }
                transfer.getSession().setLoginController(null);
                transfer.removeListener(tl);
                // Upon retry, use resume
                reload = false;
                resume = true;
            }

            @Override
            public void cleanup() {
                if (transfer.isComplete() && !transfer.isCanceled()) {
//                    if(transfer.isReset()) {
//                        if(Preferences.instance().getBoolean("queue.removeItemWhenComplete")) {
//                            removeTransfer(transfer);
//                        }
//                        if(Preferences.instance().getBoolean("queue.orderBackOnStop")) {
//                            if(!(TransferCollection.instance().numberOfRunningTransfers() > 0)) {
//                                window().close();
//                            }
//                        }
//                    }
                }
                TransferCollection.instance().save();
            }

            @Override
            public Session getSession() {
                return transfer.getSession();
            }

            @Override
            public void pause() {
                transfer.fireTransferQueued();
                super.pause();
                transfer.fireTransferResumed();
            }

            @Override
            public boolean isCanceled() {
                if ((transfer.isRunning() || transfer.isQueued()) && transfer.isCanceled()) {
                    return true;
                }
                return super.isCanceled();
            }

            @Override
            public void log(final boolean request, final String message) {
//                if(logDrawer.state() == NSDrawer.OpenState) {
//                    invoke(new WindowMainAction(TransferController.this) {
//                        public void run() {
//                            TransferController.this.transcript.log(request, message);
//                        }
//                    });
//                }
                super.log(request, message);
            }
            private final Object lock = new Object();

            @Override
            public Object lock() {
                // No synchronization with other tasks
                return lock;
            }
        });


    }

    private void validateToolbar() {
        invoke(new WindowMainAction(TransferController.this) {

            public void run() {
//                window.toolbar().validateVisibleItems();
                updateIcon();
            }
        });
    }

    /**
     *
     */
    private void updateIcon() {
        log.debug("updateIcon");
//        final int selected = transferTable.numberOfSelectedRows().intValue();
//        if(1 != selected) {
//            iconView.setImage(null);
//            return;
//        }
//        final Transfer transfer = transferTableModel.getSource().get(transferTable.selectedRow().intValue());
//         Draw file type icon
//        if(transfer.numberOfRoots() == 1) {
//            iconView.setImage(IconCache.instance().iconForPath(transfer.getRoot().getLocal(), 32));
//        }
//        else {
//            iconView.setImage(IconCache.iconNamed("NSMultipleDocuments", 32));
//        }
    }
    
//    @Outlet
//    private NSTableView transferTable;
    /**
	 * @uml.property  name="transferTableModel"
	 * @uml.associationEnd  
	 */
    private TransferTableDataSource transferTableModel;
//    private AbstractTableDelegate<Transfer> transferTableDelegate;

    public void setQueueTable() { //NSTableView view) {
        transferTableModel = new TransferTableDataSource();
    }

    
//    @Action
    public void queueSizeStepperChanged() { //final ID sender) {
//        synchronized(Queue.instance()) {
//            Queue.instance().notify();
//        }
    }
    
    /**
     * Change focus to filter field
     *
     * @param sender
     */
//    @Action
    public void searchButtonClicked() { //final ID sender) {
//        this.window().makeFirstResponder(this.filterField);
    }
    
    
//    @Action
    public void bandwidthPopupChanged() { //NSPopUpButton sender) {
//        NSIndexSet iterator = transferTable.selectedRowIndexes();
//        int bandwidth = Integer.valueOf(sender.selectedItem().representedObject());
//        for(NSUInteger index = iterator.firstIndex(); !index.equals(NSIndexSet.NSNotFound); index = iterator.indexGreaterThanIndex(index)) {
//            Transfer transfer = TransferCollection.instance().get(index.intValue());
//            transfer.setBandwidth(bandwidth);
//        }
//        this.updateBandwidthPopup();
    }
    
//    @Action
    public void paste() { //final ID sender) {
        log.debug("paste");
//        final Map<Host, PathPasteboard<NSDictionary>> boards = PathPasteboard.allPasteboards();
//        if(!boards.isEmpty()) {
//            for(PathPasteboard<NSDictionary> pasteboard : boards.values()) {
//                TransferCollection.instance().add(new DownloadTransfer(pasteboard.getFiles()));
//            }
//            this.reloadData();
//        }
//        boards.clear();
    }

//    @Action
    public void stopButtonClicked() { //final ID sender) {
//        NSIndexSet iterator = transferTable.selectedRowIndexes();
//        for(NSUInteger index = iterator.firstIndex(); !index.equals(NSIndexSet.NSNotFound); index = iterator.indexGreaterThanIndex(index)) {
//            final Transfer transfer = transferTableModel.getSource().get(index.intValue());
//            if(transfer.isRunning()) {
//                this.background(new AbstractBackgroundAction() {
//                    public void run() {
//                        transfer.cancel();
//                    }
//                });
//            }
//        }
    }

//    @Action
    public void stopAllButtonClicked() { //final ID sender) {
        final Collection<Transfer> transfers = transferTableModel.getSource();
        for(final Transfer transfer : transfers) {
            if(transfer.isRunning()) {
                this.background(new AbstractBackgroundAction() {
                    public void run() {
                        transfer.cancel();
                    }
                });
            }
        }
    }

//    @Action
    public void resumeButtonClicked() { //final ID sender) {
//        NSIndexSet iterator = transferTable.selectedRowIndexes();
//        for(NSUInteger index = iterator.firstIndex(); !index.equals(NSIndexSet.NSNotFound); index = iterator.indexGreaterThanIndex(index)) {
//            final Collection<Transfer> transfers = transferTableModel.getSource();
//            final Transfer transfer = transfers.get(index.intValue());
//            if(!transfer.isRunning()) {
//                this.startTransfer(transfer, true, false);
//            }
//        }
    }

//    @Action
    public void reloadButtonClicked() { //final ID sender) {
//        NSIndexSet iterator = transferTable.selectedRowIndexes();
//        for(NSUInteger index = iterator.firstIndex(); !index.equals(NSIndexSet.NSNotFound); index = iterator.indexGreaterThanIndex(index)) {
//            final Collection<Transfer> transfers = transferTableModel.getSource();
//            final Transfer transfer = transfers.get(index.intValue());
//            if(!transfer.isRunning()) {
//                this.startTransfer(transfer, false, true);
//            }
//        }
    }

//    @Action
    public void openButtonClicked() { //final ID sender) {
//        if(transferTable.numberOfSelectedRows().intValue() == 1) {
//            final Transfer transfer = transferTableModel.getSource().get(transferTable.selectedRow().intValue());
//            for(Path i : transfer.getRoots()) {
//                Local l = i.getLocal();
//                if(!l.open()) {
//                    if(transfer.isComplete()) {
//                        this.alert(NSAlert.alert(Locale.localizedString("Could not open the file"), //title
//                                Locale.localizedString("Could not open the file") + " \""
//                                        + l.getDisplayName()
//                                        + "\". " + Locale.localizedString("It moved since you downloaded it."), // message
//                                Locale.localizedString("OK"), // defaultbutton
//                                null, //alternative button
//                                null //other button
//                        ));
//                    }
//                    else {
//                        this.alert(NSAlert.alert(Locale.localizedString("Could not open the file"), //title
//                                Locale.localizedString("Could not open the file") + " \""
//                                        + l.getDisplayName()
//                                        + "\". " + Locale.localizedString("The file has not yet been downloaded."), // message
//                                Locale.localizedString("OK"), // defaultbutton
//                                null, //alternative button
//                                null //other button
//                        ));
//                    }
//                }
//            }
//        }
    }

//    @Action
    public void revealButtonClicked() { //final ID sender) {
//        NSIndexSet iterator = transferTable.selectedRowIndexes();
//        final Collection<Transfer> transfers = transferTableModel.getSource();
//        for(NSUInteger index = iterator.firstIndex(); !index.equals(NSIndexSet.NSNotFound); index = iterator.indexGreaterThanIndex(index)) {
//            final Transfer transfer = transfers.get(index.intValue());
//            for(Path i : transfer.getRoots()) {
//                Local l = i.getLocal();
//                // If a second path argument is specified, a new file viewer is opened. If you specify an
//                // empty string (@"") for this parameter, the file is selected in the main viewer.
//                if(!NSWorkspace.sharedWorkspace().selectFile(l.getAbsolute(), l.getParent().getAbsolute())) {
//                    if(transfer.isComplete()) {
//                        this.alert(NSAlert.alert(Locale.localizedString("Could not show the file in the Finder"), //title
//                                Locale.localizedString("Could not show the file") + " \""
//                                        + l.getDisplayName()
//                                        + "\". " + Locale.localizedString("It moved since you downloaded it."), // message
//                                Locale.localizedString("OK"), // defaultbutton
//                                null, //alternative button
//                                null //other button
//                        ));
//                    }
//                    else {
//                        this.alert(NSAlert.alert(Locale.localizedString("Could not show the file in the Finder"), //title
//                                Locale.localizedString("Could not show the file") + " \""
//                                        + l.getDisplayName()
//                                        + "\". " + Locale.localizedString("The file has not yet been downloaded."), // message
//                                Locale.localizedString("OK"), // defaultbutton
//                                null, //alternative button
//                                null //other button
//                        ));
//                    }
//                }
//                else {
//                    break;
//                }
//            }
//        }
    }

//    @Action
    public void deleteButtonClicked() { //final ID sender) {
//        NSIndexSet iterator = transferTable.selectedRowIndexes();
//        final Collection<Transfer> transfers = transferTableModel.getSource();
//        for(NSUInteger index = iterator.firstIndex(); !index.equals(NSIndexSet.NSNotFound); index = iterator.indexGreaterThanIndex(index)) {
//            final Transfer transfer = transfers.get(index.intValue());
//            if(!transfer.isRunning()) {
//                TransferCollection.instance().remove(transfer);
//            }
//        }
//        TransferCollection.instance().save();
//        this.reloadData();
    }

//    @Action
    public void clearButtonClicked() { //final ID sender) {
//        final Collection<Transfer> transfers = transferTableModel.getSource();
//        for(Transfer transfer : transfers) {
//            if(!transfer.isRunning() && transfer.isComplete()) {
//                TransferCollection.instance().remove(transfer);
//            }
//        }
//        TransferCollection.instance().save();
//        this.reloadData();
    }

//    @Action
    public void trashButtonClicked() { //final ID sender) {
//        NSIndexSet iterator = transferTable.selectedRowIndexes();
//        final Collection<Transfer> transfers = transferTableModel.getSource();
//        for(NSUInteger index = iterator.firstIndex(); !index.equals(NSIndexSet.NSNotFound); index = iterator.indexGreaterThanIndex(index)) {
//            final Transfer transfer = transfers.get(index.intValue());
//            if(!transfer.isRunning()) {
//                for(Path path : transfer.getRoots()) {
//                    path.getLocal().delete();
//                }
//            }
//        }
//        this.updateIcon();
    }
}
