/*
 * Created on 07.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.exception;

import java.io.IOException;


/**
 * Thrown when there was an exception during a basic file operation.
 * E.g.: when a file is unavailable that was there before, when we cannot
 * access a file, cannot obtain the lock, we cannot write a file, etc.
 * 
 * @author Luzius Meisser
 */
public class BasicFileOperationDatabaseException extends DatabaseException {

    private static final long serialVersionUID = 1L;

    public BasicFileOperationDatabaseException(String string) {
        super(string);
    }

    public BasicFileOperationDatabaseException(IOException e) {
        super(e);
    }

}
