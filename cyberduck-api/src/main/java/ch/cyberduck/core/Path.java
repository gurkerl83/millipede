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
import ch.cyberduck.core.io.service.BandwidthThrottleService;
import ch.cyberduck.core.io.IOResumeException;
import ch.cyberduck.core.io.ThrottledInputStream;
import ch.cyberduck.core.io.ThrottledOutputStream;
import ch.cyberduck.core.serializer.Deserializer;
import ch.cyberduck.core.serializer.DeserializerFactory;
import ch.cyberduck.core.serializer.Serializer;
import ch.cyberduck.core.serializer.SerializerFactory;
//import ch.cyberduck.ui.cocoa.model.OutlinePathReference;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @version $Id$
 */
public abstract class Path extends AbstractPath implements Serializable {
    private static Logger log = Logger.getLogger(Path.class);

    /**
     *
     */
    private PathReference reference;

    /**
     * The absolute remote path
     */
    private String path;

    /**
     * The local path to be used if file is copied
     */
    private Local local;

    private Status status;

    /**
     * A compiled representation of a regular expression.
     */
    private Pattern TEXT_FILETYPE_PATTERN = null;

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#getTextFiletypePattern()
	 */
    public Pattern getTextFiletypePattern() {
        final String regex = Preferences.instance().getProperty("filetype.text.regex");
        if(null == TEXT_FILETYPE_PATTERN ||
                !TEXT_FILETYPE_PATTERN.pattern().equals(regex)) {
            try {
                TEXT_FILETYPE_PATTERN = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            }
            catch(PatternSyntaxException e) {
                log.warn(e.getMessage());
            }
        }
        return TEXT_FILETYPE_PATTERN;
    }

    /**
     * A compiled representation of a regular expression.
     */
    private Pattern BINARY_FILETYPE_PATTERN;

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#getBinaryFiletypePattern()
	 */
    public Pattern getBinaryFiletypePattern() {
        final String regex = Preferences.instance().getProperty("filetype.binary.regex");
        if(null == BINARY_FILETYPE_PATTERN ||
                !BINARY_FILETYPE_PATTERN.pattern().equals(regex)) {
            try {
                BINARY_FILETYPE_PATTERN = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            }
            catch(PatternSyntaxException e) {
                log.warn(e.getMessage());
            }
        }
        return BINARY_FILETYPE_PATTERN;
    }

    protected <T> Path(T dict) {
        this.init(dict);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#init(T)
	 */
    public <T> void init(T serialized) {
        final Deserializer dict = DeserializerFactory.createDeserializer(serialized);
        this.init(dict);
    }

    protected void init(Deserializer dict) {
        String pathObj = dict.stringForKey("Remote");
        if(pathObj != null) {
            this.setPath(pathObj);
        }
        String localObj = dict.stringForKey("Local");
        if(localObj != null) {
            this.setLocal(LocalFactory.createLocal(localObj));
        }
        String symlinkObj = dict.stringForKey("Symlink");
        if(symlinkObj != null) {
            this.setSymlinkTarget(symlinkObj);
        }
        final Object attributesObj = dict.objectForKey("Attributes");
        if(attributesObj != null) {
            this.attributes = new PathAttributes(attributesObj);
        }
        if(dict.stringForKey("Complete") != null) {
            this.getStatus().setComplete(true);
        }
        if(dict.stringForKey("Skipped") != null) {
            this.getStatus().setSkipped(true);
        }
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#getAsDictionary()
	 */
    public <S> S getAsDictionary() {
        final Serializer dict = SerializerFactory.createSerializer();
        return (S)this.getAsDictionary(dict);
    }

    protected <S> S getAsDictionary(Serializer dict) {
        dict.setStringForKey(this.getAbsolute(), "Remote");
        if(local != null) {
            dict.setStringForKey(local.toString(), "Local");
        }
        if(StringUtils.isNotBlank(this.getSymlinkTarget())) {
            dict.setStringForKey(this.getSymlinkTarget(), "Symlink");
        }
        dict.setObjectForKey((PathAttributes) attributes, "Attributes");
        if(this.getStatus().isComplete()) {
            dict.setStringForKey(String.valueOf(true), "Complete");
        }
        if(this.getStatus().isSkipped()) {
            dict.setStringForKey(String.valueOf(true), "Skipped");
        }
        return dict.<S>getSerialized();
    }

    {
        attributes = new PathAttributes();
    }

    /**
     * A remote path where nothing is known about a local equivalent.
     *
     * @param parent the absolute directory
     * @param name   the file relative to param path
     */
    protected Path(String parent, String name, int type) {
        this.setPath(parent, name);
        this.attributes.setType(type);
    }

    /**
     * A remote path where nothing is known about a local equivalent.
     *
     * @param path The absolute path of the remote file
     */
    protected Path(String path, int type) {
        this.setPath(path);
        this.attributes.setType(type);
    }

    /**
     * Create a new path where you know the local file already exists
     * and the remote equivalent might be created later.
     * The remote filename will be extracted from the local file.
     *
     * @param parent The absolute path to the parent directory on the remote host
     * @param local  The associated local file
     */
    protected Path(String parent, final Local local) {
        this.setPath(parent, local);
        this.attributes.setType(
                local.attributes.isDirectory() ? Path.DIRECTORY_TYPE : Path.FILE_TYPE);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#setPath(java.lang.String, ch.cyberduck.core.Local)
	 */
    public void setPath(String parent, final Local file) {
        this.setPath(parent, file.getName());
        this.setLocal(file);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#setPath(java.lang.String)
	 */
    @Override
    public void setPath(String name) {
        this.path = Path.normalize(name);
        this.parent = null;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#setParent(ch.cyberduck.core.Path)
	 */
    public void setParent(Path parent) {
        this.parent = parent;
    }

    /**
     * Reference to the parent created lazily if needed
     */
    private Path parent;

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#getParent()
	 */
    @Override
    public Path getParent() {
        return this.getParent(true);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#getParent(boolean)
	 */
    public Path getParent(final boolean create) {
        if(null == parent) {
            if(create) {
                if(this.isRoot()) {
                    return this;
                }
                String parent = getParent(this.getAbsolute());
                try {
                    if(DELIMITER.equals(parent)) {
                        this.parent = PathFactory.createPath(this.getSession(), DELIMITER,
                                Path.VOLUME_TYPE | Path.DIRECTORY_TYPE);
                    }
                    else {
                        this.parent = PathFactory.createPath(this.getSession(), parent,
                                Path.DIRECTORY_TYPE);
                    }
                }
                catch(ConnectionCanceledException e) {
                    log.error(e.getMessage());
                    return null;
                }
            }
        }
        return this.parent;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#getStatus()
	 */
    public Status getStatus() {
        if(null == status) {
            status = new Status();
        }
        return status;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#getHost()
	 */
    public Host getHost() {
        try {
            return this.getSession().getHost();
        }
        catch(ConnectionCanceledException e) {
            this.error(e.getMessage(), e);
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#cache()
	 */
    @Override
    public Cache<Path> cache() {
        try {
            return this.getSession().cache();
        }
        catch(ConnectionCanceledException e) {
            log.error(e.getMessage());
        }
        return new Cache<Path>();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#writeOwner(java.lang.String, boolean)
	 */
    public void writeOwner(String owner, boolean recursive) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#writeGroup(java.lang.String, boolean)
	 */
    public void writeGroup(String group, boolean recursive) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#readChecksum()
	 */
    @Override
    public void readChecksum() {
        ;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#readSize()
	 */
    public abstract void readSize();

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#readTimestamp()
	 */
    public abstract void readTimestamp();

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#readPermission()
	 */
    public abstract void readPermission();

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#getName()
	 */
    @Override
    public String getName() {
        if(this.isRoot()) {
            return DELIMITER;
        }
        final String abs = this.getAbsolute();
        int index = abs.lastIndexOf(DELIMITER);
        return abs.substring(index + 1);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#getAbsolute()
	 */
    @Override
    public String getAbsolute() {
        return this.path;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#getReference()
	 */
    @Override
    public <T> PathReference<T> getReference() {
        if(null == reference) {
//            reference = new OutlinePathReference(this.getAbsolute());
//            reference = new PathReference() {
//                @Override
//                public Object unique() {
//                    return Path.this;
//                }
//            };
        }
        return reference;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#setLocal(ch.cyberduck.core.Local)
	 */
    public void setLocal(Local file) {
        if(null != file) {
            if(file.attributes.isSymbolicLink()) {
                if(null != file.getSymlinkTarget()) {
                    /**
                     * A canonical pathname is both absolute and unique.  The precise
                     * definition of canonical form is system-dependent.  This method first
                     * converts this pathname to absolute form if necessary, as if by invoking the
                     * {@link #getAbsolutePath} method, and then maps it to its unique form in a
                     * system-dependent way.  This typically involves removing redundant names
                     * such as <tt>"."</tt> and <tt>".."</tt> from the pathname, resolving
                     * symbolic links
                     */
                    this.local = LocalFactory.createLocal(file.getSymlinkTarget());
                    return;
                }
            }
        }
        this.local = file;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#getLocal()
	 */
    public Local getLocal() {
        if(null == this.local) {
            return getDefaultLocal();
        }
        return this.local;
    }

    private Local getDefaultLocal() {
        return LocalFactory.createLocal(this.getHost().getDownloadFolder(), this.getName());
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#kind()
	 */
    public String kind() {
        if(this.attributes.isSymbolicLink()) {
            if(this.attributes.isFile()) {
                return Locale.localizedString("Symbolic Link (File)");
            }
            if(this.attributes.isDirectory()) {
                return Locale.localizedString("Symbolic Link (Folder)");
            }
        }
        if(this.attributes.isFile()) {
            return this.getLocal().kind();
        }
        if(this.attributes.isDirectory()) {
            return Locale.localizedString("Folder");
        }
        return Locale.localizedString("Unknown");
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#getSession()
	 */
    public abstract Session getSession() throws ConnectionCanceledException;

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#download()
	 */
    public void download() {
        this.download(new AbstractStreamListener());
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#download(boolean)
	 */
    public void download(final boolean check) {
//        this.download(new BandwidthThrottle(BandwidthThrottleService.UNLIMITED), new AbstractStreamListener(), check);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#download(ch.cyberduck.core.StreamListener)
	 */
    public void download(StreamListener listener) {
//        this.download(new BandwidthThrottle(BandwidthThrottleService.UNLIMITED), listener);
    }

    /**
     * @param throttle The bandwidth limit
     * @param listener The stream listener to notify about bytes received and sent
     */
    public void download(BandwidthThrottleService throttle, StreamListener listener) {
        this.download(throttle, listener, false);
    }

    /**
     * @param throttle The bandwidth limit
     * @param listener The stream listener to notify about bytes received and sent
     * @param check    Check for open connection and open if needed before transfer
     */
    public abstract void download(BandwidthThrottleService throttle, StreamListener listener, boolean check);

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#upload()
	 */
    public void upload() {
        this.upload(new AbstractStreamListener());
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#upload(ch.cyberduck.core.StreamListener)
	 */
    public void upload(StreamListener listener) {
//        this.upload(new BandwidthThrottle(BandwidthThrottleService.UNLIMITED), listener);
    }

    /**
     * @param throttle The bandwidth limit
     * @param listener The stream listener to notify about bytes received and sent
     */
    public void upload(BandwidthThrottleService throttle, StreamListener listener) {
        this.upload(throttle, listener, null);
    }

    public void upload(BandwidthThrottleService throttle, StreamListener listener, Permission p) {
        this.upload(throttle, listener, p, false);
    }

    /**
     * @param throttle The bandwidth limit
     * @param listener The stream listener to notify about bytes received and sent
     * @param p        The permission to set after uploading or null
     * @param check    Check for open connection and open if needed before transfer
     */
    protected abstract void upload(BandwidthThrottleService throttle, StreamListener listener, Permission p, boolean check);

    /**
     * Will copy from in to out. Will attempt to skip Status#getCurrent
     * from the inputstream but not from the outputstream. The outputstream
     * is asssumed to append to a already existing file if
     * Status#getCurrent > 0
     *
     * @param in       The stream to read from
     * @param out      The stream to write to
     * @param throttle The bandwidth limit
     * @param l        The stream listener to notify about bytes received and sent
     * @throws IOResumeException If the input stream fails to skip the appropriate
     *                           number of bytes
     */
    protected void upload(OutputStream out, InputStream in, BandwidthThrottleService throttle, final StreamListener l) throws IOException {
        if(log.isDebugEnabled()) {
            log.debug("upload(" + out.toString() + ", " + in.toString());
        }
        this.getSession().message(MessageFormat.format(Locale.localizedString("Uploading {0}", "Status"),
                this.getName()));

        if(getStatus().isResume()) {
            long skipped = in.skip(getStatus().getCurrent());
            log.info("Skipping " + skipped + " bytes");
            if(skipped < getStatus().getCurrent()) {
                throw new IOResumeException("Skipped " + skipped + " bytes instead of " + getStatus().getCurrent());
            }
        }
        this.transfer(in, new ThrottledOutputStream(out, throttle), l);
    }

    /**
     * Will copy from in to out. Does not attempt to skip any bytes from the streams.
     *
     * @param in       The stream to read from
     * @param out      The stream to write to
     * @param throttle The bandwidth limit
     * @param l        The stream listener to notify about bytes received and sent
     * @throws IOException
     */
    protected void download(InputStream in, OutputStream out, BandwidthThrottleService throttle, final StreamListener l) throws IOException {
        if(log.isDebugEnabled()) {
            log.debug("download(" + in.toString() + ", " + out.toString());
        }
        this.getSession().message(MessageFormat.format(Locale.localizedString("Downloading {0}", "Status"),
                this.getName()));

        // Only update the file custom icon if the size is > 5MB. Otherwise creating too much
        // overhead when transferring a large amount of files
        final boolean updateIcon = attributes.getSize() > Status.MEGA * 5;
        // Set the first progress icon
        this.getLocal().setIcon(0);

        if(Preferences.instance().getBoolean("queue.download.quarantine")) {
            // Set quarantine attributes
            this.getLocal().setQuarantine(this.getHost().toURL(), this.toURL());
        }
        if(Preferences.instance().getBoolean("queue.download.wherefrom")) {
            // Set quarantine attributes
            this.getLocal().setWhereFrom(this.toURL());
        }

        final StreamListener listener = new StreamListener() {
            int step = 0;

            public void bytesSent(long bytes) {
                l.bytesSent(bytes);
            }

            public void bytesReceived(long bytes) {
                if(-1 == bytes) {
                    // Remove custom icon if complete. The Finder will display the default
                    // icon for this filetype
                    getLocal().setIcon(-1);
                }
                else {
                    l.bytesReceived(bytes);
                    if(updateIcon) {
                        int fraction = (int) (getStatus().getCurrent() / attributes.getSize() * 10);
                        // An integer between 0 and 9
                        if(fraction > step) {
                            // Another 10 percent of the file has been transferred
                            getLocal().setIcon(++step);
                        }
                    }
                }
            }
        };
        this.transfer(new ThrottledInputStream(in, throttle), out, listener);
    }

    /**
     * @param in       The stream to read from
     * @param out      The stream to write to
     * @param listener The stream listener to notify about bytes received and sent
     * @throws IOException
     */
    private void transfer(InputStream in, OutputStream out, StreamListener listener) throws IOException {
        final int chunksize = 32768;
        byte[] chunk = new byte[chunksize];
        long bytesTransferred = getStatus().getCurrent();
        while(!getStatus().isCanceled()) {
            int read = in.read(chunk, 0, chunksize);
            listener.bytesReceived(read);
            if(-1 == read) {
                // End of file
                getStatus().setComplete(true);
                break;
            }
            out.write(chunk, 0, read);
            listener.bytesSent(read);
            bytesTransferred += read;
            getStatus().setCurrent(bytesTransferred);
        }
        out.flush();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#copy(ch.cyberduck.core.AbstractPath)
	 */
    @Override
    public void copy(final AbstractPath copy) {
        final Local local = LocalFactory.createLocal(Preferences.instance().getProperty("tmp.dir"),
                copy.getName());
        TransferOptions options = new TransferOptions();
        options.closeSession = false;
        try {
            this.setLocal(local);
//            DownloadTransferService download = new DownloadTransferService(this);
//            download.start(new TransferPrompt() {
//                public TransferAction prompt() {
//                    return TransferAction.ACTION_OVERWRITE;
//                }
//            }, options);
            ((Path) copy).setLocal(local);
//            UploadTransferService upload = new UploadTransferService(((Path) copy));
//            upload.start(new TransferPrompt() {
//                public TransferAction prompt() {
//                    return TransferAction.ACTION_OVERWRITE;
//                }
//            }, options);
        }
        finally {
            local.delete();
        }
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#exists()
	 */
    @Override
    public boolean exists() {
        if(this.isRoot()) {
            return true;
        }
        return this.getParent().childs().contains(this);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#hashCode()
	 */
    @Override
    public int hashCode() {
        return this.getAbsolute().hashCode();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#equals(java.lang.Object)
	 */
    @Override
    public boolean equals(Object other) {
        if(null == other) {
            return false;
        }
        if(other instanceof Path) {
            //BUG: returns the wrong result on case-insensitive systems, e.g. NT!
            return this.getAbsolute().equals(((Path) other).getAbsolute());
        }
        return this.getAbsolute().equals(other.toString());
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#toString()
	 */
    @Override
    public String toString() {
        return this.getAbsolute();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#encode(java.lang.String)
	 */
    public String encode(final String p) {
        try {
            StringBuilder b = new StringBuilder();
            StringTokenizer t = new StringTokenizer(p, "/");
            while(t.hasMoreTokens()) {
                b.append(DELIMITER).append(URLEncoder.encode(t.nextToken(), "UTF-8"));
            }
            return b.toString().replaceAll("\\+", "%20");
        }
        catch(UnsupportedEncodingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#toURL()
	 */
    @Override
    public String toURL() {
        // Do not use java.net.URL because it doesn't know about custom protocols!
        return this.getHost().toURL() + this.encode(this.getAbsolute());
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.Path#toHttpURL()
	 */
    public String toHttpURL() {
        return this.toHttpURL(this.getHost().getWebURL());
    }

    /**
     * @param host
     * @return
     */
    protected String toHttpURL(String host) {
        String absolute = this.encode(this.getAbsolute());
        if(StringUtils.isNotBlank(this.getHost().getDefaultPath())) {
            if(absolute.startsWith(this.getHost().getDefaultPath())) {
                absolute = absolute.substring(this.getHost().getDefaultPath().length());
            }
        }
        if(!absolute.startsWith(Path.DELIMITER)) {
            absolute = Path.DELIMITER + absolute;
        }
        return host + absolute;
    }

    protected void error(String message) {
        this.error(message, null);
    }

    /**
     * @see Session#error(Path,String,Throwable)
     */
    protected void error(String message, Throwable throwable) {
        try {
            this.getSession().error(this, message, throwable);
        }
        catch(ConnectionCanceledException e) {
            log.error(e.getMessage());
        }
    }
}