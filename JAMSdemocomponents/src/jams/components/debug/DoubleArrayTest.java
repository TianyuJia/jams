/*
 * DoubleArrayTest.java
 * Created on 25. Mai 2009, 17:15
 *
 * This file is a JAMS component
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
package jams.components.debug;

import jams.data.*;
import jams.model.*;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
@JAMSComponentDescription (title = "Title",
                           author = "Author",
                           description = "Description")
public class DoubleArrayTest extends JAMSComponent {

    /*
     *  Component variables
     */
    @JAMSVarDescription (access = JAMSVarDescription.AccessType.WRITE,
                         description = "Description")
    public Attribute.DoubleArray values;

    @JAMSVarDescription (access = JAMSVarDescription.AccessType.READ,
                         description = "Description")
    public Attribute.Double id;

    @JAMSVarDescription (access = JAMSVarDescription.AccessType.WRITE,
                         description = "Description")
    public Attribute.DirName fName;

    /*
     *  Component run stages
     */
//    @Override
    public void init() {
        double[] doubles = new double[5];
        values.setValue(doubles);
    }

    @Override
    public void run() {

        fName.setValue("ID: " + id.getValue());
        double[] doubles = values.getValue();
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = id.getValue() + Math.random();
        }
        values.setValue(doubles);
//        System.out.println(fName);
//        System.out.println(values);
    }

    @Override
    public void cleanup() {
    }
}