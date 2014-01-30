/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jams.components.aggregate;

import jams.data.Attribute;
import jams.model.JAMSComponent;
import jams.model.JAMSComponentDescription;
import jams.model.JAMSVarDescription;

/**
 *
 * @author christian
 */
@JAMSComponentDescription(
        title = "KlimaKennwerte",
        author = "Christian Fischer",
        description = "Calculated standard Klimakennwerte for J2000Klima")
public class KlimaKennwerte extends JAMSComponent {
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "Current time")
    public Attribute.Calendar time;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "daily min temperatur",
    unit="°C")
    public Attribute.Double tmin;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "daily mean temperatur",
    unit="°C")
    public Attribute.Double tmean;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "daily max temperatur",
    unit="°C")
    public Attribute.Double tmax;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "daily precipitation",
    unit="L")
    public Attribute.Double precip;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "daily windspeed",
    unit="m/s")
    public Attribute.Double wind;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "daily pot. evapotranspiration",
    unit="L")
    public Attribute.Double potET;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "size of hru",
    unit="m²")
    public Attribute.Double area;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "1 if hitzetag")
    public Attribute.Double isHotDay;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    description = "number of hitzetage without interruption")
    public Attribute.Double successiveHotDays;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "1 if hitzeperiode has just started")
    public Attribute.Double isBeginningOfHotPeriod;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "1 if hitzeperiode")
    public Attribute.Double isHotPeriod;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "threshold for hitzetage",
    defaultValue = "30",
    unit="°C")
    public Attribute.Double hotDayThreshold;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "1 if tropennacht")
    public Attribute.Double isTropicalNight;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "threshold for tropennacht",
    defaultValue = "20",
    unit="°C")
    public Attribute.Double tropicalNightThreshold;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "1 if sommertag")
    public Attribute.Double isSummerDay;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "threshold for sommertag",
    defaultValue = "25",
    unit="°C")
    public Attribute.Double summerDayThreshold;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "1 if frosttag")
    public Attribute.Double isFrostDay;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "threshold for frosttag",
    defaultValue = "0",
    unit="°C")
    public Attribute.Double frostDayThreshold;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "threshold for frosttag",
    defaultValue = "-5",
    unit="°C")
    public Attribute.Double permanentFrostDayThreshold;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    description = "number of hitzetage without interruption")
    public Attribute.Double successivePermanentFrostDay;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "1 if hitzeperiode has just started")
    public Attribute.Double isBeginningOfPermanentFrostPeriod;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "1 if hitzeperiode")
    public Attribute.Double isPermanentFrostPeriod;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "1 if eistag")
    public Attribute.Double isIceDay;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    description = "threshold for eistag",
    defaultValue = "0",
    unit="°C")
    public Attribute.Double iceDayThreshold;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    description = "1 if tmin is/was below 0°C")
    public Attribute.Double isTempBelowZero;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "1 if frost/tau has been changed")
    public Attribute.Double isFrostDefrostChange;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "calculates kuehlgradtage")
    public Attribute.Double coolingDegreeDays;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "calculates heiztage where tmean is below 15°C")
    public Attribute.Double isHeatDay;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "calculates if we are in the thermal vegetation period")
    public Attribute.Double isThermalVegetationPeriodTrigger;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "calculates if we are in the forest vegetation period")
    public Attribute.Double isForestVegetationPeriodTrigger;
        
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    description = "calculates the number of successive days where temperature is higher 5°C")
    public Attribute.Double successiveDaysWithTmeanAboveFiveDegree;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    description = "calculates the number of successive days where temperature is higher 10°C")
    public Attribute.Double successiveDaysWithTmeanAboveTenDegree;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "calculates if a dry period has started")
    public Attribute.Double isBeginningOfDryPeriod;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "calculates if we are within a dry period")
    public Attribute.Double isDryPeriod;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "calculates if more than 20mm precip did occur")
    public Attribute.Double isPrecipHigher20mm;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "calculates if more than 30mm precip did occur")
    public Attribute.Double isPrecipHigher30mm;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "calculates if more than 40mm precip did occur")
    public Attribute.Double isPrecipHigher40mm;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE,
    description = "calculates if more than 50mm precip did occur")
    public Attribute.Double isPrecipHigher50mm;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    description = "calculates the number of successive days where precip is zero")
    public Attribute.Double successiveDaysWithoutRain;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    description = "calculates if windspeed is higher than 28m/s")
    public Attribute.Double isHeavyStorm;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    description = "calculates if windspeed is between 8 and 18m/s")
    public Attribute.Double isProductiveWindday;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    description = "calculates the climatic water balance (P-potET)")
    public Attribute.Double KWB;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    description = "calculates the climatic water balance (P-potET) during thermal vegetation period")
    public Attribute.Double KWBinThermalVegetationPeriod;

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    description = "calculates the klimatische wasserbilanz (P-potET) during forstlicher vegetation period")
    public Attribute.Double KWBinForestVegetationPeriod;
    
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    description = "tmp variables")
    public Attribute.DoubleArray tmp;
    
    @Override
    public void run(){
        isFrostDefrostChange.setValue(0.0);
        isHotDay.setValue(0.0);
        isHotPeriod.setValue(0.0);
        isBeginningOfHotPeriod.setValue(0.0);
        isSummerDay.setValue(0.0);
        isTropicalNight.setValue(0.0);
        isFrostDay.setValue(0.0);
        isPermanentFrostPeriod.setValue(0.0);
        this.isBeginningOfPermanentFrostPeriod.setValue(0.0);
        isIceDay.setValue(0.0);
        isHeatDay.setValue(0.0);
        
        isDryPeriod.setValue(0);
        isBeginningOfDryPeriod.setValue(0);
        isPrecipHigher20mm.setValue(0.0);
        isPrecipHigher30mm.setValue(0.0);
        isPrecipHigher40mm.setValue(0.0);
        isPrecipHigher50mm.setValue(0.0);
        isThermalVegetationPeriodTrigger.setValue(0.0);
        isForestVegetationPeriodTrigger.setValue(0.0);
        isHeavyStorm.setValue(0.0);
        isProductiveWindday.setValue(0.0);
        
        if (tmp.getValue() == null){
            tmp.setValue(new double[2]);
        }
        
        //Frosttauwechsel .. muss zuerst ausgeführt werden
        if (isTempBelowZero.getValue() == 1.0 && tmin.getValue() > 0.0){
            isFrostDefrostChange.setValue(1.0); 
        }
        
        isTempBelowZero.setValue(0.0);
        
        //Standardwerte .. Frosttage, Tropische Nächte .. 
        if (tmin.getValue() < 0.0){
            isTempBelowZero.setValue(1.0);
        }
        
        if (tmax.getValue() > summerDayThreshold.getValue()){
            isSummerDay.setValue(1.0);
        }
                
        if (tmin.getValue() > tropicalNightThreshold.getValue()){
            isTropicalNight.setValue(1.0);
        }
        
        //Hitzeperioden und Hitzetage
        if (tmax.getValue() > hotDayThreshold.getValue()){
            isHotDay.setValue(1.0);
            successiveHotDays.setValue(successiveHotDays.getValue()+1);
            if (successiveHotDays.getValue()==5.0){
                isHotPeriod.setValue(5.0);
                isBeginningOfHotPeriod.setValue(1.0);
            }else if (successiveHotDays.getValue()>5.0){
                isHotPeriod.setValue(1.0);

            }
        }else{
            successiveHotDays.setValue(0.0);
        }
                
        if (tmin.getValue()>tmean.getValue()){
            System.out.println("Ups .. tmin (" + tmin + ") ist größer als tmean (" + tmean + ")");
        }
        
        //Frosttage und Frostperioden
        if (tmin.getValue() < frostDayThreshold.getValue()){
            isFrostDay.setValue(1.0);
        }
        
        if (tmin.getValue() < permanentFrostDayThreshold.getValue()){            
            successivePermanentFrostDay.setValue(successivePermanentFrostDay.getValue()+1);
            if (successivePermanentFrostDay.getValue()==3.0){
                isPermanentFrostPeriod.setValue(3.0);
                isBeginningOfPermanentFrostPeriod.setValue(1.0);
            }else if (successivePermanentFrostDay.getValue()>3.0){
                isPermanentFrostPeriod.setValue(1.0);
            }
        }else{
            successivePermanentFrostDay.setValue(0.0);
        }
                
        if (tmax.getValue() < iceDayThreshold.getValue()){
            isIceDay.setValue(1.0);
        }
                        
        //Heiz- und Kühlgradtage
        coolingDegreeDays.setValue(Math.max(tmean.getValue()-18.3,0.0));
        
        //make sure months are zero-based        
        if (time.get(Attribute.Calendar.MONTH) >= 8 || time.get(Attribute.Calendar.MONTH) < 5){
            if (tmean.getValue()<15){
                isHeatDay.setValue(1.0);
            }
        }
        
        //Vegetationsperioden
        if (tmean.getValue() > 5.0){
            successiveDaysWithTmeanAboveFiveDegree.setValue(successiveDaysWithTmeanAboveFiveDegree.getValue()+1.0);
        }else{
            successiveDaysWithTmeanAboveFiveDegree.setValue(0.0);
        }
        
        if (tmean.getValue() > 10.0){
            successiveDaysWithTmeanAboveTenDegree.setValue(successiveDaysWithTmeanAboveTenDegree.getValue()+1.0);
        }else{
            successiveDaysWithTmeanAboveTenDegree.setValue(0.0);
        }
                
        if (successiveDaysWithTmeanAboveFiveDegree.getValue() >= 6){
            isThermalVegetationPeriodTrigger.setValue(1.0);
        }
        
        if (successiveDaysWithTmeanAboveTenDegree.getValue() >= 6){
            isForestVegetationPeriodTrigger.setValue(1.0);
        }
        
        //Niederschlagskenntage
        if (precip != null) {
            //Trockenperioden
            if (precip.getValue() == 0) {
                successiveDaysWithoutRain.setValue(successiveDaysWithoutRain.getValue() + 1);

                if (successiveDaysWithoutRain.getValue() == 11.0) {
                    isDryPeriod.setValue(11.0);
                    isBeginningOfDryPeriod.setValue(1.0);
                } else if (successiveDaysWithoutRain.getValue() > 11.0) {
                    isDryPeriod.setValue(1.0);

                }
            } else {
                successiveDaysWithoutRain.setValue(0.0);
            }
            //Starkniederschläge
            double precip_mm = precip.getValue() / area.getValue();
            if (precip_mm >= 20) {
                isPrecipHigher20mm.setValue(1.0);
            }
            if (precip_mm >= 30) {
                isPrecipHigher30mm.setValue(1.0);
            }
            if (precip_mm >= 40) {
                isPrecipHigher40mm.setValue(1.0);
            }
            if (precip_mm >= 50) {
                isPrecipHigher50mm.setValue(1.0);
            }
        }
        
        //Wind
        if (wind != null){
            if (wind.getValue() >= 28){
                isHeavyStorm.setValue(1.0);
            }
            if (wind.getValue() >= 8 && wind.getValue() <= 18){
                isProductiveWindday.setValue(1.0);
            }
        }
        
        //Klimatische Wasserbilanz
        if (potET != null && precip != null){
            double KWB_mm = (precip.getValue() - potET.getValue());
            
            KWB.setValue(KWB_mm);
            
            //PROBLEM!!!
            tmp.getValue()[0] += KWB_mm;
            tmp.getValue()[1] += KWB_mm;
            if (isThermalVegetationPeriodTrigger.getValue()==1){
                KWBinThermalVegetationPeriod.setValue(KWBinThermalVegetationPeriod.getValue()+tmp.getValue()[0]);
                tmp.getValue()[0] = 0;
            }
            if (isForestVegetationPeriodTrigger.getValue()==1){
                KWBinForestVegetationPeriod.setValue(KWBinForestVegetationPeriod.getValue()+tmp.getValue()[1]);
                tmp.getValue()[1] = 0;
            }            
        }
        //reset counters .. 
        if (time.get(Attribute.Calendar.DAY_OF_YEAR)==1){
            KWBinThermalVegetationPeriod.setValue(0);
            KWBinForestVegetationPeriod.setValue(0);
            tmp.getValue()[0] = 0;
            tmp.getValue()[1] = 0;
        }
    }
}