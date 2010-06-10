package org.merapi.helper.vo;


public class TrackerVO extends Object {

	//addDetailData - tracker --> static
	public String trackerName = null;
	
	//updateDetailData - tracker --> dynamic
	public String trackerStatus = null;
	public String trackerNaechstUpdate = null;	//
	public int trackerSeeds = 0;
	public int trackerPeers = 0;
	public String trackerGeladen = null;	//
	
	public TrackerVO(String trackerName, String trackerStatus, String trackerNaechstUpdate,
			int trackerSeeds, int trackerPeers, String trackerGeladen) {
		this.trackerName = trackerName;
		
		this.trackerStatus = trackerStatus;
		this.trackerNaechstUpdate = trackerNaechstUpdate;
		this.trackerSeeds = trackerSeeds;
		this.trackerPeers = trackerPeers;
		this.trackerGeladen = trackerGeladen;
	}
	
//	public String getTrackerName() {
//		return trackerName;
//	}
//	public void setTrackerName(String trackerName) {
//		this.trackerName = trackerName;
//	}
//	public String getTrackerStatus() {
//		return trackerStatus;
//	}
//	public void setTrackerStatus(String trackerStatus) {
//		this.trackerStatus = trackerStatus;
//	}
//	public String getTrackerNaechstUpdate() {
//		return trackerNaechstUpdate;
//	}
//	public void setTrackerNaechstUpdate(String trackerNaechstUpdate) {
//		this.trackerNaechstUpdate = trackerNaechstUpdate;
//	}
//	public int getTrackerSeeds() {
//		return trackerSeeds;
//	}
//	public void setTrackerSeeds(int trackerSeeds) {
//		this.trackerSeeds = trackerSeeds;
//	}
//	public int getTrackerPeers() {
//		return trackerPeers;
//	}
//	public void setTrackerPeers(int trackerPeers) {
//		this.trackerPeers = trackerPeers;
//	}
//	public String getTrackerGeladen() {
//		return trackerGeladen;
//	}
//	public void setTrackerGeladen(String trackerGeladen) {
//		this.trackerGeladen = trackerGeladen;
//	}
	
}
