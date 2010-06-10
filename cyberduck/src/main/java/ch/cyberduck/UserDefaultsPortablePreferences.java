package ch.cyberduck;

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

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import ch.cyberduck.core.Local;
import ch.cyberduck.core.LocalFactory;
//import ch.cyberduck.ui.cocoa.foundation.NSBundle;
//import ch.cyberduck.ui.cocoa.foundation.NSDictionary;
//import ch.cyberduck.ui.cocoa.foundation.NSMutableDictionary;

import org.apache.log4j.Logger;

/**
 * @version $Id$
 */
public class UserDefaultsPortablePreferences extends UserDefaultsPreferences {
    private static Logger log = Logger.getLogger(UserDefaultsPortablePreferences.class);

//    private NSMutableDictionary dict;
//    private Dictionary dict;
    /**
	 * @uml.property  name="dict"
	 * @uml.associationEnd  qualifier="property:java.lang.String java.lang.String"
	 */
    private Map dict;
    
    @Override
    public Object getObject(String property) {
//        Object value = dict.objectForKey(property);
    	Object value = dict.get(property);
        if(null == value) {
            return super.getObject(property);
        }
        return value;
    }

    @Override
    public void setProperty(String property, String value) {
        log.info("setProperty:" + property + "," + value);
//        this.dict.setObjectForKey(value, property);
        this.dict.put(property, value);
    }

    @Override
    public void deleteProperty(String property) {
//        this.dict.removeObjectForKey(property);
    	this.dict.remove(property);
        this.save();
    }

    @Override
    protected void load() {
//        Local f = LocalFactory.createLocal(NSBundle.mainBundle().objectForInfoDictionaryKey("application.preferences.path").toString());
//        if(f.exists()) {
//            log.info("Found preferences file: " + f.toString());
//            this.dict = NSMutableDictionary.dictionary();
//            this.dict.setDictionary(NSDictionary.dictionaryWithContentsOfFile(f.getAbsolute()));
//        }
//        else {
//            this.dict = NSMutableDictionary.dictionary();
//        }
    	
    	this.dict = new HashMap();
    	
    }

    @Override
    public void save() {
//        Local f = LocalFactory.createLocal(NSBundle.mainBundle().objectForInfoDictionaryKey("application.preferences.path").toString());
//        f.getParent().mkdir();
//        this.dict.writeToFile(f.getAbsolute());
    }
}