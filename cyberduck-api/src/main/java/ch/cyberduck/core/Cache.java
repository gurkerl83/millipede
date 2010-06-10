package ch.cyberduck.core;

/*
 *  Copyright (c) 2005 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */
import java.beans.XMLEncoder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
//import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * A cache for remote directory listings
 *
 * @version $Id: Cache.java 5543 2009-11-08 00:10:56Z dkocher $
 */
public class Cache<E extends AbstractPath> {

    protected static Logger log = Logger.getLogger(Cache.class);
    /**
     *
     */
//    private Map<String, AttributedList<E>> _impl = Collections.<String, AttributedList<E>>synchronizedMap(new LRUMap(
//            Preferences.instance().getInteger("browser.cache.size")) {
//
//        @Override
//        protected boolean removeLRU(LinkEntry entry) {
//            log.debug("Removing from cache:" + entry);
//            return true;
//        }
//    });

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#lookup(ch.cyberduck.core.PathReference)
	 */
    public E lookup(PathReference path) {
        final AttributedList<E> childs = this.get(Path.getParent(path.toString()));
        final E found = childs.get(path);
        if (null == found) {
            log.warn("Lookup failed for " + path + " in cache");
            return null;
        }
        return found;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#containsKey(E)
	 */
    public boolean containsKey(E path) {
        return this.containsKey(path.getAbsolute());
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#containsKey(java.lang.String)
	 */
    public boolean containsKey(String path) {
        return getImpl().containsKey(path);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#remove(E)
	 */
    public AttributedList<E> remove(E path) {
        return getImpl().remove(path.getAbsolute());
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#get(E)
	 */
    public AttributedList<E> get(E path) {
        return this.get(path.getAbsolute());
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#get(java.lang.String)
	 */
    public AttributedList<E> get(String path) {
        final AttributedList<E> childs = getImpl().get(path);
        if (null == childs) {
            log.warn("No cache for " + path);
            return AttributedList.emptyList();
        }
        return childs;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#get(E, java.util.Comparator, ch.cyberduck.core.PathFilter)
	 */
    public AttributedList<E> get(final E path, final Comparator<E> comparator, final PathFilter<E> filter) {
        return this.get(path.getAbsolute(), comparator, filter);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#get(java.lang.String, java.util.Comparator, ch.cyberduck.core.PathFilter)
	 */
    public AttributedList<E> get(final String path, final Comparator<E> comparator, final PathFilter<E> filter) {
        AttributedList<E> childs = getImpl().get(path);
        if (null == childs) {
            log.warn("No cache for " + path);
            return AttributedList.emptyList();
        }
        boolean needsSorting = !childs.attributes().get(AttributedList.COMPARATOR).equals(comparator);
        boolean needsFiltering = !childs.attributes().get(AttributedList.FILTER).equals(filter);
        if (needsSorting) {
            // Do not sort when the list has not been filtered yet
            if (!needsFiltering) {
                this.sort(childs, comparator);
            }
            // Saving last sorting comparator
            childs.attributes().put(AttributedList.COMPARATOR, comparator);
        }
        if (needsFiltering) {
            // Add previously hidden files to childs
            final Set<E> hidden = childs.attributes().getHidden();
            childs.addAll(hidden);
            // Clear the previously set of hidden files
            hidden.clear();
            for (E child : childs) {
                if (!filter.accept(child)) {
                    //child not accepted by filter; add to cached hidden files
                    childs.attributes().addHidden(child);
                    //remove hidden file from current file listing
                    childs.remove(child);
                }
            }
            // Saving last filter
            childs.attributes().put(AttributedList.FILTER, filter);
            // Sort again because the list has changed
            this.sort(childs, comparator);
        }
        return childs;
    }

    /**
     * The CopyOnWriteArrayList iterator does not support remove but the sort implementation
     * makes use of it. Provide our own implementation here to circumvent.
     *
     * @param childs
     * @param comparator
     * @see java.util.Collections#sort(java.util.List, java.util.Comparator)
     * @see java.util.concurrent.CopyOnWriteArrayList#iterator()
     */
    private void sort(AttributedList<E> childs, Comparator comparator) {
        // Because AttributedList is a CopyOnWriteArrayList we can not use Collections.sort
        AbstractPath[] sorted = childs.toArray(new AbstractPath[childs.size()]);
        Arrays.sort(sorted, (Comparator<AbstractPath>) comparator);
        for (int j = 0; j < sorted.length; j++) {
            childs.set(j, (E) sorted[j]);
        }
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#put(E, ch.cyberduck.core.AttributedList)
	 */
    public AttributedList<E> put(E path, AttributedList<E> childs) {
        return getImpl().put(path.getAbsolute(), childs);
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#clear()
	 */
    public void clear() {
        log.info("Clearing cache " + this.toString());
        getImpl().clear();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#encodeToXML()
	 */
    public void encodeToXML() {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream("D:/cust.xml");
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
        }
        XMLEncoder encoder = new XMLEncoder(os);
//Person p = new Person();
//p.setFirstName("John");
//encoder.writeObject(p);
        encoder.writeObject(getImpl());
        encoder.close();
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#getImpl()
	 */
    public Map<String, AttributedList<E>> getImpl() {
//        return _impl;
    	return null;
    }

    /* (non-Javadoc)
	 * @see ch.cyberduck.core.CacheService#setImpl(java.util.Map)
	 */
    public void setImpl(Map<String, AttributedList<E>> impl) {
//        this._impl = impl;
    }
}
