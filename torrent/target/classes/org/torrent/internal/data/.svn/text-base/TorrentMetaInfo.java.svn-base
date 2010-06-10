package org.torrent.internal.data;

import java.util.Iterator;

import org.torrent.internal.util.Range;
import org.torrent.internal.util.Validator;

public class TorrentMetaInfo implements Iterable<TorrentMetaInfo.Piece> {
	private final long length;
	private final int pieceLength;
	private final Hash infoHash;
	private final Piece[] piece;

	public final class Piece {
		private final Hash hash;
		private final int index;

		public Piece(int index, Hash hash) {
			assert hash != null;
			this.index = index;
			this.hash = hash;
		}

		public long getStart() {
			return (long) pieceLength * index;
		}

		public Hash getHash() {
			return hash;
		}

		public int getIndex() {
			return index;
		}

		public int getLength() {
			return (int) Math.min(pieceLength, length - getStart());
		}

		public BTPart asBTPart() {
			return new BTPart(index, 0, getLength());
		}

		/**
		 * @return the absolute range within the data
		 */
		public Range getRange() {
			long start = getStart();
			return Range.getRangeByLength(start, Math.min(length - start,
					pieceLength));
		}

		public TorrentMetaInfo getContentInfo() {
			return TorrentMetaInfo.this;
		}

		@Override
		public String toString() {
			return "Piece " + index + ", range = " + getRange() + ", hash = "
					+ hash;
		}
	}

	public TorrentMetaInfo(Hash infoHash, Hash[] hashes, long length,
			int pieceLength) {
		Validator.nonNull(infoHash);
		Validator.isTrue(length >= 0, "Invalid length: " + length);
		Validator.isTrue(pieceLength > 0, "Invalid piece length: " + length);
		Validator.isTrue(infoHash.getType() == Hash.Type.SHA1,
				"Wrong hash type: " + infoHash);
		this.length = length;
		this.pieceLength = pieceLength;
		int piecesCount = (int) ((length + pieceLength - 1) / pieceLength);
		Validator.isTrue(piecesCount == hashes.length,
				"Wrong number of hashes: " + hashes.length + ", expected "
						+ piecesCount);
		piece = new Piece[piecesCount];
		this.infoHash = infoHash;
		for (int i = 0; i < piecesCount; i++) {
			piece[i] = new Piece(i, hashes[i]);
		}
	}

	public long getLength() {
		return length;
	}

	public int getPieceLength() {
		return pieceLength;
	}

	public int getPiecesCount() {
		return piece.length;
	}

	public Range asRange() {
		return Range.getRangeByLength(0, length);
	}

	public Range getAbsoluteRange(BTPart pi) {
		Range result = Range.getRangeByLength(getAbsoluteStart(pi), pi
				.getLength());
		Validator.isTrue(result.getEnd() <= length,
				"Part length exceeds content range!");
		return result;
	}

	public long getAbsoluteStart(BTPart pi) {
		return piece[pi.getIndex()].getStart() + pi.getStart();
	}

	public Hash getInfoHash() {
		return infoHash;
	}

	public Piece getPiece(int pieceIndex) {
		return piece[pieceIndex];
	}

	@Override
	public Iterator<Piece> iterator() {
		return new Iterator<Piece>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < piece.length;
			}

			@Override
			public Piece next() {
				return piece[index++];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}
	
	public boolean isPrivate() {
		return false;
	}
}
