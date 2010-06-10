package org.torrent.internal.bencoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.merapi.helper.handlers.DLControlRequestHandler;
import org.merapi.helper.handlers.DLControlRequestHandler_;
import org.merapi.helper.messages.DLControlRespondMessage;
import org.merapi.helper.messages.ListUpdateMessage;
import org.milipede.modules.list.model.vo.ListVO;
import org.torrent.data.FileInfo;
import org.torrent.internal.client.Main;
import org.torrent.internal.data.MetaInfoFile;
import org.torrent.internal.transfer.TransferBuilder;
import org.torrent.internal.util.Validator;

public final class MetaInfoFileIO {
	
	
	
	private MetaInfoFileIO() {

	}

	public static MetaInfoFile load(InputStream in) throws IOException {
		
		Validator.notNull(in, "Inputstream is null!");
		try {
			MetaInfoFile metaInfoFile = new MetaInfoFile((BMap) BDecoder.bdecode(in));

			int rows = metaInfoFile.getDataInfo().getPieceLength()/TransferBuilder.chunksize;
//out to test message overflow
			
			String baseDir = metaInfoFile.getDataInfo().getBaseDir();
			
			
			
//			DLControlRequestHandler.sendDLControlRespondMessage(ListUpdateMessage.ITEM_ADDED, metaInfoFile.getInfoHash().asHexString(), baseDir, metaInfoFile.getDataInfo().getDataSize(), metaInfoFile.getDataInfo().getPiecesCount(), rows, 0, 0, 0, 0, 0);
//			FileInfo [] fi = new FileInfo[metaInfoFile.getDataInfo().getFiles().size()];
//                        metaInfoFile.getDataInfo().getFiles().toArray(fi);
			
		
			DLControlRequestHandler.sendDLControlRespondMessage(DLControlRespondMessage.SELECT_CONTENT, metaInfoFile.getInfoHash().asHexString(), metaInfoFile.getDataInfo().getFiles(), metaInfoFile);
			Main.eg.sendMessage(DLControlRespondMessage.ITEM_ADDED, new ListVO(
					metaInfoFile.getInfoHash().asHexString(), baseDir, metaInfoFile.getDataInfo().getDataSize(), "G", 
					false, 0, 0, 0, 0, 0, metaInfoFile.getDataInfo().getPiecesCount(), rows));
			
			return metaInfoFile;
//			return null;
		} catch (ClassCastException e) {
			throw new IOException(e);
		} catch (BDecodingException e) {
			throw new IOException(e);
		} catch (BTypeException e) {
			throw new IOException(e);
		}
	}

	public static void save(MetaInfoFile mif, OutputStream out)
			throws IOException {
		Validator.nonNull(mif, out);
		out.write(BEncoder.bencode(mif.asDictionary()));
	}

}
