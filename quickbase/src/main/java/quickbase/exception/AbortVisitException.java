/*
 * Created on 14.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.exception;


public class AbortVisitException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public AbortVisitException(){
    }
    
    public AbortVisitException(String message){
        super(message);
    }

    public AbortVisitException(Exception cause){
        super(cause);
    }

}
