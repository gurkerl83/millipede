////////////////////////////////////////////////////////////////////////////////
//
//  This program is free software; you can redistribute it and/or modify 
//  it under the terms of the GNU Lesser General Public License as published 
//  by the Free Software Foundation; either version 3 of the License, or (at 
//  your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
//  License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License 
//  along with this program; if not, see <http://www.gnu.org/copyleft/lesser.html>.
//
////////////////////////////////////////////////////////////////////////////////

package org.merapi.internal.io.reader;

import java.util.ArrayList;

import org.merapi.messages.IMessage;

/**
 *  The <code>IReader</code> interface describes a class for reading binary data from 
 *  the org.merapi bridge.
 * 
 *  @see org.merapi.Bridge;
 *  @see org.merapi.io.AMF3Reader;
 */
public interface IReader {

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Deserializes the binary data <code>bytes</code> and returns a the data as an Object
     */	
	ArrayList<IMessage> read( byte[] bytes ) throws Exception;
	
}
