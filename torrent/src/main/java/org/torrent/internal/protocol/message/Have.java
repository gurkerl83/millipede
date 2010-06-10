package org.torrent.internal.protocol.message;

import org.torrent.internal.util.Validator;

public class Have implements BittorrentMessage {

	private final int pieceIndex;

	/*
	 * for realtime purposes
	 */
	private final int aib;
	
	public Have(int pieceIndex) {
		Validator.isTrue(pieceIndex >= 0, "Index invalid: " + pieceIndex);
		this.pieceIndex = pieceIndex;
		this.aib = -1;
	}

	public Have(int pieceIndex, int aib) {
		this.pieceIndex = pieceIndex;
		this.aib = aib;
	}
	
	public int getPieceIndex() {
		return pieceIndex;
	}

	public int getAib() {
		return aib;
	}

	@Override
	public void accept(BTMessageVisitor visitor) {
		visitor.visitHave(this);
	}

	@Override
	public String toString() {
		return "Have: " + pieceIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pieceIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Have other = (Have) obj;
		if (pieceIndex != other.pieceIndex)
			return false;
		return true;
	}
}
