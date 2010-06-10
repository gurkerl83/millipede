package quickbase.internal.entries;

import java.io.IOException;
import java.io.RandomAccessFile;

import quickbase.exception.InvalidDataDatabaseException;
import quickbase.internal.index.Stats;


public abstract class Entry {

    public static final byte ADD = 1;
    public static final byte REMOVE = 2;

    private long prevPos;
    private byte[] key;

    public Entry(RandomAccessFile raf) throws IOException {
        prevPos = raf.readLong();
        key = new byte[raf.readShort()];
        raf.readFully(key);
    }

    public Entry(byte[] key, long pos) {
        this.prevPos = pos;
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }

    public long getPrevPos() {
        return prevPos;
    }

    public abstract void doStats(Stats stats);

    @Override
    public int hashCode() {
        throw new RuntimeException("Don't put me in a hash map");
    }
    
    @Override
    public boolean equals(Object other){
        return ((Entry)other).hasKey(key);
    }

    public boolean hasKey(byte[] bytesK) {
        if (key.length == bytesK.length) {
            for (int i = 0; i < key.length; i++) {
                if (key[i] != bytesK[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static Entry readEntry(RandomAccessFile raf, boolean valueNeeded) throws IOException, InvalidDataDatabaseException {
        byte type = raf.readByte();
        if (type == ADD) {
            return new AddEntry(raf, valueNeeded);
        } else if (type == REMOVE){
            return new RemoveEntry(raf);
        } else {
            throw new InvalidDataDatabaseException("Invalid entry type: " + type);
        }
    }

    public abstract byte getType();

    public void write(RandomAccessFile raf) throws IOException {
        raf.writeByte(getType());
        raf.writeLong(prevPos);
        raf.writeShort((short) key.length);
        raf.write(key);
    }

    public abstract byte[] getValue();

    public int getSize() {
        return key.length + 11;
    }

}
