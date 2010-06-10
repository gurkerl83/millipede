package org.merapi.io.utils;

import java.net.Socket;

public class MessageLengthWriter {
	
	public void write(Socket client, int length) throws Exception {
		byte[] lengthBytes = new byte[4];	
		lengthBytes[0] =(byte)( length >> 24 );
		lengthBytes[1] =(byte)( (length << 8) >> 24 );
		lengthBytes[2] =(byte)( (length << 16) >> 24 );
		lengthBytes[3] =(byte)( (length << 24) >> 24 );
		for(byte i=0; i<4; i++){
			client.getOutputStream().write( lengthBytes[i] );
		}
	}
}