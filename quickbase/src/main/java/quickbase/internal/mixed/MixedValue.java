/*
 * Created on 03.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.mixed;

import quickbase.exception.SerializationDatabaseException;
import quickbase.serializer.ISerializer;


public class MixedValue {
    
    private Object object;
    private byte[] data;

    public MixedValue(byte[] data) {
        this.data = data;
    }
    
    public <K,V> MixedValue(ISerializer<V> serializer, V key, byte id) throws SerializationDatabaseException {
        this.object = key;
        byte[] temp = serializer.toBytes(key);
        byte[] data = new byte[temp.length + 1];
        data[0] = id;
        System.arraycopy(temp, 0, data, 1, temp.length);
        this.data = data; 
    }

    @SuppressWarnings("unchecked")
    public <K, V> V getWrapped(ISerializer<V> serializer) throws SerializationDatabaseException {
        if (object == null){
            object = serializer.fromBytes(data, 1);
        }
        return (V)object;
    }
    
    @SuppressWarnings("unchecked")
    public byte[] getBytes() {
        return data;
    }

}
