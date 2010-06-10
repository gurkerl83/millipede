/**
 * FileIO.java
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

import java.io.IOException;
import java.io.InputStream;


/**
 * Perform file input and output operations.
 */
public final class FileIO
{
	/**
	 * Private constructor that should never be called.
	 */
	private FileIO()
	{}


	/**
	 * Read a specified number of bytes from the given input stream into the given buffer.
	 * This method blocks until the specified number of bytes has been read.
	 * An IOException is thrown if the end-of-stream is reached prematurely.
	 *
	 * @param input
	 *     input stream to be read from
	 * @param buffer
	 *     buffer to be populated with the read bytes
	 * @param offset
	 *     the start offset in array <code>buffer</code> at which the bytes are written
	 * @param len
	 *     number of bytes to be read
	 * @throws java.io.IOException
	 *     if the end-of-stream is reached prematurely, or other IO error occurs
	 */
	public static void blockingRead(
			final InputStream input,
			final byte[] buffer,
			final int offset,
			final int len)
			throws IOException
	{
		int total = 0;

		while (total < len)
		{
			final int n = input.read(buffer, offset + total, len - total);

			if (n == -1)
			{
				throw new IOException("End-of-stream reached prematurely.");
			}

			total += n;
		}
	}
}
