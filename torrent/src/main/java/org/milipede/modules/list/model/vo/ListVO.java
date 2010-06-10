package org.milipede.modules.list.model.vo;

public class ListVO {

	public static final int START = 1;
	public static final int PAUSE = 2;
	public static final int STOP = 3;
	
	private String infoHash = "";
	private String name = "";
	private String mediaType = "";
	private long size = 0;

	private long luB = 0;
	private long luT = 0;
	private long ldB = 0;
	private long ldT = 0;

	private boolean isTurbo = false;

	private long progress = 0;

	private int cols = 0;
	private int rows = 0;
	
	private String text;
	private boolean selectionEnabled = true;

	//context properties
	private int status = 0;
	
	// public ListVO(String infoHash, String name, int size, String mediaType,
	// boolean isTurbo, int luB, int luT,
	// int ldB, int ldT, long progress) {
	//		
	// this.infoHash = infoHash;
	// this.name = name;
	// this.size = size;
	// this.mediaType = mediaType;
	// this.isTurbo = isTurbo;
	//		
	// this.luB = luB;
	// this.luT = luT;
	// this.ldB = ldB;
	// this.ldT = ldT;
	//		
	// this.progress = progress;
	//	
	// }

	public ListVO(String asHexString, String baseDir, long dataSize,
			String mediaType, boolean isTurbo, long luB, long luT, long ldB,
			long ldT, long progress, int cols, int rows) {

		this.infoHash = asHexString;
		this.name = baseDir;
		this.size = dataSize;
		this.mediaType = mediaType;
		this.isTurbo = isTurbo;

		this.luB = luB;
		this.luT = luT;
		this.ldB = ldB;
		this.ldT = ldT;

		this.progress = progress;

		this.cols = cols;
		this.rows = rows;
	}

	public String getInfoHash() {
		return infoHash;
	}

	public void setInfoHash(String infoHash) {
		this.infoHash = infoHash;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getLuB() {
		return luB;
	}

	public void setLuB(long luB) {
		this.luB = luB;
	}

	public long getLuT() {
		return luT;
	}

	public void setLuT(long luT) {
		this.luT = luT;
	}

	public long getLdB() {
		return ldB;
	}

	public void setLdB(long ldB) {
		this.ldB = ldB;
	}

	public long getLdT() {
		return ldT;
	}

	public void setLdT(long ldT) {
		this.ldT = ldT;
	}

	public boolean isTurbo() {
		return isTurbo;
	}

	public void setTurbo(boolean isTurbo) {
		this.isTurbo = isTurbo;
	}

	public long getProgress() {
		return progress;
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}
	
	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isSelectionEnabled() {
		return selectionEnabled;
	}

	public void setSelectionEnabled(boolean selectionEnabled) {
		this.selectionEnabled = selectionEnabled;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
