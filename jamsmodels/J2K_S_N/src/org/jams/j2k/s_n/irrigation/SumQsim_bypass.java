/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jams.j2k.s_n.irrigation;
import jams.data.*;
import jams.model.*;

/**
 *
 * @author c6gohe2
 */
@JAMSComponentDescription(
        title="SumQsim_Bypass",
        author="c6gohe2",
        description="Calculation of the plant groth nitrogen factor after SWAT"
        )
        public class SumQsim_bypass extends JAMSComponent {


 @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READ,
            description = "HRU crop class"
            )
                public Attribute.Double Storage;


 @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READWRITE,
            description = "HRU crop class"
            )
            public Attribute.Double catchmentSimRunoff_qm;
 
 @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READWRITE,
            description = "HRU crop class"
            )
            public Attribute.Double catchmentSimRunoffN;

 @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READWRITE,
            description = "HRU crop class"
            )
            public Attribute.Double Irrigation_Bypass_water;

 @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READWRITE,
            description = "HRU crop class"
            )
            public Attribute.Double irrigationN_conc;
 
 @JAMSVarDescription(
            access = JAMSVarDescription.AccessType.READWRITE,
            description = "HRU crop class"
            )
            public Attribute.Double bypass_N;
 

    public void run() {
        
        catchmentSimRunoff_qm.setValue((Storage.getValue() / 86400000) + catchmentSimRunoff_qm.getValue() + Irrigation_Bypass_water.getValue());
        catchmentSimRunoffN.setValue((((Storage.getValue() / 86400000) * irrigationN_conc.getValue()) + bypass_N.getValue()) + catchmentSimRunoffN.getValue());
    }
}
