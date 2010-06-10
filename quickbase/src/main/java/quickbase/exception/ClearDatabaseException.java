/*
 * Created on 08.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.exception;


public class ClearDatabaseException extends DatabaseException {

    private static final long serialVersionUID = 1L;
    
    public ClearDatabaseException(DatabaseException e) {
        super(e);
    }

}
