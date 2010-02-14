/*
 * BranchAndBound.java
 *
 * Created on 8. Februar 2008, 10:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jams.components.optimizer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;
import jams.model.JAMSComponentDescription;
import jams.tools.FileTools;

@JAMSComponentDescription(
        title="Branch and Bound Optimizer",
        author="Christian Fischer",
        description="Performs a branch and bound optimization. Advantage: It can be shown, that this method will find the global optimum. Disadvantage: This method usally requires many function evaluations. So it should only be used, if model execution is very fast"
        )
public class BranchAndBound extends Optimizer{
                    
    @SuppressWarnings("unchecked")
    public BranchAndBound() {
    }
    
    public void init(){        
        super.init();
        if (!enable.getValue())
            return; 
        
        if (x0 != null){
            this.getModel().getRuntime().sendInfoMsg("start values not supported by branch and bound optimizer!\nstart value is ignored");
        }
/*             
        if (SampleDumpFileName != null){
            try {            
                String fileName = SampleDumpFileName.getValue();            
                writer = new SerializableBufferedWriter(new FileWriter(FileTools.createAbsoluteFileName(this.dirName.getValue(),fileName)));
            } catch (IOException ioe) {
                JAMS.handle(ioe);
            }
        }*/
    }
           
    public class HyperCube{
        protected Sample a;
        protected Sample b;
        protected Sample midPoint;
        protected double L;
        
        protected ArrayList<Sample> InCubeSamples;
        
        public double goodOneFactor;
        protected HyperCube parent;
        
        protected double highestLowBound;
        
        HyperCube(Sample a,Sample b,Sample midPoint,HyperCube parent){
            this.a = a;
            this.b = b;
            this.midPoint = midPoint;
            this.parent = parent;            
            this.InCubeSamples = new ArrayList<Sample>();
            InCubeSamples.add(a);
            InCubeSamples.add(b);
            InCubeSamples.add(midPoint);
            this.L = ApproxL(a,b,this);            
            if (parent!=null)
                highestLowBound = Math.max(Math.max(-1000000000000.0,
                                            Math.max(a.fx,b.fx)-VectorNorm2(VectorMul(VectorSub(b.x,a.x),L))),
                                            midPoint.fx-VectorNorm2(VectorMul(VectorSub(b.x,a.x),L))/2.0);
                                    
            double minimum = Math.min(Math.min(a.fx, b.fx), midPoint.fx);
            if (highestLowBound > minimum)   highestLowBound = minimum;
                        
            goodOneFactor = ( (midPoint.fx - a.fx) + (midPoint.fx - b.fx));                        
        }
        
        public void AddCubeSample(Sample x){           
            this.InCubeSamples.add(x);
        }
        
        double CalculateLForTarget(double target){
            //look which L value can realize highestLowBound                        
            double L_theo1 = (Math.max(a.fx,b.fx)-target)/VectorNorm(VectorSub(b.x,a.x));
            double L_theo2 = 2.0*(midPoint.fx-target)/VectorNorm(VectorSub(b.x,a.x));
                                    
            return Math.min(L_theo1, L_theo2);
        }
                        
        double bound(){
            return this.highestLowBound;
        }
                        
        public String compactDescriptionString(){
            String s = "";
            s += a.x[0] + "\t";
            s += a.x[1] + "\t";
            s += highestLowBound + "\n";
            s += b.x[0] + "\t";
            s += a.x[1] + "\t";
            s += highestLowBound + "\n";
            s += b.x[0] + "\t";
            s += b.x[1] + "\t";
            s += highestLowBound + "\n";
            s += a.x[0] + "\t";;
            s += b.x[1] + "\t";;
            s += highestLowBound + "\n";
            
            return s;
        }
        
        public String toString(){
            return "a:" + a.toString() + "\nb:" + b.toString() + "\nmidPoint:" + midPoint.toString() + "\nbound:" + highestLowBound;
        }
    }
    
    double[] VectorAdd(double[] a,double[] b){
        double sum[] = new double[a.length];
        for (int i=0;i<a.length;i++){
            sum[i] = a[i]+b[i];
        }
        return sum;
    }
    
    double[] VectorAdd(double[] a,double b){
        double sum[] = new double[a.length];
        for (int i=0;i<a.length;i++){
            sum[i] = a[i]+b;
        }
        return sum;
    }
    
    double[] VectorSub(double[] a,double[] b){
        double sum[] = new double[a.length];
        for (int i=0;i<a.length;i++){
            sum[i] = a[i]-b[i];
        }
        return sum;
    }
    
    double[] VectorMul(double[] a, double mul){
        double result[] = new double[a.length];
        for (int i=0;i<a.length;i++)
            result[i] = a[i]*mul;
        return result;
    }
    
    double[] VectorMul(double[] a, double[] mul){
        double result[] = new double[a.length];
        for (int i=0;i<a.length;i++)
            result[i] = a[i]*mul[i];
        return result;
    }
    
    boolean VectorLessEq(double[] x, double[] y){
        for (int i=0;i<x.length;i++){
            if (x[i]>y[i]-0.000001)
                return false;
        }
        return true;
    }
    
    double VectorNorm(double[] v){
        double sum = 0;
        for (int i=0;i<v.length;i++)
            sum += Math.abs(v[i]);//*v[i];
        return sum;//Math.sqrt(sum);
    }
    
    double VectorNorm2(double[] v){
        double sum = 0;
        for (int i=0;i<v.length;i++)
            sum += (v[i]*v[i]);//*v[i];
        return Math.sqrt(sum);//Math.sqrt(sum);
    }
    
    double VectorMaxNorm(double[] v){
        double sum = 0;
        for (int i=0;i<v.length;i++)
            sum = Math.max(Math.abs(v[i]),sum);//*v[i];
        return sum;//Math.sqrt(sum);
    }
    
    double VectorMin(double[] v){
        double sum = 0;
        for (int i=0;i<v.length;i++)
            sum = Math.min(v[i],sum);//*v[i];
        return sum;//Math.sqrt(sum);
    }
    
    int getMin(Vector<Sample> Q){
        double min = Double.MAX_VALUE;
        int index = 0;
        for (int i=0;i<Q.size();i++){
            if (Q.get(i).fx < min){
                min = Q.get(i).fx;
                index = i;
            }
        }
        return index;
    }
    double test=0;
    @SuppressWarnings("unchecked")
    public double ApproxL(Sample a,Sample b,HyperCube myCube){
        super.lowBound = a.x;
        super.upBound  = b.x;
                
        ArrayList<Sample> list = myCube.InCubeSamples;        
        if (myCube.parent != null){
            for (int i=0;i<myCube.parent.InCubeSamples.size();i++){
                Sample x = myCube.parent.InCubeSamples.get(i);
                if (VectorLessEq(a.x,x.x) && VectorLessEq(x.x,b.x) && !list.contains(x)){
                    boolean contains = false;
                    for (int j=0;j<list.size();j++){
                        if (VectorNorm(VectorSub(list.get(j).x,x.x))<0.0001){
                            contains = true;
                            break;
                        }
                    }
                    if (!contains)
                        myCube.AddCubeSample(x);
                }
            }
        }
        while(list.size() < 3*n+1){
            Sample rnd_point = getSample(RandomSampler());            
            if (rnd_point.fx<a.fx&&rnd_point.fx<b.fx){
                test = a.fx-rnd_point.fx + b.fx-rnd_point.fx;
            }
            myCube.AddCubeSample(rnd_point);
        }
        /*double L = 0;
        for (int i=0;i<list.size();i++){
            for (int j=0;j<i;j++){
                Sample x = list.get(i);
                Sample y = list.get(j);
                if (VectorNorm(VectorSub(x.x,y.x)) > 0.000000001)
                    L = Math.max(L,Math.abs(x.fx-y.fx)/VectorNorm(VectorSub(x.x,y.x)));                
            }
        }*/
        
        //double L[] = new double[n];        
        /*for (int i=0;i<n;i++){
            double dL = 0;
            for (int j=0;j<Math.min(list.size(),10);j++){
                 double grad[] = new double[n];
                 int index = generator.nextInt(list.size());                 
                 for (int k=0;k<n;k++){  
                     grad[k] = list.get(index).x[k];
                     if (k == i)
                         grad[k] += 0.01;                     
                 }
                 double value = funct(grad);
                 dL = Math.max((value - list.get(index).fx)/0.01,dL);
             }
            L[i] = dL;
        }*/
        /*for (int t=0;t<3*n*n+1;t++){
            Vector<Sample> tmp_list = new Vector<Sample>();
            
            boolean usedList[] = new boolean[list.size()];
            for (int u=0;u<n+1;u++){
                int rand = generator.nextInt(list.size());
                int index = 0;
                while(rand >= 0){
                    while(usedList[index]){
                        index++;
                        if (index >= usedList.length)
                            index = 0;
                    }
                    rand--;
                }
                usedList[index] = true;
                tmp_list.add(list.get(index));
            }
            
            Matrix x = null;                
            Matrix V = new Matrix(tmp_list.size(),n+1);
            Matrix y = new Matrix(tmp_list.size(),1);
            for (int i=0;i<tmp_list.size();i++){                        
                Sample r = tmp_list.get(i);
                for (int j=0;j<n;j++){
                    V.set(i,j,r.x[j]);
                }
                V.set(i,n,1.0);
                y.set(i,0,r.fx);
            }                    
            try{
                x = (((V.transpose()).times(V)).inverse()).times(V.transpose().times(y));                
                for (int i=0;i<n;i++){
                    L[i] = Math.max(Math.abs(x.get(i,0)),L[i]);
                }                    
            }catch(Exception e){
                //mat is sing. 
                System.out.println("singl. Matrix");                 
            }            
        }
        return L;*/
        
        //single value L
        double variance = 0;
        double mean = 0;
        //Stack<Double> tmp = new Stack<Double>();
        
        double sL = 0;
        double size = (list.size()-1)*(list.size())/2.0;
        for (int i=0;i<list.size();i++){
            for (int j=i+1;j<list.size();j++){
                double d = VectorNorm2(VectorSub(list.get(i).x,list.get(j).x));
                sL = Math.max(Math.abs((list.get(i).fx-list.get(j).fx)/d), sL);
                mean += sL;
                //tmp.push(new Double(sL));
            }
        }
        mean /= size;
        /*while(!tmp.empty()){
            variance += Math.pow(tmp.pop().doubleValue()-mean,2);
        }*/
        //variance /= (size-1);
        //System.out.println("sL:" + sL);
        return sL;//+1.0*Math.sqrt(variance);
    }
    
    void SaveCubes(Vector<HyperCube> cubes,String param_fileName){
        try{
            String fileName = param_fileName;
                     
            BufferedWriter writer = new BufferedWriter(new FileWriter(FileTools.createAbsoluteFileName(this.getModel().getWorkspaceDirectory().getPath(),"/info/" + fileName)));
            for (int i=0;i<cubes.size();i++){
                writer.write(cubes.get(i).compactDescriptionString());
            }
            writer.close();
        }catch(Exception e)        {
            System.out.println(e.toString());
        }
    }
    
    public void run() {
        if (!this.enable.getValue()){
            singleRun();
            return;
        }
        Vector<Sample> Q = new Vector<Sample>();
        Vector<HyperCube> cubes = new Vector<HyperCube>();
                
        int xCount = 0;
        final double epsilon1 = 0.1; //app-error
        boolean stop = false;
        double gamma, myR, my;        
        int k=1;                                

        getModel().getRuntime().sendInfoMsg("***************************");
        getModel().getRuntime().sendInfoMsg("****start optimization ****");
        getModel().getRuntime().sendInfoMsg("***************************");
                        
        Sample a = getSample(super.lowBound);
        Sample b = getSample(super.upBound);
                
        //add upperleft und lowerright corner of cube
        Q.add(a);        
        Q.add(b);        
        //midpoint xr
        double xR_tmp[] = VectorMul(VectorAdd(lowBound,upBound),0.5);
        Sample xR = getSample(xR_tmp);
        Q.add(xR);
        
        //gamma holds minimum of samples
        int IndexWithMinimum = getMin(Q);
        Sample v = Q.get(IndexWithMinimum);
        gamma = v.fx;
        
        //calculate a lower approximation my        
        HyperCube R = new HyperCube(a,b,xR,null);                
        cubes.add(R);
        
        double L = R.L; 

        myR = Math.max(Math.max(a.fx,b.fx) - VectorNorm(VectorMul(VectorSub(a.x,b.x),L)),
                        xR.fx - (VectorNorm(VectorMul(VectorSub(a.x,b.x),L))/2.0));
        my = myR;        
                
        Stack<HyperCube> queue = new Stack<HyperCube>();
        queue.push(R);
                                                       
        while (!stop){    
            R = queue.pop();
            a = R.a;
            b = R.b;
            my = R.highestLowBound;
            
            //System.out.println("Processing next cube:\nR:" + R.toString() + "\nMinimum:" + gamma + "\nk:" + k + "\nSampleCount:" + currentSampleCount);            
            
            //SaveCubes(cubes,"cubedump" + xCount + ".dat");
            if (this.maxn != null){
                if ( this.sampleList.size() >= this.maxn.getValue() ){                    
                    break;
                }
            }                                    
            //current minimum and lower approximation are close
            if (gamma - my < epsilon1){
                //break;
            }
            int sel_j = 0;
            double max = 0;
            for (int i=0;i<n;i++){
                if (b.x[i]-a.x[i] > max){
                    max = b.x[i]-a.x[i];
                    sel_j = i;
                }
            }
            Sample a1 = a;
            double b1_tmp[] = new double[n];
            double a2_tmp[] = new double[n];                        
            Sample b2 = b;                  
            
            for (int i=0;i<n;i++){
                if (i == sel_j){
                    b1_tmp[i] = (a.x[i] + b.x[i]) / 2.0;
                    a2_tmp[i] = (a.x[i] + b.x[i]) / 2.0;
                }
                else{
                    b1_tmp[i] = b.x[i];
                    a2_tmp[i] = a.x[i];
                }
            }
            Sample b1 = getSample(b1_tmp);
            Sample a2 = getSample(a2_tmp);
            
            double xR1_tmp[] = VectorMul(VectorAdd(a1.x,b1.x),0.5);   
            double xR2_tmp[] = VectorMul(VectorAdd(a2.x,b2.x),0.5);   
            
            Sample xR1 = getSample(xR1_tmp);
            Sample xR2 = getSample(xR2_tmp);

            //approx L
            HyperCube R1 = new HyperCube(a1,b1,xR1,R);   
            double L1 = R1.L;
            double tmp1 = test;
            HyperCube R2 = new HyperCube(a2,b2,xR2,R);   
            double L2 = R2.L;
            double tmp2 = test;
            
            Q.clear();
            Q.add(v);
            Q.add(b1);
            Q.add(xR1);
            Q.add(a2);
            Q.add(xR2);
            
            IndexWithMinimum = getMin(Q);
            v = Q.get(IndexWithMinimum);
            gamma = v.fx;
                        
            if (R1.goodOneFactor < tmp1)
                R1.goodOneFactor = tmp1;
                        
            if (R2.goodOneFactor < tmp2)
                R2.goodOneFactor = tmp2;
            cubes.remove(R);
            cubes.add(R1);
            cubes.add(R2);
                                    
            my = Double.MAX_VALUE;//gamma;
                        
            xCount++;
            
            if (!queue.empty()){                
                continue;
            }
            
            boolean candidateFound = false;
            
            //double T[] = {0,0.0001,0.001,0.01,0.02,0.03,0.04,0.05,0.06,0.07,0.08,0.09,0.10,0.11,0.12,0.13,0.15,0.20,0.25,0.3,0.4,0.5,0.75,1.0,1.5,2.0,3.0,6.0};                
            double T[] = {0.0};
            //double T[] = {0,0.0001,0.001,0.01,0.05,0.10,0.25,0.5,1.0,2.0,5.0};                
            int IndexForT[] = new int[T.length];
            
            for (int t=0;t<T.length;t++){
                int best = -1;
                double bestLdiff = 10000000000.0;
                
                for (int i=0;i<cubes.size();i++){
                    HyperCube c = cubes.get(i);
                    
                    double Lstar = c.CalculateLForTarget(gamma-T[t]);                                                            
                    //double Ldiff = Lstar - c.L;//VectorSub(Lstar , c.L);
                    double Ldiff = c.highestLowBound;
                    
                    
                    //if (generator.nextBoolean())
                    /*if (c.goodOneFactor>0)
                        Ldiff -= c.goodOneFactor;*/
                    
                    if ( Ldiff < bestLdiff){
                        best = i;
                        bestLdiff = Ldiff;
                    }
                    if ( xCount % 20 == 19){
                        best = 0;
                        bestLdiff = Ldiff;
                        break;
                    }
                    /*
                    //falls das nicht der fall ist kann man i verwerfen
                    //praktisch wird das nicht der fall sein .. 
                    if (c.bound <= my){
                        my = c.bound;
                        R = c;
                        candidateFound = true;
                    }
                    if (xCount % 10 == 5){
                        my = c.bound;
                        R = c;
                        candidateFound = true;
                        break;
                    }*/
                    
                }
                
                IndexForT[t] = best;
            }
            
            for (int i=0;i<IndexForT.length;i++){
                if (IndexForT[i]==-1)
                    continue;
                for (int j=i+1;j<IndexForT.length;j++){
                    if (IndexForT[i] == IndexForT[j])
                        IndexForT[j] = -1;                    
                }
                //System.out.println("add T =" + T[i]);
                queue.push(cubes.get(IndexForT[i]));
            }
                        
                /*
                if (!candidateFound){
                    System.out.println("Finished there are no more candidates!!");
                    break;
                }
                a = R.a;
                b = R.b;
                my = R.bound;        */
                            
            k++;
        }        
    }    
}
