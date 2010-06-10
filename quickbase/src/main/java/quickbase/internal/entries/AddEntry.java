package quickbase.internal.entries;

import java.io.IOException;
import java.io.RandomAccessFile;

import quickbase.internal.index.Stats;


public class AddEntry extends Entry {

    private int length;
    private byte[] value;

    public AddEntry(byte[] key, byte[] value, long prevPos) {
        super(key, prevPos);
        this.value = value;
    }

    public AddEntry(RandomAccessFile raf, boolean readFully) throws IOException {
        super(raf);
        this.length = raf.readInt();
        if (readFully) {
            value = new byte[length];
            raf.readFully(value);
        }
    }

    public void write(RandomAccessFile raf) throws IOException {
        super.write(raf);
        raf.writeInt(value.length);
        raf.write(value);
    }

    public byte getType() {
        return ADD;
    }

    public byte[] getValue() {
        return value;
    }

    public void doStats(Stats stats) {
        stats.addEntries++;
        if (getPrevPos() != -1) {
            stats.replacedEntries++;
        }
    }

    @Override
    public int getSize() {
        return super.getSize() + length + 4;
    }

}
