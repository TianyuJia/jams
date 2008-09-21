/*
 * VirtualWorkspace.java
 * Created on 23. Januar 2008, 15:42
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
package jams.virtualws;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import jams.virtualws.stores.GeoDataStore;
import jams.virtualws.stores.TableDataStore;
import jams.virtualws.stores.TSDataStore;
import jams.virtualws.stores.InputDataStore;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.StringTokenizer;
import org.unijena.jams.JAMS;
import org.unijena.jams.io.XMLIO;
import org.unijena.jams.runtime.JAMSClassLoader;
import org.unijena.jams.runtime.JAMSRuntime;
import org.unijena.jams.runtime.StandardRuntime;
import org.w3c.dom.Document;
import jams.virtualws.stores.ASCIIConverter;
import jams.virtualws.stores.DataStore;
import jams.virtualws.stores.OutputDataStore;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

public class VirtualWorkspace {

    private HashMap<String, Document> inputDataStores = new HashMap<String, Document>();
    private HashMap<String, Document> outputDataStores = new HashMap<String, Document>();
    private JAMSRuntime runtime = new StandardRuntime();
    private ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private File directory,  inputDirectory,  outputDirectory = null,  outputDataDirectory;
    private Properties properties = new Properties();
    private ArrayList<DataStore> currentStores = new ArrayList<DataStore>();

    public VirtualWorkspace(File directory, JAMSRuntime runtime) {
        this.runtime = runtime;
        this.directory = directory;
        loadConfig();

        if (!isValid(directory)) {
            this.getRuntime().sendErrorMsg("Error adding datastores: \"" +
                    directory.getAbsolutePath() + "\" not a valid datastore or wrong permissions!");
        } else {
            this.createDataStores();
        }
    }

    public void loadConfig() {
        try {
            File file = new File(directory.getPath() + File.separator + "config.txt");
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
            properties.load(is);
        } catch (IOException ioe) {
            runtime.handle(ioe);
        }
    }

    public void saveConfig() {
        try {
            File file = new File(directory.getPath() + File.separator + "config.txt");
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            properties.store(os, "JAMS workspace config");
        } catch (IOException ioe) {
            runtime.handle(ioe);
        }
    }

    private boolean isValid(File directory) {
        if (!directory.isDirectory()) {
            return false;
        }

        try {
            File inDir = new File(directory, "input");
            File outDir = new File(directory, "output");

            inDir.mkdirs();
            outDir.mkdirs();

            this.inputDirectory = inDir;
            this.outputDirectory = outDir;

            if (this.isIncremental()) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");
                this.outputDataDirectory = new File(this.outputDirectory.getPath() + File.separator + sdf.format(cal.getTime()));
            } else {
                this.outputDataDirectory = new File(this.outputDirectory.getPath() + File.separator + "current");
            }

            return true;
        } catch (SecurityException se) {
            return false;
        }
    }

    private String getStoreID(File file) {

        String id = file.getName();
        StringTokenizer tok = new StringTokenizer(id, ".");
        if (tok.countTokens() > 1) {
            id = tok.nextToken();
        }

        return id;
    }

    public void reload() {
    }

    public void setLibs(String[] libs) {
        classLoader = JAMSClassLoader.createClassLoader(libs, runtime);
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public JAMSRuntime getRuntime() {
        return runtime;
    }

    public Set<String> getInputDataStoreIDs() {
        return this.inputDataStores.keySet();
    }

    public Set<String> getOutputDataStoreIDs() {
        return this.outputDataStores.keySet();
    }

    public void removeDataStore(InputDataStore store) {
        inputDataStores.remove(store);
    }

    public InputDataStore getInputDataStore(String dsTitle) {

        Document doc = inputDataStores.get(dsTitle);
        if (doc == null) {
            return null;
        }

        InputDataStore store = null;
        String type = doc.getDocumentElement().getTagName();

        if (type.equals("tabledatastore")) {
            store = new TableDataStore(this, doc);
        } else if (type.equals("tsdatastore")) {
            store = new TSDataStore(this, doc);
        } else if (type.equals("geodatastore")) {
            store = new GeoDataStore(this, doc);
        }

        return store;
    }

    public OutputDataStore getOutputDataStore(String dsTitle) {

        Document doc = outputDataStores.get(dsTitle);
        if (doc == null) {
            return null;
        }

        OutputDataStore store = new OutputDataStore(this, doc, dsTitle);
        currentStores.add(store);
        return store;
    }

    public void close() {
        for (DataStore store : currentStores) {
            try {
                store.close();
            } catch (IOException ioe) {
                runtime.handle(ioe);
            }
        }
    }

    public String getTitle() {
        return properties.getProperty("title");
    }

    public void setTitle(String title) {
        properties.setProperty("title", title);
    }

    public boolean isIncremental() {
        return Boolean.parseBoolean(properties.getProperty("incremental"));
    }

    public void setIncremental(boolean inc) {
        properties.setProperty("incremental", Boolean.toString(inc));
    }

    private void createDataStores() {

        FileFilter filter = new FileFilter() {

            public boolean accept(File pathname) {
                if (pathname.getPath().endsWith(".xml")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        File[] inChildren = inputDirectory.listFiles(filter);
        for (File child : inChildren) {
            try {

                String storeID = getStoreID(child);
                Document doc = XMLIO.getDocument(child.getAbsolutePath());
                inputDataStores.put(storeID, doc);
                this.getRuntime().println("Added input store \"" + storeID + "\" from \"" + child.getAbsolutePath() + "\"", JAMS.VERBOSE);

            } catch (FileNotFoundException fnfe) {
                this.getRuntime().sendErrorMsg("Error reading datastore \"" + child.getAbsolutePath() + "\"!");
            }
        }

        File[] outChildren = outputDirectory.listFiles(filter);
        for (File child : outChildren) {
            try {

                String storeID = getStoreID(child);
                Document doc = XMLIO.getDocument(child.getAbsolutePath());
                outputDataStores.put(storeID, doc);
                this.getRuntime().println("Added output store \"" + storeID + "\" from \"" + child.getAbsolutePath() + "\"", JAMS.VERBOSE);

            } catch (FileNotFoundException fnfe) {
                this.getRuntime().sendErrorMsg("Error reading datastore \"" + child.getAbsolutePath() + "\"!");
            }
        }
    }

    public String dataStoreToString(String dsTitle) {
        InputDataStore store = this.getInputDataStore(dsTitle);
        return dataStoreToString(store);
    }

    public String dataStoreToString(InputDataStore store) {
        if (store == null) {
            return null;
        }
        ASCIIConverter asciiConverter = new ASCIIConverter(store);
        String result = asciiConverter.toASCIIString();
        return result;
    }

    public void inputDataStoreToFile(String dsTitle, File file) throws IOException {
        InputDataStore store = this.getInputDataStore(dsTitle);
        ASCIIConverter asciiConverter = new ASCIIConverter(store);
        asciiConverter.toASCIIFile(file);
        store.close();
    }

    public void inputDataStoreToFile() throws IOException {
        for (String dsTitle : this.getInputDataStoreIDs()) {
            File file = new File(this.directory.getAbsolutePath() + File.separator + "_" + dsTitle + ".txt");
            inputDataStoreToFile(dsTitle, file);
        }
    }

    public static void main(String[] args) throws IOException {

        JAMSRuntime runtime = new StandardRuntime();
        runtime.setDebugLevel(JAMS.VERBOSE);
        runtime.addErrorLogObserver(new Observer() {

            public void update(Observable o, Object arg) {
                System.out.print(arg);
            }
        });
        runtime.addInfoLogObserver(new Observer() {

            public void update(Observable o, Object arg) {
                System.out.print(arg);
            }
        });

        VirtualWorkspace ws = new VirtualWorkspace(new File("D:/jamsapplication/vworkspace"), runtime);

//        System.out.println(ws.dataStoreToString("tmin_local"));
//        ws.inputDataStoreToFile("tmin_local", new File("D:/jamsapplication/vworkspace/_tmin_dump.txt"));

        OutputDataStore store = ws.getOutputDataStore("TimeLoop");
        System.out.println(store.getID());
        for (String attribute : store.getAttributes()) {
            System.out.println(attribute);
        }

//        System.out.println(ws.dataStoreToString("tmean_timeseries"));
//        ws.inputDataStoreToFile("tmean_timeseries", new File("D:/jamsapplication/vworkspace/_tmean_dump.txt"));
//        ws.wsToFile();
    }

    public File getDirectory() {
        return directory;
    }

    public File getInputDirectory() {
        return inputDirectory;
    }

    public File getOutputDirectory(boolean increment) {
        return outputDirectory;
    }

    public File getOutputDataDirectory() {
        return outputDataDirectory;
    }
}

