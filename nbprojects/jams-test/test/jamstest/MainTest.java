/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamstest;

import TextDiff.MatchCommand;
import TextDiff.Report;
import TextDiff.TextDiff;
import java.io.File;
import java.util.Iterator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import reg.dsproc.SimpleSerieProcessor;

/**
 *
 * @author chris
 */
public class MainTest {

    public MainTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of main method, of class Main.
     */
    @Test
    public void testMain() {
        System.out.println("performing JAMS - Gehlberg Test");

        System.out.println(System.getProperty("user.dir"));
        String args[] = new String[]{
            "-c", "../test.jap",
            "-m", "../../../modeldata/JAMS-Gehlberg/j2k_gehlberg.jam"
        };
        jamsui.launcher.JAMSui.main(args);

        SimpleSerieProcessor ssProcessorSimulation = new SimpleSerieProcessor(new File("../../../modeldata/JAMS-Gehlberg/output/current/TimeLoop.dat"));
        SimpleSerieProcessor ssProcessorReference  = new SimpleSerieProcessor(new File("../../../modeldata/JAMS-Gehlberg/test/reference/TimeLoop.dat"));

        ssProcessorSimulation.getData(args)
        //test output
/*        try{
            Report report = new TextDiff().compare("../../../modeldata/JAMS-Gehlberg/output/current/TimeLoop.dat","../../../modeldata/JAMS-Gehlberg/test/reference/TimeLoop.dat");
            Iterator<Object> iter = report.iterator();
            Report updateReport = new Report();
            while (iter.hasNext()){
                Object obj = iter.next();
                if (! (obj instanceof MatchCommand) )
                    updateReport.add(obj);
            }
            updateReport.print();        
            org.junit.Assert.assertTrue(updateReport.isEmpty());            
        }catch(Exception e){
            org.junit.Assert.fail("Exception while comparing Gehlberg - Results:" + e.toString());
        }*/
    }

}