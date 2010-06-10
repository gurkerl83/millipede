package lbms.plugins.mldht.kad;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Comparator;

import lbms.plugins.mldht.kad.DHT.DHTtype;

/**
 * @author Damokles
 *
 */
public class DBItem {

	private byte[] item;
	private final long	time_stamp;

	private DBItem () {
		time_stamp = System.currentTimeMillis();
	}

	public DBItem (final byte[] ip_port) {
		this();
		item = ip_port.clone();
	}

	public DBItem (final DBItem dbItem) {
		this();
		item = dbItem.item.clone();		
	}

	/// See if the item is expired
	public boolean expired (final long now) {
		return (now - time_stamp >= DHTConstants.MAX_ITEM_AGE);
	}

	/// Get the data of an item
	public byte[] getData () {
		return item;
	}

	@Override
	public String toString() {
		try
		{
			if(item.length == DHTtype.IPV4_DHT.ADDRESS_ENTRY_LENGTH )
				return new InetSocketAddress(InetAddress.getByAddress(Arrays.copyOf(item, 4)),(item[4]&0xff)<<8 | item[5]&0xff).toString();
			else if(item.length == DHTtype.IPV6_DHT.ADDRESS_ENTRY_LENGTH )
				return new InetSocketAddress(InetAddress.getByAddress(Arrays.copyOf(item, 16)),(item[16]&0xff)<<8 | item[17]&0xff).toString();
			
			return new String(item);
		} catch (final Exception e)
		{
			return super.toString();
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if(obj instanceof DBItem)
		{
			byte[] otherItem = ((DBItem)obj).item;
			return Arrays.equals(item, otherItem);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(item);
	}

	public static final Comparator<DBItem> ageOrdering = new Comparator<DBItem>() {
		public int compare(final DBItem o1, final DBItem o2) {
			return (int)(o1.time_stamp - o2.time_stamp);
		}
	};
}
