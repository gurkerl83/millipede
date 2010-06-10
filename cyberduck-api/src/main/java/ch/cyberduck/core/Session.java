package ch.cyberduck.core;

/*
 *  Copyright (c) 2005 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import ch.cyberduck.core.i18n.Locale;
import ch.cyberduck.core.threading.BackgroundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.*;

import ch.cyberduck.core.service.LoginController;
/**
 * @version $Id: Session.java 5830 2010-03-03 12:02:26Z dkocher $
 */
public abstract class Session {
    private static Logger log = Logger.getLogger(Session.class);

    /**
     * Encapsulating all the information of the remote host
     */
    protected Host host;

    /**
     *
     */
    protected Path workdir;

    protected Session(Host h) {
        this.host = h;
    }

    protected abstract <C> C getClient() throws ConnectionCanceledException;

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#getIdentification()
	 */
    public String getIdentification() {
        try {
            return this.host.getIp();
        }
        catch(UnknownHostException e) {
            return this.host.getHostname();
        }
    }

    private final String ua = Preferences.instance().getProperty("application") + "/"
            + Preferences.instance().getProperty("version")
            + " (" + System.getProperty("os.name") + "/" + System.getProperty("os.version") + ")"
            + " (" + System.getProperty("os.arch") + ")";

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#getUserAgent()
	 */
    public String getUserAgent() {
        return ua;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#check()
	 */
    public void check() throws IOException {
        try {
            try {
                if(!this.isConnected()) {
                    // If not connected anymore, reconnect the session
                    this.connect();
                }
                else {
                    // The session is still supposed to be connected
                    try {
                        // Send a 'no operation command' to make sure the session is alive
                        this.noop();
                    }
                    catch(IOException e) {
                        // Close the underlying socket first
                        this.interrupt();
                        // Try to reconnect once more
                        this.connect();
                    }
                }
            }
            catch(SocketException e) {
                if(e.getMessage().equals("Software caused connection abort")) {
                    // Do not report as failed if socket opening interrupted
                    log.warn("Supressed socket exception:" + e.getMessage());
                    throw new ConnectionCanceledException();
                }
                if(e.getMessage().equals("Socket closed")) {
                    // Do not report as failed if socket opening interrupted
                    log.warn("Supressed socket exception:" + e.getMessage());
                    throw new ConnectionCanceledException();
                }
                throw e;
            }
            catch(SSLHandshakeException e) {
                log.error("SSL Handshake failed: " + e.getMessage());
//                if(e.getCause() instanceof sun.security.validator.ValidatorException) {
//                    throw e;
//                }
                // Most probably caused by user dismissing ceritifcate. No trusted certificate found.
                throw new ConnectionCanceledException(e.getMessage());
            }
            host.setTimestamp(new Date());
        }
        catch(IOException e) {
            this.interrupt();
            this.error("Connection failed", e);
            throw e;
        }
    }

    /**
     * @return The timeout in milliseconds
     */
    protected int timeout() {
        return (int) Preferences.instance().getDouble("connection.timeout.seconds") * 1000;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#isSecure()
	 */
    public boolean isSecure() {
        if(this.isConnected()) {
            return this.host.getProtocol().isSecure();
        }
        return false;
    }

    /**
     * Opens the TCP connection to the server
     *
     * @throws IOException
     * @throws LoginCanceledException
     */
    protected abstract void connect() throws IOException, ConnectionCanceledException, LoginCanceledException;

    protected LoginController login;

    /**
     * Sets the callback to ask for login credentials
     *
     * @param loginController
     * @see #login
     */
    public void setLoginController(LoginController loginController) {
        this.login = loginController;
    }

    protected void login() throws IOException {
//        login.check(host);

        final Credentials credentials = host.getCredentials();
//        this.message(MessageFormat.format(Locale.localizedString("Authenticating as {0}", "Status"),
//                credentials.getUsername()));
        this.message(credentials.getUsername());
        this.login(credentials);

        if(!this.isConnected()) {
            throw new ConnectionCanceledException();
        }

//        login.success(host);
    }

    /**
     * Send the authentication credentials to the server. The connection must be opened first.
     *
     * @throws IOException
     * @throws LoginCanceledException
     * @see #connect
     */
    protected abstract void login(Credentials credentials) throws IOException;

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#mount()
	 */
    public Path mount() {
        try {
            if(StringUtils.isNotBlank(host.getDefaultPath())) {
                return this.mount(host.getDefaultPath());
            }
            return this.mount(null);
        }
        catch(IOException e) {
            this.interrupt();
        }
        return null;
    }

    /**
     * Connect to the remote host and mount the home directory
     *
     * @param directory
     * @return null if we fail, the mounted working directory if we succeed
     */
    protected Path mount(String directory) throws IOException {
//        this.message(MessageFormat.format(Locale.localizedString("Mounting {0}", "Status"),
//                host.getHostname()));
        this.message(host.getHostname());
        this.check();
        if(!this.isConnected()) {
            return null;
        }
        Path home;
        if(directory != null) {
            if(directory.startsWith(Path.DELIMITER) || directory.equals(this.workdir().getName())) {
                home = PathFactory.createPath(this, directory,
                        directory.equals(Path.DELIMITER) ? Path.VOLUME_TYPE | Path.DIRECTORY_TYPE : Path.DIRECTORY_TYPE);
            }
            else if(directory.startsWith(Path.HOME)) {
                // relative path to the home directory
                home = PathFactory.createPath(this,
                        this.workdir().getAbsolute(), directory.substring(1), Path.DIRECTORY_TYPE);
            }
            else {
                // relative path
                home = PathFactory.createPath(this,
                        this.workdir().getAbsolute(), directory, Path.DIRECTORY_TYPE);
            }
            if(!home.childs().attributes().isReadable()) {
                // the default path does not exist or is not readable due to permission issues
                home = this.workdir();
            }
        }
        else {
            home = this.workdir();
        }
        return home;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#close()
	 */
    public abstract void close();

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#getHost()
	 */
    public Host getHost() {
        return this.host;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#getEncoding()
	 */
    public String getEncoding() {
        if(null == this.host.getEncoding()) {
            return Preferences.instance().getProperty("browser.charset.encoding");
        }
        return this.host.getEncoding();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#getMaxConnections()
	 */
    public int getMaxConnections() {
        if(null == host.getMaxConnections()) {
            return Preferences.instance().getInteger("connection.host.max");
        }
        return host.getMaxConnections();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#workdir()
	 */
    public Path workdir() throws IOException {
        if(!this.isConnected()) {
            throw new ConnectionCanceledException();
        }
        if(null == workdir) {
            workdir = PathFactory.createPath(this, Path.DELIMITER, Path.VOLUME_TYPE | Path.DIRECTORY_TYPE);
        }
        return workdir;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#setWorkdir(ch.cyberduck.core.Path)
	 */
    public void setWorkdir(Path workdir) throws IOException {
        if(!this.isConnected()) {
            throw new ConnectionCanceledException();
        }
        this.workdir = workdir;
    }

    /**
     * Send a 'no operation' command
     *
     * @throws IOException
     */
    protected abstract void noop() throws IOException;

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#interrupt()
	 */
    public void interrupt() {
        this.close();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#isSendCommandSupported()
	 */
    public boolean isSendCommandSupported() {
        return false;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#sendCommand(java.lang.String)
	 */
    public abstract void sendCommand(String command) throws IOException;

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#isArchiveSupported()
	 */
    public boolean isArchiveSupported() {
        return false;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#archive(ch.cyberduck.core.Archive, java.util.List)
	 */
    public void archive(final Archive archive, final List<Path> files) {
        try {
            this.check();

            this.sendCommand(archive.getCompressCommand(files));

            // The directory listing is no more current
            for(Path file : files) {
                file.getParent().invalidate();
            }
        }
        catch(IOException e) {
            this.error("Cannot create archive", e);
        }
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#isUnarchiveSupported()
	 */
    public boolean isUnarchiveSupported() {
        return false;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#unarchive(ch.cyberduck.core.Archive, ch.cyberduck.core.Path)
	 */
    public void unarchive(final Archive archive, Path file) {
        try {
            this.check();

            this.sendCommand(archive.getDecompressCommand(file));

            // The directory listing is no more current
            file.getParent().invalidate();
        }
        catch(IOException e) {
            this.error("Cannot expand archive", e);
        }
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#isConnected()
	 */
    public boolean isConnected() {
        try {
            this.getClient();
        }
        catch(ConnectionCanceledException e) {
            return false;
        }
        return true;
    }

    private boolean opening;

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#isOpening()
	 */
    public boolean isOpening() {
        return opening;
    }

    private Set<ConnectionListener> connectionListeners
            = Collections.synchronizedSet(new HashSet<ConnectionListener>());

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#addConnectionListener(ch.cyberduck.core.ConnectionListener)
	 */
    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#removeConnectionListener(ch.cyberduck.core.ConnectionListener)
	 */
    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    /**
     * Notifies all connection listeners that an attempt is made to open this session
     *
     * @throws ResolveCanceledException      If the name resolution has been canceled by the user
     * @throws java.net.UnknownHostException If the name resolution failed
     * @see ConnectionListener
     */
    protected void fireConnectionWillOpenEvent() throws ResolveCanceledException, UnknownHostException {
        log.debug("connectionWillOpen");
        ConnectionListener[] l = connectionListeners.toArray(new ConnectionListener[connectionListeners.size()]);
        for(ConnectionListener listener : l) {
            listener.connectionWillOpen();
        }

        // Configuring proxy if any
//        ProxyFactory.instance().configure(host);

        Resolver resolver = new Resolver(this.host.getHostname(true));
//        this.message(MessageFormat.format(Locale.localizedString("Resolving {0}", "Status"),
//                host.getHostname()));
        this.message(host.getHostname());
        
        // Try to resolve the hostname first
        resolver.resolve();
        // The IP address could successfully be determined
    }

    /**
     * Starts the <code>KeepAliveTask</code> if <code>connection.keepalive</code> is true
     * Notifies all connection listeners that the connection has been opened successfully
     *
     * @see ConnectionListener
     */
    protected void fireConnectionDidOpenEvent() {
        log.debug("connectionDidOpen");

        for(ConnectionListener listener : connectionListeners.toArray(new ConnectionListener[connectionListeners.size()])) {
            listener.connectionDidOpen();
        }
    }

    /**
     * Notifes all connection listeners that a connection is about to be closed
     *
     * @see ConnectionListener
     */
    protected void fireConnectionWillCloseEvent() {
        log.debug("connectionWillClose");
//        this.message(MessageFormat.format(Locale.localizedString("Disconnecting {0}", "Status"),
//                this.getHost().getHostname()));

        for(ConnectionListener listener : connectionListeners.toArray(new ConnectionListener[connectionListeners.size()])) {
            listener.connectionWillClose();
        }
    }

    /**
     * Notifes all connection listeners that a connection has been closed
     *
     * @see ConnectionListener
     */
    protected void fireConnectionDidCloseEvent() {
        log.debug("connectionDidClose");

        this.workdir = null;

        for(ConnectionListener listener : connectionListeners.toArray(new ConnectionListener[connectionListeners.size()])) {
            listener.connectionDidClose();
        }
    }

    private Set<TranscriptListener> transcriptListeners
            = Collections.synchronizedSet(new HashSet<TranscriptListener>());

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#addTranscriptListener(ch.cyberduck.core.TranscriptListener)
	 */
    public void addTranscriptListener(TranscriptListener listener) {
        transcriptListeners.add(listener);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#removeTranscriptListener(ch.cyberduck.core.TranscriptListener)
	 */
    public void removeTranscriptListener(TranscriptListener listener) {
        transcriptListeners.remove(listener);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#log(boolean, java.lang.String)
	 */
    public void log(boolean request, final String message) {
        log.info(message);
        for(TranscriptListener listener : transcriptListeners) {
            listener.log(request, message);
        }
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#isDownloadResumable()
	 */
    public boolean isDownloadResumable() {
        return true;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#isUploadResumable()
	 */
    public boolean isUploadResumable() {
        return true;
    }

    private Set<ProgressListener> progressListeners
            = Collections.synchronizedSet(new HashSet<ProgressListener>());

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#addProgressListener(ch.cyberduck.core.ProgressListener)
	 */
    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#removeProgressListener(ch.cyberduck.core.ProgressListener)
	 */
    public void removeProgressListener(ProgressListener listener) {
        progressListeners.remove(listener);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#message(java.lang.String)
	 */
    public void message(final String message) {
        log.info(message);
        for(ProgressListener listener : progressListeners.toArray(new ProgressListener[progressListeners.size()])) {
            listener.message(message);
        }
    }

    private Set<ErrorListener> errorListeners
            = Collections.synchronizedSet(new HashSet<ErrorListener>());

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#addErrorListener(ch.cyberduck.core.ErrorListener)
	 */
    public void addErrorListener(ErrorListener listener) {
        errorListeners.add(listener);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#removeErrorListener(ch.cyberduck.core.ErrorListener)
	 */
    public void removeErrorListener(ErrorListener listener) {
        errorListeners.remove(listener);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#error(java.lang.String, java.lang.Throwable)
	 */
    public void error(String message, Throwable e) {
        this.error(null, message, e);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#error(ch.cyberduck.core.Path, java.lang.String, java.lang.Throwable)
	 */
    public void error(Path path, String message, Throwable e) {
        final BackgroundException failure = new BackgroundException(this, path, message, e);
        this.message(failure.getMessage());
        for(ErrorListener listener : errorListeners.toArray(new ErrorListener[errorListeners.size()])) {
            listener.error(failure);
        }
    }

    /**
     * Caching files listings of previously visited directories
     */
    private Cache<Path> cache;

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#cache()
	 */
    public Cache<Path> cache() {
        if(null == cache) {
            cache = new Cache<Path>() {
                @Override
                public String toString() {
                    return "Cache for " + Session.this.toString();
                }
            };
        }
        return this.cache;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#equals(java.lang.Object)
	 */
    @Override
    public boolean equals(Object other) {
        if(null == other) {
            return false;
        }
        if(other instanceof Session) {
            return this.getHost().getHostname().equals(((Session) other).getHost().getHostname())
                    && this.getHost().getProtocol().equals(((Session) other).getHost().getProtocol());
        }
        return false;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.SessionService#toString()
	 */
    public String toString() {
        return "Session " + host.toURL();
    }
}