/*
 * ModelSubgroupDlg.java
 * Created on 11. Mai 2008, 06:37
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

package org.unijena.juice.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import org.unijena.jams.gui.LHelper;
import org.unijena.juice.ModelProperties.Group;

/**
 *
 * @author Heiko Busch
 */
public class ModelSubgroupDlg extends JDialog {
    
    public final static int OK_RESULT = 0;
    public final static int CANCEL_RESULT = -1;
    
    private JComboBox groupCombo;
    private JTextField nameField;
    private int result = CANCEL_RESULT;
    
    public ModelSubgroupDlg(Frame owner) {
        super(owner);
        setLocationRelativeTo(owner);
        init();
    }
    
    private void init() {
        
        setModal(true);
        this.setTitle("Subgroup editor");
        
        this.setLayout(new BorderLayout());
        GridBagLayout gbl = new GridBagLayout();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(gbl);
        
        LHelper.addGBComponent(contentPanel, gbl, new JPanel(), 0, 0, 1, 1, 0, 0);
        LHelper.addGBComponent(contentPanel, gbl, new JLabel("Group:"), 0, 1, 1, 1, 0, 0);
        LHelper.addGBComponent(contentPanel, gbl, new JLabel("Name:"), 0, 2, 1, 1, 0, 0);
        
        groupCombo = new JComboBox();
        nameField = new JTextField();
        
        LHelper.addGBComponent(contentPanel, gbl, groupCombo, 1, 1, 1, 1, 0, 0);
        LHelper.addGBComponent(contentPanel, gbl, nameField, 1, 2, 1, 1, 30, 0);
        
        JButton okButton = new JButton("OK");
        ActionListener okListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                result = OK_RESULT;
            }
        };
        okButton.addActionListener(okListener);
        getRootPane().setDefaultButton(okButton);        
        
        JButton cancelButton = new JButton("Cancel");
        ActionListener cancelListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                result = CANCEL_RESULT;
            }
        };
        cancelButton.addActionListener(cancelListener);
        cancelButton.registerKeyboardAction(cancelListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JButton.WHEN_IN_FOCUSED_WINDOW);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        pack();
    }
    
    
    public void update(String[] groupNames, Group subgroup, String currentGroup) {

        groupCombo.setModel(new DefaultComboBoxModel(groupNames));
        groupCombo.setSelectedItem(currentGroup);
        
        
        if (subgroup != null) {
            
            nameField.setText(subgroup.name);
            
        } else {
            nameField.setText("");
        }
        
        pack();
    }
    
    public String getGroup() {
        return (String) groupCombo.getSelectedItem();
    }
    
    public int getResult() {
        return result;
    }
    
    public String getName() {
        return nameField.getText();
    }
    
}
