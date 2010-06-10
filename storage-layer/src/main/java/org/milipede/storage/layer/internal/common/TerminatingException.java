/**
 * TerminatingException.java
 * Copyright 2007 - 2008 Zach Scrivena
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


/**
 * Represent a runtime exception that terminates the program.
 * This exception should be allowed to propagate to the top-level for proper handling.
 */
public class TerminatingException
		extends RuntimeException
{
	/** exit status code to be reported to the OS */
	private final int exitCode;


	/**
	 * Constructor.
	 *
	 * @param message
	 *      Exception message; saved for later retrieval by the Throwable.getMessage() method
	 * @param cause
	 *      Cause of the exception; saved for later retrieval by the Throwable.getCause() method
	 * @param exitCode
	 *      Exit status code; saved for later retrieval by the TerminatingException.getExitCode()
	 *      method
	 */
	public TerminatingException(
			final String message,
			final Throwable cause,
			final int exitCode)
	{
		super(message, cause);
		this.exitCode = exitCode;
	}


	/**
	 * Constructor. Exit status code is assumed to be 1.
	 *
	 * @param message
	 *      Exception message; saved for later retrieval by the Throwable.getMessage() method
	 */
	public TerminatingException(
			final String message)
	{
		this(message, null, 1);
	}


	/**
	 * Return the exit status code to be reported to the OS.
	 *
	 * @return
	 *      exit status code
	 */
	public int getExitCode()
	{
		return exitCode;
	}
}
