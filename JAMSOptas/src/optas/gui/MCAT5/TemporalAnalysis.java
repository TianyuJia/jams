/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package optas.gui.MCAT5;

import jams.data.JAMSCalendar;
import jams.gui.ObserverWorkerDlg;
import jams.gui.tools.GUIHelper;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import optas.gui.HydrographChart;
import optas.hydro.OptimizationScheme;
import optas.SA.TemporalSensitivityAnalysis;
import optas.SA.VarianceBasedTemporalSensitivityAnalysis;
import optas.hydro.GreedyOptimizationScheme;
import optas.hydro.GreedyOptimizationScheme1;
import optas.hydro.OptimalOptimizationScheme;
import optas.hydro.SimilarityBasedOptimizationScheme;
import optas.hydro.data.DataCollection;
import optas.hydro.data.DataSet;
import optas.hydro.data.Efficiency;
import optas.hydro.data.EfficiencyEnsemble;
import optas.hydro.data.Measurement;
import optas.hydro.data.Parameter;
import optas.hydro.data.SimpleEnsemble;
import optas.hydro.data.TimeSerie;
import optas.hydro.data.TimeSerieEnsemble;
import optas.hydro.gui.WeightChart;
import optas.metamodel.OptimizationDescriptionDocument;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author chris
 */
public class TemporalAnalysis extends MCAT5Plot {

    int MAX_WEIGHTS = 40;

    public static Color[] getDifferentColors(int n) {
        Color[] cols = new Color[n];

        for (int i = 0; i < n; i++) {
            cols[i] = Color.getHSBColor((float) ((113 * i) % n) / (float) n, (float) ((223 * i) % n) / (float) n, 1.0f);
        }
        return cols;
    }
    Color standardColorList[] = getDifferentColors(MAX_WEIGHTS);
    HydrographChart chart;
    WeightChart weightChart;
    JComboBox SAMethods = new JComboBox();
    JComboBox optimizationSchemes = new JComboBox();    
    JComboBox parameterGroups = new JComboBox();
    String parameterIDs[] = null;
    XYLineAndShapeRenderer weightRenderer[] = new XYLineAndShapeRenderer[MAX_WEIGHTS];
    JButton calcSA = new JButton("Calculate Temporal Sensitivity");
    JButton calcScheme = new JButton("Calculate Optimization Scheme");
    JButton exportScheme = new JButton("Export Scheme");
    JTable parameterTable = new JTable(new Object[][]{{Boolean.TRUE, Boolean.TRUE, "test", Color.black}}, new String[]{"x", "y", "z", "a"});
    JLabel infoLabel = new JLabel("Dominance:?");
    SimpleEnsemble p[] = null;
    EfficiencyEnsemble e = null;
    TimeSerieEnsemble ts = null;
    Measurement obs = null;    
    JPanel mainPanel = null;

    public TemporalAnalysis() {
        this.addRequest(new SimpleRequest(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("SIMULATED_TIMESERIE"), TimeSerie.class));
        this.addRequest(new SimpleRequest(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("OBSERVED_TIMESERIE"), Measurement.class));
        this.addRequest(new SimpleRequest(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("Efficiency"), Efficiency.class));

        optimizationSchemes.setModel(new DefaultComboBoxModel(new Object[]{
                    new GreedyOptimizationScheme1(),
                    new GreedyOptimizationScheme(),
                    new OptimalOptimizationScheme(),
                    new SimilarityBasedOptimizationScheme()
                }));

        calcScheme.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                calcOptimizationScheme((OptimizationScheme) optimizationSchemes.getSelectedItem());
            }
        });

        calcSA.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final TemporalSensitivityAnalysis tsa = (TemporalSensitivityAnalysis) SAMethods.getSelectedItem();

                ObserverWorkerDlg progress = new ObserverWorkerDlg(null, "Calculating Sensitivity Indicies");
                tsa.deleteObservers();
                tsa.addObserver(progress);

                progress.setInderminate(true);
                progress.setTask(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            tsa.calculate();
                            weightChart.update(tsa.calculate(), p, obs, getEnableList(), getShowList(), standardColorList);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(e);
                        }
                    }
                });
                progress.execute();
            }
        });

        exportScheme.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                exportOptimizationScheme(((OptimizationScheme) optimizationSchemes.getSelectedItem()));
            }
        });
        init();
    }

    public class ColorRenderer extends JLabel
            implements TableCellRenderer {

        Border unselectedBorder = null;
        Border selectedBorder = null;
        boolean isBordered = true;

        public ColorRenderer(boolean isBordered) {
            this.isBordered = isBordered;
            setOpaque(true); //MUST do this for background to show up.
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object color,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            Color newColor = (Color) color;
            setBackground(newColor);
            if (isBordered) {
                if (isSelected) {
                    if (selectedBorder == null) {
                        selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                table.getSelectionBackground());
                    }
                    setBorder(selectedBorder);
                } else {
                    if (unselectedBorder == null) {
                        unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                                table.getBackground());
                    }
                    setBorder(unselectedBorder);
                }
            }

            return this;
        }
    }

    public class ColorEditor extends AbstractCellEditor
            implements TableCellEditor,
            ActionListener {

        Color currentColor;
        JButton button;
        JColorChooser colorChooser;
        JDialog dialog;
        protected static final String EDIT = "edit";

        public ColorEditor() {
            button = new JButton();
            button.setActionCommand(EDIT);
            button.addActionListener(this);
            button.setBorderPainted(false);

            //Set up the dialog that the button brings up.
            colorChooser = new JColorChooser();
            dialog = JColorChooser.createDialog(button,
                    "Pick a Color",
                    true, //modal
                    colorChooser,
                    this, //OK button handler
                    null); //no CANCEL button handler
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (EDIT.equals(e.getActionCommand())) {
                //The user has clicked the cell, so
                //bring up the dialog.
                button.setBackground(currentColor);
                colorChooser.setColor(currentColor);
                dialog.setVisible(true);

                fireEditingStopped(); //Make the renderer reappear.

            } else { //User pressed dialog's "OK" button.
                currentColor = colorChooser.getColor();
            }
        }

        //Implement the one CellEditor method that AbstractCellEditor doesn't.
        @Override
        public Object getCellEditorValue() {
            return currentColor;
        }

        //Implement the one method defined by TableCellEditor.
        @Override
        public Component getTableCellEditorComponent(JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            currentColor = (Color) value;
            return button;
        }
    }

    class ParameterTableModel extends AbstractTableModel {

        private String[] columnNames = new String[]{"show", "name", "enabled", "color"};
        private Object[][] data = null;

        ParameterTableModel(SimpleEnsemble p[]) {
            data = new Object[p.length][3];
            for (int i = 0; i < p.length; i++) {
                data[i][0] = Boolean.TRUE;
                data[i][1] = p[i];
                data[i][2] = Boolean.TRUE;
            }
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            if (data != null) {
                return data.length;
            } else {
                return 0;
            }
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (col == 3) {
                return TemporalAnalysis.this.standardColorList[row];
            } else {
                return data[row][col];
            }
        }

        @Override
        public Class getColumnClass(int c) {
            switch (c) {
                case 0:
                    return Boolean.class;
                case 1:
                    return String.class;
                case 2:
                    return Boolean.class;
                case 3:
                    return Color.class;
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            switch (col) {
                case 0:
                    return true;
                case 1:
                    return false;
                case 2:
                    return true;
                case 3:
                    return true;
            }
            return false;
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        @Override
        public void setValueAt(Object value, int row, int col) {
            if (col == 2 && ((Boolean) value).booleanValue() == false) {
                setValueAt(Boolean.FALSE, row, 0);
            }
            if (col == 0 && ((Boolean) value).booleanValue() == true) {
                setValueAt(Boolean.TRUE, row, 2);
            }
            if (col == 3) {
                TemporalAnalysis.this.standardColorList[row] = (Color) value;
            } else {
                data[row][col] = value;
            }
            fireTableCellUpdated(row, col);
        }
    }

    private void init() {
        weightChart = new WeightChart();
        chart = new HydrographChart();

        chart.getXYPlot().getDomainAxis().addChangeListener(new AxisChangeListener() {

            @Override
            public void axisChanged(AxisChangeEvent e) {
                weightChart.getXYPlot().setDomainAxis(chart.getXYPlot().getDomainAxis());
            }
        });

        weightChart.getXYPlot().getDomainAxis().addChangeListener(new AxisChangeListener() {

            @Override
            public void axisChanged(AxisChangeEvent e) {
                chart.getXYPlot().setDomainAxis(weightChart.getXYPlot().getDomainAxis());
            }
        });

        for (int i = 0; i < MAX_WEIGHTS; i++) {
            weightRenderer[i] = new XYLineAndShapeRenderer();
            weightRenderer[i].setBaseFillPaint(this.standardColorList[i]);
            weightRenderer[i].setBaseLinesVisible(true);
            weightRenderer[i].setBaseShapesVisible(false);
            weightRenderer[i].setBaseSeriesVisible(true);
            weightRenderer[i].setDrawSeriesLineAsPath(true);
            weightRenderer[i].setStroke(new BasicStroke(1.0f));
        }

        ChartPanel weightChartPanel = new ChartPanel(weightChart.getChart(), true);

        ChartPanel chartPanel = new ChartPanel(chart.getChart(), true);
        chartPanel.addChartMouseListener(new ChartMouseListener() {

            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                ChartEntity e = event.getEntity();
                if (e != null && e instanceof XYItemEntity) {
                    XYItemEntity xy = (XYItemEntity) e;
                    int index = xy.getSeriesIndex();
                    int data = xy.getItem();

                    System.out.println("index:" + index);
                    System.out.println("data:" + data);

                    DataCollection dc = new DataCollection();
                    dc.addEnsemble(ts.get(data));
                    for (int i = 0; i < p.length; i++) {
                        dc.addEnsemble(p[i]);
                    }
                    try {
                        DataRequestPanel d = new DataRequestPanel(new DottyPlot(), dc);
                        JFrame plotWindow = MCAT5Toolbar.getDefaultPlotWindow("test");
                        plotWindow.add(d, BorderLayout.CENTER);
                        plotWindow.setVisible(true);
                    } catch (NoDataException nde) {
                        System.out.println(nde.toString());
                    }

                    TimeSeriesCollection collection = (TimeSeriesCollection) chart.getXYPlot().getDataset(index);
                    System.out.println(collection.getSeries(0).getDataItem(data).getPeriod());
                    System.out.println(collection.getSeries(0).getDataItem(data).getValue());
                }

            }

            public void chartMouseMoved(ChartMouseEvent event) {
            }
        });

        ChartPanel hydrographChartPanel = new ChartPanel(chart.getChart(), true);

        JScrollPane parameterTablePane = new JScrollPane(parameterTable);


        JPanel sideBar = new JPanel();
        GroupLayout sideLayout = new GroupLayout(sideBar);
        sideLayout.setAutoCreateGaps(true);
        sideLayout.setAutoCreateContainerGaps(true);
        sideBar.setLayout(sideLayout);
        sideLayout.setHorizontalGroup(
                sideLayout.createParallelGroup()
                .addComponent(parameterTablePane)
                .addComponent(infoLabel)
                .addGroup(sideLayout.createSequentialGroup()
                    .addComponent(calcSA)
                    .addComponent(calcScheme)
                )
                .addComponent(SAMethods)
                .addComponent(optimizationSchemes)
                .addComponent(parameterGroups)
                .addComponent(exportScheme));
        sideLayout.setVerticalGroup(
                sideLayout.createSequentialGroup()
                .addComponent(parameterTablePane)
                .addComponent(infoLabel)
                .addGroup(sideLayout.createParallelGroup()
                    .addComponent(calcSA)
                    .addComponent(calcScheme)
                )
                .addComponent(SAMethods)
                .addComponent(optimizationSchemes)
                .addComponent(parameterGroups)
                .addComponent(exportScheme));

        mainPanel = new JPanel();
        GroupLayout mainLayout = new GroupLayout(mainPanel);
        mainLayout.setAutoCreateGaps(true);
        mainLayout.setAutoCreateContainerGaps(true);
        mainPanel.setLayout(mainLayout);

        mainLayout.setHorizontalGroup(
                mainLayout.createSequentialGroup().addGroup(mainLayout.createParallelGroup().addComponent(weightChartPanel).addComponent(hydrographChartPanel)).addComponent(sideBar));
        mainLayout.setVerticalGroup(mainLayout.createParallelGroup().addGroup(mainLayout.createSequentialGroup().addComponent(weightChartPanel).addComponent(hydrographChartPanel)).addComponent(sideBar));
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public void refresh() throws NoDataException {
        if (!this.isRequestFulfilled()) {
            return;
        }

        ArrayList<DataSet> data[] = getData(new int[]{0, 1, 2});
        ts = (TimeSerieEnsemble) data[0].get(0);
        obs = (Measurement) data[1].get(0);
        e = (EfficiencyEnsemble) data[2].get(0);

        Set<String> xSet = this.getDataSource().getDatasets(Parameter.class);
        p = new SimpleEnsemble[xSet.size()];
        int counter = 0;
        for (String name : xSet) {
            p[counter++] = this.getDataSource().getSimpleEnsemble(name);
        }

        parameterTable.setModel(new ParameterTableModel(p));
        parameterTable.setDefaultEditor(Color.class, new ColorEditor());
        parameterTable.getColumnModel().getColumn(3).setCellRenderer(new ColorRenderer(true));
        parameterTable.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                /*ObserverWorkerDlg progress = new ObserverWorkerDlg(null, "Updating plot");
                temporalAnalysis.addObserver(progress);
                progress.setInderminate(true);
                progress.setTask(new Runnable() {

                    public void run() {
                        //weightChart.update(temporalAnalysis.calculate(), p, obs, getEnableList(), getShowList(), standardColorList);
                    }
                });
                progress.execute();*/
            }
        });
        this.SAMethods.setModel(new DefaultComboBoxModel(new Object[]{
                    new VarianceBasedTemporalSensitivityAnalysis(p, e, ts, obs),
                    new TemporalSensitivityAnalysis(p, e, ts, obs)
                }));

        this.chart.setHydrograph(obs);

    }

    private boolean[] getEnableList() {
        ParameterTableModel model = (ParameterTableModel) this.parameterTable.getModel();

        boolean parameterActive[] = new boolean[model.getRowCount()];
        for (int i = 0; i < parameterActive.length; i++) {
            if (((Boolean) model.getValueAt(i, 2)) == true) {
                parameterActive[i] = true;
            } else {
                parameterActive[i] = false;
            }
        }
        return parameterActive;
    }

    private boolean[] getShowList() {
        ParameterTableModel model = (ParameterTableModel) this.parameterTable.getModel();

        boolean parameterActive[] = new boolean[model.getRowCount()];
        for (int i = 0; i < parameterActive.length; i++) {
            if (((Boolean) model.getValueAt(i, 0)) == true) {
                parameterActive[i] = true;
            } else {
                parameterActive[i] = false;
            }
        }
        return parameterActive;
    }

    private void calcOptimizationScheme(final OptimizationScheme scheme) {
        //scheme.setData(temporalAnalysis.calculate(), p, e, obs);
        if (scheme instanceof GreedyOptimizationScheme1)
            ((GreedyOptimizationScheme1) scheme).setData((VarianceBasedTemporalSensitivityAnalysis)this.SAMethods.getSelectedItem(), p, e, obs);
        else
            scheme.setData(((TemporalSensitivityAnalysis)this.SAMethods.getSelectedItem()).calculate(), p, e, obs);
        
        ObserverWorkerDlg progress = new ObserverWorkerDlg(null, "Calculating Optimization Scheme");
        scheme.addObserver(progress);

        progress.setInderminate(true);
        progress.setTask(new Runnable() {

            @Override
            public void run() {
                try {
                    scheme.calcOptimizationScheme();
                    ComboBoxModel model = new DefaultComboBoxModel(scheme.getSolutionGroups().toArray());
                    synchronized (parameterGroups) {
                        parameterGroups.setModel(model);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
            }
        });
        progress.execute();
    }

    private void exportOptimizationScheme(OptimizationScheme scheme) {
        OptimizationDescriptionDocument document = scheme.getOptimizationDocument();

        JFileChooser chooser = GUIHelper.getJFileChooser();
        chooser.setFileFilter(new FileFilter() {

            public String getDescription() {
                return "optimization scheme";
            }

            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                if (file.getAbsolutePath().endsWith("xml")) {
                    return true;
                }
                return false;
            }
        });
        try {
            BeanInfo info = Introspector.getBeanInfo(JAMSCalendar.class);
            PropertyDescriptor[] propertyDescriptors =
                    info.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; ++i) {
                PropertyDescriptor pd = propertyDescriptors[i];
                if (!pd.getName().equals("milliSeconds")
                        && !pd.getName().equals("dateFormat")) {
                    pd.setValue("transient", Boolean.TRUE);
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        int result = chooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                XMLEncoder encoder = new XMLEncoder(
                        new BufferedOutputStream(
                        new FileOutputStream(chooser.getSelectedFile())));
                encoder.writeObject(document);
                encoder.close();
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, "Optimization scheme was not saved!\n" + ioe.toString());
                return;
            }
        }
    }
}