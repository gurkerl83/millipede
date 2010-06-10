package org.torrent.internal.service;

import java.util.concurrent.Callable;

public interface EventDispatcherService {
	void invokeLater(Runnable run);

	<T> T invokeAndWait(Callable<T> callable) throws Exception;

	boolean isEventDispatchThread();
}
