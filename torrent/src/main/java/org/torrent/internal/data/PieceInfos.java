package org.torrent.internal.data;

import java.util.Iterator;

import org.torrent.internal.util.Validator;

/**
 * 
 * @author dante
 * 
 */
public class PieceInfos implements Iterable<BTPart> {
	private final int piecesCount;
	private final int pieceLength;
	private final long dataSize;
	private final int pieceInfoLength;
	private int partsPerPiece;

	public PieceInfos(int pieceLength, long dataSize, int pieceInfoLength) {
		this.piecesCount = (int) ((dataSize + pieceLength - 1) / pieceLength);
		this.pieceLength = pieceLength;
		this.dataSize = dataSize;
		this.pieceInfoLength = pieceInfoLength;
		this.partsPerPiece = (pieceLength + pieceInfoLength - 1)
				/ pieceInfoLength;
	}

	@Override
	public Iterator<BTPart> iterator() {
		return new Iterator<BTPart>() {
			int pieceIndex, infoIndex;

			@Override
			public boolean hasNext() {
				return getPieceInfo(pieceIndex, infoIndex) != null;
			}

			@Override
			public BTPart next() {
				BTPart result = getPieceInfo(pieceIndex, infoIndex);
				if (result != null) {
					infoIndex++;
					if (getPieceInfo(pieceIndex, infoIndex) == null) {
						pieceIndex++;
						infoIndex = 0;
					}
				}
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	public BTPart getPieceInfo(int pieceIndex, int infoIndex) {
		Validator.isTrue(pieceIndex >= 0, "Piece Index < 0!");
		Validator.isTrue(infoIndex >= 0, "PieceInfo Index < 0!");

		if (pieceIndex >= getPiecesCount() || infoIndex >= partsPerPiece) {
			return null;
		}
		int start = infoIndex * pieceInfoLength;
		int length = (Math.min((int) (dataSize - (long) pieceIndex
				* pieceLength), (infoIndex + 1) * pieceInfoLength) - start);

		if (length <= 0) {
			return null;
		}
		return new BTPart(pieceIndex, start, length);
	}

	@Override
	public String toString() {
		return "PieceInfos: dSize = " + dataSize + ", plen = " + pieceLength
				+ ", pinfoLen = " + pieceInfoLength + " pcount = "
				+ getPiecesCount();
	}

	public int getPiecesCount() {
		return piecesCount;
	}

	public int getPieceInfoCount() {
		return (int) ((dataSize + pieceInfoLength - 1) / pieceInfoLength);
	}
}
