/*
 * Created on 01.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.files;

import quickbase.exception.DatabaseException;


public class DataFileAbandonedException extends DatabaseException {
    
    private static final long serialVersionUID = 1L;

    public DataFileAbandonedException() {
        super("Data file missing");
    }

}
