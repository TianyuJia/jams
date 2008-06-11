/*
 * EntityWriter.java
 * Created on 19. Juli 2006, 15:40
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

package jams.components.io;

import org.unijena.jams.data.*;
import org.unijena.jams.io.GenericDataWriter;
import org.unijena.jams.model.*;

/**
 *
 * @author S. Kralisch
 */
@JAMSComponentDescription(
title = "Entity file writer (spatial)",
author = "Sven Kralisch",
description = "This component can be used to output a number of selected entity " +
"attribute values at a certain point in time. The resulting CSV formatted ASCII " +
"file will contain one line per entity and one column per attribute. This " +
"component must be wrapped in a spatial, but not in a temporal context!")
        public class EntityWriter extends JAMSComponent {
    
    /*
     *  Component variables
     */
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "Data file directory name"
            )
            public JAMSString dirName;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "Output file name"
            )
            public JAMSString fileName;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "Output file header descriptions"
            )
            public JAMSStringArray headers;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "Output file attributes"
            )
            public JAMSDouble[] value;
    
    private GenericDataWriter writer;
    
    public void init() {
        writer = new GenericDataWriter(dirName.getValue()+"/"+fileName.getValue());
        
        writer.addComment("Entity attribute values");
        writer.addComment("");
        
        for (int i = 0; i < headers.getValue().length; i++) {
            writer.addColumn(headers.getValue()[i]);
        }
        
        writer.writeHeader();
    }
    
    public void run() {
        
        for (int i = 0; i < value.length; i++) {
            writer.addData(value[i]);
        }
        
        try {
            writer.writeData();
        } catch (org.unijena.jams.runtime.RuntimeException jre) {
            getModel().getRuntime().println(jre.getMessage());
        }
    }
    
    public void cleanup() {
        writer.close();
    }
}
