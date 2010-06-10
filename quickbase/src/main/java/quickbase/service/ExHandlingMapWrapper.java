/*
 * Created on 08.09.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package quickbase.service;

import java.util.ArrayList;
import java.util.Collection;

import quickbase.exception.DatabaseException;


public abstract class ExHandlingMapWrapper<K, V> {

    private IPersistedMap<K, V> wrapped;
    
    public ExHandlingMapWrapper(IPersistedMap<K, V> wrapped) {
        this.wrapped = wrapped;
    }

    public abstract void handleException(DatabaseException e);

    public boolean contains(K key) {
        try {
            return wrapped.contains(key);
        } catch (DatabaseException e) {
            handleException(e);
            return false;
        }
    }

    public V get(K key) {
        try {
            return wrapped.get(key);
        } catch (DatabaseException e) {
            handleException(e);
            return null;
        }
    }

    public void put(K key, V value) {
        try {
            wrapped.put(key, value);
        } catch (DatabaseException e) {
            handleException(e);
        }
    }

    public void remove(K key) {
        try {
            wrapped.remove(key);
        } catch (DatabaseException e) {
            handleException(e);
        }
    }

    public Collection<V> values() {
        try {
            return wrapped.values();
        } catch (DatabaseException e) {
            handleException(e);
            return new ArrayList<V>();
        }
    }

    public void visit(IVisitor<K, V> visitor) {
        try {
            wrapped.visit(visitor);
        } catch (DatabaseException e) {
            handleException(e);
        }
    }

}
