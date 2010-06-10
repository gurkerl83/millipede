/*
 * Created on 08.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.exception;



public class DatabaseException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public DatabaseException(String string) {
        super(string);
    }

    public DatabaseException(Exception e) {
        super(e);
    }

}
