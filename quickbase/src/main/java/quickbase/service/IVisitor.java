/*
 * Created on 01.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.service;

import quickbase.exception.AbortVisitException;
import quickbase.exception.SerializationDatabaseException;



public interface IVisitor<K, V> {
    
    public void visit(K key, V value) throws AbortVisitException, SerializationDatabaseException;

}
