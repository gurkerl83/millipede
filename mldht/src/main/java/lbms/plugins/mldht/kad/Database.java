package lbms.plugins.mldht.kad;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lbms.plugins.mldht.kad.DHT.DHTtype;

/**
 * @author Damokles
 * 
 */
public class Database {
	private Map<Key, Set<DBItem>>	items;
	private Map<ByteWrapper, Long>			tokens;
	private DatabaseStats			stats;

	Database()
	{
		stats = new DatabaseStats();
		items = new HashMap<Key, Set<DBItem>>();
		tokens = new HashMap<ByteWrapper, Long>();
	}

	/**
	 * Store an entry in the database
	 * 
	 * @param key
	 *            The key
	 * @param dbi
	 *            The DBItem to store
	 */
	void store(Key key, DBItem dbi) {
		synchronized (items)
		{
			if (!items.containsKey(key))
			{
				insert(key);
			}
			Set<DBItem> keyEntries = items.get(key);
			if (!keyEntries.remove(dbi))
				stats.setItemCount(stats.getItemCount() + 1);
			keyEntries.add(dbi);
		}
	}

	/**
	 * Get max_entries items from the database, which have the same key, items
	 * are taken randomly from the list. If the key is not present no items will
	 * be returned, if there are fewer then max_entries items for the key, all
	 * entries will be returned
	 * 
	 * @param key
	 *            The key to search for
	 * @param dbl
	 *            The list to store the items in
	 * @param max_entries
	 *            The maximum number entries
	 */
	void sample(Key key, List<DBItem> tdbl, int max_entries, DHTtype forType) {
		synchronized (items)
		{
			if (items.containsKey(key))
			{
				List<DBItem> dbl = new ArrayList<DBItem>(items.get(key));
				Collections.shuffle(dbl);
				int num_added = 0;
				for (DBItem item : dbl)
				{
					if(forType.ADDRESS_ENTRY_LENGTH != item.getData().length)
						continue;
					if (num_added >= max_entries)
					{
						break;
					}
					tdbl.add(item);
					num_added++;
				}
			}
		}
	}

	/**
	 * Expire all items older then 30 minutes
	 * 
	 * @param now
	 *            The time it is now (we pass this along so we only have to
	 *            calculate it once)
	 */
	void expire(long now) {
		int removed = 0;
		List<Key> keysToRemove = new ArrayList<Key>();
		synchronized (items)
		{
			int itemCount = 0;
			for (Key k : items.keySet())
			{
				Set<DBItem> dbl = items.get(k);
				List<DBItem> tmp = new ArrayList<DBItem>(dbl);
				Collections.sort(tmp, DBItem.ageOrdering);
				while (dbl.size() > 0 && tmp.get(0).expired(now))
				{
					dbl.remove(tmp.remove(0));
					removed++;
				}
				if (dbl.size() == 0)
				{
					keysToRemove.add(k);
				} else
					itemCount += dbl.size();
			}

			items.keySet().removeAll(keysToRemove);
			stats.setKeyCount(items.size());
			stats.setItemCount(itemCount);
		}
		List<ByteWrapper> tokensToRemove = new ArrayList<ByteWrapper>(); 
		synchronized (tokens)
		{
			for (ByteWrapper t : tokens.keySet())
			{
				if (now - tokens.get(t) > 3 * 60 * 1000)
				{ //tokens are invalid after 3 minutes
					tokensToRemove.add(t);
				}
			}
			
			tokens.keySet().removeAll(tokensToRemove);
		}
	}

	/**
	 * Generate a write token, which will give peers write access to the DB.
	 * 
	 * @param ip
	 *            The IP of the peer
	 * @param port
	 *            The port of the peer
	 * @return A Key
	 */
	ByteWrapper genToken(InetAddress ip, int port, Key lookupKey) {
		byte[] tdata = new byte[30 + ip.getAddress().length];
		long now = System.currentTimeMillis();
		// generate a hash of the ip port and the current time
		// should prevent anybody from crapping things up
		ByteBuffer bb = ByteBuffer.wrap(tdata);
		bb.put(ip.getAddress());
		bb.putShort((short) port);
		bb.putLong(now);
		bb.put(lookupKey.getHash());
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(tdata);
			ByteWrapper token = new ByteWrapper(md.digest());
			// keep track of the token, tokens will expire after a while
			synchronized (tokens)
			{
				tokens.put(token, now);
			}
			return token;
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			return new ByteWrapper(new byte[20]);
		}
	}

	/**
	 * Check if a received token is OK.
	 * 
	 * @param token
	 *            The token received
	 * @param ip
	 *            The ip of the sender
	 * @param port
	 *            The port of the sender
	 * @return true if the token was given to this peer, false other wise
	 */
	boolean checkToken(ByteWrapper token, InetAddress ip, int port, Key lookupKey) {
		synchronized (tokens)
		{
			// the token must be in the map
			if (!tokens.containsKey(token))
			{
				DHT.logDebug("Received Unknown token from " + ip.getHostAddress());
				return false;
			}
			// in the map so now get the timestamp and regenerate the token
			// using the IP and port of the sender
			long ts = tokens.get(token);
			byte[] tdata = new byte[30 + ip.getAddress().length];
			ByteBuffer bb = ByteBuffer.wrap(tdata);
			bb.put(ip.getAddress());
			bb.putShort((short) port);
			bb.putLong(ts);
			bb.put(lookupKey.getHash());
			try
			{
				MessageDigest md = MessageDigest.getInstance("SHA-1");
				md.update(tdata);
				ByteWrapper ct = new ByteWrapper(md.digest());
				// compare the generated token to the one received
				if (!token.equals(ct)) // not good, this peer didn't went through the proper channels
				{
					DHT.logDebug("Received Invalid token from " + ip.getHostAddress());
					return false;
				}
				// expire the token
				tokens.remove(token);
				return true;
			} catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
				return false;
			}
		}
	}

	/// Test wether or not the DB contains a key
	boolean contains(Key key) {
		synchronized (items)
		{
			return items.containsKey(key);
		}
	}

	/// Insert an empty item (only if it isn't already in the DB)
	void insert(Key key) {
		synchronized (items)
		{
			Set<DBItem> dbl = items.get(key);
			if (dbl == null)
			{
				items.put(key, new HashSet<DBItem>());
				stats.setKeyCount(items.size());
			}
		}
	}

	/**
	 * @return the stats
	 */
	public DatabaseStats getStats() {
		return stats;
	}
}
