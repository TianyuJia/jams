/*
 * SnapshotTools.java
 * Created on 24. September 2009, 09:04
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

import jams.data.Attribute.EntityCollection;
import jams.data.JAMSData;
import jams.dataaccess.DataAccessor;
import jams.model.AttributeAccess;
import jams.model.Component;
import jams.model.Context;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class SnapshotTools {

    public static class ContextSnapshotData implements Serializable{
        public EntityCollection entities;
        public HashMap<String, DataAccessor> dataAccessors;
        public byte[] iteratorState;
    }        
}
