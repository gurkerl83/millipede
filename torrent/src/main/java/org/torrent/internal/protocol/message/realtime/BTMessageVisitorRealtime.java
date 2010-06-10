package org.torrent.internal.protocol.message.realtime;

import org.torrent.internal.protocol.message.BTMessageVisitor;

public interface BTMessageVisitorRealtime extends BTMessageVisitor {


	void visitDontHave(DontHave dontHave);
	
	void visitWinUpdate(WinUpdate winUpdate);

}
