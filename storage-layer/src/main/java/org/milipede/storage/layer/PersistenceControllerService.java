package org.milipede.storage.layer;

import org.millipede.router.vo.ProviderVO;

import quickbase.exception.DatabaseException;

public interface PersistenceControllerService {

	// making provider persistent
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.milipede.storage.layer.internal.PersistenceControllerService#shutdown
	 * ()
	 */
	public abstract void shutdown() throws DatabaseException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.milipede.storage.layer.internal.PersistenceControllerService#startup
	 * ()
	 */
	public abstract void startup() throws DatabaseException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.milipede.storage.layer.internal.PersistenceControllerService#addProvider
	 * (org.millipede.router.vo.ProviderVO)
	 */
	public abstract boolean addProvider(ProviderVO provider)
			throws DatabaseException;

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.milipede.storage.layer.internal.PersistenceControllerService#
	 * removeProvider(org.millipede.router.vo.ProviderVO)
	 */
	public abstract boolean removeProvider(ProviderVO provider)
			throws DatabaseException;

}