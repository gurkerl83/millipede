package quickbase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import quickbase.exception.AbortVisitException;
import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.ClearDatabaseException;
import quickbase.exception.DatabaseClosedDatabaseException;
import quickbase.exception.DatabaseException;
import quickbase.exception.IExceptionHandlingStrategy;
import quickbase.exception.InvalidDataDatabaseException;
import quickbase.exception.SerializationDatabaseException;
import quickbase.internal.consumer.CompactorConsumer;
import quickbase.internal.consumer.RecoveryConsumer;
import quickbase.internal.consumer.VisitConsumer;
import quickbase.internal.entries.AddEntry;
import quickbase.internal.entries.Entry;
import quickbase.internal.entries.HashFunction;
import quickbase.internal.entries.RemoveEntry;
import quickbase.internal.entries.RuntimeExceptionCatchingSerializerWrapper;
import quickbase.internal.files.DataFileAbandonedException;
import quickbase.internal.files.DataFiles;
import quickbase.internal.files.Lock;
import quickbase.internal.index.Index;
import quickbase.internal.index.Stats;
import quickbase.serializer.ISerializer;
import quickbase.service.IPersistedMap;
import quickbase.service.IVisitor;

/**
 * Simple, robust and efficient persistent hash-map. A simple hashmap that
 * stores its entries to a file. It is basically as fast as it gets without
 * caching values. Note that thanks to the automatic file caching most operating
 * systems do, most gets are still much faster than disk-seek time.
 * Specification: Maximum total size: Long.MAX_VALUE bytes Maximum key size:
 * Short.MAX_VALUE bytes Maximum entry size: Integer.MAX_VALUE bytes Maximum
 * number of entries: Unlimited. However, performance takes severe hits the
 * closer you get to Integer.MAX_VALUE entries as a four-byte hash is used and
 * collisions get more likely. Typical performance: 5000 reads or writes per
 * second. Less for large values. Storage overhead: about 30 bytes per entry
 * plus not yet collected deleted entries. Compaction / garbage collection: Once
 * less than 60% (can also be set to other values) of the database is occupied
 * by deleted entries, compaction takes place. Compaction copies all existing
 * entries into a new database and might take a while. Compaction requires at
 * MIN(db_size, 10MB) free disk space. Journaling system: updates are always
 * written to the end of the current database file. -> Program crashes can't
 * corrupt data. Fast writes. By default, the size of a database file is 10MB.
 * Index stays in memory, uses 8 bytes per entry if database smaller than 2GB,
 * 12 bytes per entry otherwise. -> Scalable up to millions of entries. Fast.
 * Getting an entry only requires one disk seek (if there is no hash collision).
 * 
 * @author Luzius Meisser
 * @param <K>
 *            The key class Please note that the equals and the hashcode
 *            function of the key are ignored. Quickbase looks at the bytes of
 *            the serialized keys for comparisons/hashes.
 * @param <V>
 *            The value class
 */
public class SingleMapDatabase<K, V> implements IPersistedMap<K, V> {

    public static final double LOWEST_TOLERATED_FILL_RATIO_BEFORE_COMPACT = 0.60;

    private Lock lock;
    protected Stats stats;
    protected Index index;
    private DataFiles data;
    private ISerializer<K> keySerializer;
    private ISerializer<V> valueSerializer;
    private HashFunction<K> function;
    private IExceptionHandlingStrategy strategy;
    private DatabaseClosedDatabaseException closed;

    // normally null, only there if compaction running
    private CompactorConsumer<K, V> compactor;

    public SingleMapDatabase(IExceptionHandlingStrategy strategy, File path, String name, ISerializer<K> keySerializer, ISerializer<V> valueSerializer) throws BasicFileOperationDatabaseException {
        this(new HashFunction<K>(keySerializer), strategy, path, name, keySerializer, valueSerializer, 10L * 1024 * 1024);
    }

    protected SingleMapDatabase(HashFunction<K> function, IExceptionHandlingStrategy strategy, File path, String name, ISerializer<K> keySerializer, ISerializer<V> valueSerializer, long maxFileSize) throws BasicFileOperationDatabaseException {
        path.mkdirs();
        boolean initialized = false;
        this.lock = new Lock(path, name);
        this.strategy = strategy;
        this.function = function;
        this.keySerializer = new RuntimeExceptionCatchingSerializerWrapper<K>(keySerializer);
        this.valueSerializer = new RuntimeExceptionCatchingSerializerWrapper<V>(valueSerializer);
        try {
            try {
                init(path, name, maxFileSize);
            } catch (ClearDatabaseException e) {
                internalClear(path, name);
                try {
                    init(path, name, maxFileSize);
                } catch (ClearDatabaseException e2) {
                    throw new RuntimeException(e2);
                }
            }
            initialized = true;
        } finally {
            if (!initialized) {
                try {
                    lock.release();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private void init(File path, String name, long maxFileSize) throws BasicFileOperationDatabaseException, ClearDatabaseException {
        this.data = new DataFiles(path, name, maxFileSize);
        initIndex(path, name);
        if (CompactorConsumer.hasPendingCompact(data.getPath(), data.getName())) {
            internalCompact();
        }
    }

    private void initIndex(File path, String name) throws BasicFileOperationDatabaseException, ClearDatabaseException {
        try {
            this.index = new Index(path, name, data.getMaxPos());
            this.stats = this.index.getStats();
            this.data.visit(strategy, stats.size, new RecoveryConsumer<K, V>(function, index));
        } catch (AbortVisitException e) {
            // recovery consumer doesn't throw this
            throw new RuntimeException(e);
        }
    }

    public synchronized void put(K key, V value) throws DatabaseClosedDatabaseException, SerializationDatabaseException, BasicFileOperationDatabaseException {
        checkState();
        int hash = function.getHash(key);
        long existing = index.getPosition(hash);
        AddEntry entry = new AddEntry(keySerializer.toBytes(key), valueSerializer.toBytes(value), existing);
        entry.doStats(stats);
        try {
            long position = data.put(entry);
            index.put(hash, position);
            considerCompact();
        } catch (DataFileAbandonedException e) {
            // should not happen as long as compaction happens synchronously
            throw new RuntimeException(e);
        } catch (ClearDatabaseException e) {
            clear();
        }
    }

    public synchronized boolean contains(K key) throws DatabaseClosedDatabaseException, SerializationDatabaseException, BasicFileOperationDatabaseException {
        checkState();
        stats.contains++;
        stats.gets--;
        return get(key) != null;
    }

    public synchronized V get(K key) throws SerializationDatabaseException, DatabaseClosedDatabaseException, BasicFileOperationDatabaseException {
        Entry entry = getEntry(key);
        byte[] bytes = entry == null ? null : entry.getValue();
        return bytes == null ? null : valueSerializer.fromBytes(bytes, 0);
    }

    private synchronized Entry getEntry(K key) throws SerializationDatabaseException, DatabaseClosedDatabaseException, BasicFileOperationDatabaseException {
        checkState();
        stats.gets++;
        int hash = function.getHash(key);
        long pos = index.getPosition(hash);
        if (pos == -1) {
            return null;
        } else {
            try {
                return get(pos, keySerializer.toBytes(key));
            } catch (DataFileAbandonedException e) {
                // should not happen as long as compaction happens synchronously
                throw new RuntimeException(e);
            }
        }
    }

    private Entry get(long pos, byte[] bytesK) throws DataFileAbandonedException, BasicFileOperationDatabaseException, DatabaseClosedDatabaseException {
        try {
            try {
                Entry entry = data.get(pos);
                if (entry.hasKey(bytesK)) {
                    return entry;
                } else {
                    pos = entry.getPrevPos();
                    if (pos == -1) {
                        return null;
                    } else {
                        return get(pos, bytesK);
                    }
                }
            } catch (InvalidDataDatabaseException e) {
                strategy.handleInvalidData(e);
                return null;
            }
        } catch (ClearDatabaseException e1) {
            clear();
            return null;
        }
    }

    public synchronized void remove(K key) throws DatabaseClosedDatabaseException, SerializationDatabaseException, BasicFileOperationDatabaseException {
        checkState();
        int hash = function.getHash(key);
        long pos = index.getPosition(hash);
        if (pos != -1) {
            try {
                RemoveEntry entry = new RemoveEntry(keySerializer.toBytes(key), pos);
                entry.doStats(stats);
                pos = data.put(entry);
                index.put(hash, pos);
                considerCompact();
            } catch (DataFileAbandonedException e) {
                throw new RuntimeException(e);
            } catch (ClearDatabaseException e) {
                clear();
            }
        }
    }

    private void considerCompact() throws BasicFileOperationDatabaseException, ClearDatabaseException {
        if (stats.getFillRatio() < LOWEST_TOLERATED_FILL_RATIO_BEFORE_COMPACT && stats.addEntries > 100) {
            internalCompact();
        }
    }

    public synchronized void compact() throws DatabaseClosedDatabaseException, BasicFileOperationDatabaseException {
        checkState();
        try {
            internalCompact();
        } catch (ClearDatabaseException e) {
            clear();
        }
    }

    private synchronized void internalCompact() throws BasicFileOperationDatabaseException, ClearDatabaseException {
        assert compactor == null;
        try {
            compactor = new CompactorConsumer<K, V>(function, strategy, index, data);
            // make sure index gets rebuilt on next start if compaction doesn't
            // complete
            index.deleteFile();
            try {
                data.visitAndClear(strategy, compactor);
                index = compactor.getNewIndex();
                stats = index.getStats();
                data = compactor.getNewData();
                index.save(data.getMaxPos());
            } catch (ClearDatabaseException e) {
                compactor.destroy();
                throw e;
            } catch (AbortVisitException e) {
                // compactor consumer doesn't throw this
                throw new RuntimeException(e);
            }
        } finally {
            compactor = null;
        }
    }

    /**
     * Items are visited in chronological (by insertion date) order, starting at
     * startKey. Let your visitor throw an AbortVisitException to stop the
     * visit.
     * 
     * @param startKey
     * @param visitor
     * @throws DatabaseClosedDatabaseException
     * @throws BasicFileOperationDatabaseException
     * @throws AbortVisitException
     * @throws SerializationDatabaseException 
     */
    public synchronized V visit(K startKey, IVisitor<K, V> visitor) throws DatabaseClosedDatabaseException, BasicFileOperationDatabaseException, SerializationDatabaseException {
        try {
            try {
                checkState();
                int hash = function.getHash(startKey);
                long pos = index.getPosition(hash);
                if (pos == -1) {
                    return null;
                } else {
                    byte[] keyBytes = keySerializer.toBytes(startKey);
                    Entry entry = data.get(pos);
                    while (!entry.hasKey(keyBytes) && entry != null && entry.getPrevPos() != -1) {
                        pos = entry.getPrevPos();
                        entry = data.get(pos);
                    }
                    if (entry != null && entry.hasKey(keyBytes)) {
                        try {
                            VisitConsumer<K, V> journey = new VisitConsumer<K, V>(function, strategy, keySerializer, valueSerializer, visitor, index, data);
                            data.visit(strategy, pos, journey);
                        } catch (AbortVisitException e) {
                        }
                        return valueSerializer.fromBytes(entry.getValue(), 0);
                    } else {
                        return null;
                    }
                }
            } catch (DataFileAbandonedException e) {
                strategy.handleMissingFile(e);
                return null;
            } catch (InvalidDataDatabaseException e) {
                strategy.handleInvalidData(e);
                return null;
            }
        } catch (ClearDatabaseException e) {
            clear();
            return null;
        }
    }

    public void visit(IVisitor<K, V> visitor) throws DatabaseClosedDatabaseException, BasicFileOperationDatabaseException {
        try {
            visit(0, visitor);
        } catch (AbortVisitException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void visit(long startPos, IVisitor<K, V> visitor) throws DatabaseClosedDatabaseException, BasicFileOperationDatabaseException, AbortVisitException {
        try {
            checkState();
            VisitConsumer<K, V> journey = new VisitConsumer<K, V>(function, strategy, keySerializer, valueSerializer, visitor, index, data);
            data.visit(strategy, 0, journey);
        } catch (ClearDatabaseException e) {
            clear();
        }
    }

    // /**
    // * Values will not be read -> faster (e.g. 30%).
    // */
    // public synchronized void visitKeys(IVisitor<K, V> visitor) throws
    // DatabaseClosedDatabaseException, BasicFileOperationDatabaseException {
    // try {
    // checkState();
    // VisitConsumer<K, V> journey = new VisitConsumer<K, V>(strategy,
    // keySerializer, valueSerializer, visitor, index, data){
    // @Override
    // public boolean needsValues() {
    // return false;
    // }
    // };
    // data.visit(strategy, 0, journey);
    // } catch (ClearDatabaseException e) {
    // clear();
    // }
    // }

    protected void checkState() throws DatabaseClosedDatabaseException {
        if (closed != null) {
            throw closed;
        }
    }

    public void close() {
        close(new DatabaseClosedDatabaseException("Closed by user"));
    }

    public synchronized void close(DatabaseException reason) {
        try {
            if (lock != null) {
                lock.release();
            }
        } catch (IOException e1) {
        }
        try {
            if (data != null) {
                index.save(data.close());
            }
        } catch (BasicFileOperationDatabaseException e) {
        }
        lock = null;
        index = null;
        stats = null;
        data = null;
        keySerializer = null;
        valueSerializer = null;
        closed = DatabaseClosedDatabaseException.create(reason);
    }

    public Stats getStats() {
        return stats;
    }

    public Collection<V> values() throws DatabaseClosedDatabaseException, BasicFileOperationDatabaseException {
        final ArrayList<V> list = new ArrayList<V>();
        visit(new IVisitor<K, V>() {

            public void visit(K key, V value) {
                list.add(value);
            }
        });
        return list;
    }

    public synchronized void clear() throws DatabaseClosedDatabaseException, BasicFileOperationDatabaseException {
        checkState();
        try {
            File path = data.getPath();
            String name = data.getName();
            internalClear(path, name);
            init(path, name, data.getMaxFileSize());
        } catch (ClearDatabaseException e) {
            throw new RuntimeException(e);
        }
        checkState();
    }

    private synchronized void internalClear(File path, String name) throws BasicFileOperationDatabaseException {
        CompactorConsumer.destroyFiles(path, name);
        index.deleteFile();
        data.destroy();
    }

    public void abandon() throws BasicFileOperationDatabaseException {
        File path = data.getPath();
        String name = data.getName();
        internalClear(path, name);
        try {
            lock.release();
        } catch (IOException e) {
        }
    }

    // public void check() {
    // index.index.index.forEachValue(new TIntProcedure() {
    //
    // public boolean execute(int value) {
    // try {
    // data.get(value);
    // } catch (DataFileAbandonedException e) {
    // throw new RuntimeException(e);
    // } catch (BasicFileOperationDatabaseException e) {
    // throw new RuntimeException(e);
    // } catch (InvalidDataDatabaseException e) {
    // throw new RuntimeException(e);
    // }
    // return true;
    // }
    // });
    // }

}
