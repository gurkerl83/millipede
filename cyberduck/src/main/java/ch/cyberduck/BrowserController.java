/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.cyberduck;

import ch.cyberduck.core.Collection;
import ch.cyberduck.core.ConnectionAdapter;
import ch.cyberduck.core.ConnectionListener;
import ch.cyberduck.core.HiddenFilesPathFilter;
import ch.cyberduck.core.HistoryCollection;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Local;
import ch.cyberduck.core.LocalFactory;
import ch.cyberduck.core.NullPathFilter;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathFilter;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.Preferences;
import ch.cyberduck.core.ProgressListener;
import ch.cyberduck.core.Session;
import ch.cyberduck.core.SessionFactory;
import ch.cyberduck.core.i18n.Locale;
import ch.cyberduck.core.ssl.SSLSession;
import ch.cyberduck.core.threading.AbstractBackgroundAction;
import ch.cyberduck.core.threading.BackgroundAction;
import ch.cyberduck.core.threading.BackgroundActionRegistry;
import ch.cyberduck.service.BrowserControllerService;
import ch.cyberduck.threading.BrowserBackgroundAction;
import ch.cyberduck.threading.WindowMainAction;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author  gurkerl
 */
public class BrowserController extends WindowController implements
		BrowserControllerService {

	private static Logger log = Logger.getLogger(BrowserController.class);

	public BrowserController() {
		this.loadBundle();
		setBrowserListView();
	}

	/**
	 * @uml.property  name="workdir"
	 * @uml.associationEnd  
	 */
	private Path workdir;

	/**
	 * Keeps a ordered backward history of previously visited paths
	 * @uml.property  name="backHistory"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="ch.cyberduck.core.Path"
	 */
	private List<Path> backHistory = new Collection<Path>();

	/**
	 * Keeps a ordered forward history of previously visited paths
	 * @uml.property  name="forwardHistory"
	 */
	private List<Path> forwardHistory = new Collection<Path>();

	/**
	 * @param p
	 */
	public void addPathToHistory(final Path p) {
		if (backHistory.size() > 0) {
			// Do not add if this was a reload
			if (p.equals(backHistory.get(backHistory.size() - 1))) {
				return;
			}
		}
		backHistory.add(p);
	}

	/**
	 * Returns the prevously browsed path and moves it to the forward history
	 * 
	 * @return The previously browsed path or null if there is none
	 */
	public Path getPreviousPath() {
		int size = backHistory.size();
		if (size > 1) {
			forwardHistory.add(backHistory.get(size - 1));
			Path p = backHistory.get(size - 2);
			// delete the fetched path - otherwise we produce a loop
			backHistory.remove(size - 1);
			backHistory.remove(size - 2);
			return p;
		} else if (1 == size) {
			forwardHistory.add(backHistory.get(size - 1));
			return backHistory.get(size - 1);
		}
		return null;
	}

	/**
	 * @return The last path browsed before #getPrevoiusPath was called
	 * @see #getPreviousPath()
	 */
	public Path getForwardPath() {
		int size = forwardHistory.size();
		if (size > 0) {
			Path p = forwardHistory.get(size - 1);
			forwardHistory.remove(size - 1);
			return p;
		}
		return null;
	}

	/**
	 * @return  The ordered array of prevoiusly visited directories
	 * @uml.property  name="backHistory"
	 */
	public List<Path> getBackHistory() {
		return backHistory;
	}

	/**
	 * Remove all entries from the back path history
	 */
	public void clearBackHistory() {
		backHistory.clear();
	}

	/**
	 * @return  The ordered array of prevoiusly visited directories
	 * @uml.property  name="forwardHistory"
	 */
	public List<Path> getForwardHistory() {
		return forwardHistory;
	}

	/**
	 * Remove all entries from the forward path history
	 */
	public void clearForwardHistory() {
		forwardHistory.clear();
	}

	/**
	 * @uml.property  name="listener"
	 * @uml.associationEnd  
	 */
	private ConnectionListener listener = null;

	/**
	 * Initializes a session for the passed host. Setting up the listeners and
	 * adding any callback controllers needed for login, trust management and
	 * hostkey verification.
	 * 
	 * @param host
	 * @return A session object bound to this browser controller
	 */
	private Session init(final Host host) {
		// host.setCredentials("kirk23", "123456");
		if (this.hasSession()) {
			this.session.removeConnectionListener(listener);
		}
		this.session = SessionFactory.createSession(host);
		// if(this.session instanceof ch.cyberduck.core.sftp.SFTPSession) {
		// ((ch.cyberduck.core.sftp.SFTPSession)
		// session).setHostKeyVerificationController(
		// new HostKeyController(this));
		// }
		this.session.setLoginController(new LoginController(this));
		this.setWorkdir(null);
		this.setEncoding(this.session.getEncoding());
		this.session.addProgressListener(new ProgressListener() {
			public void message(final String message) {
				invoke(new WindowMainAction(BrowserController.this) {
					public void run() {
						// updateStatusLabel(message);
					}
				});
			}
		});
		session.addConnectionListener(listener = new ConnectionAdapter() {
			@Override
			public void connectionWillOpen() {
				invoke(new WindowMainAction(BrowserController.this) {
					public void run() {
						// bookmarkTable.setNeedsDisplay();
						// window.setTitle(host.getNickname());
						// window.setRepresentedFilename("");
					}
				});
			}

			@Override
			public void connectionDidOpen() {
				invoke(new WindowMainAction(BrowserController.this) {
					public void run() {
						getSelectedBrowserView().setNeedsDisplay();
						// bookmarkTable.setNeedsDisplay();

						// Growl.instance().notify("Connection opened",
						// host.getHostname());

						// final HistoryCollection history =
						// HistoryCollection.defaultCollection();
						// history.add(host);

						// Set the window title
						// window.setRepresentedFilename(history.getFile(host).getAbsolute());

						if (Preferences.instance().getBoolean(
								"browser.confirmDisconnect")) {
							// window.setDocumentEdited(true);
						}
						// securityLabel.setImage(session.isSecure() ?
						// IconCache.iconNamed("locked.tiff")
						// : IconCache.iconNamed("unlocked.tiff"));
						// securityLabel.setEnabled(session instanceof
						// SSLSession);
					}
				});
			}

			@Override
			public void connectionDidClose() {
				invoke(new WindowMainAction(BrowserController.this) {
					public void run() {
						getSelectedBrowserView().setNeedsDisplay();
						// bookmarkTable.setNeedsDisplay();

						if (!isMounted()) {
							// window.setTitle(Preferences.instance().getProperty("application"));
							// window.setRepresentedFilename("");
						}
						// window.setDocumentEdited(false);

						// securityLabel.setImage(IconCache.iconNamed("unlocked.tiff"));
						// securityLabel.setEnabled(false);

						// updateStatusLabel();
					}
				});
			}
		});
		// transcript.clear();
		backHistory.clear();
		forwardHistory.clear();
		// session.addTranscriptListener(new TranscriptListener() {
		// public void log(final boolean request, final String message) {
		// if(logDrawer.state() == NSDrawer.OpenState) {
		// invoke(new WindowMainAction(BrowserController.this) {
		// public void run() {
		// transcript.log(request, message);
		// }
		// });
		// }
		// }
		// });
		return session;
	}

	/**
	 * Hide files beginning with '.'
	 * @uml.property  name="showHiddenFiles"
	 */
	private boolean showHiddenFiles;

	/**
	 * @uml.property  name="filenameFilter"
	 * @uml.associationEnd  
	 */
	private PathFilter<Path> filenameFilter;

	{
		// if(Preferences.instance().getBoolean("browser.showHidden")) {
		// this.filenameFilter = new NullPathFilter<Path>();
		// this.showHiddenFiles = true;
		// }
		// else {
		this.filenameFilter = new HiddenFilesPathFilter<Path>();
		this.showHiddenFiles = false;
		// }
	}

	protected PathFilter<Path> getFileFilter() {
		return this.filenameFilter;
	}

	protected void setPathFilter(final String searchString) {
		log.debug("setPathFilter:" + searchString);
		if (StringUtils.isBlank(searchString)) {
			// this.searchField.setStringValue("");
			// Revert to the last used default filter
			if (this.getShowHiddenFiles()) {
				this.filenameFilter = new NullPathFilter<Path>();
			} else {
				this.filenameFilter = new HiddenFilesPathFilter<Path>();
			}
		} else {
			// Setting up a custom filter for the directory listing
			this.filenameFilter = new PathFilter<Path>() {
				public boolean accept(Path file) {
					if (file.getName().toLowerCase().indexOf(
							searchString.toLowerCase()) != -1) {
						// Matching filename
						return true;
					}
					// if(file.attributes.isDirectory() &&
					// getSelectedBrowserView() == browserOutlineView) {
					// // #471. Expanded item childs may match search string
					// return file.isCached();
					// }
					return false;
				}
			};
		}
		this.reloadData(true);
	}

	/**
	 * @param showHidden
	 * @uml.property  name="showHiddenFiles"
	 */
	public void setShowHiddenFiles(boolean showHidden) {
		if (showHidden) {
			this.filenameFilter = new NullPathFilter<Path>();
			this.showHiddenFiles = true;
		} else {
			this.filenameFilter = new HiddenFilesPathFilter<Path>();
			this.showHiddenFiles = false;
		}
	}

	/**
	 * @return
	 * @uml.property  name="showHiddenFiles"
	 */
	public boolean getShowHiddenFiles() {
		return this.showHiddenFiles;
	}

	/**
	 * Marks the current browser as the first responder
	 */
	private void getFocus() {
		// if(this.getSelectedTabView() == TAB_BOOKMARKS) {
		// if(this.isMounted()) {
		// int row =
		// this.bookmarkModel.getSource().indexOf(this.getSession().getHost());
		// if(row != -1) {
		// this.bookmarkTable.selectRowIndexes(NSIndexSet.indexSetWithIndex(new
		// NSInteger(row)), false);
		// this.bookmarkTable.scrollRowToVisible(new NSInteger(row));
		// }
		// }
		// this.updateStatusLabel(this.bookmarkTable.numberOfRows() + " " +
		// Locale.localizedString("Bookmarks"));
		// this.window().makeFirstResponder(bookmarkTable);
		// }
		// else {
		// if(this.isMounted()) {
		// this.window().makeFirstResponder(this.getSelectedBrowserView());
		// }
		// else {
		// this.window().makeFirstResponder(this.quickConnectPopup);
		// }
		// this.updateStatusLabel();
		// }
	}

	/**
	 * @param preserveSelection
	 *            All selected files should be reselected after reloading the
	 *            view
	 * @pre Must always be invoked from the main interface thread
	 */
	public void reloadData(boolean preserveSelection) {

		if (preserveSelection) {
			// Remember the previously selected paths
			this.reloadData(this.getSelectedPaths());
		} else {
			this.reloadData(Collections.<Path> emptyList());
		}
	}

	/**
	 * Make the broser reload its content. Will make use of the cache.
	 * 
	 * @param selected
	 *            The items to be selected
	 * @see #setSelectedPaths(java.util.List)
	 */
	protected void reloadData(final List<Path> selected) {
		log.debug("reloadData");
		// Tell the browser view to reload the data. This will request all paths
		// from the browser model
		// which will refetch paths from the server marked as invalid.
//		final NSTableView browser = this.getSelectedBrowserView();
		final BrowserListViewModel browser = this.getSelectedBrowserView();
//		browser.reloadData();
		this.setSelectedPaths(selected);
		// this.updateStatusLabel();
	}
	
	/**
     * @param path
     * @param expand Expand the existing selection
     */
    private void selectRow(Path path, boolean expand) {
        log.debug("selectRow:" + path);
//        final NSTableView browser = this.getSelectedBrowserView();
        final BrowserListViewModel browser = this.getSelectedBrowserView();
        if(this.getSelectedBrowserModel().contains(path)) { //browser, 
            this.selectRow(this.getSelectedBrowserModel().indexOf(path), expand); //browser, 
        }
        
        // do not update FE Browser already updated in reloadData with browser.reloadData();
    }

    /**
     * @param row
     * @param expand Expand the existing selection
     */
    private void selectRow(int row, boolean expand) {
        log.debug("selectRow:" + row);
        if(-1 == row) {
            return;
        }
//        final NSTableView browser = this.getSelectedBrowserView();
        final BrowserListViewModel browser = this.getSelectedBrowserView();
//        final NSInteger index = new NSInteger(row);
//        browser.selectRowIndexes(NSIndexSet.indexSetWithIndex(index), expand);
//        browser.scrollRowToVisible(index);
        
        //update FE browser row, do not change browser
        //programmable change row which is affected
    }

	
	/**
	 * @param selected
	 */
	protected void setSelectedPath(Path selected) {
		List<Path> list = new Collection<Path>();
		list.add(selected);
		this.setSelectedPaths(list);
	}

	/**
	 * @param selected
	 */
	protected void setSelectedPaths(List<Path> selected) {
		log.debug("setSelectedPaths");
		this.deselectAll();
		if (!selected.isEmpty()) {
//			 switch(browserSwitchView.selectedSegment()) {
//			 case SWITCH_LIST_VIEW: {
			 //selection handling
			 for(Path path : selected) {
				 this.selectRow(path, true);
			 }
//			 break;
//			 }
//			 case SWITCH_OUTLINE_VIEW: {
//			 for(Path path : selected) {
//			 final int row =
//			 browserOutlineView.rowForItem(path.<NSObject>getReference().unique()).intValue();
//			 this.selectRow(row, true);
//			 }
//			 break;
//			 }
//			 }
		}
	}

	/**
	 * @return The first selected path found or null if there is no selection
	 */
	public Path getSelectedPath() {
		List<Path> selected = this.getSelectedPaths();
		if (selected.size() > 0) {
			return selected.get(0);
		}
		return null;
	}

	/**
	 * @return All selected paths or an empty list if there is no selection
	 */
	protected Collection<Path> getSelectedPaths() {
		Collection<Path> selectedFiles = new Collection<Path>();
		if (this.isMounted()) {
			// NSIndexSet iterator =
			// this.getSelectedBrowserView().selectedRowIndexes();
			// for(NSUInteger index = iterator.firstIndex();
			// !index.equals(NSIndexSet.NSNotFound); index =
			// iterator.indexGreaterThanIndex(index)) {
			// Path selected = this.pathAtRow(index.intValue());
			// if(null == selected) {
			// break;
			// }
			// selectedFiles.add(selected);
			// }
		}
		return selectedFiles;
	}

	protected int getSelectionCount() {
		// return
		// this.getSelectedBrowserView().numberOfSelectedRows().intValue();
		return 0;
	}

	private void deselectAll() {
		log.debug("deselectAll");
		// final NSTableView browser = this.getSelectedBrowserView();
		// if(null == browser) {
		// return;
		// }
		// browser.deselectAll(null);
	}

	/**
	 * @uml.property  name="session"
	 * @uml.associationEnd  
	 */
	private Session session;

	/**
	 * @param host
	 * @return The session to be used for any further operations
	 */
	public void mount(final Host host) {
		log.debug("mount:" + host);
		this.unmount(new Runnable() {
			public void run() {
				// The browser has no session, we are allowed to proceed
				// Initialize the browser with the new session attaching all
				// listeners
				final Session session = init(host);

				background(new BrowserBackgroundAction(BrowserController.this) {
					private Path mount;

					public void run() {
						// Mount this session
						mount = session.mount();
					}

					@Override
					public void cleanup() {
						// Set the working directory
						setWorkdir(mount);
						if (!session.isConnected()) {
							// Connection attempt failed
							log.warn("Mount failed:" + host);
							unmountImpl();
						}
					}

					@Override
					public String getActivity() {
						return MessageFormat.format(Locale.localizedString(
								"Mounting {0}", "Status"), host.getHostname());
						// return host.getHostname();
					}
				});
			}
		});
	}

	public boolean unmount() {
		return this.unmount(new Runnable() {
			public void run() {
				;
			}
		});
	}

	/**
	 * @param disconnected
	 *            Callback after the session has been disconnected
	 * @return True if the unmount process has finished, false if the user has
	 *         to agree first to close the connection
	 */
	public boolean unmount(final Runnable disconnected) {
		return this.unmount(new SheetCallback() {
			public void callback(int returncode) {
				if (returncode == DEFAULT_OPTION) {
					unmountImpl(disconnected);
				}
			}
		}, disconnected);
	}

	/**
	 * @param callback
	 * @param disconnected
	 * @return
	 */
	public boolean unmount(final SheetCallback callback,
			final Runnable disconnected) {
		log.debug("unmount");
		if (this.isConnected() || this.isActivityRunning()) {
			if (Preferences.instance().getBoolean("browser.confirmDisconnect")) {
				// Defer the unmount to the callback function
				// final NSAlert alert =
				// NSAlert.alert(Locale.localizedString("Disconnect from") + " "
				// + this.session.getHost().getHostname(), //title
				// Locale.localizedString("The connection will be closed."), //
				// message
				// Locale.localizedString("Disconnect"), // defaultbutton
				// Locale.localizedString("Cancel"), // alternate button
				// null //other button
				// );
				// this.alert(alert, callback);
				// No unmount yet
				return false;
			}
			this.unmountImpl(disconnected);
			// Unmount in progress
			return true;
		}
		disconnected.run();
		// Unmount succeeded
		return true;
	}

	/**
	 * @param disconnected
	 */
	private void unmountImpl(final Runnable disconnected) {
		if (this.isActivityRunning()) {
			this.interrupt();
		}
		final Session session = this.getSession();
		this.background(new AbstractBackgroundAction() {
			public void run() {
				unmountImpl();
			}

			@Override
			public void cleanup() {
				// inspector = null;

				// Clear the cache on the main thread to make sure the browser
				// model is not in an invalid state
				session.cache().clear();
				session.getHost().getCredentials().setPassword(null);

				disconnected.run();
			}

			@Override
			public String getActivity() {
				// return
				// MessageFormat.format(Locale.localizedString("Disconnecting {0}",
				// "Status"),
				// session.getHost().getHostname());
				return session.getHost().getHostname();
			}
		});
	}

	/**
	 * Will close the session but still display the current working directory
	 * without any confirmation from the user
	 */
	private void unmountImpl() {
		// This is not synchronized to the <code>mountingLock</code>
		// intentionally; this allows to unmount
		// sessions not yet connected
		if (this.hasSession()) {
			// Close the connection gracefully
			this.session.close();
		}
	}

	/**
	 * Interrupt any operation in progress; just closes the socket without any
	 * quit message sent to the server
	 */
	private void interrupt() {
		if (this.hasSession()) {
			if (this.isActivityRunning()) {
				final BackgroundAction current = BackgroundActionRegistry
						.instance().getCurrent();
				if (null != current) {
					current.cancel();
				}
			}
			this.background(new BrowserBackgroundAction(this) {
				public void run() {
					if (hasSession()) {
						// Aggressively close the connection to interrupt the
						// current task
						session.interrupt();
					}
				}

				@Override
				public void cleanup() {
					;
				}

				@Override
				public String getActivity() {
					return MessageFormat.format(Locale.localizedString(
							"Disconnecting {0}", "Status"), session.getHost()
							.getHostname());
				}

				@Override
				public int retry() {
					return 0;
				}

				private final Object lock = new Object();

				@Override
				public Object lock() {
					// No synchronization with other tasks
					return lock;
				}
			});
		}
	}

	/**
	 * Unmount this session
	 */
	private void disconnect() {
		this.background(new BrowserBackgroundAction(this) {
			public void run() {
				unmountImpl();
			}

			@Override
			public void cleanup() {
				if (Preferences.instance().getBoolean(
						"browser.disconnect.showBookmarks")) {
					// BrowserController.this.toggleBookmarks(true);
				}
			}

			@Override
			public String getActivity() {
				return MessageFormat.format(Locale.localizedString(
						"Disconnecting {0}", "Status"), session.getHost()
						.getHostname());
			}
		});
	}

	/**
	 * @return true if a connection is being opened or is already initialized
	 */
	public boolean hasSession() {
		return this.session != null;
	}

	/**
	 * @return  This browser's session or null if not mounted
	 * @uml.property  name="session"
	 */
	public Session getSession() {
		return this.session;
	}

	/**
	 * @return true if the remote file system has been mounted
	 */
	public boolean isMounted() {
		return this.hasSession() && this.workdir() != null;
	}

	/**
	 * @return true if mounted and the connection to the server is alive
	 */
	public boolean isConnected() {
		if (this.isMounted()) {
			return this.session.isConnected();
		}
		return false;
	}

	/**
	 * @return true if there is any network activity running in the background
	 */
	public boolean isActivityRunning() {
		final BackgroundAction current = BackgroundActionRegistry.instance()
				.getCurrent();
		if (null == current) {
			return false;
		}
		if (current instanceof BrowserBackgroundAction) {
			return ((BrowserBackgroundAction) current).getController() == this;
		}
		return false;
	}

	/**
	 * @param path
	 * @return Null if not mounted or lookup fails
	 */
	// public Path lookup(OutlinePathReference path) {
	// if(this.isMounted()) {
	// return this.getSession().cache().lookup(path);
	// }
	// return null;
	// }

	/**
	 * Accessor to the working directory
	 * 
	 * @return The current working directory or null if no file system is
	 *         mounted
	 */
	public Path workdir() {
		return this.workdir;
	}

	/**
	 * @param directory
	 * @uml.property  name="workdir"
	 */
	public void setWorkdir(final Path directory) {
		this.setWorkdir(directory, Collections.<Path> emptyList());
	}

	public void setWorkdir(final Path directory, Path selected) {
		this.setWorkdir(directory, Collections.singletonList(selected));
	}

	/**
	 * Sets the current working directory. This will udpate the path selection
	 * dropdown button and also add this path to the browsing history. If the
	 * path cannot be a working directory (e.g. permission issues trying to
	 * enter the directory), reloading the browser view is canceled and the
	 * working directory not changed.
	 * 
	 * @param directory
	 *            The new working directory to display or null to detach any
	 *            working directory from the browser
	 */
	public void setWorkdir(final Path directory, final List<Path> selected) {
		log.debug("setWorkdir:" + directory);
		if (null == directory) {
			// Clear the browser view if no working directory is given
			this.workdir = null;
//			this.validateNavigationButtons();
			this.reloadData(false);
			return;
		}
		// final NSTableView browser = this.getSelectedBrowserView();
		// window.endEditingFor(browser);
		this.background(new BrowserBackgroundAction(this) {
			@Override
			public String getActivity() {
				return MessageFormat
						.format(Locale.localizedString("Listing directory {0}",
								"Status"), directory.getName());
			}

			public void run() {
				if (directory.isCached()) {
					// Reset the readable attribute
					directory.childs().attributes().setReadable(true);
				}
				// Get the directory listing in the background
				directory.childs();
				if (directory.childs().attributes().isReadable()) {
					// Update the working directory if listing is successful
					workdir = directory;
					// Update the current working directory
					addPathToHistory(workdir());
				}
			}

			@Override
			public void cleanup() {
				// // Remove any custom file filter
				setPathFilter(null);
				//
				// // Change to last selected browser view
				// browserSwitchClicked(Preferences.instance().getInteger("browser.view"));
				//
//				validateNavigationButtons();
				//
				// Mark the browser data source as dirty
				reloadData(selected);
			}
		});
	}

	public void encodingChanged(final String encoding) {
		if (null == encoding) {
			return;
		}
		this.setEncoding(encoding);
		if (this.isMounted()) {
			if (this.session.getEncoding().equals(encoding)) {
				return;
			}
			this.background(new BrowserBackgroundAction(this) {
				public void run() {
					unmountImpl();
				}

				@Override
				public void cleanup() {
					session.getHost().setEncoding(encoding);
					// reloadButtonClicked(null);
				}

				@Override
				public String getActivity() {
					return MessageFormat.format(Locale.localizedString(
							"Disconnecting {0}", "Status"), session.getHost()
							.getHostname());
				}
			});
		}
	}

	/**
	 * @param encoding
	 */
	private void setEncoding(final String encoding) {
		// this.encodingPopup.selectItemWithTitle(encoding);
	}

	/**
	 * @param source
	 *            The original file to duplicate
	 * @param destination
	 *            The destination of the duplicated file
	 * @param edit
	 *            Open the duplicated file in the external editor
	 */
	protected void duplicatePath(final Path source, final Path destination,
			boolean edit) {
		this
				.duplicatePaths(Collections.singletonMap(source, destination),
						edit);
	}

	/**
	 * @param selected
	 *            A map with the original files as the key and the destination
	 *            files as the value
	 * @param edit
	 *            Open the duplicated files in the external editor
	 */
	protected void duplicatePaths(final Map<Path, Path> selected,
			final boolean edit) {
		final Map<Path, Path> normalized = this.checkHierarchy(selected);
		this.checkOverwrite(normalized.values(), new BrowserBackgroundAction(
				this) {
			public void run() {
				Iterator<Path> sourcesIter = normalized.keySet().iterator();
				Iterator<Path> destinationsIter = normalized.values()
						.iterator();
				while (sourcesIter.hasNext()) {
					if (this.isCanceled()) {
						break;
					}
					final Path source = sourcesIter.next();
					final Path destination = destinationsIter.next();
					source.copy(destination);
					source.getParent().invalidate();
					destination.getParent().invalidate();
					if (!isConnected()) {
						break;
					}
				}
			}

			@Override
			public void cleanup() {
				for (Path duplicate : normalized.values()) {
					if (edit) {
						// Editor editor =
						// EditorFactory.createEditor(BrowserController.this,
						// duplicate);
						// editor.open();
					}
					if (duplicate.getName().charAt(0) == '.') {
						setShowHiddenFiles(true);
					}
				}
				reloadData(new ArrayList<Path>(normalized.values()));
			}

			@Override
			public String getActivity() {
				return MessageFormat.format(Locale.localizedString(
						"Copying {0} to {1}", "Status"), normalized.keySet()
						.iterator().next().getName(), normalized.values()
						.iterator().next().getName());
			}
		});
	}

	/**
	 * @param path
	 *            The existing file
	 * @param renamed
	 *            The renamed file
	 */
	protected void renamePath(final Path path, final Path renamed) {
		this.renamePaths(Collections.singletonMap(path, renamed));
	}

	/**
	 * @param selected
	 *            A map with the original files as the key and the destination
	 *            files as the value
	 */
	protected void renamePaths(final Map<Path, Path> selected) {
		final Map<Path, Path> normalized = this.checkHierarchy(selected);
		this.checkMove(normalized.values(), new BrowserBackgroundAction(this) {
			public void run() {
				Iterator<Path> originalIterator = normalized.keySet()
						.iterator();
				Iterator<Path> renamedIterator = normalized.values().iterator();
				while (originalIterator.hasNext()) {
					if (this.isCanceled()) {
						break;
					}
					final Path original = originalIterator.next();
					original.getParent().invalidate();
					final Path renamed = renamedIterator.next();
					original.rename(renamed);
					renamed.invalidate();
					renamed.getParent().invalidate();
					if (!isConnected()) {
						break;
					}
				}
			}

			@Override
			public void cleanup() {
				reloadData(new ArrayList<Path>(normalized.values()));
			}

			@Override
			public String getActivity() {
				return MessageFormat.format(Locale.localizedString(
						"Renaming {0} to {1}", "Status"), normalized.keySet()
						.iterator().next().getName(), normalized.values()
						.iterator().next().getName());
			}
		});
	}

	/**
	 * Displays a warning dialog about already existing files
	 * 
	 * @param selected
	 *            The files to check for existance
	 */
	private void checkOverwrite(final java.util.Collection<Path> selected,
			final BackgroundAction action) {
		if (selected.size() > 0) {
			StringBuilder alertText = new StringBuilder(
					Locale
							.localizedString("A file with the same name already exists. Do you want to replace the existing file?"));
			int i = 0;
			Iterator<Path> iter = null;
			boolean shouldWarn = false;
			for (iter = selected.iterator(); i < 10 && iter.hasNext();) {
				Path item = iter.next();
				if (item.exists()) {
					alertText.append("\n").append(Character.toString('\u2022'))
							.append(" ").append(item.getName());
					shouldWarn = true;
				}
				i++;
			}
			if (iter.hasNext()) {
				alertText.append("\n" + Character.toString('\u2022') + " ...)");
			}
			if (shouldWarn) {
				// NSAlert alert = NSAlert.alert(
				// Locale.localizedString("Overwrite"), //title
				// alertText.toString(),
				// Locale.localizedString("Overwrite"), // defaultbutton
				// Locale.localizedString("Cancel"), //alternative button
				// null //other button
				// );
				// this.alert(alert, new SheetCallback() {
				// public void callback(final int returncode) {
				// if(returncode == DEFAULT_OPTION) {
				// BrowserController.this.background(action);
				// }
				// }
				// });
			} else {
				this.background(action);
			}
		}
	}

	/**
	 * Displays a warning dialog about files to be moved
	 * 
	 * @param selected
	 *            The files to check for existance
	 */
	private void checkMove(final java.util.Collection<Path> selected,
			final BackgroundAction action) {
		if (selected.size() > 0) {
			if (Preferences.instance().getBoolean("browser.confirmMove")) {
				StringBuilder alertText = new StringBuilder(
						Locale
								.localizedString("Do you want to move the selected files?"));
				int i = 0;
				Iterator<Path> iter = null;
				for (iter = selected.iterator(); i < 10 && iter.hasNext();) {
					Path item = iter.next();
					alertText.append("\n" + Character.toString('\u2022') + " "
							+ item.getName());
					i++;
				}
				if (iter.hasNext()) {
					alertText.append("\n" + Character.toString('\u2022')
							+ " ...)");
				}
				// final NSAlert alert = NSAlert.alert(
				// Locale.localizedString("Move"), //title
				// alertText.toString(),
				// Locale.localizedString("Move"), // defaultbutton
				// Locale.localizedString("Cancel"), //alternative button
				// null //other button
				// );
				// this.alert(alert, new SheetCallback() {
				// public void callback(final int returncode) {
				// if(returncode == DEFAULT_OPTION) {
				// checkOverwrite(selected, action);
				// }
				// }
				// });
			} else {
				this.checkOverwrite(selected, action);
			}
		}
	}

	/**
	 * Prunes the map of selected files. Files which are a child of an already
	 * included directory are removed from the returned map.
	 */
	protected Map<Path, Path> checkHierarchy(final Map<Path, Path> selected) {
		final Map<Path, Path> normalized = new HashMap<Path, Path>();
		Iterator<Path> sourcesIter = selected.keySet().iterator();
		Iterator<Path> destinationsIter = selected.values().iterator();
		while (sourcesIter.hasNext()) {
			Path f = sourcesIter.next();
			Path r = destinationsIter.next();
			boolean duplicate = false;
			for (Iterator<Path> normalizedIter = normalized.keySet().iterator(); normalizedIter
					.hasNext();) {
				Path n = normalizedIter.next();
				if (f.isChild(n)) {
					// The selected file is a child of a directory
					// already included for deletion
					duplicate = true;
					break;
				}
				if (n.isChild(f)) {
					// Remove the previously added file as it is a child
					// of the currently evaluated file
					normalizedIter.remove();
				}
			}
			if (!duplicate) {
				normalized.put(f, r);
			}
		}
		return normalized;
	}

	/**
	 * Prunes the list of selected files. Files which are a child of an already
	 * included directory are removed from the returned list.
	 */
	protected List<Path> checkHierarchy(final List<Path> selected) {
		final List<Path> normalized = new Collection<Path>();
		for (Path f : selected) {
			boolean duplicate = false;
			for (Path n : normalized) {
				if (f.isChild(n)) {
					// The selected file is a child of a directory
					// already included for deletion
					duplicate = true;
					break;
				}
			}
			if (!duplicate) {
				normalized.add(f);
			}
		}
		return normalized;
	}

	/**
	 * Recursively deletes the file
	 * 
	 * @param file
	 */
	public void deletePath(final Path file) {
		this.deletePaths(Collections.singletonList(file));
	}

	/**
	 * Recursively deletes the files
	 * 
	 * @param selected
	 *            The files selected in the browser to delete
	 */
	public void deletePaths(final List<Path> selected) {
		final List<Path> normalized = this.checkHierarchy(selected);
		if (normalized.isEmpty()) {
			return;
		}
		StringBuilder alertText = new StringBuilder(
				Locale
						.localizedString("Really delete the following files? This cannot be undone."));
		int i = 0;
		Iterator<Path> iter = null;
		for (iter = normalized.iterator(); i < 10 && iter.hasNext();) {
			alertText.append("\n").append(Character.toString('\u2022')).append(
					" ").append(iter.next().getName());
			i++;
		}
		if (iter.hasNext()) {
			alertText.append("\n").append(Character.toString('\u2022')).append(
					" " + "(...)");
		}
		// NSAlert alert = NSAlert.alert(Locale.localizedString("Delete"),
		// //title
		// alertText.toString(),
		// Locale.localizedString("Delete"), // defaultbutton
		// Locale.localizedString("Cancel"), //alternative button
		// null //other button
		// );
		// this.alert(alert, new SheetCallback() {
		// public void callback(final int returncode) {
		// if(returncode == DEFAULT_OPTION) {
		// BrowserController.this.deletePathsImpl(normalized);
		// }
		// }
		// });
	}

	private void deletePathsImpl(final List<Path> files) {
		this.background(new BrowserBackgroundAction(this) {
			public void run() {
				for (Path file : files) {
					if (this.isCanceled()) {
						break;
					}
					file.delete();
					file.getParent().invalidate();
					if (!isConnected()) {
						break;
					}
				}
			}

			@Override
			public String getActivity() {
				return MessageFormat.format(Locale.localizedString(
						"Deleting {0}", "Status"), "");
			}

			@Override
			public void cleanup() {
				reloadData(false);
			}
		});
	}

	/**
	 * @param selected
	 * @return True if the selected path is editable (not a directory and no
	 *         known binary file)
	 */
	private boolean isEditable(final Path selected) {
		if (selected.attributes.isFile()) {
			if (Preferences.instance().getBoolean("editor.kqueue.enable")) {
				return true;
			}
			return !selected.getBinaryFiletypePattern().matcher(
					selected.getName()).matches();
		}
		return false;
	}

	/**
	 * @return The currently selected browser view (which is either an
	 *         outlineview or a plain tableview)
	 */
	// public NSTableView getSelectedBrowserView() {
	public BrowserListViewModel getSelectedBrowserView() {
		// switch(this.browserSwitchView.selectedSegment()) {
		// case SWITCH_LIST_VIEW: {
		// return this.browserListView;
		// }
		// case SWITCH_OUTLINE_VIEW: {
		// return this.browserOutlineView;
		// }
		// }
		// log.fatal("No selected brower view");
		// return null;
		return this.browserListModel;
	}

	/**
	 * @return The datasource of the currently selected browser view
	 */
	public BrowserTableDataSource getSelectedBrowserModel() {
		// switch(this.browserSwitchView.selectedSegment()) {
		// case SWITCH_LIST_VIEW: {
		return this.browserListModel;
		// }
		// case SWITCH_OUTLINE_VIEW: {
		// return this.browserOutlineModel;
		// }
		// }
		//
		// log.fatal("No selected brower view");
		// return null;
	}

	public AbstractBrowserTableDelegate<Path> getSelectedBrowserDelegate() {
//        switch(this.browserSwitchView.selectedSegment()) {
//            case SWITCH_LIST_VIEW: {
                return this.browserListViewDelegate;
//            }
//            case SWITCH_OUTLINE_VIEW: {
//         getComparator       return this.browserOutlineViewDelegate;
//            }
//        }
//        log.fatal("No selected brower view");
//        return null;
    }

	protected Comparator<Path> getComparator() {
		return this.getSelectedBrowserDelegate().getSortingComparator();
		// return null;
	}

	private abstract class AbstractBrowserOutlineViewDelegate<E> extends
			AbstractBrowserTableDelegate<E> { //implements NSOutlineView.Delegate {

//		public String outlineView_toolTipForCell_rect_tableColumn_item_mouseLocation(
//				NSOutlineView outlineView, NSCell cell, ID rect,
//				NSTableColumn tc, NSObject item, NSPoint mouseLocation) {
//			return this.tooltip(lookup(new OutlinePathReference(item)));
//		}
	}

	private abstract class AbstractBrowserListViewDelegate<E> extends
			AbstractBrowserTableDelegate<E> { //implements NSTableView.Delegate {

//		public String tableView_toolTipForCell_rect_tableColumn_row_mouseLocation(
//				NSTableView aTableView, NSCell aCell, ID rect,
//				NSTableColumn aTableColumn, NSInteger row, NSPoint mouseLocation) {
//			return this.tooltip(browserListModel.childs(workdir()).get(
//					row.intValue()));
//		}
	}

	private abstract class AbstractBrowserTableDelegate<E> extends
			AbstractPathTableDelegate {

		public AbstractBrowserTableDelegate() {
			BrowserController.this.addListener(new WindowListener() {
				public void windowWillClose() {
//					if (QuickLookFactory.instance().isAvailable()) {
//						if (QuickLookFactory.instance().isOpen()) {
//							QuickLookFactory.instance().close();
//						}
//					}
				}
			});
		}

//		@Override
//		public boolean isColumnEditable(NSTableColumn column) {
//			if (Preferences.instance().getBoolean("browser.editable")) {
//				return column.identifier().equals(
//						BrowserTableDataSource.FILENAME_COLUMN);
//			}
//			return false;
//		}
//
//		@Override
//		public void tableRowDoubleClicked(final ID sender) {
//			BrowserController.this.insideButtonClicked(sender);
//		}
//
//		public void spaceKeyPressed(final ID sender) {
//			if (QuickLookFactory.instance().isAvailable()) {
//				if (QuickLookFactory.instance().isOpen()) {
//					QuickLookFactory.instance().close();
//				} else {
//					this.updateQuickLookSelection(BrowserController.this
//							.getSelectedPaths());
//				}
//			}
//		}

		/**
		 * @param selected
		 */
		private void updateQuickLookSelection(final Collection<Path> selected) {
//			if (QuickLookFactory.instance().isAvailable()) {
//				final Collection<Path> downloads = new Collection<Path>();
//				for (Path path : selected) {
//					if (!path.attributes.isFile()) {
//						continue;
//					}
//					final Local folder = LocalFactory.createLocal(new File(
//							Preferences.instance().getProperty("tmp.dir"), path
//									.getParent().getAbsolute()));
//					folder.mkdir(true);
//					path.setLocal(LocalFactory.createLocal(folder, path
//							.getName()));
//					downloads.add(path);
//				}
//				if (downloads.size() > 0) {
//					background(new BrowserBackgroundAction(
//							BrowserController.this) {
//						final Collection<Local> previews = new Collection<Local>() {
//							@Override
//							public void collectionItemRemoved(Local o) {
//								super.collectionItemRemoved(o);
//								(o).delete(false);
//							}
//						};
//
//						public void run() {
//							for (Path download : downloads) {
//								if (this.isCanceled()) {
//									break;
//								}
//								if (!download.getLocal().exists()) {
//									download.download(true);
//									if (download.getStatus().isComplete()) {
//										previews.add(download.getLocal());
//									} else {
//										download.getLocal().delete(false);
//									}
//								} else {
//									previews.add(download.getLocal());
//								}
//							}
//						}
//
//						@Override
//						public void cleanup() {
//							if (previews.isEmpty()) {
//								return;
//							}
//							if (this.isCanceled()) {
//								return;
//							}
//							// Change files in Quick Look
//							QuickLookFactory.instance().select(previews);
//							// Open Quick Look Preview Panel
//							QuickLookFactory.instance().open();
//							// Revert status label
//							BrowserController.this.updateStatusLabel();
//							// Restore the focus to our window to demo the
//							// selection changing, scrolling
//							// (left/right) and closing (space) functionality
//							BrowserController.this.window().makeKeyWindow();
//						}
//
//						@Override
//						public String getActivity() {
//							return Locale.localizedString("Quick Look",
//									"Status");
//						}
//					});
//				}
//			}
		}

//		public void deleteKeyPressed(final ID sender) {
//			BrowserController.this.deleteFileButtonClicked(sender);
//		}
//
//		@Override
//		public void tableColumnClicked(NSTableView view,
//				NSTableColumn tableColumn) {
//			List<Path> selected = BrowserController.this.getSelectedPaths();
//			if (this.selectedColumnIdentifier()
//					.equals(tableColumn.identifier())) {
//				this.setSortedAscending(!this.isSortedAscending());
//			} else {
//				// Remove sorting indicator on previously selected column
//				this.setBrowserColumnSortingIndicator(null, this
//						.selectedColumnIdentifier());
//				// Set the newly selected column
//				this.setSelectedColumn(tableColumn);
//			}
//			this.setBrowserColumnSortingIndicator(
//					this.isSortedAscending() ? IconCache
//							.iconNamed("NSAscendingSortIndicator") : IconCache
//							.iconNamed("NSDescendingSortIndicator"),
//					tableColumn.identifier());
//			reloadData(selected);
//		}
//
//		@Override
//		public void selectionDidChange(NSNotification notification) {
//			final Collection<Path> selected = getSelectedPaths();
//			if (Preferences.instance().getBoolean("browser.info.isInspector")) {
//				if (inspector != null && inspector.isVisible()) {
//					if (selected.size() > 0) {
//						background(new BrowserBackgroundAction(
//								BrowserController.this) {
//							public void run() {
//								for (Path p : selected) {
//									if (this.isCanceled()) {
//										break;
//									}
//									if (p.attributes.getPermission() == null) {
//										p.readPermission();
//									}
//								}
//							}
//
//							@Override
//							public void cleanup() {
//								if (inspector != null) {
//									inspector.setFiles(selected);
//								}
//							}
//						});
//					}
//				}
//			}
//			if (QuickLookFactory.instance().isOpen()) {
//				this.updateQuickLookSelection(selected);
//			}
//		}
//
//		private void setBrowserColumnSortingIndicator(NSImage image,
//				String columnIdentifier) {
//			if (browserListView.tableColumnWithIdentifier(columnIdentifier) != null) {
//				browserListView.setIndicatorImage_inTableColumn(image,
//						browserListView
//								.tableColumnWithIdentifier(columnIdentifier));
//			}
//			if (browserOutlineView.tableColumnWithIdentifier(columnIdentifier) != null) {
//				browserOutlineView.setIndicatorImage_inTableColumn(image,
//						browserOutlineView
//								.tableColumnWithIdentifier(columnIdentifier));
//			}
//		}
	}

	@Override
	protected String getBundleName() {
		return "Browser";
	}

	/**
	 * @uml.property  name="browserListModel"
	 * @uml.associationEnd  
	 */
	private BrowserListViewModel browserListModel;
	// @Outlet
	// private NSTableView browserListView;
	/**
	 * @uml.property  name="browserListViewDelegate"
	 * @uml.associationEnd  
	 */
	private AbstractBrowserTableDelegate<Path> browserListViewDelegate;

	public void setBrowserListView() { // NSTableView view) {
		browserListModel = new BrowserListViewModel(this);
//		browserListViewDelegate = new AbstractBrowserListViewDelegate<Path>() {
//
//			@Override
//			protected boolean isTypeSelectSupported() {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//			@Override
//			public void deleteKeyPressed(ch.cyberduck.ID sender) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void enterKeyPressed(ch.cyberduck.ID sender) {
//				// TODO Auto-generated method stub
//				
//			}
//		}
	}
//	public void enterKeyPressed(final ID sender) {
//                if(Preferences.instance().getBoolean("browser.enterkey.rename")) {
//                    if(browserListView.numberOfSelectedRows().intValue() == 1) {
//                        browserListView.editRow(
//                                browserListView.columnWithIdentifier(BrowserTableDataSource.FILENAME_COLUMN),
//                                browserListView.selectedRow(), true);
//                    }
//                }
//                else {
//                    this.tableRowDoubleClicked(sender);
//                }
//            }
//
//            public void tableView_willDisplayCell_forTableColumn_row(NSTableView view, NSTextFieldCell cell, NSTableColumn tableColumn, NSInteger row) {
//                final String identifier = tableColumn.identifier();
//                if(identifier.equals(BrowserTableDataSource.FILENAME_COLUMN)) {
//                    final Path item = browserListModel.childs(BrowserController.this.workdir()).get(row.intValue());
//                    cell.setEditable(item.isRenameSupported());
//                }
//                if(cell.isKindOfClass(Foundation.getClass(NSTextFieldCell.class.getSimpleName()))) {
//                    if(!BrowserController.this.isConnected()) {// || CDBrowserController.this.activityRunning) {
//                        cell.setTextColor(NSColor.disabledControlTextColor());
//                    }
//                    else {
//                        cell.setTextColor(NSColor.controlTextColor());
//                    }
//                }
//            }
//
//            @Override
//            protected boolean isTypeSelectSupported() {
//                return true;
//            }
//        }).id());
//        {
//            NSTableColumn c = browserListColumnsFactory.create(BrowserTableDataSource.ICON_COLUMN);
//            c.headerCell().setStringValue("");
//            c.setMinWidth((20));
//            c.setWidth((20));
//            c.setMaxWidth((20));
//            c.setResizingMask(NSTableColumn.NSTableColumnAutoresizingMask);
//            c.setDataCell(imageCellPrototype);
//            c.dataCell().setAlignment(NSText.NSCenterTextAlignment);
//            browserListView.addTableColumn(c);
//        }
//        {
//            NSTableColumn c = browserListColumnsFactory.create(BrowserTableDataSource.FILENAME_COLUMN);
//            c.headerCell().setStringValue(Locale.localizedString("Filename"));
//            c.setMinWidth((100));
//            c.setWidth((250));
//            c.setMaxWidth((1000));
//            c.setResizingMask(NSTableColumn.NSTableColumnAutoresizingMask | NSTableColumn.NSTableColumnUserResizingMask);
//            c.setDataCell(filenameCellPrototype);
//            this.browserListView.addTableColumn(c);
//        }
//	}

	// @Action
	// public void bookmarkSwitchClicked(final ID sender) {
	// this.toggleBookmarks(this.getSelectedTabView() != TAB_BOOKMARKS);
	// }
	//
	// @Action
	// public void browserSwitchButtonClicked(final NSSegmentedControl sender) {
	// this.browserSwitchClicked(sender.selectedSegment());
	// }
	//    
	// @Action
	// public void browserSwitchMenuClicked(final NSMenuItem sender) {
	// this.browserSwitchView.setSelectedSegment(sender.tag());
	// this.browserSwitchClicked(sender.tag());
	// }
	//    
	// @Action
	// public void quickConnectSelectionChanged(final NSControl sender) {
	// String input = (sender).stringValue();
	// if(StringUtils.isBlank(input)) {
	// return;
	// }
	// input = input.trim();
	// // First look for equivalent bookmarks
	// for(Host h : (Iterable<Host>) HostCollection.defaultCollection()) {
	// if(h.getNickname().equals(input)) {
	// this.mount(h);
	// return;
	// }
	// }
	// // Try to parse the input as a URL and extract protocol, hostname,
	// username and password if any.
	// this.mount(Host.parse(input));
	// }
	//    
	// /**
	// * Change focus to filter field
	// *
	// * @param sender
	// */
	// @Action
	// public void searchButtonClicked(final ID sender) {
	// this.window().makeFirstResponder(searchField);
	// }
	//    
	// @Action
	// public void connectBookmarkButtonClicked(final ID sender) {
	// if(bookmarkTable.numberOfSelectedRows().intValue() == 1) {
	// final Host selected =
	// bookmarkModel.getSource().get(bookmarkTable.selectedRow().intValue());
	// this.mount(selected);
	// }
	// }
	//    
	// @Action
	// public void editBookmarkButtonClicked(final ID sender) {
	// BookmarkController c = BookmarkController.Factory.create(
	// bookmarkModel.getSource().get(bookmarkTable.selectedRow().intValue())
	// );
	// c.window().makeKeyAndOrderFront(null);
	// }
	//    
	// @Action
	// public void addBookmarkButtonClicked(final ID sender) {
	// final Host item;
	// if(this.isMounted()) {
	// Path selected = this.getSelectedPath();
	// if(null == selected || !selected.attributes.isDirectory()) {
	// selected = this.workdir();
	// }
	// item = new Host(this.session.getHost().getAsDictionary());
	// item.setDefaultPath(selected.getAbsolute());
	// }
	// else {
	// item = new
	// Host(Protocol.forName(Preferences.instance().getProperty("connection.protocol.default")),
	// Preferences.instance().getProperty("connection.hostname.default"),
	// Preferences.instance().getInteger("connection.port.default"));
	// }
	//
	// this.toggleBookmarks(true);
	//
	// bookmarkModel.setFilter(null);
	// bookmarkModel.getSource().add(item);
	// final int row = bookmarkModel.getSource().lastIndexOf(item);
	// final NSInteger index = new NSInteger(row);
	// bookmarkTable.selectRowIndexes(NSIndexSet.indexSetWithIndex(index),
	// false);
	// bookmarkTable.scrollRowToVisible(index);
	// BookmarkController c = BookmarkController.Factory.create(item);
	// c.window().makeKeyAndOrderFront(null);
	// }
	//    
	//    
	// @Action
	// public void deleteBookmarkButtonClicked(final ID sender) {
	// final NSIndexSet iterator = bookmarkTable.selectedRowIndexes();
	// NSUInteger[] indexes = new NSUInteger[iterator.count().intValue()];
	// int i = 0;
	// for(NSUInteger index = iterator.firstIndex();
	// !index.equals(NSIndexSet.NSNotFound); index =
	// iterator.indexGreaterThanIndex(index)) {
	// indexes[i] = index;
	// i++;
	// }
	// bookmarkTable.deselectAll(null);
	// int j = 0;
	// for(i = 0; i < indexes.length; i++) {
	// int row = indexes[i].intValue() - j;
	// final NSInteger index = new NSInteger(row);
	// bookmarkTable.selectRowIndexes(NSIndexSet.indexSetWithIndex(index),
	// false);
	// bookmarkTable.scrollRowToVisible(index);
	// if(bookmarkModel.getSource().allowsEdit()) {
	// Host host = bookmarkModel.getSource().get(row);
	// final NSAlert alert =
	// NSAlert.alert(Locale.localizedString("Delete Bookmark"),
	// Locale.localizedString("Do you want to delete the selected bookmark?")
	// + " (" + host.getNickname() + ")",
	// Locale.localizedString("Delete"),
	// Locale.localizedString("Cancel"),
	// null);
	// switch(alert.runModal()) {
	// case SheetCallback.ALTERNATE_OPTION:
	// continue;
	// }
	// }
	// bookmarkModel.getSource().remove(row);
	// j++;
	// }
	// bookmarkTable.deselectAll(null);
	// }
	//    
	//    
	// // ----------------------------------------------------------
	// // Browser navigation
	// // ----------------------------------------------------------
	//
	//    
	// @Action
	// public void navigationButtonClicked(NSSegmentedControl sender) {
	// switch(sender.selectedSegment()) {
	// case NAVIGATION_LEFT_SEGMENT_BUTTON: {
	// this.backButtonClicked(sender);
	// break;
	// }
	// case NAVIGATION_RIGHT_SEGMENT_BUTTON: {
	// this.forwardButtonClicked(sender);
	// break;
	// }
	// }
	// }
	//
	// @Action
	// public void backButtonClicked(final NSSegmentedControl sender) {
	// final Path selected = this.getPreviousPath();
	// if(selected != null) {
	// final Path previous = this.workdir();
	// if(previous.getParent().equals(selected)) {
	// this.setWorkdir(selected, previous);
	// }
	// else {
	// this.setWorkdir(selected);
	// }
	// }
	// }
	//
	// @Action
	// public void forwardButtonClicked(final NSSegmentedControl sender) {
	// final Path selected = this.getForwardPath();
	// if(selected != null) {
	// this.setWorkdir(selected);
	// }
	// }
	//
	// @Action
	// public void pathPopupSelectionChanged(final NSPopUpButton sender) {
	// final String selected = sender.selectedItem().representedObject();
	// final Path previous = this.workdir();
	// if(selected != null) {
	// final Path path = PathFactory.createPath(session, selected,
	// Path.DIRECTORY_TYPE);
	// this.setWorkdir(path);
	// if(previous.getParent().equals(path)) {
	// this.setWorkdir(path, previous);
	// }
	// else {
	// this.setWorkdir(path);
	// }
	// }
	// }
	//    
	//    
	// @Action
	// public void encodingButtonClicked(final NSPopUpButton sender) {
	// this.encodingChanged(sender.titleOfSelectedItem());
	// }
	//
	// @Action
	// public void encodingMenuClicked(final NSMenuItem sender) {
	// this.encodingChanged(sender.title());
	// }
	//    
	// // ----------------------------------------------------------
	// // Drawers
	// // ----------------------------------------------------------
	//
	// @Action
	// public void toggleLogDrawer(final ID sender) {
	// this.logDrawer.toggle(this.id());
	// }
	//    
	//    
	// @Action
	// public void securityLabelClicked(final ID sender) {
	// if(session instanceof SSLSession) {
	// final X509Certificate[] certificates = ((SSLSession)
	// this.session).getTrustManager().getAcceptedIssuers();
	// if(0 == certificates.length) {
	// log.warn("No accepted certificates found");
	// return;
	// }
	// KeychainFactory.instance().displayCertificates(certificates);
	// }
	// }
	//    
	//    
	// /**
	// * Marks all expanded directories as invalid and tells the
	// * browser table to reload its data
	// *
	// * @param sender
	// */
	// @Action
	// public void reloadButtonClicked(final ID sender) {
	// if(this.isMounted()) {
	// final Collection<Path> selected = this.getSelectedPaths();
	// switch(this.browserSwitchView.selectedSegment()) {
	// case SWITCH_LIST_VIEW: {
	// this.workdir().invalidate();
	// break;
	// }
	// case SWITCH_OUTLINE_VIEW: {
	// this.workdir().invalidate();
	// for(int i = 0; i < browserOutlineView.numberOfRows().intValue(); i++) {
	// final Path item = this.lookup(new
	// OutlinePathReference(browserOutlineView.itemAtRow(new NSInteger(i))));
	// if(null == item) {
	// continue;
	// }
	// item.invalidate();
	// }
	// break;
	// }
	// }
	// this.reloadData(selected);
	// }
	// }

	// /**
	// * Open a new browser with the current selected folder as the working
	// directory
	// *
	// * @param sender
	// */
	// @Action
	// public void newBrowserButtonClicked(final ID sender) {
	// Path selected = this.getSelectedPath();
	// if(null == selected || !selected.attributes.isDirectory()) {
	// selected = this.workdir();
	// }
	// BrowserController c = new BrowserController();
	// c.cascade();
	// c.window().makeKeyAndOrderFront(null);
	// final Host host = new
	// Host(this.getSession().getHost().<NSDictionary>getAsDictionary());
	// host.setDefaultPath(selected.getAbsolute());
	// c.mount(host);
	// }
	//    
	//    
	// @Action
	// public void gotoButtonClicked(final ID sender) {
	// SheetController sheet = new GotoController(this);
	// sheet.beginSheet();
	// }
	//
	// @Action
	// public void createFileButtonClicked(final ID sender) {
	// SheetController sheet = new CreateFileController(this);
	// sheet.beginSheet();
	// }
	//
	// @Action
	// public void duplicateFileButtonClicked(final ID sender) {
	// SheetController sheet = new DuplicateFileController(this);
	// sheet.beginSheet();
	// }
	//
	// @Action
	// public void createFolderButtonClicked(final ID sender) {
	// SheetController sheet = new FolderController(this);
	// sheet.beginSheet();
	// }
	//
	// @Action
	// public void renameFileButtonClicked(final ID sender) {
	// final NSTableView browser = this.getSelectedBrowserView();
	// browser.editRow(
	// browser.columnWithIdentifier(BrowserTableDataSource.FILENAME_COLUMN),
	// browser.selectedRow(), true);
	// }
	//
	// @Action
	// public void sendCustomCommandClicked(final ID sender) {
	// SheetController sheet = new CommandController(this, this.session);
	// sheet.beginSheet();
	// }
	//
	// @Action
	// public void editMenuClicked(final NSMenuItem sender) {
	// for(Path selected : this.getSelectedPaths()) {
	// Editor editor = EditorFactory.createEditor(this,
	// sender.representedObject(), selected);
	// editor.open();
	// }
	// }
	//
	// @Action
	// public void editButtonClicked(final ID sender) {
	// for(Path selected : this.getSelectedPaths()) {
	// Editor editor = EditorFactory.createEditor(this, selected);
	// editor.open();
	// }
	// }
	//
	// @Action
	// public void openBrowserButtonClicked(final ID sender) {
	// NSWorkspace.sharedWorkspace().openURL(NSURL.URLWithString(this.getSelectedPathWebUrl()));
	// }
	//
	//    
	// @Action
	// public void infoButtonClicked(final ID sender) {
	// if(this.getSelectionCount() > 0) {
	// final List<Path> selected = this.getSelectedPaths();
	// if(Preferences.instance().getBoolean("browser.info.isInspector")) {
	// if(null == inspector || null == inspector.window()) {
	// inspector = InfoController.Factory.create(BrowserController.this,
	// selected);
	// }
	// else {
	// inspector.setFiles(selected);
	// }
	// inspector.window().makeKeyAndOrderFront(null);
	// }
	// else {
	// InfoController c = InfoController.Factory.create(BrowserController.this,
	// selected);
	// c.window().makeKeyAndOrderFront(null);
	// }
	// }
	// }
	//
	// @Action
	// public void deleteFileButtonClicked(final ID sender) {
	// this.deletePaths(this.getSelectedPaths());
	// }
	//    
	//
	// @Action
	// public void downloadToButtonClicked(final ID sender) {
	// downloadToPanel = NSOpenPanel.openPanel();
	// downloadToPanel.setCanChooseDirectories(true);
	// downloadToPanel.setCanCreateDirectories(true);
	// downloadToPanel.setCanChooseFiles(false);
	// downloadToPanel.setAllowsMultipleSelection(false);
	// downloadToPanel.setPrompt(Locale.localizedString("Choose"));
	// downloadToPanel.beginSheetForDirectory(
	// lastSelectedDownloadDirectory, //trying to be smart
	// null, this.window, this.id(),
	// Foundation.selector("downloadToPanelDidEnd:returnCode:contextInfo:"),
	// null);
	// }
	//    
	// @Action
	// public void downloadAsButtonClicked(final ID sender) {
	// downloadAsPanel = NSSavePanel.savePanel();
	// downloadAsPanel.setMessage(Locale.localizedString("Download the selected file to..."));
	// downloadAsPanel.setNameFieldLabel(Locale.localizedString("Download As:"));
	// downloadAsPanel.setPrompt(Locale.localizedString("Download"));
	// downloadAsPanel.setCanCreateDirectories(true);
	// downloadAsPanel.beginSheetForDirectory(null,
	// this.getSelectedPath().getLocal().getDisplayName(), this.window,
	// this.id(),
	// Foundation.selector("downloadAsPanelDidEnd:returnCode:contextInfo:"),
	// null);
	// }
	//
	//    
	// @Action
	// public void syncButtonClicked(final ID sender) {
	// final Path selection;
	// if(this.getSelectionCount() == 1 &&
	// this.getSelectedPath().attributes.isDirectory()) {
	// selection = this.getSelectedPath();
	// }
	// else {
	// selection = this.workdir();
	// }
	// syncPanel = NSOpenPanel.openPanel();
	// syncPanel.setCanChooseDirectories(selection.attributes.isDirectory());
	// syncPanel.setCanChooseFiles(selection.attributes.isFile());
	// syncPanel.setCanCreateDirectories(true);
	// syncPanel.setAllowsMultipleSelection(false);
	// syncPanel.setMessage(Locale.localizedString("Synchronize")
	// + " " + selection.getName() + " "
	// + Locale.localizedString("with"));
	// syncPanel.setPrompt(Locale.localizedString("Choose"));
	// syncPanel.beginSheetForDirectory(null, null, this.window, this.id(),
	// Foundation.selector("syncPanelDidEnd:returnCode:contextInfo:"), null
	// //context info
	// );
	// }
	//    
	//    
	// @Action
	// public void downloadButtonClicked(final ID sender) {
	// final Session session = this.getTransferSession();
	// final List<Path> roots = new Collection<Path>();
	// for(Path selected : this.getSelectedPaths()) {
	// Path path = PathFactory.createPath(session, selected.getAsDictionary());
	// path.setLocal(null);
	// roots.add(path);
	// }
	// final Transfer q = new DownloadTransfer(roots);
	// this.transfer(q);
	// }
	//    
	//    
	// @Action
	// public void uploadButtonClicked(final ID sender) {
	// uploadPanel = NSOpenPanel.openPanel();
	// uploadPanel.setCanChooseDirectories(true);
	// uploadPanel.setCanCreateDirectories(false);
	// uploadPanel.setCanChooseFiles(true);
	// uploadPanel.setAllowsMultipleSelection(true);
	// uploadPanel.setPrompt(Locale.localizedString("Upload"));
	// uploadPanel.beginSheetForDirectory(lastSelectedUploadDirectory, //trying
	// to be smart
	// null, this.window,
	// this.id(),
	// Foundation.selector("uploadPanelDidEnd:returnCode:contextInfo:"),
	// null);
	// }
	//    
	//    
	// @Action
	// public void insideButtonClicked(final ID sender) {
	// final Path selected = this.getSelectedPath(); //last row selected
	// if(null == selected) {
	// return;
	// }
	// if(selected.attributes.isDirectory()) {
	// this.setWorkdir(selected);
	// }
	// else if(selected.attributes.isFile() || this.getSelectionCount() > 1) {
	// if(Preferences.instance().getBoolean("browser.doubleclick.edit")) {
	// this.editButtonClicked(null);
	// }
	// else {
	// this.downloadButtonClicked(null);
	// }
	// }
	// }

	// @Action
	public void connectButtonClicked() { // final ID sender) {
		final SheetController controller = ConnectionController.instance(this);
		this.addListener(new WindowListener() {
			public void windowWillClose() {
				controller.invalidate();
			}
		});
		controller.beginSheet();
	}

	// @Action
	public void interruptButtonClicked() { // final ID sender) {
		// Remove all pending actions
		for (BackgroundAction action : BackgroundActionRegistry.instance()
				.toArray(
						new BackgroundAction[BackgroundActionRegistry
								.instance().size()])) {
			action.cancel();
		}
		// Interrupt any pending operation by forcefully closing the socket
		this.interrupt();
	}

	// @Action
	public void disconnectButtonClicked() { // final ID sender) {
		if (this.isActivityRunning()) {
			this.interruptButtonClicked(); // sender);
		} else {
			this.disconnect();
		}
	}

	// @Action
	// public void showHiddenFilesClicked(final NSMenuItem sender) {
	// if(sender.state() == NSCell.NSOnState) {
	// this.setShowHiddenFiles(false);
	// sender.setState(NSCell.NSOffState);
	// }
	// else if(sender.state() == NSCell.NSOffState) {
	// this.setShowHiddenFiles(true);
	// sender.setState(NSCell.NSOnState);
	// }
	// if(this.isMounted()) {
	// this.reloadData(true);
	// }
	// }
	//    
	// @Action
	// public void paste(final ID sender) {
	// final PathPasteboard<NSDictionary> pasteboard =
	// PathPasteboard.getPasteboard(this.getSession().getHost());
	// if(pasteboard.isEmpty()) {
	// return;
	// }
	// final Map<Path, Path> files = new HashMap<Path, Path>();
	// Path parent = this.workdir();
	// if(this.getSelectionCount() == 1) {
	// Path selected = this.getSelectedPath();
	// if(selected.attributes.isDirectory()) {
	// parent = selected;
	// }
	// else {
	// parent = selected.getParent();
	// }
	// }
	// for(final Path next : pasteboard.getFiles(this.getSession())) {
	// Path current = PathFactory.createPath(getSession(),
	// next.getAbsolute(), next.attributes.getType());
	// Path renamed = PathFactory.createPath(getSession(),
	// parent.getAbsolute(), current.getName(), next.attributes.getType());
	// files.put(current, renamed);
	// }
	// pasteboard.clear();
	// this.renamePaths(files);
	// }
	//
	// @Action
	// public void pasteFromFinder(final ID sender) {
	// NSPasteboard pboard = NSPasteboard.generalPasteboard();
	// if(pboard.availableTypeFromArray(NSArray.arrayWithObject(NSPasteboard.FilenamesPboardType))
	// != null) {
	// NSObject o =
	// pboard.propertyListForType(NSPasteboard.FilenamesPboardType);
	// if(o != null) {
	// final NSArray elements = Rococoa.cast(o, NSArray.class);
	// final Path workdir = this.workdir();
	// final Session session = this.getTransferSession();
	// final List<Path> roots = new Collection<Path>();
	// for(int i = 0; i < elements.count().intValue(); i++) {
	// Path p = PathFactory.createPath(session,
	// workdir.getAbsolute(),
	// LocalFactory.createLocal(elements.objectAtIndex(new
	// NSUInteger(i)).toString()));
	// roots.add(p);
	// }
	// final Transfer q = new UploadTransfer(roots);
	// if(q.numberOfRoots() > 0) {
	// this.transfer(q, workdir);
	// }
	// }
	// }
	// }
	//
	// @Action
	// public void copyURLButtonClicked(final ID sender) {
	// final StringBuilder url = new StringBuilder();
	// if(this.getSelectionCount() > 0) {
	// for(Iterator<Path> iter = this.getSelectedPaths().iterator();
	// iter.hasNext();) {
	// url.append(iter.next().toURL());
	// if(iter.hasNext()) {
	// url.append("\n");
	// }
	// }
	// }
	// else {
	// url.append(this.workdir().toURL());
	// }
	// NSPasteboard pboard = NSPasteboard.generalPasteboard();
	// pboard.declareTypes(NSArray.arrayWithObject(NSString.stringWithString(NSPasteboard.StringPboardType)),
	// null);
	// if(!pboard.setStringForType(url.toString(),
	// NSPasteboard.StringPboardType)) {
	// log.error("Error writing URL to NSPasteboard.StringPboardType.");
	// }
	// }
	//
	// @Action
	// public void copyWebURLButtonClicked(final ID sender) {
	// NSPasteboard pboard = NSPasteboard.generalPasteboard();
	// pboard.declareTypes(NSArray.arrayWithObject(NSPasteboard.StringPboardType),
	// null);
	// if(!pboard.setString_forType(this.getSelectedPathWebUrl(),
	// NSPasteboard.StringPboardType)) {
	// log.error("Error writing URL to NSPasteboard.StringPboardType.");
	// }
	// }
	//
	// @Action
	// public void openTerminalButtonClicked(final ID sender) {
	// final boolean identity =
	// this.getSession().getHost().getCredentials().isPublicKeyAuthentication();
	// String workdir = null;
	// if(this.getSelectionCount() == 1) {
	// Path selected = this.getSelectedPath();
	// if(selected.attributes.isDirectory()) {
	// workdir = selected.getAbsolute();
	// }
	// }
	// if(null == workdir) {
	// workdir = this.workdir().getAbsolute();
	// }
	// final String command
	// = "tell application \"Terminal\"\n"
	// + "do script \"ssh -t "
	// + (identity ? "-i " +
	// this.getSession().getHost().getCredentials().getIdentity().getAbsolute()
	// : "")
	// + " "
	// + this.getSession().getHost().getCredentials().getUsername()
	// + "@"
	// + this.getSession().getHost().getHostname()
	// + " "
	// + "-p " + this.getSession().getHost().getPort()
	// + " "
	// + "\\\"cd " + workdir + " && exec \\\\$SHELL\\\"\""
	// + "\n"
	// + "end tell";
	// final NSAppleScript as = NSAppleScript.createWithSource(command);
	// as.executeAndReturnError(new PointerByReference());
	// final String terminal =
	// NSWorkspace.sharedWorkspace().absolutePathForAppBundleWithIdentifier("com.apple.Terminal");
	// if(StringUtils.isEmpty(terminal)) {
	// log.error("Terminal.app not installed");
	// return;
	// }
	// NSWorkspace.sharedWorkspace().launchApplication(terminal);
	// }
	//
	// @Action
	// public void archiveMenuClicked(final NSMenuItem sender) {
	// final Archive archive = Archive.forName(sender.representedObject());
	// this.archiveClicked(archive);
	// }
	//
	// @Action
	// public void archiveButtonClicked(final NSToolbarItem sender) {
	// this.archiveClicked(Archive.TARGZ);
	// }
	//
	//    
	// @Action
	// public void unarchiveButtonClicked(final ID sender) {
	// final List<Path> expanded = new ArrayList<Path>();
	// for(final Path selected : this.getSelectedPaths()) {
	// final Archive archive = Archive.forName(selected.getName());
	// if(null == archive) {
	// continue;
	// }
	// this.checkOverwrite(archive.getExpanded(Collections.singletonList(selected)),
	// new BrowserBackgroundAction(this) {
	// public void run() {
	// session.unarchive(archive, selected);
	// }
	//
	// @Override
	// public void cleanup() {
	// expanded.addAll(archive.getExpanded(Collections.singletonList(selected)));
	// // Update Selection
	// reloadData(expanded);
	// }
	//
	// @Override
	// public String getActivity() {
	// return archive.getDecompressCommand(selected);
	// }
	// });
	// }
	// ;
	// }
	//    
	// @Action
	// public void printDocument(final ID sender) {
	// NSPrintInfo print = NSPrintInfo.sharedPrintInfo();
	// print.setOrientation(NSPrintInfo.NSPrintingOrientation.NSLandscapeOrientation);
	// NSPrintOperation op =
	// NSPrintOperation.printOperationWithView_printInfo(this.getSelectedBrowserView(),
	// print);
	// op.setShowsPrintPanel(true);
	// final NSPrintPanel panel = op.printPanel();
	// panel.setOptions(panel.options() |
	// NSPrintPanel.NSPrintPanelShowsOrientation
	// | NSPrintPanel.NSPrintPanelShowsPaperSize |
	// NSPrintPanel.NSPrintPanelShowsScaling);
	// op.runOperationModalForWindow_delegate_didRunSelector_contextInfo(this.window(),
	// this.id(),
	// Foundation.selector("printOperationDidRun:success:contextInfo:"), null);
	// }

	/**
	 * Overrriden to remove any listeners from the session
	 */
	@Override
	protected void invalidate() {
		if (this.hasSession()) {
			this.session.removeConnectionListener(this.listener);
		}

		// bookmarkTable.setDelegate(null);
		// bookmarkTable.setDataSource(null);
		// bookmarkModel.invalidate();
		//
		// browserListView.setDelegate(null);
		// browserListView.setDataSource(null);
		browserListModel.invalidate();

		// browserOutlineView.setDelegate(null);
		// browserOutlineView.setDataSource(null);
		// browserOutlineModel.invalidate();
		//
		// toolbar.setDelegate(null);
		// toolbarItems.clear();
		//
		// browserListColumnsFactory.clear();
		// browserOutlineColumnsFactory.clear();
		// bookmarkTableColumnFactory.clear();
		//
		// quickConnectPopup.setDelegate(null);
		// quickConnectPopup.setDataSource(null);
		//
		// archiveMenu.setDelegate(null);
		// editMenu.setDelegate(null);

		super.invalidate();
	}

}
