package org.milipede;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.milipede.storage.layer.PersistenceControllerService;
import org.milipede.storage.layer.internal.ProviderController;
import org.millipede.router.vo.ProviderVO;

import quickbase.MultiMapDatabase;
import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.DatabaseClosedDatabaseException;
import quickbase.exception.DatabaseException;
import quickbase.exception.IgnoreExceptionsStrategy;
import quickbase.exception.SerializationDatabaseException;
import quickbase.serializer.ByteArraySerializer;
import quickbase.serializer.StringSerializer;
import quickbase.service.IPersistedMap;

public class PersistenceController { //implements PersistenceControllerService {

	
    private MultiMapDatabase multi;
    private IPersistedMap<String, byte[]> storageMap;
    private IPersistedMap<String, byte[]> mailMap;
    private IPersistedMap<String, byte[]> hosterMap;

    public PersistenceController() throws BasicFileOperationDatabaseException{
//        try {
            multi = new MultiMapDatabase(new IgnoreExceptionsStrategy(), new File("test"), "multi");
//        } catch (BasicFileOperationDatabaseException ex) {
//            Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        storageMap = multi.createMap((byte) 0, new StringSerializer(), new ByteArraySerializer());
        mailMap = multi.createMap((byte) 1, new StringSerializer(), new ByteArraySerializer());
        hosterMap = multi.createMap((byte) 2, new StringSerializer(), new ByteArraySerializer());


        }
    
        public static void main(String[] args) throws DatabaseException {
        PersistenceController test = new PersistenceController();
        test.startup();
        test.addProvider(new ProviderVO());
        }
    
    //making provider persistent
    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.PersistenceControllerService#shutdown()
	 */
    public void shutdown() throws DatabaseException {
    	for ( Iterator<ProviderVO> i = ProviderController.getInstance().getStorage().iterator(); i.hasNext(); )
    	{
    	  ProviderVO s = i.next();
//            try {
                storageMap.put(s.getTitle(), s.getBytes());
//            } catch (DatabaseClosedDatabaseException ex) {
//                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (SerializationDatabaseException ex) {
//                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (BasicFileOperationDatabaseException ex) {
//                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
//            }
    	}
    	
    	for ( Iterator<ProviderVO> i = ProviderController.getInstance().getMail().iterator(); i.hasNext(); )
    	{
    	  ProviderVO s = i.next();
//            try {
                mailMap.put(s.getTitle(), s.getBytes());
//            } catch (DatabaseClosedDatabaseException ex) {
//                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (SerializationDatabaseException ex) {
//                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (BasicFileOperationDatabaseException ex) {
//                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
//            }
    	}
    	
    	for ( Iterator<ProviderVO> i = ProviderController.getInstance().getHoster().iterator(); i.hasNext(); )
    	{
    	  ProviderVO s = i.next();
//            try {
                hosterMap.put(s.getTitle(), s.getBytes());
//            } catch (DatabaseClosedDatabaseException ex) {
//                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (SerializationDatabaseException ex) {
//                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (BasicFileOperationDatabaseException ex) {
//                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
//            }
    	}
    }
    
    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.PersistenceControllerService#startup()
	 */
    public void startup() throws DatabaseException {
//        try {
            for (Iterator<byte[]> i = storageMap.values().iterator(); i.hasNext();) {
                ProviderController.getInstance().getStorage().add(ProviderVO.getProviderVO(i.next()));
            }
            for (Iterator<byte[]> i = mailMap.values().iterator(); i.hasNext();) {
                ProviderController.getInstance().getMail().add(ProviderVO.getProviderVO(i.next()));
            }
            for (Iterator<byte[]> i = hosterMap.values().iterator(); i.hasNext();) {
                ProviderController.getInstance().getHoster().add(ProviderVO.getProviderVO(i.next()));
            }
//        } catch (DatabaseClosedDatabaseException ex) {
//            Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (BasicFileOperationDatabaseException ex) {
//            Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.PersistenceControllerService#addProvider(org.millipede.router.vo.ProviderVO)
	 */
    public void addProvider(ProviderVO provider) {
        if(provider.getCategory().equals("storage")) {
            try {
                storageMap.put(provider.getTitle(), provider.getBytes());
            } catch (DatabaseClosedDatabaseException ex) {
                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SerializationDatabaseException ex) {
                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BasicFileOperationDatabaseException ex) {
                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /* (non-Javadoc)
	 * @see org.milipede.storage.layer.internal.PersistenceControllerService#removeProvider(org.millipede.router.vo.ProviderVO)
	 */
    public void removeProvider(ProviderVO provider) {
        if(provider.getCategory().equals("storage")) {
            try {
                storageMap.remove(provider.getTitle());
            } catch (DatabaseClosedDatabaseException ex) {
                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SerializationDatabaseException ex) {
                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BasicFileOperationDatabaseException ex) {
                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
