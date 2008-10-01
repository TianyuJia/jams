/*
 * JAMSIntegerArray.java
 * Created on 02. October 2005, 23:01
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
package jams.data;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author S. Kralisch
 */
public class JAMSIntegerArray implements JAMSData, Serializable {

    private int[] value;

    /**
     * Creates a new instance of JAMSIntegerArray
     */
    public JAMSIntegerArray() {
    }

    public String toString() {
        String s = "";
        if (value == null || value.length == 0) {
            s = "null";
        } else {
            s += value[0];
            for (int i = 1; i < value.length; i++) {
                s += "," + value[i];
            }
        }
        return s;
    }

    public int[] getValue() {
        return value;
    }

    public void setValue(int[] value) {
        this.value = value;
    }

    public void setValue(String value) {
        StringTokenizer st = new StringTokenizer(value, ",");
        int[] values = new int[st.countTokens()];
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.parseInt(st.nextToken().trim());
        }
        this.value = values;
    }
}
