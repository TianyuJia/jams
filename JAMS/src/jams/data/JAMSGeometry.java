/*
 * JAMSGeometry.java
 * Created on 16. Dezember 2007, 19:11
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
package jams.data;

import com.vividsolutions.jts.geom.Geometry;
import java.io.Serializable;

/**
 *
 * @author C. Schwartze
 */
public class JAMSGeometry implements Attribute.Geometry, Serializable {

    private Geometry geo;

    public JAMSGeometry(Geometry geo) {
        this.geo = geo;
    }

    public void setValue(Geometry geo) {
        this.geo = geo;
    }

    public Geometry getValue() {
        return geo;
    }

    public void setValue(String data) {
    }
}
