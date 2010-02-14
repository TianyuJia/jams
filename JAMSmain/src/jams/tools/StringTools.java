/*
 * StringTools.java
 * Created on 13. Februar 2010, 15:17
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

package jams.tools;

import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class StringTools {
    
    public static String[] parseTSRow(String row) throws ParseException {

        StringTokenizer tok = new StringTokenizer(row);
        int n = tok.countTokens();

        if (n > 1) {

            String dateString = tok.nextToken();
            String[] data;
            int i;

            String s = tok.nextToken();
            if (s.contains(":")) {
                data = new String[n - 1];
                data[0] = dateString + " " + s;
                i = 1;
            } else {
                data = new String[n];
                data[0] = dateString;
                data[1] = s;
                i = 2;
            }

            while (tok.hasMoreTokens()) {
                data[i++] = tok.nextToken();
            }

            return data;

        } else {

            return null;

        }
    }
    
    /**
     * Checks if a string is empty (i.e. if its null, has length 0 or contains only whitespaces
     * @param theString The string to be checked
     * @return True, if theString is empty, false otherwise
     */
    public static boolean isEmptyString(String theString) {
        if (theString == null) {
            return true;
        }
        theString = theString.trim();
        if (theString.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates a string representation of a stack trace
     * @param stea The stack trace
     * @return The stack trace string
     */
    public static String getStackTraceString(StackTraceElement[] stea) {
        String result = "";

        for (StackTraceElement ste : stea) {
            result += "        at " + ste.toString() + "\n";
        }
        return result;
    }

    /**
     * Splits a string into tokens and fills a string array with them
     * @param str The string to be splitted
     * @return A string array with the tokens
     */
    public static String[] toArray(String str) {
        return toArray(str, null);
    }

    /**
     * converts a string into array
     *
     * @param str
     * @param delimiter
     * @return result array
     */
    public static String[] toArray(String str, String delimiter) {

        if (str == null) {
            return null;
        }

        int delimLen = delimiter.length();
        Vector resultV = new Vector();
        String rest = str;
        String foundPart;
        int i = rest.indexOf(delimiter);
        while (i > -1) {
            foundPart = rest.substring(0, i);
            resultV.add(foundPart);
            rest = rest.substring(i + delimLen);
            //BPS.log().debug(i + ", foundPart: " + foundPart + ", rest: " + rest);
            i = rest.indexOf(delimiter);
        }
        resultV.add(rest);

        String[] result = new String[resultV.size()];
        resultV.toArray(result);

        return result;
    }

    /**
     * get one special part of token
     *
     * @param theToken
     * @param thePart   (int)
     * @param delimiter
     * @return string
     */
    public static String getPartOfToken(String theToken, int thePart, String delimiter) {
        String result = null;
        StringTokenizer tokenizer = new StringTokenizer(theToken, delimiter);
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            result = tokenizer.nextToken();
            i++;
            if (i == thePart) {
                return result;
            } else {
                result = null;
            }
        }
        return result;
    }
}
