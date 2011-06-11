/*
 * Optimizer.java
 *
 * Created on 8. Februar 2008, 10:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jams.components.optimizer;

import java.util.Comparator;
import java.util.StringTokenizer;
import jams.data.*;
import jams.model.JAMSVarDescription;
import jams.JAMS;
import jams.components.optimizer.Optimizer.Sample;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 * @author Christian Fischer
 */
public abstract class MOOptimizer extends Optimizer {                   
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.WRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "best paramter values found so far"
            )
            public Attribute.EntityCollection bestParameterSets;
                   
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.INIT,
            description = "objective function name"
            )
            public Attribute.String effMethodName;
            
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READWRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "the prediction series"
            )
            public Attribute.Double[] effValue;
        
    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READWRITE,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "optimization mode, 1 - minimization, 2 - maximization, 3 - max |f(x)|, 4 - min |f(x)|",
            defaultValue = "1"
            )
            public Attribute.String mode;

    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "known optimal value",
            defaultValue = "0;0"
            )
            public Attribute.Double[] target;

    @JAMSVarDescription(
    access = JAMSVarDescription.AccessType.READ,
            update = JAMSVarDescription.UpdateType.RUN,
            description = "stopping criterion if target is met",
            defaultValue = "-1"
            )
            public Attribute.Double epsilonToTarget;
    /*************************
     * first some very useful nested classes     
     *************************/ 
    public static abstract class AbstractMOFunction {
        public abstract double[] f(double x[]);
    }
        
    //class for representing samples
    public class SampleMO extends Sample{
        public SampleMO(double[] x, double[] fx) {
            super(x,fx);
        }
        double [] getFX(){
            return this.fx;
        }
        @Override
        public SampleMO clone(){
            Sample x = super.clone();
            return new SampleMO(x.getParameter(),x.fx);            
        }
    }
/*    SampleMO getFromSampleList(int i){
        return (SampleMO)this.sampleList.get(i);
    }*/
    //compare samples
    static public class SampleMOComperator implements Comparator {

        private int order = 1;

        public SampleMOComperator(boolean decreasing_order) {
            order = decreasing_order ? -1 : 1;
        }

        public int compare(Object d1, Object d2) {
            int m = ((Sample)d1).fx.length;
            if (((Sample)d1).fx.length != ((Sample)d2).fx.length){
                return 0;
            }
            int ord = 0;
            for (int i=0;i<m;i++){
                int nextOrd;
                if (((Sample) d1).fx[i] < ((Sample) d2).fx[i]) {
                    nextOrd = -1 * order;
                } else if ( Math.abs(((Sample) d1).fx[i] - ((Sample) d2).fx[i] )<0.000000001) {
                    nextOrd = 0 * order;
                } else {
                    nextOrd = 1 * order;
                }
                if (ord == 0)
                    ord = nextOrd;
                else{
                    if (ord != nextOrd && nextOrd != 0)
                        return 0;                    
                }
            }
            return ord;
        }
    }            
    protected String[] efficiencyNames;    
    protected Set<SampleMO> bestSamples;        
    //number of efficiencies
    public int m;       
    protected AbstractMOFunction GoalFunction = null;
    protected int iMode[];
    public MOOptimizer() {
    }
           
    @Override
    public void init() {   
        super.init();        
        if (!enable.getValue())
            return;            

        if (this.GoalFunction == null){
            if (this.effMethodName == null)
                stop(JAMS.i18n("effMethod_not_specified"));
            if (this.effValue == null)
                stop(JAMS.i18n("effValue_not_specified"));
            if (this.mode == null)
                stop(JAMS.i18n("mode_not_specified"));
               
            m = effValue.length;
        }else{
            StringTokenizer tok = new StringTokenizer(this.mode.getValue(),";");
            m = tok.countTokens();
        }
        StringTokenizer tok = new StringTokenizer(this.mode.getValue(),";");
        if (tok.countTokens() != m)
            stop(JAMS.i18n("efficiency_count_does_not_match_mode_count"));
        
        int j=0;
        iMode = new int[m];
        while(tok.hasMoreTokens()){
            String sgnMode = tok.nextToken();
            iMode[j++] = Integer.parseInt(sgnMode);
        }
                
        StringTokenizer effTok = new StringTokenizer(this.effMethodName.getValue(),";");
        efficiencyNames = new String[m];
        for (int i=0;i<m;i++){
            try{
                efficiencyNames[i] = effTok.nextToken();            
            }catch(NoSuchElementException e){
                stop(JAMS.i18n("efficiency_count_does_not_effMethodName_mode_count"));
            }
        }           
        bestSamples = new HashSet<SampleMO>();
    }
                
    public SampleMO getSample(double[]x) throws SampleLimitException, ObjectiveAchievedException{
        if (sampleList.size()>=this.maxn.getValue())
            throw new SampleLimitException("maximum sample count reached");
        SampleMO s = new SampleMO(x,funct(x));
        this.bestSamples.add(s);
        this.bestSamples = this.getParetoOptimalSet(this.bestSamples);

        return s;
    }
                  
    public Set<SampleMO> getParetoOptimalSet(Set<SampleMO> set){        
        SampleMOComperator comparer = new SampleMOComperator(true);
        HashSet<SampleMO> result = new HashSet<SampleMO>();
        Iterator<SampleMO> iter = set.iterator();
        while(iter.hasNext()){
            SampleMO candidate = iter.next();
            boolean isDominated = false;
            Iterator<SampleMO> iter2 = set.iterator();            
            while(iter2.hasNext()){
                SampleMO rivale = iter2.next();
                if (candidate == rivale)
                    continue;
                if (comparer.compare(candidate,rivale)<0){
                    isDominated = true;
                    break;
                }
            }
            if (!isDominated)
                result.add(candidate);
        }
        return result;
    }
    
    public double[] funct(double x[]) throws ObjectiveAchievedException {
        
        double value[] = new double[m];        
        if (GoalFunction == null) {          
            this.setParameters(x);         
            singleRun();       
            for (int i=0;i<m;i++)
                value[i] = effValue[i].getValue();                        
        } else {            
            value = GoalFunction.f(x);
        }
        if (this.sampleWriter!=null){
            try{
                for (int i=0;i<x.length;i++)
                    sampleWriter.write(x[i]+"\t");

                for (int i=0;i<value.length;i++)
                    sampleWriter.write(value[i]+"\t");

                sampleWriter.write("\n");
                sampleWriter.flush();
            }catch(Exception e){

            }
        }
        this.iterationCounter.setValue(this.iterationCounter.getValue()+1);
        for (int i=0;i<m;i++)
            value[i] = this.transformByMode(value[i], iMode[i]);
                        
        Iterator<SampleMO> iter = this.bestSamples.iterator();        
        for (int i=0;iter.hasNext();i++){
            SampleMO s = iter.next();
            
            ArrayList<Attribute.Entity> list = new ArrayList<Attribute.Entity>();
            Attribute.Entity entity = JAMSDataFactory.createEntity();
            entity.setId(i);
            for (int j=0;j<n;j++){
                entity.setDouble("x_"+(j+1), s.getParameter()[j]);
            }
            for (int j=0;j<m;j++){
                entity.setDouble("y_"+(j+1), s.getFX()[j]);
            }
            list.add(entity);

            this.bestParameterSets.setEntities(list);
        }
        
        if (target!=null && this.target.length == effValue.length){
            boolean objectiveAchieved = true;
            for (int i=0;i<this.effValue.length;i++){
                if (Math.abs(effValue[i].getValue() - target[i].getValue())>=this.epsilonToTarget.getValue())
                    objectiveAchieved = false;
            }
            if (objectiveAchieved) {
                double targets[] = new double[target.length];
                for (int i = 0; i < targets.length; i++) {
                    targets[i] = target[i].getValue();
                }
                throw new ObjectiveAchievedException(value, targets);
            }
        }
        return value;
    }
}
