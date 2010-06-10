/*
 * Created on 04.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.service;

import quickbase.exception.DatabaseException;


public interface IExceptionHandler<K, V> {

    public void handleException(IPersistedMap<K, V> map, DatabaseException exception);
    
}
