/**
 * SimpleProperties.java
 * Copyright 2008 Zach Scrivena
 * zachscrivena@gmail.com
 * http://zs.freeshell.org/
 *
 * TERMS AND CONDITIONS:
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.milipede.storage.layer.internal.common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Represent a collection of properties, each denoted by a key-value pair.
 * This class is synchronized to allow simultaneous access; however, it must
 * be externally synchronized when iterating through its keys and values.
 */
public class SimpleProperties
{
	/** map of key-value pairs */
	private final Map<String,Object> map = new HashMap<String,Object>();


	/**
	 * Constructor.
	 * Creates an empty collection of properties.
	 */
	public SimpleProperties()
	{}


	/**
	 * Constructor.
	 * Creates a collection of properties containing the specified default properties.
	 *
	 * @param defaultProperties
	 *     default properties
	 */
	public SimpleProperties(
			final SimpleProperties defaultProperties)
	{
		synchronized (map)
		{
			synchronized (defaultProperties)
			{
				for (String k : defaultProperties.keySet())
				{
					map.put(k, defaultProperties.get(k));
				}
			}
		}
	}


	/**
	 * Constructor.
	 * Creates a collection of properties containing the default properties obtained
	 * by parsing the specified string for "key:value" pairs.
	 *
	 * @param defaultProperties
	 *     default properties
	 */
	public SimpleProperties(
			final String defaultProperties)
	{
		synchronized (map)
		{
			for (String s : defaultProperties.split("[\n\r\u0085\u2028\u2028]++"))
			{
				if (s.isEmpty() || s.startsWith("#"))
				{
					/* ignore empty lines and comments */
				}
				else
				{
					/* process single "key", or "key:value" pair */
					final String[] kv = StringManipulator.parseKeyValueString(s);
					map.put(kv[0], kv[1]);
				}
			}
		}
	}


	/**
	 * Get the set of keys in this collection of properties.
	 *
	 * @return
	 *     set of keys in this collection of properties
	 */
	public Set<String> keySet()
	{
		synchronized (map)
		{
			return map.keySet();
		}
	}


	/**
	 * Get the property value corresponding to the specified key.
	 *
	 * @param key
	 *     key
	 * @return
	 *     property value corresponding to the specified key
	 */
	public Object get(
			final String key)
	{
		synchronized (map)
		{
			return map.get(key);
		}
	}


	/**
	 * Set the property value corresponding to the specified key.
	 *
	 * @param key
	 *     key
	 * @param value
	 *     property value
	 */
	public void set(
			final String key,
			final Object value)
	{
		synchronized (map)
		{
			map.put(key, value);
		}
	}


	/**
	 * Get the property value (cast as a String) corresponding to the specified key.
	 * Unlike getString(), this method does not change the property value in the map to a String.
	 *
	 * @param key
	 *     key
	 * @return
	 *     property value (cast as a String)
	 */
	public String getAsString(
			final String key)
	{
		synchronized (map)
		{
			final Object value = map.get(key);

			if (value instanceof String)
			{
				return (String) value;
			}
			else
			{
				return value.toString();
			}
		}
	}


	/**
	 * Get the property value (cast as a String) corresponding to the specified key.
	 * This method changes the property value in the map to a String, if necessary.
	 *
	 * @param key
	 *     key
	 * @return
	 *     property value (cast as a String)
	 */
	public String getString(
			final String key)
	{
		synchronized (map)
		{
			final Object value = map.get(key);

			if (value instanceof String)
			{
				return (String) value;
			}
			else
			{
				final String stringValue = value.toString();
				map.put(key, stringValue);
				return stringValue;
			}
		}
	}


	/**
	 * Set the property value (a String) corresponding to the specified key.
	 *
	 * @param key
	 *     key
	 * @param value
	 *     property value (a String)
	 */
	public void setString(
			final String key,
			final String value)
	{
		synchronized (map)
		{
			map.put(key, value);
		}
	}


	/**
	 * Get the property value (cast as a boolean) corresponding to the specified key.
	 * This method changes the property value in the map to a Boolean, if necessary.
	 *
	 * @param key
	 *     key
	 * @return
	 *     property value (cast as a boolean)
	 */
	public boolean getBoolean(
			final String key)
	{
		synchronized (map)
		{
			final Object value = map.get(key);

			if (value instanceof Boolean)
			{
				return (Boolean) value;
			}
			else
			{
				final Boolean booleanValue = Boolean.valueOf(value.toString());
				map.put(key, booleanValue);
				return booleanValue;
			}
		}
	}


	/**
	 * Set the property value (a boolean) corresponding to the specified key.
	 *
	 * @param key
	 *     key
	 * @param value
	 *     property value (a boolean)
	 */
	public void setBoolean(
			final String key,
			final boolean value)
	{
		synchronized (map)
		{
			map.put(key, (Boolean) value);
		}
	}


	/**
	 * Get the property value (cast as an int) corresponding to the specified key.
	 * This method changes the property value in the map to an Integer, if necessary.
	 *
	 * @param key
	 *     key
	 * @return
	 *     property value (cast as an int)
	 */
	public int getInt(
			final String key)
	{
		synchronized (map)
		{
			final Object value = map.get(key);

			if (value instanceof Integer)
			{
				return (Integer) value;
			}
			else
			{
				final Integer integerValue = Integer.valueOf(value.toString());
				map.put(key, integerValue);
				return integerValue;
			}
		}
	}


	/**
	 * Set the property value (an int) corresponding to the specified key.
	 *
	 * @param key
	 *     key
	 * @param value
	 *     property value (an int)
	 */
	public void setInt(
			final String key,
			final int value)
	{
		synchronized (map)
		{
			map.put(key, (Integer) value);
		}
	}


	/**
	 * Get the property value (cast as a long) corresponding to the specified key.
	 * This method changes the property value in the map to a Long, if necessary.
	 *
	 * @param key
	 *     key
	 * @return
	 *     property value (cast as a long)
	 */
	public long getLong(
			final String key)
	{
		synchronized (map)
		{
			final Object value = map.get(key);

			if (value instanceof Long)
			{
				return (Long) value;
			}
			else
			{
				final Long longValue = Long.valueOf(value.toString());
				map.put(key, longValue);
				return longValue;
			}
		}
	}


	/**
	 * Set the property value (a long) corresponding to the specified key.
	 *
	 * @param key
	 *     key
	 * @param value
	 *     property value (a long)
	 */
	public void setLong(
			final String key,
			final long value)
	{
		synchronized (map)
		{
			map.put(key, (Long) value);
		}
	}


	/**
	 * Get the property value (cast as a File) corresponding to the specified key.
	 * This method changes the property value in the map to a File, if necessary.
	 *
	 * @param key
	 *     key
	 * @return
	 *     property value (cast as a File)
	 */
	public File getFile(
			final String key)
	{
		synchronized (map)
		{
			final Object value = map.get(key);

			if (value instanceof File)
			{
				return (File) value;
			}
			else
			{
				final File fileValue = new File(value.toString());
				map.put(key, fileValue);
				return fileValue;
			}
		}
	}


	/**
	 * Set the property value (a File) corresponding to the specified key.
	 *
	 * @param key
	 *     key
	 * @param value
	 *     property value (a File)
	 */
	public void setFile(
			final String key,
			final File value)
	{
		synchronized (map)
		{
			map.put(key, value);
		}
	}
}
