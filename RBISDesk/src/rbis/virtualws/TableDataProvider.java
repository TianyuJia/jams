/*
 * DataProvider.java
 * Created on 28. Januar 2008, 13:12
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
package rbis.virtualws;

import org.w3c.dom.Document;
import rbis.virtualws.plugins.DataIO;

/**
 *
 * @author Sven Kralisch
 */
public class TableDataProvider {

    private Document xmlDoc;
    private VirtualWorkspace ws;

    public TableDataProvider(VirtualWorkspace ws, Document xmlDoc) {
        this.xmlDoc = xmlDoc;
        this.ws = ws;

    }

    public boolean hasNext() {
        return false;
    }

    public DataSet getNext() {
        return null;
    }
}
