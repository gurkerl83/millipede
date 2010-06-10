/*
 * Created on 01.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.index;

import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntIntProcedure;
import gnu.trove.TIntLongHashMap;
import gnu.trove.TIntLongProcedure;
import gnu.trove.TIntProcedure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AdaptingIntLongMap {

    private static final byte USE_INTS = 1;
    private static final byte USE_LONGS = 2;

    private TIntIntHashMap index;
    private TIntLongHashMap longIndex;

    public AdaptingIntLongMap(int initialSize) {
        this.index = new TIntIntHashMap(initialSize);
    }

    public AdaptingIntLongMap(DataInputStream dis) throws IOException {
        byte type = dis.readByte();
        int entries = dis.readInt();
        if (type == USE_INTS) {
            index = new TIntIntHashMap(entries);
            for (int i = 0; i < entries; i++) {
                int key = dis.readInt();
                int value = dis.readInt();
                if (value < 0){
                    throw new IOException("Negative value");
                }
                index.put(key, value);
            }
        } else if (type == USE_LONGS) {
            longIndex = new TIntLongHashMap(entries);
            for (int i = 0; i < entries; i++) {
                int key = dis.readInt();
                long value = dis.readLong();
                if (value < 0){
                    throw new IOException("Negative value");
                }
                longIndex.put(key, value);
            }
        } else {
            throw new IOException("Unexpected data: " + type);
        }
    }

    public long get(int hash) {
        if (index == null) {
            if (longIndex.contains(hash)) {
                return longIndex.get(hash);
            } else {
                return -1;
            }
        } else {
            if (index.contains(hash)) {
                return index.get(hash);
            } else {
                return -1;
            }
        }
    }

    public void put(int hash, long position) {
        assert position >= 0;
        if (index == null) {
            longIndex.put(hash, position);
        } else {
            if (position > Integer.MAX_VALUE) {
                convertMap();
                longIndex.put(hash, position);
            } else {
                index.put(hash, (int) position);
            }
        }
    }

    private void convertMap() {
        final TIntLongHashMap longIndex = new TIntLongHashMap(index.size());
        index.forEachEntry(new TIntIntProcedure() {

            public boolean execute(int a, int b) {
                longIndex.put(a, b);
                return true;
            }
        });
        this.longIndex = longIndex;
        this.index = null;
    }

    public void write(final DataOutputStream dos) throws IOException {
        final IOException[] ex = new IOException[1];
        if (index == null) {
            dos.writeByte(USE_LONGS);
            dos.writeInt(longIndex.size());
            longIndex.forEachEntry(new TIntLongProcedure() {

                public boolean execute(int a, long b) {
                    try {
                        dos.writeInt(a);
                        dos.writeLong(b);
                        return true;
                    } catch (IOException e) {
                        ex[0] = e;
                        return false;
                    }
                }
            });
        } else {
            dos.writeByte(USE_INTS);
            dos.writeInt(index.size());
            index.forEachEntry(new TIntIntProcedure() {

                public boolean execute(int a, int b) {
                    try {
                        dos.writeInt(a);
                        dos.writeInt(b);
                        return true;
                    } catch (IOException e) {
                        ex[0] = e;
                        return false;
                    }
                }
            });
        }
        if (ex[0] != null) {
            throw ex[0];
        }
    }

    public void visitKeys(TIntProcedure visitor) {
        if (index == null){
            longIndex.forEachKey(visitor);
        } else {
            index.forEachKey(visitor);
        }
    }

}
