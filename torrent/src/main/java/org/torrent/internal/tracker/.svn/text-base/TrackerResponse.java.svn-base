package org.torrent.internal.tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.torrent.internal.bencoding.BList;
import org.torrent.internal.bencoding.BMap;
import org.torrent.internal.bencoding.BTypeException;
import org.torrent.internal.peer.PeerInfo;
import org.torrent.internal.util.Validator;

public class TrackerResponse {
	private static final String FAILURE_REASON = "failure reason";

	private static final String WARNING_MESSAGE = "warning message";

	private static final String INTERVAL = "interval";

	private static final String MIN_INTERVAL = "min interval";

	private static final String TRACKER_ID = "tracker id";

	private static final String COMPLETE = "complete";

	private static final String INCOMPLETE = "incomplete";

	private static final String PEERS = "peers";

	private String failureReason;
	private String warningMessage;

	private int interval;

	private Integer minInterval;

	private byte[] trackerID;

	private Integer complete;

	private Integer incomplete;

	private List<PeerInfo> peerList;

	private boolean compact;

	/**
	 * @param response
	 * @throws BTypeException
	 * @throws IllegalArgumentException
	 *             if the response contains errors
	 */
	public TrackerResponse(BMap response) throws BTypeException {
		Validator.notNull(response, "Response is null!");
		if (response.containsKey(FAILURE_REASON)) {
			failureReason = response.getString(FAILURE_REASON);
			return;
		}
		Validator.nonNull(response.get(INTERVAL), response.get(PEERS));

		warningMessage = response.getString(WARNING_MESSAGE);
		interval = response.getInteger(INTERVAL);
		minInterval = response.getInteger(MIN_INTERVAL);
		trackerID = (byte[]) response.get(TRACKER_ID);

		complete = response.getInteger(COMPLETE);
		incomplete = response.getInteger(INCOMPLETE);

		Object peers = response.get(PEERS);
		if (peers instanceof BList) {
			BList list = (BList) peers;
			peerList = new ArrayList<PeerInfo>(list.size());
			for (int i = 0; i < list.size(); i++) {
				peerList.add(PeerInfo.fromBMap(list.getMap(i)));
			}
		} else {
			byte[] list = (byte[]) peers;
			Validator.isTrue(list.length % 6 == 0,
					"Peerlist not in format IPv4:port!");
			peerList = new ArrayList<PeerInfo>(list.length / 6);
			for (int i = 0; i < list.length; i += 6) {
				peerList.add(PeerInfo.fromRawIP(list, i, 6));
			}
			compact = true;
		}
	}

	public boolean hasFailed() {
		return failureReason != null;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public boolean isCompact() {
		return compact;
	}

	public int getInterval() {
		return interval;
	}

	public Integer getMinInterval() {
		return minInterval;
	}

	public byte[] getTrackerID() {
		return Arrays.copyOf(trackerID, trackerID.length);
	}

	public Integer getComplete() {
		return complete;
	}

	public Integer getIncomplete() {
		return incomplete;
	}

	public List<PeerInfo> getPeerList() {
		return Collections.unmodifiableList(peerList);
	}

}
