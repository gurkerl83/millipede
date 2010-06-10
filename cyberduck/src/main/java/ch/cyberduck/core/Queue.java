package ch.cyberduck.core;

/*
 *  Copyright (c) 2007 David Kocher. All rights reserved.
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

/**
 * @version  $Id: Queue.java 2939 2007-04-20 12:52:33Z dkocher $
 */
public final class Queue {

    /**
	 * @uml.property  name="instance"
	 * @uml.associationEnd  
	 */
    private static Queue instance;

    private static final Object lock = new Object();

    public static Queue instance() {
        synchronized(lock) {
            if(null == instance) {
                instance = new Queue();
            }
            return instance;
        }
    }
}