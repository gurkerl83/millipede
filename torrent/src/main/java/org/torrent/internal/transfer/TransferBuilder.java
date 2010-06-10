package org.torrent.internal.transfer;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
import org.gudy.azureus2.plugins.download.DownloadListener;
import org.gudy.azureus2.plugins.download.DownloadPeerListener;
import org.gudy.azureus2.plugins.download.DownloadTrackerListener;
import org.gudy.azureus2.plugins.peers.PeerManager;
import org.gudy.azureus2.plugins.torrent.Torrent;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.merapi.helper.handlers.BarUpdateRequestHandler;
import org.merapi.helper.messages.BarUpdateRespondMessage;
//import org.milipede.problemResolver.ProblemResolver;
import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.io.DataReader;
import org.torrent.internal.io.DigesterService;
import org.torrent.internal.io.NBDataReader;
import org.torrent.internal.io.NBDataWriter;
import org.torrent.internal.io.PieceEvent;
import org.torrent.internal.io.PieceVerifier;
import org.torrent.internal.io.NBDataWriter.Callback;
import org.torrent.internal.io.PieceVerifier.PieceVerifierEventListener;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Piece;
import org.torrent.internal.service.ContentState;
import org.torrent.internal.service.ContentStateListener;
import org.torrent.internal.service.event.ContentStateEvent;
import org.torrent.internal.util.BTUtil;
import org.torrent.transfer.Transfer;
import org.torrent.transfer.TransferBuilderService;

public class TransferBuilder implements TransferBuilderService {

//	public static int chunksize = 16348; 
    public static int chunksize = 16384;
    AvailabilityObserver availabilityObserver;
    List<Integer> order;
    ContentWatcher contentwatcher;
    PieceVerifier pieceVerifier;
    Download dl;
    Uploader ul;
    TrafficWatcher trafficWatcher;
    DefaultSession session;

    @Override
    public Transfer createTransfer(final TorrentMetaInfo contentInfo,
            NBDataReader reader, final NBDataWriter writer,
            DigesterService digester, DataReader breader) throws IOException {
        session = new DefaultSession();

        contentwatcher = new ContentWatcher(contentInfo);
        session.addBTSessionListener(contentwatcher);

        availabilityObserver = new AvailabilityObserver(
                contentInfo);

        order = new ArrayList<Integer>(contentInfo.getPiecesCount());


        for (int i = 0; i < contentInfo.getPiecesCount(); i++) {
            order.add(i);
        }
        Collections.shuffle(order);

        DistributionWatcher distWatcher = new DefaultDistributionWatcher(
                contentInfo);




        distWatcher.setPieceComparator(new Comparator<org.torrent.internal.data.TorrentMetaInfo.Piece>() {

            @Override
            public int compare(org.torrent.internal.data.TorrentMetaInfo.Piece o1,
                    org.torrent.internal.data.TorrentMetaInfo.Piece o2) {
                // TODO Auto-generated method stub
                int res = availabilityObserver.getAvailability(o1) - availabilityObserver.getAvailability(o2);
                if (res == 0) {
                    res = order.get(o1.getIndex()) - order.get(o2.getIndex());
                }
                return res;
            }
        });

//		chain.add(distWatcher);

        trafficWatcher = new TrafficWatcher();
        session.addBTSessionListener(trafficWatcher);
//		trafficWatcher.addPropertyChangeListener(listener);
        

        SimpleScoreKeeper scorer = new SimpleScoreKeeper();
        session.addBTSessionListener(scorer);

        session.addBTSessionListener(availabilityObserver);




        SimpleRequestProvider srp = new SimpleRequestProvider(contentInfo,
                contentwatcher, 16384, 8);
//		DWRequestProvider dwrp = new DWRequestProvider(contentInfo,
//				contentwatcher, 16348, 16, distWatcher);

//		dl = new Download(session, new
//				 DWRequestProvider(contentInfo,
//				 contentwatcher, 16384, 16, distWatcher), contentwatcher);

        srp.setPieceComparator(new Comparator<org.torrent.internal.data.TorrentMetaInfo.Piece>() {

            public int compare(org.torrent.internal.data.TorrentMetaInfo.Piece o1,
                    org.torrent.internal.data.TorrentMetaInfo.Piece o2) {
                int res = availabilityObserver.getAvailability(o1) - availabilityObserver.getAvailability(o2);
                if (res == 0) {
                    res = order.get(o1.getIndex()) - order.get(o2.getIndex());
                }
                return res;
            }
        });

        dl = new Download(session, srp, contentwatcher);
        session.addBTSessionListener(dl);

        
        ul = new SimpleScoreBasedUploader(session,
                contentwatcher, scorer, reader, contentInfo, 5);
        session.addBTSessionListener(ul);

        pieceVerifier = BTUtil.createPieceVerifier(
                contentInfo, digester, breader);

        pieceVerifier.addPieceVerifierEventListener(new PieceVerifierEventListener() {

            @Override
            public void caughtException(PieceEvent evt) {
                if (evt.getException() instanceof EOFException) {
                    contentwatcher.markRequired(evt.getPiece());
                }
            }

            @Override
            public void falsified(PieceEvent evt) {
                contentwatcher.markRequired(evt.getPiece());
                
            }

            @Override
            public void verified(PieceEvent evt) {
//                contentwatcher.markChecked(evt.getPiece());
//out to test message overflow
//                BarUpdateRequestHandler.getInstance().sendUpdateBarData(BarUpdateRespondMessage.UPDATE_BAR_DATA, evt.getPiece().getContentInfo().getInfoHash().asHexString(), evt.getPiece().getIndex(), 1);
                contentwatcher.setAvailable(evt.getPiece().asBTPart());
            }
        });

        contentwatcher.addContentStateListener(new ContentStateListener() {

            @Override
            public void verifiedPiece(ContentStateEvent evt) {
//                pieceVerifier.checkPiece(evt.getPiece());
                ul.setValidated(evt.getPiece());
            }

            @Override
            public void receivedPiece(ContentStateEvent evt) {
                pieceVerifier.checkPiece(evt.getPiece());
            }

            @Override
            public void requiresPiece(ContentStateEvent evt) {
                //+++
//				if (evt.getPiece().getIndex() > 100) {

                dl.performDownload(evt.getPiece());
//				} else return;
            }

            @Override
            public void stateChanged(ContentStateEvent event) {
                // TODO Auto-generated method stub
                // funzt

				if (event.getState() == ContentState.AVAILABLE) {
                pieceVerifier.checkPiece(event.getPiece());
//					BarUpdateRequestHandler.sendUpdateBarData(BarUpdateRespondMessage.UPDATE_BAR_DATA, event.getPiece().getContentInfo().getInfoHash().asHexString(), event.getPiece().getIndex(), 1);
				}
            }
        });
        session.addBTSessionListener(new BTSessionListenerAdapter() {

            @Override
            public void receivedBTMessage(BTConnection from,
                    BittorrentMessage message) {
                if (message instanceof Piece) {
                    final Piece p = (Piece) message;
                    writer.write(p.getData(), contentInfo.getAbsoluteStart(p.getPieceInfo()), new Callback() {

                        @Override
                        public void caughtException(IOException e) {
                            System.err.println(e.getLocalizedMessage());
                        }

                        @Override
                        public void written(ByteBuffer src, long position) {
                            contentwatcher.markAvailable(p.getPieceInfo());
                        }
                    });
                }
            }
        });

        return new Transfer() {

            @Override
            public ContentWatcher getContentWatcher() {
                return contentwatcher;
            }

            @Override
            public BTSession getSession() {
                return session;
            }

            @Override
            public TorrentMetaInfo getContentInfo() {
                return contentInfo;
            }

            @Override
            public PieceVerifier getPieceVerifier() {
                return pieceVerifier;
            }

            @Override
            public TrafficWatcher getTrafficWatcher() {
                return trafficWatcher;
            }

            @Override
            public void checkAllPieces() {
                for (TorrentMetaInfo.Piece piece : contentInfo) {
                    pieceVerifier.checkPiece(piece);
                }
            }

            @Override
            public AvailabilityObserver getAvailabilityObserver() {
                return availabilityObserver;
            }

            @Override
            public DownloadAnnounceResult getLastAnnounceResult() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String[] getListAttribute(TorrentAttribute arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getState() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public TorrentMetaInfo getTorrent() {
                // TODO Auto-generated method stub
                return this.getContentInfo();
            }

            @Override
            public void setAnnounceResult(DownloadAnnounceResult arg0) {
                // TODO Auto-generated method stub
            	
            }

            @Override
            public void removeAttributeListener(DownloadAttributeListener arg0,
                    TorrentAttribute arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void removeListener(DownloadListener arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void removeTrackerListener(DownloadTrackerListener arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void addAttributeListener(DownloadAttributeListener arg0,
                    TorrentAttribute arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void addListener(DownloadListener arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void addTrackerListener(DownloadTrackerListener arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void addTrackerListener(DownloadTrackerListener arg0,
                    boolean arg1) {
                // TODO Auto-generated method stub
            }

            @Override
            public boolean isForceStart() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setForceStart(boolean forceStart) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public PeerManager getPeerManager() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public byte[] getDownloadPeerId() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public boolean isMessagingEnabled() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setMessagingEnabled(boolean enabled) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void addPeerListener(DownloadPeerListener l) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void removePeerListener(DownloadPeerListener l) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };


    }
}
