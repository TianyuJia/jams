/*
 * Context.java
 * Created on 21.03.2010, 20:45:41
 *
 * This file is part of JAMS
 * Copyright (C) FSU Jena
 *
 * JAMS is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * JAMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JAMS. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package jamsui.juice.gui;

import jams.gui.tools.GUIHelper;
import jamsui.juice.JUICE;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import jams.JAMS;
import jams.meta.ComponentDescriptor;
import jams.meta.ContextAttribute;
import jams.meta.ContextDescriptor;
import jams.meta.OutputDSDescriptor;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class OutputDSDlg extends JDialog {

    private JComboBox contextCombo;
    private JButton okButton, cancelButton;
    private ModelView view;
    private JList dsList;

    public OutputDSDlg(Frame owner) {
        super(owner);
        setLocationRelativeTo(owner);
        setModal(false);
        setResizable(false);
        setLocationByPlatform(true);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout mainLayout = new GridBagLayout();
        contentPanel.setLayout(mainLayout);

        contextCombo = new JComboBox();
        contextCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    updateContextAttributes();
                }
            }
        });

        dsList = new JList();
        dsList.setPreferredSize(new Dimension(100,100));
        JPanel storesPanel = new JPanel();
        storesPanel.add(new JLabel("Datastores"));
        storesPanel.add(new JScrollPane(dsList));
        getContentPane().add(storesPanel, BorderLayout.WEST);

        GUIHelper.addGBComponent(contentPanel, mainLayout, new JLabel(JAMS.resources.getString("Contexts:")), 1, 0, 1, 1, 0, 0);
        GUIHelper.addGBComponent(contentPanel, mainLayout, contextCombo, 2, 0, 1, 1, 0, 0);

        okButton = new JButton(JAMS.resources.getString("OK"));
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            }
        });

        cancelButton = new JButton(JAMS.resources.getString("Cancel"));
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            }
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        this.pack();
        getRootPane().setDefaultButton(okButton);

    }

    public void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
        if (isVisible) {
            view = JUICE.getJuiceFrame().getCurrentView();
            HashMap<String, ComponentDescriptor> cdMap = view.getModelDescriptor().getComponentDescriptors();

            // create a list containing all contexts of this model
            ArrayList<ComponentDescriptor> contextList = new ArrayList<ComponentDescriptor>();
            for (ComponentDescriptor cd : cdMap.values()) {
                if (cd instanceof ContextDescriptor) {
                    contextList.add(cd);
                }
            }

            // sort the context list
            Collections.sort(contextList, new Comparator<ComponentDescriptor>() {

                @Override
                public int compare(ComponentDescriptor a1, ComponentDescriptor a2) {
                    return a1.toString().compareTo(a2.toString());
                }
            });

            this.contextCombo.setModel(new DefaultComboBoxModel(contextList.toArray(new ComponentDescriptor[contextList.size()])));
        }

        dsList.setModel(new AbstractListModel() {

            public int getSize() {
                HashMap<String, OutputDSDescriptor> stores = view.getModelDescriptor().getDatastores();
                return stores.size();
            }

            public Object getElementAt(int index) {

                HashMap<String, OutputDSDescriptor> stores = view.getModelDescriptor().getDatastores();

                // create a list of all stores
                ArrayList<OutputDSDescriptor> storesList = new ArrayList<OutputDSDescriptor>();
                for (OutputDSDescriptor store : stores.values()) {
                    storesList.add(store);
                }

                // sort the stores list
                Collections.sort(storesList, new Comparator<OutputDSDescriptor>() {

                    @Override
                    public int compare(OutputDSDescriptor a1, OutputDSDescriptor a2) {
                        return a1.getName().compareTo(a2.getName());
                    }
                });

                return storesList.get(index);
            }
        });
    }

    private void updateContextAttributes() {
        System.out.println("updating list");
        ContextDescriptor cd = (ContextDescriptor) contextCombo.getSelectedItem();

        for (ContextAttribute ca : cd.getDynamicAttributes().values()) {
            System.out.println(ca.getName());
        }
    }
}
