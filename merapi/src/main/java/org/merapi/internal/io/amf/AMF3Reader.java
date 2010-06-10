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

package org.merapi.internal.io.amf;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.ASObject;
import flex.messaging.io.amf.Amf3Input;
import org.merapi.internal.io.reader.IReader;
import org.merapi.messages.IMessage;
import org.merapi.messages.Message;
import org.apache.log4j.Logger;
//import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The <code>AMF3Reader</code> class deserializes AMF 3 encoded binary data into
 * an <code>IMessage</code>. When a message has been received from the Flex
 * bridge.
 * 
 * @see org.merapi.io.reader.IReader;
 */
//@Service
public class AMF3Reader implements IReader {

	// --------------------------------------------------------------------------
	//
	// Constructor
	//
	// --------------------------------------------------------------------------

	/**
	 * Constructor.
	 */
	public AMF3Reader() {
		super();

		__amf3Input = new Amf3Input(new SerializationContext());
	}

	// --------------------------------------------------------------------------
	//
	// Variables
	//
	// --------------------------------------------------------------------------

	/**
	 * @private Used to deserialize AMF binary data into an
	 *          <code>IMessage</code>.
	 */
	Amf3Input __amf3Input = null;
	Logger __logger = Logger.getLogger(AMF3Reader.class);

	// --------------------------------------------------------------------------
	//
	// Methods
	//
	// --------------------------------------------------------------------------

	/**
	 * @return Reads the binary data <code>bytes</code> and deserializes it into
	 *         an <code>IMessage</code>.
	 */
	public ArrayList<IMessage> read(byte[] bytes)
			throws ClassNotFoundException, IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				bytes);
		
		__amf3Input.reset();
		__amf3Input.setInputStream(byteArrayInputStream);

		Object decoded = __amf3Input.readObject();
		IMessage message = null;
		ArrayList<IMessage> al = new ArrayList<IMessage>();
		while (decoded != null) {
			if (decoded instanceof IMessage) {
				message = (IMessage) decoded;
			} else if (decoded instanceof ASObject) {
				ASObject aso = (ASObject) decoded;

				Message m = new Message();
				if (aso.get("type") != null) {
					m.setType(aso.get("type").toString());
				}
				if (aso.get("uid") != null ) {
					m.setUid(aso.get("uid").toString());
				}
				if (aso.containsKey("data")) {
					m.setData(aso.get("data"));
				}
				
				message = m;
			}

			al.add(message);
			try {
				decoded = __amf3Input.readObject();
			} catch (Exception e) {
				decoded = null;
				__logger.error(e.getMessage());
			}
		}

		return al;
	}

}
