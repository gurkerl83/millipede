/*
 * Created on 01.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.files;

import java.io.File;
import java.io.FilenameFilter;

import quickbase.exception.AbortVisitException;
import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.ClearDatabaseException;
import quickbase.exception.IExceptionHandlingStrategy;
import quickbase.exception.InvalidDataDatabaseException;
import quickbase.internal.consumer.IEntryConsumer;
import quickbase.internal.entries.Entry;


public class DataFiles {


    private File path;
    private String name;
    private long maxFileSize;
    private DataFile[] dataFiles;

    public DataFiles(File path, final String name, long maxFileSize) throws BasicFileOperationDatabaseException {
        this.path = path;
        this.name = name;
        this.maxFileSize = maxFileSize;
        File[] files = path.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String fname) {
                return fname.startsWith(name) && fname.endsWith(DataFile.SUFFIX);
            }
        });
        this.dataFiles = new DataFile[getCount(files, name)];
        for (int i = 0; i < dataFiles.length; i++) {
            File file = new File(path, name + i + DataFile.SUFFIX);
            if (file.exists() || (i == dataFiles.length - 1)) {
                dataFiles[i] = new DataFile(file);
            } else {
                dataFiles[i] = new AbandonedDataFile();
            }
        }
    }

    private int getCount(File[] files, String prefix) {
        int max = 0;
        for (File file : files) {
            String name = file.getName();
            String number = name.substring(prefix.length(), name.length() - DataFile.SUFFIX.length());
            try {
                max = Math.max(Integer.parseInt(number), max);
            } catch (NumberFormatException e) {
            }
        }
        return max + 1;
    }

    public long getMaxPos() {
        return getOffset() + getCurrent().getSize();
    }

    private long getOffset() {
        return (dataFiles.length - 1) * maxFileSize;
    }

    private DataFile getCurrent() {
        return dataFiles[dataFiles.length - 1];
    }

    public void visitAndClear(IExceptionHandlingStrategy strategy, IEntryConsumer consumer) throws BasicFileOperationDatabaseException, ClearDatabaseException, AbortVisitException {
        for (int i = 0; i < dataFiles.length; i++) {
            try {
                dataFiles[i].visit(strategy, i * maxFileSize, 0, consumer);
                dataFiles[i].destroy();
                dataFiles[i] = new AbandonedDataFile();
            } catch (DataFileAbandonedException e) {
                // ignore, assume already compacted
            }
        }
    }

    public void visit(IExceptionHandlingStrategy strategy, long startPos, IEntryConsumer consumer) throws BasicFileOperationDatabaseException, ClearDatabaseException, AbortVisitException {
        int number = (int) (startPos / maxFileSize);
        if (number < dataFiles.length) {
            boolean more = true;
            for (int i = number; more && i < dataFiles.length; i++) {
                try {
                    more = dataFiles[i].visit(strategy, i * maxFileSize, startPos, consumer);
                } catch (DataFileAbandonedException e) {
                    strategy.handleMissingFile(e);
                }
                startPos = 0;
            }
        }
    }

    public long put(Entry entry) throws DataFileAbandonedException, BasicFileOperationDatabaseException {
        try {
            long pos = getCurrent().put(entry, maxFileSize);
            return getOffset() + pos;
        } catch (MaxFileLenReachedException e) {
            addFile();
            return put(entry);
        }
    }

    private void addFile() throws BasicFileOperationDatabaseException {
        DataFile[] newFiles = new DataFile[dataFiles.length + 1];
        System.arraycopy(dataFiles, 0, newFiles, 0, dataFiles.length);
        newFiles[dataFiles.length] = new DataFile(new File(path, name + dataFiles.length + DataFile.SUFFIX));
        dataFiles = newFiles;
    }

    public Entry get(long pos) throws DataFileAbandonedException, BasicFileOperationDatabaseException, InvalidDataDatabaseException {
        int number = (int) (pos / maxFileSize);
        return dataFiles[number].get(pos % maxFileSize);
    }

    public long close() {
        for (DataFile file : dataFiles) {
            file.close();
        }
        return getOffset() + dataFiles[dataFiles.length - 1].getSize();
    }

    public String getName() {
        return name;
    }

    public File getPath() {
        return path;
    }

    public void rename(String newName) throws BasicFileOperationDatabaseException {
        for (int i = 0; i < dataFiles.length; i++) {
            if (dataFiles[i] != null) {
                // TODO: undo rename in case of exception
                dataFiles[i].rename(newName + i);
            }
        }
        this.name = newName;
    }

    public void destroy() throws BasicFileOperationDatabaseException {
        for (int i = 0; i < dataFiles.length; i++) {
            dataFiles[i].destroy();
        }
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }
    
}
