/**
 * StringManipulator.java
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

import java.text.NumberFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.List;


/**
 * Perform string manipulation.
 */
public final class StringManipulator
{
	/** default regex delimiter pattern */
	private static final String DEFAULT_REGEX_DELIM = "[\\s]++";


	/**
	 * Private constructor that should never be called.
	 */
	private StringManipulator()
	{}


	/**
	 * Tokenize the given string, using the specified regex delimiter pattern.
	 *
	 * @param in
	 *     String to be tokenized
	 * @param regexDelim
	 *     Regex delimiter pattern
	 * @param includeDelim
	 *     If true, then delimiter tokens are returned too;
	 *     otherwise, only non-delimiter tokens are returned
	 * @return
	 *     Tokens in an array of strings;
	 *     null, if the string to be tokenized is null
	 */
	public static Token[] tokenize(
			final String in,
			final String regexDelim,
			final boolean includeDelimiters)
	{
		/* null input string */
		if (in == null)
		{
			return null;
		}

		/* regex matcher for delimiter */
		final Matcher delimiterMatcher = (regexDelim == null) ?
				Pattern.compile(DEFAULT_REGEX_DELIM).matcher(in) :
				Pattern.compile(regexDelim).matcher(in);

		/* return value */
		final List<Token> tokens = new ArrayList<Token>();

		/* initialize buffer string */
		final StringBuilder buffer = new StringBuilder();

		/* parse each character in input string */
		for (int i = 0; i < in.length(); i++)
		{
			delimiterMatcher.region(i, in.length());

			if (delimiterMatcher.lookingAt())
			{
				/* found delimiter match starting at this index */

				/* add buffer string to tokens if nonempty */
				final int bufferLength = buffer.length();

				if (bufferLength > 0)
				{
					tokens.add(new Token(buffer.toString(), false));
					buffer.delete(0, bufferLength);
				}

				if (includeDelimiters)
				{
					tokens.add(new Token(delimiterMatcher.group(), true));
				}

				/* advance index by length of the delimiter match */
				i += (delimiterMatcher.group().length() - 1);
			}
			else
			{
				/* not a match at this index, so we add the char */
				/* to the buffer string */
				buffer.append(in.charAt(i));
			}
		}

		/* flush buffer string if nonempty */
		if (buffer.length() > 0)
		{
			tokens.add(new Token(buffer.toString(), false));
		}

		/* return value */
		return tokens.toArray(new Token[tokens.size()]);
	}


	/**
	 * Extract a substring from a given string, according to the specified
	 * format.
	 *
	 * @param in
	 *     String from which the substring is to be extracted
	 * @param format
	 *     Format string describing the sequence of characters in the substring
	 * @param rangeChar
	 *     Range character to be used
	 * @param delimChar
	 *     Delimiter character to be used
	 * @return
	 *     Substring of the given string
	 */
	public static String substring(
			final String in,
			final String format,
			final char rangeChar,
			final char delimChar)
	{
		/* length of input string */
		final int len = in.length();

		/* nothing to do for empty string */
		if (len == 0)
		{
			return in;
		}

		/* tokenize format string by delimiter character, e.g. ',' */
		final String[] tokens = format.split(String.format("[\\%c]++", delimChar));

		/* return value */
		final StringBuilder out = new StringBuilder();

		/* Regex pattern for nonzero integers */
		final Pattern nonZeroIntegerPattern =
				Pattern.compile("\\s*([\\+\\-]?[1-9][0-9]*)\\s*");

		/* process each token */
		for (int i = 0; i < tokens.length; i++)
		{
			/* split betwen range characters, e.g. ':' */
			final String[] entries = tokens[i].split(String.format("[\\%c]++", rangeChar));

			int[] indices = new int[entries.length];

			/* check if entries are all non-zero integers */
			for (int j = 0; j < entries.length; j++)
			{
				final String entry = entries[j];

				if (nonZeroIntegerPattern.matcher(entry).matches())
				{
					/* convert to int */
					indices[j] = Integer.parseInt(entry);
				}
				else
				{
					/* error; not a nonzero integer */
					return null;
				}
			}

			if (indices.length == 1)
			{
				/* format "a" */
				indices[0] = normalizeIndex(len, indices[0]);
				out.append(in.charAt(indices[0] - 1));
			}
			else if (indices.length == 2)
			{
				/* format "a:b" */
				indices[0] = normalizeIndex(len, indices[0]);
				indices[1] = normalizeIndex(len, indices[1]);
				final int delta = (indices[0] <= indices[1]) ? 1 : -1;

				for (int k = indices[0]; ; k += delta)
				{
					if (delta > 0)
					{
						if (k > indices[1]) break;
					}
					else
					{
						if (k < indices[1]) break;
					}

					out.append(in.charAt(k - 1));
				}
			}
			else if (indices.length == 3)
			{
				/* format "a:b:c" */
				indices[0] = normalizeIndex(len, indices[0]);
				indices[2] = normalizeIndex(len, indices[2]);

				if ((indices[2] - indices[0]) * indices[1] < 0)
				{
					continue;
				}

				for (int k = indices[0]; ; k += indices[1])
				{
					if (indices[1] > 0)
					{
						if (k > indices[2]) break;
					}
					else
					{
						if (k < indices[2]) break;
					}

					out.append(in.charAt(k - 1));
				}
			}
			else
			{
				/* invalid format string */
				return null;
			}
		}

		return out.toString();
	}


	/**
	 * Normalize user-specified index value.
	 *
	 * @param len
	 *     Length of the source string
	 * @param index
	 *     User-specified index value
	 * @return
	 *     Normalized index value
	 */
	private static int normalizeIndex(
			final int len,
			final int index)
	{
		/* clip index to [1,len] */
		int newIndex = index;

		if (newIndex < 0)
		{
			newIndex += (len + 1);
		}

		if (newIndex < 1)
		{
			newIndex = 1;
		}

		if (newIndex > len)
		{
			newIndex = len;
		}

		return newIndex;
	}


	/**
	 * Return a formatted string representation of a given long number
	 * (format is locale-sensitive).
	 *
	 * @param n
	 *     Long number to be formatted
	 * @return
	 *     Formatted string representation of the given long number
	 */
	public static String formattedLong(
			final long n)
	{
		final NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setGroupingUsed(true);

		try
		{
			return nf.format(n);
		}
		catch (Exception e)
		{
			return (n + "");
		}
	}


	/**
	 * Return a formatted string representation of a given double number
	 * (format is locale-sensitive).
	 *
	 * @param n
	 *     Double number to be formatted
	 * @return
	 *     Formatted string representation of the given double number
	 */
	public static String formattedDouble(
			final double n)
	{
		final NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setGroupingUsed(true);

		try
		{
			return nf.format(n);
		}
		catch (Exception e)
		{
			return (n + "");
		}
	}


	/**
	 * Center-justify the string representation of a given object, padding with
	 * leading and trailing spaces so that its length is at least the specified
	 * width.
	 *
	 * @param o
	 *     Object to be center-justified
	 * @param width
	 *     Width of the resulting center-justified string
	 * @return
	 *     Center-justified string representation
	 */
	public static String centerJustify(
			final Object o,
			final int width)
	{
		final String s = o + "";
		final int len = s.length();
		final int totalSpace = width - len;

		if (totalSpace <= 0)
		{
			return s;
		}

		final StringBuilder t = new StringBuilder();

		final int leadingSpace = totalSpace / 2;

		for (int i = 0; i < leadingSpace; i++)
		{
			t.append(' ');
		}

		t.append(s);

		for (int i = 0; i < totalSpace - leadingSpace; i++)
		{
			t.append(' ');
		}

		return t.toString();
	}


	/**
	 * Left-justify the string representation of a given object, padding with
	 * trailing spaces so that its length is at least the specified width.
	 *
	 * @param o
	 *     Object for to be left-justified
	 * @param width
	 *     Width of the resulting left-justified string
	 * @return
	 *     Left-justified string representation
	 */
	public static String leftJustify(
			final Object o,
			final int width)
	{
		final String s = o + "";
		final int len = s.length();
		final int totalSpace = width - len;

		if (totalSpace <= 0)
		{
			return s;
		}

		final StringBuilder t = new StringBuilder();

		for (int i = 0; i < totalSpace; i++)
		{
			t.append(' ');
		}

		t.append(s);

		return t.toString();
	}


	/**
	 * Right-justify the string representation of a given object, padding with
	 * leading spaces so that its length is at least the specified width.
	 *
	 * @param o
	 *     Object to be right-justified
	 * @param width
	 *     Width of the resulting right-justified string
	 * @return
	 *     Right-justified string representation
	 */
	public static String rightJustify(
			final Object o,
			final int width)
	{
		final String s = o + "";
		final int len = s.length();
		final int totalSpace = width - len;

		if (totalSpace <= 0)
		{
			return s;
		}

		final StringBuilder t = new StringBuilder(s);

		for (int i = 0; i < totalSpace; i++)
		{
			t.append(' ');
		}

		return t.toString();
	}


	/**
	 * Repeat the string representation of a given object, a specified number
	 * of times.
	 *
	 * @param o
	 *     Object to be repeated
	 * @param n
	 *     Number of times to repeat
	 * @return
	 *     Repeated string representation
	 */
	public static String repeat(
			final Object o,
			final int n)
	{
		final String s = o + "";
		final StringBuilder t = new StringBuilder();

		for (int i = 0; i < n; i++)
		{
			t.append(s);
		}

		return t.toString();
	}


	/**
	 * Convert a specified string to title case, by capitalizing only the
	 * first letter of each word.
	 *
	 * @param s
	 *     Input string
	 * @return
	 *     Output string
	 */
	public static String toTitleCase(
			final String s)
	{
		final Token[] tokens = tokenize(s, "[\\s\\p{Punct}]++", true);
		final StringBuilder t = new StringBuilder();

		for (Token token : tokens)
		{
			if (token.val.isEmpty())
			{
				continue;
			}

			if (token.isDelimiter)
			{
				t.append(token.val);
			}
			else
			{
				t.append(Character.toUpperCase(token.val.charAt(0)));
				t.append(token.val.substring(1).toLowerCase());
			}
		}

		return t.toString();
	}


	/**
	 * Abbreviate a specified string, by keeping only the first letter
	 * of each word.
	 *
	 * @param s
	 *     Input string
	 * @return
	 *     Output string
	 */
	public static String abbreviate(
			final String s)
	{
		final Token[] tokens = tokenize(s, "[\\s\\p{Punct}]++", true);
		final StringBuilder t = new StringBuilder();

		for (Token token : tokens)
		{
			if (token.val.isEmpty())
			{
				continue;
			}

			if (token.isDelimiter)
			{
				t.append(token.val);
			}
			else
			{
				t.append(token.val.charAt(0));
			}
		}

		return t.toString();
	}


	/**
	 * Reverse the string.
	 *
	 * @param s
	 *     Input string
	 * @return
	 *     Output string
	 */
	public static String reverse(
			final String s)
	{
		return (new StringBuilder(s)).reverse().toString();
	}


	/**
	 * Trim away whitespace on the left.
	 *
	 * @param s
	 *     Input string
	 * @return
	 *     Output string
	 */
	public static String leftTrim(
			final String s)
	{
		final Matcher m = Pattern.compile("[\\s]++(.*)").matcher(s);

		if (m.matches())
		{
			return m.group(1);
		}
		else
		{
			return s;
		}
	}


	/**
	 * Trim away whitespace on the right.
	 *
	 * @param s
	 *     Input string
	 * @return
	 *     Output string
	 */
	public static String rightTrim(
			final String s)
	{
		return reverse(leftTrim(reverse(s)));
	}


	/**
	 * Delete extra whitespace in a specified string, by replacing contiguous
	 * whitespace characters with a single space.
	 *
	 * @param s
	 *     Input string
	 * @return
	 *     Output string
	 */
	public static String deleteExtraWhitespace(
			final String s)
	{
		final String[] tokens = s.split("[\\s]++");
		final StringBuilder t = new StringBuilder();

		for (int i = 0; i < tokens.length - 1; i++)
		{
			if (!tokens[i].isEmpty())
			{
				t.append(tokens[i]);
				t.append(' ');
			}
		}

		t.append(tokens[tokens.length - 1]);
		return t.toString();
	}


	/**
	 * Delete whitespace in a specified string, by deleting all whitespace
	 * characters.
	 *
	 * @param s
	 *     Input string
	 * @return
	 *     Output string
	 */
	public static String deleteWhitespace(
			final String s)
	{
		final String[] tokens = s.split("[\\s]++");
		final StringBuilder t = new StringBuilder();

		for (String token : tokens)
		{
			t.append(token);
		}

		return t.toString();
	}


	/**
	 * Delete punctuation in a specified string, by deleting all punctuation
	 * characters.
	 *
	 * @param s
	 *     Input string
	 * @return
	 *     Output string
	 */
	public static String deletePunctuation(
			final String s)
	{
		final String[] tokens = s.split("[\\p{Punct}]++");
		final StringBuilder t = new StringBuilder();

		for (String token : tokens)
		{
			t.append(token);
		}

		return t.toString();
	}


	/**
	 * Space out words in a specified string, by inserting a single space
	 * between concatenated words.
	 *
	 * @param s
	 *     Input string
	 * @return
	 *     Output string
	 */
	public static String spaceOutWords(
			final String s)
	{
		final StringBuilder t = new StringBuilder();

		for (int i = 0; i < s.length(); i++)
		{
			final char c = s.charAt(i);

			if (Character.isLowerCase(c) &&
					(i + 1 < s.length()) &&
					Character.isUpperCase(s.charAt(i + 1)))
			{
				t.append(c);
				t.append(' ');
			}
			else if (Character.isUpperCase(c) &&
					(i - 1 >= 0) &&
					Character.isUpperCase(s.charAt(i - 1)) &&
					(i + 1 < s.length()) &&
					Character.isLowerCase(s.charAt(i + 1)))
			{
				t.append(' ');
				t.append(c);
			}
			else
			{
				t.append(c);
			}
		}

		return t.toString();
	}


	/**
	 * Swap the case of a specified string, by converting lower case
	 * characters to upper case and vice versa.
	 *
	 * @param s
	 *     Input string
	 * @return
	 *     Output string
	 */
	public static String swapCase(
			final String s)
	{
		final StringBuilder t = new StringBuilder();

		for (int i = 0; i < s.length(); i++)
		{
			final char c = s.charAt(i);

			if (Character.isLowerCase(c))
			{
				t.append(Character.toUpperCase(c));
			}
			else if (Character.isUpperCase(c))
			{
				t.append(Character.toLowerCase(c));
			}
			else
			{
				t.append(c);
			}
		}

		return t.toString();
	}


	/**
	 * Return a modified version of the input string with the specified
	 * strings trimmed off from its tail, if found. The returned string is
	 * guaranteed not to end with any of the specified strings.
	 *
	 * @param s
	 *     Input string
	 * @param trimStrings
	 *     Strings to be trimmed off from the end of the input string;
	 *     empty strings are ignored
	 * @return
	 *     Modified version of input string
	 */
	public static String trimTrailingStrings(
			final String s,
			final String[] trimStrings)
	{
		/* resultant string (to be trimmed) */
		final StringBuilder result = new StringBuilder(s);

		while (true)
		{
			boolean modified = false;

			for (String trim : trimStrings)
			{
				final int trimLength = trim.length();

				/* ignore empty strings */
				if (trimLength == 0)
				{
					continue;
				}

				TrimString:
				while (true)
				{
					final int resultLength = result.length();

					if ((resultLength >= trimLength) &&
							result.substring(resultLength - trimLength).equals(trim))
					{
						/* trim trailing string */
						result.delete(resultLength - trimLength, resultLength);
						modified = true;
					}
					else
					{
						break TrimString;
					}
				}
			}

			if (!modified)
			{
				break;
			}
		}

		return result.toString();
	}


	/**
	 * Return a modified version of the input string with the specified
	 * strings trimmed off from its head, if found. The returned string is
	 * guaranteed not to start with any of the specified strings.
	 *
	 * @param s
	 *     Input string
	 * @param trimStrings
	 *     Strings to be trimmed off from the head of the input string;
	 *     empty strings are ignored
	 * @return
	 *     Modified version of input string
	 */
	public static String trimLeadingStrings(
			final String s,
			final String[] trimStrings)
	{
		/* resultant string (to be trimmed) */
		final StringBuilder result = new StringBuilder(s);

		while (true)
		{
			boolean modified = false;

			for (String trim : trimStrings)
			{
				final int trimLength = trim.length();

				/* ignore empty strings */
				if (trimLength == 0)
				{
					continue;
				}

				TrimString:
				while (true)
				{
					final int resultLength = result.length();

					if ((resultLength >= trimLength) &&
						result.substring(0, trimLength).equals(trim))
					{
						/* trim leading string */
						result.delete(0, trimLength);
						modified = true;
					}
					else
					{
						break TrimString;
					}
				}
			}

			if (!modified)
			{
				break;
			}
		}

		return result.toString();
	}


	/**
	 * Return a modified version of the input string with the specified
	 * strings trimmed off from its head and tail, if found. The returned
	 * string is guaranteed not to start or end with any of the specified
	 * strings.
	 *
	 * @param s
	 *     Input string
	 * @param trimStrings
	 *     Strings to be trimmed off from the head and tail of the input
	 *     string; empty strings are ignored
	 * @return
	 *     Modified version of input string
	 */
	public static String trimStrings(
			final String s,
			final String[] trimStrings)
	{
		return trimLeadingStrings(trimTrailingStrings(s, trimStrings), trimStrings);
	}


	/**
	 * Parse the specified "key:value" string.
	 * The key string is the trimmed string before the first colon (:).
	 * The value string is the trimmed string after the first colon (:);
	 * if this is enclosed in double quotes ("), then the value string is the string between them.
	 * If the specified string does not contain a colon (:), then the key string is the entire
	 * string trimmed, and the value string is the empty string.
	 *
	 * @param s
	 *     string to be parsed
	 * @return
	 *     String array containing two elements: the first is the key string,
	 *     the second is the value string
	 */
	public static String[] parseKeyValueString(
			final String s)
	{
		final String[] t = new String[2];
		final int k = s.indexOf(":");

		if (k >= 0)
		{
			t[0] = s.substring(0, k).trim();
			t[1] = s.substring(k + 1).trim();

			final int leftQuote = t[1].indexOf("\"");
			final int rightQuote = t[1].lastIndexOf("\"");

			if ((leftQuote == 0) &&
					(rightQuote == (t[1].length() - 1)) &&
					(leftQuote < rightQuote))
			{
				t[1] = t[1].substring(1, rightQuote);
			}
		}
		else
		{
			t[0] = s.trim();
			t[1] = "";
		}

		return t;
	}

	/******************
	 * NESTED CLASSES *
	 ******************/

	/**
	 * Represent a string token.
	 */
	public static class Token
	{
		public String val;
		public boolean isDelimiter;

		public Token(String val, boolean isDelimiter)
		{
			this.val = val;
			this.isDelimiter = isDelimiter;
		}
	}
}
