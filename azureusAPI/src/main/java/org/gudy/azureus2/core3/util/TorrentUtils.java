/*
 * File    : TorrentUtils.java
 * Created : 13-Oct-2003
 * By      : stuff
 * 
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.gudy.azureus2.core3.util;

/**
 * @author parg
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;

//import com.aelitis.azureus.core.*;
//import com.aelitis.azureus.core.util.CopyOnWriteList;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.config.ParameterListener;
//import org.gudy.azureus2.core3.internat.*;
//import org.gudy.azureus2.core3.logging.LogRelation;
import org.gudy.azureus2.core3.torrent.*;
//import org.gudy.azureus2.core3.disk.*;
//import org.gudy.azureus2.core3.download.*;


public class 
TorrentUtils 
{
//	public static final int TORRENT_FLAG_LOW_NOISE	= 0x00000001;
//
//	private static final String		TORRENT_AZ_PROP_DHT_BACKUP_ENABLE		= "dht_backup_enable";
//	private static final String		TORRENT_AZ_PROP_DHT_BACKUP_REQUESTED	= "dht_backup_requested";
//	private static final String		TORRENT_AZ_PROP_TORRENT_FLAGS			= "torrent_flags";
//	private static final String		TORRENT_AZ_PROP_PLUGINS					= "plugins";
//
	public static final String		TORRENT_AZ_PROP_OBTAINED_FROM			= "obtained_from";
//	public static final String		TORRENT_AZ_PROP_PEER_CACHE				= "peer_cache";
//	public static final String		TORRENT_AZ_PROP_PEER_CACHE_VALID		= "peer_cache_valid";
//
	private static final String		MEM_ONLY_TORRENT_PATH		= "?/\\!:mem_only:!\\/?";
//
//	private static final long		PC_MARKER = RandomUtils.nextLong();
//
	private static final List	created_torrents = new ArrayList();
//	private static final Set	created_torrents_set;
//
//	private static ThreadLocal		tls	=
//		new ThreadLocal()
//		{
//			public Object
//			initialValue()
//			{
//				return( new HashMap());
//			}
//		};
//
//	private static volatile Set		ignore_set;
//
	private static boolean bSaveTorrentBackup = true;
//
//	private static CopyOnWriteList	torrent_attribute_listeners = new CopyOnWriteList();
//
//	static {
//		COConfigurationManager.addAndFireParameterListener("Save Torrent Backup",
//				new ParameterListener() {
//					public void parameterChanged(String parameterName) {
//						bSaveTorrentBackup = COConfigurationManager.getBooleanParameter(parameterName);
//					}
//				});
//
//		created_torrents = COConfigurationManager.getListParameter( "my.created.torrents", new ArrayList());
//
//		created_torrents_set	= new HashSet();
//
//		Iterator	it = created_torrents.iterator();
//
//		while( it.hasNext()){
//
//			created_torrents_set.add( new HashWrapper((byte[])it.next()));
//		}
//	}

	public static TOTorrent
	readFromFile(
		File		file,
		boolean		create_delegate )
		
		throws TOTorrentException
	{
		return( readFromFile( file, create_delegate, false ));
	}
	
		/**
		 * If you set "create_delegate" to true then you must understand that this results
		 * is piece hashes being discarded and then re-read from the torrent file if needed
		 * Therefore, if you delete the original torrent file you're going to get errors
		 * if you access the pieces after this (and they've been discarded)
		 * @param file
		 * @param create_delegate
		 * @param force_initial_discard - use to get rid of pieces immediately
		 * @return
		 * @throws TOTorrentException
		 */
	
//	public static ExtendedTorrent
//	readDelegateFromFile(
//		File		file,
//		boolean		force_initial_discard )
//
//		throws TOTorrentException
//	{
//		return((ExtendedTorrent)readFromFile( file, true, force_initial_discard ));
//	}
	
	public static TOTorrent
	readFromFile(
		File		file,
		boolean		create_delegate,
		boolean		force_initial_discard )
		
		throws TOTorrentException
	{
		TOTorrent torrent;
   
		try{
			torrent = TOTorrentFactory.deserialiseFromBEncodedFile(file);
			
				// make an immediate backup if requested and one doesn't exist 
			
	    	if (bSaveTorrentBackup) {

	    		File torrent_file_bak = new File(file.getParent(), file.getName() + ".bak");

	    		if ( !torrent_file_bak.exists()){

	    			try{
	    				torrent.serialiseToBEncodedFile(torrent_file_bak);

	    			}catch( Throwable e ){

//	    				Debug.printStackTrace(e);
	    			}
	    		}
	    	}

		}catch (TOTorrentException e){
      
//			Debug.outNoStack( e.getMessage() );
			
			File torrentBackup = new File(file.getParent(), file.getName() + ".bak");
			
			if( torrentBackup.exists()){
				
				torrent = TOTorrentFactory.deserialiseFromBEncodedFile(torrentBackup);
				
					// use the original torrent's file name so that when this gets saved
					// it writes back to the original and backups are made as required
					// - set below
			}else{
				
				throw e;
			}
		}
				
		torrent.setAdditionalStringProperty("torrent filename", file.toString());
		
//		if ( create_delegate ){
//
//			torrentDelegate	res = new torrentDelegate( torrent, file );
//
//			if ( force_initial_discard ){
//
//				res.discardPieces( SystemTime.getCurrentTime(), true );
//			}
//
//			return( res );
//
//		}else{
			
			return( torrent );
//		}
	}

        public static TOTorrent
	readFromBEncodedInputStream(
		InputStream		is )

		throws TOTorrentException
	{
		TOTorrent	torrent = TOTorrentFactory.deserialiseFromBEncodedInputStream( is );

			// as we've just imported this torrent we want to clear out any possible attributes that we
			// don't want such as "torrent filename"

		torrent.removeAdditionalProperties();

		return( torrent );
	}

        public static URL
	getDecentralisedEmptyURL()
	{
		try{
			return( new URL( "dht://" ));

		}catch( Throwable e ){

//			Debug.printStackTrace(e);

			return( null );
		}
	}


	public static void
	addCreatedTorrent(
		TOTorrent		torrent )
	{
		synchronized( created_torrents ){

			try{
				byte[]	hash = torrent.getHash();

				//System.out.println( "addCreated:" + new String(torrent.getName()) + "/" + ByteFormatter.encodeString( hash ));

//				if ( created_torrents.size() == 0 ){
//
//					COConfigurationManager.setParameter( "my.created.torrents", created_torrents );
//				}

				HashWrapper	hw = new HashWrapper( hash );

//				if ( !created_torrents_set.contains( hw )){
//
					created_torrents.add( hash );
//
//					created_torrents_set.add( hw );

//					COConfigurationManager.setDirty();
//				}
			}catch( TOTorrentException e ){

			}
		}
	}



	public static void
	writeToFile(
		final TOTorrent		torrent )

		throws TOTorrentException
	{
		writeToFile( torrent, false );
	}

	public static void
	writeToFile(
		TOTorrent		torrent,
		boolean			force_backup )

		throws TOTorrentException
	{
	   try{
	   		torrent.getMonitor().enter();

	    	String str = torrent.getAdditionalStringProperty("torrent filename");

	    	if ( str == null ){

	    		throw (new TOTorrentException("TorrentUtils::writeToFile: no 'torrent filename' attribute defined", TOTorrentException.RT_FILE_NOT_FOUND));
	    	}

	    	if ( str.equals( MEM_ONLY_TORRENT_PATH )){

	    		return;
	    	}

	    		// save first to temporary file as serialisation may require state to be re-read from
	    		// the existing file first and if we rename to .bak first then this aint good

    		File torrent_file_tmp = new File(str + "._az");

	    	torrent.serialiseToBEncodedFile( torrent_file_tmp );

	    		// now backup if required

	    	File torrent_file = new File(str);

	    	if ( 	( force_backup ||COConfigurationManager.getBooleanParameter("Save Torrent Backup")) &&
	    			torrent_file.exists()) {

	    		File torrent_file_bak = new File(str + ".bak");

	    		try{

	    				// Will return false if it cannot be deleted (including if the file doesn't exist).

	    			torrent_file_bak.delete();

	    			torrent_file.renameTo(torrent_file_bak);

	    		}catch( SecurityException e){

//	    			Debug.printStackTrace( e );
	    		}
	    	}

	    		// now rename the temp file to required one

	    	if ( torrent_file.exists()){

	    		torrent_file.delete();
	    	}

	    	torrent_file_tmp.renameTo( torrent_file );

	   	}finally{

	   		torrent.getMonitor().exit();
	   	}
	}

	public static void
	writeToFile(
		TOTorrent		torrent,
		File			file )

		throws TOTorrentException
	{
		writeToFile( torrent, file, false );
	}

	public static void
	writeToFile(
		TOTorrent		torrent,
		File			file,
		boolean			force_backup )

		throws TOTorrentException
	{
		torrent.setAdditionalStringProperty("torrent filename", file.toString());

		writeToFile( torrent, force_backup );
	}


        public static String
	getTorrentFileName(
		TOTorrent		torrent )

		throws TOTorrentException
	{
    	String str = torrent.getAdditionalStringProperty("torrent filename");

    	if ( str == null ){

    		throw( new TOTorrentException("TorrentUtils::getTorrentFileName: no 'torrent filename' attribute defined", TOTorrentException.RT_FILE_NOT_FOUND));
    	}

    	if ( str.equals( MEM_ONLY_TORRENT_PATH )){

    		return( null );
    	}

		return( str );
	}


	/**
	 * Copy a file to the Torrent Save Directory, taking into account all the
	 * user config options related to that.
	 * <p>
	 * Also makes the directory if it doesn't exist.
	 *
	 * @param f File to copy
	 * @param persistent Whether the torrent is persistent
	 * @return File after it's been copied (may be the same as f)
	 * @throws IOException
	 */
	public static File copyTorrentFileToSaveDir(File f, boolean persistent)
			throws IOException {
		File torrentDir;
		boolean saveTorrents = persistent
				&& COConfigurationManager.getBooleanParameter("Save Torrent Files");
		if (saveTorrents)
			torrentDir = new File(COConfigurationManager
					.getDirectoryParameter("General_sDefaultTorrent_Directory"));
		else
			torrentDir = new File(f.getParent());

		//if the torrent is already in the completed files dir, use this
		//torrent instead of creating a new one in the default dir
		boolean moveWhenDone = COConfigurationManager.getBooleanParameter("Move Completed When Done");
		String completedDir = COConfigurationManager.getStringParameter(
				"Completed Files Directory", "");
		if (moveWhenDone && completedDir.length() > 0) {
			File cFile = new File(completedDir, f.getName());
			if (cFile.exists()) {
				//set the torrentDir to the completedDir
				torrentDir = new File(completedDir);
			}
		}

		FileUtil.mkdirs(torrentDir);

		File fDest = new File(torrentDir, f.getName().replaceAll("%20", "."));
		if (fDest.equals(f)) {
			return f;
		}

		while (fDest.exists()) {
			fDest = new File(torrentDir, "_" + fDest.getName());
		}

		fDest.createNewFile();

		if (!FileUtil.copyFile(f, fDest)) {
			throw new IOException("File copy failed");
		}

		return fDest;
	}

public static String
	getLocalisedName(
		TOTorrent		torrent )
	{
		try{

//			LocaleUtilDecoder decoder = LocaleTorrentUtil.getTorrentEncodingIfAvailable( torrent );
			String decoder = null;
			if ( decoder == null ){

				return( new String(torrent.getName(),Constants.DEFAULT_ENCODING));
			}

//			return( decoder.decodeString(torrent.getName()));
                        return( new String(torrent.getName(),Constants.DEFAULT_ENCODING));

		}catch( Throwable e ){

			Debug.printStackTrace( e );

			return( new String( torrent.getName()));
		}
	}

public static void
	setObtainedFrom(
		File			file,
		String			str )
	{
		try{
			TOTorrent	torrent = readFromFile( file, false, false );

			setObtainedFrom( torrent, str );

			writeToFile( torrent );

		} catch (TOTorrentException e) {
			// ignore, file probably not torrent

		}catch( Throwable e ){

			Debug.out( e );
		}
	}

	public static void
	setObtainedFrom(
		TOTorrent		torrent,
		String			str )
	{
		Map	m = getAzureusPrivateProperties( torrent );

		try{
			m.put( TORRENT_AZ_PROP_OBTAINED_FROM, str.getBytes( "UTF-8" ));

//			fireAttributeListener( torrent, TORRENT_AZ_PROP_OBTAINED_FROM, str );

		}catch( Throwable e ){

			Debug.printStackTrace(e);
		}
	}

        private static Map
	getAzureusPrivateProperties(
		TOTorrent	torrent )
	{
		Map	m = torrent.getAdditionalMapProperty( TOTorrent.AZUREUS_PRIVATE_PROPERTIES );

		if ( m == null ){

			m = new HashMap();

			torrent.setAdditionalMapProperty( TOTorrent.AZUREUS_PRIVATE_PROPERTIES, m );
		}

		return( m );
	}


	}

