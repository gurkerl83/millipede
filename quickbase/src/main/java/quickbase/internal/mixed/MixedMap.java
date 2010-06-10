/*
 * Created on 03.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.mixed;

import java.util.ArrayList;
import java.util.Collection;

import quickbase.SingleMapDatabase;
import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.DatabaseClosedDatabaseException;
import quickbase.exception.SerializationDatabaseException;
import quickbase.internal.entries.RuntimeExceptionCatchingSerializerWrapper;
import quickbase.serializer.ISerializer;
import quickbase.service.IPersistedMap;
import quickbase.service.IVisitor;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MixedMap<K, V> implements IPersistedMap<K, V> {

    private byte id;
    private ISerializer<K> keySerializer;
    private ISerializer<V> valueSerializer;
    private SingleMapDatabase<MixedKey, MixedValue> map;

    public MixedMap(byte num, ISerializer<K> keySerializer, ISerializer<V> valueSerializer, SingleMapDatabase<MixedKey, MixedValue> database) {
        this.id = num;
        this.keySerializer = new RuntimeExceptionCatchingSerializerWrapper<K>(keySerializer);
        this.valueSerializer = new RuntimeExceptionCatchingSerializerWrapper<V>(valueSerializer);
        this.map = database;
    }

    public boolean contains(K key) throws DatabaseClosedDatabaseException, SerializationDatabaseException, BasicFileOperationDatabaseException {
        return map.contains(new MixedKey(keySerializer, key, id));
    }

    @SuppressWarnings("unchecked")
    public V get(K key) throws SerializationDatabaseException, DatabaseClosedDatabaseException, BasicFileOperationDatabaseException {
        MixedValue val = map.get(new MixedKey(keySerializer, key, id));
        if (val == null) {
            return null;
        } else {
            return val.getWrapped(valueSerializer);
        }
    }

    public void put(K key, V value) throws DatabaseClosedDatabaseException, SerializationDatabaseException, BasicFileOperationDatabaseException {
        map.put(new MixedKey(keySerializer, key, id), new MixedValue(valueSerializer, value, id));
    }

    public void remove(K key) throws DatabaseClosedDatabaseException, SerializationDatabaseException, BasicFileOperationDatabaseException {
        map.remove(new MixedKey(keySerializer, key, id));
    }

    public void visit(final IVisitor<K, V> visitor) throws DatabaseClosedDatabaseException, BasicFileOperationDatabaseException {
        map.visit(new MixedVisitor<K, V>(id, keySerializer, valueSerializer, visitor));
    }

    public Collection<V> values() throws DatabaseClosedDatabaseException, BasicFileOperationDatabaseException {
        final ArrayList<V> list = new ArrayList<V>();
        visit(new IVisitor<K, V>() {

            public void visit(K key, V value) {
                list.add(value);
            }
        });
        return list;
    }

    public void clear() throws DatabaseClosedDatabaseException, BasicFileOperationDatabaseException {
//        throw new NotImplementedException();
    }

}
