/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gudy.azureus2.pluginsimpl.local.torrent;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.torrent.Torrent;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.torrent.TorrentCreator;
import org.gudy.azureus2.plugins.torrent.TorrentDownloader;
import org.gudy.azureus2.plugins.torrent.TorrentException;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import org.gudy.azureus2.plugins.torrent.TorrentManagerListener;

/**
 *
 * @author gurkerl
 */
public class TorrentManagerImpl implements TorrentManager {

    private static TorrentManagerImpl singleton;
    protected PluginInterface plugin_interface;

    protected TorrentManagerImpl(
            PluginInterface _pi) {
        plugin_interface = _pi;
    }

    public TorrentManager specialise(
            PluginInterface _pi) {
        // specialised one attached to plugin

        return (new TorrentManagerImpl(_pi));
    }

    public static TorrentManagerImpl getSingleton() {

        if (singleton == null) {

            // default singleton not attached to a plugin

            singleton = new TorrentManagerImpl(null);
        }

        return (singleton);


    }

    @Override
    public TorrentDownloader getURLDownloader(URL url) throws TorrentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TorrentDownloader getURLDownloader(URL url, String user_name, String password) throws TorrentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Torrent createFromBEncodedFile(File file) throws TorrentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Torrent createFromBEncodedFile(File file, boolean for_seeding) throws TorrentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Torrent createFromBEncodedInputStream(InputStream data) throws TorrentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Torrent createFromBEncodedData(byte[] data) throws TorrentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Torrent createFromBEncodedFile(File file, int preserve) throws TorrentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Torrent createFromBEncodedInputStream(InputStream data, int preserve) throws TorrentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Torrent createFromBEncodedData(byte[] data, int preserve) throws TorrentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Torrent createFromDataFile(File data, URL announce_url) throws TorrentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Torrent createFromDataFile(File data, URL announce_url, boolean include_other_hashes) throws TorrentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TorrentCreator createFromDataFileEx(File data, URL announce_url, boolean include_other_hashes) throws TorrentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TorrentAttribute[] getDefinedAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TorrentAttribute getAttribute(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TorrentAttribute getPluginAttribute(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addListener(TorrentManagerListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeListener(TorrentManagerListener l) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
