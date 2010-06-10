package org.merapi.helper.messages;

import org.merapi.helper.vo.AllgemeinVO;
import org.merapi.helper.vo.DateienVO;
import org.merapi.helper.vo.PeersVO;
import org.merapi.helper.vo.TeileVO;
import org.merapi.helper.vo.TrackerVO;
import org.merapi.messages.Message;

public class DetailInfoMessage extends Message {

	
	public static final String DETAIL_INFO = "detailInfo";
	
	public static final String ADD_DETAIL_INFO = "addDetailInfo";
	
	//Sub-Action constants for adding details
	public static final String ADD_DETAIL_ALLGEMEIN_INFO = "addDetailAllgemeinInfo";
	public static final String ADD_DETAIL_TRACKER_INFO = "addDetailTrackerInfo";
	public static final String ADD_DETAIL_PEERS_INFO = "addDetailPeersInfo";
	public static final String ADD_DETAIL_TEILE_INFO = "addDetailTeileInfo";
	public static final String ADD_DETAIL_DATEIEN_INFO = "addDetailDateienInfo";
	
	
	public static final String UPDATE_DETAIL_INFO = "updateDetailInfo";
	
	//Sub-Action constants for updating details
	public static final String UPDATE_DETAIL_ALLGEMEIN_INFO = "updateDetailAllgemeinInfo";
	public static final String UPDATE_DETAIL_TRACKER_INFO = "updateDetailTrackerInfo";
	public static final String UPDATE_DETAIL_PEERS_INFO = "updateDetailPeersInfo";
	public static final String UPDATE_DETAIL_TEILE_INFO = "updateDetailTeileInfo";
	public static final String UPDATE_DETAIL_DATEIEN_INFO = "updateDetailDateienInfo";

	//unique id for each row
	public String rowId = null;
	
	public String action = null;
	public String subAction = null;
	
	
//	public AllgemeinVO allgemeinVO = null;
//	public DateienVO dateinVO = null;
//	public PeersVO peersVO = null;
//	public TeileVO teileVO = null;
//	public TrackerVO trackerVO = null;
	
	
	//Section for addDetailedData
	public String trackerName = null;
	
	//Section for updateDetailedData
	public String trackerStatus = null;
	public String trackerNaechstUpdate = null;
	public int trackerSeeds = 0;
	public int trackerPeers = 0;
	public String trackerGeladen = null;
	
	
	/**
	 * Constructor.
	 */
	public DetailInfoMessage() {
		super(DETAIL_INFO);
	
	}
}
