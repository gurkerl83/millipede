package org.torrent.internal.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class BasicEventDispatcherService implements EventDispatcherService {

	private volatile Thread thread;

	private ExecutorService service = Executors
			.newSingleThreadExecutor(new ThreadFactory() {

				@Override
				public Thread newThread(Runnable r) {
					return thread = new Thread(r);
				}

			});

	@Override
	public <T> T invokeAndWait(Callable<T> callable) throws Exception {
		Future<T> fut = service.submit(callable);
		return fut.get();
	}

	@Override
	public void invokeLater(Runnable run) {
		service.execute(run);
	}

	@Override
	public boolean isEventDispatchThread() {
		return Thread.currentThread() == thread;
	}
}
