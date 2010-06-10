/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.torrent.transfer;

import java.io.IOException;
import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.io.DataReader;
import org.torrent.internal.io.DigesterService;
import org.torrent.internal.io.NBDataReader;
import org.torrent.internal.io.NBDataWriter;

/**
 *
 * @author gurkerl
 */
public interface TransferBuilderService {

    Transfer createTransfer(final TorrentMetaInfo contentInfo, NBDataReader reader, final NBDataWriter writer, DigesterService digester, DataReader breader) throws IOException;

}
