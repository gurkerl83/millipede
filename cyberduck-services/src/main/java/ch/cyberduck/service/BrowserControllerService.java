/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.cyberduck.service;

//import ch.cyberduck.BrowserTableDataSource;
import ch.cyberduck.SheetCallback;
import ch.cyberduck.core.Host;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.Session;
import java.util.List;

/**
 *
 * @author gurkerl
 */
public interface BrowserControllerService {

    /**
     * @param p
     */
    void addPathToHistory(final Path p);

    /**
     * Remove all entries from the back path history
     */
    void clearBackHistory();

    /**
     * Remove all entries from the forward path history
     */
    void clearForwardHistory();

    /**
     * Recursively deletes the file
     *
     * @param file
     */
    void deletePath(final Path file);

    /**
     * Recursively deletes the files
     *
     * @param selected The files selected in the browser to delete
     */
    void deletePaths(final List<Path> selected);

    void encodingChanged(final String encoding);

    /**
     * @return The ordered array of prevoiusly visited directories
     */
    List<Path> getBackHistory();

    /**
     * @return The ordered array of prevoiusly visited directories
     */
    List<Path> getForwardHistory();

    /**
     * @return The last path browsed before #getPrevoiusPath was called
     * @see #getPreviousPath()
     */
    Path getForwardPath();

    /**
     * Returns the prevously browsed path and moves it to the forward history
     *
     * @return The previously browsed path or null if there is none
     */
    Path getPreviousPath();

    /**
     * @return The datasource of the currently selected browser view
     */
//    BrowserTableDataSource getSelectedBrowserModel();

    /**
     * @return This browser's session or null if not mounted
     */
    Session getSession();

    boolean getShowHiddenFiles();

    /**
     * @return true if a connection is being opened or is already initialized
     */
    boolean hasSession();

    /**
     * @return true if there is any network activity running in the background
     */
    boolean isActivityRunning();

    /**
     * @return true if mounted and the connection to the server is alive
     */
    boolean isConnected();

    /**
     * @return true if the remote file system has been mounted
     */
    boolean isMounted();

    /**
     * @param host
     * @return The session to be used for any further operations
     */
    void mount(final Host host);

    /**
     * @param preserveSelection All selected files should be reselected after reloading the view
     * @pre Must always be invoked from the main interface thread
     */
    void reloadData(final boolean preserveSelection);

    void setShowHiddenFiles(boolean showHidden);

    void setWorkdir(final Path directory);

    void setWorkdir(final Path directory, Path selected);

    /**
     * Sets the current working directory. This will udpate the path selection dropdown button
     * and also add this path to the browsing history. If the path cannot be a working directory (e.g. permission
     * issues trying to enter the directory), reloading the browser view is canceled and the working directory
     * not changed.
     *
     * @param directory The new working directory to display or null to detach any working directory from the browser
     */
    void setWorkdir(final Path directory, final List<Path> selected);

    boolean unmount();

    /**
     * @param disconnected Callback after the session has been disconnected
     * @return True if the unmount process has finished, false if the user has to agree first
     * to close the connection
     */
    boolean unmount(final Runnable disconnected);

    /**
     * @param callback
     * @param disconnected
     * @return
     */
    boolean unmount(final SheetCallback callback, final Runnable disconnected);

}
