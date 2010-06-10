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

import ch.cyberduck.core.i18n.Locale;
import ch.cyberduck.core.serializer.Deserializer;
import ch.cyberduck.core.serializer.DeserializerFactory;
import ch.cyberduck.core.serializer.Serializer;
import ch.cyberduck.core.serializer.SerializerFactory;

import org.apache.log4j.Logger;

/**
 * Attributes of a remote directory or file.
 *
 * @version $Id: PathAttributes.java 5760 2010-01-16 21:23:16Z dkocher $
 */
public class PathAttributes implements Attributes, Serializable {
    private static Logger log = Logger.getLogger(PathAttributes.class);

    /**
	 * The file length
	 * @uml.property  name="size"
	 */
    private long size = -1;
    /**
	 * The file modification date
	 * @uml.property  name="modified"
	 */
    private long modified = -1;
    /**
	 * @uml.property  name="accessed"
	 */
    private long accessed = -1;
    /**
	 * @uml.property  name="created"
	 */
    private long created = -1;
    /**
	 * @uml.property  name="owner"
	 */
    private String owner = null;
    /**
	 * @uml.property  name="group"
	 */
    private String group = null;
    /**
	 * The file type
	 * @uml.property  name="type"
	 */
    private int type = Path.FILE_TYPE;

    /**
	 * @uml.property  name="permission"
	 * @uml.associationEnd  
	 */
    protected Permission permission = null;
    /**
	 * @uml.property  name="checksum"
	 */
    private String checksum;

    public PathAttributes() {
        super();
    }

    public <T> PathAttributes(T dict) {
        this.init(dict);
    }

    public <T> void init(T serialized) {
        final Deserializer dict = DeserializerFactory.createDeserializer(serialized);
        String typeObj = dict.stringForKey("Type");
        if(typeObj != null) {
            this.type = Integer.parseInt(typeObj);
        }
        String sizeObj = dict.stringForKey("Size");
        if(sizeObj != null) {
            this.size = Long.parseLong(sizeObj);
        }
        String modifiedObj = dict.stringForKey("Modified");
        if(modifiedObj != null) {
            this.modified = Long.parseLong(modifiedObj);
        }
        Object permissionObj = dict.objectForKey("Permission");
        if(permissionObj != null) {
            this.permission = new Permission(permissionObj);
        }
    }

    public <T> T getAsDictionary() {
        final Serializer dict = SerializerFactory.createSerializer();
        dict.setStringForKey(String.valueOf(this.type), "Type");
        if(this.size != -1) {
            dict.setStringForKey(String.valueOf(this.size), "Size");
        }
        if(this.modified != -1) {
            dict.setStringForKey(String.valueOf(this.modified), "Modified");
        }
        if(null != permission) {
            dict.setObjectForKey(permission, "Permission");
        }
        return dict.<T>getSerialized();
    }

    /**
	 * @param size  the size of file in bytes.
	 * @uml.property  name="size"
	 */
    public void setSize(long size) {
        this.size = size;
    }

    /**
	 * @return  length the size of file in bytes.
	 * @uml.property  name="size"
	 */
    public long getSize() {
        return this.size;
    }

    public long getModificationDate() {
        return this.modified;
    }

    public void setModificationDate(long millis) {
        this.modified = millis;
    }

    public long getCreationDate() {
        return this.created;
    }

    public void setCreationDate(long millis) {
        this.created = millis;
    }

    public long getAccessedDate() {
        return this.accessed;
    }

    public void setAccessedDate(long millis) {
        this.accessed = millis;
    }

    /**
	 * @param  p
	 * @uml.property  name="permission"
	 */
    public void setPermission(Permission p) {
        this.permission = p;
    }

    /**
	 * @return
	 * @uml.property  name="permission"
	 */
    public Permission getPermission() {
        return this.permission;
    }

    /**
	 * @param type
	 * @uml.property  name="type"
	 */
    public void setType(int type) {
        this.type = type;
    }

    /**
	 * @return
	 * @uml.property  name="type"
	 */
    public int getType() {
        return this.type;
    }

    public boolean isVolume() {
        return (this.type & Path.VOLUME_TYPE) == Path.VOLUME_TYPE;
    }

    public boolean isDirectory() {
        return (this.type & Path.DIRECTORY_TYPE) == Path.DIRECTORY_TYPE
                || this.isVolume();
    }

    public boolean isFile() {
        return (this.type & Path.FILE_TYPE) == Path.FILE_TYPE;
    }

    public boolean isSymbolicLink() {
        return (this.type & Path.SYMBOLIC_LINK_TYPE) == Path.SYMBOLIC_LINK_TYPE;
    }

    /**
	 * @param o
	 * @uml.property  name="owner"
	 */
    public void setOwner(String o) {
        this.owner = o;
    }

    /**
	 * @return  The owner of the file or 'Unknown' if not set
	 * @uml.property  name="owner"
	 */
    public String getOwner() {
        if(null == this.owner) {
            return Locale.localizedString("Unknown");
        }
        return this.owner;
    }

    /**
	 * @param g
	 * @uml.property  name="group"
	 */
    public void setGroup(String g) {
        this.group = g;
    }

    /**
	 * @return
	 * @uml.property  name="group"
	 */
    public String getGroup() {
        if(null == this.group) {
            return Locale.localizedString("Unknown");
        }
        return this.group;
    }

    /**
	 * @return
	 * @uml.property  name="checksum"
	 */
    public String getChecksum() {
        return checksum;
    }

    /**
	 * @param checksum
	 * @uml.property  name="checksum"
	 */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
