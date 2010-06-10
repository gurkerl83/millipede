package ch.cyberduck.threading;

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

import ch.cyberduck.WindowController;
import ch.cyberduck.core.threading.DefaultMainAction;
//import ch.cyberduck.ui.cocoa.WindowController;

/**
 * @version $Id$
 */
public abstract class WindowMainAction extends DefaultMainAction {

    /**
	 * @uml.property  name="controller"
	 * @uml.associationEnd  
	 */
    private WindowController controller;

    public WindowMainAction(WindowController c) {
        this.controller = c;
    }

    /**
     * @return True if hte window is still on screen
     */
    @Override
    public boolean isValid() {
//        return controller.isVisible();
        return true;
    }
}