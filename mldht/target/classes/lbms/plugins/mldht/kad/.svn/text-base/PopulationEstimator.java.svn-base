package lbms.plugins.mldht.kad;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import lbms.plugins.mldht.kad.DHT.LogLevel;

/**
 * @author The_8472, Damokles
 *
 */
public class PopulationEstimator {

	static final int						KEYSPACE_BITS					= Key.SHA1_HASH_LENGTH * 8;
	static final double						KEYSPACE_SIZE					= Math.pow(2, KEYSPACE_BITS);
	
	// try to close in to a rough estimate fast
	static final double						DISTANCE_INITIAL_WEIGHT			= 0.1;
	static final int						INITIAL_WEIGHT_COUNT			= 45;
	// apply less weight to individual estimates later on
	static final double						DISTANCE_WEIGHT					= 0.03;

	
	private double					averageNodeDistanceExp2			= KEYSPACE_BITS;
	private int						updateCount						= 0;
	private List<PopulationListener>	listeners						= new ArrayList<PopulationListener>(1);
	
	private static final int				MAX_RECENT_LOOKUP_CACHE_SIZE	= 40;
	private Deque<Key>				recentlySeenPrefixes			= new LinkedList<Key>();

	public long getEstimate () {
		return (long) (KEYSPACE_SIZE / Math.pow(2, averageNodeDistanceExp2));
	}
	
	double getRawDistanceEstimate() {
		return averageNodeDistanceExp2;
	}

	void setInitialRawDistanceEstimate(double initialValue) {
		averageNodeDistanceExp2 = initialValue;
		if(averageNodeDistanceExp2 > KEYSPACE_BITS)
			averageNodeDistanceExp2 = KEYSPACE_BITS;
	}

	public void update (SortedSet<Key> neighbors) {
		if(neighbors.size() < 2)
			return;
		DHT.log("Estimator: new node group of "+neighbors.size(), LogLevel.Debug);
		Key prefix = Key.getCommonPrefix(neighbors);
		
		synchronized (recentlySeenPrefixes)
		{
			for(Key oldPrefix : recentlySeenPrefixes)
			{
				if(prefix.isPrefixOf(oldPrefix) || oldPrefix.isPrefixOf(prefix))
				{
					/*
					 * displace old entry, narrower entries will also replace
					 * wider ones, to clean out accidents like prefixes covering
					 * huge fractions of the keyspace
					 */
					recentlySeenPrefixes.remove(oldPrefix);
					recentlySeenPrefixes.addLast(prefix);
					return;
				}
			}

			// no match found => add
			recentlySeenPrefixes.addLast(prefix);
			if(recentlySeenPrefixes.size() > MAX_RECENT_LOOKUP_CACHE_SIZE)
				recentlySeenPrefixes.removeFirst();
		}
		
		Key previous = null;
		for (Key entry : neighbors) {
			if (previous == null) {
				previous = entry;
				continue;
			}

			byte[] rawDistance = previous.distance(entry).getHash();

			double distance = 0;

			int nonZeroBytes = 0;
			for (int i = 0; i < Key.SHA1_HASH_LENGTH; i++) {
				if (rawDistance[i] == 0) {
					continue;
				}
				if (nonZeroBytes == 4) {
					break;
				}
				nonZeroBytes++;
				distance += (rawDistance[i] & 0xFF)
						* Math.pow(2, KEYSPACE_BITS - (i + 1) * 8 - 1);
			}

			/*
			 * weighted average of the exponents (since single results can be
			 * off by several orders of magnitude -> logarithm dampens that
			 * exponentially)
			 */
			
			distance = Math.log(distance)/Math.log(2);
			
			double weight;

			if (updateCount < INITIAL_WEIGHT_COUNT)
				weight = DISTANCE_INITIAL_WEIGHT;
			else
				weight = DISTANCE_WEIGHT;
			synchronized (PopulationEstimator.class)
			{
				averageNodeDistanceExp2 = distance * weight + averageNodeDistanceExp2 * (1. - weight);
			}
			
			updateCount++;
			DHT.log("Estimator: distance update #"+updateCount+": " + distance + " avg:" + averageNodeDistanceExp2, LogLevel.Debug);

			previous = entry;
		}
		
		DHT.log("Estimator: new estimate:"+getEstimate(), LogLevel.Info);
		
		fireUpdateEvent();
	}

	public void addListener (PopulationListener l) {
		listeners.add(l);
	}

	public void removeListener (PopulationListener l) {
		listeners.remove(l);
	}

	private void fireUpdateEvent () {
		long estimated = getEstimate();
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).populationUpdated(estimated);
		}
	}
}
