/*
 * Created on 01.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.files;

import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.IExceptionHandlingStrategy;
import quickbase.internal.consumer.IEntryConsumer;
import quickbase.internal.entries.Entry;


public class AbandonedDataFile extends DataFile {

    public AbandonedDataFile() {
        super();
    }
    
    @Override
    public long getSize() {
        throw new RuntimeException();
    }

    @Override
    public int put(Entry entry, long maxLen) throws DataFileAbandonedException {
        throw new DataFileAbandonedException();
    }

    @Override
    public boolean visit(IExceptionHandlingStrategy strategy, long offset, long startPos, IEntryConsumer consumer) throws DataFileAbandonedException {
        throw new DataFileAbandonedException();
    }

    @Override
    public void close() {
    }

    @Override
    public void rename(String newName) throws BasicFileOperationDatabaseException {
    }
    
    @Override
    public void destroy() throws BasicFileOperationDatabaseException {
    }

    @Override
    public Entry get(long pos) throws DataFileAbandonedException {
        throw new DataFileAbandonedException();
    }

}
