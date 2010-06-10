/*
 * Created on 08.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.exception;

import quickbase.internal.files.DataFileAbandonedException;


public class ClearDatabaseOnExceptionsStrategy implements IExceptionHandlingStrategy {
    
    public void handleInvalidData(InvalidDataDatabaseException e) throws ClearDatabaseException {
        throw new ClearDatabaseException(e);
    }

    public long handleInvalidEntry(long currentPos, long len, InvalidDataDatabaseException e) throws ClearDatabaseException {
        throw new ClearDatabaseException(e);
    }

    public void handleSerializationProblem(SerializationDatabaseException e) throws ClearDatabaseException {
        throw new ClearDatabaseException(e);
    }

    public void handleMissingFile(DataFileAbandonedException e) throws ClearDatabaseException {
        throw new ClearDatabaseException(e);
    }

}
