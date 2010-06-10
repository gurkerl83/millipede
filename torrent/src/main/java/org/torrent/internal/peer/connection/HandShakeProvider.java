package org.torrent.internal.peer.connection;

import org.torrent.internal.data.Hash;
import org.torrent.internal.util.Bits;

public interface HandShakeProvider {
	Bits getBitField();

	Hash getPeerID();

	Hash getInfoHash();
}
