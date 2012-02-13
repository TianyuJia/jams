/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package optas.optimizer;

import jams.model.JAMSComponentDescription;
import jams.model.JAMSModel;
import jams.model.Model;
import jams.runtime.StandardRuntime;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import optas.hydro.data.DataCollection;
import optas.optimizer.management.BooleanOptimizerParameter;
import optas.optimizer.management.NumericOptimizerParameter;
import optas.optimizer.management.SampleFactory.Sample;
import optas.optimizer.management.ObjectiveAchievedException;
import optas.optimizer.management.OptimizerDescription;
import optas.optimizer.parallel.ParallelExecution;
import optas.optimizer.parallel.ParallelJob;
import optas.optimizer.parallel.ParallelTask;
import reg.dsproc.ImportMonteCarloData;

@SuppressWarnings("unchecked")
@JAMSComponentDescription(
        title="Random Sampler",
        author="Christian Fischer",
        description="Performs a random search"
        )
public class ParallelHaltonSequenceSampling extends Optimizer{    

    static public class InputData implements Serializable {
        public int sampleCount;
        public int offset;
        public long seed;
        public ParallelHaltonSequenceSampling context;

        public InputData(int offset, int sampleCount, ParallelHaltonSequenceSampling context) {
            this.offset = offset;
            this.seed = generator.nextLong();
            this.sampleCount = sampleCount;
            this.context = context;
        }
    }

    static public class OutputData implements Serializable {

        public DataCollection dc;
        public ArrayList<Sample> list;

        public OutputData(ArrayList<Sample> list) {
            dc = null;
            this.list = list;
        }

        public void add(String context, File dataStore) {
            ImportMonteCarloData importer = new ImportMonteCarloData(dataStore);
            if (dc == null)
                dc = importer.getEnsemble();
            else
                dc.unifyDataCollections(importer.getEnsemble());            
        }
    }
    
    public double offset = 0;
    public boolean analyzeQuality = true;
    public double targetQuality = 0.8;
    public double minn = 0;
    public String excludeFiles = "";
    public int samplesPerIteration = 192;
    public Model model = null;
    public int threadCount = 12;

    public void setThreadCount(int threadCount){
        this.threadCount = threadCount;
    }

    public int getThreadCount(){
        return this.threadCount;
    }

    public double getSamplesPerIteration(double value){
        return this.samplesPerIteration;
    }
    public void setSamplesPerIteration(double value){
        this.samplesPerIteration = (int)value;
    }
    public Model getModel(){
        return model;
    }

    public void setModel(Model model){
        this.model = model;
    }

    public String getExcludeFiles(){
        return excludeFiles;
    }

    public void setExcludeFiles(String excludeFiles){
        this.excludeFiles = excludeFiles;
    }

    public double getMinn(){
        return minn;
    }
    public void setMinn(double minn){
        this.minn = minn;
    }
    
    public double getOffset(){
        return this.offset;
    }

    public void setOffset(double offset){
        this.offset = offset;
    }

    public void setAnalyzeQuality(boolean analyzeQuality){
        this.analyzeQuality = analyzeQuality;
    }

    public boolean isAnalyzeQuality(){
        return this.analyzeQuality;
    }

    public void setTargetQuality(double targetQuality){
        this.targetQuality = targetQuality;
    }

    public double getTargetQuality(){
        return this.targetQuality;
    }

    private int primTable[] = {2,     3,     5,     7,    11,    13,    17,    19,    23,    29,    31,    37,    41,    43,
   47,    53,    59,    61,    67,    71,    73,    79,    83,    89,    97,   101,   103,   107,
  109,   113,   127,   131,   137,   139,   149,   151,   157,   163,   167,   173,   179,   181,
  191,   193,   197,   199,   211,   223,   227,   229,   233,   239,   241,   251,   257,   263,
  269,   271,   277,   281,   283,   293,   307,   311,   313,   317,   331,   337,   347,   349,
  353,   359,   367,   373,   379,   383,   389,   397,   401,   409,   419,   421,   431,   433,
  439,   443,   449,   457,   461,   463,   467,   479,   487,   491,   499,   503,   509,   521,
  523,   541,   547,   557,   563,   569,   571,   577,   587,   593,   599,   601,   607,   613,
  617,   619,   631,   641,   643,   647,   653,   659,   661,   673,   677,   683,   691,   701,
  709,   719,   727,   733,   739,   743,   751,   757,   761,   769,   773,   787,   797,   809,
  811,   821,   823,   827,   829,   839,   853,   857,   859,   863,   877,   881,   883,   887,
  907,   911,   919,   929,   937,   941,   947,   953,   967,   971,   977,   983,   991,   997,
 1009,  1013,  1019,  1021,  1031,  1033,  1039,  1049,  1051,  1061,  1063,  1069,  1087,  1091,
 1093,  1097,  1103,  1109,  1117,  1123,  1129,  1151,  1153,  1163,  1171,  1181,  1187,  1193,
 1201,  1213,  1217,  1223,  1229,  1231,  1237,  1249,  1259,  1277,  1279,  1283,  1289,  1291,
 1297,  1301,  1303,  1307,  1319,  1321,  1327,  1361,  1367,  1373,  1381,  1399,  1409,  1423,
 1427,  1429,  1433,  1439,  1447,  1451,  1453,  1459,  1471,  1481,  1483,  1487,  1489,  1493,
 1499,  1511,  1523,  1531,  1543,  1549,  1553,  1559,  1567,  1571,  1579,  1583,  1597,  1601,
 1607,  1609,  1613,  1619,  1621,  1627,  1637,  1657,  1663,  1667,  1669,  1693,  1697,  1699,
 1709,  1721,  1723,  1733,  1741,  1747,  1753,  1759,  1777,  1783,  1787,  1789,  1801,  1811,
 1823,  1831,  1847,  1861,  1867,  1871,  1873,  1877,  1879,  1889,  1901,  1907,  1913,  1931,
 1933,  1949,  1951,  1973,  1979,  1987,  1993,  1997,  1999,  2003,  2011,  2017,  2027,  2029,
 2039,  2053,  2063,  2069,  2081,  2083,  2087,  2089,  2099,  2111,  2113,  2129,  2131,  2137,
 2141,  2143,  2153,  2161,  2179,  2203,  2207,  2213,  2221,  2237,  2239,  2243,  2251,  2267,
 2269,  2273,  2281,  2287,  2293,  2297,  2309,  2311,  2333,  2339,  2341,  2347,  2351,  2357,
 2371,  2377,  2381,  2383,  2389,  2393,  2399,  2411,  2417,  2423,  2437,  2441,  2447,  2459,
 2467,  2473,  2477,  2503,  2521,  2531,  2539,  2543,  2549,  2551,  2557,  2579,  2591,  2593,
 2609,  2617,  2621,  2633,  2647,  2657,  2659,  2663,  2671,  2677,  2683,  2687,  2689,  2693,
 2699,  2707,  2711,  2713,  2719,  2729,  2731,  2741,  2749,  2753,  2767,  2777,  2789,  2791
};

    public Sample[] initialSimplex = null;
    
    public OptimizerDescription getDescription() {
        OptimizerDescription desc = OptimizerLibrary.getDefaultOptimizerDescription(ParallelHaltonSequenceSampling.class.getSimpleName(), ParallelHaltonSequenceSampling.class.getName(), 500, false);

        desc.addParameter(new NumericOptimizerParameter("offset",
                "offset", 0, 0, Integer.MAX_VALUE));

        desc.addParameter(new BooleanOptimizerParameter("analyzeQuality",
                "analyzeQuality", false));

        desc.addParameter(new NumericOptimizerParameter("targetQuality",
                "targetQuality", 0.8, -100.0, 1.0));
        
        return desc;
    }

    private long iexp(int base, int exp){
        long result = 1;
        while(exp>0){
            result*=base;
            exp--;
        }
        return result;
    }

    private int[] generatePermutation(int base){
        int map[] = new int[base];
        int set[] = new int[base];

        for (int i=0;i<base;i++){
            set[i] = i;
        }
        for (int i=0;i<base;i++){
            int index = generator.nextInt(base-i);
            map[i] = set[index];
            set[index] = set[base-i-1];
        }
        return map;
    }


    private int[] scramble(int[] p, int base){
        int map[] = generatePermutation(base);
        for (int i=0;i<p.length;i++){
            p[i] = map[p[i]];
        }
        return p;
    }

    private int[] toRadix(long value, int base) {
        int exp = 0;
        while (iexp(base, exp+1) <= value){
            exp++;
        }

        long radix = 0;
        int result[] = new int[exp+1];

        while (exp>=0) {
            radix = iexp(base, exp);
            result[exp] = (int)(value/radix);            
            value -= result[exp] * radix;
            exp--;
        }
        return result;
    }

    private double toFractional(int[] number, int base){
        double result = 0;

        for (int i=0;i<number.length;i++){
            long radix = iexp(base, i+1);
            result += (1.0 / (double)radix) * number[i];
        }
        return result;
    }

    @Override
    public boolean init(){
        if (!super.init())
            return false;

        String libs[] = null;
        if (this.getModel().getRuntime() instanceof StandardRuntime)
             libs = ((StandardRuntime)this.getModel().getRuntime()).getLibs();

        if (libs != null) {
            for (int i = 0; i < libs.length; i++) {
                String lib = libs[i];
                File fileToLib = new File(lib);
                if (fileToLib.exists()) {
                    ParallelExecution.addJarsToClassPath(ClassLoader.getSystemClassLoader(), fileToLib);
                }
            }
        }else{
            log("Warning: no libary path was specified");
        }

        return true;
    }

    public static class ParallelHaltonSequenceSamplingTask extends ParallelTask<InputData, OutputData> implements Serializable{

        public ArrayList<ParallelJob> split(InputData taskArgument, int gridSize) {
            ArrayList<ParallelJob> jobs = new ArrayList<ParallelJob>(gridSize);

            int iterationsPerJob = (int)Math.ceil((double)taskArgument.sampleCount / (double)gridSize);
            InputData jobsData[] = new InputData[gridSize];

            int start = taskArgument.offset;
            int end   = start;

            for (int i = 0; i < gridSize; i++) {                
                end = Math.min(start+iterationsPerJob, taskArgument.offset+taskArgument.sampleCount);

                jobsData[i] = new InputData(start, end-start, taskArgument.context);
                start = end;
            }

            for (int i = 0; i < jobsData.length; i++) {
                // Pass in value to check, and minimum/maximum range boundaries
                // into job as arguments.

                jobs.add(new ParallelJob<InputData, OutputData>(jobsData[i]) {

                    public void moveWorkspace(File newWorkspace) {
                        ((JAMSModel) arg.context.getModel()).moveWorkspaceDirectory(newWorkspace.getAbsolutePath());
                    }

                    public OutputData execute() {
                        OutputData result = ParallelHaltonSequenceSampling.parallelExecute(arg);

                        return result;
                    }
                });
            }

            // List of jobs to be executed on the grid.
            return jobs;
        }

        public OutputData reduce(ArrayList<OutputData> results) {
            System.out.println("reduce_function_started_");
            
            OutputData merged = new OutputData(new ArrayList<Sample>());
            for (int i = 0; i < results.size(); i++) {
                if (merged.dc == null) {
                    merged.dc = results.get(i).dc;
                } else {
                    merged.dc.mergeDataCollections(results.get(i).dc);
                }
                merged.list.addAll(results.get(i).list);
            }
            System.out.println("reduce_function_finished");
            return merged;
        }
    }

    @Override
    public void procedure() throws SampleLimitException, ObjectiveAchievedException {        
        double meanTime = 0;
        double totalExecutionTime = 0;
        double executionCounter = 0;
        double remainingTime = 0;

        long startTime = System.currentTimeMillis();

        OutputData result = null;
        DataCollection collection = null;
        ParallelExecution<InputData, OutputData> executor = new ParallelExecution<InputData, OutputData>(workspace, excludeFiles);

        for (int i = 0; i < Math.ceil(this.maxn / samplesPerIteration); i++) {

            int currentOffset      = (int)offset + (i * samplesPerIteration);
            int sampleCount        = (int)Math.min( samplesPerIteration, this.maxn - i * samplesPerIteration);
            InputData param = new InputData(currentOffset, sampleCount, this);
            
            result = executor.execute(param, new ParallelHaltonSequenceSamplingTask(), threadCount);
            this.injectSamples(result.list);

            if (collection == null) {
                collection = result.dc;
            } else {
                collection.mergeDataCollections(result.dc);
            }

            long time2 = System.currentTimeMillis();

            totalExecutionTime = (double) (time2 - startTime) / 1000.0;
            executionCounter = this.factory.getSize();
            meanTime = (double) (totalExecutionTime / executionCounter);

            remainingTime = (this.maxn - executionCounter) * meanTime;

            if (analyzeQuality) {
                double quality = this.factory.getStatistics().calcQuality();
                this.log("Estimating Quality of sampling (prior optimization) with " + this.getStatistics().size() + " samples");                
                this.log("Average Quality based on E2 is: " + quality);
                this.log("Target quality is " + targetQuality);
                this.log("Mean time per execution is " + meanTime);
                if (remainingTime > 86400) {
                    this.log("Estimated time of finish is in " + remainingTime / 86400 + " days");
                } else if (remainingTime > 3600) {
                    this.log("Estimated time of finish is in " + remainingTime / 3600 + " hrs");
                } else if (remainingTime > 60) {
                    this.log("Estimated time of finish is in " + remainingTime / 60.0 + " min");
                } else {
                    this.log("Estimated time of finish is in " + remainingTime + " sec");
                }

                if (targetQuality <= quality && i >= this.minn) {
                    this.log("Finish sampling");
                    break;
                }
            }
            if (result != null) {
                //result.dc.dump(getModel().getWorkspace().getOutputDataDirectory());
            }
        }
        if (collection != null) {
            collection.dump(getModel().getWorkspace().getOutputDataDirectory());
        }
    }

    
    public void procedure2(long seed, int offset)throws SampleLimitException, ObjectiveAchievedException{
        Sample simplex[] = new Sample[(int)this.getMaxn()];
                        
        int N = (int)this.getMaxn();

        if (generator == null){
            generator = new Random(seed);
        }else{
            generator.setSeed(seed);
        }

        for (int i=0;i<N;i++){
            if (i==0 && x0 != null && offset == 0){
                simplex[i] = this.getSample(x0);
                continue;
            }            
            
            double x[] = new double[n];
            final long L = 409;
            for (int j=0;j<n;j++){
                int base = primTable[j];
                //generate radix presentation of i with base prim[j]
                //e.g 11, p=3 -> 11_3 = 102
                int radix[] = toRadix(L*(long)(i+offset),base);
                //interpret representation as fractional number
                //102 -> 0.201_3
                //convert it to double
                //0.201_3 = 0.704_10
                double w = toFractional(/*scramble(radix, base)*/radix,base);
                x[j] = this.lowBound[j] + w*(this.upBound[j] - this.lowBound[j]);
            }
                        
            simplex[i] = this.getSample(x);            
        }
    }
    
    static public OutputData parallelExecute(InputData in) {                
        in.context.offset = in.offset;
        in.context.maxn = in.sampleCount;
        try{
            in.context.procedure2(in.seed,in.offset);
        }catch(SampleLimitException sle){
            
        }catch(ObjectiveAchievedException oae){

        }
        
        in.context.log("finished myjob");
        OutputData data = new OutputData(in.context.factory.getSampleList());
        //nach output files suchen
        File outputDataDir = in.context.getModel().getWorkspace().getOutputDataDirectory();
        File list[] = outputDataDir.listFiles();
        if (list!=null){
            for (int i = 0; i < list.length; i++) {
                if (list[i].getName().endsWith("dat"))
                    data.add(list[i].getName(), list[i]);
            }
        }       
        return data;
    }
        
    public static void main(String[] args) {
        ParallelHaltonSequenceSampling hss = new ParallelHaltonSequenceSampling();
        hss.maxn = 500;

        int n = 10;
        int m = 1;

        hss.n = n;
        hss.m = m;
        hss.lowBound = new double[]{0,0,0,0,0,0,0,0,0,0};
        hss.upBound = new double[]{1,1,1,1,1,1,1,1,1,1};
        hss.objNames = new String[]{"y"};
        hss.offset = 0;

        hss.x0 = null;
        hss.setParameterNames(new String[]{"x0,x1,x2,x3,x4,x5,x6,x7,x8,x9"});
        hss.setWorkspace(new File("C:/Arbeit/"));
        hss.setFunction(new AbstractFunction() {

            @Override
            public double[] f(double[] x) {
                return new double[]{1.0};
            }

            @Override
            public void logging(String msg) {
                System.out.println(msg);
            }
        });

        hss.init();

        Arrays.toString(hss.optimize().toArray());
    }
}
