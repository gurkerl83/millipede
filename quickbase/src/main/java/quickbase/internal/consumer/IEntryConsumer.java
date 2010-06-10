/*
 * Created on 01.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.consumer;

import quickbase.exception.AbortVisitException;
import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.ClearDatabaseException;
import quickbase.exception.SerializationDatabaseException;
import quickbase.internal.entries.Entry;



public interface IEntryConsumer {
    
    public boolean consume(Entry entry, long pos) throws BasicFileOperationDatabaseException, ClearDatabaseException, SerializationDatabaseException, AbortVisitException;

    public void flush() throws BasicFileOperationDatabaseException;

    public boolean needsValues();

}
