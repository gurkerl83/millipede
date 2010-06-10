/*
 * Created on 07.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.exception;



public class DatabaseClosedDatabaseException extends DatabaseException {
    
    private static final long serialVersionUID = 1L;
    
    public DatabaseClosedDatabaseException(DatabaseException e) {
        super(e);
    }

    public DatabaseClosedDatabaseException(String string) {
        super(string);
    }

    public static DatabaseClosedDatabaseException create(DatabaseException reason) {
        if (reason instanceof DatabaseClosedDatabaseException){
            return (DatabaseClosedDatabaseException)reason;
        } else {
            return new DatabaseClosedDatabaseException(reason);
        }
    }

}
