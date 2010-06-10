package ch.cyberduck.core.io.service;

public interface BandwidthThrottleService {

	/**
	 * No throttling
	 */
	public static final int UNLIMITED = -1;

	/**
	 * Sets the throttle to the given throttle rate.  The default windows size
	 * T is used.  The bytes per windows N is calculated from bytesPerSecond.
	 *
	 * @param bytesPerSecond the limits in bytes (not bits!) per second
	 *                       (not milliseconds!)
	 */
	public abstract void setRate(float bytesPerSecond);

	/**
	 *
	 * @return Transfer rate in bytes per second allowed by this throttle
	 */
	public abstract float getRate();

	/**
	 * Sets whether or not this throttle is switching bandwidth on/off.
	 */
	public abstract void setSwitching(boolean switching);

	/**
	 * Blocks until the caller can send at least one byte without violating
	 * bandwidth constraints.  Records the number of byte sent.
	 *
	 * @param desired the number of bytes the caller would like to send
	 * @return the number of bytes the sender is expected to send, which
	 *         is always greater than one and less than or equal to desired
	 */
	public abstract int request(int desired);

}