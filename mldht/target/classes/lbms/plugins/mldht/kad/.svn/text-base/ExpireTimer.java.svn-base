package lbms.plugins.mldht.kad;

/**
 * @author Damokles
 *
 */
public class ExpireTimer {

	private long	last;

	public ExpireTimer () {
		last = System.currentTimeMillis();
	}

	void update () {
		last = System.currentTimeMillis();
	}

	long getElapsedSinceUpdate () {
		long now = System.currentTimeMillis();

		long d = now - last;
		if (d < 0) {
			d = 0;
		}
		return d;
	}
}
