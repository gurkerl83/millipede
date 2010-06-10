package quickbase.internal.index;

import gnu.trove.TIntProcedure;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import quickbase.exception.BasicFileOperationDatabaseException;


public class Index {

    public static final byte VERSION = 1;

    private static final String SUFFIX = ".index";

    private AdaptingIntLongMap index;
    private File indexFile;
    private Stats stats;

    public Index(File path, String name, long maxPos) {
        indexFile = new File(path, name + SUFFIX);
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(indexFile), 100000));
            try {
                readContent(dis);
            } finally {
                dis.close();
            }
            if (maxPos != stats.size) {
                // inconsistency -> rebuild index
                clear();
            }
        } catch (IOException e) {
            clear();
        }
    }

    private void clear() {
        index = new AdaptingIntLongMap(32);
        stats = new Stats();
    }

    private void readContent(DataInputStream dis) throws IOException {
        byte v = dis.readByte();
        if (v == VERSION) {
            stats = new Stats(dis);
            index = new AdaptingIntLongMap(dis);
        } else {
            throw new IOException("Invalid version: " + v);
        }
    }

    public Stats getStats() {
        return stats;
    }

    public long getPosition(int hash) {
        return index.get(hash);
    }

    public void put(int hash, long position) {
        index.put(hash, position);
    }

    public void save(long size) throws BasicFileOperationDatabaseException {
        try {
            final DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(indexFile)));
            try {
                dos.writeByte(VERSION);
                stats.size = size;
                stats.write(dos);
                index.write(dos);
            } finally {
                dos.close();
            }
        } catch (IOException e) {
            throw new BasicFileOperationDatabaseException(e);
        }
    }

    public void rename(String name) {
        this.indexFile = new File(indexFile.getParent(), name + SUFFIX);
    }

    public void deleteFile() throws BasicFileOperationDatabaseException {
        if (indexFile.exists() && !indexFile.delete()) {
            throw new BasicFileOperationDatabaseException("Could not delete " + indexFile + ", " + indexFile.exists());
        }
    }

    public void visitIndex(TIntProcedure visitor) {
        index.visitKeys(visitor);
    }

}
