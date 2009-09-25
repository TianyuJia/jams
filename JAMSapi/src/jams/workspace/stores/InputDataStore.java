/*
 * InputDataStore.java
 * Created on 23. Januar 2008, 15:46
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
package jams.workspace.stores;

import jams.workspace.DataSet;
import jams.workspace.DataSetDefinition;

/**
 *
 * @author Sven Kralisch
 */
public interface InputDataStore extends DataStore {

    public static final int LIVE_MODE = 2;
    public static final int USE_CACHE_MODE = 1;
    public static final int BUILD_CACHE_MODE = 0;

    public static final String TYPE_TABLEDATASTORE = "tabledatastore";
    public static final String TYPE_TSDATASTORE = "tsdatastore";
    public static final String TYPE_J2KTSDATASTORE = "j2ktsdatastore";
    public static final String TYPE_SHAPEFILEDATASTORE = "shapefiledatastore";

    public String getDescription();

    public boolean hasNext();

    public DataSet getNext();

    public int getAccessMode();

    public DataSetDefinition getDataSetDefinition();
    
    public String getMissingDataValue();
    
}

