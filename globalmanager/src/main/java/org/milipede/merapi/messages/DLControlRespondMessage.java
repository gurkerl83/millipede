package org.milipede.merapi.messages;

//package org.milipede.merapi.messages;
//
//import org.merapi.messages.Message;
//
//public class DLControlRespondMessage extends Message {
//
//	// --------------------------------------------------------------------------
//	//
//	// Constants
//	//
//	// --------------------------------------------------------------------------
//	public static final String ITEM_ADDED = "itemAdded";
//	public static final String ITEM_REMOVED = "itemRemoved";
//
//	public static final String ITEM_UPDATED = "itemUpdated";
//	/**
//	 * Message type for a SAY_IT message.
//	 */
//	public static final String DL_CONTROL_RESPOND = "dlControlRespond";
//
//	// --------------------------------------------------------------------------
//	//
//	// Constructor
//	//
//	// --------------------------------------------------------------------------
//	/**
//	 * Constructor.
//	 */
//	public DLControlRespondMessage() {
//		super(DL_CONTROL_RESPOND);
//
//	}
//
//	// --------------------------------------------------------------------------
//	//
//	// Properties
//	//
//	// --------------------------------------------------------------------------
//
//	/**
//	 * A custom data property that contains the action data
//	 */
//	public String action = null;
//
//	/**
//	 * A custom data property that contains the hash value removeTorrent
//	 */
//	public String infoHash = null;
//	public String name = null;
//	public int cols = 0;
//	public int rows = 0;
//
//	//local Up/Down -Bandwidth and -Transfer
//	public long luB = 0;
//	public long luT = 0;
//	public long ldB = 0;
//	public long ldT = 0;
//
//	public float progress = 0;
//
////	/**
////	 * A set of args to use as the system execute parameters
////	 */
////	public String[] getArgs() {
////		Object[] data = (Object[]) getData();
////		String[] args = new String[data.length];
////		for (int i = 0; i < data.length; i++) {
////			args[i] = (String) data[i];
////		}
////		return args;
////	}
////
////	public void setArgs(String[] stringArray) {
////		// for (int i = 0; i< stringArray.length ; i++) {
////		// this.setData(stringArray[i]);
////		// }
////		this.setData(stringArray);
////	}
//}