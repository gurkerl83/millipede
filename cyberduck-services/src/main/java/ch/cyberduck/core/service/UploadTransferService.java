package ch.cyberduck.core.service;

import ch.cyberduck.core.AttributedList;
import ch.cyberduck.core.Cache;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.PathReference;
import ch.cyberduck.core.TransferAction;
//import ch.cyberduck.core.Transfer.TransferFilter;

public interface UploadTransferService {

	public abstract <T> T getAsDictionary();

	public abstract AttributedList<Path> childs(final Path parent);

	public abstract Path lookup(PathReference r);

	public abstract Cache<Path> cache();

	public abstract boolean isResumable();

//	public abstract TransferFilter filter(final TransferAction action);

	public abstract TransferAction action(final boolean resumeRequested,
			final boolean reloadRequested);

}