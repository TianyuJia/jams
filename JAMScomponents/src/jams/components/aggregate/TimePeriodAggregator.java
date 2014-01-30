/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jams.components.aggregate;

import it.unimi.dsi.fastutil.doubles.Double2ObjectAVLTreeMap;
import jams.JAMS;
import jams.data.Attribute;
import jams.data.DefaultDataFactory;
import jams.model.JAMSComponent;
import jams.model.JAMSComponentDescription;
import jams.model.JAMSVarDescription;
import jams.tools.FileTools;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.logging.Level;

/**
 *
 * @author christian
 */
@JAMSComponentDescription(
        title = "TimePeriodAggregator",
        author = "Christian Fischer",
        description = "Aggregates timeseries values to a given time period of day, month, year or dekade")

public class TimePeriodAggregator extends JAMSComponent {
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "current time")
    public Attribute.Calendar time;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "current time")
    public Attribute.TimeInterval interval;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "id of e.g. the spatial unit",
    defaultValue = "1")
    public Attribute.Double id;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "Calculate the average value? If average is false, the sum will be calculated.")
    public Attribute.String[] attributeNames;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "The value(s) to be aggregated")
    public Attribute.Double[] value;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "A weight to be used to calculate the weighted aggregate",
    defaultValue="1")
    public Attribute.Double weight;
        
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "aggregationMode: sum; avg; min; max;")
    public Attribute.String[] outerAggregationMode;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "aggregationMode: sum; avg; min; max; ind;")
    public Attribute.String[] innerAggregationMode;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "enable or disable aggregation for the i-th value")
    public Attribute.Boolean[] enabled;
            
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "The time period to which the values should be aggregated, possible values are: daily, monthly, seasonal, halfyear, hydhalfyear, yearly, decadly or custom",
    defaultValue = "daily")
    public Attribute.String outerTimeUnit;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "The reference time period for aggregation, e.g. yearly mean of months mean, possible values are: daily, monthly, seasonal, halfyear, hydhalfyear, yearly, decadly",
    defaultValue = "daily")
    public Attribute.String innerTimeUnit;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "custom time period to which values should be aggregated. Is only used when timeUnit is set to custom.",
    defaultValue = "")
    public Attribute.String customOuterTimePeriod;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "custom number of hrus to filter",
    defaultValue = "")
    public Attribute.String idFilters;
        
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "the shapefile to which data should be added.")
    public Attribute.String shpFile;
    
    private class SpatialAggregationEntity{
        double innerValue;
        double outerValue[];
        
        int innerTimestepCounter;
        int outerTimestepCounter[];        
        
        public void initInnerAggregatedValueArray(AggregationMode innerAggregationMode) {
            switch (innerAggregationMode) {
                default:
                    innerValue = 0;
                    break;
                case AVERAGE:
                    innerValue = 0;
                    break;
                case MINIMUM:
                    innerValue = Double.POSITIVE_INFINITY;
                    break;
                case MAXIMUM:
                    innerValue = Double.NEGATIVE_INFINITY;
                    break;
                case LAST:
                    innerValue = 0;
                    break;
            }
            innerTimestepCounter = 0;
        }
        
        public void initOuterAggregatedValueArray(AggregationMode outerAggregationMode, 
                AggregationTimePeriod outerTimePeriod, AggregationTimePeriod innerTimePeriod,
                Attribute.TimeInterval interval) {
            
                                                
            switch (outerAggregationMode) {
                case SUM:
                    if (outerValue==null){
                        outerValue = new double[1];
                        outerTimestepCounter = new int[1];
                    }
                    outerValue[0] = 0;
                    outerTimestepCounter[0] = 0;
                    break;
                case AVERAGE:
                    if (outerValue==null){
                        outerValue = new double[1];
                        outerTimestepCounter = new int[1];
                    }
                    outerValue[0] = 0;
                    outerTimestepCounter[0] = 0;
                    break;
                case MINIMUM:
                    if (outerValue==null){
                        outerValue = new double[1];
                        outerTimestepCounter = new int[1];
                    }
                    outerValue[0] = Double.POSITIVE_INFINITY;
                    outerTimestepCounter[0] = 0;
                    break;
                case MAXIMUM:
                    if (outerValue==null){
                        outerValue = new double[1];
                        outerTimestepCounter = new int[1];
                    }
                    outerValue[0] = Double.NEGATIVE_INFINITY;
                    outerTimestepCounter[0] = 0;
                    break;
                case LAST:
                    if (outerValue==null){
                        outerValue = new double[1];
                        outerTimestepCounter = new int[1];
                    }
                    outerValue[0] = 0;
                    outerTimestepCounter[0] = 0;
                    break;
                case INDEPENDENT:{
                    if (outerValue == null) {
                        if (innerTimePeriod == AggregationTimePeriod.DAILY) {
                            if (outerTimePeriod == AggregationTimePeriod.MONTHLY) {
                                outerValue = new double[31];
                                outerTimestepCounter = new int[31];
                            } else {
                                outerTimestepCounter = new int[366];
                                outerValue = new double[366];
                            }
                        } else if (innerTimePeriod == AggregationTimePeriod.SEASONAL) {
                            outerValue = new double[4];
                            outerTimestepCounter = new int[4];
                        } else if (innerTimePeriod == AggregationTimePeriod.MONTHLY) {
                            outerValue = new double[12];
                            outerTimestepCounter = new int[12];
                        } else if (innerTimePeriod == AggregationTimePeriod.YEARLY) {
                            int l = interval.getEnd().get(Calendar.YEAR)
                                    - interval.getStart().get(Calendar.YEAR);
                            outerValue = new double[l + 1];
                            outerTimestepCounter = new int[l+1];
                        } else {
                            getModel().getRuntime().sendHalt("Error unsupported combination of aggregation options!");
                        }
                    }else{
                        Arrays.fill(outerValue, 0);
                        Arrays.fill(outerTimestepCounter, 0);
                    }
                    break;                
                }                    
            }
        }
        
        public void considerData(double data, AggregationMode innerAggregationMode, AggregationTimePeriod innerTimePeriod,
                Attribute.Calendar time, Attribute.TimeInterval interval) {
            if (data == JAMS.getMissingDataValue()){
                return;
            }
            switch(innerAggregationMode){
                case AVERAGE:
                    this.innerValue += data/weight.getValue(); break;
                case SUM:
                    this.innerValue += data/weight.getValue(); break;
                case MINIMUM:
                    this.innerValue = Math.min(data/weight.getValue(), innerValue); break;
                case MAXIMUM:
                    this.innerValue = Math.max(data/weight.getValue(), innerValue); break;
                case LAST:
                    this.innerValue = data/weight.getValue(); 
                    this.innerTimestepCounter = 1;
                    return;
                    
            }
            this.innerTimestepCounter += 1.0;
        }
        
        public void transferInnerDataToOuterData(
            AggregationMode outerAggregationMode, AggregationTimePeriod outerTimePeriod,
            AggregationMode innerAggregationMode, AggregationTimePeriod innerTimePeriod,             
            Attribute.Calendar time, Attribute.TimeInterval interval) {
            
            if (innerTimestepCounter==0){
                initInnerAggregatedValueArray(innerAggregationMode);
                return;
            }
            
            if (innerAggregationMode==AggregationMode.AVERAGE){
                innerValue /= (double)this.innerTimestepCounter;
            }
            
            
            if (outerAggregationMode == AggregationMode.AVERAGE || outerAggregationMode == AggregationMode.SUM) {
                    outerValue[0] += innerValue;
                    outerTimestepCounter[0]++;
                } else if (outerAggregationMode == AggregationMode.MAXIMUM) {
                    outerValue[0] = Math.max(outerValue[0],innerValue);
                    outerTimestepCounter[0]++;
                } else if (outerAggregationMode == AggregationMode.MINIMUM) {
                    outerValue[0] = Math.min(outerValue[0],innerValue);
                    outerTimestepCounter[0]++;
                    
                } else if (outerAggregationMode == AggregationMode.INDEPENDENT) {
                    if (innerTimePeriod == AggregationTimePeriod.DAILY) {
                        if (outerTimePeriod == AggregationTimePeriod.MONTHLY) {
                            int dayOfMonth = time.get(Calendar.DAY_OF_MONTH);
                            outerValue[dayOfMonth - 1] += innerValue;
                            outerTimestepCounter[dayOfMonth - 1]++;
                        } else {
                            int dayOfYear = time.get(Calendar.DAY_OF_YEAR);
                            outerValue[dayOfYear - 1] += innerValue;
                            outerTimestepCounter[dayOfYear - 1]++;
                        }
                    } else if (innerTimePeriod == AggregationTimePeriod.SEASONAL) {
                        int month = time.get(Calendar.MONTH);
                        if (month < 3){
                            outerValue[0] += innerValue;
                            outerTimestepCounter[0]++;
                        }else if (month < 6){
                            outerValue[1] += innerValue;
                            outerTimestepCounter[1]++;
                        }else if (month < 9){
                            outerValue[2] += innerValue;
                            outerTimestepCounter[2]++;
                        }else{
                            outerValue[3] += innerValue;
                            outerTimestepCounter[3]++;
                        }
                    } else if (innerTimePeriod == AggregationTimePeriod.MONTHLY) {
                        int month = time.get(Calendar.MONTH);
                        outerValue[month] += innerValue;
                        outerTimestepCounter[month]++;
                    } else if (innerTimePeriod == AggregationTimePeriod.YEARLY) {
                        int l1 = interval.getStart().get(Calendar.YEAR);

                        int year = time.get(Calendar.YEAR);
                        outerValue[year - l1] += innerValue;
                        outerTimestepCounter[year-l1]++;
                    } else {
                        getModel().getRuntime().sendHalt("Error unsupported combination of aggregation options!");
                    }                    
                }else if (outerAggregationMode == AggregationMode.LAST) {
                    outerValue[0] = innerValue;
                    outerTimestepCounter[0]=1;
                }
                initInnerAggregatedValueArray(innerAggregationMode);
        }
        
    }
    
    SimpleOutputDataStore outData[] = null;
    Double2ObjectAVLTreeMap<SpatialAggregationEntity[]> aggregatedValues = new Double2ObjectAVLTreeMap<SpatialAggregationEntity[]>();
    //ArrayList<SpatialAggregationEntity[]> aggregatedValues = new ArrayList<SpatialAggregationEntity[]>();
        
    Attribute.Calendar currentOuterTimePeriod = null, currentInnerTimePeriod = null;
    Attribute.Calendar currentTimeStep = null;
    AggregationTimePeriod outerTimeUnitID = AggregationTimePeriod.YEARLY;
    AggregationTimePeriod innerTimeUnitID = AggregationTimePeriod.DAILY;
    AggregationMode outerAggregationModeID[] = null;
    AggregationMode innerAggregationModeID[] = null;
        
    boolean isHeaderWritten = false;

    //shp kram
    boolean writeShpFile = false;
    File dbfFileOriginal = null;
    ShapeFileOutputDataStore shpStore[] = null;
            
    boolean isEnabled[] = null;
    HashSet<Double> selectedIds = null;
    
    enum AggregationTimePeriod{DAILY, YEARLY, SEASONAL, MONTHLY, DECADLY, HALFYEAR, HYDHALFYEAR, CUSTOM};
    enum AggregationMode{MINIMUM, MAXIMUM, AVERAGE, SUM, INDEPENDENT, LAST};
    
    ArrayList<Attribute.TimeInterval> customTimePeriods = new ArrayList<Attribute.TimeInterval>();
        
    int n = 0;
            
    @Override
    public void init(){
        n = attributeNames.length;
        outData = new SimpleOutputDataStore[n];        
        shpStore = new ShapeFileOutputDataStore[n];
        isEnabled = new boolean[n];
        outerAggregationModeID = new AggregationMode[n];
        innerAggregationModeID = new AggregationMode[n];
                
        writeShpFile = shpFile != null && shpFile.getValue() != null && !shpFile.getValue().isEmpty();
        
        outerTimeUnitID = getAggregationTimePeriodByString(outerTimeUnit.getValue());
        innerTimeUnitID = getAggregationTimePeriodByString(innerTimeUnit.getValue());
                        
        if (outerTimeUnitID == null){
            getModel().getRuntime().sendHalt("Unknown outer aggregation ID:" + outerTimeUnit);
        }
        if (innerTimeUnitID == null){
            getModel().getRuntime().sendHalt("Unknown inner aggregation ID:" + innerTimeUnit);
        }
        
        //try to open file in the beginning. 
        for (int i = 0; i < n; i++) {
            isEnabled[i] = !(enabled != null && enabled[i] != null && !enabled[i].getValue());
            
            if (!isEnabled[i]){
                continue;
            }
            
            outerAggregationModeID[i] = getAggregationModeByString(outerAggregationMode[i].getValue());
            
            String prefix = null;
            if (innerAggregationMode!=null){
                innerAggregationModeID[i] = getAggregationModeByString(innerAggregationMode[i].getValue());
                prefix = attributeNames[i].getValue() + " " + JAMS.i18n(outerTimeUnitID.name()) + " " + JAMS.i18n(outerAggregationModeID[i].name()) + " of " + JAMS.i18n(innerTimeUnitID.name()) + " " + JAMS.i18n(innerAggregationModeID[i].name());
            }else{
                innerAggregationModeID[i] = AggregationMode.AVERAGE;
                prefix = attributeNames[i].getValue() + " " + JAMS.i18n(outerTimeUnitID.name()) + " " + JAMS.i18n(outerAggregationModeID[i].name());
            }
            
            File f = new File(FileTools.createAbsoluteFileName(getModel().getWorkspace().getOutputDataDirectory().getAbsolutePath(), prefix + ".dat"));
            
            try {
                outData[i] = new SimpleOutputDataStore(f);
            } catch (IOException ioe) {
                getModel().getRuntime().sendHalt("Can't write to output file:" + f);
            } 
            
            if (writeShpFile) {
                File originalShpFile = new File(FileTools.createAbsoluteFileName(getModel().getWorkspacePath(), shpFile.getValue()));                
                File newDBFFile = new File(getModel().getWorkspace().getOutputDataDirectory().getAbsolutePath() + "/" + prefix);
                newDBFFile.mkdirs();                
                try{
                    shpStore[i] = new ShapeFileOutputDataStore(originalShpFile, newDBFFile);
                }catch(IOException ioe){
                    getModel().getRuntime().getLogger().log(Level.SEVERE, MessageFormat.format(ioe.toString(), getInstanceName()));
                }
            }
        }
        
        customTimePeriods.clear();
        if (!customOuterTimePeriod.getValue().isEmpty()) {
            String periods[] = customOuterTimePeriod.getValue().split(";");

            for (String period : periods) {
                period += " 6 1";
                Attribute.TimeInterval ti = DefaultDataFactory.getDataFactory().createTimeInterval();
                ti.setValue(period);
                this.customTimePeriods.add(ti);
            }
            //check if time periods are overlapping
            for (int i = 0; i < customTimePeriods.size(); i++) {
                Attribute.TimeInterval tii = customTimePeriods.get(i);
                for (int j = 0; j < customTimePeriods.size(); j++) {
                    if (i == j) {
                        continue;
                    }
                    Attribute.TimeInterval tij = customTimePeriods.get(j);
                    // sii sij  eii eij
                    if ((tii.getStart().before(tij.getStart()) && tii.getEnd().after(tij.getStart()))
                            || (tii.getEnd().before(tij.getEnd()) && tii.getEnd().after(tij.getEnd()))) {
                        getModel().getRuntime().sendHalt("Error: Time-Interval " + tii + " is overlapping with " + tij + "!");
                    }
                }
            }
        }         
    }

    private AggregationTimePeriod getAggregationTimePeriodByString(String mode){
        if (mode.compareToIgnoreCase("daily")==0){
            return AggregationTimePeriod.DAILY;
        }else if (mode.compareToIgnoreCase("monthly")==0){
            return AggregationTimePeriod.MONTHLY;
        }else if (mode.compareToIgnoreCase("yearly")==0){
            return AggregationTimePeriod.YEARLY;
        }else if (mode.compareToIgnoreCase("decadly")==0){
            return AggregationTimePeriod.DECADLY;
        }else if (mode.compareToIgnoreCase("seasonal")==0){
            return AggregationTimePeriod.SEASONAL;
        }else if (mode.compareToIgnoreCase("halfyear")==0){
            return AggregationTimePeriod.HALFYEAR;
        }else if (mode.compareToIgnoreCase("hydhalfyear")==0){
            return AggregationTimePeriod.HYDHALFYEAR;
        }else if (mode.compareToIgnoreCase("custom")==0){
            return AggregationTimePeriod.CUSTOM;
        }else{
            getModel().getRuntime().getLogger().log(Level.SEVERE, MessageFormat.format(JAMS.i18n("Unknown time unit:" + mode + ".\nPossible values are daily, monthly, yearly and decadly."), getInstanceName()));
            return null;
        }   
    }
    
    private AggregationMode getAggregationModeByString(String mode){
        if (mode.compareToIgnoreCase("sum")==0){
            return AggregationMode.SUM;
        }else if (mode.compareToIgnoreCase("avg")==0){
            return AggregationMode.AVERAGE;
        }else if (mode.compareToIgnoreCase("min")==0){
            return AggregationMode.MINIMUM;
        }else if (mode.compareToIgnoreCase("max")==0){
            return AggregationMode.MAXIMUM;
        }else if (mode.compareToIgnoreCase("last")==0){
            return AggregationMode.LAST;
        }else if (mode.compareToIgnoreCase("independent")==0){
            return AggregationMode.INDEPENDENT;
        }else{
            getModel().getRuntime().getLogger().log(Level.SEVERE, MessageFormat.format(JAMS.i18n("Unknown aggregation mode:" + mode + ".\nPossible values are sum, average, min, max, independent"), getInstanceName()));
            return null;
        } 
    }
    
    private Attribute.Calendar roundToTimePeriod(Attribute.Calendar in, AggregationTimePeriod timeUnitID){
        Attribute.Calendar out = in.clone();
        switch (timeUnitID){
            case DAILY: out.removeUnsignificantComponents(Attribute.Calendar.DAY_OF_MONTH); break;
            case MONTHLY: out.removeUnsignificantComponents(Attribute.Calendar.MONTH); break;
            case YEARLY: out.removeUnsignificantComponents(Attribute.Calendar.YEAR); break;
            case DECADLY: out.removeUnsignificantComponents(Attribute.Calendar.YEAR); 
                    int yearInDekade = out.get(Attribute.Calendar.YEAR) % 10;
                    out.set(out.get(Attribute.Calendar.YEAR)-yearInDekade, 0, 1, 12, 0, 0);
                    break;
            case SEASONAL: {out.removeUnsignificantComponents(Attribute.Calendar.MONTH); 
                    int month = out.get(Attribute.Calendar.MONTH);
                    if (month < 3){
                        month = 0;
                    }else if (month < 6){
                        month = 3;
                    }else if (month < 9){
                        month = 6;
                    }else {
                        month = 9;
                    }
                    out.set(out.get(Attribute.Calendar.YEAR), month, 1, 12, 0, 0);}
                    break;
            case HALFYEAR: {out.removeUnsignificantComponents(Attribute.Calendar.MONTH); 
                    int month = out.get(Attribute.Calendar.MONTH);
                    if (month < 6){
                        month = 0;
                    }else
                        month = 6;
                    out.set(out.get(Attribute.Calendar.YEAR), month, 1, 12, 0, 0);}
                break;
            case HYDHALFYEAR:
                {out.removeUnsignificantComponents(Attribute.Calendar.MONTH); 
                    int month = out.get(Attribute.Calendar.MONTH);
                    if (month >= 4 && month <= 9){
                        month = 4;
                        out.set(out.get(Attribute.Calendar.YEAR), month, 1, 12, 0, 0);
                    }else if (month < 4){
                        month = 10;
                        out.set(out.get(Attribute.Calendar.YEAR)-1, month, 1, 12, 0, 0);
                    }else{
                        month = 10;
                        out.set(out.get(Attribute.Calendar.YEAR), month, 1, 12, 0, 0);
                    }}
                break;
            case CUSTOM:{
                boolean isConsidered = false;
                for (Attribute.TimeInterval ti : customTimePeriods){
                    if (!ti.getStart().after(in) && !ti.getEnd().before(in)){
                        out.setValue(ti.getStart().toString());
                        isConsidered = true;
                        break;
                    }
                }
                if (!isConsidered)
                    return null;
                break;
            }
        }
        return out;
    }
       
    ArrayList<double[]> buffers = new ArrayList<double[]>();
    private void writeData(AggregationMode outerAggregationMode[], AggregationTimePeriod innerAggregationTimePeriod, 
            AggregationTimePeriod outerAggregationTimePeriod, Attribute.Calendar outerTimePeriod){
        //write data        
        if (!isHeaderWritten) {
            for (int i = 0; i < outData.length; i++) {
                if (!isEnabled[i])
                    continue;
                try {
                    outData[i].setHeader(aggregatedValues.keySet());
                    //outData[i].setHeader(aggregatedValues.size());
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                isHeaderWritten = true;
            }
        }
        for (int i = 0; i< buffers.size();i++){
            Arrays.fill(buffers.get(i), 0);
        }
        for (int i = 0; i < outData.length; i++) {
            if (!isEnabled[i]) {
                continue;
            }
            try {                
                int j=0;                
                                                
                if (buffers.size()==0){
                    buffers.add(new double[aggregatedValues.size()]);
                }
                
                double buffer[] = buffers.get(0);
                                
                for (double id : aggregatedValues.keySet()) {                    
                //for (int id=0;id<aggregatedValues.size();id++) {                    
                    SpatialAggregationEntity[] dataset = aggregatedValues.get(id);
                                        
                    switch (outerAggregationMode[i]) {                        
                        case AVERAGE:
                            dataset[i].outerValue[0] /= dataset[i].outerTimestepCounter[0];
                            buffer[j] = dataset[i].outerValue[0];
                            break;
                        case SUM:                            
                            buffer[j] = dataset[i].outerValue[0];
                            break;
                        case MINIMUM:
                            buffer[j] = dataset[i].outerValue[0];
                            break;
                        case MAXIMUM:
                            buffer[j] = dataset[i].outerValue[0];
                            break;
                        case INDEPENDENT:{
                            for (int k=0;k<dataset[i].outerValue.length;k++){
                                dataset[i].outerValue[k] /= (double)dataset[i].outerTimestepCounter[k];
                                if (buffers.size()<=k){
                                    buffers.add(new double[aggregatedValues.size()]);
                                }
                                buffers.get(k)[j] = dataset[i].outerValue[k];
                            }
                        }
                        case LAST:{
                            buffer[j] = dataset[i].outerValue[0];
                            break;
                        }
                    }
                    j++;
                }
                if (outerTimePeriod!=null){                    
                    if (outerAggregationMode[i] == AggregationMode.INDEPENDENT){
                        for (int k=0;k<buffers.size();k++){
                            Attribute.Calendar c = outerTimePeriod.clone();
                            if (innerAggregationTimePeriod == AggregationTimePeriod.DAILY && 
                                outerAggregationTimePeriod == AggregationTimePeriod.MONTHLY){                                
                                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), k, 1, 0, 0);                                
                            }
                            if (innerAggregationTimePeriod == AggregationTimePeriod.DAILY && 
                                outerAggregationTimePeriod == AggregationTimePeriod.YEARLY){                                
                                c.add(Calendar.DAY_OF_MONTH, k);
                            }
                            if (innerAggregationTimePeriod == AggregationTimePeriod.MONTHLY){
                                c.add(Calendar.MONTH, k);
                            }
                            if (innerAggregationTimePeriod == AggregationTimePeriod.SEASONAL){
                                c.add(Calendar.MONTH, 3*k);
                            }
                            if (innerAggregationTimePeriod == AggregationTimePeriod.YEARLY){
                                c.add(Calendar.YEAR, k);
                            }
                            outData[i].writeData(c.toString(), buffers.get(k));
                        }
                    }else{
                        outData[i].writeData(outerTimePeriod.toString(), buffer);
                    }
                }
            }catch (IOException ioe) {
                getModel().getRuntime().sendHalt("Can't write to output file:" + outData[i].getFile());
            }
        }
    }
        
    private void initAggregatedValueArray(SpatialAggregationEntity[] value, 
            AggregationMode innerAggregationMode[],
            AggregationMode outerAggregationMode[],
            AggregationTimePeriod innerTimePeriod, 
            AggregationTimePeriod outerTimePeriod) {
        
        for (int i = 0; i < value.length; i++) {
            if (!isEnabled[i]) {
                continue;
            }
            value[i] = new SpatialAggregationEntity();
            value[i].initInnerAggregatedValueArray(innerAggregationMode[i]);
            value[i].initOuterAggregatedValueArray(outerAggregationMode[i], outerTimePeriod, innerTimePeriod, interval);
            
        }
    }
    
    public boolean isIDSelected(double id) {      
        if (!this.idFilters.getValue().isEmpty()) {
            if (selectedIds != null){
                return selectedIds.contains(id);
            }
            selectedIds = new HashSet<Double>();
            
            String idFilter[] = idFilters.getValue().split(";");
            for (String filter : idFilter) {
                try {
                    if (filter.contains("[")) {
                        String ids[] = filter.split("-");
                        ids[0] = ids[0].replace("[", "");
                        ids[0] = ids[0].replace("]", "");
                        ids[1] = ids[1].replace("[", "");
                        ids[1] = ids[1].replace("]", "");
                        double id0 = Double.parseDouble(ids[0]);
                        double id1 = Double.parseDouble(ids[1]);
                        while (id0 <= id1){
                            selectedIds.add(id0);
                            id0++;
                        }
                    } else {
                        double idF = Double.parseDouble(filter);
                        selectedIds.add(idF);
                    }                    
                } catch (Throwable nfe) {
                    getModel().getRuntime().getLogger().log(Level.SEVERE, "Error: Could not parse filter string:" + filter);
                    return false;
                }
            }
            
            return selectedIds.contains(id);
            
        } else {
            return true;
        }        
    }
    
    private void transferInnerDataToOuterData(
            AggregationMode outerAggregationMode[], AggregationTimePeriod outerTimePeriod,
            AggregationMode innerAggregationMode[], AggregationTimePeriod innerTimePeriod,             
            Attribute.Calendar time, Attribute.TimeInterval timeInterval) {

        int n = outerAggregationMode.length;
        
        for (int i = 0; i < n; i++) {
            for (double id : aggregatedValues.keySet()) {                
            //for (int id=0;id<aggregatedValues.size();id++){
                SpatialAggregationEntity entity[] = aggregatedValues.get(id);
                if (isEnabled[i]){
                    entity[i].transferInnerDataToOuterData(outerAggregationMode[i], outerTimePeriod, 
                            innerAggregationMode[i], innerTimePeriod, time, timeInterval);                                
                }
            }
        }
    }
    
    @Override
    public void run(){
        //init
        double iid = id.getValue();
        SpatialAggregationEntity entity[] = null;
        //if (iid < aggregatedValues.size()){
            entity = aggregatedValues.get(iid);
        //}
        
        boolean idSelected = isIDSelected(iid);
        //no attribute selected
        if (!idSelected){
            return;
        }
        //init inner/outer aggregation arrays
        if (entity == null) {            
            //while(aggregatedValues.size()<=iid){
                entity = new SpatialAggregationEntity[n];
                initAggregatedValueArray(entity, innerAggregationModeID, outerAggregationModeID, innerTimeUnitID, outerTimeUnitID);            
                aggregatedValues.put(iid,entity);
            //}
            
        }
        
        if (currentTimeStep == null){
            currentTimeStep = time.getValue();
        }
        if (currentInnerTimePeriod == null){
            currentInnerTimePeriod = roundToTimePeriod(currentTimeStep, innerTimeUnitID);
            for (int i=0;i<entity.length;i++){
                if (isEnabled[i]) {
                    entity[i].innerTimestepCounter = 0;
                }
            }
        }
        if (currentOuterTimePeriod == null){
            currentOuterTimePeriod = roundToTimePeriod(currentTimeStep, outerTimeUnitID);
            for (int i=0;i<entity.length;i++){
                if (isEnabled[i]) {
                    Arrays.fill(entity[i].outerTimestepCounter,0);
                }
            }
        }
        //we are not yet within a custom time interval
        if (currentOuterTimePeriod == null) {            
            return;
        }
        //next time step begins
        boolean nextTimeStep = currentTimeStep.getTimeInMillis() != time.getTimeInMillis();
        if ( nextTimeStep ){            
            currentTimeStep = time.clone();
            //last time period was finished                        
            if (!currentInnerTimePeriod.equals(roundToTimePeriod(currentTimeStep, innerTimeUnitID))){
                //start next time period
                transferInnerDataToOuterData(outerAggregationModeID, outerTimeUnitID, 
                        innerAggregationModeID, innerTimeUnitID, currentInnerTimePeriod, interval);
                                                
                currentInnerTimePeriod = roundToTimePeriod(currentTimeStep, innerTimeUnitID);
            }
            if (!currentOuterTimePeriod.equals(roundToTimePeriod(currentTimeStep, outerTimeUnitID))){
                //start next time period
                writeData(outerAggregationModeID, innerTimeUnitID, outerTimeUnitID, currentOuterTimePeriod);                
                //reset data
                for (SpatialAggregationEntity entity2[] : this.aggregatedValues.values()){
//                for (int id=0;id<this.aggregatedValues.size();id++){
                    this.initAggregatedValueArray(aggregatedValues.get(id.getValue()), innerAggregationModeID, outerAggregationModeID, innerTimeUnitID, outerTimeUnitID);                                
                }
                currentOuterTimePeriod = roundToTimePeriod(currentTimeStep, outerTimeUnitID);
            }
        }
        for (int i = 0; i < n; i++) {
            if (!isEnabled[i]) {
                continue;
            }
            entity[i].considerData(value[i].getValue(), innerAggregationModeID[i], innerTimeUnitID, time, interval);
        }
        //recheck if this is the last timestep, if so output data
        if (nextTimeStep){
            Attribute.Calendar tmp = time.clone();
            tmp.add(interval.getTimeUnit(), interval.getTimeUnitCount());
            
            boolean isLastTimeStep = tmp.after(interval.getEnd());
            
            if (isLastTimeStep) {
                transferInnerDataToOuterData(outerAggregationModeID, outerTimeUnitID, 
                        innerAggregationModeID, innerTimeUnitID, currentTimeStep, interval);
                
                writeData(outerAggregationModeID, innerTimeUnitID, outerTimeUnitID, currentOuterTimePeriod);                
                if (writeShpFile) {
                    for (int i = 0; i < attributeNames.length; i++) {
                        if (!isEnabled[i]) {
                            continue;
                        }
                        getModel().getRuntime().sendInfoMsg("Transfering data to shapefile from dataset: " + outData[i].getFile().getName());
                        try {
                            shpStore[i].addDataToShpFiles(outData[i]);
                        } catch (IOException ioe) {
                            getModel().getRuntime().sendHalt("Can't write to output file:" + outData[i].getFile() + "\n" + ioe.toString());
                        }
                    }
                }
            }
        }   
    }
}