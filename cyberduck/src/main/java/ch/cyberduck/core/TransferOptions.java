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
 * @version  $Id: TransferOptions.java 3119 2007-07-01 15:29:15Z dkocher $
 */
public class TransferOptions {

    /**
	 * @uml.property  name="dEFAULT"
	 * @uml.associationEnd  
	 */
    public static final TransferOptions DEFAULT 
            = new TransferOptions();

    /**
	 * Resume requested using user interface
	 * @uml.property  name="resumeRequested"
	 */
    public boolean resumeRequested = false;

    /**
	 * Reload requested using user interface
	 * @uml.property  name="reloadRequested"
	 */
    public boolean reloadRequested = false;

    /**
	 * Close session after transfer
	 * @uml.property  name="closeSession"
	 */
    public boolean closeSession = true;
}
