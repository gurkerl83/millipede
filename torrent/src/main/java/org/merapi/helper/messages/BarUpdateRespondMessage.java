package org.merapi.helper.messages;

import org.merapi.messages.Message;

public class BarUpdateRespondMessage extends Message {

	// --------------------------------------------------------------------------
	//
	// Constants
	//
	// --------------------------------------------------------------------------
//	public static final String ITEM_ADDED = "itemAdded";
//	public static final String ITEM_REMOVED = "itemRemoved";
	
	public static final String ADD_BAR_DATA = "addBarData";
	public static final String UPDATE_BAR_DATA = "updateBarData";

	public static final String ADD_DETAILED_DATA = "addDetailedData";
	public static final String UPDATE_DETAILED_DATA = "updateDetailedData";
	
	/**
	 * Message type for a SAY_IT message.
	 */
	public static final String BAR_UPDATE_RESPOND = "barUpdateRespond";

	// --------------------------------------------------------------------------
	//
	// Constructor
	//
	// --------------------------------------------------------------------------
	/**
	 * Constructor.
	 */
	public BarUpdateRespondMessage() {
		super(BAR_UPDATE_RESPOND);
	
	}

	// --------------------------------------------------------------------------
	//
	// Properties
	//
	// --------------------------------------------------------------------------

	/**
	 * A custom data property that contains the action data
	 */
	public String action = null;

	/**
	 * A custom data property that contains the hash value removeTorrent
	 */
	public String infoHash = null;

	public int cols = 0;
	public long rows = 0;
	
	public int lostCol = 0;
	
//	//local Up/Down -Bandwidth and -Transfer
//	public long luB = 0;
//	public long luT = 0;
//	public long ldB = 0;
//	public long ldT = 0;
//	
//	public float progress = 0; 
	
	
	//global Up/Down -Bandwidth and -Transfer
//	public long guB = 0;
//	public long guT = 0;
//	public long gdB = 0;
//	public long gdT = 0;
	
	
//	/**
//	 * A set of args to use as the system execute parameters
//	 */
//	public String[] getArgs() {
//		Object[] data = (Object[]) getData();
//		String[] args = new String[data.length];
//		for (int i = 0; i < data.length; i++) {
//			args[i] = (String) data[i];
//		}
//		return args;
//	}
//
//	public void setArgs(String[] stringArray) {
//		// for (int i = 0; i< stringArray.length ; i++) {
//		// this.setData(stringArray[i]);
//		// }
//		this.setData(stringArray);
//	}
	
	//Section for addDetailedData
	public String trackerName = null;
	
	public String dateienPfad = null;
	public int dateienGroesse = 0;
	public int dateienAnzahlTeile = 0;
//	public int dateienDarstellungTeile = 0; //intern itemRenderer
	public int dateienPrioritaet = 0;
	
	//Section for updateDetailedData
	public String trackerStatus = null;
//	public String trackerNaechstUpdate = null;
	public int trackerSeeds = 0;
	public int trackerPeers = 0;
//	public String trackerGeladen:String = null;
	
	public int teileNr = 0;
	public int teileGroesse = 0;
	public int teileAnzahlBloecke = 0;
//	public int teileDarstellungBloecke = 0; //intern itemRenderer
	public int teileFertig = 0;
	public int teileVerfuegbarkeit = 0;
	public int teileModus = 0;
	
	public int dateienFertig = 0;
	public int dateienProzent = 0;
	
	public String peersIP = null;
	public String peersProgramm = null;
	public String peersFlags = null;
	public int peersProzent = 0;
	public int peersDLRate = 0;
	public int peersULRate = 0;
	public int peersAnfragen = 0;
	public int peersUpgeloaded = 0;
	public int peersGeladen = 0;
	public int peersPeerDL = 0;
}