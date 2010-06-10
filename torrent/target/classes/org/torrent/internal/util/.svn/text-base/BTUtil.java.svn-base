package org.torrent.internal.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.data.Hash;
import org.torrent.internal.io.DataReader;
import org.torrent.internal.io.DigesterService;
import org.torrent.internal.io.PieceEvent;
import org.torrent.internal.io.PieceVerifier;
import org.torrent.internal.io.DigesterService.CheckCallback;
import org.torrent.internal.io.DigesterService.CheckRequest;

public class BTUtil {
	private static final Logger LOG = Logger.getLogger(BTUtil.class.getName());
	private static ExecutorService dispatcher = Executors
			.newSingleThreadExecutor(new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r, "BT Event dispatcher");
					t.setDaemon(true);
					return t;
				}

			});

    /*
     * Requests that some code be executed in the event-dispatching thread.
     * This method returns immediately, without waiting for the code to execute.
     */
    public static void invokeLater(final Runnable run) {
		dispatcher.execute(new Runnable() {

			@Override
			public void run() {
				Time time = new Time();
				run.run();
				if (time.delta() > 200) {
					LOG.finer(run + " took " + time.delta() + " to complete!");
				}
			}

		});
	}

    /*
     * Acts like invokeLater(), except that this method waits
     * for the code to execute. As a rule, you should use invokeLater()
     * instead of this method.
     */
	public static <T> T invokeAndWait(Callable<T> callable) throws Exception {
		return dispatcher.submit(callable).get();
	}

	public static PieceVerifier createPieceVerifier(
			final TorrentMetaInfo contentInfo, final DigesterService digester,
			final DataReader reader) {
		return new PieceVerifier() {

			@Override
			public void checkPiece(final TorrentMetaInfo.Piece piece) {

				digester.requestCheck(new CheckRequest(reader,
						piece.getStart(), piece.getLength(),
						new CheckCallback() {

							@Override
							public void caughtException(IOException e) {
								fireCaughtException(new PieceEvent(
										BTUtil.class, e, piece));
							}

							@Override
							public void resultDigest(byte[] digest) {
								Hash res = new Hash(digest, Hash.Type.SHA1);
								if (Arrays.equals(digest, piece.getHash()
										.toByteArray())) {
									firePieceVerified(new PieceEvent(
											BTUtil.class, piece, res));
								} else {
									firePieceFalsified(new PieceEvent(
											BTUtil.class, piece, res));
								}
							}

						}));
			}

		};
	}
}
