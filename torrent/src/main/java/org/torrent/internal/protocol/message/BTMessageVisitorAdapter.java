package org.torrent.internal.protocol.message;


public class BTMessageVisitorAdapter implements BTMessageVisitor {

	@Override
	public void visitBitField(BitField bitField) {
	}

	@Override
	public void visitCancel(Cancel cancel) {
	}

	@Override
	public void visitChoke(Choke choke) {
	}

	@Override
	public void visitHandShakeA(HandShakeA handShakeA) {
	}

	@Override
	public void visitHandShakeB(HandShakeB handShakeB) {
	}

	@Override
	public void visitHave(Have have) {
	}

	@Override
	public void visitInterested(Interested interested) {
	}

	@Override
	public void visitKeepAlive(KeepAlive keepAlive) {
	}

	@Override
	public void visitNotInterested(NotInterested notInterested) {
	}

	@Override
	public void visitPiece(Piece piece) {
	}

	@Override
	public void visitPort(Port port) {
	}

	@Override
	public void visitRequest(Request request) {
	}

	@Override
	public void visitUnChoke(UnChoke unChoke) {
	}

	@Override
	public void visitRawMessage(RawMessage rawMessage) {
	}

//	@Override
//	public void visitDontHave(DontHave dontHave) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void visitWinUpdate(WinUpdate winUpdate) {
//		// TODO Auto-generated method stub
//		
//	}
}
