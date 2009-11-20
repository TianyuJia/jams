/*
 * TimeSpaceDSPanel.java
 * Created on 12. Februar 2009, 09:18
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
package reg.gui;

import jams.gui.tools.GUIHelper;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import reg.dsproc.DataMatrix;
import reg.dsproc.DataStoreProcessor;
import reg.dsproc.SimpleSerieProcessor;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class SimpleDSPanel extends DSPanel {

    private static final Dimension ACTION_BUTTON_DIM = new Dimension(150, 25),  LIST_DIMENSION = new Dimension(150, 250);
    private SimpleSerieProcessor proc;
    private GridBagLayout mainLayout;
    private JList timeList,  monthList,  yearList;
    private JTextField timeField;
    private JPanel aggregationPanel;
    private GridBagLayout aggregationLayout;    
    private Action[] actions = {
        new AbstractAction(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("SHOW_DATA")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                showData();
            }
        },
        new AbstractAction(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("MEAN")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                showMean();
            }
        },
        new AbstractAction(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("MONTHLY_MEAN")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                showMonthlyMean();
            }
        },
        new AbstractAction(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("YEARLY_MEAN")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                showYearlyMean();
            }
        }
    };
    private Action showData = actions[0], mean = actions[1],  monthMean = actions[2],  yearMean = actions[3];
    private Action cacheReset = new AbstractAction(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("RESET_CACHES")) {

        @Override
        public void actionPerformed(ActionEvent e) {
            resetCaches();
        }
    };
    private Action indexReset = new AbstractAction(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("RELOAD_INDEX")) {

        @Override
        public void actionPerformed(ActionEvent e) {
            resetIndex();
        }
    };
    private Action freeTempMean = new AbstractAction(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("TEMP._MEAN_(FILTER)")) {

        @Override
        public void actionPerformed(ActionEvent e) {
            showFreeTempMean();
        }
    };

    public SimpleDSPanel() {
        init();
    }

    private void init() {

        for (Action a : actions) {
            a.setEnabled(false);
        }
        
        freeTempMean.setEnabled(false);
        cacheReset.setEnabled(false);
        indexReset.setEnabled(false);

        mainLayout = new GridBagLayout();
        this.setLayout(mainLayout);

        timeList = new JList();
        JScrollPane timeListScroll = new JScrollPane(timeList);
        timeListScroll.setPreferredSize(LIST_DIMENSION);
        timeList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {                    
                    if (timeList.getSelectedValues().length == 0) {
                        mean.setEnabled(false); 
                        showData.setEnabled(false);
                    }else if (timeList.getSelectedValues().length == 1){
                        showData.setEnabled(true);
                        mean.setEnabled(false);                        
                    }else {
                        mean.setEnabled(true);
                        showData.setEnabled(true);
                    }
                }
            }
        });

        monthList = new JList();
        monthList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane monthListScroll = new JScrollPane(monthList);
        monthListScroll.setPreferredSize(new Dimension(LIST_DIMENSION.width - 100, LIST_DIMENSION.height));
        monthList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (monthList.getSelectedValues().length == 1) {
                        monthMean.setEnabled(true);
                    } else {
                        monthMean.setEnabled(false);
                    }
                }
            }
        });

        yearList = new JList();
        yearList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane yearListScroll = new JScrollPane(yearList);
        yearListScroll.setPreferredSize(new Dimension(LIST_DIMENSION.width - 100, LIST_DIMENSION.height));
        yearList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (yearList.getSelectedValues().length == 1) {
                        yearMean.setEnabled(true);
                    } else {
                        yearMean.setEnabled(false);
                    }
                }
            }
        });
        
        GUIHelper.addGBComponent(this, mainLayout, new JLabel(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("ATTRIBUTE/AGGREGATION:")), 0, 10, 1, 1, 0, 0);

        aggregationLayout = new GridBagLayout();
        aggregationPanel = new JPanel();
        aggregationPanel.setLayout(aggregationLayout);
        JScrollPane aggregationScroll = new JScrollPane(aggregationPanel);
        aggregationScroll.setPreferredSize(new Dimension(LIST_DIMENSION.width + 100, LIST_DIMENSION.height));

        GUIHelper.addGBComponent(this, mainLayout, aggregationScroll, 0, 20, 1, 1, 0, 0);
        GUIHelper.addGBComponent(this, mainLayout, new JLabel(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("IDS:")), 10, 10, 1, 1, 0, 0);
        GUIHelper.addGBComponent(this, mainLayout, timeListScroll, 10, 20, 1, 1, 0, 0);

        JPanel buttonPanelA = new JPanel();
        buttonPanelA.setPreferredSize(LIST_DIMENSION);
        JButton button;

        for (int i = 0; i <= 1; i++) {
            Action a = actions[i];
            button = new JButton(a);
            button.setPreferredSize(ACTION_BUTTON_DIM);
            buttonPanelA.add(button);
        }

        JPanel filterPanel = new JPanel();
        filterPanel.setPreferredSize(new Dimension(LIST_DIMENSION.width, LIST_DIMENSION.height - 150));
        filterPanel.setBorder(BorderFactory.createEtchedBorder());

        filterPanel.add(new JLabel(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("FILTER:")));
        timeField = new JTextField();
        timeField.setEnabled(false);
        timeField.setToolTipText(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("DATE_EXPRESSION_IN_SQL_SYNTAX,_E.G._1992-11-%_FOR_ALL_NOVEMBER_VALUES_IN_1992"));
        timeField.setPreferredSize(new Dimension(ACTION_BUTTON_DIM.width - 20, timeField.getPreferredSize().height));
        timeField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                update();
            }

            public void removeUpdate(DocumentEvent e) {
                update();
            }

            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                toggleFreeTempMeanButton();
            }
        });

        filterPanel.add(timeField);

        button = new JButton(freeTempMean);
        button.setPreferredSize(new Dimension(ACTION_BUTTON_DIM.width - 20, ACTION_BUTTON_DIM.height));
        filterPanel.add(button);

        buttonPanelA.add(filterPanel);

        GUIHelper.addGBComponent(this, mainLayout, buttonPanelA, 40, 20, 1, 1, 0, 0);
        GUIHelper.addGBComponent(this, mainLayout, new JLabel(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("MONTHS:")), 60, 10, 1, 1, 0, 0);
        GUIHelper.addGBComponent(this, mainLayout, monthListScroll, 60, 20, 1, 1, 0, 0);
        GUIHelper.addGBComponent(this, mainLayout, new JLabel(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("YEARS:")), 70, 10, 1, 1, 0, 0);
        GUIHelper.addGBComponent(this, mainLayout, yearListScroll, 70, 20, 1, 1, 0, 0);

        JPanel buttonPanelB = new JPanel();
        buttonPanelB.setPreferredSize(LIST_DIMENSION);

        for (int i = 2; i < actions.length; i++) {
            Action a = actions[i];
            button = new JButton(a);
            button.setPreferredSize(ACTION_BUTTON_DIM);
            buttonPanelB.add(button);
        }

        buttonPanelB.add(new JPanel());

        button = new JButton(cacheReset);
        button.setPreferredSize(ACTION_BUTTON_DIM);
        buttonPanelB.add(button);

        button = new JButton(indexReset);
        button.setPreferredSize(ACTION_BUTTON_DIM);
        buttonPanelB.add(button);

        GUIHelper.addGBComponent(this, mainLayout, buttonPanelB, 80, 20, 1, 1, 0, 0);

    }

    /**
     * @return the tsproc
     */
    public SimpleSerieProcessor getProc() {
        return proc;
    }

    private void createDB() {
        workerDlg.setInderminate(false);
        workerDlg.setTask(new CancelableSwingWorker() {

            public int cancel() {
                dsdb.cancelCreateIndex();
                return -1;
            }

            public Object doInBackground() {
                try {
                    dsdb.createDB();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        });

        try {
            if (!dsdb.existsH2DB()) {
                workerDlg.execute();
            }

            if (!dsdb.existsH2DB()) {
                clearPanel();
                System.out.println("Creation canceled");
            }

            this.setProc(new SimpleSerieProcessor(dsdb));

        } catch (SQLException ex) {
        } catch (IOException ex) {
        }
    }

    private void resetIndex() {

        try {
            dsdb.clearDB();
        } catch (SQLException ex) {
            Logger.getLogger(SimpleDSPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        createDB();
    }

    private void setProc(SimpleSerieProcessor tsproc) throws SQLException, IOException {
        this.proc = tsproc;

        timeList.setModel(new AbstractListModel() {

            Object[] dates = getProc().getIDs();

            public int getSize() {
                return dates.length;
            }

            public Object getElementAt(int i) {
                return dates[i];
            }
        });
        
        yearList.setModel(new AbstractListModel() {

            int[] years = getProc().getYears();

            public int getSize() {
                return years.length;
            }

            public Object getElementAt(int i) {
                return years[i];
            }
        });

        monthList.setModel(new AbstractListModel() {

            int[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

            public int getSize() {
                return months.length;
            }

            public Object getElementAt(int i) {
                return months[i];
            }
        });

        if (!this.getProc().isTimeSerie()) {
            monthList.setEnabled(false);
            yearList.setEnabled(false);
        }

        // create the attribute panel for switching on/off attributes and
        // defining their aggregation weight
        JLabel label;

        label = new JLabel(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("AREA_ATTRIBUTE"));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        GUIHelper.addGBComponent(aggregationPanel, aggregationLayout, label, 5, 0, 1, 1, 0, 0);

        ArrayList<DataStoreProcessor.AttributeData> attribs = getProc().getDataStoreProcessor().getAttributes();

        label = new JLabel(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("AGGREGATION_WEIGHT"));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        GUIHelper.addGBComponent(aggregationPanel, aggregationLayout, label, 10, 3, 3, 1, 0, 0);
        label = new JLabel("1");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        GUIHelper.addGBComponent(aggregationPanel, aggregationLayout, label, 10, 5, 1, 1, 0, 0);
        label = new JLabel("a/A");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        GUIHelper.addGBComponent(aggregationPanel, aggregationLayout, label, 11, 5, 1, 1, 0, 0);
        label = new JLabel("l->mm");
        label.setHorizontalAlignment(SwingConstants.LEFT);
        GUIHelper.addGBComponent(aggregationPanel, aggregationLayout, label, 12, 5, 1, 1, 0, 0);

        int i = 0;
        ArrayList<JCheckBox> allChecks = new ArrayList<JCheckBox>();
        for (DataStoreProcessor.AttributeData attrib : attribs) {

            AttribCheckBox attribCheck = new AttribCheckBox(attrib);
            attribCheck.setSelected(attrib.isSelected());

            attribCheck.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    AttribCheckBox thisCheck = (AttribCheckBox) e.getSource();
                    if (!thisCheck.isSelected() && attribCombo.getSelectedItem().toString().equals(thisCheck.getText())) {
                        attribCombo.setSelectedIndex(0);
                        GUIHelper.showInfoDlg(parent, java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("AREA_ATTRIBUTE_HAS_BEEN_RESET!"), java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("INFO"));
                    }
                    thisCheck.attrib.setSelected(thisCheck.isSelected());
                }
            });

            allChecks.add(attribCheck);
            GUIHelper.addGBComponent(aggregationPanel, aggregationLayout, attribCheck, 5, i + 10, 1, 1, 0, 0);

            AttribRadioButton button1, button2, button3;
            button1 = new AttribRadioButton(attrib, DataStoreProcessor.AttributeData.AGGREGATION_NONE);
            button2 = new AttribRadioButton(attrib, DataStoreProcessor.AttributeData.AGGREGATION_WEIGHT);
            button3 = new AttribRadioButton(attrib, DataStoreProcessor.AttributeData.AGGREGATION_REL_WEIGHT);
            button1.setSelected(true);

            ItemListener attribRadioButtonListener = new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.DESELECTED) {
                        return;
                    }
                    AttribRadioButton thisButton = (AttribRadioButton) e.getSource();
                    thisButton.attrib.setAggregationType(thisButton.aggregationType);
                    setCheckBox(thisButton.attrib.getName());

                }
            };

            button1.addItemListener(attribRadioButtonListener);
            button2.addItemListener(attribRadioButtonListener);
            button3.addItemListener(attribRadioButtonListener);

            ButtonGroup bGroup = new ButtonGroup();
            bGroup.add(button1);
            bGroup.add(button2);
            bGroup.add(button3);

            GUIHelper.addGBComponent(aggregationPanel, aggregationLayout, button1, 10, i + 10, 1, 1, 0, 0);
            GUIHelper.addGBComponent(aggregationPanel, aggregationLayout, button2, 11, i + 10, 1, 1, 0, 0);
            GUIHelper.addGBComponent(aggregationPanel, aggregationLayout, button3, 12, i + 10, 1, 1, 0, 0);

            i++;
        }

        String[] attribNames = new String[attribs.size() + 1];
        attribNames[0] = java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("[CHOOSE]");
        i = 1;
        for (DataStoreProcessor.AttributeData attrib : attribs) {
            attribNames[i++] = attrib.getName();
        }

        attribCombo = new AttribComboBox(allChecks);
        attribCombo.setModel(new DefaultComboBoxModel(attribNames));
        attribCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                AttribComboBox thisCombo = (AttribComboBox) e.getSource();
                setCheckBox(thisCombo.getSelectedItem().toString());
            }
        });
        GUIHelper.addGBComponent(aggregationPanel, aggregationLayout, attribCombo, 10, 0, 5, 1, 0, 0);

        GroupCheckBox allOnOffCheck = new GroupCheckBox(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("ALL_ON/OFF"), allChecks);
        allOnOffCheck.setSelected(DataStoreProcessor.AttributeData.SELECTION_DEFAULT);

        GUIHelper.addGBComponent(aggregationPanel, aggregationLayout, allOnOffCheck, 5, 3, 1, 1, 0, 0);

        allOnOffCheck.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                GroupCheckBox thisCheck = (GroupCheckBox) e.getSource();
                boolean selected = thisCheck.isSelected();
                ArrayList<JCheckBox> allChecks = thisCheck.checkBoxList;
                for (JCheckBox checkBox : allChecks) {
                    checkBox.setSelected(selected);
                }
            }
        });

        aggregationPanel.updateUI();

        cacheReset.setEnabled(true);
        timeField.setEnabled(true);
        indexReset.setEnabled(true);

        tsproc.addProcessingProgressObserver(new Observer() {

            public void update(Observable o, Object arg) {
                workerDlg.setProgress(Integer.parseInt(arg.toString()));
            }
        });
    }

    private boolean setCheckBox(String theLabel) {

        for (JCheckBox check : attribCombo.checkBoxList) {
            if (theLabel.equals(check.getText())) {
                check.setSelected(true);
                return true;
            }
        }
        return false;
    }

    private void clearPanel() {
        timeList.setEnabled(false);
        yearList.setEnabled(false);
        monthList.setEnabled(false);
        cacheReset.setEnabled(false);
        timeField.setEnabled(false);
        indexReset.setEnabled(false);
    }
    
    private void showData() {               
        if (timeList.getSelectedValues().length == 0)                
            return;
                
        workerDlg.setInderminate(false);
        workerDlg.setProgress(0);
        workerDlg.setTask(new CancelableSwingWorker() {

            DataMatrix m = null;

            public Object doInBackground() {
                try {                                                            
                    SimpleSerieProcessor proc = getProc();
                    // check if number of selected ids is equal to all ids
                    // if so, we better derive temp avg from monthly means
                    Object[] objects = timeList.getSelectedValues();

                    String[] ids = new String[objects.length];
                    int c = 0;
                    for (Object o : objects) {
                        ids[c++] = o.toString();
                    }
                    m = proc.getData(ids);                                                        
                } catch (SQLException ex) {
                } catch (IOException ex) {
                }
                return null;
            }

            @Override
            public void done() {
                loadData(m, proc.isTimeSerie());
            }

            public int cancel() {
                getProc().sendAbortOperation();
                return 0;
            }
        });
        workerDlg.execute();
    }
    
    private void showMonthlyMean() {

        if (monthList.getSelectedValues().length == 0) {
            return;
        }

        workerDlg.setInderminate(false);
        workerDlg.setProgress(0);
        workerDlg.setTask(new CancelableSwingWorker() {

            DataMatrix m;

            public Object doInBackground() {
                try {

                    int month = (Integer) monthList.getSelectedValue();

                    workerDlg.setInderminate(true);
                        
                    m = getProc().getMonthlyMean(month);

                } catch (SQLException ex) {
                    System.out.println(ex);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    System.out.println(ex);
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            public void done() {
                loadData(m, false);
            }

            public int cancel() {
                proc.sendAbortOperation();
                return 0;
            }
        });
        workerDlg.execute();
    }

    private void showYearlyMean() {

        if (yearList.getSelectedValues().length == 0) {
            return;
        }

        workerDlg.setInderminate(false);
        workerDlg.setProgress(0);
        workerDlg.setTask(new CancelableSwingWorker() {

            DataMatrix m;

            public Object doInBackground() {
                try {

                    int year = (Integer) yearList.getSelectedValue();

                    m = proc.getYearlyMean(year);
                    workerDlg.setInderminate(true);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            public void done() {
                loadData(m, false);
            }

            public int cancel() {
                proc.sendAbortOperation();
                return 0;
            }
        });
        workerDlg.execute();
    }

    private void showMean() {               
        if (timeList.getSelectedValues().length == 0)                
            return;
                
        workerDlg.setInderminate(false);
        workerDlg.setProgress(0);
        workerDlg.setTask(new CancelableSwingWorker() {

            DataMatrix m = null;

            public Object doInBackground() {
                try {                                                            
                    SimpleSerieProcessor proc = getProc();
                    // check if number of selected ids is equal to all ids
                    // if so, we better derive temp avg from monthly means
                    Object[] objects = timeList.getSelectedValues();

                    String[] ids = new String[objects.length];
                    int c = 0;
                    for (Object o : objects) {
                        ids[c++] = o.toString();
                    }
                    m = proc.getMean(ids);                                                        
                } catch (SQLException ex) {
                } catch (IOException ex) {
                }
                return null;
            }

            @Override
            public void done() {
                loadData(m, true);
            }

            public int cancel() {
                getProc().sendAbortOperation();
                return 0;
            }
        });
        workerDlg.execute();
    }
    
    private void showFreeTempMean() {

        String filter = timeField.getText();
        if (!filter.contains("%") && !filter.contains("?")) {
            return;
        }

        workerDlg.setInderminate(false);
        workerDlg.setProgress(0);
        workerDlg.setTask(new CancelableSwingWorker() {

            DataMatrix m;

            public Object doInBackground() {
                try {
                    String filter = timeField.getText();
                    m = getProc().calcTemporalMean(filter);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                }
                return null;
            }

            public void done() {
                loadData(m, false);
            }

            public int cancel() {
                getProc().sendAbortOperation();
                return 0;
            }
        });
        workerDlg.execute();
    }
    
    private void toggleFreeTempMeanButton() {
        String filter = timeField.getText();
        if (!filter.contains("%") && !filter.contains("?")) {
            freeTempMean.setEnabled(false);
        } else {
            freeTempMean.setEnabled(true);
        }
    }

    private void resetCaches() {
        /*try {
            //getProc().deleteCache();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }*/
    }

    public void createProc(File file) {

        workerDlg.setTitle(workerDlg.getTitle() + " [" + file.getName() + "]");
        dsdb = new DataStoreProcessor(file);
        dsdb.addImportProgressObserver(new Observer() {

            public void update(Observable o, Object arg) {
                workerDlg.setProgress(Integer.parseInt(arg.toString()));
            }
        });
        createDB();

        this.outputDSFile = file;
    }
}
