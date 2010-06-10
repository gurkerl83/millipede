package org.torrent.internal.peer.connection;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider;
import org.torrent.internal.client.Main;
import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.data.Hash;
import org.torrent.internal.peer.connection.AbstractBTConnection.HandshakeChecker;
import org.torrent.internal.protocol.BTTransform;
import org.torrent.internal.protocol.BTTransformImpl;
import org.torrent.internal.protocol.BittorrentDecoder;
import org.torrent.internal.protocol.BittorrentMessageDecodingException;
import org.torrent.internal.protocol.message.BitField;
import org.torrent.internal.protocol.message.BittorrentMessage;
import org.torrent.internal.protocol.message.HandShakeA;
import org.torrent.internal.protocol.message.HandShakeB;
import org.torrent.internal.protocol.message.Have;
import org.torrent.internal.protocol.message.Port;
import org.torrent.internal.protocol.message.Request;
import org.torrent.internal.transfer.BTSession;
import org.torrent.internal.util.BTUtil;
import org.torrent.internal.util.Bits;
import org.torrent.internal.util.Validator;

public class SocketConnection {

	private final static Logger LOG = Logger.getLogger(SocketConnection.class
			.getName());
	public static boolean ml_dht_enabled = false;

	public interface BandwidthLimiter {

		void allocate(int amount) throws InterruptedException;
	}

	public interface Filter {

		void received(BTConnection con, BittorrentMessage msg);
	}

	public enum BittorrentType {
		STANDARD, REALTIME;

//		STANDARD(0), REALTIME(1);
//
//		public final Byte type;
//
//		private BittorrentType(Integer type) {
//			this.type = type != null ? type.byteValue() : null;
//		}
	}
	
	public static BittorrentType type = BittorrentType.STANDARD;

	private static class BTSocketConnection extends AbstractBTConnection {

		// private Socket socket;
		private final BlockingQueue<Runnable> outQueue;

		public BTSocketConnection(Socket socket,
				BlockingQueue<Runnable> outQueue, BTMessageSender transport,
				BTSession controller, Bits remoteBitField,
				HandshakeChecker checker) {
			super(transport, controller, remoteBitField, checker);
			assert socket != null && outQueue != null;
			this.socket = socket;
			this.outQueue = outQueue;

		}

		@Override
		public boolean isConnected() {

			return socket.isConnected() && !socket.isClosed();
		}

		@Override
		public String toString() {
			return "Connection to " + socket.getRemoteSocketAddress();
		}
	}

	public static AbstractBTConnection createBTConnection(
			final BTSession session, final TorrentMetaInfo info,
			final Socket socket, final BandwidthLimiter outLimit,
			final BandwidthLimiter inLimit, HandshakeChecker hsChecker,
			final Filter filter) throws IOException {
		Validator.notNull(socket, "Socket is null!");
		Validator.isTrue(socket.isConnected(),
				"Socket must already be connected!");

		if (hsChecker == null) {
			hsChecker = new HandshakeChecker() {

				@Override
				public boolean accept(HandShakeA handshakeA) {
					return handshakeA.getInfoHash().equals(info.getInfoHash());
				}

				@Override
				public boolean accept(HandShakeB handshakeB) {
					return true;
				}
			};
		}
		final BlockingQueue<Runnable> outQueue = new LinkedBlockingQueue<Runnable>();
		final WritableByteChannel out = Channels.newChannel(socket
				.getOutputStream());

		final BTMessageSender transport = new BTMessageSender() {

			BTTransform trans = new BTTransformImpl();

			@Override
			public void send(final BittorrentMessage msg,
					final BTMessageSenderCallback callback) {
				outQueue.add(messageRunnable(msg, callback));
			}

			private Runnable messageRunnable(final BittorrentMessage msg,
					final BTMessageSenderCallback callback) {
				return new Runnable() {

					public void run() {
						try {
							final ByteBuffer buffer = trans.encodeMessage(null,
									msg);
							buffer.flip();
							assert buffer.getInt(buffer.position()) != 0 : "Invalid buffer for "
									+ msg + " :" + buffer;
							if (outLimit != null) {
								outLimit.allocate(buffer.remaining());
							}
							assert buffer.getInt(buffer.position()) != 0;
							out.write(buffer);

							if (callback != null) {
								callback.sent(msg);
							}
						} catch (IOException e) {
							if (callback != null) {
								callback.failed(e);
							}
						} catch (InterruptedException e) {
							LOG.fine(e.getLocalizedMessage());
						}
					}
				};
			}

			@Override
			public void close() throws IOException {
				socket.close();
				outQueue.add(new Runnable() {

					@Override
					public void run() {
						// Hack
						Thread.currentThread().interrupt();
					}
				});
			}

			@Override
			public boolean hasNoPending() {
				return outQueue.isEmpty();
			}
		};

		final BTSocketConnection con = new BTSocketConnection(socket, outQueue,
				transport, session, new Bits(info.getPiecesCount()), hsChecker);
		session.addConnection(con);

		final Thread outgoing = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (socket.isConnected() && !socket.isClosed()) {
						((BTSocketConnection) con).outQueue.take().run();
					}
				} catch (InterruptedException e) {
					LOG.finest(e.getLocalizedMessage());
				}
			}
		}, "Outgoing queue for " + socket);
		outgoing.setDaemon(true);

		Thread incoming = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					BittorrentDecoder decoder = new BittorrentDecoder(type);
					byte[] data = new byte[4096];
					int read;
					while ((read = socket.getInputStream().read(data)) > 0) {
						if (inLimit != null) {
							inLimit.allocate(read);
						}
						// System.out.println("in: " + read + " bytes.");
						decoder.update(data, 0, read);
						BittorrentMessage msg;
						while ((msg = decoder.nextMessage()) != null) {
							// System.out.println(msg);
							if (filter != null) {
								filter.received(con, msg);
							}
							con.getReceiver().received(msg);
						}
					}
				} catch (IOException e) {
					LOG.finer(e.getLocalizedMessage());
				} catch (BittorrentMessageDecodingException e) {
					LOG.warning(e.getLocalizedMessage());
				} catch (InterruptedException e) {
					LOG.finer(e.getLocalizedMessage());
				} finally {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					// System.out.println(result + " - disconnected");
					outgoing.interrupt();
					con.getController().removeConnection(con);
				}
			}
		}, "Incoming queue for " + socket);
		incoming.setDaemon(true);
		outgoing.start();
		incoming.start();

		return con;
	}

	public static BTConnection connect(BTSession session,
			TorrentMetaInfo dataInfo, BandwidthLimiter out,
			BandwidthLimiter in, HandshakeChecker hsChecker,
			final BitfieldProvider bitProv, Socket socket, Hash peerID)
			throws IOException {
		final BTSocketConnection con = (BTSocketConnection) createBTConnection(
				session, dataInfo, socket, out, in, hsChecker, null);
		con.send(new HandShakeA(dataInfo.getInfoHash()), null);
		con.send(new HandShakeB(peerID), bitFieldSender(bitProv, con));

		return con;
	}

	public static BTConnection accept(BTSession session,
			final TorrentMetaInfo info, BandwidthLimiter out,
			BandwidthLimiter in, HandshakeChecker hsChecker,
			final BitfieldProvider bitProv, final Socket socket,
			final Hash peerID) throws IOException {
		BTConnection con = createBTConnection(session, info, socket, out, in,
				hsChecker, new Filter() {

					public void received(final BTConnection con,
							BittorrentMessage msg) {
						if (msg instanceof HandShakeA) {
							System.out.println("Message of type HandShakeA");
							con.send(new HandShakeA(info.getInfoHash()), null);
							con.send(new HandShakeB(peerID), bitFieldSender(
									bitProv, (BTSocketConnection) con));
						} // } else if (msg instanceof Have) {
						//
						// System.out.println("Message of type have");
						// System.out.println("Requesting piece index: " +
						// ((Have) msg).getPieceIndex() + "   length: " +
						// info.getPieceLength());
						//
						// con.send(new Request(((Have) msg).getPieceIndex(), 0,
						// info.getPieceLength()), null);
						// // //bitFieldSender(bitProv, (BTSocketConnection)
						// con));
						// //
						// else if (msg instanceof Port) {
						// System.out.println("RemoteDHTPort: " + ((Port)
						// msg).getPort());
						//
						// // if (!ml_dht_enabled) {return;}
						// MainlineDHTProvider provider = getDHTProvider();
						// System.out.println("Provider: " + provider);
						//
						// if (provider == null) {
						// return;
						// }
						//
						// try {
						// provider.notifyOfIncomingPort(socket.getRemoteSocketAddress().toString(),
						// ((Port) msg).getPort());
						// } catch (Throwable t) {
						// t.printStackTrace();
						// }
						// }
						// con.send(new Request(((Have) msg).getPieceIndex(), 0,
						// info.getPieceLength()), null);
						// //bitFieldSender(bitProv, (BTSocketConnection) con));
						//
						// }
					}
				});

		return con;
	}

	protected static BTMessageSenderCallback bitFieldSender(
			final BitfieldProvider bitProv, final BTSocketConnection con) {
		return new BTMessageSenderCallback() {

			@Override
			public void failed(IOException e) {
			}

			@Override
			public void sent(BittorrentMessage message) {
				try {
					// BTUtil.invokeAndWait(new Callable<BitField>() {
					//
					// @Override
					// public BitField call() throws Exception {
					// con.setHandshakeComplete();
					// if (bitProv != null) {
					// Bits bits = bitProv.getBitField();
					// if (bits != null) {
					// con.send(new BitField(bits), null);
					// }
					// }
					// return null;
					// }
					// });
					BTUtil.invokeLater(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							con.setHandshakeComplete();
							if (bitProv != null) {
								Bits bits = bitProv.getBitField();
								if (bits != null) {
									con.send(new BitField(bits), null);
								}
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
	// private static MainlineDHTProvider getDHTProvider() {
	// return Main.gm.getMainlineDHTProvider();
	// }
}
