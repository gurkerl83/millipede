/*
 * Created on 14.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.entries;

import quickbase.exception.SerializationDatabaseException;
import quickbase.serializer.ISerializer;

public class HashFunction<K> {
    
    private static final int HASH_MULTIPLICATOR = 1372819921; 
    private static final int HASH_ADDER = -412231553;
    
    protected ISerializer<K> keySerializer;

    public HashFunction(ISerializer<K> keySerializer) {
        this.keySerializer = keySerializer;
    }

    public int getHash(K key) throws SerializationDatabaseException {
        byte[] data = keySerializer.toBytes(key);
        return calcHash(data);
    }

    public int getHash(Entry entry) {
        return calcHash(entry.getKey());
    }
    
    private int calcHash(byte[] key){
        int h = 0;
        int len = key.length;
        for (int i = 0; i < len; i++) {
            h = (HASH_MULTIPLICATOR * h + key[i]) ^ HASH_ADDER;
        }
        return h;
    }

}
