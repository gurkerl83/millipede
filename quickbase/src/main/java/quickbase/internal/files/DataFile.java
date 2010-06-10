package quickbase.internal.files;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import quickbase.exception.AbortVisitException;
import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.ClearDatabaseException;
import quickbase.exception.IExceptionHandlingStrategy;
import quickbase.exception.InvalidDataDatabaseException;
import quickbase.exception.SerializationDatabaseException;
import quickbase.internal.consumer.IEntryConsumer;
import quickbase.internal.entries.Entry;


public class DataFile {

    public static final String SUFFIX = ".db";

    private File file;
    private RandomAccessFile raf;

    public DataFile(File file) throws BasicFileOperationDatabaseException {
        assert file.getName().endsWith(SUFFIX);
        this.file = file;
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new BasicFileOperationDatabaseException("Could create file " + file + ", cause: " + e.getMessage());
        }
    }

    DataFile() {
    }

    public long getSize() {
        return file.length();
    }

    private RandomAccessFile getRaf() throws BasicFileOperationDatabaseException {
        if (raf == null) {
            try {
                raf = new RandomAccessFile(file, "rw");
            } catch (FileNotFoundException e) {
                throw new BasicFileOperationDatabaseException(e);
            }
        }
        return raf;
    }

    @SuppressWarnings("unused")
    public int put(Entry entry, long maxLen) throws MaxFileLenReachedException, DataFileAbandonedException, BasicFileOperationDatabaseException {
        try {
            RandomAccessFile raf = getRaf();
            long end = raf.length();
            if (end >= maxLen) {
                throw new MaxFileLenReachedException();
            } else {
                raf.seek(end);
                entry.write(raf);
                return (int) end;
            }
        } catch (IOException e) {
            throw new BasicFileOperationDatabaseException(e);
        }
    }

    @SuppressWarnings("unused")
    public boolean visit(IExceptionHandlingStrategy strategy, long offset, long currentPos, IEntryConsumer consumer) throws DataFileAbandonedException, BasicFileOperationDatabaseException, ClearDatabaseException, AbortVisitException {
        boolean more = true;
        boolean valueNeeded = consumer.needsValues();
        RandomAccessFile raf = getRaf();
        try {
            try {
                long len = raf.length();
                while (currentPos < len && more) {
                    assert currentPos >= 0;
                    raf.seek(currentPos);
                    try {
                        Entry entry = Entry.readEntry(raf, valueNeeded);
                        long nextPos = currentPos + entry.getSize();
                        // remember nextPos already here, who knows what
                        // consumer does to the file...
//                        long nextPos = raf.getFilePointer();
//                        assert testPos == nextPos;
                        try {
                            more = consumer.consume(entry, offset + currentPos);
                        } catch (SerializationDatabaseException e) {
                            strategy.handleSerializationProblem(e);
                        }
                        currentPos = nextPos;
                    } catch (InvalidDataDatabaseException e) {
                        currentPos = strategy.handleInvalidEntry(currentPos, len, e);
                    }
                }
            } catch (EOFException e) {
                // assume last entry incomplete -> truncate
                raf.setLength(currentPos);
            }
        } catch (IOException e) {
            throw new BasicFileOperationDatabaseException(e);
        } finally {
            consumer.flush();
        }
        return more;
    }

    @SuppressWarnings("unused")
    public Entry get(long pos) throws DataFileAbandonedException, BasicFileOperationDatabaseException, InvalidDataDatabaseException {
        try {
            RandomAccessFile raf = getRaf();
            raf.seek(pos);
            return Entry.readEntry(raf, true);
        } catch (EOFException e) {
            //maybe index wrong?
            throw new InvalidDataDatabaseException(e);
        } catch (IOException e) {
            throw new BasicFileOperationDatabaseException(e);
        }
    }

    public void close() {
        if (raf != null) {
            try {
                raf.close();
            } catch (IOException e) {
                // ignore
            }
            raf = null;
        }
    }

    public void rename(String newName) throws BasicFileOperationDatabaseException {
        close();
        File neu = new File(file.getParent(), newName + SUFFIX);
        if (neu.exists()) {
            neu.delete();
        }
        boolean success = file.renameTo(neu);
        if (!success) {
            throw new BasicFileOperationDatabaseException("Could not rename " + neu);
        }
        this.file = neu;
        assert file.getName().endsWith(SUFFIX);
    }

    public void destroy() throws BasicFileOperationDatabaseException {
        close();
        if (!file.delete()) {
            throw new BasicFileOperationDatabaseException("Could not delete: " + file);
        }
    }

}
