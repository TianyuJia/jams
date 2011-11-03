/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package optas.hydro.gui;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import optas.gui.MCAT5.MCAT5Plot;
import optas.hydro.calculations.SlopeCalculations;
import optas.hydro.data.DataSet;
import optas.hydro.data.Efficiency;
import optas.hydro.data.EfficiencyEnsemble;
import optas.hydro.data.Parameter;
import optas.hydro.data.SimpleEnsemble;

/**
 *
 * @author Christian Fischer
 */
public class GllobalSensitivityEfficiencyComparison extends MCAT5Plot {

    XYPlot plot = new XYPlot();
    ChartPanel chartPanel = null;

    public GllobalSensitivityEfficiencyComparison() {
        this.addRequest(new SimpleRequest(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("Efficiency"), Efficiency.class));
        this.addRequest(new SimpleRequest(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("PARAMETER"), Parameter.class));

        init();
    }

    private void init() {
        //setup renderer
        XYDotRenderer renderer = new XYDotRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setDotHeight(3);
        renderer.setDotWidth(3);
        //setup plot
        plot.setRenderer(renderer);
        //setup chart
        JFreeChart chart = new JFreeChart(plot);
        chart.setTitle(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("DOTTY_PLOT"));
        chartPanel = new ChartPanel(chart, true);

        try{
            refresh();
        }catch(NoDataException e){
            JOptionPane.showMessageDialog(chartPanel, "Failed to show dataset. The data is incommensurate!");
        }
    }

  
    

    @Override
    public void refresh() throws NoDataException {
        if (!this.isRequestFulfilled()) {
            return;
        }

        ArrayList<DataSet> p[] = getData(new int[]{0,1});
        EfficiencyEnsemble eff = (EfficiencyEnsemble)p[0].get(0);
        SimpleEnsemble param = (SimpleEnsemble)p[1].get(0);
        
        plot.setDomainAxis(new NumberAxis(eff.getName()));
        plot.setRangeAxis(new NumberAxis("slope"));
        
        XYSeries dataset[] = SlopeCalculations.calculateDerivative(eff,this.getDataSource());

        int c=-1;
        for (int i=0;i<dataset.length;i++){
            if (dataset[i].getDescription().equals(param.getName())){
                c=i;
                break;
            }
        }
        if (c==-1)
            return;
        plot.setDataset(0, new XYSeriesCollection(dataset[c]));

        if (plot.getRangeAxis() != null) {
            plot.getRangeAxis().setAutoRange(true);
        }
        if (plot.getDomainAxis() != null) {
            plot.getDomainAxis().setAutoRange(true);
        }
    }
    public JPanel getPanel(){
        return this.chartPanel;
    }
}
