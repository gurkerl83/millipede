/*
 * Created on 03.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.mixed;

import quickbase.exception.SerializationDatabaseException;
import quickbase.serializer.ISerializer;

public class MixedKey {

    private Object object;
    private byte[] data;

    public MixedKey(byte[] data) {
        this.data = data;
    }

    public <K, V> MixedKey(ISerializer<K> serializer, K key, byte id) throws SerializationDatabaseException {
        this.object = key;
        byte[] temp = serializer.toBytes(key);
        this.data = new byte[temp.length + 1];
        System.arraycopy(temp, 0, data, 1, temp.length);
        data[0] = id;
    }

    @SuppressWarnings("unchecked")
    public <K, V> K getWrapped(ISerializer<K> serializer) throws SerializationDatabaseException {
        if (object == null) {
            object = serializer.fromBytes(data, 1);
        }
        return (K) object;
    }

    @SuppressWarnings("unchecked")
    public byte[] getBytes() {
        return data;
    }

    public byte getId() {
        return data[0];
    }

}
