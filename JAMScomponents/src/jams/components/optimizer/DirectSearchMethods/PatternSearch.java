/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jams.components.optimizer.DirectSearchMethods;

import Jama.Matrix;
import jams.components.optimizer.Optimizer.ObjectiveAchievedException;
import jams.components.optimizer.SOOptimizer;
import jams.components.optimizer.SampleFactory.SampleSO;
import jams.components.optimizer.SampleLimitException;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author Christian Fischer
 */
abstract public class PatternSearch {
    abstract public SampleSO step(SOOptimizer context,SampleSO[] Simplex,Matrix LinearConstraintMatrixA,Matrix LinearConstraintVectorb,double lowBound[],double upBound[]) throws SampleLimitException, ObjectiveAchievedException;
    static Random Generator = null;
        
    protected SampleSO step(SOOptimizer context,SampleSO best,SampleSO worst,Vector<Matrix> P) throws SampleLimitException, ObjectiveAchievedException{
        Matrix x = new Matrix(best.x,best.x.length);
        for (int i=0;i<P.size();i++){
            Matrix x_next = x.plus(P.get(i));
            SampleSO next = context.getSample(x_next.getColumnPackedCopy());
            if (next.f() < worst.f()){
                return next;
            }
        }
        return null;
    }
    abstract public SampleSO search(SOOptimizer context,Matrix LinearConstraintMatrixA,Matrix LinearConstraintVectorb);
}