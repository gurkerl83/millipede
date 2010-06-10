package org.torrent.internal.peer.connection;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.logging.Logger;

//import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider;
import org.torrent.internal.data.BTPart;
import org.torrent.internal.protocol.message.BTMessageVisitorAdapter;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.Choke;
import org.torrent.internal.protocol.message.HandShakeA;
import org.torrent.internal.protocol.message.HandShakeB;
import org.torrent.internal.protocol.message.Interested;
import org.torrent.internal.protocol.message.NotInterested;
import org.torrent.internal.protocol.message.Piece;
import org.torrent.internal.protocol.message.Port;
import org.torrent.internal.protocol.message.Request;
import org.torrent.internal.protocol.message.UnChoke;
import org.torrent.internal.service.ContentStateListener;
import org.torrent.internal.transfer.BTSession;
import org.torrent.internal.transfer.event.BitfieldListener;
import org.torrent.internal.util.BTUtil;
import org.torrent.internal.util.Bits;
import org.torrent.internal.util.Time;
import org.torrent.internal.util.Validator;

public abstract class AbstractBTConnection implements BTConnection {

    private static final Logger LOG = Logger.getLogger(AbstractBTConnection.class.getName());
    protected Socket socket;
    private PeerStatus myPeerStatus = new PeerStatus();
    private PeerStatus remotePeerStatus = new PeerStatus();
    private final BTMessageSender sender;
    private final BTSession controller;
    private HandShakeA handshakeA;
    private HandShakeB handshakeB;
    private boolean sentHandshakeA;
    private boolean sentHandshakeB;
    private final BTPendingRequestsHolder pendingRequests;
    private volatile boolean handshakeComplete;
    private final BitfieldHolder bitfieldHolder;
    private final BTMessageReceiver receiver;
    private final BufferedReceiver bufferedReceiver;
    private final BTPendingRequestsHolder remoteRequests;
    private HandshakeChecker handshakeChecker;

    public interface HandshakeChecker {

        boolean accept(HandShakeA handshakeA);

        boolean accept(HandShakeB handshakeB);
    };

    private class PeerStatus implements BTPeerStatus {

        private PropertyChangeSupport pcs = new PropertyChangeSupport(
                AbstractBTConnection.this);
        private volatile boolean choking = true;
        private volatile boolean interested = false;

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }

        @Override
        public boolean isChoking() {
            return choking;
        }

        @Override
        public boolean isInterested() {
            return interested;
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }

        public void setChoking(boolean b) {
            final boolean old = choking;
            choking = b;
            BTUtil.invokeLater(new Runnable() {

                @Override
                public void run() {
                    pcs.firePropertyChange("choking", old, choking);
                }
            });
        }

        public void setInterested(boolean b) {
            final boolean old = interested;
            interested = b;
            BTUtil.invokeLater(new Runnable() {

                @Override
                public void run() {
                    pcs.firePropertyChange("interested", old, interested);
                }
            });
        }

        @Override
        public String toString() {
            return "Choking: " + choking + ", Interested: " + interested;
        }
    }

    @Override
    public String toString() {
        return "Connection with id:" + handshakeB.getPeerID() + ", my status: " + myPeerStatus + ", remote status: " + remotePeerStatus;
    }

    public AbstractBTConnection(BTMessageSender sender, BTSession controller,
            Bits remoteBitField, HandshakeChecker checker) {
        Validator.nonNull(sender, controller, remoteBitField, checker);
        this.controller = controller;
        this.handshakeChecker = checker;

        // Build outgoing stack
        PendingRequestsHoldingSender pr = new PendingRequestsHoldingSender(
                sender);
        pendingRequests = pr;
        sender = pr;
        this.sender = sender;

        // Build incoming stack
        final PendingRequestsHoldingReceiver prhr = new PendingRequestsHoldingReceiver();
        remoteRequests = prhr;

        BTMessageReceiver recv = new BTMessageReceiver() {

            @Override
            public void received(BittorrentMessage message) {
                LOG.finest("Received " + message);
                try {
                    if (!myPeerStatus.isChoking()) {
                        prhr.received(message);
                    }
                    dispatch(message);
                } catch (IOException e) {
                    LOG.warning(e.getLocalizedMessage());
                    try {
                        AbstractBTConnection.this.sender.close();
                    } catch (IOException e1) {
                        LOG.warning(e1.getLocalizedMessage());
                    }
                }
            }
        };

        BitfieldHoldingReceiver bhr = new BitfieldHoldingReceiver(recv,
                new Bits(remoteBitField), new BitfieldListener() {

            @Override
            public void bitFieldBitChanged(
                    IndexedPropertyChangeEvent event) {
                dispatchBitfieldEvent(new IndexedPropertyChangeEvent(
                        AbstractBTConnection.this, event.getPropertyName(),
                        event.getOldValue(), event.getNewValue(), event.getIndex()));
            }

            @Override
            public void bitFieldChanged(PropertyChangeEvent event) {
                dispatchBitfieldEvent(new PropertyChangeEvent(
                        AbstractBTConnection.this, event.getPropertyName(),
                        event.getOldValue(), event.getNewValue()));
            }

            @Override
            public boolean isAvailable(BTPart part) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isRequired(
                    org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isValidated(
                    org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void removeContentStateListener(
                    ContentStateListener listener) {
                // TODO Auto-generated method stub
            }

            @Override
            public void setAvailable(BTPart part) {
                // TODO Auto-generated method stub
            }

            @Override
            public void setRequired(
                    org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
                // TODO Auto-generated method stub
            }

            @Override
            public void setValidated(
                    org.torrent.internal.data.TorrentMetaInfo.Piece piece) {
                // TODO Auto-generated method stub
            }
            //1

            @Override
            public void addContentStateListener(
                    ContentStateListener listener) {
                // TODO Auto-generated method stub
            }

            
        });
        bitfieldHolder = bhr;

        bufferedReceiver = new BufferedReceiver(bhr);
        bufferedReceiver.setEnableBuffering(true);
        receiver = new SynchronizingReceiver(bufferedReceiver);
    }

    public BTMessageReceiver getReceiver() {
        return receiver;
    }

    public BTSession getController() {
        return controller;
    }

    @Override
    public synchronized BTPeerStatus getMyPeerStatus() {
        return myPeerStatus;
    }

    @Override
    public synchronized Collection<BTPart> getPendingRequests() {
        return pendingRequests.getPendingRequests();
    }

    @Override
    public synchronized BTPeerStatus getRemotePeerStatus() {
        return remotePeerStatus;
    }

    @Override
    public synchronized void purgePendingRequests() {
        pendingRequests.clearPendingRequests();
    }

    @Override
    public void sendChoked(boolean choke) {
        send(choke ? Choke.CHOKE : UnChoke.UNCHOKE, null);
    }

    @Override
    public void sendInterested(boolean interested) {
        send(interested ? Interested.INTERESTED : NotInterested.NOTINTERESTED,
                null);
    }

    public synchronized void setHandshakeComplete() {
        this.handshakeComplete = true;
        // Can't be done currently, but should be possible
        // handshakeChecker = null;

        controller.connectionEstablished(this);

        synchronized (bufferedReceiver) {
            bufferedReceiver.flush();
            bufferedReceiver.setEnableBuffering(false);
        }
    }

    @Override
    public boolean isConnectionEstablished() {
        return handshakeComplete && isConnected();
    }

    @Override
    public synchronized Bits getRemoteBitField() {
        return bitfieldHolder.getBitField();
    }

    @Override
    public synchronized void close() throws IOException {
        sender.close();
    }

    @Override
    public synchronized BTPart takeRemoteRequest() {
        return remoteRequests.takePendingRequest();
    }

    @Override
    public boolean hasNoPending() {
        return sender.hasNoPending();
    }

    @Override
    public synchronized void send(BittorrentMessage msg,
            final BTMessageSenderCallback callback) {
        LOG.finest("Sending " + msg);
        handleOutgoing(msg);
        sender.send(msg, new BTMessageSenderCallback() {

            @Override
            public void sent(BittorrentMessage message) {
                if (callback != null) {
                    callback.sent(message);

                }
                AbstractBTConnection.this.sent(message);
            }

            @Override
            public void failed(IOException e) {
                if (callback != null) {
                    callback.failed(e);
                }
            }
        });
    }

    private synchronized void sent(BittorrentMessage msg) {
        assert msg != null;
        if (!sentHandshakeA) {
            if (msg instanceof HandShakeA); else {
                throw new IllegalStateException("Sent " + msg + " before HandshakeA!");
            }
            sentHandshakeA = true;
        } else if (!sentHandshakeB) {
            if (msg instanceof HandShakeB); else {
                throw new IllegalStateException("Sent " + msg + " before HandshakeB!");
            }
            sentHandshakeB = true;

        }
        controller.sent(AbstractBTConnection.this, msg);
    }

    private synchronized void handleOutgoing(final BittorrentMessage msg) {
        msg.accept(new BTMessageVisitorAdapter() {

            @Override
            public void visitRequest(Request request) {
                if (request.getLength() > 16384 || request.getLength() == 0) {
                    //throw new RuntimeException("Requested " + request);
                }
            }

            @Override
            public void visitPiece(Piece piece) {
                remoteRequests.dequeuePendingRequest(piece.getPieceInfo());
            }

            @Override
            public void visitChoke(Choke choke) {
                myPeerStatus.setChoking(true);
                remoteRequests.clearPendingRequests();
            }

            @Override
            public void visitUnChoke(UnChoke unChoke) {
                myPeerStatus.setChoking(false);
            }

            @Override
            public void visitInterested(Interested interested) {
                myPeerStatus.setInterested(true);
            }

            @Override
            public void visitNotInterested(NotInterested notInterested) {
                myPeerStatus.setInterested(false);
            }


             @Override
                    public void visitPort(Port port) {
                        System.out.println("RemoteDHTHost: " + socket.getInetAddress().getHostAddress() + "   RemoteDHTPort: " + socket.getPort());

//                                                        if (!ml_dht_enabled) {return;}
//                        MainlineDHTProvider provider = getDHTProvider();
//                        System.out.println("Provider: " + provider);
//
//                        if (provider == null) {
//                            return;
//                        }
//
//                        try {
//                            provider.notifyOfIncomingPort(socket.getInetAddress().getHostAddress(), socket.getPort());
//                        } catch (Throwable t) {
//                            t.printStackTrace();
//                        }
                    }
        });
    }

    protected void dispatchBitfieldEvent(PropertyChangeEvent propertyChangeEvent) {
        controller.bitFieldChanged(propertyChangeEvent);
    }

    private void dispatchBitfieldEvent(
            IndexedPropertyChangeEvent indexedPropertyChangeEvent) {
        controller.bitFieldBitChanged(indexedPropertyChangeEvent);
    }

    private void dispatch(final BittorrentMessage msg) throws IOException {
        Time time = new Time();
        boolean unrequestedPiece = false;
        if (handshakeA == null) {
            if (msg instanceof HandShakeA) {
                handshakeA = (HandShakeA) msg;
                if (!handshakeChecker.accept(handshakeA)) {
                    throw new IOException("HandshakeA not accepted!");
                }
            } else {
                throw new IOException("Expected HandshakeA, but got " + msg);
            }
        } else if (handshakeB == null) {
            if (msg instanceof HandShakeB) {
                handshakeB = (HandShakeB) msg;
                if (!handshakeChecker.accept(handshakeB)) {
                    throw new IOException("HandshakeB not accepted!");
                }
            } else {
                throw new IOException("Expected HandshakeB, but got " + msg);
            }
        } else {
            assert !(msg instanceof HandShakeA);
            assert !(msg instanceof HandShakeB);
            synchronized (this) {
                if (msg instanceof Piece) {
                    unrequestedPiece = !pendingRequests.getPendingRequests().contains(((Piece) msg).getPieceInfo());
                }
                msg.accept(new BTMessageVisitorAdapter() {

                    @Override
                    public void visitChoke(Choke choke) {
                        remotePeerStatus.setChoking(true);
                    }

                    @Override
                    public void visitHandShakeA(HandShakeA handShakeA) {
                        throw new IllegalStateException("Received HandShakeA");
                    }

                    @Override
                    public void visitHandShakeB(HandShakeB handShakeB) {
                        throw new IllegalStateException("Received HandShakeB");
                    }

                    @Override
                    public void visitInterested(Interested interested) {
                        remotePeerStatus.setInterested(true);
                    }

                    @Override
                    public void visitNotInterested(NotInterested notInterested) {
                        remotePeerStatus.setInterested(false);
                    }

                    @Override
                    public void visitPiece(Piece piece) {
                        pendingRequests.dequeuePendingRequest(piece.getPieceInfo());
                    }

                    @Override
                    public void visitUnChoke(UnChoke unChoke) {
                        remotePeerStatus.setChoking(false);
                    }

//                    @Override
//                    public void visitPort(Port port) {
//                        System.out.println("RemoteDHTHost: " + socket.getRemoteSocketAddress().toString() + "   RemoteDHTPort: " + ((Port) msg).getPort());
//
////                                                        if (!ml_dht_enabled) {return;}
//                        MainlineDHTProvider provider = getDHTProvider();
//                        System.out.println("Provider: " + provider);
//
//                        if (provider == null) {
//                            return;
//                        }
//
//                        try {
//                            provider.notifyOfIncomingPort(socket.getRemoteSocketAddress().toString(), ((Port) msg).getPort());
//                        } catch (Throwable t) {
//                            t.printStackTrace();
//                        }
//                    }
                });
            }
        }
        if (!unrequestedPiece) {
            controller.received(AbstractBTConnection.this, msg);
        } else {
            controller.unrequestedPiece(AbstractBTConnection.this, (Piece) msg);
        }
        if (time.delta() > 100) {
            LOG.warning(this + " took " + time.delta() + "ms to handle message " + msg);
        }
    }

//    private static MainlineDHTProvider getDHTProvider() {
//        return Main.gm.getMainlineDHTProvider();
//    }
}
