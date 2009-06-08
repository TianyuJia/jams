/*
 * ObjectTest2.java
 * Created on 2. Juni 2009, 09:22
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

package jams.components.demo;

import jams.data.*;
import jams.model.*;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
@JAMSComponentDescription(
title="Title",
        author="Author",
        description="Description"
        )
        public class ObjectTest2 extends JAMSComponent {
    
    /*
     *  Component variables
     */
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            description = "Description"
            )
            public Attribute.Object value;
    
    
    /*
     *  Component run stages
     */
    
    @Override
    public void init() {
        
    }
    
    @Override
    public void run() {
        TestPOJO pojo = (TestPOJO) value.getValue();
        System.out.println("X: " + pojo.getX());
    }

    @Override
    public void cleanup() {
        
    }
}
