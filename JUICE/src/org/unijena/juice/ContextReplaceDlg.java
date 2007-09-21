/*
 * ContextReplaceDlg.java
 * Created on 26. Januar 2007, 09:46
 *
 * This file is part of JAMS
 * Copyright (C) 2006 FSU Jena
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 *
 */

package org.unijena.juice;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import org.unijena.jams.gui.LHelper;
import org.unijena.juice.tree.ComponentDescriptor;

/**
 *
 * @author Sven Kralisch
 */
public class ContextReplaceDlg extends JDialog {
    
    public static final int APPROVE_OPTION = 1;
    public static final int CANCEL_OPTION = 0;
    private static final int TEXTAREA_WIDTH = 295;
    
    private int result = CANCEL_OPTION;
    private JComboBox contextCombo = new JComboBox();
    private HashMap<String, JTextPane> textAreas = new HashMap<String, JTextPane>();
    private JLabel oldComponentLabel = new JLabel();
    
    /** Creates a new instance of ContextReplaceDlg */
    public ContextReplaceDlg(Frame owner) {
        
        super(owner);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(owner);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setModal(true);
        
        GridBagLayout mainLayout = new GridBagLayout();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(mainLayout);
        
        
        String replaceText = "Found references to context";
        
        LHelper.addGBComponent(mainPanel, mainLayout, new JLabel("Found references to old context"), 0, 1, 1, 1, 0, 0);
        LHelper.addGBComponent(mainPanel, mainLayout, oldComponentLabel, 0, 2, 1, 1, 0, 0);
        LHelper.addGBComponent(mainPanel, mainLayout, new JLabel("from the following components: "), 0, 3, 1, 1, 0, 0);
        LHelper.addGBComponent(mainPanel, mainLayout, getTextPane("components", "", 140, false), 0, 4, 1, 1, 0, 0);
        LHelper.addGBComponent(mainPanel, mainLayout, new JLabel("Please choose new context from this list:"), 0, 5, 1, 1, 0, 0);
        
        LHelper.addGBComponent(mainPanel, mainLayout, contextCombo, 0, 6, 1, 1, 0, 0);
        
        this.getContentPane().add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                result = ComponentAttributeDlg.APPROVE_OPTION;
            }
        });
        buttonPanel.add(okButton);
                
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
    }
    
    private JScrollPane getTextPane(String key, String value, int height, boolean editable) {
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/plain");
        textPane.setEditable(editable);
        textPane.setText(value);
        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setPreferredSize(new Dimension(TEXTAREA_WIDTH, height));
        textAreas.put(key, textPane);
        return scroll;
    }
    
    public void show(String oldContext, String[] availableContexts, HashSet<ComponentDescriptor> components) {
        
        this.setTitle("Replace context : " + oldContext);
        
        oldComponentLabel.setText("  \"" + oldContext + "\"");
        contextCombo.setModel(new DefaultComboBoxModel(availableContexts));
        String componentString = "";

        //get component names, sort them and put them into JLabel
        ArrayList<String> componentNames = new ArrayList<String>();
        for (ComponentDescriptor cd : components) {
            componentNames.add(cd.getName());
        }
        Collections.sort(componentNames);
        for (String name : componentNames) {
            componentString += name + "\n";
        }
        
        componentString = componentString.substring(0, componentString.length()-1);
        JTextPane pane = textAreas.get("components");
        pane.setText(componentString);
        pane.setCaretPosition(0);
        
        pack();
        this.setVisible(true);
    }
    
    
    public String getContext() {
        return (String) contextCombo.getSelectedItem();
    }
    
    public int getResult() {
        return result;
    }
    
    
}
