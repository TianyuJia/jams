/*
 * JAMSEntityDataAccessor.java
 * Created on 29. Dezember 2005, 11:29
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

package org.unijena.jams.dataaccess;

/**
 *
 * @author S. Kralisch
 */
public interface JAMSEntityDataAccessor {

    public static final int READ_ACCESS = 0;
    public static final int WRITE_ACCESS = 1;
    public static final int READWRITE_ACCESS = 2;
    
    public int getAccessType();
    public void setIndex(int index);
    public void read();
    public void write();
    
    public Object getComponentObject();
}
