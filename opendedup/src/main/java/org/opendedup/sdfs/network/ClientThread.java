package org.opendedup.sdfs.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opendedup.sdfs.Main;
import org.opendedup.sdfs.filestore.HashChunk;
import org.opendedup.sdfs.servers.HashChunkService;
import org.opendedup.util.StringUtils;

/**
 * @author Sam Silverberg This is the network class that is used within the
 *         Chunk store to service all client requests and responses. It is
 *         threaded and is spawned by @see
 *         com.annesam.sdfs.network.NetworkHCServer when a new TCP connect in
 *         accepted.
 */

class ClientThread extends Thread {

	// DataInputStream is = null;
	DataOutputStream os = null;
	Socket clientSocket = null;
	private ReentrantLock writelock = new ReentrantLock();

	private static ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	private static Logger log = Logger.getLogger("sdfs");

	public ClientThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
		log.finer("Client Threads is " + clients.size());
		addClient(this);
	}

	public static void addClient(ClientThread client) {
		clients.add(client);
	}

	public static void removeClient(ClientThread client) {
		clients.remove(client);
	}

	public void run() {
		try {
			// is = new DataInputStream(clientSocket.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()), 32768 * 2);
			DataInputStream is = new DataInputStream(new BufferedInputStream(
					clientSocket.getInputStream(), 32768 * 2));
			os = new DataOutputStream(new BufferedOutputStream(clientSocket
					.getOutputStream()));
			String versionMessage = "SDFS version " + Main.PROTOCOL_VERSION
					+ "\r\n";
			os.write(versionMessage.getBytes());
			os.flush();

			while (true) {
				try {
					byte cmd = is.readByte();
					if (cmd == NetworkCMDS.QUIT_CMD) {
						log.info("Quiting Client Network Thread");
						break;
					}
					if (cmd == NetworkCMDS.HASH_EXISTS_CMD) {
						byte[] hash = new byte[is.readShort()];
						is.readFully(hash);
						boolean exists = HashChunkService.hashExists(hash);
						try {
							writelock.lock();
							os.writeBoolean(exists);
							os.flush();
							writelock.unlock();
						} catch (IOException e) {
							if (writelock.isLocked())
								writelock.unlock();
							throw new IOException(e.toString());
						} finally {

						}

					}
					if (cmd == NetworkCMDS.WRITE_HASH_CMD
							|| cmd == NetworkCMDS.WRITE_COMPRESSED_CMD) {
						byte[] hash = new byte[is.readShort()];
						is.readFully(hash);
						int len = is.readInt();
						byte[] chunkBytes = new byte[len];
						is.readFully(chunkBytes);
						boolean done = false;
						if (cmd == NetworkCMDS.WRITE_COMPRESSED_CMD) {
							done = HashChunkService.writeChunk(hash,
									chunkBytes, len, len, true);
						} else {
							done = HashChunkService.writeChunk(hash,
									chunkBytes, len, len, false);
						}
						try {
							writelock.lock();
							os.writeBoolean(done);
							os.flush();
							writelock.unlock();
						} catch (IOException e) {
							if (writelock.isLocked())
								writelock.unlock();
							throw new IOException(e.toString());
						} finally {

						}
					}
					if (cmd == NetworkCMDS.FETCH_CMD
							|| cmd == NetworkCMDS.FETCH_COMPRESSED_CMD) {
						byte[] hash = new byte[is.readShort()];
						is.readFully(hash);
						HashChunk dChunk = null;
						try {
							dChunk = HashChunkService.fetchChunk(hash);
							if (cmd == NetworkCMDS.FETCH_COMPRESSED_CMD
									&& !dChunk.isCompressed()) {
								/*
								 * byte[] cChunk = CompressionUtils
								 * .compress(dChunk.getData()); try {
								 * writelock.lock(); os.writeInt(cChunk.length);
								 * os.write(cChunk); os.flush();
								 * writelock.unlock(); } catch (IOException e) {
								 * if(writelock.isLocked()) writelock.unlock();
								 * throw new IOException(e.toString()); }
								 * finally {
								 * 
								 * }
								 */
								throw new Exception("not implemented");
							} else if (cmd == NetworkCMDS.FETCH_CMD
									&& dChunk.isCompressed()) {
								/*
								 * byte[] cChunk = CompressionUtils
								 * .decompress(dChunk.getData());
								 * 
								 * try { writelock.lock();
								 * os.writeInt(cChunk.length); os.write(cChunk);
								 * os.flush(); writelock.unlock(); } catch
								 * (IOException e) { if(writelock.isLocked())
								 * writelock.unlock(); throw new
								 * IOException(e.toString()); } finally {
								 * 
								 * }
								 */
								throw new IOException("Not implemented");
							} else {
								try {
									writelock.lock();
									os.writeInt(dChunk.getData().length);
									os.write(dChunk.getData());
									os.flush();
									writelock.unlock();
								} catch (IOException e) {
									if (writelock.isLocked())
										writelock.unlock();
									throw new IOException(e.toString());
								} finally {

								}
							}
						} catch (NullPointerException e) {
							log.warning("chunk "
									+ StringUtils.getHexString(hash)
									+ " does not exist");
							try {
								writelock.lock();
								os.writeInt(-1);
								os.flush();
								writelock.unlock();
							} catch (IOException e1) {
								if (writelock.isLocked())
									writelock.unlock();
								throw new IOException(e1.toString());
							} finally {

							}
						}
					}
					if (cmd == NetworkCMDS.PING_CMD) {
						try {
							writelock.lock();
							os.writeShort(NetworkCMDS.PING_CMD);
							os.flush();
							writelock.unlock();
						} catch (IOException e) {
							if (writelock.isLocked())
								writelock.unlock();
							throw new IOException(e.toString());
						} finally {

						}
					}

				} catch (Exception e) {
					log.log(Level.FINEST, "unable to write data", e);
					try {
						reader.close();
					} catch (Exception e1) {
					}
					try {
						os.close();
					} catch (Exception e1) {
					}
					try {
						is.close();
					} catch (Exception e1) {
					}
					try {
						clientSocket.close();
					} catch (Exception e1) {
					}
					break;

				}
			}
			try {
				reader.close();
			} catch (Exception e1) {
			}
			try {
				os.close();
			} catch (Exception e1) {
			}
			try {
				is.close();
			} catch (Exception e1) {
			}
			try {
				clientSocket.close();
			} catch (Exception e1) {
			}
			clientSocket.close();
		} catch (IOException e) {
		} finally {
			ClientThread.removeClient(this);
		}
	}

	public static final int byteArrayToInt(byte[] b) {
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}

}
