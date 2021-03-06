package ch.cyberduck.core.threading;

/*
 *  Copyright (c) 2008 David Kocher. All rights reserved.
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @version $Id: AbstractBackgroundAction.java 5401 2009-10-03 21:55:19Z dkocher $
 */
public abstract class AbstractBackgroundAction implements BackgroundAction {

    public void init() {
        ;
    }

    /**
	 * @uml.property  name="canceled"
	 */
    private boolean canceled;

    public void cancel() {
        canceled = true;
        BackgroundActionListener[] l = listeners.toArray(
                new BackgroundActionListener[listeners.size()]);
        for(BackgroundActionListener listener : l) {
            listener.cancel(this);
        }
    }

    /**
	 * To be overriden by a concrete subclass. Returns false by default for actions not connected to a graphical user interface
	 * @return  True if the user canceled this action
	 * @uml.property  name="canceled"
	 */
    public boolean isCanceled() {
        return canceled;
    }

    /**
	 * @uml.property  name="running"
	 */
    private boolean running;

    /**
	 * @return
	 * @uml.property  name="running"
	 */
    public boolean isRunning() {
        return running;
    }

    public boolean prepare() {
        if(this.isCanceled()) {
            return false;
        }
        running = true;
        BackgroundActionListener[] l = listeners.toArray(
                new BackgroundActionListener[listeners.size()]);
        for(BackgroundActionListener listener : l) {
            listener.start(this);
        }
        return true;
    }

    public void finish() {
        running = false;
        BackgroundActionListener[] l = listeners.toArray(
                new BackgroundActionListener[listeners.size()]);
        for(BackgroundActionListener listener : l) {
            listener.stop(this);
        }
    }

    public void cleanup() {
        ;
    }

    /**
	 * @uml.property  name="listeners"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="ch.cyberduck.core.threading.BackgroundActionListener"
	 */
    private Set<BackgroundActionListener> listeners
            = Collections.synchronizedSet(new HashSet<BackgroundActionListener>());

    public void addListener(BackgroundActionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(BackgroundActionListener listener) {
        listeners.remove(listener);
    }

    public String getActivity() {
        return Locale.localizedString("Unknown");
    }

    @Override
    public String toString() {
        return this.getActivity();
    }

    /**
	 * @uml.property  name="lock"
	 */
    private final Object lock = new Object();

    public Object lock() {
        // No synchronization with other tasks by default
        return lock;
    }
}