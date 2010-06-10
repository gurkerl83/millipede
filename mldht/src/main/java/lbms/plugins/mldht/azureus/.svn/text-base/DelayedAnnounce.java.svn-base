package lbms.plugins.mldht.azureus;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.gudy.azureus2.plugins.download.Download;


/**
 * @author Damokles
 *
 */
public class DelayedAnnounce implements Delayed {

	private long		timestamp;
	private Download	download;

	/**
	 * @param download Download to delay
	 * @param delay in Microseconds
	 */
	public DelayedAnnounce (Download download, long delay) {
		this(download, delay, TimeUnit.MILLISECONDS);
	}

	/**
	 * @param download Download to delay
	 * @param delay delay in the given TimeUnit
	 * @param unit TimeUnit of the delay
	 */
	public DelayedAnnounce (Download download, long delay, TimeUnit unit) {
		this.download = download;
		timestamp = System.currentTimeMillis()
				+ TimeUnit.MILLISECONDS.convert(delay, unit);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit)
	 */
	public long getDelay (TimeUnit unit) {
		return unit.convert(timestamp - System.currentTimeMillis(),
				TimeUnit.MILLISECONDS);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo (Delayed o) {
		long x = getDelay(TimeUnit.MILLISECONDS)
				- o.getDelay(TimeUnit.MILLISECONDS);
		if (x > 0) {
			return 1;
		} else if (x < 0) {
			return -1;
		}
		return 0;
	}

	/**
	 * @return the download
	 */
	public Download getDownload () {
		return download;
	}
}
