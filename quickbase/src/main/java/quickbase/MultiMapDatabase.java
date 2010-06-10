/*
 * Created on 03.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase;

import java.io.File;

import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.IExceptionHandlingStrategy;
import quickbase.internal.index.Stats;
import quickbase.internal.mixed.MixedKey;
import quickbase.internal.mixed.MixedKeySerializer;
import quickbase.internal.mixed.MixedMap;
import quickbase.internal.mixed.MixedValue;
import quickbase.internal.mixed.MixedValueSerializer;
import quickbase.serializer.ISerializer;
import quickbase.service.IPersistedMap;

/**
 * A database that can contain multiple maps.
 * For each map stored in it, a unique number must be defined.
 * 
 * @author Luzius Meisser
 */
public class MultiMapDatabase {
    
    private SingleMapDatabase<MixedKey, MixedValue> database;
    
    public MultiMapDatabase(IExceptionHandlingStrategy strategy, File path, String name) throws BasicFileOperationDatabaseException {
        database = new SingleMapDatabase<MixedKey, MixedValue>(strategy, path, name, new MixedKeySerializer(), new MixedValueSerializer());
    }
    
    /**
     * Creates a Persisted map.
     * Please not that the equals and the hashcode function of the key are ignored.
     * Quickbase looks at the bytes of the serialized keys for comparisons/hashes.
     * 
     * @param <K> Key type
     * @param <V> Value type
     * @param A unique id for this map (to be chosen by you). Make sure you create
     * no other map with this id.
     * @return
     */
    public <K, V> IPersistedMap<K, V> createMap(byte num, ISerializer<K> keySerializer, ISerializer<V> valueSerializer){
        return new MixedMap<K, V>(num, keySerializer, valueSerializer, this.database);
    }

    public void close(){
        database.close();
    }

    public Stats getStats() {
        return database.getStats();
    }
    
}
