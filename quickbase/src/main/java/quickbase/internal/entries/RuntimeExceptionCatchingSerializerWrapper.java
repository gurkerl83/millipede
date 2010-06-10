/*
 * Created on 04.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.entries;

import quickbase.exception.SerializationDatabaseException;
import quickbase.serializer.ISerializer;


public class RuntimeExceptionCatchingSerializerWrapper<K> implements ISerializer<K> {
    
    private ISerializer<K> keys;
    
    public RuntimeExceptionCatchingSerializerWrapper(ISerializer<K> keys){
        this.keys = keys;
    }

    public K fromBytes(byte[] data, int offset) throws SerializationDatabaseException {
        try {
            return keys.fromBytes(data, offset);
        } catch (RuntimeException e){
            throw new SerializationDatabaseException(e);
        }
    }

    public byte[] toBytes(K key) throws SerializationDatabaseException {
        try {
            return keys.toBytes(key);
        } catch (RuntimeException e){
            throw new SerializationDatabaseException(e);
        }
    }

}
