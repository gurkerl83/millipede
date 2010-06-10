/*
 * Created on 08.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.exception;



public class InvalidDataDatabaseException extends DatabaseException {

    private static final long serialVersionUID = 1L;
    
    public InvalidDataDatabaseException(String string) {
        super(string);
    }

    public InvalidDataDatabaseException(Exception e) {
        super(e);
    }

}
