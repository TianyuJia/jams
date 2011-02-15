/*
 * SerializableBufferedReader.java
 * Created on 5. November 2009, 16:25
 *
 * This file is part of JAMS
 * Copyright (C) FSU Jena
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 *
 */

package jams.io;

import java.io.IOException;
import java.io.Serializable;
import jams.JAMS;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 *
 * @author Christian Fischer
 */
public class SerializableBufferedReader implements Serializable {

    transient private RandomAccessFile reader = null;

    public SerializableBufferedReader(File s) throws FileNotFoundException {
        reader = new RandomAccessFile(s,"r");        
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
    
    public void read(char[] cbuf, int off, int len) throws IOException {
        byte[] array = new byte[cbuf.length];
        if (reader != null) {            
            reader.read(array, off, len);
        }
        for (int i=0;i<cbuf.length;i++)
            cbuf[i] = (char)array[i];
    }

    public int read() throws IOException {
        if (reader != null) {
            return reader.read();
        }
        throw new IOException(JAMS.i18n("reader_not_reader!"));
    }
    
    public String readLine() throws IOException {
        if (reader != null) {
            return reader.readLine();
        }
        throw new IOException(JAMS.i18n("reader_not_reader!"));
    }    
}
