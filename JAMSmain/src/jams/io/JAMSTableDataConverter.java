/*
 * JAMSTableDataConverter.java
 *
 * Created on 5. Oktober 2005, 20:14
 *
 * This file is part of JAMS
 * Copyright (C) 2005 FSU Jena
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

import jams.data.*;
import java.util.*;

/**
 *
 * @author S. Kralisch
 */
public class JAMSTableDataConverter {
    
    
    public static double[] toDouble(JAMSTableDataArray a) {
        double[] d = new double[a.getValues().length];
        for (int i=0; i < a.getValues().length; i++) {
            d[i] = Double.parseDouble(a.getValues()[i]);
        }
        return d;
    }
    
    public static double[] toDouble(JAMSTableDataArray a, int start, int end) {
        double[] d = new double[end-start+1];
        int c = 0;
        for (int i=start-1; i < end; i++) {
            d[c++] = Double.parseDouble(a.getValues()[i]);
        }
        return d;
    }

    public static double[] toDouble(JAMSTableDataArray a, int start) {
        return toDouble(a, start, a.getLength());
    }
    
    public static int[] toInt(JAMSTableDataArray a) {
        int[] d = new int[a.getValues().length];
        for (int i=0; i < a.getValues().length; i++) {
            d[i] = Integer.parseInt(a.getValues()[i]);
        }
        return d;
    }
    
    public static JAMSCalendar parseTime(String timeString) {
        
        //Array keeping values for year, month, day, hour, minute
        String[] timeArray = new String[5];
        timeArray[0] = "0";
        timeArray[1] = "1";
        timeArray[2] = "1";
        timeArray[3] = "0";
        timeArray[4] = "0";
        
        StringTokenizer st = new StringTokenizer(timeString, ".-/ :");
        int n = st.countTokens();
        
        for (int i = 0; i < n; i++) {
            timeArray[i] = st.nextToken();
        }
        
        JAMSCalendar cal = JAMSDataFactory.createCalendar();
        cal.setValue(timeArray[0]+"-"+timeArray[1]+"-"+timeArray[2]+" "+timeArray[3]+":"+timeArray[4]);
        return cal;
    }
}


