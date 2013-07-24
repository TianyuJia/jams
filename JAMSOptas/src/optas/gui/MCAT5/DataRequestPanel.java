/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package optas.gui.MCAT5;

import jams.gui.WorkerDlg;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import optas.gui.MCAT5.MCAT5Plot.NoDataException;
import optas.gui.MCAT5.MCAT5Plot.SimpleRequest;
import optas.gui.wizard.ObjectiveConstructorDialog;
import optas.hydro.data.DataCollection;
import optas.hydro.data.DataSet;
import optas.hydro.data.Efficiency;
import optas.hydro.data.EfficiencyEnsemble;
import optas.hydro.data.TimeSerieEnsemble;


/**
 *
 * @author Christian Fischer
 */
public final class DataRequestPanel extends JPanel{
    
    Dimension defaultDatasetTableDimension = new Dimension(500, 150);

    MCAT5Plot plot;
    DataCollection data;

    JPanel dataPanel = new JPanel();

    public final class RequestGUI{
        SimpleRequest request;
        ArrayList<JComboBox> boxes;

        RequestGUI(SimpleRequest r) throws NoDataException{
            this.request = r;
            boxes = new ArrayList<JComboBox>();
            while(boxes.size()<r.min)
                if (!addBox()){
                    throw new NoDataException(r);
                }
        }

        public void removeLastBox(){
            boxes.remove(boxes.size()-1);
        }

        public boolean addBox() {
            JComboBox box = new JComboBox();
            box.setPreferredSize(new Dimension(150, 30));            
            if (data.getDatasets(request.clazz).isEmpty()) {
                return false;
            }

            for (String d : data.getDatasets(request.clazz)) {
                box.addItem(d);
            }

            if (request.clazz.equals(Efficiency.class) && ObjectiveConstructorDialog.isApplicable(data)){
                box.addItem("User defined");
            }

            box.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange()!=ItemEvent.SELECTED)
                        return;
                    if (e.getItem().toString().compareTo("User defined")==0){
                        ObjectiveConstructorDialog dlg = new ObjectiveConstructorDialog(data);
                        dlg.setVisible(true);
                        if (dlg.getApproved()){
                            EfficiencyEnsemble ensemble = dlg.getResult();
                            JComboBox box = (JComboBox)e.getSource();
                            DataRequestPanel.this.data.addEnsemble(ensemble);
                            box.addItem(ensemble);
                            box.setSelectedItem(box);
                        }
                    }
                    WorkerDlg progress = new WorkerDlg(null, "Updating plot");
                    progress.setInderminate(true);
                    progress.setTask(new Runnable() {

                        public void run() {
                            updatePlot();
                        }
                    });
                    progress.execute();
                }
            });
            box.putClientProperty("request", request);
            boxes.add(box);
            return true;
        }
    }

    ArrayList<RequestGUI> requests = new ArrayList<RequestGUI>();

    public DataRequestPanel(MCAT5Plot plot, DataCollection data) throws NoDataException{
        this.plot = plot;
        this.data = data;
        plot.setDataSource(data);
        if (data==null)
            throw new NoDataException("datacollection not provided!");
        ArrayList<SimpleRequest> request = plot.getRequiredData();

        if (request.isEmpty()){
            updatePlot();
            return;
        }
        for (SimpleRequest r : request){
            RequestGUI rGUI = new RequestGUI(r);
            requests.add(rGUI);
        }
        JScrollPane datasetScroll = new JScrollPane(dataPanel);
        /*datasetScroll.setSize(defaultDatasetTableDimension);
        datasetScroll.setMinimumSize(defaultDatasetTableDimension);
        datasetScroll.setPreferredSize(defaultDatasetTableDimension);*/

        dataPanel.setLayout(new GridBagLayout());

        JScrollPane contentScroll = new JScrollPane(plot.getPanel());
        contentScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contentScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        layouting();

        this.setLayout(new BorderLayout());
        this.add(contentScroll, BorderLayout.CENTER);
        this.add(datasetScroll, BorderLayout.SOUTH);

        updatePlot();
    }

    public void layouting(){
        dataPanel.removeAll();

        int i=0;
        for (RequestGUI r : requests){
            for (int j=0;j<r.boxes.size();j++){
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = i;
                c.anchor = GridBagConstraints.WEST;
                c.ipadx = 10;
                dataPanel.add(new JLabel(r.request.name),c);

                c.gridx = 1;
                if (j==r.boxes.size()-1){
                    JPanel panel = new JPanel(new BorderLayout());
                    JPanel buttonPanel = new JPanel(new FlowLayout());

                    panel.add(r.boxes.get(j), BorderLayout.WEST);
                    panel.add(buttonPanel, BorderLayout.EAST);

                    if (r.boxes.size() < r.request.max){
                        JButton button = new JButton("+");
                        button.putClientProperty("request", r);
                        button.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                RequestGUI rGUI = (RequestGUI)((JButton)e.getSource()).getClientProperty("request");
                                rGUI.addBox();
                                SwingUtilities.invokeLater(new Runnable(){
                                    public void run(){
                                        layouting();
                                    }
                                });
                            }
                        });
                        buttonPanel.add(button);
                         
                    }

                    if (r.boxes.size() > r.request.min){
                        JButton button = new JButton("-");
                        button.putClientProperty("request", r);
                        button.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                RequestGUI rGUI = (RequestGUI)((JButton)e.getSource()).getClientProperty("request");
                                rGUI.removeLastBox();
                                SwingUtilities.invokeLater(new Runnable(){
                                    public void run(){
                                        layouting();
                                    } 
                                });

                            }
                        });
                        buttonPanel.add(button);
                    }                    
                    dataPanel.add(panel,c);
                }else
                    dataPanel.add(r.boxes.get(j),c);
                i++;
            }                                   
        }

        dataPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                "Data Configuration"));
                
        this.invalidate();
        this.updateUI();
    }

    public void updatePlot(){
        for (RequestGUI rGUI : this.requests){            
            ArrayList<DataSet> list = new ArrayList<DataSet>();
            for (JComboBox box : rGUI.boxes){
                DataSet e = null;
                if (box.getSelectedItem() instanceof String){
                    e = data.getDataSet((String)box.getSelectedItem());
                    list.add(e);
                } else
                    list.add((DataSet)box.getSelectedItem());

            }
            this.plot.setData(rGUI.request.name, list);
        }
        try{
            this.plot.refresh();
            this.invalidate();
            this.updateUI();
        }catch(NoDataException nde){
            JOptionPane.showMessageDialog(dataPanel, "failed to show data. The data is incommensurate!");
        }
    }
}
