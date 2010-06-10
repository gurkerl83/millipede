/*
 * Created on 08.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.exception;

import quickbase.internal.files.DataFileAbandonedException;

public class IgnoreExceptionsStrategy implements IExceptionHandlingStrategy {

    public void handleInvalidData(InvalidDataDatabaseException e) throws ClearDatabaseException {
        log(e);
    }

    public long handleInvalidEntry(long currentPos, long len, InvalidDataDatabaseException e) {
        log(e);
        return len;
    }

    public void handleSerializationProblem(SerializationDatabaseException e) throws ClearDatabaseException {
        log(e);
    }

    public void handleMissingFile(DataFileAbandonedException e) throws ClearDatabaseException {
        log(e);
    }

    protected void log(DatabaseException e) {
        e.printStackTrace();
    }

}
