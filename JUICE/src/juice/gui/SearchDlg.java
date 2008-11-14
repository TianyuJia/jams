/*
 * SearchDlg.java
 * Created on 10. November 2008, 16:32
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
package juice.gui;

import jams.data.JAMSString;
import jams.gui.LHelper;
import jams.gui.input.InputComponent;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.tree.TreePath;
import juice.ComponentDescriptor;
import juice.gui.tree.JAMSNode;
import juice.gui.tree.JAMSTree;
import juice.gui.tree.LibTree;
import juice.gui.tree.ModelTree;
import juice.JUICE;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class SearchDlg extends JDialog {

    private static final int TEXTFIELD_WIDTH = 35;

    private static final int FOUND_IN_CLASS = 1,  FOUND_IN_INSTANCE = 2,  FOUND_IN_CONTEXT_ATTRIBS = 3,  FOUND_IN_COMPONENT_ATTRIBS = 4,  FOUND_IN_COMPONENT_VALUES = 5;

    private JAMSTree tree;

    private Enumeration treeEnum;

    private JCheckBox inClassName,  inInstanceName,  inContextAttribs,  inComponentAttribs,  caseSensitive,  inComponentValues,  wholeString;

    private InputComponent searchText;

    private JRadioButton repo,  model;

    private boolean modelSelect = true,  foundResult = false;

    public SearchDlg(Frame owner) {
        super(owner);
        setLocationRelativeTo(owner);
        setModal(false);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        GridBagLayout mainLayout = new GridBagLayout();
        contentPanel.setLayout(mainLayout);

        inClassName = new JCheckBox(JUICE.resources.getString("inClassName"), true);
        inInstanceName = new JCheckBox(JUICE.resources.getString("inInstanceName"), true);
        inContextAttribs = new JCheckBox(JUICE.resources.getString("inContextAttribs"), true);
        inComponentAttribs = new JCheckBox(JUICE.resources.getString("inComponentAttribs"), true);
        inComponentValues = new JCheckBox(JUICE.resources.getString("inComponentValues"), true);

        caseSensitive = new JCheckBox(JUICE.resources.getString("caseSensitiveSearch"), false);
        wholeString = new JCheckBox(JUICE.resources.getString("wholeStringSearch"), false);

        searchText = LHelper.createInputComponent(JAMSString.class.getSimpleName());
        searchText.setLength(TEXTFIELD_WIDTH);

        repo = new JRadioButton(JUICE.resources.getString("Search_in_Repo"));
        model = new JRadioButton(JUICE.resources.getString("Search_in_Model"));
        model.setSelected(true);

        repo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (repo.isSelected() && modelSelect) {
                    modelSelect = false;
                    setTree(JUICE.getLibTree());
                }
            }
        });
        model.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (model.isSelected() && !modelSelect) {
                    modelSelect = true;
                    setTree(JUICE.getJuiceFrame().getCurrentView().getTree());
                }
            }
        });


        ButtonGroup group = new ButtonGroup();
        group.add(repo);
        group.add(model);

        LHelper.addGBComponent(contentPanel, mainLayout, new JLabel(JUICE.resources.getString("Search_text")), 1, 0, 2, 1, 0, 0);
        LHelper.addGBComponent(contentPanel, mainLayout, searchText.getComponent(), 1, 1, 2, 1, 0, 0);

        LHelper.addGBComponent(contentPanel, mainLayout, model, 1, 2, 1, 1, 0, 0);
        LHelper.addGBComponent(contentPanel, mainLayout, repo, 2, 2, 1, 1, 0, 0);

        LHelper.addGBComponent(contentPanel, mainLayout, caseSensitive, 1, 5, 1, 1, 0, 0);
        LHelper.addGBComponent(contentPanel, mainLayout, wholeString, 2, 5, 1, 1, 0, 0);

        LHelper.addGBComponent(contentPanel, mainLayout, new JLabel(JUICE.resources.getString("Where_to_search")), 1, 10, 2, 1, 0, 0);
        LHelper.addGBComponent(contentPanel, mainLayout, inClassName, 1, 11, 2, 1, 0, 0);
        LHelper.addGBComponent(contentPanel, mainLayout, inComponentAttribs, 1, 12, 2, 1, 0, 0);
        LHelper.addGBComponent(contentPanel, mainLayout, inInstanceName, 1, 13, 2, 1, 0, 0);
        LHelper.addGBComponent(contentPanel, mainLayout, inComponentValues, 1, 14, 2, 1, 0, 0);
        LHelper.addGBComponent(contentPanel, mainLayout, inContextAttribs, 1, 18, 2, 1, 0, 0);

        JButton findButton = new JButton(JUICE.resources.getString("Find"));
        findButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                processFind();
            }
        });

        JButton resetButton = new JButton(JUICE.resources.getString("ResetSearch"));
        resetButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
                processFind();
            }
        });

        JButton closeButton = new JButton(JUICE.resources.getString("Close"));
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                processClose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);
        buttonPanel.add(findButton);
        buttonPanel.add(closeButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        this.pack();
    }

    private void reset() {
        JAMSNode rootNode = (JAMSNode) tree.getModel().getRoot();
        treeEnum = rootNode.breadthFirstEnumeration();
        foundResult = false;
    }

    public void setTree(JAMSTree tree) {
        this.tree = tree;

        if (tree instanceof LibTree) {
            this.setTitle(JUICE.resources.getString("Search_in_Repo"));
            inInstanceName.setEnabled(false);
            inComponentValues.setEnabled(false);
            inContextAttribs.setEnabled(false);
        } else if (tree instanceof ModelTree) {
            this.setTitle(JUICE.resources.getString("Search_in_Model"));
            inInstanceName.setEnabled(true);
            inComponentValues.setEnabled(true);
            inContextAttribs.setEnabled(true);
        }

        reset();
    }

    private void processFind() {

        while (treeEnum.hasMoreElements()) {

            JAMSNode node = (JAMSNode) treeEnum.nextElement();
            if ((node.getType() == JAMSNode.COMPONENT_NODE) || (node.getType() == JAMSNode.CONTEXT_NODE) || (node.getType() == JAMSNode.MODEL_ROOT)) {

                ComponentDescriptor cd = (ComponentDescriptor) node.getUserObject();

                if (find(cd, searchText.getValue(), caseSensitive.isSelected(), wholeString.isSelected()) != -1) {
                    TreePath resultPath = new TreePath(node.getPath());
                    tree.scrollPathToVisible(resultPath);
                    tree.setSelectionPath(resultPath);
                    foundResult = true;
                    return;
                }
            }
        }

        // check if we have found anything
        if (!foundResult) {
            LHelper.showInfoDlg(JUICE.getJuiceFrame(), JUICE.resources.getString("No_searchresults_txt"), JUICE.resources.getString("Search_finished"));
            reset();
            return;
        }

        // we've found all results, ask what to do next
        if (LHelper.showYesNoDlg(JUICE.getJuiceFrame(), JUICE.resources.getString("No_further_searchresults_txt"), JUICE.resources.getString("Search_finished")) == JOptionPane.YES_OPTION) {
            reset();
            processFind();
        }
    }

    private int find(ComponentDescriptor cd, String needle, boolean caseSensitive, boolean wholeString) {

        if (inClassName.isSelected()) {
            if (contains(cd.getClazz().getName(), needle, caseSensitive, wholeString)) {
                return FOUND_IN_CLASS;
            }
        }
        if (inInstanceName.isSelected()) {
            if (contains(cd.getName(), needle, caseSensitive, wholeString)) {
                return FOUND_IN_INSTANCE;
            }
        }
        if (inComponentAttribs.isSelected()) {
            for (ComponentDescriptor.ComponentAttribute ca : cd.getComponentAttributes().values()) {

                // check for component attribute name
                if (contains(ca.name, needle, caseSensitive, wholeString)) {
                    return FOUND_IN_COMPONENT_ATTRIBS;
                }
            }
        }
        if (inComponentValues.isSelected()) {
            for (ComponentDescriptor.ComponentAttribute ca : cd.getComponentAttributes().values()) {

                // check for component attribute values
                if (ca.getValue() != null) {
                    if (contains(ca.getValue().toString(), needle, caseSensitive, wholeString)) {
                        return FOUND_IN_COMPONENT_VALUES;
                    }
                }

                // check for context attribute name
                if (ca.getContextAttribute() != null) {
                    if (contains(ca.getContextAttribute().getName(), needle, caseSensitive, wholeString)) {
                        return FOUND_IN_COMPONENT_VALUES;
                    }
                }
            }
        }
        if (inContextAttribs.isSelected()) {
            for (String s : cd.getContextAttributes().keySet()) {
                if (contains(s, needle, caseSensitive, wholeString)) {
                    return FOUND_IN_CONTEXT_ATTRIBS;
                }
            }
        }
        return -1;
    }

    private boolean contains(String hay, String needle, boolean caseSensitive, boolean wholeString) {
        if (!caseSensitive) {
            hay = hay.toLowerCase();
            needle = needle.toLowerCase();
        }
        if (wholeString) {
            return hay.equals(needle);
        }
        if (hay.indexOf(needle) != -1) {
            return true;
        } else {
            return false;
        }
    }

    private void processClose() {
        setVisible(false);
    }

    @Override
    public void setVisible(boolean b) {

        // check if we have a model opened
        if (JUICE.getJuiceFrame().getCurrentView() == null) {
            repo.setSelected(true);
            model.setEnabled(false);
            modelSelect = false;
        } else {
            model.setEnabled(true);
        }

        // check if we already have a tree
        if (tree == null) {
            if (modelSelect) {
                setTree(JUICE.getJuiceFrame().getCurrentView().getTree());
            } else {
                setTree(JUICE.getLibTree());
            }
        }

        super.setVisible(b);
    }
}
