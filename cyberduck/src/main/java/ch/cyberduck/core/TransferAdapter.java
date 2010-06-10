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

import ch.cyberduck.core.io.service.BandwidthThrottleService;

/**
 * @version $Id: TransferAdapter.java 4088 2008-07-14 16:19:34Z dkocher $
 */
public class TransferAdapter implements TransferListener {

    public void transferWillStart() {
    }

    public void transferQueued() {
    }

    public void transferResumed() {
    }

    public void transferDidEnd() {
    }

    public void willTransferPath(final Path path) {
    }

    public void didTransferPath(final Path path) {
    }

    public void bandwidthChanged(BandwidthThrottleService bandwidth) {
    }
}