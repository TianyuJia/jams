/*
 * ShuffleComplexEvolution.java
 * Created on 30. Juni 2006, 15:12
 *
 * This file is part of JAMS
 * Copyright (C) 2005 S. Kralisch and P. Krause
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
package optas.optimizer;

import optas.optimizer.management.OptimizerDescription;
import optas.optimizer.management.NumericOptimizerParameter;
import Jama.Matrix;

import java.util.Arrays;
import jams.JAMS;

import jams.model.*;
import java.util.Arrays.*;
import java.util.StringTokenizer;
import optas.optimizer.DirectSearchMethods.ImplicitFiltering;
import optas.optimizer.DirectSearchMethods.MDS;
import optas.optimizer.DirectSearchMethods.NelderMead;
import optas.optimizer.DirectSearchMethods.PatternSearch;
import optas.optimizer.management.SampleFactory.SampleSO;
import optas.optimizer.management.SampleFactory.SampleSOComperator;
import optas.optimizer.management.ObjectiveAchievedException;

/**
 *
 * @author Christian Fischer, based on the original MatLab sources
 */
@JAMSComponentDescription(title = "SCE Optimization routine",
author = "Christian Fischer",
description = "optimization routine based on Duan et al. 1992")

@SuppressWarnings("unchecked")
public class SCE extends Optimizer {

    //"A and B specify linear constraints, so that for every x the condition Ax = B is satisfied. if you don^t specify A and B the unconstrained problem will be solved"
    public String linearConstraintMatrixA;
    public String linearConstraintVectorB;
    public double complexesCount;
    public double pcento;
    public double kstop;
    public double peps;
    
    int N; //parameter dimension
    int p; //number of complexes
    int s; //population size
    int m; //complex size; floor(s/q)
    int iterationCounter = 0;
    PatternSearch SearchMethod = null;
    Matrix LinearConstraints_A = null, LinearConstraints_b = null;

    @Override
    public boolean init() {
        if (!super.init())
            return false;

        iterationCounter = 0;
        if (linearConstraintMatrixA != null && linearConstraintVectorB != null) {
            StringTokenizer tok = new StringTokenizer(linearConstraintMatrixA, ";");

            int n = tok.countTokens(), m = -1;
            LinearConstraints_A = null;
            for (int i = 0; i < n; i++) {
                StringTokenizer line_tok = new StringTokenizer(tok.nextToken(), ",");
                if (m == -1) {
                    m = line_tok.countTokens();
                    LinearConstraints_A = new Matrix(n, m);
                }
                if (m != line_tok.countTokens()) {
                    log(JAMS.i18n("Linear_Constraint_Matrix_dimension_mismatch"));
                    return false;
                }
                for (int j = 0; j < m; j++) {
                    String number = line_tok.nextToken();
                    double value = 0.0;
                    try {
                        value = Double.parseDouble(number);
                    } catch (NumberFormatException e) {
                        log(JAMS.i18n("Cant_read_Linear_Constraint_Matrix_because_there_are_unparseable_elements") + ":" + e.toString());
                        return false;
                    }
                    LinearConstraints_A.set(i, j, value);
                }
            }

            tok = new StringTokenizer(linearConstraintVectorB, ";");
            LinearConstraints_b = new Matrix(n, 1);
            n = tok.countTokens();
            for (int i = 0; i < n; i++) {
                String number = tok.nextToken();
                double value = 0.0;
                try {
                    value = Double.parseDouble(number);
                } catch (NumberFormatException e) {
                    log(JAMS.i18n("Cant_read_Linear_Constraint_Matrix_because_there_are_unparseable_elements") + ":" + e.toString());
                }
                LinearConstraints_b.set(i, 0, value);
            }
        }

        if (LinearConstraints_A != null && LinearConstraints_b != null) {
            if (LinearConstraints_A.getRowDimension() != LinearConstraints_b.getRowDimension()) {
                log(JAMS.i18n("LinearConstraintMatrixA_must_have_the_same_number_of_rows_as_LinearConstraintVectorB"));
                return false;
            }
            if (LinearConstraints_A.getColumnDimension() != n) {
                log(JAMS.i18n("LinearConstraintMatrixA_must_have_the_same_number_of_columns_as_there_are_parameters"));
                return false;
            }
        }

        if (x0 == null){
            x0 = randomSampler();
        }

        if (complexesCount <= 0) {
            log("warning: NumberOfComplexes_value_not_specified, set to default of 2");
            complexesCount = 2;
        }
        if (pcento <= 0) {
            log("warning: pcento_value_not_specified, set to default of 0.1");
            pcento = 0.1;
        }
        if (peps <= 0) {
            log("warning: peps_value_not_specified, set to default of 0.00001");
            peps=(0.00001);
        }
        if (kstop <= 0) {
            log("warning: kstop_value_not_specified, set to default of 10");
            peps=(10);
        }
        return true;
    }
           
    private double NormalizedgeometricRange(SampleSO x[], double bound[]) {
        if (x.length == 0) {
            return 0;
        }
        double mean = 0;

        for (int i = 0; i < n; i++) {
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;

            for (int j = 0; j < x.length; j++) {
                min = Math.min(min, x[j].x[i]);
                max = Math.max(max, x[j].x[i]);
            }

            mean += (max - min) / bound[i];
        }
        mean /= n;
        return (mean);
    }

    private int find(int lcs[], int startindex, int endindex, int value) {
        for (int i = startindex; i < endindex; i++) {
            if (lcs[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /*class SCEFunctionEvaluator extends AbstractFunction {
        SCE myOptimizer = null;

        SCEFunctionEvaluator(SCE mySCE) {
            myOptimizer = mySCE;
        }

        public void logging(String s){

        }

        public double[] f(double x[])  throws SampleLimitException, ObjectiveAchievedException {
            return myOptimizer.getSample(x).F();
        }
    }*/

    //this method is superseeded by the pattern search algorithms, but its not a bad idea to keep this meothd
    //s forms the simplex
    //sf function values of simplex
    /*bl lower bound, bu upper bound */
    private SampleSO cceua2(SampleSO[] s, double bl[], double bu[])  throws SampleLimitException, ObjectiveAchievedException{
        int nps = s.length;
        int nopt = s[0].x.length;

        int n = nps;
        int m = nopt;

        double alpha = 1.0;
        double beta = 0.5;        
        // Assign the best and worst points:                        
        SampleSO sw = s[n-1];
        
        // Compute the centroid of the simplex excluding the worst point:
        double ce[] = new double[nopt];
        for (int i = 0; i < nopt; i++) {
            ce[i] = 0;
            for (int j = 0; j < n - 1; j++) {
                ce[i] += s[j].x[i];
            }
            ce[i] /= (n - 1);
        }

        // Attempt a reflection point
        double snew[] = new double[nopt];
        for (int i = 0; i < nopt; i++) {
            snew[i] = ce[i] + alpha * (ce[i] - sw.x[i]);
        }

        // Check if is outside the bounds:
        int ibound = 0;
        for (int i = 0; i < nopt; i++) {
            if ((snew[i] - bl[i]) < 0) {
                ibound = 1;
            }
            if ((bu[i] - snew[i]) < 0) {
                ibound = 2;
            }
        }

        if (ibound >= 1) {
            snew = this.randomSampler();
        }
        
        SampleSO fnew = getSampleSO(snew);
        
        // Reflection failed; now attempt a contraction point:
        if (fnew.f() > sw.f()) {
            for (int i = 0; i < nopt; i++) {
                snew[i] = sw.x[i] + beta * (ce[i] - s[n-1].x[i]);
            }
            fnew = getSampleSO(snew);
        }
        // Both reflection and contraction have failed, attempt a random point;
        if (fnew.f() > sw.f()) {
            snew = this.randomSampler();
            fnew = getSampleSO(snew);
        }
        
        return fnew;
    }
    
    private SampleSO cceua(SampleSO[] simplex, double bl[], double bu[]) throws SampleLimitException, ObjectiveAchievedException {
        //convert boundary constraints to linear constraints
        Matrix A = new Matrix(bl.length + bu.length, n);
        Matrix b = new Matrix(bl.length + bu.length, 1);

        for (int i = 0; i < bl.length; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    A.set(i, j, -1);
                    A.set(i + n, j, 1);
                } else {
                    A.set(i, j, 0);
                    A.set(i + n, j, 0);
                }
            }
            b.set(i, 0, -bl[i]);
            b.set(i + n, 0, bu[i]);
        }
        return SearchMethod.step(this, simplex, A, b, lowBound, upBound);    
    }

    private void EvolveSubPopulation(int nspl, int nps, int npg, int nopt, SampleSO c[], double[] bu, double[] bl) {
        //Evolve sub-population igs for nspl steps:
        for (int loop = 0; loop < nspl; loop++) {
            // Select simplex by sampling the complex according to a linear
            // probability distribution
            int lcs[] = new int[nps];
            lcs[0] = 0;
            for (int k3 = 1; k3 < nps; k3++) {
                int lpos = 0;
                for (int iter = 0; iter < 1000; iter++) {
                    lpos = (int) Math.floor(npg + 0.5 - Math.sqrt((npg + 0.5) * (npg + 0.5) - npg * (npg + 1) * this.randomValue()));
                    //wirklich n^tig??
                    int idx = find(lcs, 0, k3, lpos);
                    if (idx == -1) {
                        break;
                    }
                }
                lcs[k3] = lpos;
            }
            Arrays.sort(lcs);

            // Construct the simplex:            
            SampleSO s[] = new SampleSO[nps];

            for (int i = 0; i < nps; i++) {                
                s[i] = c[lcs[i]].clone();   //?necessary to clone?
            }            
            // Replace the worst point in Simplex with the new point:
            try{
                s[nps - 1] = cceua(s, bl, bu);
            }catch(Exception e){
                return;
            }

            //Replace the simplex into the complex;            
            for (int i = 0; i < nps; i++) {
                c[lcs[i]] = s[i];
            }

            // Sort the complex;
            Arrays.sort(c, new SampleSOComperator(false));
        // End of Inner Loop for Competitive Evolution of Simplexes
        }
    }
       
    public SampleSO sceua(double[] x0, double[] bl, double[] bu, int maxn, int kstop, double pcento, double peps, int ngs, int iseed){
        int method = 1;
                
        SearchMethod = null;
        if (method == 1) {
            SearchMethod = new NelderMead();
        } else if (method == 2) {
            SearchMethod = new ImplicitFiltering();
        } else if (method == 3) {
            SearchMethod = new MDS();
        }
        int nopt = x0.length;
        int npg = 2 * nopt + 1;
        int nps = nopt + 1;
        int nspl = npg;
        int npt = npg * ngs;

        double bound[] = new double[nopt];
        for (int i = 0; i < nopt; i++) {
            bound[i] = bu[i] - bl[i];
        }
        int i=0;
        SampleSO x[] = new SampleSO[npt];
        try{
            x[0] = getSampleSO(x0);
            for (i = 0; i < npt; i++) {
                double value[] = randomSampler();
                x[i] = getSampleSO(value);
            }
        }catch(SampleLimitException e){
            System.out.println(e);
            return x[i];
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
            return x[i];
        }

        int nloop = 0;        
        // Sort the population in order of increasing function values;
        Arrays.sort(x, new SampleSOComperator(false));
                        
        // Computes the normalized geometric range of the parameters
        double gnrng = NormalizedgeometricRange(x, bound); //exp(mean(log((max(x)-min(x))./bound)));

        log(JAMS.i18n("The_Inital_Loop_0"));
        log(JAMS.i18n("Best") + x[0].toString());
        log(JAMS.i18n("Worst") + x[npt-1].toString());
                                
        if (gnrng < peps) {            
            log(JAMS.i18n("THE_POPULATION_HAS_CONVERGED_TO_A_PRESPECIFIED_SMALL_PARAMETER_SPACE"));
        }

        // Begin evolution loops:
        nloop = 0;
        double criter[] = new double[kstop];
        double criter_change = 100000;

        while (criter_change > pcento) {
            nloop++;
            // Loop on complexes (sub-populations);
            for (int igs = 0; igs < ngs; igs++) {
                // Partition the population into complexes (sub-populations);
                int k1[] = new int[npg];
                int k2[] = new int[npg];

                for ( i = 0; i < npg; i++) {
                    k1[i] = i;
                    k2[i] = k1[i] * ngs + igs;
                }

                SampleSO c[] = new SampleSO[npg];                
                for ( i = 0; i < npg; i++) {
                    c[k1[i]] = x[k2[i]].clone();
                }
                EvolveSubPopulation(nspl, nps, npg, nopt, c, bu, bl);

                // Replace the complex back into the population;
                for ( i = 0; i < npg; i++) {                    
                    x[k2[i]] = c[k1[i]];
                }
            // End of Loop on Complex Evolution;
            }            
            Arrays.sort(x, new SampleSOComperator(false));
                  
            //Compute the standard deviation for each parameter            
            gnrng = NormalizedgeometricRange(x, bound);

            // Record the best and worst points;            
            log(JAMS.i18n("Evolution_Loop") + ":" + nloop + JAMS.i18n("Trial") + " " + getIterationCounter());
            log(JAMS.i18n("Best") + x[0]);
            log(JAMS.i18n("Worst") + x[x.length-1]);
                        
            if (gnrng < peps) {
                log(JAMS.i18n("THE_POPULATION_HAS_CONVERGED_TO_A_PRESPECIFIED_SMALL_PARAMETER_SPACE"));
            }

            for ( i = 0; i < kstop - 1; i++) {
                criter[i] = criter[i + 1];
            }
            criter[kstop - 1] = x[0].f();
            if (nloop >= kstop) {
                criter_change = Math.abs(criter[0] - criter[kstop - 1]) * 100.0;
                double criter_mean = 0;
                for ( i = 0; i < kstop; i++) {
                    criter_mean += Math.abs(criter[i]);
                }
                criter_mean /= kstop;
                criter_change /= criter_mean;

                if (criter_change < pcento) {
                    log(JAMS.i18n("THE_BEST_POINT_HAS_IMPROVED_IN_LAST") + " " + kstop + " " +  JAMS.i18n("LOOPS_BY"));
                    log(JAMS.i18n("LESS_THAN_THE_THRESHOLD") + " " + pcento + "%");
                    log(JAMS.i18n("CONVERGENCY_HAS_ACHIEVED_BASED_ON_OBJECTIVE_FUNCTION_CRITERIA"));
                }
            }
        }
        log(JAMS.i18n("SEARCH_WAS_STOPPED_AT_TRIAL_NUMBER") + " " +getIterationCounter());
        log(JAMS.i18n("NORMALIZED_GEOMETRIC_RANGE") + " " + gnrng);
        log(JAMS.i18n("THE_BEST_POINT_HAS_IMPROVED_IN_LAST") + kstop + " "+JAMS.i18n("LOOPS_BY") + " "+ criter_change + "%");
                
        return x[0];
    }

    @Override
    public void procedure()  throws SampleLimitException, ObjectiveAchievedException {                                                     
        sceua(x0, this.lowBound, this.upBound, (int)getMaxn(), (int)kstop, pcento, peps, (int)complexesCount, 10);
    }

    public OptimizerDescription getDescription() {
        OptimizerDescription desc = OptimizerLibrary.getDefaultOptimizerDescription(SCE.class.getSimpleName(), SCE.class.getName(), 6, false);

        desc.addParameter(new NumericOptimizerParameter("complexesCount",
                JAMS.i18n("number_of_complexes"), 2, 1, 100));

        desc.addParameter(new NumericOptimizerParameter(
                "pcento", JAMS.i18n("worst_acceptable_improvement"),
                0.05, 0.000001, 1));

        desc.addParameter(new NumericOptimizerParameter(
                "peps", JAMS.i18n("minimal_geometric_population"),
                0.00001, 0.000001, 1));

        desc.addParameter(new NumericOptimizerParameter(
                "kstop", JAMS.i18n("kStop"),
                10, 1, 100));

        return desc;
    }
}