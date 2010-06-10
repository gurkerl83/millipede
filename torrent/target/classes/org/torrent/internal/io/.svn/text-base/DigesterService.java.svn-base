package org.torrent.internal.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.torrent.internal.util.Validator;

public class DigesterService implements Runnable {
	private static final int BUFFER_SIZE = 4096;
	private static final CheckRequest END_OF_REQUESTS = new CheckRequest();

	private MessageDigest digest;
	private BlockingQueue<CheckRequest> requests = new LinkedBlockingQueue<CheckRequest>();

	private boolean terminated;
	private static Logger log = Logger.getLogger(DigesterService.class
			.getName());

	public static class CheckRequest {

		private final DataReader reader;
		private final long dataStart;
		private final long dataLength;
		private final CheckCallback callback;

		public CheckRequest(DataReader reader, long dataStart, long dataLength,
				CheckCallback callback) {
			Validator.nonNull(reader, callback);
			Validator.isTrue(dataStart >= 0, "Negative start of data!");
			Validator.isTrue(dataLength > 0, "Invalid length: " + dataLength);
			this.reader = reader;
			this.dataStart = dataStart;
			this.dataLength = dataLength;
			this.callback = callback;
		}

		private CheckRequest() {
			reader = null;
			dataStart = -1;
			dataLength = -1;
			callback = null;
		}

		public DataReader getReader() {
			return reader;
		}

		public long getDataLength() {
			return dataLength;
		}

		public long getDataStart() {
			return dataStart;
		}

		public CheckCallback getCallback() {
			return callback;
		}

	}

	public interface CheckCallback {

		void caughtException(IOException e);

		void resultDigest(byte[] digest);

	}

	public DigesterService(String algorithm) throws NoSuchAlgorithmException {
		digest = MessageDigest.getInstance(algorithm);
	}

	public void requestCheck(CheckRequest request) {
		Validator.notNull(request, "Request is null!");
		synchronized (requests) {
			if (terminated || requests.contains(END_OF_REQUESTS)) {
				throw new IllegalStateException("Service has been terminated!");
			}
			requests.add(request);
		}
	}

	@Override
	public void run() {
		try {
			synchronized (requests) {
				if (terminated) {
					throw new IllegalStateException(
							"Service has been terminated!");
				}
			}
			ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
			while (!Thread.interrupted()) {
				CheckRequest request = requests.take();
				if (request == END_OF_REQUESTS) {
					break;
				}
				DataReader reader = request.getReader();
				long remaining = request.getDataLength();
				long position = request.getDataStart();
				try {
					while (remaining > 0) {
						buffer.limit((int) Math.min(remaining, buffer
								.capacity()));
						reader.read(buffer, position);
						buffer.flip();
						position += buffer.limit();
						remaining -= buffer.limit();
						digest.update(buffer);
						buffer.clear();
					}
					request.getCallback().resultDigest(digest.digest());
				} catch (IOException e) {
					request.getCallback().caughtException(e);
				}
			}
		} catch (InterruptedException e) {
			log.finest("Got interrupted: " + e);
		} finally {
			synchronized (requests) {
				terminated = true;
				requests.notifyAll();
			}
		}
	}

	public void terminate() {
		synchronized (requests) {
			if (terminated || requests.contains(END_OF_REQUESTS)) {
				return;
			}
			requests.add(END_OF_REQUESTS);
		}
	}

	public void awaitTermination(int timeout) {
		synchronized (requests) {
			if (terminated) {
				return;
			}
			long endTime = System.currentTimeMillis() + timeout;
			try {
				while (!requests.isEmpty()
						&& System.currentTimeMillis() > endTime) {
					requests.wait(Math.max(0, endTime
							- System.currentTimeMillis()));
				}
			} catch (InterruptedException e) {
				log.finest(e.getLocalizedMessage());
			}
		}
	}
}
