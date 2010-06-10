package org.torrent.internal.service;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.torrent.internal.data.TorrentMetaInfo.Piece;
import org.torrent.internal.service.event.ContentStateEvent;
import org.torrent.internal.util.Validator;

public abstract class AbstractContentStateService implements
		ContentStateService {

	private Collection<ContentStateListener> listeners = new CopyOnWriteArrayList<ContentStateListener>();

	private final EventDispatcherService disp;

	public AbstractContentStateService(EventDispatcherService dispatcher) {
		Validator.nonNull(dispatcher);
		disp = dispatcher;
	}

	@Override
	public void addContentStateListener(ContentStateListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeContentStateListener(ContentStateListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setAvailable(Piece piece) {
		fire(new ContentStateEvent(this, piece, ContentState.AVAILABLE));
	}

	@Override
	public void setRequired(Piece piece) {
		fire(new ContentStateEvent(this, piece, ContentState.REQUIRED));
	}

	@Override
	public void setValidated(Piece piece) {
		fire(new ContentStateEvent(this, piece, ContentState.VALIDATED));
	}

	private void fire(final ContentStateEvent contentEvent) {
		disp.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (ContentStateListener l : listeners) {
					l.stateChanged(contentEvent);
				}
			}

		});
	}
}
