/*
 * Created on 12.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.serializer;

import quickbase.exception.SerializationDatabaseException;


public class IntegerSerializer implements ISerializer<Integer> {

    public Integer fromBytes(byte[] data, int offset) throws SerializationDatabaseException {
        return (int)((((data[0] & 0xff) << 24) |
                ((data[1] & 0xff) << 16) |
                ((data[2] & 0xff) <<  8) |
                ((data[3] & 0xff))));
    }

    public byte[] toBytes(Integer key) throws SerializationDatabaseException {
        int x = key.intValue();
        return new byte[]{(byte) (x >> 24), (byte) (x >> 16), (byte) (x >> 8), (byte) x};
    }
    
}
