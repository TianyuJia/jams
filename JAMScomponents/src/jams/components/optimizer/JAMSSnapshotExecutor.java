/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jams.components.optimizer;

import jams.data.JAMSData;
import jams.data.JAMSDouble;
import jams.data.JAMSEntity;
import jams.data.JAMSString;
import jams.model.Context;
import jams.model.JAMSComponent;
import jams.model.JAMSFullModelState;
import jams.model.JAMSVarDescription;
import jams.model.Model;
import java.io.File;
import jams.JAMS;
import jams.data.Attribute;
import jams.data.Attribute.Entity;
import jams.data.JAMSBoolean;
import jams.data.JAMSObject;
import jams.model.Component;
import java.io.IOException;

/**
 *
 * @author Christian Fischer
 */
public class JAMSSnapshotExecutor extends JAMSComponent{
    //this enumeration is not nice, but is there a better method?
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble in1;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString inName1;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble in2;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString inName2;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble in3;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString inName3;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble in4;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString inName4;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble in5;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString inName5;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble in6;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString inName6;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble in7;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString inName7;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble in8;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString inName8;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble in9;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString inName9;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble in10;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString inName10;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.WRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble out1;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString outName1;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.WRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble out2;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString outName2;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.WRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble out3;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString outName3;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.WRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble out4;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString outName4;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.WRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSDouble out5;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public JAMSString outName5;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.WRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public Attribute.Object outObj1;
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "parameter input"
            )
            public Attribute.String outObjName1;
    
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "if you dont want to execute the jams model completly in every iteration, you can specify a JAMS - Snapshot which is loaded before execution"
            )
            public JAMSString snapshotFile;
    
    private final int inValueCount = 10;
    private final int outValueCount = 5;
    private final int outObjValueCount = 1;

    public double searchForAttributeInModel(Context c, String key) throws JAMSEntity.NoSuchAttributeException{
        JAMSData d = (c.getAttributeMap().get(key));
        if (d!=null){
            if (d instanceof JAMSDouble)
                return ((JAMSDouble)d).getValue();
            else
                throw new JAMSEntity.NoSuchAttributeException(JAMS.i18n("Attribute_") + key + JAMS.i18n("_(float)_not_found!"));
        }else{
            for (Component comp : c.getComponents()){
                if (comp instanceof Context){
                    try{
                        return searchForAttributeInModel((Context)comp,key);
                    }catch(JAMSEntity.NoSuchAttributeException nsae){

                    }
                }
            }
        }
        throw new JAMSEntity.NoSuchAttributeException(JAMS.i18n("Attribute_") + key + JAMS.i18n("_(float)_not_found!"));
    }

    public Object searchForObjectInModel(Context c, String key) throws JAMSEntity.NoSuchAttributeException{
        JAMSData d = (c.getAttributeMap().get(key));
        if (d!=null){
            if (d instanceof JAMSObject)
                return ((JAMSObject)d).getValue();
            else
                throw new JAMSEntity.NoSuchAttributeException(JAMS.i18n("Attribute_") + key + JAMS.i18n("_(float)_not_found!"));
        }else{
            for (Component comp : c.getComponents()){
                if (comp instanceof Context){
                    try{
                        return searchForAttributeInModel((Context)comp,key);
                    }catch(JAMSEntity.NoSuchAttributeException nsae){

                    }
                }
            }
        }
        throw new JAMSEntity.NoSuchAttributeException(JAMS.i18n("Attribute_") + key + JAMS.i18n("_(float)_not_found!"));
    }

    @Override
    public void run(){
        double inValues[] = new double[inValueCount];
        String inNames[]  = new String[inValueCount];
        
        double outValues[] = new double[outValueCount];
        String outNames[]  = new String[outValueCount];

        Object outObjValues[] = new Object[outObjValueCount];
        String outObjNames[]  = new String[outObjValueCount];
        
        if (in1 != null)    inValues[0] = in1.getValue();
        if (in2 != null)    inValues[1] = in2.getValue();
        if (in3 != null)    inValues[2] = in3.getValue();
        if (in4 != null)    inValues[3] = in4.getValue();
        if (in5 != null)    inValues[4] = in5.getValue();
        if (in6 != null)    inValues[5] = in6.getValue();
        if (in7 != null)    inValues[6] = in7.getValue();
        if (in8 != null)    inValues[7] = in8.getValue();
        if (in9 != null)    inValues[8] = in9.getValue();
        if (in10!= null)    inValues[9] = in10.getValue();
        
        if (inName1 != null)    inNames[0] = inName1.getValue();
        if (inName2 != null)    inNames[1] = inName2.getValue();
        if (inName3 != null)    inNames[2] = inName3.getValue();
        if (inName4 != null)    inNames[3] = inName4.getValue();
        if (inName5 != null)    inNames[4] = inName5.getValue();
        if (inName6 != null)    inNames[5] = inName6.getValue();
        if (inName7 != null)    inNames[6] = inName7.getValue();
        if (inName8 != null)    inNames[7] = inName8.getValue();
        if (inName9 != null)    inNames[8] = inName9.getValue();
        if (inName10!= null)    inNames[9] = inName10.getValue();
                        
        if (outName1 != null)    outNames[0] = outName1.getValue();
        if (outName2 != null)    outNames[1] = outName2.getValue();
        if (outName3 != null)    outNames[2] = outName3.getValue();
        if (outName4 != null)    outNames[3] = outName4.getValue();
        if (outName5 != null)    outNames[4] = outName5.getValue();

        if (outObjName1 != null)    outObjNames[0] = outObjName1.getValue();
        
        JAMSFullModelState state = null;
        try{
            state = new JAMSFullModelState(new File(this.getModel().getWorkspacePath() + "/" + this.snapshotFile.getValue()));
        }catch(ClassNotFoundException e){
            this.getModel().getRuntime().sendHalt("class not found, so that model state could not loaded:" + e.toString());
        }catch(IOException ioe){
            this.getModel().getRuntime().sendHalt("could not read file, so that model state could not loaded:" + ioe.toString());
        }
        Model model = state.getModel();                            
        for (int i=0;i<inValueCount;i++){
            String key = inNames[i];
            double value = inValues[i];
            if (key != null){
                if (model.getRuntime().getDataHandles().get(key) instanceof JAMSDouble){
                    ((JAMSDouble)model.getRuntime().getDataHandles().get(key) ).setValue(value);
                }

                if (model.getRuntime().getDataHandles().get(key) instanceof JAMSBoolean){
                    if (value==1.0){
                        ((JAMSBoolean)model.getRuntime().getDataHandles().get(key) ).setValue(true);
                    }else if(value == 0.0){
                        ((JAMSBoolean)model.getRuntime().getDataHandles().get(key) ).setValue(false);
                    }else{
                        this.getModel().getRuntime().sendHalt("invalid value for boolean:" + value);
                    }
                }
            }
        }
        try{
            model.getRuntime().resume(state.getSmallModelState());                
        }catch(Exception e){
            this.getModel().getRuntime().sendHalt(e.toString());
            e.printStackTrace();
        }
        
        for (int i=0;i<outValueCount;i++){
            String key = outNames[i];
            if (key != null){
                try{
                    outValues[i] = searchForAttributeInModel(model,key);
                }catch(Entity.NoSuchAttributeException nsae){
                    System.out.println(nsae.toString());
                }
                System.out.println("key:" + key + " ----> " + outValues[i]);
            }
        }

        for (int i=0;i<outObjValueCount;i++){
            String key = outObjNames[i];
            if (key != null){
                try{
                    outObjValues[i] = searchForObjectInModel(model,key);
                }catch(Entity.NoSuchAttributeException nsae){
                    System.out.println(nsae.toString());
                }
                System.out.println("key:" + key + " ----> " + outValues[i]);
            }
        }

        model = null;
        // collect some garbage ;)
        Runtime.getRuntime().gc();    
        
        if (outName1 != null)    out1.setValue(outValues[0]);
        if (outName2 != null)    out2.setValue(outValues[1]);
        if (outName3 != null)    out3.setValue(outValues[2]);
        if (outName4 != null)    out4.setValue(outValues[3]);
        if (outName5 != null)    out5.setValue(outValues[4]);
        
        if (outObjName1 != null)    outObj1.setValue(outObjValues[0]);
    }
    
}