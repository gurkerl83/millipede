package org.opendedup.sdfs.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opendedup.sdfs.Main;
import org.opendedup.sdfs.filestore.DedupFileStore;
import org.opendedup.sdfs.filestore.MetaFileStore;
import org.opendedup.sdfs.monitor.IOMonitor;
import org.opendedup.util.ByteUtils;

import com.eaio.uuid.UUID;

/**
 * 
 * @author annesam Stores Meta-Data about a dedupFile. This class is modeled
 *         from the java.io.File class. Meta-Data files are stored within the
 *         MetaDataFileStore @see com.annesam.filestore.MetaDataFileStore
 */
public class MetaDataDedupFile implements java.io.Externalizable {

	private static final long serialVersionUID = -4598940197202968523L;
	transient public static final String pathSeparator = File.pathSeparator;
	transient public static final String separator = File.separator;
	transient public static final char pathSeparatorChar = File.pathSeparatorChar;
	transient public static final char separatorChar = File.separatorChar;
	transient private static Logger log = Logger.getLogger("sdfs");
	protected long timeStamp = 0;
	private long length = 0;
	private String path = "";
	private long lastModified = 0;
	private long lastAccessed = 0;
	private boolean execute = true;
	private boolean read = true;
	private boolean write = true;
	private boolean directory = false;
	private boolean hidden = false;
	private boolean ownerWriteOnly = false;
	private boolean ownerExecOnly = false;
	private boolean ownerReadOnly = false;
	private String dfGuid = null;
	private String guid = "";
	private IOMonitor monitor;
	private boolean vmdk;
	private int permissions;
	private int owner_id;
	private int group_id;
	private HashMap<String, String> extendedAttrs = new HashMap<String, String>();
	private boolean dedup = Main.dedupFiles;

	/**
	 * 
	 * @return true if all chunks within the file will be deduped.
	 */
	public boolean isDedup() {
		return dedup;
	}

	/**
	 * 
	 * @param dedup
	 *            if true all chunks will be deduped, Otherwise chunks will be
	 *            deduped opportunistically.
	 */
	public void setDedup(boolean dedupNow) {
		if (!this.dedup && dedupNow) {
			try {
				this.dedup = dedupNow;
				this.getDedupFile().optimize(this.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.dedup = dedupNow;
	}

	/**
	 * adds a posix extended attribute
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param value
	 *            the value of the attribute
	 */
	public void addXAttribute(String name, String value) {
		extendedAttrs.put(name, value);
	}

	/**
	 * returns an extended attribute for a give name
	 * 
	 * @param name
	 * @return the extended attribute
	 */
	public String getXAttribute(String name) {
		if (this.extendedAttrs.containsKey(name))
			return extendedAttrs.get(name);
		else
			return "-1";
	}

	/**
	 * 
	 * @return list of all extended attribute names
	 */
	public String[] getXAttersNames() {
		String[] keys = new String[this.extendedAttrs.size()];
		Iterator<String> iter = this.extendedAttrs.keySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			keys[i] = iter.next();
			i++;
		}
		return keys;
	}

	/**
	 * 
	 * @return posix permissions e.g. 0777
	 */
	public int getPermissions() {
		return permissions;
	}

	/**
	 * 
	 * @param permissions
	 *            sets permissions
	 */
	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}

	/**
	 * 
	 * @return the file owner id
	 */
	public int getOwner_id() {
		return owner_id;
	}

	/**
	 * 
	 * @param owner_id
	 *            sets the file owner id
	 */
	public void setOwner_id(int owner_id) {
		this.owner_id = owner_id;
	}

	/**
	 * 
	 * @return returns the group owner id
	 */
	public int getGroup_id() {
		return group_id;
	}

	/**
	 * 
	 * @param group_id
	 *            sets the group owner id
	 */
	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	/**
	 * 
	 * @return true if this file is a vmdk
	 */
	public boolean isVmdk() {
		return vmdk;
	}

	/**
	 * 
	 * @param vmdk
	 *            flags this file as a vmdk if true
	 */
	public void setVmdk(boolean vmdk) {
		this.vmdk = vmdk;
	}

	public MetaDataDedupFile() {
	}
	
	public static MetaDataDedupFile getFile(String path)  {
		File f = new File(path);
		MetaDataDedupFile mf = null;
		if(!f.exists() || f.isDirectory()) {
			mf = new MetaDataDedupFile(path);
		}
		else {
			try {
			ObjectInputStream in =
		        new ObjectInputStream(
		          new FileInputStream(path));
			
				mf = (MetaDataDedupFile)in.readObject();
				mf.path = path;
			} catch (Exception e) {
				log.log(Level.SEVERE,"unable to de-serialize " +path,e);
			}

		}
		return mf;
	}

	/**
	 * 
	 * @return returns the IOMonitor for this file. IOMonitors monitor
	 *         reads,writes, and dedup rate.
	 */
	public IOMonitor getIOMonitor() {
		if (monitor == null)
			monitor = new IOMonitor();
		return monitor;
	}

	/**
	 * 
	 * @param path
	 *            the path to the dedup file.
	 */
	private MetaDataDedupFile(String path) {
		init(path);
	}

	/**
	 * 
	 * @param parent
	 *            the parent folder
	 * @param child
	 *            the file name
	 */
	public MetaDataDedupFile(File parent, String child) {
		String pth = parent.getAbsolutePath() + File.separator + child;
		init(pth);
	}

	/**
	 * 
	 * @return the DedupFile associated with this file. It will create one if it
	 *         does not already exist.
	 * @throws IOException
	 */

	public synchronized DedupFile getDedupFile() throws IOException {
		if (this.dfGuid == null) {
			DedupFile df = DedupFileStore.getDedupFile(this);
			this.dfGuid = df.getGUID();
			log.finer("No DF EXISTS .... Set dedup file for " + this.getPath()
					+ " to " + this.dfGuid);
			this.sync();
			return df;
		} else {
			return DedupFileStore.getDedupFile(this);
		}
	}

	/**
	 * 
	 * @return the guid associated with this file
	 */
	public String getGUID() {
		return guid;
	}

	/**
	 * Clones a file and the underlying DedupFile
	 * 
	 * @param snaptoPath
	 *            the path to clone to
	 * @param overwrite
	 *            if true, it will overwrite the destination file if it alreay
	 *            exists
	 * @return the new clone
	 * @throws IOException
	 */
	public MetaDataDedupFile snapshot(String snaptoPath, boolean overwrite)
			throws IOException {
		if (!this.isDirectory()) {
			File f = new File(snaptoPath);
			if (f.exists() && !overwrite)
				throw new IOException("path exists [" + snaptoPath
						+ "]Cannot overwrite existing data ");
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			MetaDataDedupFile _mf = new MetaDataDedupFile(snaptoPath);
			_mf.directory = this.directory;
			_mf.execute = this.execute;
			_mf.hidden = this.hidden;
			_mf.lastModified = this.lastModified;
			_mf.setLength(this.length, false);
			_mf.ownerExecOnly = this.ownerExecOnly;
			_mf.ownerReadOnly = this.ownerReadOnly;
			_mf.ownerWriteOnly = this.ownerWriteOnly;
			_mf.timeStamp = this.timeStamp;
			_mf.read = this.read;
			_mf.write = this.write;
			_mf.owner_id = this.owner_id;
			_mf.group_id = this.group_id;
			_mf.permissions = this.permissions;
			_mf.dedup = this.dedup;
			_mf.dfGuid = DedupFileStore.cloneDedupFile(this, _mf).getGUID();
			_mf.getIOMonitor().setVirtualBytesWritten(this.length());
			_mf.getIOMonitor().setDuplicateBlocks(
					this.getIOMonitor().getDuplicateBlocks());
			_mf.setVmdk(this.isVmdk());
			_mf.unmarshal();

			return _mf;
		} else {
			File f = new File(snaptoPath);
			f.mkdirs();
			int trimlen = this.getPath().length();
			MetaDataDedupFile[] files = this.listFiles();
			for (int i = 0; i < files.length; i++) {
				MetaDataDedupFile file = files[i];
				String newPath = snaptoPath + File.separator
						+ file.getPath().substring(trimlen);
				file.snapshot(newPath, overwrite);
			}
			return MetaFileStore.getMF(snaptoPath);
		}
	}

	/**
	 * initiates the MetaDataDedupFile
	 * 
	 * @param path
	 *            the path to the file
	 */
	private void init(String path) {
		this.lastAccessed = System.currentTimeMillis();
		this.path = path;
		File f = new File(path);
		if (!f.exists()) {
			log.finer("Creating new MetaFile for " + this.path);
			this.guid = new UUID().toString();
			monitor = new IOMonitor();
			this.owner_id = Main.defaultOwner;
			this.group_id = Main.defaultGroup;
			this.permissions = Main.defaultFilePermissions;
			this.lastModified = System.currentTimeMillis();
			this.dedup = Main.dedupFiles;
			this.timeStamp = System.currentTimeMillis();
			this.setLength(0, false);
		} else if (f.isDirectory()) {
			this.permissions = Main.defaultDirPermissions;
			this.owner_id = Main.defaultOwner;
			this.group_id = Main.defaultGroup;
			this.directory = true;
			this.length = 4096;
		}

	}

	/**
	 * Writes the stub for this file to disk. Stubs are pointers written to a
	 * file system that map to virtual filesystem directory and file structure.
	 * The stub only contains the guid associated with the file in question.
	 * 
	 * @return true if written
	 */
	private synchronized boolean writeFile() {
		File f = new File(this.path);
		
		if (!f.isDirectory()) {
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			try {
				ObjectOutputStream out = new ObjectOutputStream(
						new FileOutputStream(this.path));
				out.writeObject(this);
				out.close();
			} catch (Exception e) {
				log.log(Level.WARNING, "unable to write file metadata for ["
						+ this.path + "]", e);
				return false;
			}
			return true;

		}
		return true;
	}

	/**
	 * Serializes the file to the MetaFileStore
	 * 
	 * @return true if serialized
	 */
	public boolean unmarshal() {
		return this.writeFile();
	}

	/**
	 * 
	 * @return returns the GUID for the underlying DedupFile
	 */
	public String getDfGuid() {
		return dfGuid;
	}

	/**
	 * 
	 * @return time when file was last modified
	 */
	public long lastModified() {
		if (this.isDirectory())
			return new File(this.path).lastModified();
		return lastModified;
	}

	/**
	 * 
	 * @return creates a blank new file
	 */
	public boolean createNewFile() {
		return this.unmarshal();
	}

	/**
	 * 
	 * @return true if hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * 
	 * @param hidden
	 *            true if hidden
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
		this.unmarshal();
	}

	/**
	 * 
	 * @return true if deleted
	 */
	public boolean deleteStub() {
		File f = new File(this.path);
		return f.delete();
	}

	/**
	 * 
	 * @param df
	 *            the DedupFile that will be referenced within this file
	 */
	protected void setDedupFile(DedupFile df) {
		this.dfGuid = df.getGUID();
	}

	/**
	 * Delete this file only. This is used to delete the file in question but
	 * not the DedupFile Reference
	 * 
	 */
	protected synchronized boolean deleteSelf() {
		File f = new File(this.path);
		MetaFileStore.removedCachedMF(this.path);
		return f.delete();
	}

	/**
	 * 
	 * @return the children of this directory and null if it is not a directory.
	 */
	public String[] list() {
		File f = new File(this.path);
		if (f.isDirectory()) {
			return f.list();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return the children as MetaDataDedupFiles or null if it is not a
	 *         directory
	 */
	public MetaDataDedupFile[] listFiles() {
		File f = new File(this.path);
		if (f.isDirectory()) {
			String[] files = f.list();
			MetaDataDedupFile[] df = new MetaDataDedupFile[files.length];
			for (int i = 0; i < df.length; i++) {

				df[i] = MetaFileStore.getMF(this.getPath() + File.separator
						+ files[i]);
			}
			return df;
		} else {
			return null;
		}
	}

	public boolean mkdir() {
		this.directory = true;
		File f = new File(this.path);
		return f.mkdir();
	}

	public boolean mkdirs() {
		File f = new File(this.path);
		return f.mkdirs();
	}

	public boolean renameTo(String dest) {
		File f = new File(this.path);
		if (f.isDirectory()) {
			return f.renameTo(new File(dest));
		} else {
			boolean rename = f.renameTo(new File(dest));
			if (rename) {
				MetaFileStore.rename(this.path, dest, this);
				this.path = dest;
				this.unmarshal();
			} else {
				log.info("unable to move file");
			}
			return rename;
		}
	}

	public boolean exists() {
		return new File(this.path).exists();
	}

	public String getAbsolutePath() {
		return this.path;
	}

	public String getCanonicalPath() {
		return this.path;
	}

	public String getParent() {
		return new File(this.path).getParent();
	}

	public boolean canExecute() {
		return execute;
	}

	public boolean canRead() {
		return this.read;
	}

	public boolean canWrite() {
		return this.write;
	}

	public boolean setExecutable(boolean executable, boolean ownerOnly) {
		this.execute = executable;
		this.ownerExecOnly = ownerOnly;
		this.unmarshal();
		return true;
	}

	public boolean setExecutable(boolean executable) {
		this.execute = executable;
		this.unmarshal();
		return true;
	}

	public boolean setWritable(boolean writable, boolean ownerOnly) {
		this.write = writable;
		this.ownerWriteOnly = ownerOnly;
		this.unmarshal();
		return true;
	}

	public boolean setWritable(boolean writable) {
		this.write = writable;
		this.unmarshal();
		return true;
	}

	public boolean setReadable(boolean readable, boolean ownerOnly) {
		this.read = readable;
		this.ownerReadOnly = ownerOnly;
		this.unmarshal();
		return true;
	}

	public boolean setReadable(boolean readable) {
		this.read = readable;
		this.unmarshal();
		return true;
	}

	public void setReadOnly() {
		this.read = true;
		this.unmarshal();
	}

	public boolean isFile() {
		return new File(this.path).isFile();
	}

	public boolean isDirectory() {
		return new File(this.path).isDirectory();
	}

	public String getName() {
		return new File(this.path).getName();

	}

	/**
	 * @param lastModified
	 *            the lastModified to set
	 */

	public boolean setLastModified(long lastModified) {
		this.lastModified = lastModified;
		this.lastAccessed = lastModified;
		return true;
	}

	/**
	 * @return the timeStamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp
	 *            the timeStamp to set
	 */
	public void setTimeStamp(long timeStamp, boolean serialize) {
		this.timeStamp = timeStamp;
		if (serialize)
			this.unmarshal();
	}

	/**
	 * @return the length
	 */
	public long length() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(long l, boolean serialize) {
		long len = l - this.length;
		Main.volume.updateCurrentSize(len);
		this.length = l;
		if (serialize)
			this.unmarshal();
	}

	/**
	 * @return the path to the file stub on disk
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 
	 * @param filePath
	 *            the path to the file
	 * @return true if the file exists
	 */
	public static boolean exists(String filePath) {
		File f = new File(filePath);
		return f.exists();
	}

	public boolean isAbsolute() {
		return true;
	}

	public int hashCode() {
		return new File(this.path).hashCode();
	}

	/**
	 * writes all the metadata and the Dedup blocks to the dedup chunk service
	 */
	public void sync() {
		this.unmarshal();
	}

	/**
	 * 
	 * @param lastAccessed
	 */
	public void setLastAccessed(long lastAccessed) {
		this.lastAccessed = lastAccessed;
	}

	public long getLastAccessed() {
		return lastAccessed;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {

		this.timeStamp = in.readLong();
		this.length = in.readLong();
		this.lastModified = in.readLong();
		this.lastAccessed = in.readLong();
		this.execute = in.readBoolean();
		this.read = in.readBoolean();
		this.write = in.readBoolean();
		this.directory = in.readBoolean();
		this.hidden = in.readBoolean();
		this.ownerWriteOnly = in.readBoolean();
		this.ownerExecOnly = in.readBoolean();
		this.ownerReadOnly = in.readBoolean();
		int dfgl = in.readInt();
		if (dfgl == -1) {
			this.dfGuid = null;
		} else {
			byte[] dfb = new byte[dfgl];
			in.read(dfb);
			this.dfGuid = new String(dfb);
		}
		int gl = in.readInt();
		byte[] gfb = new byte[gl];
		in.read(gfb);
		this.guid = new String(gfb);

		int ml = in.readInt();
		if (ml == -1) {
			this.monitor = null;
		} else {
			byte[] mlb = new byte[ml];
			in.read(mlb);
			this.guid = new String(mlb);
		}
		this.vmdk = in.readBoolean();
		this.owner_id = in.readInt();
		this.group_id = in.readInt();
		byte[] hmb = new byte[in.readInt()];
		this.extendedAttrs = ByteUtils.deSerializeHashMap(hmb);
		this.dedup = in.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(timeStamp);
		out.writeLong(length);
		out.writeLong(lastModified);
		out.writeLong(lastAccessed);
		out.writeBoolean(execute);
		out.writeBoolean(read);
		out.writeBoolean(write);
		out.writeBoolean(directory);
		out.writeBoolean(hidden);
		out.writeBoolean(ownerWriteOnly);
		out.writeBoolean(ownerExecOnly);
		out.writeBoolean(ownerReadOnly);

		if (this.dfGuid != null) {
			byte[] dfb = this.dfGuid.getBytes();
			out.writeInt(dfb.length);
			out.write(dfb);
		} else {
			out.writeInt(-1);
		}
		byte[] dfb = this.guid.getBytes();
		out.writeInt(dfb.length);
		out.write(dfb);
		if (this.monitor != null) {
			byte[] mfb = this.monitor.toByteArray();
			out.writeInt(mfb.length);
			out.write(mfb);
		} else {
			out.writeInt(-1);
		}
		out.writeBoolean(vmdk);
		out.writeInt(owner_id);
		out.writeInt(group_id);
		byte[] hmb = ByteUtils.serializeHashMap(extendedAttrs);
		out.writeInt(hmb.length);
		out.write(hmb);
		out.writeBoolean(dedup);
	}
}
