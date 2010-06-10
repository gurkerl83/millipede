package quickbase.test;

import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntObjectHashMap;

import java.io.File;
import java.util.Random;

import quickbase.MultiMapDatabase;
import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.DatabaseException;
import quickbase.exception.IgnoreExceptionsStrategy;
import quickbase.serializer.ByteArraySerializer;
import quickbase.serializer.StringSerializer;
import quickbase.service.IPersistedMap;

public class MixedMapTest {

    private MultiMapDatabase multi;
    private IPersistedMap<String, byte[]> sbMap;
    private IPersistedMap<byte[], String> bsMap;

    public MixedMapTest() throws BasicFileOperationDatabaseException {
        multi = new MultiMapDatabase(new IgnoreExceptionsStrategy(), new File("test"), "multi");
        sbMap = multi.createMap((byte) 0, new StringSerializer(), new ByteArraySerializer());
        bsMap = multi.createMap((byte) 1, new ByteArraySerializer(), new StringSerializer());
    }

    public void bigRandomTest() throws DatabaseException {
        long t0 = System.nanoTime();
        Random rand = new Random(7);
        TIntObjectHashMap<String> controlMap2 = new TIntObjectHashMap<String>();
        TIntIntHashMap controlMap = new TIntIntHashMap();
        for (int i = 0; i < 100000; i++) {
            int item = rand.nextInt(10000);
            String name = "Item " + item;
            switch (rand.nextInt(9)) {
                case 0:
                    int len = rand.nextInt(100000);
                    byte[] data = new byte[len];
                    sbMap.put(name, data);
                    controlMap.put(item, len);
                    break;
                case 1:
                    byte[] data2 = sbMap.get(name);
                    if (data2 == null) {
                        assert !controlMap.contains(item);
                    } else {
                        assert data2.length == controlMap.get(item);
                    }
                    break;
                case 2:
                    assert controlMap.contains(item) == sbMap.contains(name);
                    break;
                case 3:
                    sbMap.remove(name);
                    controlMap.remove(item);
                    break;
                case 4:
                    byte[] keyData = java.nio.ByteBuffer.allocate(4).putInt(item).array(); 
                    bsMap.put(keyData, name);
                    controlMap2.put(item, name);
                    break;
                case 5:
                    byte[] keyData2 = java.nio.ByteBuffer.allocate(4).putInt(item).array(); 
                    String string = bsMap.get(keyData2);
                    if (string == null) {
                        assert !controlMap2.contains(item);
                    } else {
                        assert string.equals(controlMap2.get(item));
                    }
                    break;
                case 6:
                    byte[] keyData3 = java.nio.ByteBuffer.allocate(4).putInt(item).array(); 
                    assert controlMap2.contains(item) == bsMap.contains(keyData3);
                    break;
                case 7:
                    byte[] keyData4 = java.nio.ByteBuffer.allocate(4).putInt(item).array(); 
                    bsMap.remove(keyData4);
                    controlMap2.remove(item);
                    break;
                case 8:
                    System.out.println("Round " + i + " " + multi.getStats().getFillRatio());
                default:
            }
        }
        long t1 = System.nanoTime();
        System.out.println((t1 - t0) / 1000 / 1000);
        multi.close();
        long size = 0;
        for (int val : controlMap.getValues()) {
            size += val;
        }
        System.out.println(size);
    }

    public static void main(String[] args) throws DatabaseException {
        MixedMapTest test = new MixedMapTest();
        test.bigRandomTest();
    }

}
