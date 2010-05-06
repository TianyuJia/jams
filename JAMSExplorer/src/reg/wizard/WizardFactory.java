/*
 * WizardFactory.java
 * Created on 18. January 2010, 11:40
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
package reg.wizard;

import jams.tools.FileTools;
import jams.workspace.JAMSWorkspace;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import reg.wizard.tlug.panels.BaseDataPanel;
import reg.wizard.tlug.panels.RegMethodPanel;
import reg.wizard.tlug.panels.StationParamsPanel;

/**
 *
 * @author hbusch
 */
public class WizardFactory {

    /**
     * mapping of  wizardKey -> key(s) used in model
     **/
    private static final Map<String, String[]> KEY_MODEL_MAPPING = createMapping();

    private static Map<String, String[]> createMapping() {
        Map<String, String[]> resultMap = new HashMap<String, String[]>();

        resultMap.put(BaseDataPanel.KEY_INTERVAL, new String[]{"Interpolation.timeInterval"});
        resultMap.put(StationParamsPanel.KEY_INTERVAL, new String[]{"J2K.timeInterval"});
        resultMap.put(BaseDataPanel.KEY_REGIONALIZATION, new String[]{"Interpolation.inputDataStore"});
        resultMap.put(RegMethodPanel.KEY_SCHWELLENWERT,
                new String[]{"Regionaliser.rsqThreshold"
                });
        resultMap.put(RegMethodPanel.KEY_ELEVATION_CORRECTION,
                new String[]{"Regionaliser.elevationCorrection"
                });
        resultMap.put(RegMethodPanel.KEY_GEWICHTUNG,
                new String[]{"Weights.pidw"
                });
        resultMap.put(RegMethodPanel.KEY_STATION,
                new String[]{"Regionaliser.nidw"
                });

        return Collections.unmodifiableMap(resultMap);
    }

    /**
     * copy model and related files to workspace
     * @param sourceDir
     * @return name of model file or null
     * @throws IOException
     */
    public static String copyModelFiles(String sourceDir, String targetDir)
            throws IOException {

        String outputTargetDir = targetDir + File.separator + "output";
        deleteModelFiles(targetDir);
        deleteModelFiles(outputTargetDir);

        String modelSourceDir = sourceDir + File.separator + "workspace";
        File[] modelFiles = FileTools.getFiles(modelSourceDir, null);
        if (modelFiles == null || modelFiles.length == 0) {
            System.out.println("no model files found ind " + modelSourceDir);
        } else {
            File modelFile = modelFiles[0];


            String completeModelFileName = modelFile.getAbsolutePath();
            String modelFileName = modelFile.getName();
            String targetFileName = targetDir + File.separator + modelFileName;
            FileTools.copyFile(completeModelFileName, targetFileName);


            // copy output files
            String outputSourceDir = sourceDir + File.separator + "output";
            File[] outputFiles = FileTools.getFiles(outputSourceDir, "xml");
            if (outputFiles == null || outputFiles.length == 0) {
                System.out.println("no output files found in " + outputSourceDir);
            } else {
                for (File outputFile : outputFiles) {
                    String outputFileName = outputFile.getAbsolutePath();
                    targetFileName = outputTargetDir + File.separator + outputFile.getName();
                    System.out.println("outputFile file found: " + outputFileName);
                    FileTools.copyFile(outputFileName, targetFileName);
                }
            }
            return modelFileName;
        }
        return null;
    }

    public static String copyInputFile(String sourceDir, String targetDir)
            throws IOException {

        String inputTargetDirName = targetDir + File.separator + JAMSWorkspace.INPUT_DIR_NAME;
        String inputSourceDirName = sourceDir + File.separator + JAMSWorkspace.INPUT_DIR_NAME;
        File inputSourceDir = new File(inputSourceDirName);
        if (inputSourceDir != null && inputSourceDir.exists() && inputSourceDir.isDirectory()) {
            File[] inputFiles = FileTools.getFiles(inputSourceDirName, JAMSWorkspace.SUFFIX_XML);
            if (inputFiles == null || inputFiles.length == 0) {
                System.out.println("no inputFile found in " + inputSourceDirName);
            } else {
                File inputFile = inputFiles[0];

                String completeInputFileName = inputFile.getAbsolutePath();
                String inputFileName = inputFile.getName();
                String targetFileName = inputTargetDirName + File.separator + inputFileName;
                FileTools.copyFile(completeInputFileName, targetFileName);
                return inputFileName;
            }
        }
        return null;
    }

    /**
     * replace a certain string in all files in targetDir, finding in sourceDir
     * @param sourceDir
     * @param targetDir
     * @param findString
     * @param replaceString
     * @return
     */
    public static void replaceInOutputFiles(String sourceDir, String targetDir, String findString, String replaceString)
            throws IOException {
        String outputSourceDir = sourceDir + File.separator + "output";
        String outputTargetDir = targetDir + File.separator + "output";
        File[] outputFiles = FileTools.getFiles(outputSourceDir, "xml");
        if (outputFiles == null || outputFiles.length == 0) {
            System.out.println("replaceInOutputFiles. no output files found in " + outputSourceDir);
        } else {
            for (File outputFile : outputFiles) {
                String targetFileName = outputTargetDir + File.separator + outputFile.getName();
                if (FileTools.replaceWithinFile(targetFileName, findString, replaceString)) {
                    System.out.println(targetFileName + ": " + findString + " replaced by " + replaceString);
                } else {
                    System.out.println(targetFileName + ": " + findString + " not found. no replacement necessary.");
                }
            }
        }
    }

    /**
     * delete all xml files of directory
     * @param theDirectoryName
     * @throws IOException
     */
    public static void deleteModelFiles(String theDirectoryName)
            throws IOException {
        File[] modelFiles = FileTools.getFiles(theDirectoryName, "xml");
        FileTools.deleteFiles(modelFiles);
    }

    /**
     * convert wizardSettings into properties usable for models
     * @param wizardSettings
     * @return properties
     */
    public static Properties getModelPropertiesFromWizardResult(Map wizardSettings) {
        // put parameter to model
        System.out.println("getting modelProperties from WizardResult.");
        Properties properties = new Properties();
        Set<String> wizardKeys = KEY_MODEL_MAPPING.keySet();
        for (String wizardKey : wizardKeys) {
            String value = (String) wizardSettings.get(wizardKey);
            if (value != null) {
                String[] modelKeys = KEY_MODEL_MAPPING.get(wizardKey);
                for (String modelKey : modelKeys) {
                    properties.put(modelKey, value);
                    System.out.println("  " + modelKey + " -> " + value);
                }
            }
        }
        return properties;
    }
}
