/*
 * RandomParaSampler.java
 * Created on 10. Mai 2006, 17:03
 *
 * This file is part of JAMS
 * Copyright (C) 2005 S. Kralisch and P. Krause
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

package jams.tspaces;

import java.util.Random;
import java.util.StringTokenizer;
import org.unijena.jams.JAMS;
import org.unijena.jams.data.*;
import org.unijena.jams.io.GenericDataWriter;
import org.unijena.jams.model.*;
import com.ibm.tspaces.*;

/**
 *
 * @author nsk
 */
@JAMSComponentDescription(
title="Title",
        author="Author",
        description="Description"
        )
        public class RandomParaSamplerTS extends JAMSContext {
    
    /*
     *  Component variables
     */
     @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "tSpaceIP"
            )
            public JAMSString tSpaceIP;
     
     @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "tSpaceIP"
            )
            public JAMSString modelerKey;
     
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "Data file directory name"
            )
            public JAMSString dirName;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "List of parameter identifiers to be sampled"
            )
            public JAMSString parameterIDs;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "List of parameter value bounaries corresponding to parameter identifiers"
            )
            public JAMSString boundaries;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "Number of samples to be taken"
            )
            public JAMSInteger sampleCount;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "efficiency methods"
            )
            public JAMSString effMethodNames;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READWRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "efficiency values"
            )
            public JAMSDouble[] effValues;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "Flag for dis/enabling this sampler"
            )
            public JAMSBoolean enable;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT
            )
            public JAMSString paraFileName;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT
            )
            public JAMSString attribFileName;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "The model time interval"
            )
            public JAMSTimeInterval modelTimeInterval;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "Output file header descriptions"
            )
            public JAMSString attribHeader;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READWRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "Output file attribute"
            )
            public JAMSDoubleArray targetValue;
  
      
    JAMSDouble[] parameters;
    String[] parameterNames;
    double[] lowBound;
    double[] upBound;
    int currentCount;
    Random generator;
    GenericDataWriter paraWriter;
    SpaceTools spaceTools;
    GenericDataWriter attribWriter;
    double[][] valueArray;
    int timeStepCounter = 0;
    int runCounter = 0;
    int timeSteps = 0;
    
    
    
    
    public void init() {
       
            getModel().getRuntime().println("INIT RandomParaSampler");
            //connect to the TSpace
          
            spaceTools = new SpaceTools(modelerKey,tSpaceIP); 
            
            
            
            try{
                //Transaction trans = new Transaction();
                //trans.addTupleSpace(spaceTools.tsJAMS);
                //trans.beginTrans();
                if (!spaceTools.patternExists(new JAMSString("****initRandomPara****"))){
                     spaceTools.tsJAMS.write(new Tuple(new JAMSString("****initRandomPara****")));
                
                      if (!spaceTools.patternExists(new JAMSString("sampleCount"),JAMSInteger.class)){
                         spaceTools.setInteger(new JAMSString("sampleCount"),sampleCount);
                      }    
                     //set currentCount if value not set
                     if (!spaceTools.patternExists(new JAMSString("currentCount"),JAMSInteger.class)){
                         spaceTools.setInteger(new JAMSString("currentCount"),new JAMSInteger(0));
                     } 
                      spaceTools.tsJAMS.delete(new Tuple(new JAMSString("****initRandomPara****"))); 
                }   
              //  else{
               //      Thread.currentThread().sleep(300);
               // }
            //    trans.commitTrans();
            }
            catch (Exception e){
                System.out.println(e.toString());
            }
             //tsJAMS = spaceTools.getTupleSpace();
            
            
        if(enable.getValue()){
//add more checks!!!
            //retreiving parameter names
            int i;
            StringTokenizer tok = new StringTokenizer(parameterIDs.getValue(), ";");
            String key;
            parameters = new JAMSDouble[tok.countTokens()];
            parameterNames = new String[tok.countTokens()];
            
            i = 0;
            while (tok.hasMoreTokens()) {
                key = tok.nextToken();
                parameterNames[i] = key;
                parameters[i] = (JAMSDouble) getModel().getRuntime().getDataHandles().get(key);
                if(parameters[i] == null){
                    System.out.println("Problem in Sampler: parameter: " + key + "does not exist!");
                }
                i++;
            }
            
            //retreiving boundaries
            tok = new StringTokenizer(boundaries.getValue(), ";");
            int n = tok.countTokens();
            lowBound = new double[n];
            upBound = new double[n];
            
            //check if number of parameter ids and boundaries match
            if (n != i) {
                getModel().getRuntime().sendHalt("Component " + this.getInstanceName() + ": Different number of parameterIDs and boundaries!");
            }
            
            i = 0;
            while (tok.hasMoreTokens()) {
                key = tok.nextToken();
                key = key.substring(1, key.length()-1);
                
                StringTokenizer boundTok = new StringTokenizer(key, ">");
                lowBound[i] = Double.parseDouble(boundTok.nextToken());
                upBound[i] = Double.parseDouble(boundTok.nextToken());
                
                //check if upBound is higher than lowBound
                if (upBound[i] <= lowBound[i]) {
                    getModel().getRuntime().sendHalt("Component " + this.getInstanceName() + ": upBound must be higher than lowBound!");
                }
                
                i++;
            }
            
            //retreiving effMethodNames
            i = 0;
            tok = new StringTokenizer(effMethodNames.getValue(), ";");
            String[] effNames = new String[tok.countTokens()];
            i = 0;
            while (tok.hasMoreTokens()) {
                key = tok.nextToken();
                effNames[i] = key;
                i++;
            }
            
            //create parameter output file
           // paraWriter = new GenericDataWriter(dirName.getValue()+"/"+this.paraFileName.getValue());
            paraWriter = new GenericDataWriter(dirName.getValue()+"/"+this.paraFileName.getValue(),true);
            paraWriter.addColumn("Run");
            
            for(int j = 0; j < this.parameters.length; j++)
                paraWriter.addColumn(this.parameterNames[j]);
            
            for(int e = 0; e < effNames.length; e++){
                paraWriter.addColumn(effNames[e]);
            }
            
            
            paraWriter.writeHeader();
            
            //the attribute output file
            attribWriter = new GenericDataWriter(dirName.getValue()+"/"+attribFileName.getValue());
            
            attribWriter.addComment("J2K model output");
            attribWriter.addComment("");
            
            //always write time
            attribWriter.addColumn("date/time");
            
            for(int s = 0; s < this.sampleCount.getValue(); s++){
                int counter = s + 1;
                attribWriter.addColumn(attribHeader.getValue() + "_run_" + counter);
            }
            
            
            attribWriter.writeHeader();
            
            //setting up the dataArray
            //this.timeSteps = (int)modelTimeInterval.getNumberOfTimesteps();
//            this.valueArray = new double[this.sampleCount.getValue()][timeSteps];
            this.timeStepCounter = 0;
            this.runCounter = 0;
            
        }
    }
    
    public void run() {
            getModel().getRuntime().println("RUN RandomParaSampler");
      
        if (runEnumerator == null) {
            runEnumerator = getChildrenEnumerator();
        }
        
        if (!enable.getValue()) {
            singleRun();
        } else {
            resetValues();
            while (hasNext()) {
                updateValues();
                singleRun();
                
                paraWriter.addData(currentCount);
                for(int i = 0; i < this.parameters.length; i++)
                    paraWriter.addData(this.parameters[i].getValue());
                for(int e = 0; e < effValues.length; e++)
                    paraWriter.addData(this.effValues[e].getValue());
                try{
                    paraWriter.writeData();
                    
                    paraWriter.flush();
                    spaceTools.writeString(new JAMSString("result"),new JAMSString(paraWriter.getDataString()));
                    paraWriter.deleteDataString();
                    spaceTools.setString(new JAMSString("header"),new JAMSString(paraWriter.getHeaderString()));
                    spaceTools.setString(new JAMSString("fileName"),new JAMSString(paraWriter.getFileName()));
                    //spaceTools.tsJAMS.write(new JAMSString("result"),new String("paraWriter"));
                   // System.out.println(paraWriter.toString());
                    
                }catch(org.unijena.jams.runtime.RuntimeException e){
                    
                }
                
                
//                this.valueArray[runCounter] = this.targetValue.getValue();
                this.runCounter++;
            }
            
            runEnumerator.reset();
            while(runEnumerator.hasNext() && doRun) {
                JAMSComponent comp = runEnumerator.next();
            }
        }
    }
    
    
    
    
    public void cleanup() {
//         getModel().getRuntime().println("CLEANUP RandomParaSampler");
//            String tSpaceIP = new String("localhost");
//            try{
//                if (TupleSpace.exists("tsJAMS",tSpaceIP)) {
//                  TupleSpace tsJAMS = new TupleSpace("tsJAMS",tSpaceIP);
//                  Tuple tupleToWrite = new Tuple();
//                  tupleToWrite.add(new Field("result"));
//                  tupleToWrite.add(new Field(paraWriter.toString()));
//                  tsJAMS.write(tupleToWrite);
//                }    
//            }
//            catch (Exception e){
//                System.out.println(e.toString());
//            }
//            paraWriter.close();
        if (enable.getValue()) {
            /*
            //always write time
            //the time also knows a toString() method with additional formatting parameters
            //e.g. time.toString("%1$tY-%1$tm-%1$td %1$tH:%1$tM")
            JAMSCalendar timeStamp = this.modelTimeInterval.getStart();
            for(int t = 0; t < this.timeSteps; t++){
                attribWriter.addData(timeStamp.toString("%1$tY-%1$tm-%1$td %1$tH:%1$tM"));
                timeStamp.add(modelTimeInterval.getTimeUnit(), 1);
                for(int r = 0; r < this.sampleCount.getValue(); r++){
                    attribWriter.addData(this.valueArray[r][t]);
                }
                try {
                    attribWriter.writeData();
                } catch (org.unijena.jams.runtime.JAMSRuntimeException jre) {
                    getModel().getRuntime().println(jre.getMessage());
                }
            }
            attribWriter.close();
             
             
             */
        
        }
    }
    
    
    private void updateValues() {
       // int count = this.currentCount + 1;
         int count = spaceTools.readIntegerW(new JAMSString("currentCount")).getValue();
         
         
        getModel().getRuntime().println("Run No. " + count + " of " + this.sampleCount.getValue());
        double[] sample = this.randomSampler(parameters.length);
        
        for (int i = 0; i < parameters.length; i++) {
            //System.out.println("Parameter: " + this.parameterIDs.getValue());
            //double d = generator.nextDouble();
            parameters[i].setValue(sample[i]);//lowBound[i] + d * (upBound[i]-lowBound[i]));
            getModel().getRuntime().println("Para: " + parameterNames[i] + " = " + sample[i]);
        }
        
        spaceTools.addInteger(new JAMSString("currentCount"),new JAMSInteger(1));
        currentCount = spaceTools.readIntegerW(new JAMSString("currentCount")).getValue();
        //currentCount++;
    }
//     private void updateValues() {
//        int count = this.currentCount + 1;
//        getModel().getRuntime().println("Run No. " + count + " of " + this.sampleCount.getValue());
//        double[] sample = this.randomSampler(parameters.length);
//        
//        for (int i = 0; i < parameters.length; i++) {
//            //System.out.println("Parameter: " + this.parameterIDs.getValue());
//            //double d = generator.nextDouble();
//            parameters[i].setValue(sample[i]);//lowBound[i] + d * (upBound[i]-lowBound[i]));
//            getModel().getRuntime().println("Para: " + parameterNames[i] + " = " + sample[i]);
//        }
//        
//        currentCount++;
//    }
    
    private double[] randomSampler(int nSamples){
        double[] sample = new double[nSamples];
        for(int i = 0; i < nSamples; i++){
            double d = generator.nextDouble();
            sample[i] = (lowBound[i] + d * (upBound[i]-lowBound[i]));
        }
        return sample;
    }
    
    
    
    private void resetValues() {
        //set parameter values to initial values corresponding to their boundaries
        generator = new Random(System.currentTimeMillis());
        for (int i = 0; i < parameters.length; i++) {
            double d = generator.nextDouble();
            parameters[i].setValue(lowBound[i] + d * (upBound[i]-lowBound[i]));
        }
        currentCount = 0;
    }
    
    private void singleRun() {
        
        runEnumerator.reset();
        while(runEnumerator.hasNext() && doRun) {
            JAMSComponent comp = runEnumerator.next();
            //comp.updateInit();
            try {
                comp.init();
            } catch (Exception e) {
                
            }
        }
        
        
        runEnumerator.reset();
        while(runEnumerator.hasNext() && doRun) {
            JAMSComponent comp = runEnumerator.next();
            //comp.updateRun();
            try {
                comp.run();
            } catch (Exception e) {
                
            }
        }
        
        runEnumerator.reset();
        while(runEnumerator.hasNext() && doRun) {
            JAMSComponent comp = runEnumerator.next();
            try {
                comp.cleanup();
            } catch (Exception e) {
                
            }
        }
    }
    
    private boolean hasNext() {
        boolean nextRun = false;
         int current ,sample;
         current = spaceTools.readIntegerW(new JAMSString("currentCount")).getValue();
         sample =  spaceTools.readIntegerW(new JAMSString("sampleCount")).getValue();
         if (current<sample){
             nextRun = true;
         }
         else{
             nextRun = false; //only for debugging
         }
         
         
        return nextRun;
    }
}