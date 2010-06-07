/*
 * JAMSui.java
 * Created on 2. Oktober 2005, 16:05
 *
 * This file is part of JAMSui
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
 * GNU General Publiccc License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 *
 */
package jamsui.launcher;

import jamsui.cmdline.*;
import jams.tools.XMLTools;
import jams.tools.JAMSTools;
import java.io.*;
import javax.swing.UIManager;
import jams.runtime.*;
import jams.io.*;
import jams.JAMS;
import jams.JAMSProperties;
import jams.SystemProperties;
import jams.model.JAMSFullModelState;
import jams.model.Model;
import jams.tools.FileTools;
import jams.tools.StringTools;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Sven Kralisch
 */
public class JAMSui {

    private static File baseDir = new File(System.getProperty("user.dir"));
    public static final String APP_TITLE = "JAMS";
    protected SystemProperties properties;

    /**
     * JAMSui contructor
     * @param cmdLine A JAMSCmdLine object containing the command line arguments
     */
    public JAMSui(JAMSCmdLine cmdLine) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception lnfe) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                handle(ex);
            }
        }

        //create a JAMSui default set of property values
        properties = JAMSProperties.createProperties();

        //try to load property values from file
        if (cmdLine.getConfigFileName() != null) {
            //check for file provided at command line
            try {
                properties.load(cmdLine.getConfigFileName());
            } catch (IOException ioe) {
                System.out.println(JAMS.resources.getString("Error_while_loading_config_from") + cmdLine.getConfigFileName());
                handle(ioe);
            }
            baseDir = new File(cmdLine.getConfigFileName()).getParentFile();
        } else {
            //check for default file
            String defaultFile = System.getProperty("user.dir") + System.getProperty("file.separator") + JAMS.DEFAULT_PARAMETER_FILENAME;
            File file = new File(defaultFile);
            if (file.exists()) {
                try {
                    properties.load(defaultFile);
                } catch (IOException ioe) {
                    System.out.println(JAMS.resources.getString("Error_while_loading_config_from") + defaultFile);
                    handle(ioe);
                }
            }
        }

        JAMSTools.configureLocaleEncoding(properties);

        if (cmdLine.isNogui()) {
            properties.setProperty(JAMSProperties.GUICONFIG_IDENTIFIER, "0");
            properties.setProperty(JAMSProperties.WINDOWENABLE_IDENTIFIER, "0");
            properties.setProperty(JAMSProperties.VERBOSITY_IDENTIFIER, "1");
            properties.setProperty(JAMSProperties.ERRORDLG_IDENTIFIER, "0");
        }

        int guiConfig = Integer.parseInt(properties.getProperty(SystemProperties.GUICONFIG_IDENTIFIER, "0"));
        String modelFileName = cmdLine.getModelFileName();

        // check if there is a model file provided
        if ((modelFileName == null)) {

            //check if at least GUI is enabled
            if (guiConfig == 1) {
                startGUI();
            } else {
                System.out.println(JAMS.resources.getString("You_must_provide_a_model_file_name_(see_JAMS_--help)_when_disabling_GUI_config!"));
                System.exit(-1);
            }

        } else {

            String cmdLineParameterValues = cmdLine.getParameterValues();

            // if there is a model file, check if the user wants to use GUI
            if (guiConfig == 1) {

                try {
                    startGUI(modelFileName, cmdLineParameterValues);
                } catch (Exception e) {
                    JAMSui.handle(e);
                }

            } else {

                // if GUI is disabled and a model file provided, then run
                // the model directly

                String info = "";

                //check if file exists
                File file = new File(modelFileName);
                if (!file.exists()) {
                    System.out.println(JAMS.resources.getString("Model_file_") + modelFileName + JAMS.resources.getString("_could_not_be_found_-_exiting!"));
                    return;
                }

                // do some search and replace on the input file and create new file if necessary
                String newModelFilename = XMLProcessor.modelDocConverter(modelFileName);
                if (!newModelFilename.equalsIgnoreCase(modelFileName)) {
                    info = JAMS.resources.getString("The_model_definition_in_") + modelFileName + JAMS.resources.getString("_has_been_adapted_in_order_to_meet_changes_in_the_JAMS_model_specification.The_new_definition_has_been_stored_in_") + newModelFilename + JAMS.resources.getString("_while_your_original_file_was_left_untouched.");
                    modelFileName = newModelFilename;
                }

                JAMSRuntime runtime = null;
                try {

                    String xmlString = FileTools.fileToString(modelFileName);
                    String[] args = StringTools.toArray(cmdLineParameterValues, ";");
                    if (args != null) {
                        for (int i = 0; i < args.length; i++) {
                            xmlString = xmlString.replaceAll("%" + i, args[i]);
                        }
                    }

                    Document modelDoc = XMLTools.getDocumentFromString(xmlString);
                    runtime = new StandardRuntime();
                    runtime.loadModel(modelDoc, properties);

                    // if workspace has not been provided, check if the document has been
                    // read from file and try to use parent directory instead
                    if (StringTools.isEmptyString(runtime.getModel().getWorkspacePath())
                            && !StringTools.isEmptyString(modelFileName)) {
                        String dir = new File(modelFileName).getParent();
                        runtime.getModel().setWorkspacePath(dir);
                        runtime.sendInfoMsg(JAMS.resources.getString("no_workspace_defined_use_loadpath") + dir);
                    }

                    if (!info.equals("")) {
                        runtime.println(info);
                    }
                    String snapshotFileName = cmdLine.getSnapshotFileName();
                    if (snapshotFileName != null) {
                        File snapshotFile = new File(snapshotFileName);
                        if (!snapshotFile.exists()) {
                            final JAMSFullModelState state = new JAMSFullModelState(snapshotFile);

                            Model model = state.getModel();
                            try {
                                model.getRuntime().resume(state.getSmallModelState());
                            } catch (Exception e) {
                                JAMSTools.handle(e);
                            }
                            // collect some garbage ;)
                            Runtime.getRuntime().gc();
                        }
                    } else {
                        runtime.runModel();
                    }

                } catch (IOException ioe) {
                    System.out.println(JAMS.resources.getString("The_model_definition_file_") + modelFileName + JAMS.resources.getString("_could_not_be_loaded,_because:_") + ioe.toString());
                } catch (SAXException se) {
                    System.out.println(JAMS.resources.getString("The_model_definition_file_") + modelFileName + JAMS.resources.getString("_contained_errors!"));
                } catch (Exception ex) {
                    if (runtime != null) {
                        runtime.handle(ex);
                    } else {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    protected void startGUI() {
        new JAMSFrame(null, properties).setVisible(true);
    }

    protected void startGUI(String modelFileName, String cmdLineParameterValues) {
        new JAMSFrame(null, properties, modelFileName, cmdLineParameterValues).setVisible(true);
    }

    /**
     * JAMSui main method
     * @param args The command line arguments
     */
    public static void main(String[] args) {

        new JAMSui(new JAMSCmdLine(args, APP_TITLE));

    }

    /**
     * Exception handling method
     * @param ex Exception to be handled
     */
    public static void handle(Throwable t) {
        handle(t, true);
    }

    /**
     * Exception handling method
     * @param ex Exception to be handled
     * @param proceed Proceed or not?
     */
    public static void handle(Throwable t, boolean proceed) {
        t.printStackTrace();
        if (!proceed) {
            System.exit(-1);
        }
    }

    /**
     * Get the JAMSui base directory
     * @return The JAMSui base directory
     */
    public static File getBaseDir() {
        return baseDir;
    }
}
