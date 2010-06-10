/*
 * Created on 04.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.exception;



public class SerializationDatabaseException extends DatabaseException {

    private static final long serialVersionUID = 1L;
    
    public SerializationDatabaseException(String msg){
        super(msg);
    }

    public SerializationDatabaseException(RuntimeException e) {
        super(e);
    }
    
}
