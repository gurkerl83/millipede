package ch.cyberduck.core;

/*
 *  Copyright (c) 2006 David Kocher. All rights reserved.
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

import ch.cyberduck.core.serializer.TransferReaderFactory;
import ch.cyberduck.core.serializer.TransferWriterFactory;

import org.apache.log4j.Logger;

/**
 * @version $Id: TransferCollection.java 5382 2009-10-01 20:47:16Z dkocher $
 */
public class TransferCollection extends Collection<Transfer> {
    private static Logger log = Logger.getLogger(TransferCollection.class);

    private static TransferCollection instance;

    private TransferCollection() {
        this.load();
    }

    private static final Object lock = new Object();

    public static TransferCollection instance() {
        synchronized(lock) {
            if(null == instance) {
                instance = new TransferCollection();
            }
            return instance;
        }
    }

    private static final Local QUEUE_FILE
            = LocalFactory.createLocal(Preferences.instance().getProperty("application.support.path"), "Queue.plist");

    static {
        QUEUE_FILE.getParent().mkdir();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.TransferCollectionService#add(ch.cyberduck.core.Transfer)
	 */
    @Override
    public  boolean add(Transfer o) {
        boolean r = super.add(o);
        this.save();
        return r;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.TransferCollectionService#add(int, ch.cyberduck.core.Transfer)
	 */
    @Override
    public  void add(int row, Transfer o) {
        super.add(row, o);
        this.save();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.TransferCollectionService#remove(int)
	 */
    @Override
    public  Transfer remove(int row) {
        return super.remove(row);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.TransferCollectionService#save()
	 */
    public void save() {
        this.save(QUEUE_FILE);
    }

    private  void save(Local f) {
        log.debug("save");
        if(Preferences.instance().getBoolean("queue.save")) {
            TransferWriterFactory.instance().write(this, f);
        }
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.TransferCollectionService#load()
	 */
    public void load() {
        this.load(QUEUE_FILE);
    }

    private  void load(Local f) {
        log.debug("load");
        if(f.exists()) {
            log.info("Found Queue file: " + f.toString());
            this.addAll(TransferReaderFactory.instance().readCollection(f));
        }
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.TransferCollectionService#numberOfRunningTransfers()
	 */
    public  int numberOfRunningTransfers() {
        int running = 0;
        // Count the number of running transfers
        for(Transfer t : this) {
            if(null == t) {
                continue;
            }
            if(t.isRunning()) {
                running++;
            }
        }
        log.debug("numberOfRunningTransfers:" + running);
        return running;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.TransferCollectionService#numberOfQueuedTransfers()
	 */
    public  int numberOfQueuedTransfers() {
        int queued = 0;
        // Count the number of queued transfers
        for(Transfer t : this) {
            if(null == t) {
                continue;
            }
            if(t.isQueued()) {
                queued++;
            }
        }
        log.debug("numberOfQueuedTransfers:" + queued);
        return queued;
    }
}