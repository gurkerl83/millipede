/*
 * Created on 08.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.consumer;

import quickbase.exception.AbortVisitException;
import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.ClearDatabaseException;
import quickbase.exception.IExceptionHandlingStrategy;
import quickbase.exception.InvalidDataDatabaseException;
import quickbase.exception.SerializationDatabaseException;
import quickbase.internal.entries.AddEntry;
import quickbase.internal.entries.Entry;
import quickbase.internal.entries.HashFunction;
import quickbase.internal.files.DataFileAbandonedException;
import quickbase.internal.files.DataFiles;
import quickbase.internal.index.Index;

public abstract class AbstractConsumer<K, V> implements IEntryConsumer {

    private IExceptionHandlingStrategy strategy;
    private Index index;
    protected HashFunction hash;
    private DataFiles original;

    public AbstractConsumer(HashFunction hash, IExceptionHandlingStrategy strategy, Index index, DataFiles data) {
        this.hash = hash;
        this.strategy = strategy;
        this.index = index;
        this.original = data;
    }

    public boolean consume(Entry entry, long pos) throws BasicFileOperationDatabaseException, ClearDatabaseException, SerializationDatabaseException, AbortVisitException {
        if (entry instanceof AddEntry) {
            long filePos = index.getPosition(hash.getHash(entry));
            checkLater((AddEntry) entry, pos, filePos);
        }
        return true;
    }
    
    private void checkLater(AddEntry entry, long pos, long filePos) throws BasicFileOperationDatabaseException, ClearDatabaseException, SerializationDatabaseException, AbortVisitException {
        if (filePos == pos) {
            visit(entry);
        } else if (filePos > pos) {
            try {
                Entry later = original.get(filePos);
                if (later.hasKey(entry.getKey())) {
                    // we will get to that entry again later, skip it now
                } else {
                    filePos = later.getPrevPos();
                    if (filePos < 0) {
                        throw new InvalidDataDatabaseException("Invalid file pos: " + filePos);
                    } else {
                        checkLater(entry, pos, filePos);
                    }
                }
            } catch (InvalidDataDatabaseException e) {
                strategy.handleInvalidData(e);
            } catch (DataFileAbandonedException e) {
                strategy.handleMissingFile(e);
            }
        } else {
            strategy.handleInvalidData(new InvalidDataDatabaseException(hash.getHash(entry) + " has pos " + filePos + " in index but found entry later at " + pos));
        }
    }

    protected abstract void visit(AddEntry entry) throws SerializationDatabaseException, BasicFileOperationDatabaseException, AbortVisitException;

    public void flush() throws BasicFileOperationDatabaseException {
    }
    
    public boolean needsValues() {
        return true;
    }

}
