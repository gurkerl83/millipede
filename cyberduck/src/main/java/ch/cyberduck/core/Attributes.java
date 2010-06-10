package ch.cyberduck.core;

/*
 *  Copyright (c) 2007 David Kocher. All rights reserved.
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

/**
 * @version   $Id: Attributes.java 5760 2010-01-16 21:23:16Z dkocher $
 */
public interface Attributes {

    /**
	 * @return
	 * @uml.property  name="type"
	 */
    public abstract int getType();

    /**
	 * @param i
	 * @see AbstractPath#FILE_TYPE
	 * @see AbstractPath#DIRECTORY_TYPE
	 * @see AbstractPath#SYMBOLIC_LINK_TYPE
	 * @see #isDirectory()
	 * @see #isFile()
	 * @see  #isSymbolicLink()
	 * @uml.property  name="type"
	 */
    public abstract void setType(int i);

    /**
	 * @return  The length of the file
	 * @uml.property  name="size"
	 */
    public abstract long getSize();

    /**
	 * @return  The time the file was last modified in millis UTC or -1 if unknown
	 * @uml.property  name="modificationDate"
	 */
    public abstract long getModificationDate();

    /**
	 * @param millis
	 * @uml.property  name="modificationDate"
	 */
    public abstract void setModificationDate(long millis);

    /**
	 * @return  The time the file was created in millis UTC or -1 if unknown
	 * @uml.property  name="creationDate"
	 */
    public abstract long getCreationDate();

    /**
	 * @param millis
	 * @uml.property  name="creationDate"
	 */
    public abstract void setCreationDate(long millis);

    /**
	 * @return  The time the file was last accessed in millis UTC or -1 if unknown
	 * @uml.property  name="accessedDate"
	 */
    public abstract long getAccessedDate();

    /**
	 * @param millis
	 * @uml.property  name="accessedDate"
	 */
    public abstract void setAccessedDate(long millis);

    /**
	 * @return    The file permission mask or null if unknown
	 * @uml.property  name="permission"
	 * @uml.associationEnd  
	 */
    public abstract Permission getPermission();

    /**
	 * @param  permission
	 * @uml.property  name="permission"
	 */
    public abstract void setPermission(Permission permission);

    /**
     * @return True if this path denotes a directory or is a symbolic link pointing to a directory
     */
    public abstract boolean isDirectory();

    public abstract boolean isVolume();

    /**
     * @return True if this path denotes a regular file or is a symbolic link pointing to a regular file
     */
    public abstract boolean isFile();

    /**
     * @return True if this path denotes a symbolic link.
     *         Warning! Returns false for Mac OS Classic Alias
     */
    public abstract boolean isSymbolicLink();

    /**
	 * @param size
	 * @uml.property  name="size"
	 */
    public abstract void setSize(long size);

    /**
	 * @param  owner
	 * @uml.property  name="owner"
	 */
    public abstract void setOwner(String owner);

    /**
	 * @param  group
	 * @uml.property  name="group"
	 */
    public abstract void setGroup(String group);

    /**
	 * @uml.property  name="owner"
	 */
    public abstract String getOwner();

    /**
	 * @uml.property  name="group"
	 */
    public abstract String getGroup();

    /**
	 * @uml.property  name="checksum"
	 */
    public abstract String getChecksum();

    /**
	 * @param  md5
	 * @uml.property  name="checksum"
	 */
    public abstract void setChecksum(String md5);
}