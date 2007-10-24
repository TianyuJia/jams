/*
 * ABCDataReader.java
 * Created on 21. März 2007, 17:25
 *
 * This file is part of JAMS
 * Copyright (C) 2006 FSU Jena
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

package jams.components.demomodel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import org.unijena.jams.data.*;
import org.unijena.jams.model.*;

/**
 *
 * @author Sven Kralisch
 */
@JAMSComponentDescription(
title="ABCModel precip reader",
        author="Sven Kralisch",
        description="ABC model climate data reader"
        )
        public class ABCDataReader extends JAMSComponent {
    
    /*
     *  Component variables
     */
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "Input data file name"
            )
            public JAMSString fileName;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.WRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "Precip value read from file"
            )
            public JAMSDouble precip;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.WRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "Runoff value read from file"
            )
            public JAMSDouble runoff;

    private BufferedReader reader;
    
    
    /*
     *  Component run stages
     */
    public void init(){
        try {
            reader = new BufferedReader(new FileReader(this.fileName.getValue()));
            reader.readLine();
            reader.readLine();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void run(){
        
        String line, token;
        try {
            
            line = reader.readLine();
            StringTokenizer st = new StringTokenizer(line);
            token = st.nextToken();
            token = st.nextToken();
            precip.setValue(Double.parseDouble(token));
            token = st.nextToken();
            runoff.setValue(Double.parseDouble(token));
            
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void cleanup(){
        try {
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
