/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package reg.spreadsheet;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGEncodeParam;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
import jams.data.JAMSCalendar;
import jams.data.JAMSDataFactory;
import jams.gui.LHelper;
import jams.gui.WorkerDlg;
import jams.workspace.DataSet;
import jams.workspace.JAMSWorkspace;
import jams.workspace.datatypes.DataValue;
import jams.workspace.datatypes.DoubleValue;
import jams.workspace.stores.InputDataStore;
import java.util.Vector;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.BorderFactory.*;
import javax.swing.GroupLayout.*;


import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import reg.Regionalizer;


/**
 *
 * @author Robert Riedel
 */
public class STPConfigurator extends JFrame{

    JPanel plotpanel;
    JPanel optionpanel;
    JPanel chooserpanel;
    
    GridBagLayout ogbl;
    
    JComboBox templateBox[];
    JRadioButton timeButton[];
    JLabel titleLabel[];
    JTextField weightField[];
    JTextField titleField;
    ButtonGroup axisGroup;
    
    JButton plotbutton;
    JButton settitleButton;
    JButton epsButton;
    JButton addbutton;
    JButton removebutton;
    JLabel edTitleLabel;
    
    JPanel chartpanel;

    JAMSSpreadSheet sheet;
    JAMSStackedPlot stackedplot;
    JFrame parent;
//    JTable table; //wichtig?
    File templateFile;
    private JAMSWorkspace workspace;
            
    int rows, columns, graphCount, selectedTimeAxis;
    int[] weights;
    String[] headers;
    
    Vector<double[]> arrayVector;
    
    Vector<GraphProperties> propVector; //for one plot!!!
    Vector<JAMSCalendar> timeVector;
    
//    static String DATASET_01 = "tmax";
//    static String DATASET_02 = "tmean";
    
    static String DATASET[] = {"tmax","tmean"};
    String[] dataset;
    String datasetID;
//    private JAMSTimePlot jts_01 = new JAMSTimePlot();
//    private JAMSTimePlot jts_02 = new JAMSTimePlot();
    
    private JAMSTimePlot jts[];
    
    InputDataStore store;
    
    int rLeft, rRight = 0;
    boolean invLeft, invRight = false;
    boolean timeFormat_yy, timeFormat_mm, timeFormat_dd, timeFormat_hm = true;
    
    String title, tLeft, tRight, xAxisTitle = "";
    
    JPanel plotPanel;
    
    File templateFiles[];
    int numberOfPlots;
    
    public STPConfigurator(Regionalizer regionalizer, int numberOfPlots){

        this.workspace = regionalizer.getWorkspace();
        this.parent = regionalizer.getRegionalizerFrame();
        this.setIconImage(parent.getIconImage());
        setTitle("StackedTimePlot Configurator");
        
        Container cp = this.getContentPane();
        JPanel bgPanel = new JPanel();
        cp.setBackground(Color.WHITE);
        cp.add(bgPanel);
        bgPanel.setBackground(Color.WHITE);
        
        setLayout(new FlowLayout());
        Point parentloc = parent.getLocation();
        setLocation(parentloc.x + 30, parentloc.y + 30);

        this.numberOfPlots = numberOfPlots;
//        jts = new JAMSTimePlot[numberOfPlots];
        //this.headers = new String[graphCount];

        setPreferredSize(new Dimension(1024, 768));
        

        createPanel();

        pack();
        setVisible(true);
    }
    
    private void createPanel(){
        
//        JPanel backgroundPanel = (JPanel)this.getContentPane();
//        backgroundPanel.setBackground(Color.white);
        
        setLayout(new BorderLayout());
        plotpanel = new JPanel();
        plotpanel.setBackground(Color.WHITE);

        optionpanel = new JPanel();
        chooserpanel = new JPanel();
        
        GridBagLayout gbl = new GridBagLayout();
        chooserpanel.setLayout(gbl);
        ogbl = new GridBagLayout();
        optionpanel.setLayout(ogbl);
        
        
        plotbutton = new JButton("PLOT");
        epsButton = new JButton("EPS Export");
        settitleButton = new JButton("set");
        addbutton = new JButton("Add Plot");
        removebutton = new JButton("Remove Plot");
        titleField = new JTextField();
        titleField.setSize(50, 10);
        titleField.setText("Stacked Time Plot");
        edTitleLabel = new JLabel("set Title: ");
        
        // PROGRAMME //
        buildOptionPanel();
//        optionpanel.add(plotbutton);
        
        XYPlot[] xyplots = new XYPlot[numberOfPlots];
        DateAxis dateAxis = new DateAxis();
        
        jts = new JAMSTimePlot[numberOfPlots];
        weights = new int[numberOfPlots];
        
        for(int i = 0; i < numberOfPlots; i++){
            String datasetFileID = (String)templateBox[i].getSelectedItem();
            templateFiles[i] = new File(workspace.getInputDirectory(), datasetFileID);

            loadInputDSData(loadDatasetID(templateFiles[i]));
            loadTemplate(templateFiles[i]);
            
            jts[i] = new JAMSTimePlot();
            jts[i].setPropVector(propVector);
            jts[i].createPlot();
            jts[i].setTitle(title);
            try{
               weights[i] = new Integer(weightField[i].getText());
            }catch(NumberFormatException nfe){
                weights[i] = 1;
                weightField[i].setText("1");
            }
            titleLabel[i].setText(title);
//            jts[i].getPanel().add(templateBox[i]);
            
            plot(i);
            xyplots[i] = jts[i].getXYPlot();
            //last date axis
            if(timeButton[i].isSelected()) dateAxis = jts[i].getDateAxis();
            
        }
        
        title = titleField.getText();
        stackedplot = new JAMSStackedPlot(xyplots, weights, dateAxis, title);
        
        chartpanel = stackedplot.getChartPanel();
        
        add(chartpanel, BorderLayout.CENTER);
        add(optionpanel, BorderLayout.SOUTH);
        
        repaint();
    }
    
    private void buildOptionPanel(){
        
        templateFiles = new File[numberOfPlots];
        templateBox = new JComboBox[numberOfPlots];
        timeButton = new JRadioButton[numberOfPlots];
        titleLabel = new JLabel[numberOfPlots];
        weightField = new JTextField[numberOfPlots];
        axisGroup = new ButtonGroup();
        
        plotbutton.addActionListener(plotaction);
        titleField.addActionListener(titleListener);
        settitleButton.addActionListener(titleListener);
        epsButton.addActionListener(saveImageAction);
        addbutton.addActionListener(addAction);
        removebutton.addActionListener(removeAction);
        dataset = getAccessibleIDs();
        
        //create optionpanel GUI
//        optionpanel.add(edTitleLabel);
//        optionpanel.add(titleField);
//        optionpanel.add(settitleButton);
//        optionpanel.add(new JLabel("  "));
        
        LHelper.addGBComponent(optionpanel, ogbl, new JLabel("Weight"), 0, 0, 1, 1, 0, 0);
        LHelper.addGBComponent(optionpanel, ogbl, new JLabel("Template"), 1, 0, 1, 1, 0, 0);
        LHelper.addGBComponent(optionpanel, ogbl, new JLabel("Time Axis"), 2, 0, 1, 1, 0, 0);
        LHelper.addGBComponent(optionpanel, ogbl, addbutton,    0, numberOfPlots+1, 1, 1, 0, 0);
        LHelper.addGBComponent(optionpanel, ogbl, removebutton, 1, numberOfPlots+1, 1, 1, 0, 0);
        LHelper.addGBComponent(optionpanel, ogbl, edTitleLabel, 0, numberOfPlots+2, 1, 1, 0, 0);
        LHelper.addGBComponent(optionpanel, ogbl, titleField,   1, numberOfPlots+2, 1, 1, 0, 0);
        LHelper.addGBComponent(optionpanel, ogbl, settitleButton,   2, numberOfPlots+2, 1, 1, 0, 0);
        LHelper.addGBComponent(optionpanel, ogbl, plotbutton,   4, numberOfPlots+2, 1, 1, 0, 0);
        LHelper.addGBComponent(optionpanel, ogbl, epsButton,    6, numberOfPlots+2, 1, 1, 0, 0);
          
        
        for(int c = 0; c < numberOfPlots; c++){

            
            templateBox[c] = new JComboBox(dataset);
            timeButton[c] = new JRadioButton();
            weightField[c] = new JTextField("1");
            axisGroup.add(timeButton[c]);
            timeButton[c].setSelected(true);
            selectedTimeAxis = c;
            titleLabel[c] = new JLabel("");
//            optionpanel.add(templateBox[c]);
            templateBox[c].setSelectedIndex(c);
            LHelper.addGBComponent(optionpanel, ogbl, weightField[c], 0, c+1, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, templateBox[c], 1, c+1, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, titleLabel[c], 2, c+1, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, timeButton[c], 3, c+1, 1, 1, 0, 0);
        }
    }
    
    private void addPlot(){
        
        dataset = getAccessibleIDs();
       
        if(numberOfPlots <dataset.length ){

            int[] selectedTemplates = new int[numberOfPlots];
            
            for(int k = 0; k < numberOfPlots; k++){
                selectedTemplates[k] = templateBox[k].getSelectedIndex();
            }
            numberOfPlots++;
            remove(optionpanel);
            optionpanel = new JPanel();
            ogbl = new GridBagLayout();
            optionpanel.setLayout(ogbl);
            
            templateFiles = new File[numberOfPlots];
            templateBox = new JComboBox[numberOfPlots];
            timeButton = new JRadioButton[numberOfPlots];
            weightField = new JTextField[numberOfPlots];
            titleLabel = new JLabel[numberOfPlots];
            axisGroup = new ButtonGroup();

            plotbutton.addActionListener(plotaction);
            titleField.addActionListener(titleListener);
            settitleButton.addActionListener(titleListener);
            epsButton.addActionListener(saveImageAction);
            addbutton.addActionListener(addAction);
            removebutton.addActionListener(removeAction);
            dataset = getAccessibleIDs();

            LHelper.addGBComponent(optionpanel, ogbl, new JLabel("Template"), 0, 0, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, new JLabel("Time Axis"), 2, 0, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, addbutton, 0, numberOfPlots+1, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, removebutton, 1, numberOfPlots+1, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, edTitleLabel, 0, numberOfPlots+2, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, titleField,   1, numberOfPlots+2, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, settitleButton,   2, numberOfPlots+2, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, plotbutton,   4, numberOfPlots+2, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, epsButton,   6, numberOfPlots+2, 1, 1, 0, 0);


            for(int c = 0; c < numberOfPlots; c++){

                templateBox[c] = new JComboBox(dataset);
                timeButton[c] = new JRadioButton();
                weightField[c] = new JTextField("1");
                axisGroup.add(timeButton[c]);
                if(c == selectedTimeAxis) timeButton[c].setSelected(true);
                titleLabel[c] = new JLabel("");
    //            optionpanel.add(templateBox[c]);
                if(c<selectedTemplates.length) templateBox[c].setSelectedIndex(selectedTemplates[c]);
                
                LHelper.addGBComponent(optionpanel, ogbl, weightField[c], 0, c+1, 1, 1, 0, 0);
                LHelper.addGBComponent(optionpanel, ogbl, templateBox[c], 1, c+1, 1, 1, 0, 0);
                LHelper.addGBComponent(optionpanel, ogbl, titleLabel[c], 2, c+1, 1, 1, 0, 0);
                LHelper.addGBComponent(optionpanel, ogbl, timeButton[c], 3, c+1, 1, 1, 0, 0);
            }
            
            repaintPlotPanel();
            
        } else {
            /* Not enough Templates */
        }
        
    }
    
    private void removePlot(){
        
        dataset = getAccessibleIDs();
       
        if(numberOfPlots <dataset.length ){

            int[] selectedTemplates = new int[numberOfPlots];
            
            for(int k = 0; k < numberOfPlots; k++){
                selectedTemplates[k] = templateBox[k].getSelectedIndex();
            }
            numberOfPlots--;
            remove(optionpanel);
            optionpanel = new JPanel();
            ogbl = new GridBagLayout();
            optionpanel.setLayout(ogbl);
            
            templateFiles = new File[numberOfPlots];
            templateBox = new JComboBox[numberOfPlots];
            timeButton = new JRadioButton[numberOfPlots];
            titleLabel = new JLabel[numberOfPlots];
            axisGroup = new ButtonGroup();

            plotbutton.addActionListener(plotaction);
            titleField.addActionListener(titleListener);
            settitleButton.addActionListener(titleListener);
            epsButton.addActionListener(saveImageAction);
            addbutton.addActionListener(addAction);
            removebutton.addActionListener(removeAction);
            dataset = getAccessibleIDs();

            LHelper.addGBComponent(optionpanel, ogbl, new JLabel("Template"), 0, 0, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, new JLabel("Time Axis"), 2, 0, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, addbutton, 0, numberOfPlots+1, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, removebutton, 1, numberOfPlots+1, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, edTitleLabel, 0, numberOfPlots+2, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, titleField,   1, numberOfPlots+2, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, settitleButton,   2, numberOfPlots+2, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, plotbutton,   4, numberOfPlots+2, 1, 1, 0, 0);
            LHelper.addGBComponent(optionpanel, ogbl, epsButton,   6, numberOfPlots+2, 1, 1, 0, 0);


            for(int c = 0; c < numberOfPlots; c++){

                templateBox[c] = new JComboBox(dataset);
                timeButton[c] = new JRadioButton();
                axisGroup.add(timeButton[c]);
                if(c == selectedTimeAxis) timeButton[c].setSelected(true);
                titleLabel[c] = new JLabel("");
    //            optionpanel.add(templateBox[c]);
                if(c<selectedTemplates.length) templateBox[c].setSelectedIndex(selectedTemplates[c]);
                
                LHelper.addGBComponent(optionpanel, ogbl, templateBox[c],   0, c+1, 1, 1, 0, 0);
                LHelper.addGBComponent(optionpanel, ogbl, titleLabel[c],    1, c+1, 1, 1, 0, 0);
                LHelper.addGBComponent(optionpanel, ogbl, timeButton[c],    2, c+1, 1, 1, 0, 0);
            }
            
            repaintPlotPanel();
            
        } else {
            /* Not enough Templates */
        }
        
    }
    
    private void repaintPlotPanel(){
        
        this.remove(chartpanel);

        XYPlot[] xyplots = new XYPlot[numberOfPlots];
        DateAxis dateAxis = new DateAxis();
        
        jts = new JAMSTimePlot[numberOfPlots];
        weights = new int[numberOfPlots];
        
        for(int i = 0; i < numberOfPlots; i++){
            
            String datasetFileID = (String)templateBox[i].getSelectedItem();
            templateFiles[i] = new File(workspace.getInputDirectory(), datasetFileID);
            
            
            loadInputDSData(loadDatasetID(templateFiles[i]));
            loadTemplate(templateFiles[i]);
            
            jts[i] = new JAMSTimePlot();
            jts[i].setPropVector(propVector);
            
            jts[i].createPlot();
            jts[i].setTitle(title);
            try{
                weights[i] = new Integer(weightField[i].getText());
            }catch(NumberFormatException nfe){
                weights[i] = 1;
                weightField[i].setText("1");
            }
            titleLabel[i].setText(title);
            
            plot(i);
            xyplots[i] = jts[i].getXYPlot();
            //last date axis
            if(timeButton[i].isSelected()){
                
                selectedTimeAxis = i;
                dateAxis = jts[i].getDateAxis();
            }

        }
        
        title = titleField.getText();
        stackedplot = new JAMSStackedPlot(xyplots, weights, dateAxis, title);
        
        chartpanel = stackedplot.getChartPanel();
        
        add(chartpanel, BorderLayout.CENTER);
        add(optionpanel, BorderLayout.SOUTH);
        pack();
        //repaint();
        
    }
   
    private InputDataStore getInputDataStore(String datasetID){
        
        InputDataStore store = workspace.getInputDataStore(datasetID);
        
        return store;
    }
    
    
    
    private String[] getAccessibleIDs(){
        

        int accessibleIDs = 0;
        int failedIDs = 0;
        ArrayList<String> accIDList = new ArrayList<String>();
        String[] accIDArray;
        
//        File testfile = new File());
        File testfile = new File(workspace.getDirectory().toString()+"/input");
        File[] filelist = testfile.listFiles();

        Set<String> idSet;
//        idSet = Regionalizer.getRegionalizerFrame().getWorkspace().getInputDataStoreIDs();
//        totalIDs = idSet.size();
        
        //idArray = idSet.toArray(idArray);
        
        for(int i = 0; i < filelist.length; i++){
            
            String name = filelist[i].getName();
            if(name.indexOf(".ttp")!=-1){
                accIDList.add(name);
            }
                
                
            
//            File testFile = new File(Regionalizer.getRegionalizerFrame().getWorkspace().getInputDirectory(), idArray[i] +".ttp");
//            if(testFile.exists()) accIDList.add(idArray[i]);
//            else failedIDs++;
////            try {
//                FileInputStream fin = new FileInputStream(testFile);
//                fin.close();
//                accIDList.add(idArray[i]);
//            }   catch (IOException ioe) {
//                    failedIDs++;
//            }
            
        }
        
        accessibleIDs = accIDList.size();
        //String idArray[] = new String[accessibleIDs];
        accIDArray = new String[accessibleIDs];
        accIDArray = accIDList.toArray(accIDArray);
       
        return accIDArray;
    }
    
    private String loadDatasetID(File templateFile){
        Properties properties = new Properties();
        try {
            FileInputStream fin = new FileInputStream(templateFile);
            properties.load(fin);
            fin.close();
        } catch (Exception e) {
        }
        String id = (String) properties.getProperty("store");

        return id;
    }
    
    private void loadInputDSData(String datasetID){
        
        arrayVector = new Vector<double[]>();
        timeVector = new Vector<JAMSCalendar>();
        
        double rowBuffer[];
        this.store = getInputDataStore(datasetID);

        ArrayList<Object> names = store.getDataSetDefinition().getAttributeValues("NAME");
        columns = store.getDataSetDefinition().getColumnCount();
        headers = new String[columns + 1];
        headers[0] = "";
        int i = 1;
        for (Object o : names) {
            headers[i++] = (String) o;
        }

        // read table values from store
        while (store.hasNext()) {
            DataSet ds = store.getNext();

            DataValue[] rowData = ds.getData();

            JAMSCalendar timeval = JAMSDataFactory.createCalendar();
            try {
                timeval.setValue(rowData[0].getString(), "dd.MM.yyyy HH:mm");
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
            timeVector.add(timeval);

            rowBuffer = new double[columns];
            for (i = 1; i < rowData.length; i++) {
                rowBuffer[i - 1] = ((DoubleValue) rowData[i]).getDouble();
            }
            arrayVector.add(rowBuffer);
        }
        rows = arrayVector.size();
    }
    
    private void loadTimeIntervals(File[] templateFiles){
            
            String timeSTART = "";
            String timeEND = "";
            String names ="";
            String name = "";
            
            int no_of_props;
            boolean loadProp = false;
            
            Properties properties = new Properties();
            
            for(int i=0; i<numberOfPlots; i++){

                try {
                    FileInputStream fin = new FileInputStream(templateFiles[i]);
                    properties.load(fin);
                    fin.close();
                } catch (Exception e) {
                }
                
                names = properties.getProperty("names");
                no_of_props = new Integer(properties.getProperty("number"));
                StringTokenizer nameTokenizer = new StringTokenizer(names, ",");
                
                for(int c=0; c<no_of_props; c++){
                    if (nameTokenizer.hasMoreTokens()) {

                        name = nameTokenizer.nextToken();

                        for (int k = 0; k < columns; k++) {
                            if (headers[k].compareTo(name) == 0) { //stringcompare?

                                loadProp = true;
                                break;
                            }
                        }
                        if(loadProp){
                            if(i == 0){
                                timeSTART = (String)properties.getProperty(name + ".timeSTART");
                                timeEND = (String)properties.getProperty(name + ".timeEND");
                            } else {
                                String read_tStart = (String)properties.getProperty(name + ".timeSTART");
                                if(read_tStart.compareTo(timeSTART) < 0) timeSTART = read_tStart;
                                String read_tEnd = (String)properties.getProperty(name + ".timeEND");
                                if(read_tEnd.compareTo(timeEND) > 0) timeEND = read_tEnd;
                            }
                        }
                    }
                }
        }
    }
    //attention: only for ONE tempFile
    private void loadTemplate(File templateFile) {
            
    Properties properties = new Properties();
        boolean load_prop = false;

        String names;
        String name;
        String stroke_color;
        String shape_color;
        String outline_color;
        int no_of_props;
        int returnVal = -1;

        try {
            FileInputStream fin = new FileInputStream(templateFile);
            properties.load(fin);
            fin.close();
        } catch (Exception e) {
        }

        this.propVector = new Vector<GraphProperties>();
        
        datasetID = (String) properties.getProperty("store");
        names = (String) properties.getProperty("names");

        no_of_props = new Integer(properties.getProperty("number"));

        this.graphCount = no_of_props;

        StringTokenizer nameTokenizer = new StringTokenizer(names, ",");

        for (int i = 0; i < no_of_props; i++) {

            load_prop = false;
            GraphProperties gprop = new GraphProperties(this);

            if (nameTokenizer.hasMoreTokens()) {

                name = nameTokenizer.nextToken();

                for (int k = 0; k < columns; k++) {
                    if (headers[k+1].compareTo(name) == 0) {

                        gprop.setSelectedColumn(k);
                        load_prop = true;
                        break;
                    }
                }

                boolean readStart = false, readEnd = false;
                gprop.setTimeSTART(0);
                gprop.setTimeEND(rows - 1);

                if (load_prop) {
                    //Legend Name
                    gprop.setLegendName(properties.getProperty(name + ".legendname", "legend name"));
                    //POSITION left/right
                    gprop.setPosition(properties.getProperty(name + ".position"));
                    //INTERVAL
                    String timeSTART = properties.getProperty(name + ".timeSTART");
                    String timeEND = properties.getProperty(name + ".timeEND");
                    String read = null;

                    for (int tc = 0; tc < rows; tc++) {

                        if (readStart && readEnd) {
                            break;
                        }

                        read = gprop.getTimeChoiceSTART().getItemAt(tc).toString();

                        if (!readStart) {
                            //start
                            if (read.equals(timeSTART)) {
                                gprop.setTimeSTART(tc);
                                readStart = true;
                            }
                        } else {
                            //end
                            if (read.equals(timeEND)) {
                                gprop.setTimeEND(tc);
                                readEnd = true;
                            }
                        }
                    }


//                    gprop.setTimeSTART(0);
//                    gprop.setTimeEND(table.getRowCount() - 1);

                    //NAME
                    gprop.setName(name);

                    //STROKE
                    gprop.setStroke(new Integer(properties.getProperty(name + ".linestroke", "2")));
                    gprop.setStrokeSlider(gprop.getStrokeType());

                    //STROKE COLOR
                    stroke_color = properties.getProperty(name + ".linecolor", "255,0,0");

                    StringTokenizer colorTokenizer = new StringTokenizer(stroke_color, ",");

                    gprop.setSeriesPaint(new Color(new Integer(colorTokenizer.nextToken()),
                            new Integer(colorTokenizer.nextToken()),
                            new Integer(colorTokenizer.nextToken())));

                    //LINES VISIBLE
                    boolean lv = new Boolean(properties.getProperty(name + ".linesvisible"));
                    gprop.setLinesVisible(lv);
                    gprop.setLinesVisBox(lv);
                    //SHAPES VISIBLE
                    boolean sv = new Boolean(properties.getProperty(name + ".shapesvisible"));
                    gprop.setShapesVisible(sv);
                    gprop.setShapesVisBox(sv);

                    //SHAPE TYPE AND SIZE
                    int stype = new Integer(properties.getProperty(name + ".shapetype", "0"));
                    int ssize = new Integer(properties.getProperty(name + ".shapesize"));
                    gprop.setShape(stype, ssize);
                    gprop.setShapeBox(stype);
                    gprop.setShapeSlider(ssize);

                    //SHAPE COLOR
                    shape_color = properties.getProperty(name + ".shapecolor", "255,0,0");

                    StringTokenizer shapeTokenizer = new StringTokenizer(shape_color, ",");

                    gprop.setSeriesFillPaint(new Color(new Integer(shapeTokenizer.nextToken()),
                            new Integer(shapeTokenizer.nextToken()),
                            new Integer(shapeTokenizer.nextToken())));

                    //OUTLINE STROKE
                    int os = new Integer(properties.getProperty(name + ".outlinestroke"));
                    gprop.setOutlineStroke(os);
                    gprop.setOutlineSlider(os);

                    //OUTLINE COLOR
                    outline_color = properties.getProperty(name + ".outlinecolor", "255,0,0");

                    StringTokenizer outTokenizer = new StringTokenizer(outline_color, ",");

                    gprop.setSeriesOutlinePaint(new Color(new Integer(outTokenizer.nextToken()),
                            new Integer(outTokenizer.nextToken()),
                            new Integer(outTokenizer.nextToken())));

                    gprop.setColorLabelColor();
                    propVector.add(gprop);
                    //addPropGroup(gprop);
                }
            }
        }

        //////////////// hier implementieren!! /////////////////////////
        //}
        //Titles
        title = (String) properties.getProperty("title");
        tLeft = (String) properties.getProperty("axisLTitle");
        tRight = (String)properties.getProperty("axisRTitle");
        xAxisTitle = (String) properties.getProperty("xAxisTitle");
        //RENDERER
        rLeft = new Integer(properties.getProperty("renderer_left"));
        rRight = new Integer(properties.getProperty("renderer_right"));
        invLeft = new Boolean(properties.getProperty("inv_left"));
        invRight = new Boolean(properties.getProperty("inv_right"));

        //TimeFormat
        timeFormat_yy = new Boolean(properties.getProperty("timeFormat_yy"));
        timeFormat_mm = new Boolean(properties.getProperty("timeFormat_mmy"));
        timeFormat_dd = new Boolean(properties.getProperty("timeFormat_dd"));
        timeFormat_hm = new Boolean(properties.getProperty("timeFormat_hm"));

//        jts.setPropVector(propVector);



    }
    
    private void updatePropVector() {

        for (int i = 0; i < propVector.size(); i++) {
            propVector.get(i).applySTPProperties(arrayVector, timeVector);
        }
    }
    
    public void plot(int plot_index) {

        final int index = plot_index;

        Runnable r = new Runnable() {
            
            
            @Override
            public void run() {

                updatePropVector();

                int l = 0;
                int r = 0;

                XYItemRenderer rendererLeft = new XYLineAndShapeRenderer();
                XYItemRenderer rendererRight = new XYLineAndShapeRenderer();

                XYLineAndShapeRenderer lsr_R = new XYLineAndShapeRenderer();
                XYBarRenderer brr_R = new XYBarRenderer();
                XYDifferenceRenderer dfr_R = new XYDifferenceRenderer();
                XYAreaRenderer ar_R = new XYAreaRenderer();
                XYStepRenderer str_R = new XYStepRenderer();
                XYStepAreaRenderer sar_R = new XYStepAreaRenderer();

                XYLineAndShapeRenderer lsr_L = new XYLineAndShapeRenderer();
                XYBarRenderer brr_L = new XYBarRenderer();
                XYDifferenceRenderer dfr_L = new XYDifferenceRenderer();
                XYAreaRenderer ar_L = new XYAreaRenderer();
                XYStepRenderer str_L = new XYStepRenderer();
                XYStepAreaRenderer sar_L = new XYStepAreaRenderer();

                GraphProperties prop;
                //2 Renderer einfÃƒÆ’Ã‚Â¼gen. Typ aus rLeftBox bzw rRightBox holen!
                //Switch/Case Anweisung in den Configurator packen
                //

                /////////////// In dieser Schleife Eigenschaften ÃƒÆ’Ã‚Â¼bernehmen!! /////////////
                for (int i = 0; i < propVector.size(); i++) {

                    prop = propVector.get(i);

//                prop.setLegendName((String)prop.setColumn.getSelectedItem());
//                prop.setName((String)prop.setColumn.getSelectedItem());

                    if (prop.getPosChoice().getSelectedItem() == "left") {
                        l++;
                        //prop.setRendererType(rLeft);

                        switch (rLeft) {

                            case 0:
                                lsr_L.setSeriesPaint(i - r, prop.getSeriesPaint());
                                //lsr_L.setSeriesPaint(i-r, Color.black);
                                lsr_L.setSeriesStroke(i - r, prop.getSeriesStroke());
                                lsr_L.setSeriesShape(i - r, prop.getSeriesShape());
                                lsr_L.setSeriesShapesVisible(i - r, prop.getShapesVisible());
                                lsr_L.setSeriesLinesVisible(i - r, prop.getLinesVisible());
                                //lsr_L.setDrawOutlines(prop.getOutlineVisible());
                                lsr_L.setUseOutlinePaint(true);
                                lsr_L.setSeriesFillPaint(i - r, prop.getSeriesFillPaint());
                                lsr_L.setUseFillPaint(true);
                                lsr_L.setSeriesOutlineStroke(i - r, prop.getSeriesOutlineStroke());
                                lsr_L.setSeriesOutlinePaint(i - r, prop.getSeriesOutlinePaint());
                                rendererLeft = lsr_L;
                                break;

                            case 1:
                                brr_L.setSeriesPaint(i - r, prop.getSeriesPaint());
                                brr_L.setSeriesStroke(i - r, prop.getSeriesStroke());

                                brr_L.setSeriesOutlineStroke(i - r, prop.getSeriesOutlineStroke());
                                brr_L.setSeriesOutlinePaint(i - r, prop.getSeriesOutlinePaint());


                                rendererLeft = brr_L;
                                //set Margin
                                break;

                            case 2:
                                ar_L.setSeriesPaint(i - r, prop.getSeriesPaint());
                                ar_L.setSeriesStroke(i - r, prop.getSeriesStroke());
                                ar_L.setSeriesShape(i - r, prop.getSeriesShape());
                                ar_L.setSeriesOutlineStroke(i - r, prop.getSeriesOutlineStroke());
                                ar_L.setSeriesOutlinePaint(i - r, prop.getSeriesOutlinePaint());
                                ar_L.setOutline(prop.getOutlineVisible());
                                //ar_L.setSeriesOu

                                rendererLeft = ar_L;

                                break;

                            case 3:
                                str_L.setSeriesPaint(i - r, prop.getSeriesPaint());
                                str_L.setSeriesStroke(i - r, prop.getSeriesStroke());
                                str_L.setSeriesShape(i - r, prop.getSeriesShape());
//                            str_L.setSeriesOutlineStroke(i-r, prop.getSeriesOutlineStroke());
//                            str_L.setSeriesOutlinePaint(i-r, prop.getSeriesOutlinePaint());

                                rendererLeft = str_L;
                                break;

                            case 4:
                                sar_L.setSeriesPaint(i - r, prop.getSeriesPaint());
                                sar_L.setSeriesStroke(i - r, prop.getSeriesStroke());
                                sar_L.setSeriesShape(i - r, prop.getSeriesShape());
                                sar_L.setSeriesOutlineStroke(i - r, prop.getSeriesOutlineStroke());
                                sar_L.setSeriesOutlinePaint(i - r, prop.getSeriesOutlinePaint());
                                sar_L.setOutline(prop.getOutlineVisible());

                                rendererLeft = sar_L;

                                break;

                            case 5:
                                dfr_L.setSeriesPaint(i - r, prop.getSeriesPaint());
                                dfr_L.setSeriesStroke(i - r, prop.getSeriesStroke());
                                dfr_L.setSeriesShape(i - r, prop.getSeriesShape());
                                dfr_L.setSeriesOutlineStroke(i - r, prop.getSeriesOutlineStroke());
                                dfr_L.setSeriesOutlinePaint(i - r, prop.getSeriesOutlinePaint());
                                dfr_L.setShapesVisible(prop.getShapesVisible());


//                            dfr_L.setNegativePaint(prop.getNegativePaint());
//                            dfr_L.setPositivePaint(prop.getNegativePaint());

                                rendererLeft = dfr_L;

                                break;

                            default:
                                lsr_L.setSeriesPaint(i - r, prop.getSeriesPaint());
                                lsr_L.setSeriesStroke(i - r, prop.getSeriesStroke());
                                lsr_L.setSeriesShape(i - r, prop.getSeriesShape());
                                lsr_L.setSeriesShapesVisible(i - r, prop.getShapesVisible());
                                lsr_L.setSeriesLinesVisible(i - r, prop.getLinesVisible());
                                lsr_L.setSeriesOutlineStroke(i - r, prop.getSeriesOutlineStroke());
                                lsr_L.setSeriesOutlinePaint(i - r, prop.getSeriesOutlinePaint());

                                rendererLeft = lsr_L;
                                break;
                        }

                    }
                    if (prop.getPosChoice().getSelectedItem() == "right") {
                        r++;
                        //prop.setRendererType(rRight);
                        switch (rRight) {
                            case 0:
                                lsr_R.setSeriesPaint(i - l, prop.getSeriesPaint());
                                lsr_R.setSeriesStroke(i - l, prop.getSeriesStroke());
                                lsr_R.setSeriesShape(i - l, prop.getSeriesShape());
                                lsr_R.setSeriesShapesVisible(i - l, prop.getShapesVisible());
                                lsr_R.setSeriesLinesVisible(i - l, prop.getLinesVisible());
                                //lsr_R.setDrawOutlines(prop.getOutlineVisible());
                                lsr_R.setUseOutlinePaint(true);
                                lsr_R.setSeriesFillPaint(i - l, prop.getSeriesFillPaint());
                                lsr_R.setUseFillPaint(true);
                                lsr_R.setSeriesOutlineStroke(i - l, prop.getSeriesOutlineStroke());
                                lsr_R.setSeriesOutlinePaint(i - l, prop.getSeriesOutlinePaint());

                                rendererRight = lsr_R;
                                break;

                            case 1:
                                brr_R.setSeriesPaint(i - l, prop.getSeriesPaint());
                                brr_R.setSeriesStroke(i - l, prop.getSeriesStroke());
                                brr_R.setSeriesOutlineStroke(i - l, prop.getSeriesOutlineStroke());
                                brr_R.setSeriesOutlinePaint(i - l, prop.getSeriesOutlinePaint());

                                rendererRight = brr_R;
                                //set Margin
                                break;

                            case 2:
                                ar_R.setSeriesPaint(i - l, prop.getSeriesPaint());
                                ar_R.setSeriesStroke(i - l, prop.getSeriesStroke());
                                ar_R.setSeriesShape(i - l, prop.getSeriesShape());
                                ar_R.setSeriesOutlineStroke(i - l, prop.getSeriesOutlineStroke());
                                ar_R.setSeriesOutlinePaint(i - l, prop.getSeriesOutlinePaint());

                                rendererRight = ar_R;

                                break;

                            case 3:
                                str_R.setSeriesPaint(i - l, prop.getSeriesPaint());
                                str_R.setSeriesStroke(i - l, prop.getSeriesStroke());
                                str_R.setSeriesShape(i - l, prop.getSeriesShape());
                                str_R.setSeriesOutlineStroke(i - l, prop.getSeriesOutlineStroke());
                                str_R.setSeriesOutlinePaint(i - l, prop.getSeriesOutlinePaint());

                                rendererRight = str_R;

                                break;

                            case 4:
                                sar_R.setSeriesPaint(i - l, prop.getSeriesPaint());
                                sar_R.setSeriesStroke(i - l, prop.getSeriesStroke());
                                sar_R.setSeriesShape(i - l, prop.getSeriesShape());
                                sar_R.setSeriesOutlineStroke(i - l, prop.getSeriesOutlineStroke());
                                sar_R.setSeriesOutlinePaint(i - l, prop.getSeriesOutlinePaint());

                                rendererRight = sar_R;

                                break;

                            case 5:
                                dfr_R.setSeriesPaint(i - l, prop.getSeriesPaint());
                                dfr_R.setSeriesStroke(i - l, prop.getSeriesStroke());
                                dfr_R.setSeriesShape(i - l, prop.getSeriesShape());
                                dfr_R.setSeriesOutlineStroke(i - l, prop.getSeriesOutlineStroke());
                                dfr_R.setSeriesOutlinePaint(i - l, prop.getSeriesOutlinePaint());
                                dfr_R.setShapesVisible(prop.getShapesVisible());
                                rendererRight = dfr_R;

                                break;

                            default:
                                lsr_R.setSeriesPaint(i - l, prop.getSeriesPaint());
                                lsr_R.setSeriesStroke(i - l, prop.getSeriesStroke());
                                lsr_R.setSeriesShape(i - l, prop.getSeriesShape());
                                lsr_R.setSeriesShapesVisible(i - l, prop.getShapesVisible());
                                lsr_R.setSeriesLinesVisible(i - l, prop.getLinesVisible());
                                lsr_R.setSeriesOutlineStroke(i - l, prop.getSeriesOutlineStroke());
                                lsr_R.setSeriesOutlinePaint(i - l, prop.getSeriesOutlinePaint());

                                rendererRight = lsr_R;
                                break;
                        }

                        prop.setLegendName(prop.setLegend.getText());
                        prop.setColorLabelColor();
                        prop.applySTPProperties(arrayVector, timeVector);
                        
                    }
                }

                ////////////////////////////////////////////////////////////////////////////
                //Renderer direkt ÃƒÆ’Ã‚Â¼bernehmen! //
                
                if (l > 0) {
                    jts[index].plotLeft(rendererLeft, tLeft, xAxisTitle, invLeft);
                }
                if (r > 0) {
                    jts[index].plotRight(rendererRight, tRight, xAxisTitle, invRight);
                }
                if (r == 0 && l == 0) {
                    jts[index].plotEmpty();
                }

                jts[index].setTitle(title);
                jts[index].setDateFormat(timeFormat_yy, timeFormat_mm,
                        timeFormat_dd, timeFormat_hm);

            }
        };

        WorkerDlg dlg = new WorkerDlg(parent, "Creating Plot...");
//        Point parentloc = parent.getLocation();
//        dlg.setLocation(parentloc.x + 30, parentloc.y + 30);
//        dlg.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2,
//                Toolkit.getDefaultToolkit().getScreenSize().height / 2);
        dlg.setTask(r);
        dlg.execute();
        
        //repaint();
        
    }
    
    
    
    public int getRowCount(){
        return rows;
    }
    
    public int getColumnCount(){
        return columns;
    }
    
    public String[] getHeaders(){
        return headers;
    }
    
    public Vector<double[]> getArrayVector(){
        return arrayVector;
    }
    
    ActionListener plotaction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            repaintPlotPanel();
            setVisible(true);
        }
    };
    
    ActionListener titleListener = new ActionListener() {

        public void actionPerformed(ActionEvent te) {
            title = titleField.getText();
            stackedplot.setTitle(title);
        }
    };
    
     ActionListener saveImageAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {

//            showHiRes();
            try {
                JFileChooser chooser = new JFileChooser();
                int returnVal = chooser.showSaveDialog(parent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    stackedplot.saveAsEPS(file);
                }
            } catch (Exception ex) {
            }
        }
    };
    
    ActionListener addAction = new ActionListener() {

        public void actionPerformed(ActionEvent te) {
            
            addPlot();
        }
    };
   
    ActionListener removeAction = new ActionListener() {

        public void actionPerformed(ActionEvent te) {
            
            removePlot();
        }
    };

}