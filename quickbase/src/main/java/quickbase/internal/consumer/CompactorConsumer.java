/*
 * Created on 01.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.consumer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import quickbase.exception.AbortVisitException;
import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.ClearDatabaseException;
import quickbase.exception.IExceptionHandlingStrategy;
import quickbase.exception.SerializationDatabaseException;
import quickbase.exception.StrategyWrapper;
import quickbase.internal.entries.AddEntry;
import quickbase.internal.entries.HashFunction;
import quickbase.internal.files.DataFileAbandonedException;
import quickbase.internal.files.DataFiles;
import quickbase.internal.index.Index;


public class CompactorConsumer<K, V> extends AbstractConsumer<K, V> {

    private static final String PREFIX = "COMP-";
    private static final int MAX_BUFFER_SIZE = 6000000;

    private String name;
    private Index newIndex;
    private DataFiles compacted;

    private int bufferSize;
    private ArrayList<AddEntry> writeBuffer;

    public CompactorConsumer(HashFunction<K> hash, IExceptionHandlingStrategy strategy, Index index, DataFiles data) throws BasicFileOperationDatabaseException, ClearDatabaseException {
        super(hash, new StrategyWrapper(strategy) {
            public void handleMissingFile(DataFileAbandonedException e) throws ClearDatabaseException {
                // ignore, we have already dealt with all entries from that file
                // and earlier
            }
        }, index, data);
        this.name = data.getName();
        this.compacted = new DataFiles(data.getPath(), PREFIX + data.getName(), data.getMaxFileSize());
        this.newIndex = new Index(data.getPath(), PREFIX + data.getName(), compacted.getMaxPos());
        this.writeBuffer = new ArrayList<AddEntry>(100);
        try {
            this.compacted.visit(strategy, newIndex.getStats().size, new RecoveryConsumer<K, V>(hash, newIndex));
        } catch (ClearDatabaseException e) {
            destroy();
            throw e;
        } catch (AbortVisitException e) {
            //recovery consumer doesn't throw this
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void visit(AddEntry entry) throws SerializationDatabaseException, BasicFileOperationDatabaseException {
        bufferSize += entry.getSize();
        writeBuffer.add(entry);
        if (bufferSize > MAX_BUFFER_SIZE) {
            flush();
        }
    }

    @Override
    public void flush() throws BasicFileOperationDatabaseException {
        try {
            for (AddEntry entry : writeBuffer) {
                long prevPos = newIndex.getPosition(hash.getHash(entry));
                AddEntry newEntry = new AddEntry(entry.getKey(), entry.getValue(), prevPos);
                newEntry.doStats(newIndex.getStats());
                long newPos = compacted.put(newEntry);
                newIndex.put(hash.getHash(newEntry), newPos);
            }
            writeBuffer.clear();
            bufferSize = 0;
        } catch (DataFileAbandonedException e) {
            throw new RuntimeException(e);
        }
    }

    public DataFiles getNewData() throws BasicFileOperationDatabaseException {
        compacted.rename(name);
        return compacted;
    }

    public Index getNewIndex() {
        newIndex.rename(name);
        newIndex.getStats().replacedEntries = 0;
        return newIndex;
    }

    public void destroy() throws BasicFileOperationDatabaseException {
        newIndex.deleteFile();
        compacted.destroy();
    }

    public static void destroyFiles(File path, final String name) throws BasicFileOperationDatabaseException {
        File[] files = path.listFiles(new FilenameFilter() {

            private String prefix = PREFIX + name;

            public boolean accept(File dir, String name) {
                return name.startsWith(prefix);
            }
        });
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    throw new BasicFileOperationDatabaseException("Could not delete " + file);
                }
            }
        }
    }

    public static boolean hasPendingCompact(File path, final String name) {
        File[] files = path.listFiles(new FilenameFilter() {

            private String prefix = PREFIX + name;

            public boolean accept(File dir, String name) {
                return name.startsWith(prefix);
            }
        });
        return files != null && files.length > 0;
    }

}
