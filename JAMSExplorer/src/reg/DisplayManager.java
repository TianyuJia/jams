/*
 * DisplayManager.java
 * Created on 21. November 2008, 15:55
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
package reg;

import jams.gui.GUIHelper;
import java.io.FileNotFoundException;
import java.io.IOException;
import reg.spreadsheet.JAMSSpreadSheet;
import jams.workspace.stores.InputDataStore;
import jams.workspace.stores.StandardInputDataStore;
import jams.workspace.stores.TSDataStore;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import reg.gui.InputDSInfoPanel;
import reg.gui.OutputDSPanel;
import reg.gui.TSPanel;
import reg.gui.TreePanel;
import reg.tree.DSTreeNode;
import reg.tree.FileObject;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class DisplayManager implements Observer {

    private InputDSInfoPanel inputDSInfoPanel;

    private TSPanel tsPanel;

    private TreePanel treePanel;

    private HashMap<String, JPanel> dataPanels = new HashMap<String, JPanel>();

    private JAMSExplorer explorer;

    public DisplayManager(JAMSExplorer regionalizer) {
        this.explorer = regionalizer;
        treePanel = new TreePanel(regionalizer);
        inputDSInfoPanel = new InputDSInfoPanel();
        treePanel.getTree().addObserver(this);
    }

    // handle selection of tree nodes and show metadata
    public void update(Observable o, Object arg) {
        if (arg == null) {
            inputDSInfoPanel.updateDS(null);
            return;
        }
        DSTreeNode node = (DSTreeNode) arg;
        if (node.getType() == DSTreeNode.INPUT_DS) {
            try {
                StandardInputDataStore store = (StandardInputDataStore) explorer.getWorkspace().getInputDataStore(node.toString());
                inputDSInfoPanel.updateDS(store);
            } catch (Exception e) {
                explorer.getRuntime().sendErrorMsg(e.toString());
                e.printStackTrace();
            }
        } else if (node.getType() == DSTreeNode.OUTPUT_DS) {
            //display info dlg
        }
    }

    public HashMap<String, JPanel> getDataPanels() {
        return dataPanels;
    }

    public void removeDisplay(String name) {
        JPanel panel = dataPanels.get(name);
        explorer.getExplorerFrame().getTPane().remove(panel);
        dataPanels.remove(name);
    }

    public void displayDS(DSTreeNode node) {
        if (node == null) {
            return;
        }
        switch (node.getType()) {
            case DSTreeNode.INPUT_DS:

                String dsID = node.toString();
                InputDataStore store = explorer.getWorkspace().getInputDataStore(dsID);

                if (store instanceof TSDataStore) {

                    JAMSSpreadSheet spreadSheet;

                    if (!dataPanels.containsKey(dsID)) {

                        spreadSheet = new JAMSSpreadSheet(explorer);
                        spreadSheet.init();
                        spreadSheet.setID(dsID);
                        dataPanels.put(dsID, spreadSheet);
                        explorer.getExplorerFrame().getTPane().addTab(dsID, spreadSheet);
                        explorer.getExplorerFrame().getTPane().setSelectedComponent(spreadSheet);
                        try {
                            spreadSheet.loadTSDS((TSDataStore) store, explorer.getWorkspace().getInputDirectory());
                        } catch (Exception e) {
                            GUIHelper.showErrorDlg(explorer.getExplorerFrame(), "An error occured while trying to read from datastore \"" + store.getID() + "\"", "Error");
                            e.printStackTrace();
                        }

                    } else {

                        JPanel panel = dataPanels.get(dsID);

                        if (panel instanceof JAMSSpreadSheet) {
                            spreadSheet = (JAMSSpreadSheet) panel;
                            explorer.getExplorerFrame().getTPane().setSelectedComponent(panel);
                        }
                    }
                }
                break;
            case DSTreeNode.OUTPUT_DS:
                FileObject fo = (FileObject) node.getUserObject();
//                OutputDSPanel odsPanel = OutputDSPanel.createPanel(fo.getFile());
//                JAMSExplorer.getExplorerFrame().updateMainPanel(odsPanel);
                try {
                    JPanel outputPanel = OutputPanelFactory.getOutputDSPanel(explorer, fo.getFile());
                    if (outputPanel instanceof OutputDSPanel) {
                        OutputDSPanel odspanel = (OutputDSPanel) outputPanel;
                        //JAMSSpreadSheet spreadsheet = odspanel.getSpreadsheet();

//                    regionalizer.getExplorerFrame().addToTabbedPane(node.toString(),outputPanel);

                        if (!dataPanels.containsKey(fo.getFile().getName())) {

                            dataPanels.put(fo.getFile().getName(), odspanel);
                            //                        regionalizer.getExplorerFrame().addToTabbedPane(node.toString(), outputPanel);
                            explorer.getExplorerFrame().addToTabbedPane(fo.getFile().getName(), odspanel);
                        } else {
                            outputPanel = dataPanels.get(fo.getFile().getName());
                            explorer.getExplorerFrame().showTab(outputPanel);
                        }

                    } else {
                        if (!dataPanels.containsKey(fo.getFile().getName())) {

                            dataPanels.put(fo.getFile().getName(), outputPanel);
                            //                        regionalizer.getExplorerFrame().addToTabbedPane(node.toString(), outputPanel);
                            explorer.getExplorerFrame().addToTabbedPane(fo.getFile().getName(), outputPanel);
                        } else {
                            explorer.getExplorerFrame().showTab(dataPanels.get(fo.getFile().getName()));
                        }
                    }

                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
        }
    }

    /**
     * @return the infoPanel
     */
    public InputDSInfoPanel getInputDSInfoPanel() {
        return inputDSInfoPanel;
    }

    /**
     * @return the treePanel
     */
    public TreePanel getTreePanel() {
        return treePanel;
    }

    /**
     * @return the tSPanel
     */
    public TSPanel getTSPanel() {
        if (tsPanel == null) {
            tsPanel = new TSPanel(explorer);
        }
        return tsPanel;
    }
//    /**
//     * @return the spreadSheets
//     */
//    public HashMap<String, JAMSSpreadSheet> getDataPanels() {
//        return spreadSheets;
//    }
}
