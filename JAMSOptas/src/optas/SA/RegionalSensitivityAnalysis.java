/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package optas.SA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import optas.hydro.data.EfficiencyEnsemble;

/**
 *
 * @author chris
 */
public class RegionalSensitivityAnalysis extends SensitivityAnalyzer{

    int currentIndex = 0;
    double sensitivityIndex[];

    @Override
    public void init(){
        super.init();

        double range[][] = this.getParameterRange();

        ArrayList<Integer> behavourialBox = new ArrayList<Integer>();
        ArrayList<Integer> nonBehavourialBox = new ArrayList<Integer>();

        EfficiencyEnsemble likelihood = y.CalculateLikelihood();
        Integer sortedIds[] = likelihood.sort();

        sensitivityIndex =new double[n];

        //sort data into boxes
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < L; j++) {
                if (j > L / 2) {
                    behavourialBox.add(sortedIds[j]);
                } else {
                    nonBehavourialBox.add(sortedIds[j]);
                }
            }

            this.currentIndex = i;

            Collections.sort(behavourialBox, new Comparator<Integer>() {

                public int compare(Integer i, Integer j) {
                    double vi = RegionalSensitivityAnalysis.this.x[currentIndex].getValue(i);
                    double vj = RegionalSensitivityAnalysis.this.x[currentIndex].getValue(j);

                    if (vi < vj)
                        return -1;
                    else if (vi == vj)
                        return 0;
                    else
                        return 1;
                }
            });

            Collections.sort(nonBehavourialBox, new Comparator<Integer>() {

                public int compare(Integer i, Integer j) {
                    double vi = RegionalSensitivityAnalysis.this.x[currentIndex].getValue(i);
                    double vj = RegionalSensitivityAnalysis.this.x[currentIndex].getValue(j);

                    if (vi < vj)
                        return -1;
                    else if (vi == vj)
                        return 0;
                    else
                        return 1;
                }
            });

            double behavourialDistribution = 0;
            double nonBehavourialDistribution = 0;

            double step1 = 1.0 / (double)behavourialBox.size();
            double step2 = 1.0 / (double)nonBehavourialBox.size();

            int k2 = 0;
            sensitivityIndex[i] = 0;

            for (int k=0;k<behavourialBox.size();k++){
                double value = behavourialBox.get(k);
                while(nonBehavourialBox.get(k2)<value){
                    k2++;
                    nonBehavourialDistribution += step2;
                }
                behavourialDistribution += step1;
                if (Math.abs(behavourialDistribution - nonBehavourialDistribution) > sensitivityIndex[i])
                    sensitivityIndex[i] = Math.abs(behavourialDistribution - nonBehavourialDistribution);
            }
        }
        double sum = 0;
        for (int k=0;k<n;k++){
            sum += sensitivityIndex[k];
        }
        for (int k=0;k<n;k++){
            sensitivityIndex[k] /= sum;
        }
    }

    public double getSensitivity(int parameter){
        return sensitivityIndex[parameter];
    }

}