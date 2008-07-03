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
package org.unijena.juice.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.unijena.jams.JAMS;
import org.unijena.jams.JAMSTools;
import org.unijena.jams.data.HelpComponent;
import org.unijena.jams.gui.LHelper;
import org.unijena.jams.gui.WorkerDlg;
import org.unijena.jams.io.XMLIO;
import org.unijena.jams.runtime.JAMSRuntime;
import org.unijena.jams.runtime.StandardRuntime;
import org.unijena.juice.*;
import org.unijena.juice.ComponentDescriptor;
import org.unijena.juice.ModelProperties.Group;
import org.unijena.juice.ModelProperties.ModelElement;
import org.unijena.juice.ModelProperties.ModelProperty;
import org.unijena.juice.gui.tree.ModelTree;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author S. Kralisch
 */
public class ModelView {

    private static final int TREE_PANE_WIDTH = 250;
    private static final int DIVIDER_WIDTH = 9;
    private JInternalFrame frame;
    private File savePath;
    private Document modelDoc;
    private Document initialDoc;
    private JButton modelRunButton;
    private ModelTree tree;
    private ComponentPanel compEditPanel;
    //private HashMap<ComponentDescriptor, DataRepository> dataRepositories = new HashMap<ComponentDescriptor, DataRepository>();
    private LauncherPanel launcherPanel;
    private ModelEditPanel modelEditPanel;
    private String author;
    private String date;
    private String description;
    private String helpBaseUrl;
    private HashMap<String, ComponentDescriptor> componentDescriptors = new HashMap<String, ComponentDescriptor>();
    private TreePanel modelTreePanel;
    private JDesktopPane parentPanel;
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
        compEditPanel = new ComponentPanel(this);
        modelEditPanel = new ModelEditPanel(this);
        launcherPanel = new LauncherPanel(this);
        modelTreePanel = new TreePanel();

        modelLoading = new Runnable() {

            @Override
            public void run() {
                try {
                    // create the runtime
                    runtime = new StandardRuntime();

                    // add info and error log output
                    runtime.addInfoLogObserver(new Observer() {

                        @Override
                        public void update(Observable obs, Object obj) {
                            JUICE.getJuiceFrame().getInfoDlg().appendText(obj.toString());
                        }
                    });
                    runtime.addErrorLogObserver(new Observer() {

                        @Override
                        public void update(Observable obs, Object obj) {
                            JUICE.getJuiceFrame().getErrorDlg().appendText(obj.toString());
                        }
                    });

                    // load the model
                    runtime.loadModel(getModelDoc(), JUICE.getJamsProperties());
                } catch (Exception e) {
                }
            }
        };

        setupModelDlg = new WorkerDlg(JUICE.getJuiceFrame(), "Model Setup");
        //compEditPanel.setPreferredSize(new Dimension(200,200));

        frame = new JInternalFrame();

        frame.setClosable(true);
        frame.setIconifiable(true);
        frame.setMaximizable(true);
        frame.setResizable(true);
        frame.setTitle(title);
        frame.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/ContextComponent_si.png")));
        //frame.setVisible(true);
        frame.setBounds(0, 0, 600, 600);
        frame.addInternalFrameListener(new InternalFrameListener() {

            @Override
            public void internalFrameActivated(InternalFrameEvent evt) {
            }

            @Override
            public void internalFrameClosed(InternalFrameEvent evt) {
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent evt) {
                exit();
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent evt) {
            }

            @Override
            public void internalFrameDeiconified(InternalFrameEvent evt) {
            }

            @Override
            public void internalFrameIconified(InternalFrameEvent evt) {
            }

            @Override
            public void internalFrameOpened(InternalFrameEvent evt) {
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        /*
         * create the toolbar
         */
        JToolBar toolBar = new JToolBar();
        toolBar.setPreferredSize(new Dimension(0, JAMS.TOOLBAR_HEIGHT));

        modelRunButton = new JButton();
        //modelRunButton.setPreferredSize(new Dimension(40,40));
        modelRunButton.setToolTipText("Start model");
        modelRunButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/ModelRun.png")));
        modelRunButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runModel();
            }
        });
        modelRunButton.setEnabled(false);
        toolBar.add(modelRunButton);

        JButton copyGUIButton = new JButton(JUICE.getJuiceFrame().getCopyModelGUIAction());
        copyGUIButton.setText("");
        copyGUIButton.setToolTipText("Copy Model GUI");
        copyGUIButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/Copy.png")));
        toolBar.add(copyGUIButton);

        JButton pasteGUIButton = new JButton(JUICE.getJuiceFrame().getPasteModelGUIAction());
        pasteGUIButton.setText("");
        pasteGUIButton.setToolTipText("Paste Model GUI");
        pasteGUIButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/Paste.png")));
        toolBar.add(pasteGUIButton);

        /*
         * create the splitpane
         */
        JSplitPane modelSplitPane = new JSplitPane();
        modelSplitPane.setAutoscrolls(true);
        modelSplitPane.setContinuousLayout(true);

        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Component", new JScrollPane(compEditPanel));
        tabPane.addTab("Model metadata", new JScrollPane(modelEditPanel));
        tabPane.addTab("Model GUI", new JScrollPane(launcherPanel));

        modelSplitPane.setLeftComponent(modelTreePanel);
        modelSplitPane.setRightComponent(tabPane);
        modelSplitPane.setDividerLocation(TREE_PANE_WIDTH);
        modelSplitPane.setOneTouchExpandable(true);
        modelSplitPane.setDividerSize(DIVIDER_WIDTH);


        /*
         * add everything to the frame
         */
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);
        frame.getContentPane().add(modelSplitPane, BorderLayout.CENTER);

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

            @Override
            public void run() {

                // start the model
                runtime.runModel();

                JUICE.getJuiceFrame().getInfoDlg().appendText("\n\n");
                JUICE.getJuiceFrame().getErrorDlg().appendText("\n\n");

                //dump the runtime and clean up
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

    public static String getNextViewName() {
        viewCounter++;
        return "Model" + viewCounter;
    }

    public boolean save() {

        boolean result = false;

        launcherPanel.updateProperties();

        Document doc = tree.getModelDocument();

        try {
            result = XMLIO.writeXmlFile(doc, savePath);
        } catch (IOException ioe) {
            return false;
        }

        return result;
    }

    public ModelTree getTree() {
        return tree;
    }

    public void setTree(ModelTree tree) {
        this.tree = tree;
        modelTreePanel.setTree(tree);
        updateLauncherPanel();
        modelRunButton.setEnabled(true);
    }

    public void updateLauncherPanel() {
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

    /*
     * Create a new name for a component instance.
     * If possible, use the given name, else add a suffix in order to create a unique one.
     */
    public String createComponentInstanceName(String name) {

        Set<String> names = getComponentDescriptors().keySet();

        if (!names.contains(name)) {
            return name;
        }

        String[] sArray = JAMSTools.toArray(name, "_");
        if (sArray.length > 1) {
            String suffix = "_" + sArray[sArray.length - 1];
            name = name.substring(0, name.length() - suffix.length());
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

        if (newXMLString.compareTo(oldXMLString) != 0) {
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

    public void setModelParameters(Element launcherNode) {
        Node node;

        if (launcherNode != null) {
            getModelProperties().removeAll();
            NodeList groupNodes = launcherNode.getElementsByTagName("group");
            for (int gindex = 0; gindex < groupNodes.getLength(); gindex++) {
                node = groupNodes.item(gindex);
                Element groupElement = (Element) node;
                String groupName = groupElement.getAttribute("name");
                getModelProperties().addGroup(groupName);
                Group group = getModelProperties().getGroup(groupName);

                // @todo subgroups and properties recursive
                NodeList groupChildNodes = groupElement.getChildNodes();
                for (int pindex = 0; pindex < groupChildNodes.getLength(); pindex++) {
                    node = groupChildNodes.item(pindex);
                    if (node.getNodeName().equalsIgnoreCase("property")) {
                        Element propertyElement = (Element) node;
                        ModelProperty property = getPropertyFromElement(propertyElement);
                        if (property != null) {
                            getModelProperties().addProperty(group, property);
                        }
                    }
                    if (node.getNodeName().equalsIgnoreCase("subgroup")) {
                        Element subgroupElement = (Element) node;
                        String subgroupName = subgroupElement.getAttribute("name");
                        Group subgroup = getModelProperties().createSubgroup(group, subgroupName);
                        setHelpComponent(subgroupElement, subgroup);

                        NodeList propertyNodes = subgroupElement.getElementsByTagName("property");
                        for (int kindex = 0; kindex < propertyNodes.getLength(); kindex++) {
                            Element propertyElement = (Element) propertyNodes.item(kindex);
                            ModelProperty property = getPropertyFromElement(propertyElement);
                            if (property != null) {
                                getModelProperties().addProperty(subgroup, property);
                            }
                        }
                    }
                }
            }
        }
        return;
    }

    private ModelProperty getPropertyFromElement(Element propertyElement) {
        ModelProperties.ModelProperty property = getModelProperties().createProperty();
        property.component = getComponentDescriptor(propertyElement.getAttribute("component"));

        if (property.component == null) {
            LHelper.showErrorDlg(JUICE.getJuiceFrame(), "Component \"" + propertyElement.getAttribute("component") +
                    "\" does not exist, but is referred in list of model parameters!\n" +
                    "Will be removed when model is saved!", "Model loading error");
            return null;
        }

        String attributeName = propertyElement.getAttribute("attribute");

        property.var = property.component.getComponentAttributes().get(attributeName);

        //in case this is a context component, check whether this refers to a context attribute
        if (property.attribute == null) {
            property.attribute = property.component.getContextAttributes().get(attributeName);
        }

        //check wether the referred var is existing or not
        if ((property.attribute == null) && (property.var == null) && !attributeName.equals("%enable%")) {
            LHelper.showErrorDlg(JUICE.getJuiceFrame(), "Attribute " + attributeName +
                    " does not exist in component " + property.component.getName() +
                    ". Removing associated property!", "Model loading error");
            return null;
        }

        property.defaultValue = propertyElement.getAttribute("default");
        property.description = propertyElement.getAttribute("description");
        property.name = propertyElement.getAttribute("name");
        property.value = propertyElement.getAttribute("value");
        String range = propertyElement.getAttribute("range");
        StringTokenizer tok = new StringTokenizer(range, ";");
        if (tok.countTokens() == 2) {
            property.lowerBound = Double.parseDouble(tok.nextToken());
            property.upperBound = Double.parseDouble(tok.nextToken());
        }
        String lenStr = propertyElement.getAttribute("length");
        if (lenStr != null && lenStr.length() > 0) {
            property.length = Integer.parseInt(lenStr);
        }
        setHelpComponent(propertyElement, property);

        return property;

    }

    /**
     * Return an XML document descibing the model.
     * @return The XML document describing the model.
     */
    public Document getModelDoc() {
        launcherPanel.updateProperties();
        if (!launcherPanel.verifyInputs()) {
            return null;
        }
        return tree.getModelDocument();
    }

    /**
     * Loads a JAMS model from file
     * @param fileName The file containing the models XML document.
     */
    public void loadModel(String fileName) {
        try {
            this.setSavePath(new File(fileName));
            this.setTree(new ModelTree(this, XMLIO.getDocument(fileName)));
        } catch (FileNotFoundException fnfe) {
            LHelper.showErrorDlg(JUICE.getJuiceFrame(), "File " + fileName + " could not be loaded.", "File open error");
        }
        this.setInitialState();
    }

    public JInternalFrame getFrame() {
        return frame;
    }

    public ComponentPanel getCompEditPanel() {
        return compEditPanel;
    }

    public ModelEditPanel getModelEditPanel() {
        return modelEditPanel;
    }

    public void setCompEditPanel(ComponentPanel compEditPanel) {
        this.compEditPanel = compEditPanel;
    }

    /*
    public DataRepository getDataRepository(ComponentDescriptor context) {
    DataRepository repo = dataRepositories.get(context);
    if (repo == null) {
    repo = new DataRepository(context);
    dataRepositories.put(context, repo);
    }
    return repo;
    }
    
    public HashMap<ComponentDescriptor, DataRepository> getDataRepositories() {
    return dataRepositories;
    }
     */
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

    public String getHelpBaseUrl() {
        return helpBaseUrl;
    }

    public void setHelpBaseUrl(String helpBaseUrl) {
        this.helpBaseUrl = helpBaseUrl;
    }

    public ComponentDescriptor getComponentDescriptor(String name) {
        return this.getComponentDescriptors().get(name);
    }

    public String registerComponentDescriptor(String oldName, String newName, ComponentDescriptor cd) {

        String newNewName = createComponentInstanceName(newName);
        this.getComponentDescriptors().remove(oldName);
        this.getComponentDescriptors().put(newNewName, cd);

        return newNewName;
    }

    public void unRegisterComponentDescriptor(ComponentDescriptor cd) {
        this.getComponentDescriptors().remove(cd.getName());
    }

    public void setInitialState() {
        this.initialDoc = tree.getModelDocument();
    }

    public ModelProperties getModelProperties() {
        return modelProperties;
    }

    public void setModelProperties(ModelProperties modelProperties) {
        this.modelProperties = modelProperties;
    }

    public HashMap<String, ComponentDescriptor> getComponentDescriptors() {
        return componentDescriptors;
    }

    private void setHelpComponent(Element theElement, ModelElement theModelElement) throws DOMException {
        // get help component from help node
        HelpComponent helpComponent = new HelpComponent(theElement);
        theModelElement.setHelpComponent(helpComponent);
    }
}    
    
    