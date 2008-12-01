/*
 * RuntimeManagerPanel.java
 * Created on 1. Dezember 2008, 10:43
 *
 * This file is part of JAMS
 * Copyright (C) FSU Jena
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
package jams.gui;

import jams.JAMS;
import jams.runtime.RuntimeManager;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class RuntimeManagerPanel extends JPanel {

    private JList runtimeList;
    private DefaultListModel listModel = new DefaultListModel();
    private JButton stopButton;
    private JPanel buttonPanel;

    public RuntimeManagerPanel() {

        this.setLayout(new BorderLayout());

        runtimeList = new JList(listModel);

        runtimeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        runtimeList.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        stopButton = new JButton(JAMS.resources.getString("stop_model"));
        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] rtInfoArray = runtimeList.getSelectedValues();
                if (rtInfoArray != null) {
                    for (Object o : rtInfoArray) {
                        RuntimeManager.RuntimeInfo rtInfo = (RuntimeManager.RuntimeInfo) o;
                        rtInfo.getRuntime().sendHalt();
                    }
                }
            }
        });

        runtimeList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    if (runtimeList.getSelectedIndex() == -1) {
                        stopButton.setEnabled(false);
                    } else {
                        stopButton.setEnabled(true);
                    }
                }
            }
        });

        JAMS.getRuntimeManager().addObserver(new Observer() {

            @Override
            public void update(Observable o, Object arg) {

                RuntimeManager.RuntimeInfo rtInfo = (RuntimeManager.RuntimeInfo) arg;

                if (rtInfo.getEndTime() == null) {
                    listModel.addElement(rtInfo);
                } else {
//                    int pos = listModel.lastIndexOf(rtInfo);
//                    listModel.removeElement(rtInfo);
//                    listModel.add(pos, rtInfo);
                    listModel.removeElement(rtInfo);
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(runtimeList);
        this.add(listScrollPane, BorderLayout.CENTER);
        buttonPanel = new JPanel();
        buttonPanel.add(stopButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

    }

    /**
     * @return the buttonPanel
     */
    public JPanel getButtonPanel() {
        return buttonPanel;
    }
}
