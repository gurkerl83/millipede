package quickbase.test;

import gnu.trove.TIntIntHashMap;

import java.io.File;
import java.util.Random;

import quickbase.SingleMapDatabase;
import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.DatabaseClosedDatabaseException;
import quickbase.exception.DatabaseException;
import quickbase.exception.IgnoreExceptionsStrategy;
import quickbase.exception.SerializationDatabaseException;
import quickbase.serializer.ByteArraySerializer;
import quickbase.serializer.StringSerializer;
import quickbase.service.IVisitor;

public class DataBaseTest {
    
    private static final int TEST_SIZE = 50000;
	
    TIntIntHashMap controlMap;
	private SingleMapDatabase<String, byte[]> db;
	
	public DataBaseTest() throws BasicFileOperationDatabaseException {
		db = new SingleMapDatabase<String, byte[]>(new IgnoreExceptionsStrategy(), new File("test"), "rand", new StringSerializer(), new ByteArraySerializer());
	}
	
	public void visitContent() throws DatabaseClosedDatabaseException, BasicFileOperationDatabaseException {
	    fillControlMap();
	    long t0 = System.nanoTime();
	    db.visit(new IVisitor<String, byte[]>(){
            public void visit(String key, byte[] value) throws SerializationDatabaseException {
                int len = controlMap.remove(Integer.parseInt(key.substring(5)));
                assert len == value.length;
            }
	    });
	    long t1 = System.nanoTime();
	    System.out.println((t1 - t0)/1000/1000 + " ms");
//	    assert controlMap.isEmpty();
    }
	
	public void fillControlMap() {
        Random rand = new Random(7);
        controlMap = new TIntIntHashMap();
        for (int i=0; i<TEST_SIZE; i++){
            int item = rand.nextInt(10000);
            switch(rand.nextInt(5)){
                case 0:
                    int len = rand.nextInt(100000);
                    controlMap.put(item, len);
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    controlMap.remove(item);
                    break;
                case 4:
                default:
            }
        }
    }
	
	public void bigRandomTest() throws DatabaseException {
	    db.clear();
	    long t0 = System.nanoTime();
	    Random rand = new Random(7);
	    TIntIntHashMap controlMap = new TIntIntHashMap();
	    for (int i=0; i<TEST_SIZE; i++){
	        int item = rand.nextInt(10000);
	        String name = "Item " + item;
	        switch(rand.nextInt(5)){
	            case 0:
	                int len = rand.nextInt(100000);
	                byte[] data = new byte[len];
	                db.put(name, data);
	                controlMap.put(item, len);
	                break;
	            case 1:
	                byte[] data2 = db.get(name);
	                if (data2 == null){
	                    assert !controlMap.contains(item);
	                } else {
	                    assert data2.length == controlMap.get(item);
	                }
	                break;
	            case 2:
	                assert controlMap.contains(item) == db.contains(name);
	                break;
	            case 3:
	                db.remove(name);
	                controlMap.remove(item);
	                break;
	            case 4:
	                System.out.println("Round " + i + ", " + db.getStats().getFillRatio());
	            default:
	        }
	    }
	    long t1 = System.nanoTime();
        System.out.println((t1 - t0)/1000/1000);
	    long size = 0;
	    for (int val: controlMap.getValues()){
	        size += val;
	    }
	    System.out.println(size);
	}
	
	public static void main(String[] args) throws DatabaseException {
		DataBaseTest test = new DataBaseTest();
		test.bigRandomTest();
		test.db.close();
////////		
////////		test.db.compact();
//		test = new DataBaseTest();
//		test.visitContent();
//		test.db.close();
	}

}
