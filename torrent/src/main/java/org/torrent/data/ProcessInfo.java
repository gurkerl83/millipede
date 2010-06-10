package org.torrent.data;

import java.util.List;

public class ProcessInfo {

	protected List<FileInfo> files;
	public List<FileInfo> getFiles() {
		return files;
	}
	public void setFiles(List<FileInfo> files) {
		this.files = files;
	}
	
	protected String savePath;
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	
	protected String status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
