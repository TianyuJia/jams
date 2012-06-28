/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package optas.optimizer;


import jams.io.SerializableBufferedWriter;
import jams.model.JAMSComponentDescription;
import optas.optimizer.management.SampleFactory.Sample;
import optas.optimizer.management.ObjectiveAchievedException;
import optas.optimizer.management.OptimizerDescription;

@SuppressWarnings("unchecked")
@JAMSComponentDescription(
        title="Random Sampler",
        author="Christian Fischer",
        description="Performs a random search"
        )
public class RandomSampler extends Optimizer{           
    SerializableBufferedWriter writer = null;
            
    public Sample[] initialSimplex = null;

    public OptimizerDescription getDescription() {
        return OptimizerLibrary.getDefaultOptimizerDescription(RandomSampler.class.getSimpleName(), RandomSampler.class.getName(), 500, false);
    }
        
    @Override
    public void procedure()throws SampleLimitException, ObjectiveAchievedException{        
        Sample simplex[] = new Sample[(int)this.getMaxn()];
        
        for (int i=0;i<simplex.length;i++){
             if (x0 != null && i<x0.length){
                 simplex[i] = this.getSample(x0[i]);
             }
             simplex[i] = this.getSample(this.randomSampler());
        }
    }        
}
