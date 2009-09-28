/*
 * ModelEditPanel.java
 * Created on 12. Dezember 2006, 22:43
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
package jams.juice.gui;

import jamsui.juice.gui.ModelView;
import jams.data.JAMSCalendar;
import jams.data.JAMSDirName;
import jams.data.JAMSString;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jams.gui.tools.GUIHelper;
import jams.gui.input.InputComponent;
import jams.gui.input.ValueChangeListener;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import jamsui.juice.JUICE;
import jams.gui.input.InputComponentFactory;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import reg.JAMSExplorer;

/**
 *
 * @author S. Kralisch
 *
 * Panel that provides swing components for defining model author,
 * date, description and help-baseURL
 *
 */
public class ModelEditPanel extends JPanel {

    private static final int TEXTAREA_WIDTH = 450, TEXTAREA_HEIGHT = 100, TEXTFIELD_WIDTH = 35;

    private JPanel componentPanel;

    private GridBagLayout mainLayout;

    private ModelView view;

    private InputComponent workspace, author, date, helpBaseURL;

    private JTextPane description;

    private JButton explorerButton;

    public ModelEditPanel(ModelView view) {
        super();
        this.view = view;
        init();
    }

    private void init() {

        componentPanel = new JPanel();
        //setBorder(BorderFactory.createTitledBorder("Model Properties"));

        mainLayout = new GridBagLayout();
        componentPanel.setLayout(mainLayout);

        GUIHelper.addGBComponent(componentPanel, mainLayout, new JLabel(JUICE.resources.getString("Workspace:")), 1, 0, 1, 1, 0, 0);
        GUIHelper.addGBComponent(componentPanel, mainLayout, new JLabel(JUICE.resources.getString("Author:")), 1, 1, 1, 1, 0, 0);
        GUIHelper.addGBComponent(componentPanel, mainLayout, new JLabel(JUICE.resources.getString("Help_Base_URL:")), 1, 5, 1, 1, 0, 0);
        GUIHelper.addGBComponent(componentPanel, mainLayout, new JLabel(JUICE.resources.getString("Date:")), 1, 3, 1, 1, 0, 0);
        GUIHelper.addGBComponent(componentPanel, mainLayout, new JLabel(JUICE.resources.getString("Description:")), 1, 4, 1, 1, 0, 0);

        workspace = InputComponentFactory.createInputComponent(JAMSDirName.class);
        workspace.setLength(TEXTFIELD_WIDTH);

        author = InputComponentFactory.createInputComponent(JAMSString.class);
        author.setLength(TEXTFIELD_WIDTH);

        date = InputComponentFactory.createInputComponent(JAMSCalendar.class);
        date.setLength(TEXTFIELD_WIDTH);

        helpBaseURL = InputComponentFactory.createInputComponent(JAMSString.class);
        helpBaseURL.setLength(TEXTFIELD_WIDTH);

        description = new JTextPane();
        description.setContentType("text/plain");
        description.setEditable(true);
        JScrollPane scroll = new JScrollPane(description);
        scroll.setBorder(BorderFactory.createEtchedBorder());
        scroll.setPreferredSize(new Dimension(TEXTAREA_WIDTH, TEXTAREA_HEIGHT));

        Action explorerAction = new AbstractAction(jams.JAMS.resources.getString("JEDI")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                openExplorer();
            }
        };
        explorerButton = new JButton(explorerAction);
        explorerAction.setEnabled(true);

        GUIHelper.addGBComponent(componentPanel, mainLayout, workspace.getComponent(), 2, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NONE, GridBagConstraints.WEST);
        GUIHelper.addGBComponent(componentPanel, mainLayout, explorerButton, 3, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NONE, GridBagConstraints.WEST);
        GUIHelper.addGBComponent(componentPanel, mainLayout, author.getComponent(), 2, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.NONE, GridBagConstraints.WEST);
        GUIHelper.addGBComponent(componentPanel, mainLayout, helpBaseURL.getComponent(), 2, 5, 1, 1, 1.0, 1.0,
                GridBagConstraints.NONE, GridBagConstraints.WEST);
        GUIHelper.addGBComponent(componentPanel, mainLayout, date.getComponent(), 2, 3, 1, 1, 1.0, 1.0,
                GridBagConstraints.NONE, GridBagConstraints.WEST);
        GUIHelper.addGBComponent(componentPanel, mainLayout, scroll, 2, 4, 2, 1, 1.0, 1.0);

        workspace.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChanged() {
                view.setWorkspace(workspace.getValue());
            }
        });

        author.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChanged() {
                updateAuthor();
            }
        });

        date.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChanged() {
                updateDate();
            }
        });

        helpBaseURL.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChanged() {
                updateHelpBaseUrl();
            }
        });

        description.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                updateDescription();
            }

            public void insertUpdate(DocumentEvent e) {
                updateDescription();
            }

            public void removeUpdate(DocumentEvent e) {
                updateDescription();
            }
        });

        /*
        textFields.get("author").addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent evt) {
        updateAuthor();
        }
        });
        textFields.get("date").addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent evt) {
        updateDate();
        }
        });
        textAreas.get("description").addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(java.awt.event.FocusEvent evt) {
        updateDescription();
        }
        });
         */
        add(componentPanel);
    }

    public void updatePanel() {
        author.setValue(view.getAuthor());
        date.setValue(view.getDate());
        helpBaseURL.setValue(view.getHelpBaseUrl());
        description.setText(view.getDescription());
        workspace.setValue(view.getWorkspace());
    }

    private void openExplorer() {
        JAMSExplorer explorer = new JAMSExplorer(null, false, false);
        explorer.getExplorerFrame().setVisible(true);
        explorer.getExplorerFrame().open(new File(view.getWorkspace()));
    }

    private void updateAuthor() {
        view.setAuthor(author.getValue());
    }

    private void updateDate() {
        view.setDate(date.getValue());
    }

    private void updateDescription() {
        view.setDescription(description.getText());
    }

    private void updateHelpBaseUrl() {
        view.setHelpBaseUrl(helpBaseURL.getValue());
    }
}
