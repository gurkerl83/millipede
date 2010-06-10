package quickbase.serializer;

import quickbase.exception.SerializationDatabaseException;


public interface ISerializer<K> {

    public byte[] toBytes(K key) throws SerializationDatabaseException;

    public K fromBytes(byte[] data, int offset) throws SerializationDatabaseException;

}
