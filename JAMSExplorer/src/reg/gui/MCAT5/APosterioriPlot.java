/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package reg.gui.MCAT5;

import java.awt.Color;
import java.awt.Window;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYBarDataset;
import reg.gui.MCAT5Toolbar.EfficiencyDataSet;
import reg.gui.MCAT5Toolbar.ParameterSet;

/**
 *
 * @author Christian Fischer
 */
public class APosterioriPlot{                        
        XYPlot plot = new XYPlot();        
        ChartPanel chartPanel = null;
                
        ParameterSet data;
        EfficiencyDataSet eff;
                       
        final int BOX_COUNT = 20;
        
        private double FindMinimalParameterValue(ParameterSet data){
            int n = data.set.length;
            double min = Double.POSITIVE_INFINITY;
            for (int i=0;i<n;i++){
                min = Math.min(min, data.set[i]);
            }
            return min;
        }
        private double FindMaximalParameterValue(ParameterSet data){
            int n = data.set.length;
            double max = Double.NEGATIVE_INFINITY;
            for (int i=0;i<n;i++){
                max = Math.max(max, data.set[i]);
            }
            return max;
        }
    
        public APosterioriPlot(ParameterSet data, EfficiencyDataSet eff){                  
            this.data = data;
            this.eff = eff;
            
            //renderer.set            
            XYBarRenderer renderer = new XYBarRenderer(0.33 / (double)BOX_COUNT);                        
            renderer.setSeriesPaint(0, Color.DARK_GRAY);            
            plot.setRenderer(0, renderer);                                                              
                        
            plot.setDomainAxis(new NumberAxis(data.name));
            plot.setRangeAxis(new NumberAxis(eff.name));
                                
            JFreeChart chart = new JFreeChart(plot);
            chart.setTitle("A Posterio Parameter Distribution");            
            chartPanel = new ChartPanel(chart, true);
                                              
            updateData();
        }
        
        public void updateData(){ 
            XYSeries dataset = new XYSeries("mean of efficiency");
            
            double boxes[] = new double[BOX_COUNT];
            double boxes_count[] = new double[BOX_COUNT];
            
            double min = this.FindMinimalParameterValue(data);
            double max = this.FindMaximalParameterValue(data);
            
            for (int i=0;i<this.data.set.length;i++){
                int index = (int)((data.set[i] - min) / (max-min) * (boxes.length-1));
                boxes[index] += eff.set[i];
                boxes_count[index] += 1.0;
            }
            for (int i=0;i<boxes.length;i++){
                dataset.add(((max-min)/(boxes.length-1))*i,boxes[i]/boxes_count[i]);
            }
            
            plot.setDataset(0, new XYBarDataset(new XYSeriesCollection(dataset),(max-min)/(double)BOX_COUNT));
            
            if (null != plot.getRangeAxis())   plot.getRangeAxis().setAutoRange(true);
            if (null != plot.getDomainAxis())  plot.getDomainAxis().setAutoRange(true);            
        }
        
        public JPanel getPanel(){
            return chartPanel;
        }
    }
