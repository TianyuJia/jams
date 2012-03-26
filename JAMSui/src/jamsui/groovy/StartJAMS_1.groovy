/*
 * Copyright (C) 2012 Sven Kralisch <sven.kralisch at uni-jena.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jamsui.groovy


import java.io.*
import jams.*
import jams.tools.*
import jams.runtime.*
import jams.meta.*
import jams.io.*
import jams.model.*

// helper class needed later on, could also use null instead..
//class ExHandler implements ExceptionHandler {
//    
//    public void handle(JAMSException ex) {
//        println ex
//    }
//
//    public void handle(ArrayList<JAMSException> exList) {
//        for (JAMSException jex : exList) {
//            println ex
//        }
//    }
//}

// set some variables first
propertyFile = "D:/jamsapplication/nsk.jap"
modelFile = "D:/jamsmodeldata/J2K-Gramme/j2k_gramme.jam"
defaultWorkspacePath = new File(modelFile).getParent()

// create some property object
properties = JAMSProperties.createProperties()
properties.load(propertyFile)
properties.setProperty(JAMSProperties.GUICONFIG_IDENTIFIER, "false")
properties.setProperty(JAMSProperties.WINDOWENABLE_IDENTIFIER, "false")
properties.setProperty(JAMSProperties.VERBOSITY_IDENTIFIER, "true")
properties.setProperty(JAMSProperties.ERRORDLG_IDENTIFIER, "false")
properties.setProperty(JAMSProperties.DEBUG_IDENTIFIER, "1")

// tweak localization
JAMSTools.configureLocaleEncoding(properties)

// create XML document from model file
modelDoc = XMLTools.getDocument(modelFile)

// do some preprocessing on the XML
//ParameterProcessor.preProcess(modelDoc);

// create a runtime object
runtime = new StandardRuntime(properties)

// create a ModelDescriptor object, i.e. a representation of the XML for further tweaking etc.
modelIO = ModelIO.getStandardModelIO()
modelDescriptor = modelIO.loadModel(modelDoc, runtime.getClassLoader(), false, new ExHandler())

// set the workspace explicitly if needed
modelDescriptor.setWorkspacePath(defaultWorkspacePath)

controllerClazz = runtime.getClassLoader().loadClass("jams.components.concurrency.ConcurrentContext")
partitionerClazz = runtime.getClassLoader().loadClass("jams.components.concurrency.EntityPartitioner")

//modelDescriptor.enableConcurrency(2, controllerClazz, new ExHandler());
modelDescriptor.enableSpatialConcurrency(3, controllerClazz, partitionerClazz, new ExHandler());
System.out.println(XMLTools.getStringFromDocument(modelIO.getModelDocument(modelDescriptor)));

// load the model into the runtime and execute it
runtime.loadModel(modelDescriptor, defaultWorkspacePath)
runtime.runModel()
System.exit(0)
