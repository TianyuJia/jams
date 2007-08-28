/*
 * ByteStream.java
 * Created on 30. Mai 2007, 16:11
 *
 * This file is part of JAMS
 * Copyright (C) 2007 FSU Jena
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

package jams.remote.common;

/**
 *
 * @author Sven Kralisch
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.SocketException;

public class ByteStream {
    
    public static final int BUFFER_SIZE = 1024*8;
    
    private static byte[] toByteArray(int in_int) {
        byte a[] = new byte[4];
        for (int i=0; i < 4; i++) {
            
            int  b_int = (in_int >> (i*8) ) & 255;
            byte b = (byte) ( b_int );
            
            a[i] = b;
        }
        return a;
    }
    
    private static int toInt(byte[] byte_array_4) {
        int ret = 0;
        for (int i=0; i<4; i++) {
            int b = (int) byte_array_4[i];
            if (i<3 && b<0) {
                b=256+b;
            }
            ret += b << (i*8);
        }
        return ret;
    }
    
    public static int toInt(InputStream in) throws java.io.IOException {
        try {
            byte[] byte_array_4 = new byte[4];
            
            byte_array_4[0] = (byte) in.read();
            byte_array_4[1] = (byte) in.read();
            byte_array_4[2] = (byte) in.read();
            byte_array_4[3] = (byte) in.read();
            
            return toInt(byte_array_4);
        } catch (SocketException se) {
            return -1;
        }
    }
    
    public static String toString(InputStream ins) throws java.io.IOException {
        try {
            int len = toInt(ins);
            if (len < 0) {
                return null;
            } else {
                return toString(ins, len);
            }
        } catch (SocketException se) {
            return null;
        }
    }
    
    private static String toString(InputStream ins, int len) throws java.io.IOException {
        String ret=new String();
        for (int i=0; i<len;i++) {
            ret+=(char) ins.read();
        }
        return ret;
    }
    
    public static void toStream(OutputStream os, int i) throws java.io.IOException {
        byte [] byte_array_4 = toByteArray(i);
        os.write(byte_array_4);
    }
    
    public static void toStream(OutputStream os, String s) throws java.io.IOException {
        int len_s = s.length();
        toStream(os, len_s);
        for (int i=0;i<len_s;i++) {
            os.write((byte) s.charAt(i));
        }
        os.flush();
    }
    
    private static byte[] toByteArray(InputStream ins, int an_int) throws
            java.io.IOException,
            Exception{
        
        byte[] ret = new byte[an_int];
        
        int offset  = 0;
        int numRead = 0;
        int outstanding = an_int;
        
        while (
                (offset < an_int)
                &&
                (  (numRead = ins.read(ret, offset, outstanding)) > 0 )
                ) {
            offset     += numRead;
            outstanding = an_int - offset;
        }
        if (offset < ret.length) {
            throw new Exception("Could not completely read from stream, numRead="+numRead+", ret.length="+ret.length); // ???
        }
        return ret;
    }
    
    private static void toFile(InputStream ins, FileOutputStream fos, int len, int buf_size) throws
            java.io.FileNotFoundException,
            java.io.IOException {
        
        byte[] buffer = new byte[buf_size];
        
        int       len_read=0;
        int total_len_read=0;
        
        while ( total_len_read + buf_size <= len) {
            len_read = ins.read(buffer);
            total_len_read += len_read;
            //System.out.println("read " + total_len_read + " of " + len + " bytes");
            fos.write(buffer, 0, len_read);
        }
        
        if (total_len_read < len) {
            toFile(ins, fos, len-total_len_read, buf_size/2);
        }
    }
    
    private static void toFile(InputStream ins, File file, int len) throws
            java.io.FileNotFoundException,
            java.io.IOException {
        
        FileOutputStream fos=new FileOutputStream(file);
        
        toFile(ins, fos, len, BUFFER_SIZE);
        
        fos.close();
    }
    
    public static void toFile(InputStream ins, File file) throws
            java.io.FileNotFoundException,
            java.io.IOException {
        
        int len = toInt(ins);
        toFile(ins, file, len);
    }
    
    public static void toStream(OutputStream os, File file)
    throws java.io.FileNotFoundException,
            java.io.IOException{
        
        toStream(os, (int) file.length());
        
        byte b[]=new byte[BUFFER_SIZE];
        InputStream is = new FileInputStream(file);
        int numRead=0;
        
        while ( ( numRead=is.read(b)) > 0) {
            os.write(b, 0, numRead);
        }
        os.flush();
        is.close();
    }
}
