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

import ch.cyberduck.core.io.BandwidthThrottle;
//import ch.cyberduck.core.s3.S3Session;
import ch.cyberduck.core.serializer.Serializer;
//import ch.cyberduck.ui.growl.Growl;
import ch.cyberduck.core.service.UploadTransferService;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @version $Id: UploadTransfer.java 5830 2010-03-03 12:02:26Z dkocher $
 */
public class UploadTransfer extends Transfer implements UploadTransferService {

    public UploadTransfer(Path root) {
        super(root);
    }

    public UploadTransfer(List<Path> roots) {
        super(roots);
    }

    public <T> UploadTransfer(T dict, Session s) {
        super(dict, s);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.UploadTransferService#getAsDictionary()
	 */
    @Override
    public <T> T getAsDictionary() {
        final Serializer dict = super.getSerializer();
        dict.setStringForKey(String.valueOf(KIND_UPLOAD), "Kind");
        return dict.<T>getSerialized();
    }

    @Override
    protected void init() {
        log.debug("init");
        this.bandwidth = new BandwidthThrottle(
                Preferences.instance().getFloat("queue.upload.bandwidth.bytes"));
    }

    @Override
    protected void setRoots(List<Path> uploads) {
        final List<Path> normalized = new Collection<Path>();
        for(Path upload : uploads) {
            boolean duplicate = false;
            for(Iterator<Path> iter = normalized.iterator(); iter.hasNext();) {
                Path n = iter.next();
                if(upload.getLocal().isChild(n.getLocal())) {
                    // The selected file is a child of a directory already included
                    duplicate = true;
                    break;
                }
                if(n.getLocal().isChild(upload.getLocal())) {
                    iter.remove();
                }
                if(upload.getName().equals(n.getName())) {
                    // The selected file has the same name; if downloaded as a root element
                    // it would overwrite the earlier
                    final String parent = upload.getParent().getAbsolute();
                    final String filename = upload.getName();
                    String proposal;
                    int no = 0;
                    int index = filename.lastIndexOf(".");
                    do {
                        no++;
                        if(index != -1 && index != 0) {
                            proposal = filename.substring(0, index)
                                    + "-" + no + filename.substring(index);
                        }
                        else {
                            proposal = filename + "-" + no;
                        }
                        upload.setPath(parent, proposal);
                    }
                    while(false);//(upload.exists());
                    log.info("Changed name to:" + upload.getName());
                }
            }
            // Prunes the list of selected files. Files which are a child of an already included directory
            // are removed from the returned list.
            if(!duplicate) {
                normalized.add(upload);
            }
        }
        super.setRoots(normalized);
    }

    /**
     *
     */
    private abstract class UploadTransferFilter extends TransferFilter {
        public boolean accept(final Path file) {
            return file.getLocal().exists();
        }

        @Override
        public void prepare(Path p) {
            if(p.attributes.isFile()) {
                // Read file size
                size += p.getLocal().attributes.getSize();
                if(p.getStatus().isResume()) {
                    transferred += p.attributes.getSize();
                }
            }
            if(p.attributes.isDirectory()) {
                if(!p.exists()) {
                    p.cache().put(p, new AttributedList<Path>());
                }
            }
        }
    }

    /**
	 * @uml.property  name="childFilter"
	 * @uml.associationEnd  
	 */
    private final PathFilter<Local> childFilter = new PathFilter<Local>() {
        public boolean accept(Local child) {
            try {
                if(Preferences.instance().getBoolean("queue.upload.skip.enable")) {
                    if(Pattern.compile(Preferences.instance().getProperty("queue.upload.skip.regex")).matcher(child.getName()).matches()) {
                        return false;
                    }
                }
            }
            catch(PatternSyntaxException e) {
                log.warn(e.getMessage());
            }
            return true;
        }
    };

    /**
	 * File listing cache for children of the root paths not part of the session cache because they only exist on the local file system.
	 * @uml.property  name="cache"
	 * @uml.associationEnd  
	 */
    private final Cache<Path> cache = new Cache<Path>();

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.UploadTransferService#childs(ch.cyberduck.core.Path)
	 */
    @Override
    public AttributedList<Path> childs(final Path parent) {
        if(!cache.containsKey(parent)) {
            if(!parent.getLocal().exists()) {
                // Cannot fetch file listing of non existant file
                return AttributedList.emptyList();
            }
            final AttributedList<Path> childs = new AttributedList<Path>();
            for(AbstractPath child : parent.getLocal().childs(childFilter)) {
                final Local local = LocalFactory.createLocal(child.getAbsolute());
                Path upload = PathFactory.createPath(getSession(), parent.getAbsolute(), local);
                if(upload.exists()) {
                    upload = this.getSession().cache().lookup(upload.getReference());
                    upload.setLocal(local);
                }
                childs.add(upload);
            }
            cache.put(parent, childs);
        }
        return cache.get(parent);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.UploadTransferService#lookup(ch.cyberduck.core.PathReference)
	 */
    @Override
    public Path lookup(PathReference r) {
        return cache.lookup(r);
    }

    @Override
    protected void clear(final TransferOptions options) {
        cache.clear();
        super.clear(options);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.UploadTransferService#cache()
	 */
    @Override
    public Cache<Path> cache() {
        return cache;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.UploadTransferService#isResumable()
	 */
    @Override
    public boolean isResumable() {
        return this.getSession().isUploadResumable();
    }

    /**
	 * @uml.property  name="aCTION_OVERWRITE"
	 * @uml.associationEnd  
	 */
    private final TransferFilter ACTION_OVERWRITE = new UploadTransferFilter() {
        @Override
        public boolean accept(final Path p) {
            if(super.accept(p)) {
                if(p.attributes.isDirectory()) {
                    // Do not attempt to create a directory that already exists
                    return !p.exists();
                }
                return true;
            }
            return false;
        }

        @Override
        public void prepare(final Path p) {
            if(p.exists()) {
                if(p.attributes.getPermission() == null) {
                    if(Preferences.instance().getBoolean("queue.upload.changePermissions")) {
                        p.readPermission();
                    }
                }
            }
            if(p.attributes.isFile()) {
                p.getStatus().setResume(false);
            }
            super.prepare(p);
        }

    };

    /**
	 * @uml.property  name="aCTION_RESUME"
	 * @uml.associationEnd  
	 */
    private final TransferFilter ACTION_RESUME = new UploadTransferFilter() {
        @Override
        public boolean accept(final Path p) {
            if(super.accept(p)) {
                if(p.attributes.isDirectory()) {
                    return !p.exists();
                }
                if(p.getStatus().isComplete() || p.getLocal().attributes.getSize() == p.attributes.getSize()) {
                    // No need to resume completed transfers
                    p.getStatus().setComplete(true);
                    return false;
                }
                return true;
            }
            return false;
        }

        @Override
        public void prepare(final Path p) {
            if(p.exists()) {
                if(p.attributes.getSize() == -1) {
                    p.readSize();
                }
                if(p.attributes.getModificationDate() == -1) {
                    if(Preferences.instance().getBoolean("queue.upload.preserveDate")) {
                        p.readTimestamp();
                    }
                }
                if(p.attributes.getPermission() == null) {
                    if(Preferences.instance().getBoolean("queue.upload.changePermissions")) {
                        p.readPermission();
                    }
                }
            }
            if(p.attributes.isFile()) {
                // Append to file if size is not zero
                final boolean resume = p.exists()
                        && p.attributes.getSize() > 0;
                p.getStatus().setResume(resume);
                if(p.getStatus().isResume()) {
                    p.getStatus().setCurrent(p.attributes.getSize());
                }
            }
            super.prepare(p);
        }
    };

    /**
	 * @uml.property  name="aCTION_RENAME"
	 * @uml.associationEnd  
	 */
    private final TransferFilter ACTION_RENAME = new UploadTransferFilter() {
        @Override
        public boolean accept(final Path p) {
            // Rename every file
            return super.accept(p);
        }

        @Override
        public void prepare(final Path p) {
            if(p.exists()) {
                final String parent = p.getParent().getAbsolute();
                final String filename = p.getName();
                int no = 0;
                while(p.exists()) { // Do not use cached value of exists!
                    no++;
                    String proposal = FilenameUtils.getBaseName(filename) + "-" + no;
                    if(StringUtils.isNotBlank(FilenameUtils.getExtension(filename))) {
                        proposal += "." + FilenameUtils.getExtension(filename);
                    }
                    p.setPath(parent, proposal);
                }
                log.info("Changed local name to:" + p.getName());
            }
            if(p.attributes.isFile()) {
                p.getStatus().setResume(false);
            }
            super.prepare(p);
        }
    };

    /**
	 * @uml.property  name="aCTION_SKIP"
	 * @uml.associationEnd  
	 */
    private final TransferFilter ACTION_SKIP = new UploadTransferFilter() {
        @Override
        public boolean accept(final Path p) {
            if(super.accept(p)) {
                if(!p.exists()) {
                    return true;
                }
            }
            return false;
        }
    };

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.UploadTransferService#filter(ch.cyberduck.core.TransferAction)
	 */
    @Override
    public TransferFilter filter(final TransferAction action) {
        log.debug("filter:" + action);
        if(action.equals(TransferAction.ACTION_OVERWRITE)) {
            return ACTION_OVERWRITE;
        }
        if(action.equals(TransferAction.ACTION_RESUME)) {
            return ACTION_RESUME;
        }
        if(action.equals(TransferAction.ACTION_RENAME)) {
            return ACTION_RENAME;
        }
        if(action.equals(TransferAction.ACTION_SKIP)) {
            return ACTION_SKIP;
        }
        if(action.equals(TransferAction.ACTION_CALLBACK)) {
            for(Path root : this.getRoots()) {
                if(root.exists()) {
                    if(root.getLocal().attributes.isDirectory()) {
                        if(0 == this.childs(root).size()) {
                            // Do not prompt for existing empty directories
                            continue;
                        }
                    }
                    // Prompt user to choose a filter
                    TransferAction result = prompt.prompt();
                    return this.filter(result); //break out of loop
                }
            }
            // No files exist yet therefore it is most straightforward to use the overwrite action
            return this.filter(TransferAction.ACTION_OVERWRITE);
        }
        return super.filter(action);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.UploadTransferService#action(boolean, boolean)
	 */
    @Override
    public TransferAction action(final boolean resumeRequested, final boolean reloadRequested) {
        log.debug("action:" + resumeRequested + "," + reloadRequested);
        if(resumeRequested) {
            // Force resume
            return TransferAction.ACTION_RESUME;
        }
        if(reloadRequested) {
            return TransferAction.forName(
                    Preferences.instance().getProperty("queue.upload.reload.fileExists")
            );
        }
        // Use default
        return TransferAction.forName(
                Preferences.instance().getProperty("queue.upload.fileExists")
        );
    }

    @Override
    protected void _transferImpl(final Path p) {
        Permission permission = null;
        if(Preferences.instance().getBoolean("queue.upload.changePermissions")) {
            permission = p.attributes.getPermission();
            if(null == permission) {
                if(Preferences.instance().getBoolean("queue.upload.permissions.useDefault")) {
                    if(p.attributes.isFile()) {
                        permission = new Permission(
                                Preferences.instance().getInteger("queue.upload.permissions.file.default"));
                    }
                    if(p.attributes.isDirectory()) {
                        permission = new Permission(
                                Preferences.instance().getInteger("queue.upload.permissions.folder.default"));
                    }
                }
                else {
                    permission = p.getLocal().attributes.getPermission();
                }
            }
        }
        p.upload(bandwidth, new AbstractStreamListener() {
            @Override
            public void bytesSent(long bytes) {
                transferred += bytes;
            }
        }, permission);
    }

    @Override
    protected void fireTransferDidEnd() {
        if(this.isReset() && this.isComplete() && !this.isCanceled() && !(this.getTransferred() == 0)) {
//            Growl.instance().notify("Upload complete", getName());
        }
        super.fireTransferDidEnd();
    }
}