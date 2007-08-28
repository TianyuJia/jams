/*
 * ModelView.java
 * Created on 12. Mai 2006, 08:25
 *
 * This file is part of JAMS
 * Copyright (C) 2005 FSU Jena
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
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.unijena.jams.JAMS;
import org.unijena.jams.JAMSProperties;
import org.unijena.jams.gui.LHelper;
import org.unijena.jams.io.XMLIO;
import org.unijena.jams.runtime.JAMSRuntime;
import org.unijena.jams.runtime.StandardRuntime;
import org.unijena.juice.tree.ComponentDescriptor;
import org.unijena.juice.tree.ModelTree;
import org.w3c.dom.Document;

/**
 *
 * @author S. Kralisch
 */
public class ModelView {
    
    private static final int TREE_PANE_WIDTH = 250;
    private static final int DIVIDER_WIDTH = 9;
    
    private JInternalFrame frame;
    private File savePath;
    private Document modelDoc, initialDoc;
    private JButton modelStopButton, modelRunButton;
    private ModelTree tree;
    private CompEditPanel compEditPanel;
    private HashMap<ComponentDescriptor, DataRepository> dataRepositories = new HashMap<ComponentDescriptor, DataRepository>();
    private LauncherPanel launcherPanel;
    private ModelEditPanel modelEditPanel;
    private String author, date, description;
    private HashMap<String, ComponentDescriptor> componentDescriptors = new HashMap<String, ComponentDescriptor>();
    private TreePanel modelTreePanel;
    private JDesktopPane parentPanel;
    private boolean modified = false;
    private ModelProperties modelProperties = new ModelProperties();
    private WorkerDlg setupModelDlg;
    private Runnable modelLoading;
    
    private static boolean firstFrame = true;
    private static int viewCounter = 0;
    public static ViewList viewList = new ViewList();
    
    private JAMSRuntime runtime;
    
    public ModelView(JDesktopPane parentPanel) {
        this(getNextViewName(), parentPanel);
    }
    
    public ModelView(String title, JDesktopPane parentPanel) {
        
        this.parentPanel = parentPanel;
        compEditPanel = new CompEditPanel(this);
        modelEditPanel = new ModelEditPanel(this);
        launcherPanel = new LauncherPanel(this);
        modelTreePanel = new TreePanel();
        
        modelLoading = new Runnable() {
            public void run() {
                try {
                    // create the runtime
                    runtime = new StandardRuntime();
                    
                    // add info and error log output
                    runtime.addInfoLogObserver(new Observer() {
                        public void update(Observable obs, Object obj) {
                            JUICE.getJuiceFrame().getInfoDlg().appendText(obj.toString());
                        }
                    });
                    runtime.addErrorLogObserver(new Observer() {
                        public void update(Observable obs, Object obj) {
                            JUICE.getJuiceFrame().getErrorDlg().appendText(obj.toString());
                        }
                    });
                    
                    // load the model
                    runtime.loadModel(modelDoc, JUICE.getJamsProperties());
                } catch (Exception e) {}
            }
        };
        
        setupModelDlg = new WorkerDlg(JUICE.getJuiceFrame(), "Setting up the model");
        //compEditPanel.setPreferredSize(new Dimension(200,200));
        
        JPanel modelPanel = new JPanel();
        
        frame = new JInternalFrame();
        
        frame.setClosable(true);
        frame.setIconifiable(true);
        frame.setMaximizable(true);
        frame.setResizable(true);
        frame.setTitle(title);
        frame.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/ContextComponent_s.png")));
        //frame.setVisible(true);
        frame.setBounds(0, 0, 600, 600);
        frame.addInternalFrameListener(new InternalFrameListener() {
            public void internalFrameActivated(InternalFrameEvent evt) {
            }
            public void internalFrameClosed(InternalFrameEvent evt) {
            }
            public void internalFrameClosing(InternalFrameEvent evt) {
                exit();
            }
            public void internalFrameDeactivated(InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(InternalFrameEvent evt) {
            }
            public void internalFrameIconified(InternalFrameEvent evt) {
            }
            public void internalFrameOpened(InternalFrameEvent evt) {
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        
        JSplitPane modelSplitPane = new JSplitPane();
        modelSplitPane.setAutoscrolls(true);
        modelSplitPane.setContinuousLayout(true);
        modelSplitPane.setLeftComponent(modelTreePanel);
        
        JSplitPane compEditSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        
        /*
        JSplitPane infoSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        infoSplitPane.setTopComponent(new JScrollPane(modelEditPanel));
        infoSplitPane.setBottomComponent(new JScrollPane(launcherPanel));
        infoSplitPane.setDividerLocation(400);
         */
        //compEditSplitPane.setBottomComponent(infoSplitPane);
        
        //compEditSplitPane.setTopComponent(new JScrollPane(compEditPanel));
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Component", new JScrollPane(compEditPanel));
        tabPane.addTab("Model", new JScrollPane(modelEditPanel));
        compEditSplitPane.setTopComponent(tabPane);
        compEditSplitPane.setBottomComponent(new JScrollPane(launcherPanel));
        compEditSplitPane.setDividerLocation(400);
        
        modelSplitPane.setRightComponent(compEditSplitPane);
        modelSplitPane.setDividerLocation(TREE_PANE_WIDTH);
        modelSplitPane.setOneTouchExpandable(true);
        modelSplitPane.setDividerSize(DIVIDER_WIDTH);
        
        frame.getContentPane().add(modelSplitPane, BorderLayout.CENTER);
        
        JToolBar toolBar = new JToolBar();
        toolBar.setPreferredSize(new Dimension(0, 40));
        
        modelRunButton = new JButton();
        modelRunButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/ModelRun.png")));
        modelRunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runModel();
            }
        });
        modelRunButton.setEnabled(true);
        toolBar.add(modelRunButton);
        
        modelStopButton = new JButton();
        modelStopButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/ModelStop.png")));
        modelStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //ModelExecutor.modelStop();
            }
        });
        modelStopButton.setEnabled(false);
        //toolBar.add(modelStopButton);
        
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);
        
        ModelView.viewList.addView(frame, this);
        
        parentPanel.add(frame, JLayeredPane.DEFAULT_LAYER);
        
        try {
            frame.setSelected(true);
            if (firstFrame) {
                frame.setMaximum(true);
                firstFrame = false;
            }
        } catch (PropertyVetoException pve) {
            JAMS.handle(pve);
        }
        
    }
    
    public void runModel() {
/*
        modelDoc = tree.getModelDocument();
 
        JAMSLauncher launcher = new JAMSLauncher(modelDoc, JUICE.getJamsProperties());
        launcher.setVisible(true);
 */
        launcherPanel.updateProperties();
        if (!launcherPanel.verifyInputs()) {
            return;
        }
        modelDoc = tree.getModelDocument();
        
        // first load the model via the modelLoading runnable
        setupModelDlg.setTask(modelLoading);
        setupModelDlg.execute();
        
        // then execute it
        Thread t = new Thread() {
            public void run() {
                
                // start the model
                runtime.runModel();
                
                JUICE.getJuiceFrame().getInfoDlg().appendText("\n\n");
                JUICE.getJuiceFrame().getErrorDlg().appendText("\n\n");
                
                runtime = null;
                Runtime.getRuntime().gc();
            }
        };
        try {
            t.start();
        } catch (Exception e) {
            runtime.handle(e);
        }
    }
    
    public static void runModel(JAMSProperties properties, Document modelDoc) throws NumberFormatException {
        
        // create the runtime
        JAMSRuntime runtime = new StandardRuntime();
        
        // add info and error log output
        runtime.addInfoLogObserver(new Observer() {
            public void update(Observable obs, Object obj) {
                JUICE.getJuiceFrame().getInfoDlg().appendText(obj.toString());
            }
        });
        runtime.addErrorLogObserver(new Observer() {
            public void update(Observable obs, Object obj) {
                JUICE.getJuiceFrame().getErrorDlg().appendText(obj.toString());
            }
        });
        
        // load the model
        runtime.loadModel(modelDoc, properties);
        
        // start the model
        runtime.runModel();
        
        JUICE.getJuiceFrame().getInfoDlg().appendText("\n\n");
        JUICE.getJuiceFrame().getErrorDlg().appendText("\n\n");
        
        runtime = null;
        Runtime.getRuntime().gc();
    }
    
    public static String getNextViewName() {
        viewCounter++;
        return "Model"+viewCounter;
    }
    
    public boolean save() {
        
        boolean result = false;
        
        try {
            result = XMLIO.writeXmlFile(tree.getModelDocument(), savePath);
        } catch (IOException ioe) {
            LHelper.showErrorDlg(JUICE.getJuiceFrame(), "Error saving configuration to " + savePath.toString(), "Error");
            return false;
        }
        if (result) {
            setInitialState();
        }
        
        return result;
    }
    
    public ModelTree getTree() {
        return tree;
    }
    
    public void setTree(ModelTree tree) {
        this.tree = tree;
        modelTreePanel.setTree(tree);
        this.launcherPanel.updatePanel();
    }
    
    public TreePanel getModelTreePanel() {
        return modelTreePanel;
    }
    
    public void setModelTreePanel(TreePanel modelTreePanel) {
        this.modelTreePanel = modelTreePanel;
    }
    
    public File getSavePath() {
        return savePath;
    }
    
    public void setSavePath(File savePath) {
        
        if (savePath != null) {
            if (!(savePath.getAbsolutePath().endsWith(".jam") || savePath.getAbsolutePath().endsWith(".xml"))) {
                savePath = new File(savePath.getAbsolutePath() + ".jam");
            }
            frame.setTitle(savePath.getAbsolutePath());
        }
        this.savePath = savePath;
    }
    
    public String getName(String name) {
        
        Set<String> names = getComponentDescriptors().keySet();
        
        if (!names.contains(name)) {
            return name;
        }
        
        int i = 1;
        String result = name + "_" + i;
        
        while (names.contains(result)) {
            i++;
            result = name + "_" + i;
        }
        
        return result;
    }
    
    public boolean exit() {
        
        boolean returnValue = false;
        
        launcherPanel.updateProperties();
        
        if (tree == null) {
            return true;
        }
        
        String newXMLString = XMLIO.getStringFromDocument(tree.getModelDocument());
        String oldXMLString = XMLIO.getStringFromDocument(initialDoc);
        
        if (modified || (newXMLString.compareTo(oldXMLString) != 0)) {
            int result = LHelper.showYesNoCancelDlg(JUICE.getJuiceFrame(), "Save modifications in " + this.getFrame().getTitle() + "?", "JUICE: unsaved modifications");
            if (result == JOptionPane.OK_OPTION) {
                JUICE.getJuiceFrame().saveModel(this);
                ModelView.viewList.removeView(this.getFrame());
                this.getFrame().dispose();
                returnValue = true;
            } else if (result == JOptionPane.NO_OPTION) {
                ModelView.viewList.removeView(this.getFrame());
                this.getFrame().dispose();
                returnValue = true;
            }
        } else {
            ModelView.viewList.removeView(this.getFrame());
            this.getFrame().dispose();
            parentPanel.updateUI();
            returnValue = true;
        }
        return returnValue;
    }
    
    public Document getModelDoc() {
        return modelDoc;
    }
    
    public void setModelDoc(Document modelDoc) {
        this.modelDoc = modelDoc;
    }
    
    public JInternalFrame getFrame() {
        return frame;
    }
    
    public CompEditPanel getCompEditPanel() {
        return compEditPanel;
    }
    
    public ModelEditPanel getModelEditPanel() {
        return modelEditPanel;
    }
    
    public void setCompEditPanel(CompEditPanel compEditPanel) {
        this.compEditPanel = compEditPanel;
    }
    
    public DataRepository getDataRepository(ComponentDescriptor context) {
        DataRepository repo = dataRepositories.get(context);
        if (repo == null) {
            repo = new DataRepository();
            dataRepositories.put(context, repo);
        }
        return repo;
    }
    
    public HashMap<ComponentDescriptor, DataRepository> getDataRepositories() {
        return dataRepositories;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setModified(boolean modified) {
        this.modified = modified;
    }
    
    public ComponentDescriptor getComponentDescriptor(String name) {
        return this.getComponentDescriptors().get(name);
    }
    
    public String registerComponentDescriptor(String oldName, String newName, ComponentDescriptor cd) {
        
        String newNewName = getName(newName);
        this.getComponentDescriptors().remove(oldName);
        this.getComponentDescriptors().put(newNewName, cd);
        
        return newNewName;
    }
    
    public void setInitialState() {
        this.initialDoc = tree.getModelDocument();
    }
    
    public ModelProperties getModelProperties() {
        return modelProperties;
    }
    
    public HashMap<String, ComponentDescriptor> getComponentDescriptors() {
        return componentDescriptors;
    }
    
    
}
