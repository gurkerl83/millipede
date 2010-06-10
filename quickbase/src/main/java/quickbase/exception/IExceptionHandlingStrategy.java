/*
 * Created on 08.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.exception;

import quickbase.internal.files.DataFileAbandonedException;



public interface IExceptionHandlingStrategy {

    public long handleInvalidEntry(long currentPos, long len, InvalidDataDatabaseException e) throws ClearDatabaseException;

    public void handleInvalidData(InvalidDataDatabaseException e) throws ClearDatabaseException;

    public void handleSerializationProblem(SerializationDatabaseException e) throws ClearDatabaseException;

    public void handleMissingFile(DataFileAbandonedException e) throws ClearDatabaseException;

}
