/*
 * Created on 08.01.2008
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.files;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

import quickbase.exception.BasicFileOperationDatabaseException;



public class Lock {
    
    public static final String SUFFIX = ".lock";
    
    private File file;
    private RandomAccessFile raf;
    private FileLock lock;
    
    public Lock(File path, String name) throws BasicFileOperationDatabaseException{
        try {
            this.file = new File(path, name + SUFFIX);
            this.raf = new RandomAccessFile(file, "rw");
            this.lock = raf.getChannel().tryLock();
            if (lock == null){
                throw new BasicFileOperationDatabaseException("Could not acquire lock for " + file);
            }
        } catch (IOException e) {
            throw new BasicFileOperationDatabaseException(e);
        }
    }
    
    public void release() throws IOException{
        lock.release();
        raf.close();
        file.delete();
    }

}
