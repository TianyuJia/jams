/*
 * DSTree.java
 * Created on 19. November 2008, 17:58
 *
 * This file is part of JAMS
 * Copyright (C) 2008 FSU Jena
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
package reg.tree;

import jams.workspace.stores.InputDataStore;
import jams.workspace.stores.J2KTSDataStore;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import reg.JAMSExplorer;
import reg.hydro.HydroAnalysisPanel;

/**
 *
 * @author S. Kralisch
 */
public class DSTree extends JAMSTree {

    private static final String ROOT_NAME = java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("DATENSPEICHER"), INPUT_NAME = java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("EINGABEDATEN"), OUTPUT_NAME = java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("AUSGABEDATEN");

    private JPopupMenu popupDS;
    private JPopupMenu popupIDS;
    private JPopupMenu popupDir;

    private NodeObservable nodeObservable = new NodeObservable();

    private JAMSExplorer explorer;

    public DSTree(JAMSExplorer explorer) {
        super();

        this.explorer = explorer;
        setEditable(false);

        JMenuItem detailItem = new JMenuItem(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("ZEIGE_DATEN"));
        //detailItem.setAccelerator(KeyStroke.getKeyStroke('D'));
        detailItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                displayDSData();
            }
        });

        JMenuItem hydroAnalyseItem = new JMenuItem("Hydrograph Analysis");
        //detailItem.setAccelerator(KeyStroke.getKeyStroke('D'));
        hydroAnalyseItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                showHydrograph();
            }
        });

        JMenuItem deleteFileItem = new JMenuItem(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("DELETE"));
        deleteFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteFileItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                deleteDSFile();
            }
        });

        JMenuItem deleteDirItem = new JMenuItem(java.util.ResourceBundle.getBundle("reg/resources/JADEBundle").getString("DELETE"));
        deleteDirItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteDirItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                deleteDSDir();
            }
        });

        popupDS = new JPopupMenu();
        popupDS.add(detailItem);
        popupDS.add(deleteFileItem);

        popupIDS = new JPopupMenu();
        popupIDS.add(detailItem);
        popupIDS.add(deleteFileItem);
        popupIDS.add(hydroAnalyseItem);

        popupDir = new JPopupMenu();
        popupDir.add(deleteDirItem);

        addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON3) {
                    showPopup(evt);
                }
            }

            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() >= 2) {
                    displayDSData();
                }
            }
        });

        addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (getSelectionPath() != null) {
                    nodeObservable.setNode((DSTreeNode) getLastSelectedPathComponent());
                } else {
                    nodeObservable.setNode(null);
                }
            }
        });

        this.setVisible(false);
    }

    private DSTreeNode createInputNode() {
        DSTreeNode inputRoot = new DSTreeNode(INPUT_NAME, DSTreeNode.INPUT_ROOT);
        Set<String> inIDs = explorer.getWorkspace().getInputDataStoreIDs();
        List<String> inIDList = explorer.getWorkspace().getSortedInputDataStoreIDs();
        for (String id : inIDList) {
            DSTreeNode dsNode = new DSTreeNode(id, DSTreeNode.INPUT_DS);
            inputRoot.add(dsNode);
        }
        return inputRoot;
    }

    private DSTreeNode createOutputNode() {
        DSTreeNode outputRoot = new DSTreeNode(OUTPUT_NAME, DSTreeNode.OUTPUT_ROOT);

        File[] outputDirs = explorer.getWorkspace().getOutputDataDirectories();
        for (File dir : outputDirs) {
            DSTreeNode outputDataDirNode = new DSTreeNode(dir.getName(), DSTreeNode.OUTPUT_DIR);
            for (File file : explorer.getWorkspace().getOutputDataFiles(dir)) {
                DSTreeNode outputDataStoreNode = new DSTreeNode(new FileObject(file), DSTreeNode.OUTPUT_DS);
                outputDataDirNode.add(outputDataStoreNode);
            }
            outputRoot.add(outputDataDirNode);
        }

//        List<String> outIDList = new ArrayList<String>(workspace.getOutputDataStoreIDs());
//        Collections.sort(outIDList);
//        for (String id : outIDList) {
//            DSTreeNode dsNode = new DSTreeNode(id, DSTreeNode.OUTPUT_DS);
//            outputRoot.add(dsNode);
//            //!!!take care of different result sets here!!!
//        }
        return outputRoot;
    }

    private void showHydrograph(){
        DSTreeNode node = (DSTreeNode) getLastSelectedPathComponent();
        String dsID = node.toString();
        InputDataStore store = explorer.getWorkspace().getInputDataStore(dsID);
        if (store instanceof J2KTSDataStore){
            JDialog hydroAnalysisDialog = new JDialog();
            hydroAnalysisDialog.setLayout(new BorderLayout());
            hydroAnalysisDialog.setPreferredSize(new Dimension(800,500));
            hydroAnalysisDialog.setMinimumSize(new Dimension(800,500));
            hydroAnalysisDialog.add(new HydroAnalysisPanel(this.explorer.getExplorerFrame(), (J2KTSDataStore)store ));
            hydroAnalysisDialog.invalidate();
            hydroAnalysisDialog.pack();
            hydroAnalysisDialog.setVisible(true);
        }else{
            JOptionPane.showMessageDialog(this, "unsuitable datastore");
        }
    }

    private void displayDSData() {
        explorer.getDisplayManager().displayDS((DSTreeNode) getLastSelectedPathComponent());
    }

    private void deleteDSFile(){
        explorer.getDisplayManager().deleteDS((DSTreeNode) getLastSelectedPathComponent());
    }
    private void deleteDSDir(){
        explorer.getDisplayManager().deleteDS((DSTreeNode) getLastSelectedPathComponent());
    }

    private void showPopup(MouseEvent evt) {
        TreePath p = this.getClosestPathForLocation(evt.getX(), evt.getY());
        this.setSelectionPath(p);
        DSTreeNode node = (DSTreeNode) this.getLastSelectedPathComponent();

        if ((node != null) && ((node.getType() == DSTreeNode.INPUT_DS) || (node.getType() == DSTreeNode.OUTPUT_DS))) {
            if ((node != null) && ((node.getType() == DSTreeNode.INPUT_DS))) {
                InputDataStore store = explorer.getWorkspace().getInputDataStore(node.toString());
                if (store instanceof J2KTSDataStore)
                    popupIDS.show(this, evt.getX(), evt.getY());
                else
                    popupDS.show(this, evt.getX(), evt.getY());
            }else
                popupDS.show(this, evt.getX(), evt.getY());
        }
        if ((node != null) && ((node.getType() == DSTreeNode.OUTPUT_DIR))) {
            popupDir.show(this, evt.getX(), evt.getY());
        }
    }

    public void update() {
        this.setVisible(false);
        DSTreeNode root = createIOTree();
        this.setModel(new DefaultTreeModel(root));
        this.expandAll();
        this.setVisible(true);
    }

    private DSTreeNode createIOTree() {

        DSTreeNode root = new DSTreeNode(ROOT_NAME, DSTreeNode.IO_ROOT);
        DSTreeNode inputRoot = createInputNode();
        DSTreeNode outputRoot = createOutputNode();

        root.add(inputRoot);
        root.add(outputRoot);

        return root;
    }

    public void addObserver(Observer o) {
        nodeObservable.addObserver(o);
    }

    private class NodeObservable extends Observable {

        DSTreeNode node;

        public void setNode(DSTreeNode node) {
            this.node = node;
            this.setChanged();
            notifyObservers();
        }

        @Override
        public void notifyObservers(Object arg) {
            super.notifyObservers(node);
        }
    }
}
