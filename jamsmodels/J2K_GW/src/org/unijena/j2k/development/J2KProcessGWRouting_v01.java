/*
 * J2KProcessRouting.java
 * Created on 28. November 2005, 09:21
 *
 * This file is part of JAMS
 * Copyright (C) 2005 FSU Jena, c0krpe
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
package org.unijena.j2k.development;

import jams.data.*;
import jams.model.*;

/**
 *
 * @author Peter Krause
 */
@JAMSComponentDescription(title = "J2KProcessRouting",
author = "Peter Krause",
description = "Passes the output of the entities as input to the respective reach or unit")
public class J2KProcessGWRouting_v01 extends JAMSComponent {

    /*
     *  Component variables
     */
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    update = JAMSVarDescription.UpdateType.RUN,
    description = "HRU statevar GW inflow")
    public JAMSDouble inGW;
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    update = JAMSVarDescription.UpdateType.RUN,
    description = "HRU statevar GW outflow")
    public JAMSDouble outGW;                                                   //not used
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    update = JAMSVarDescription.UpdateType.RUN,
    description = "sum attribute")
    public JAMSEntity[] fP;
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    update = JAMSVarDescription.UpdateType.RUN,
    description = "attribute area")
    public JAMSDouble area;
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    update = JAMSVarDescription.UpdateType.RUN,
    description = "Heigth of the Aquifer Base in m + NN")
    public JAMSDouble baseHeigth;
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    update = JAMSVarDescription.UpdateType.RUN,
    description = "estimated Porosity")
    public JAMSDouble Peff;
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    update = JAMSVarDescription.UpdateType.RUN,
    description = "actual GW storage")
    public JAMSDouble actGW;
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    update = JAMSVarDescription.UpdateType.RUN,
    description = "Groundwater Level")
    public JAMSDouble gwTable;
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    update = JAMSVarDescription.UpdateType.RUN,
    description = "Groundwater Level")
    public JAMSDouble calcFactor;
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    update = JAMSVarDescription.UpdateType.RUN,
    description = "Reduction of outflows, 0 = off, 1 = average, 2 = exponential")
    public JAMSInteger outflowReduction;
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READWRITE,
    update = JAMSVarDescription.UpdateType.RUN,
    description = "Number of sender-HRUs")
    public JAMSDouble sender;
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ,
    update = JAMSVarDescription.UpdateType.RUN,
    description = "The current hru entity")
    public JAMSEntityCollection entities;
    /*
     *  Component run stages
     */
    double[] gradientNew;
    double gwVolume, run_area, run_Peff, run_gwTableUpper, run_gwTableLower, run_gwDepthUpper, run_gwDepthLower, run_baseHeigth,
            pot_gwTable, sumGWin, sumGWin_new, run_GWact, run_outGW, old_GWact;
    int oR;
    //JAMSEntity[] fP;

    public void init() throws JAMSEntity.NoSuchAttributeException {
    }

    public void run() throws JAMSEntity.NoSuchAttributeException {

        Attribute.Entity entity = entities.getCurrent();

        fP = (JAMSEntity[]) entity.getObject("from_poly");

        //aktuelle HRU
        run_GWact = this.actGW.getValue();
        run_area = this.area.getValue();
        run_Peff = Peff.getValue();
        if (run_Peff == 0){
//            getModel().getRuntime().println("Current entity ID: " + (int) entity.getDouble("ID") + "Peff = 0");
        }
        run_baseHeigth = baseHeigth.getValue();

        //Oberlieger:


        gradientNew = new double[fP.length];
        oR = this.outflowReduction.getValue();

        if (fP.length != 0) {   //is there any sender-HRU? Wenn kein Oberlieger, dann gibt es auch nichts zu tun.
            sumGWin_new = 0;

            // Calculation of the accumulated input...  Hier wird zusammengefasst, was von oben zuflie�en k�nnte.
            for (int i = 0; i < fP.length; i++) {
                sumGWin_new = fP[i].getDouble("pot_outGW") + sumGWin_new;
            }

            //sumGWin_new ist nun der Zwischenspeicher aus dem verteilt wird

            // double new_gwVolume = (run_GWact + sumGWin_new) / 1000;
            // der aktuelle Speicherinhalt wird um den zuflie�enden Betrag erh�ht. Damit erh�ht sich automatisch die
            // Grundwasserspiegellage. Es besteht nun die M�glichkeit zu entscheiden, ob eine Gradientenreduktion
            // auf Basis dieses vr�nderten Wasserstandes durchgef�hrt werden soll oder nicht.
            old_GWact = run_GWact;
            run_GWact = (run_GWact + sumGWin_new);

            //Calculation of the potential GW-Levels
            boolean flag = false;
            
            updateGWTable(flag);  // wie w�rden die GW-St�nde sein, wenn tats�chlich inGW flie�en w�rde.
            
            if (oR != 0) {
                gradientNew = calcGradientReduction();  // Reduktion: JA
            } else {
                for (int i = 0; i < fP.length; i++) {   // Reduktion: NEIN
                    //es wird der Gradient von Zeitschritt t-1 unver�ndert verwendet
                    gradientNew[i] = fP[i].getDouble("gwTable") - gwTable.getValue();   //fP("gwTable") = old_gwTable!
                    if (gradientNew[i] < 0) {
                        //getModel().getRuntime().println("Negativer Gradient!");
                    }
                }
            }
            
            run_GWact = recalcDarcyGWOut(gradientNew);  // Neuberechnung n�tig, da eventuell mehr als eine HRU von oben zuflie�t!
            
            //gwVolume = (run_GWact + sumGWin_new)/1000;
            flag = true;                                //neue Grundwasserspiegellage auf Basis der neu berechneten Zufl�sse

            updateGWTable(flag);
/*
            double newActGW;
            double newGenGW;
            double newOutGW;
            double newGWTable;
            for (int i = 0; i < fP.length; i++) {
                newOutGW = 0;
                newActGW = fP[i].getDouble("pot_actGW");
                newGenGW = fP[i].getDouble("pot_genGW");
                newGWTable = fP[i].getDouble("pot_gwTable");
                fP[i].setDouble("actGW", newActGW);
                fP[i].setDouble("outGW", newOutGW);
                fP[i].setDouble("genGW", newGenGW);
                fP[i].setDouble("gwTable", newGWTable);
            }*/
            actGW.setValue(run_GWact);
            inGW.setValue(sumGWin_new);
        }
    }

    private boolean updateGWTable(boolean flag) throws JAMSEntity.NoSuchAttributeException {

        for (int i = 0; i < fP.length; i++) {
            if (flag) {
                gwVolume = fP[i].getDouble("actGW") / 1000;
                run_gwDepthUpper = gwVolume / fP[i].getDouble("area") / fP[i].getDouble("Peff");
                run_gwTableUpper = run_gwDepthUpper + fP[i].getDouble("baseHeigth");
                fP[i].setDouble("gwTable", run_gwTableUpper);
                if (run_gwTableUpper < 0){
                    //getModel().getRuntime().println("Negative Wasserspiegellage.");
                }
            }
        }

        gwVolume = run_GWact / 1000;

        run_gwDepthLower = gwVolume / run_area / run_Peff;
        run_gwTableLower = run_gwDepthLower + run_baseHeigth;

        if (run_gwTableLower < 0){
            //getModel().getRuntime().println("Negative Wasserspiegellage.");
        }

        if (flag) {
            gwTable.setValue(run_gwTableLower);
        } else {
            pot_gwTable = run_gwTableLower;
        }

        return true;
    }

    private double[] calcGradientReduction() throws JAMSEntity.NoSuchAttributeException {
        double gradientPre;
        double gradientPost;

        for (int i = 0; i < fP.length; i++) {
            gradientPre = fP[i].getDouble("gwTable") - gwTable.getValue();
            gradientPost = fP[i].getDouble("pot_gwTable") - pot_gwTable;

            if (gradientPost < 0) {
                //getModel().getRuntime().println("Negative PostGradient.");
                gradientPost = 0;
            }

            if (oR == 1) {   //Mittelwert
                gradientNew[i] = (gradientPre + gradientPost) / 2;
            } else {          //Integral der Exponentialfunktion y = a*e^(cx)
                double a = gradientPre;
                double m = (gradientPost - gradientPre) / 1;  // dx/dy, dy ist 1 da der Zeitschtitt ein Tag ist
                if (m > 0) {            // Ansteigender Grundwassserstand
                    m = m * (-1);
                    double c = m / a;
                    gradientNew[i] = a + (a - (a / c * Math.exp(c) - a / c));
                                if (gradientNew[i] < 0) {
                //getModel().getRuntime().println("Negative Gradient.");
                gradientNew[i] = 0;
            }
                } else if (m < 0){      // Fallender Grundwasserstand
                    double c = m / a;
                    gradientNew[i] = a / c * Math.exp(c) - a / c;
                if (gradientNew[i] < 0) {
                //getModel().getRuntime().println("Negative Gradient.");
                gradientNew[i] = 0;
            }
                } else {                // Grundwasserstand vorher und nachher ist gleich
                    gradientNew[i] = 0;
                }
            }
        }
        return gradientNew;
    }

    private double recalcDarcyGWOut(double[] gradientNew) throws JAMSEntity.NoSuchAttributeException {
        double GWout_new;
        double GWact_new;

        //this.run_gwTableUpper = 0;
        sumGWin_new = 0;    //Neuberechnung des zuflie�enden Wassers!
        if (this.run_gwTableUpper >= this.run_gwTableLower) {
            sumGWin_new = 0;    //Neuberechnung des zuflie�enden Wassers!
            double GWout_sum = 0;

            for (int i = 0; i < fP.length; i++) {
                if (fP[i].getDouble("hgeoID") != 1){ //alle Fl�chen, die au�erhalb des Squifers liegen geben ihr RG2 als GW ab
                    GWout_new = fP[i].getDouble("outRG2");
                    
                }else{
                GWout_new = (fP[i].getDouble("calcFactor") * gradientNew[i]) * 1000;
                if (GWout_new < 0) {
                    getModel().getRuntime().println("MIST.");
                    GWout_new = 0;
                }

                //GWact_new = fP[i].getDouble("actGW") - GWout_new;

                GWout_sum = fP[i].getDouble("preOutGW") + GWout_new;
                }

                if (GWout_new > fP[i].getDouble("actGW")){
                    GWout_new = fP[i].getDouble("actGW");
                }
                GWact_new = fP[i].getDouble("actGW") - GWout_new;

                fP[i].setDouble("actGW", GWact_new);
                fP[i].setDouble("outGW", GWout_sum);
                fP[i].setDouble("genGW", GWout_new);
                
                sumGWin_new = sumGWin_new + GWout_sum;
           }
        } else {
            //getModel().getRuntime().println("Groundwater-Table in Receiver-HRU is higher.");
        }

        GWact_new = old_GWact + sumGWin_new;

        return GWact_new;
    }

    public void cleanup() {
    }
}
