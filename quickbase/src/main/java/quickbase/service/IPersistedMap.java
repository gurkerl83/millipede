/*
 * Created on 03.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.service;

import java.util.Collection;

import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.DatabaseClosedDatabaseException;
import quickbase.exception.SerializationDatabaseException;



public interface IPersistedMap<K, V> {
    
    public void put(K key, V value) throws DatabaseClosedDatabaseException, SerializationDatabaseException, BasicFileOperationDatabaseException;
    
    public boolean contains(K key) throws DatabaseClosedDatabaseException, SerializationDatabaseException, BasicFileOperationDatabaseException;

    public V get(K key) throws SerializationDatabaseException, DatabaseClosedDatabaseException, BasicFileOperationDatabaseException;

    public void remove(K key) throws DatabaseClosedDatabaseException, SerializationDatabaseException, BasicFileOperationDatabaseException;

    public void visit(IVisitor<K, V> visitor) throws BasicFileOperationDatabaseException, DatabaseClosedDatabaseException;

    public Collection<V> values() throws DatabaseClosedDatabaseException, BasicFileOperationDatabaseException;

    /**
     * Deletes the underlying database completely.
     * @throws BasicFileOperationDatabaseException 
     */
    public void clear() throws DatabaseClosedDatabaseException, BasicFileOperationDatabaseException;

}
