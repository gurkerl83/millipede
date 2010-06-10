package quickbase.internal.entries;

import java.io.IOException;
import java.io.RandomAccessFile;

import quickbase.internal.index.Stats;



public class RemoveEntry extends Entry {

	public RemoveEntry(byte[] bytesK, long pos) {
		super(bytesK, pos);
	}

	public RemoveEntry(RandomAccessFile raf) throws IOException {
		super(raf);
	}
	
    public void doStats(Stats stats) {
        stats.replacedEntries++;
    }

	@Override
	public byte getType() {
		return REMOVE;
	}

	@Override
	public byte[] getValue() {
		return null;
	}

}
