/*
 * Created on 12.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.exception;



public class InvalidIndexDatabaseException extends DatabaseException {
    
    private static final long serialVersionUID = 1L;

    public InvalidIndexDatabaseException(Exception e) {
        super(e);
    }

    public InvalidIndexDatabaseException(String string) {
        super(string);
    }

}
