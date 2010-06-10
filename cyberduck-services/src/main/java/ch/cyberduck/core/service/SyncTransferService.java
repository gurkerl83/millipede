package ch.cyberduck.core.service;

//import ch.cyberduck.core.SyncTransfer.Comparison;
import ch.cyberduck.core.AttributedList;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.TransferAction;
//import ch.cyberduck.core.Transfer.TransferFilter;
import ch.cyberduck.core.i18n.Locale;

public interface SyncTransferService {

	public abstract <T> T getAsDictionary();

	public abstract void setBandwidth(float bytesPerSecond);

	public abstract float getBandwidth();

	public abstract String getName();

	public abstract double getSize();

	public abstract boolean isResumable();

	public abstract double getTransferred();

	/**
	 * @param action
	 */
	public abstract void setTransferAction(TransferAction action);

	/**
	 * @return
	 */
	public abstract TransferAction getAction();

	public static final TransferAction ACTION_DOWNLOAD = new TransferAction() {
		public String toString() {
			return "download";
		}

		@Override
		public String getLocalizableString() {
			return Locale.localizedString("Download");
		}
	};
	public static final TransferAction ACTION_UPLOAD = new TransferAction() {
		public String toString() {
			return "upload";
		}

		@Override
		public String getLocalizableString() {
			return Locale.localizedString("Upload");
		}
	};
	public static final TransferAction ACTION_MIRROR = new TransferAction() {
		public String toString() {
			return "mirror";
		}

		@Override
		public String getLocalizableString() {
			return Locale.localizedString("Mirror");
		}
	};

//	public abstract TransferFilter filter(final TransferAction action);

	public abstract AttributedList<Path> childs(final Path parent);

	public abstract TransferAction action(final boolean resumeRequested,
			final boolean reloadRequested);

//	/**
//	 * Remote file is newer or local file does not exist
//	 */
//	public static final Comparison COMPARISON_REMOTE_NEWER = new Comparison() {
//		@Override
//		public String toString() {
//			return "COMPARISON_REMOTE_NEWER";
//		}
//	};
//	/**
//	 * Local file is newer or remote file does not exist
//	 */
//	public static final Comparison COMPARISON_LOCAL_NEWER = new Comparison() {
//		@Override
//		public String toString() {
//			return "COMPARISON_LOCAL_NEWER";
//		}
//	};
//	/**
//	 * Files are identical or directories
//	 */
//	public static final Comparison COMPARISON_EQUAL = new Comparison() {
//		@Override
//		public String toString() {
//			return "COMPARISON_EQUAL";
//		}
//	};
//
//	/**
//	 * @param p The path to compare
//	 * @return COMPARISON_REMOTE_NEWER, COMPARISON_LOCAL_NEWER or COMPARISON_EQUAL
//	 */
//	public abstract Comparison compare(Path p);

}