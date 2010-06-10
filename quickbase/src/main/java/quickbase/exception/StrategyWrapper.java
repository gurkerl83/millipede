/*
 * Created on 08.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.exception;

import quickbase.internal.files.DataFileAbandonedException;


public class StrategyWrapper implements IExceptionHandlingStrategy {
    
    private IExceptionHandlingStrategy strategy;

    public StrategyWrapper(IExceptionHandlingStrategy strategy) {
        this.strategy = strategy;
    }

    public void handleInvalidData(InvalidDataDatabaseException e) throws ClearDatabaseException {
        strategy.handleInvalidData(e);
    }

    public long handleInvalidEntry(long currentPos, long len, InvalidDataDatabaseException e) throws ClearDatabaseException {
        return strategy.handleInvalidEntry(currentPos, len, e);
    }

    public void handleMissingFile(DataFileAbandonedException e) throws ClearDatabaseException {
        strategy.handleMissingFile(e);
    }

    public void handleSerializationProblem(SerializationDatabaseException e) throws ClearDatabaseException {
        strategy.handleSerializationProblem(e);
    }

}
