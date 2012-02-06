/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jams.components.optimizer;

import jams.JAMS;
import jams.components.optimizer.SampleFactory.SampleSO;
/**
 *
 * @author Christian Fischer
 */
public class ParticleSwarm extends SOOptimizer {
    
    static public class Particle{
        SampleSO particle;
        SampleSO local_best;
        double[] velocity;
        
        Particle(SampleSO particle,double[] velocity){
            this.particle = particle;
            this.local_best = particle;
            this.velocity = velocity;
        }                
    }
    
    int numberOfParticles = 1000;
    Particle particles[] = new Particle[numberOfParticles];            
    Particle bestParticle = null;
    
    double w = 0.9;
    double c1 = 0.7;
    double c2 = 1.4;
    double c3 = 0.0;
    
    boolean feasible(double nextPosition[]){
        for (int i=0;i<n;i++){
            if (nextPosition[i]<this.lowBound[i] || nextPosition[i]>this.upBound[i])
                return false;
        }
        return true;
    }
         
    @Override
    public void init(){
        super.init();
    }
    @Override
    public void procedure() throws SampleLimitException, ObjectiveAchievedException{
        System.out.println(JAMS.i18n("start_optimization_of") + " " + this.getInstanceName());
        for (int i=0;i<numberOfParticles;i++){
            SampleSO rndSample = null;
            
            if (i==0 && x0 != null){
                rndSample = this.getSample(x0);
            }else
                rndSample = this.getSample(this.RandomSampler());
            
            particles[i] = new Particle(rndSample,new double[n]);
                        
            if (bestParticle!=null){
                if (bestParticle.particle.f() > particles[i].particle.f())
                    bestParticle = particles[i];
            }else
                bestParticle = particles[i];            
        }
        
        while(true){
            //randomly generate r1 and r2
            double r1[] = new double[n];
            double r2[] = new double[n];
            double r3[] = new double[n];
            for (int i=0;i<n;i++){
                r1[i] = generator.nextDouble();
                r2[i] = generator.nextDouble();
                r3[i] = generator.nextDouble();                
            }            
            for (int i=0;i<numberOfParticles;i++){
                //update velocities
                double nextPosition[] = new double[n];
                for (int j=0;j<n;j++){
                    particles[i].velocity[j] = w*particles[i].velocity[j];                    
                    particles[i].velocity[j] -= c1*r1[j]*(particles[i].particle.x[j]-particles[i].local_best.x[j]);
                    particles[i].velocity[j] -= c2*r2[j]*(particles[i].particle.x[j]-bestParticle.local_best.x[j]);
                    
                    int rndParticle = generator.nextInt(numberOfParticles);
                    
                    particles[i].velocity[j] -= c3*r3[j]*(particles[i].particle.x[j]-particles[rndParticle].local_best.x[j]);
                    
                    nextPosition[j] = particles[i].particle.x[j] + particles[i].velocity[j];                                                            
                }
                double beta = 0.5;
                while(!feasible(nextPosition)){
                    for (int j=0;j<n;j++){
                        nextPosition[j] -= beta*particles[i].velocity[j];     
                    }
                }    
                //evaluate
                particles[i].particle = this.getSample(nextPosition);
                if (particles[i].particle.f() < particles[i].local_best.f())
                    particles[i].local_best = particles[i].particle;
                if (particles[i].particle.f() < bestParticle.local_best.f()){
                    bestParticle = particles[i];
                }
            }
            System.out.println(JAMS.i18n("current_best")+":" + bestParticle.local_best.toString() + " " + JAMS.i18n("after") + " " +  this.iterationCounter.getValue());
        }
        //System.out.println(JAMS.i18n("finished_optimization"));
    }
}