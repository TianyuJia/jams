/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jams.j2k.s_n;
import jams.data.*;
import jams.model.*;
/**
 *
 * @author c6gohe2
 */
@JAMSComponentDescription(
        title="J2KPlantGrowthNitrogenStress",
        author="Manfred Fink",
        description="Calculation of the plant groth nitrogen factor after SWAT"
        )
        public class ReservoirManagement extends JAMSComponent {


    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ,
            description = "HRU crop class"
            )
            public Attribute.Double storageInput;

    @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READWRITE,
            description = "HRU crop class"
            )
            public Attribute.Double storage;


//Berechnung
    public void run (){
    
        storage.setValue(storageInput.getValue()*1000);

    }



}
