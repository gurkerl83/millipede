package org.torrent.internal.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class PieceInfoMap<T> {

	private final Map<BTPart, Collection<T>> map;
	private final PieceInfos pieceInfos;

	public PieceInfoMap(PieceInfos infos) {

		this.pieceInfos = infos;
		map = new HashMap<BTPart, Collection<T>>();
	}

	public List<BTPart> getUnboundPieceInfos() {
		List<BTPart> lst = new ArrayList<BTPart>();
		for (BTPart pi : pieceInfos) {
			if (!map.containsKey(pi)) {
				lst.add(pi);
			}
		}
		assert pieceInfos.getPieceInfoCount() == lst.size()
				+ map.keySet().size() : "Expected "
				+ pieceInfos.getPieceInfoCount() + " but got " + lst.size()
				+ " + " + map.keySet().size() + " = "
				+ (lst.size() + map.keySet().size());
		return lst;
	}

	public List<BTPart> getBoundPieceInfos(T value) {
		List<BTPart> lst = new ArrayList<BTPart>();
		for (Entry<BTPart, Collection<T>> entry : map.entrySet()) {
			if (entry.getValue().contains(value)) {
				lst.add(entry.getKey());
			}
		}
		return lst;
	}

	public boolean unbind(BTPart pieceInfo, T value) {
		Collection<T> values = map.get(pieceInfo);
		if (values == null) {
			return false;
		}
		if (values.remove(value)) {
			if (values.isEmpty()) {
				map.remove(pieceInfo);
			}
			return true;
		}
		return false;
	}

	public void bind(BTPart pieceInfo, T value) {
		Collection<T> values = map.get(pieceInfo);
		if (values == null) {
			values = new LinkedList<T>();
			map.put(pieceInfo, values);
		}
		values.add(value);
	}

	@Override
	public String toString() {
		return "PieceInfoMap: Bound PieceInfos: " + map.keySet().size() + ", "
				+ pieceInfos;
	}

	public Iterator<BTPart> unboundPieceInfoIterator() {
		return new Iterator<BTPart>() {
			Iterator<BTPart> i = pieceInfos.iterator();
			BTPart next;

			@Override
			public boolean hasNext() {
				return peekNext() != null;
			}

			private BTPart peekNext() {
				while (next == null && i.hasNext()) {
					BTPart pi = i.next();
					if (!map.containsKey(pi)) {
						next = pi;
					}
				}
				return next;
			}

			@Override
			public BTPart next() {
				BTPart result = peekNext();
				next = null;
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}
}
