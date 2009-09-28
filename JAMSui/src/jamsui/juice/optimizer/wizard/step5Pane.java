/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jamsui.juice.optimizer.wizard;

import jamsui.juice.optimizer.wizard.OptimizationWizard.Efficiency;
import jamsui.juice.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author Christian Fischer
 */
public class step5Pane extends stepPane{
    JPanel step5EfficiencyPanel = null;
    ArrayList<Efficiency> selectedEfficiencies = null;
    JComboBox modeList[] = null;
    
    String modes[] = {  JUICE.resources.getString("minimization"),
                        JUICE.resources.getString("maximization"),
                        JUICE.resources.getString("absolute_minimization"),
                        JUICE.resources.getString("absolute_maximization") };
                    
    public void setSelectedEfficiencies(ArrayList<Efficiency> selectedEfficiencies){
        this.selectedEfficiencies = selectedEfficiencies;
    }
    
    @Override
    public JPanel build() {        
        panel.setLayout(new BorderLayout());
        panel.setBorder(null);
        panel.add(new JLabel(JUICE.resources.getString("step4_desc")), BorderLayout.NORTH);               
        
        step5EfficiencyPanel = new JPanel(new BorderLayout());
        
        JScrollPane Scroller = new JScrollPane(step5EfficiencyPanel);        
        Scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        Scroller.setVisible(true);
        panel.add(Scroller,BorderLayout.CENTER);
        
        
        return panel;
    }
    
    @Override    
    public JPanel getPanel(){
        return panel;
    }
    @Override   
    public String init(){
        if (selectedEfficiencies == null){
            return JUICE.resources.getString("error_no_parameter");
        }
        step5EfficiencyPanel.removeAll();
        step5EfficiencyPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;    c.gridy = 0;    c.fill = GridBagConstraints.HORIZONTAL;
        step5EfficiencyPanel.add(new JLabel(JUICE.resources.getString("parameter")),c);        
        c.gridx = 1;    c.gridy = 0;
        step5EfficiencyPanel.add(new JLabel(JUICE.resources.getString("component")),c);        
        c.gridx = 2;    c.gridy = 0;
        step5EfficiencyPanel.add(new JLabel(JUICE.resources.getString("mode")),c);        
        step5EfficiencyPanel.setMaximumSize(new Dimension(100,100));
        
        this.modeList = new JComboBox[selectedEfficiencies.size()];        
        
        for (int i=0;i<selectedEfficiencies.size();i++){
            JTextField paramName = new JTextField(10);
            paramName.setEditable(false);
            JTextField componentName = new JTextField(10);            
            componentName.setEditable(false);
            
            if (selectedEfficiencies.get(i).variableName != null){
                paramName.setText(selectedEfficiencies.get(i).variableName);
                componentName.setText(selectedEfficiencies.get(i).componentName);
            }else{
                paramName.setText(selectedEfficiencies.get(i).attributeName);
                componentName.setText(selectedEfficiencies.get(i).contextName);
            }                        
            modeList[i] = new JComboBox(modes);
            c.gridx = 0;    c.gridy = i+1;
            step5EfficiencyPanel.add(paramName,c);
            c.gridx = 1;    c.gridy = i+1;
            step5EfficiencyPanel.add(componentName,c);
            c.gridx = 2;    c.gridy = i+1;
            step5EfficiencyPanel.add(modeList[i],c);
            
        }
        return null;
    }
    
    @Override
    public String finish(){
        for (int i=0;i<this.modeList.length;i++){
           int index = this.modeList[i].getSelectedIndex()+1;
           this.selectedEfficiencies.get(i).mode = index;
        }
        return null;
    }
}
