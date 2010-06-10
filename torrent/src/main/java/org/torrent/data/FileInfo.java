package org.torrent.data;

public class FileInfo {
    //has to be public/protected to be serialized java -> as3
	
	public boolean isSelected = true;
	
//	public boolean isSelected() {
//		return isSelected;
//	}
//
//	public void setSelected(boolean isSelected) {
//		this.isSelected = isSelected;
//	}

	protected String fileName;
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	protected long length;

	public FileInfo(String fileName, long length) {
		this.fileName = fileName;
		this.length = length;
	}

//	public String getFileName() {
//		return fileName;
//	}
//
//	public long getLength() {
//		return length;
//	}

	@Override
	public int hashCode() {
		return fileName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FileInfo) {
			FileInfo o = (FileInfo) obj;
			return fileName.equals(o.fileName) && length == o.length;
		}
		return false;
	}
}
