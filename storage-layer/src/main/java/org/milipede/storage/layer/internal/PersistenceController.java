package org.milipede.storage.layer.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.milipede.storage.layer.PersistenceControllerService;
import org.millipede.router.vo.ProviderVO;

import quickbase.MultiMapDatabase;
import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.DatabaseClosedDatabaseException;
import quickbase.exception.DatabaseException;
import quickbase.exception.IgnoreExceptionsStrategy;
import quickbase.serializer.ByteArraySerializer;
import quickbase.serializer.StringSerializer;
import quickbase.service.IPersistedMap;

public class PersistenceController { // implements PersistenceControllerService
	// {

	private MultiMapDatabase multi;
	private IPersistedMap<String, byte[]> storageMap;
	private IPersistedMap<String, byte[]> mailMap;
	private IPersistedMap<String, byte[]> hosterMap;

	private boolean started = false;

	public PersistenceController() throws BasicFileOperationDatabaseException {
		multi = new MultiMapDatabase(new IgnoreExceptionsStrategy(), new File(
				"test"), "multi");

		storageMap = multi.createMap((byte) 0, new StringSerializer(),
				new ByteArraySerializer());
		mailMap = multi.createMap((byte) 1, new StringSerializer(),
				new ByteArraySerializer());
		hosterMap = multi.createMap((byte) 2, new StringSerializer(),
				new ByteArraySerializer());

	}

	private static PersistenceController instance;

	public static PersistenceController getInstance()
			throws BasicFileOperationDatabaseException {
		if (instance != null) {
			return instance;
		} else {
			instance = new PersistenceController();
			return instance;
		}

	}

	// making provider persistent
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.milipede.storage.layer.internal.PersistenceControllerService#shutdown
	 * ()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.milipede.storage.layer.internal.PersistenceControllerService#shutdown
	 * ()
	 */
	public void shutdown() throws DatabaseException {

		for (Iterator<ProviderVO> i = ProviderController.getInstance()
				.getStorage().iterator(); i.hasNext();) {
			ProviderVO s = i.next();
			storageMap.put(s.getTitle(), s.getBytes());
		}

		for (Iterator<ProviderVO> i = ProviderController.getInstance()
				.getMail().iterator(); i.hasNext();) {
			ProviderVO s = i.next();
			mailMap.put(s.getTitle(), s.getBytes());
		}

		for (Iterator<ProviderVO> i = ProviderController.getInstance()
				.getHoster().iterator(); i.hasNext();) {
			ProviderVO s = i.next();
			hosterMap.put(s.getTitle(), s.getBytes());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.milipede.storage.layer.internal.PersistenceControllerService#startup
	 * ()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.milipede.storage.layer.internal.PersistenceControllerService#startup
	 * ()
	 */
	public void startup() throws DatabaseException {
		if (started == true) {
			return;
		} else {

			Collection<byte[]> values = (Collection<byte[]>) storageMap
					.values();
			for (byte[] t : values)
				ProviderController.getInstance().getStorage().add(
						ProviderVO.getProviderVO(t));

			values = (Collection<byte[]>) mailMap.values();
			for (Iterator<byte[]> i = values.iterator(); i.hasNext();) {

				ProviderController.getInstance().getMail().add(
						ProviderVO.getProviderVO(i.next()));
			}
			values = (Collection<byte[]>) hosterMap.values();
			for (Iterator<byte[]> i = values.iterator(); i.hasNext();) {
				ProviderController.getInstance().getHoster().add(
						ProviderVO.getProviderVO(i.next()));
			}
		}
		started = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.milipede.storage.layer.internal.PersistenceControllerService#addProvider
	 * (org.millipede.router.vo.ProviderVO)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.milipede.storage.layer.internal.PersistenceControllerService#addProvider
	 * (org.millipede.router.vo.ProviderVO)
	 */
	public boolean addProvider(ProviderVO provider) throws DatabaseException {
		if (provider.getCategory().equals("storage")) {
			if (storageMap.contains(provider.getTitle())) {
				return false;
			} else {
				storageMap.put(provider.getTitle(), provider.getBytes());
				return true;
			}

		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.milipede.storage.layer.internal.PersistenceControllerService#
	 * removeProvider(org.millipede.router.vo.ProviderVO)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.milipede.storage.layer.internal.PersistenceControllerService#
	 * removeProvider(org.millipede.router.vo.ProviderVO)
	 */
	public boolean removeProvider(ProviderVO provider) throws DatabaseException {
		if (provider.getCategory().equals("storage")) {
			storageMap.remove(provider.getTitle());
			return true;
		} else
			return false;
	}

	public ProviderVO getProvider() throws DatabaseClosedDatabaseException,
			BasicFileOperationDatabaseException {
		ProviderVO provider = null;
		return provider;
	}

	public List<ProviderVO> getProviders()
			throws DatabaseClosedDatabaseException,
			BasicFileOperationDatabaseException {
		ProviderVO provider;
		List<ProviderVO> providers = new ArrayList<ProviderVO>();
		Collection<byte[]> values = (Collection<byte[]>) storageMap.values();
		for (byte[] t : values) {
			provider = ProviderVO.getProviderVO(t);
			providers.add(provider);
		}
		return providers;
	}

}
