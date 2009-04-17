/*
 * CompleteReader.java
 * 
 * Copyright (c) 2006, 2007 by Daniel Strecker
 * <daniel dot strecker R-REMOVE-THIS-R at gmx dot net>
 * 
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
 *
 *
 * Change History:
 * 2006-12-27 created
 * 2007-01-29 moved to package exists_de
 * 2007-12-07 added "if (tmp == -1) return read;" after reading into byte array
 * 2007-12-20 changed class comment
 *            added method read(InputStream, int)
 *            added method read(String,	long, byte[], int, int)
 */


package org.simmi;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * This class provides methods for reading a number of bytes, including waiting
 * for the data to become available on the stream.
 */
public class CompleteReader {

	/**
	 * Reads a range of bytes from the specified file, starting at the specified
	 * fileOffset and reading until length bytes are read or end of stream is
	 * reached. The read bytes are stored in the specified buffer, starting at
	 * index bufferOffset. The returned value is the number of bytes actually
	 * read.
	 */
	public static final int read(
		String filename,
		long fileOffset,
		byte[] buffer,
		int  bufferOffset,
		int length
	) throws IOException {
		//sanity checks NOT requiring i/o
        if (fileOffset < 0)
            throw new ArrayIndexOutOfBoundsException(
            	"fileOffset < 0 is not allowed");

        if (bufferOffset < 0)
            throw new ArrayIndexOutOfBoundsException(
            	"bufferOffset < 0 is not allowed");

        if (length < 0)
            throw new ArrayIndexOutOfBoundsException(
            	"length < 0 is not allowed");

        if (bufferOffset + length > buffer.length) {
            throw new ArrayIndexOutOfBoundsException(
            	"bufferOffset + length exceeds the buffer length:" +
            	(bufferOffset + length) + " > " + buffer.length
            );
        }

		//sanity checks requiring i/o
		File file = new File(filename);		
		if (! file.exists())
			throw new IllegalArgumentException(
				"file \"" + filename + "\" doesn't exist");
		
		if (file.isDirectory())
			throw new IllegalArgumentException(
				"file \"" + filename + "\" is a directory");
		
		if (! file.canRead())
			throw new IllegalArgumentException(
				"can't read file \"" + filename + "\"");

		long fileLen = file.length();
		long fileRangeEnd = fileOffset + length;
		if (fileRangeEnd > fileLen) { 
			throw new IllegalArgumentException(
				"fileOffset + length exceeds file length: " +
				fileRangeEnd + " > " + fileLen
			);
		}

		//open input stream, seek to pos, and delegate reading
		FileInputStream fis = new FileInputStream(file);

		fis.skip(fileOffset);

		int read = read(fis, buffer, bufferOffset, length);
		
		try { fis.close(); }
		catch (IOException e) { /* disregard */ }
		
		return read;
	}

	/**
     * Convenience method for read(String, int, byte[], int, int), which does
     * the creation of the byte array for holding the read data. If less then
     * length bytes are read, then the length of the returned byte array is
     * shrinked to fit the number of read bytes.
	 */
	public static final byte[] read(
		String filename,
		long fileOffset,
		int length
	) throws IOException
	{		
		byte[] buffer = new byte[length];

		int read = read(filename, fileOffset, buffer, 0, length);

        if (read == length)
        	return buffer;

        byte[] buffer2 = new byte[read];
        System.arraycopy(buffer, 0, buffer2, 0, read);
        
        return buffer2;
	}

    /**
     * Convenience method for read(InpuStream, byte[]), which does the creation
     * of the byte array for holding the read data. If less then length bytes
     * are read, then the length of the returned byte array is shrinked to fit
     * the number of read bytes.
     */
    public static final byte[] read(
    	InputStream is,
    	int length
    ) throws IOException {
    	byte[] buffer = new byte[length];

        int read = read(is, buffer);

        if (read == length)
        	return buffer;

        byte[] buffer2 = new byte[read];
        System.arraycopy(buffer, 0, buffer2, 0, read);
        
        return buffer2;
    }

    /**
     * A read method similar to InputStream.read(byte[]). The difference is that
     * this method doesn't return if the available() method of the underlying
     * stream returns zero.
     */
    public static final int read(
     InputStream is, byte[] b) throws IOException {
        return read(is, b, 0, b.length);
    }

    /**
     * A read method similar to InputStream.read(byte[], int, int). The
     * difference is that this method doesn't return if the available() method
     * of the underlying stream returns zero.
     */
    public static final int read(
    	InputStream is,
    	byte[] buffer,
    	int offset,
    	int length
    ) throws IOException
    {
        if (offset < 0)
            throw new ArrayIndexOutOfBoundsException(
             "offset < 0 is not allowed");

        if (length < 0)
            throw new ArrayIndexOutOfBoundsException(
             "length < 0 is not allowed");

        if (offset + length > buffer.length)
            throw new ArrayIndexOutOfBoundsException(
             "offset + length > buffer.length is not allowed");

        int read = 0;
        int tmp;

        while (length > 0) {
            if (is.available() == 0) { //in case read(byte[] ...) would NOT block
                tmp = is.read();

                if (tmp == -1)
                    return read;

                buffer[offset] = (byte)tmp;
                offset++;
                read++;
            }
            else { //in case there is data available, the underlying
                //read(byte[] ...) method is more efficient
                tmp = is.read(buffer, offset, length);
                
                if (tmp == -1)
                	return read;
                
                offset += tmp;
                length -= tmp;
                read   += tmp;
            } 
        }

        return read;
    }
}
