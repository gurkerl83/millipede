/*
 * Created on 01.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.index;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Stats {
    
    private static final byte VERSION = 1;
    
    public long creation;
    public long size;
    
    public long addEntries;
    public long replacedEntries;
    
    public long gets;
    public long puts;
    public long contains;
    public long removes;

    public Stats(){
        creation = System.currentTimeMillis();
    }
    
    public double getFillRatio(){
        //reports a little lower than actual value if there are hash collisions or double removes
        return Math.max(0, ((double)addEntries - replacedEntries)/addEntries);
    }
    
    public Stats(DataInputStream dis) throws IOException {
        byte v = dis.readByte();
        assert VERSION == v;
        creation = dis.readLong();
        size = dis.readLong();
        addEntries = dis.readLong();
        replacedEntries = dis.readLong();
        gets = dis.readLong();
        puts = dis.readLong();
        contains = dis.readLong();
        removes = dis.readLong();
    }

    public void write(DataOutputStream dos) throws IOException {
        dos.writeByte(VERSION);
        dos.writeLong(creation);
        dos.writeLong(size);
        dos.writeLong(addEntries);
        dos.writeLong(replacedEntries);
        dos.writeLong(gets);
        dos.writeLong(puts);
        dos.writeLong(contains);
        dos.writeLong(removes);
    }

}