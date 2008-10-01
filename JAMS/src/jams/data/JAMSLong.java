/*
 * JAMSLong.java
 * Created on 28. September 2005, 16:06
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

/**
 *
 * @author S. Kralisch
 */
public class JAMSLong extends JAMSNumeric {

    private long value;

    /** Creates a new instance of JAMSLong */
    public JAMSLong() {
    }

    public JAMSLong(long value) {
        this.value = value;
    }

    public String toString() {
        return Long.toString(value);
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = Long.parseLong(value);
    }

    public boolean equals(Object other) {
        if ((other == null) || !(other instanceof JAMSLong)) {
            return false;
        }
        return value == ((JAMSLong) other).getValue();
    }  // end equals()
}
