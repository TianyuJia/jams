/*
 * StandardRuntime.java
 * Created on 2. Juni 2006, 13:24
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
package jams.runtime;

import jams.ExceptionHandler;
import jams.JAMSException;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import jams.JAMS;
import jams.JAMSProperties;
import jams.JAMSVersion;
import jams.SystemProperties;
import jams.data.JAMSData;
import jams.io.ModelLoader;
import jams.model.SmallModelState;
import jams.io.ParameterProcessor;
import jams.model.FullModelState;
import jams.model.GUIComponent;
import jams.model.JAMSContext;
import jams.model.JAMSFullModelState;
import jams.model.JAMSModel;
import jams.model.JAMSSmallModelState;
import jams.model.Model;
import jams.tools.StringTools;
import jams.workspace.InvalidWorkspaceException;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.w3c.dom.Document;

/**
 *
 * @author S. Kralisch
 */
public class StandardRuntime extends Observable implements JAMSRuntime, Serializable {

    private HashMap<String, JAMSData> dataHandles = new HashMap<String, JAMSData>();
    private JAMSLog errorLog = new JAMSLog();
    private JAMSLog infoLog = new JAMSLog();
    private int debugLevel = JAMS.STANDARD;
    //private RunState runState = new RunState();
    private ArrayList<GUIComponent> guiComponents = new ArrayList<GUIComponent>();
    transient private JButton stopButton, pauseButton, saveButton, closeButton;
    transient private JFrame frame;
    private Model model;
    transient private PrintStream infoStream, errorStream;
    private boolean guiEnabled = false;
    transient private ClassLoader classLoader;
    private Document modelDocument = null;
    private SystemProperties properties = null;
    String[] libs = null;
    private int runState = -1;
    private HashMap<String, Integer> idMap;
    transient private SmallModelState state = new JAMSSmallModelState();

    /**
     * Loads a model from an XML document
     * @param modelDocument the XML document
     * @param properties a set of system properties providing information on
     * libs to be used and other parameter
     */
    @Override
    public void loadModel(Document modelDocument, SystemProperties properties) {

        this.modelDocument = modelDocument;
        this.properties = properties;

        initLogging();

        String user = properties.getProperty(JAMSProperties.USERNAME_IDENTIFIER);
        if (!StringTools.isEmptyString(user)) {
            user = System.getProperty("user.name") + " (" + user + ")";
        } else {
            user = System.getProperty("user.name");
        }

        this.println(JAMS.i18n("JAMS_version") + JAMSVersion.getInstance().getVersionDateString(), JAMS.STANDARD);
        this.println(JAMS.i18n("User_name_:") + user, JAMS.STANDARD);
        this.println(JAMS.i18n("Date_time") + new SimpleDateFormat().format(new Date()), JAMS.STANDARD);
        this.println("", JAMS.STANDARD);


        // start the loading process
        long start = System.currentTimeMillis();

        // get libraries specified in properties
        libs = StringTools.toArray(properties.getProperty("libs", ""), ";");

        // load the libraries and create the class loader

        this.println(JAMS.i18n("Creating_class_loader"), JAMS.STANDARD);
        this.println(JAMS.i18n("*************************************"), JAMS.STANDARD);

        classLoader = JAMSClassLoader.createClassLoader(libs, this);

        // create model config object from config document
        //ModelConfig config = new ModelConfig(modelDocument);

        // create preprocessor
        //ModelPreprocessor preProc = new ModelPreprocessor(modelDocument, config, this);

        // run preprocessor
        //preProc.process();
        ParameterProcessor.preProcess(modelDocument);


        // load the model
        this.println("", JAMS.STANDARD);
        this.println(JAMS.i18n("Loading_Model"), JAMS.STANDARD);

        ModelLoader modelLoader = new ModelLoader(this);

        ExceptionHandler exHandler = new ExceptionHandler() {

            public void handle(JAMSException ex) {
                StandardRuntime.this.handle(ex, true);
            }

            public void handle(ArrayList<JAMSException> exList) {
                
            }
        };

        try {
            this.model = modelLoader.loadModel(modelDocument, exHandler);
        } catch (Exception jex) {
            this.handle(jex, false);
        }

        // define if the model should profile or not
        boolean doProfiling = ("1".equals(properties.getProperty(JAMSProperties.PROFILE_IDENTIFIER, "0")) ? true : false);
        this.model.setProfiling(doProfiling);

        // get the id map which maps class names to id values (used during logging)
        this.idMap = modelLoader.getIdMap();

        //this.idMap.put(JAMSWorkspace.class.getName(), 0);
        //this.idMap.put(JAMSModel.class.getName(), 0);

//        // create IDs for all used components
//        HashSet<Class> componentClassSet = new HashSet<Class>();
//        for (Component component : modelLoader.getComponentRepository().values()) {
//            componentClassSet.add(component.getClass());
//        }
//
//        int i = 1;
//        for (Class c : componentClassSet) {
//            componentMap.put(c, i);
//            idMap.put(i, c);
//            println(String.format("%03d", i) + ": " + c.getName(), JAMS.VERBOSE);
//            i++;
//        }

        // create GUI if needed
        int wEnable = Integer.parseInt(properties.getProperty("windowenable", "1"));
        if (wEnable != 0) {
            int width = Integer.parseInt(properties.getProperty("windowwidth", "600"));
            int height = Integer.parseInt(properties.getProperty("windowheight", "400"));
            int ontop = Integer.parseInt(properties.getProperty("windowontop", "0"));
            this.initGUI(model.getName(), (ontop == 1 ? true : false), width, height);
            this.guiEnabled = true;
        }

        long end = System.currentTimeMillis();
        this.println(JAMS.i18n("*************************************"), JAMS.STANDARD);
        this.println(JAMS.i18n("JAMS_model_setup_time:_") + (end - start) + " ms", JAMS.STANDARD);
        this.println(JAMS.i18n("*************************************"), JAMS.STANDARD);

//        classLoader = null;
//        Runtime.getRuntime().gc();

        runState = JAMSRuntime.STATE_RUN;

    }

    public Document getModelDocument() {
        return this.modelDocument;
    }

    private void initLogging() {
        // set the debug (i.e. output verbosity) level
        this.setDebugLevel(Integer.parseInt(properties.getProperty(SystemProperties.DEBUG_IDENTIFIER, "1")));

        // add log observers for output to system.out if needed
        int verbose = Integer.parseInt(properties.getProperty(SystemProperties.VERBOSITY_IDENTIFIER, "1"));
        if (verbose != 0) {

            // add info and error log output
            this.addInfoLogObserver(new Observer() {

                @Override
                public void update(Observable obs, Object obj) {
                    System.out.print(obj);
                }
            });
            this.addErrorLogObserver(new Observer() {

                @Override
                public void update(Observable obs, Object obj) {
                    System.out.print(obj);
                }
            });
        }

        int errorDlg = Integer.parseInt(properties.getProperty(SystemProperties.ERRORDLG_IDENTIFIER, "0"));
        if (errorDlg != 0) {

            // add error log output via JDialog
            this.addErrorLogObserver(new Observer() {

                @Override
                public void update(Observable obs, Object obj) {

                    Object[] options = {JAMS.i18n("OK"), JAMS.i18n("OK,_skip_other_messages")};
                    int result = JOptionPane.showOptionDialog(frame, obj.toString(), JAMS.i18n("Model_execution_error"),
                            JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);

                    if (result == 1) {
                        StandardRuntime.this.deleteErrorLogObserver(this);
                    }

                }
            });
        }

        try {
            String infoLogFile = properties.getProperty(SystemProperties.INFOLOG_IDENTIFIER);
            if (!StringTools.isEmptyString(infoLogFile)) {
                infoStream = new PrintStream(infoLogFile);
            }
        } catch (FileNotFoundException fnfe) {
            this.handle(fnfe);
        }

        try {
            String errorLogFile = properties.getProperty(SystemProperties.ERRORLOG_IDENTIFIER);
            if (!StringTools.isEmptyString(errorLogFile)) {
                errorStream = new PrintStream(errorLogFile);
            }
        } catch (FileNotFoundException fnfe) {
            this.handle(fnfe);
        }
    }

    public String[] getLibs() {
        return libs;
    }

    public FullModelState getFullModelState() {
        try {
            getModel().getWorkspace().saveState(state);
            return new JAMSFullModelState(this.state, this.getModel());
        } catch (IOException ioe) {
            this.sendErrorMsg(ioe.toString());
            return null;
        }
    }

    private void recoverIteratorStates(JAMSContext context, SmallModelState state) {
        //this is ugly but adds the context as runstate observer to our runtime
        context.setModel(model);
        for (int i = 0; i < context.getComponents().size(); i++) {
            if (context.getComponents().get(i) instanceof JAMSContext) {
                recoverIteratorStates(((JAMSContext) context.getComponents().get(i)), state);
            }
        }
    }

    public void resume(SmallModelState state) throws Exception {
        this.state = state;

        if (guiEnabled && (guiComponents.size() > 0)) {
            frame.setVisible(true);
        }
        //is there a better method without unsafe casting?
        recoverIteratorStates((JAMSModel) model, state);
        model.getWorkspace().restore(state);
        this.setRunState(JAMSRuntime.STATE_RUN);

        // add this runtime to the runtime manager
        RuntimeManager.getInstance().addRuntime(this);
        long start = System.currentTimeMillis();

        model.restore();
        if (this.getState() == JAMSRuntime.STATE_RUN) {
            model.resume();
        }

        if (this.getState() == JAMSRuntime.STATE_RUN) {
            model.cleanup();
        }

        if (this.getState() == JAMSRuntime.STATE_PAUSE) {
            this.model.getWorkspace().saveState(state);
        }
        state.setExecutionTime(state.getExecutionTime() + (System.currentTimeMillis() - start));

        finishModelExecution(state.getExecutionTime());
    }

    @Override
    public void runModel() throws Exception {

        //check if runstate is on "run"
        if (this.getState() != JAMSRuntime.STATE_RUN) {
            return;
        }

        // prepare the model's workspace
        try {
            if (model.getWorkspace()==null){
                this.sendHalt(JAMS.i18n("no_workspace_defined_did_you_save_your_model"));
            }else
                model.getWorkspace().init();
        } catch (InvalidWorkspaceException iwe) {
            this.sendHalt(iwe.getMessage());
            return;
        }

        // add this runtime to the runtime manager
        RuntimeManager.getInstance().addRuntime(this);

        if (guiEnabled && (guiComponents.size() > 0)) {
            frame.setVisible(true);
        }

        long start = System.currentTimeMillis();

        if (this.getState() == JAMSRuntime.STATE_RUN) {
            model.init();
        }

        if (this.getState() == JAMSRuntime.STATE_RUN) {
            model.run();
        }

        if (this.getState() == JAMSRuntime.STATE_RUN) {
            model.cleanup();
        }

        finishModelExecution(System.currentTimeMillis() - start);

        if (this.getState() != JAMSRuntime.STATE_PAUSE) {
            model = null;
            classLoader = null;
            Runtime.getRuntime().gc();
        }
    }

    private void finishModelExecution(long executionTime) {
        if (this.getState() == JAMSRuntime.STATE_PAUSE) {
            this.model.getWorkspace().saveState(state);
        }

        if (model.getWorkspace() != null) {
            model.getWorkspace().close();
        }

        if (infoStream != null) {
            infoStream.print(this.getInfoLog());
            infoStream.flush();
            infoStream.close();
        }

        if (errorStream != null) {
            errorStream.print(this.getErrorLog());
            errorStream.close();
        }

        if (this.getState() != JAMSRuntime.STATE_PAUSE) {
            this.println(JAMS.i18n("*************************************"), JAMS.STANDARD);
            this.println(JAMS.i18n("JAMS_model_execution_time:_") + (executionTime) + " ms", JAMS.STANDARD);
            this.println(JAMS.i18n("*************************************"), JAMS.STANDARD);
            this.sendHalt();
        } else {
            this.println(JAMS.i18n("*************************************"), JAMS.STANDARD);
            this.println(JAMS.i18n("JAMS_model_execution_paused"), JAMS.STANDARD);
            this.println(JAMS.i18n("*************************************"), JAMS.STANDARD);
        }
    }

    @Override
    public void initGUI(String title, boolean ontop, int width, int height) {

        if (guiComponents.isEmpty()) {
            return;
        }

        frame = new JFrame();
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frame.setTitle(title);
        frame.setName(title);
        frame.setAlwaysOnTop(ontop);
        frame.setIconImage(new ImageIcon(ClassLoader.getSystemResource("resources/images/JAMSicon16.png")).getImage());
        frame.setPreferredSize(new java.awt.Dimension(width, height));

        ListIterator<GUIComponent> i = guiComponents.listIterator();
        if (guiComponents.size() > 1) {
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setTabPlacement(JTabbedPane.LEFT);

            while (i.hasNext()) {
                GUIComponent comp = i.next();
                try {
                    tabbedPane.addTab(comp.getInstanceName(), comp.getPanel());
                } catch (Throwable t) {
                    this.sendErrorMsg(JAMS.i18n("Could_not_load_component") + comp.getInstanceName()
                            + JAMS.i18n("Proceed_anyway?"));
                    this.handle(t, true);
                }
            }
            frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        } else {

            while (i.hasNext()) {
                frame.getContentPane().add(i.next().getPanel(), BorderLayout.CENTER);
            }

        }

        JToolBar toolBar = new JToolBar();
//        toolBar.setPreferredSize(new Dimension(0, JAMS.TOOLBAR_HEIGHT));

        stopButton = new JButton();
        stopButton.setToolTipText(JAMS.i18n("Stop_model"));
        stopButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/ModelStop.png")));
        stopButton.setEnabled(true);
        stopButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                StandardRuntime.this.sendHalt();
                stopButton.setEnabled(false);
                saveButton.setEnabled(false);
            }
        });
        toolBar.add(stopButton);

        pauseButton = new JButton();
        pauseButton.setToolTipText(JAMS.i18n("Pause_model"));
        pauseButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/ModelPause.png")));
        pauseButton.setEnabled(true);
        pauseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (StandardRuntime.this.runState == JAMSRuntime.STATE_RUN) {
                    StandardRuntime.this.pause();
                    pauseButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/ModelRun.png")));
                    saveButton.setEnabled(true);
                } else if (StandardRuntime.this.runState == JAMSRuntime.STATE_PAUSE) {
                    Thread resumedModelThread = new Thread(new Runnable() {

                        public void run() {
                            pauseButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/ModelPause.png")));
                            saveButton.setEnabled(false);
                            try {
                                StandardRuntime.this.resume(state);
                            } catch (Exception e) {
                                StandardRuntime.this.sendErrorMsg(e.toString());
                                StandardRuntime.this.handle(e, true);
                            }
                        }
                    });
                    resumedModelThread.start();
                } else {
                    //error
                }
                //pauseButton.setEnabled(false);
            }
        });
        toolBar.add(pauseButton);

        saveButton = new JButton();
        saveButton.setToolTipText(JAMS.i18n("Pause_model"));
        saveButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/ModelSave.png")));
        saveButton.setEnabled(false);
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                if (StandardRuntime.this.runState == JAMSRuntime.STATE_PAUSE) {
                    JFileChooser fc = new JFileChooser();
                    fc.setFileFilter(new FileFilter() {

                        @Override
                        public boolean accept(File f) {
                            return f.isDirectory() || f.getName().toLowerCase().endsWith(".ser");
                        }

                        @Override
                        public String getDescription() {
                            return JAMS.i18n("Serialization_(ser)");
                        }
                    });

                    int returnVal = fc.showSaveDialog(StandardRuntime.this.frame);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try {
                            JAMSFullModelState state = new JAMSFullModelState(StandardRuntime.this.state,
                                    StandardRuntime.this.getModel());
                            state.writeToFile(file);
                        } catch (IOException e) {
                            sendErrorMsg(JAMS.i18n("Unable_to_save_model_state_because,") + e.toString());
                            handle(e, true);
                        }
                    }
                }
            }
        });
        toolBar.add(saveButton);

        closeButton = new JButton();
        closeButton.setToolTipText(JAMS.i18n("Close_window"));
        closeButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/Shutdown.png")));
        closeButton.setEnabled(false);
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                StandardRuntime.this.sendHalt();
                frame.dispose();
                Runtime.getRuntime().gc();
            }
        });
        toolBar.add(closeButton);
        frame.add(toolBar, BorderLayout.NORTH);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                StandardRuntime.this.sendHalt();
                frame.dispose();
                Runtime.getRuntime().gc();
            }
        });

        this.addStateObserver(new Observer() {

            @Override
            public void update(Observable obs, Object obj) {
                if (StandardRuntime.this.getState() == JAMSRuntime.STATE_STOP) {
                    stopButton.setEnabled(false);
                    closeButton.setEnabled(true);
                }
            }
        });

        frame.pack();
        frame.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - width) / 2, (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - height) / 2);

    }

    @Override
    public HashMap<String, JAMSData> getDataHandles() {
        return dataHandles;
    }

    public void setDataHandles(HashMap<String, JAMSData> dataHandles) {
        this.dataHandles = dataHandles;
    }

    @Override
    public void println(String s, int debugLevel) {
        if (debugLevel <= getDebugLevel()) {
            sendInfoMsg(s);
        }
    }

    @Override
    public void println(String s) {
        sendInfoMsg(s);
    }

    @Override
    public int getDebugLevel() {
        return debugLevel;
    }

    @Override
    public void setDebugLevel(int aDebugLevel) {
        debugLevel = aDebugLevel;
    }

    @Override
    public void handle(Throwable t) {
        handle(t, null, false);
    }

    @Override
    public void handle(Throwable t, String cName) {
        handle(t, cName, false);
    }

    @Override
    public void handle(Throwable t, boolean proceed) {
        handle(t, null, proceed);
    }

    public void handle(Throwable t, String cName, boolean proceed) {

        String message = "";

        if (cName != null) {
            message += JAMS.i18n("Exception_occured_in_component_") + cName + "!\n";
        }

        message += t.toString();
        if (getDebugLevel() > JAMS.STANDARD) {
            message += "\n" + StringTools.getStackTraceString(t.getStackTrace());
        }
        sendErrorMsg(message);
        if (!proceed) {
            sendHalt();
        }
    }

    @Override
    public void sendHalt() {
        this.setRunState(JAMSRuntime.STATE_STOP);
    }

    @Override
    public void sendHalt(String str) {
        sendErrorMsg(str);
        sendHalt();
    }

    private String getCallerID() {

        String jamsID = "[000]";

        if (getState() != JAMSRuntime.STATE_RUN) {
            return jamsID;
        }

//        int i = 1;
//        String caller = Reflection.getCallerClass(i).getName();
//        while (caller.equals("jams.runtime.StandardRuntime")) {
//            caller = Reflection.getCallerClass(++i).getName();
//        }

        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        int i = 1;
        String caller = ste[i].getClassName();
        while (caller.equals("jams.runtime.StandardRuntime")) {
            caller = ste[++i].getClassName();
        }

        Integer id = idMap.get(caller);
        if (id != null) {
            return "[" + String.format("%03d", id) + "]";
        } else {
            return jamsID;
        }
    }

    @Override
    public void sendErrorMsg(String str) {
        errorLog.print(JAMS.i18n("ERROR") + ": " + str + "\n");
    }

    @Override
    public void sendInfoMsg(String str) {
        infoLog.print(JAMS.i18n("INFO") + getCallerID() + ": " + str + "\n");
    }

    @Override
    public void addStateObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public int getState() {
        return runState;
    }

    @Override
    public void pause() {
        setRunState(JAMSRuntime.STATE_PAUSE);
    }

    private void setRunState(int state) {
        this.runState = state;
        this.setChanged();
        this.notifyObservers();
    }

    @Override
    public void addInfoLogObserver(Observer o) {
        infoLog.addObserver(o);
    }

    @Override
    public void deleteInfoLogObservers() {
        infoLog.deleteObservers();
    }

    @Override
    public void deleteInfoLogObserver(Observer o) {
        infoLog.deleteObserver(o);
    }

    @Override
    public void addErrorLogObserver(Observer o) {
        errorLog.addObserver(o);
    }

    @Override
    public void deleteErrorLogObservers() {
        errorLog.deleteObservers();
    }

    @Override
    public void deleteErrorLogObserver(Observer o) {
        errorLog.deleteObserver(o);
    }

    @Override
    public String getErrorLog() {
        return errorLog.getLogString();
    }

    @Override
    public String getInfoLog() {
        return infoLog.getLogString();
    }

    @Override
    public void addGUIComponent(GUIComponent component) {
        guiComponents.add(component);
    }

    @Override
    public void saveModelParameter() {

        // save the model's parameter set to the workspace output dir, if it exists

        if (this.model.getWorkspace() == null) {
            return;
        }

        try {
            File modelFile = new File(this.model.getWorkspace().getOutputDataDirectory().getPath()
                    + File.separator + JAMS.DEFAULT_MODEL_FILENAME);
            modelFile.getParentFile().mkdirs();
            ParameterProcessor.saveParams(this.modelDocument, modelFile, this.properties.getProperty("username"), null);
        } catch (IOException ioe) {
            getModel().getRuntime().handle(ioe);
        }
    }

    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    private void readObject(ObjectInputStream objIn) throws IOException, ClassNotFoundException {
        objIn.defaultReadObject();

        deleteErrorLogObservers();
        deleteInfoLogObservers();
        this.initLogging();

        classLoader = JAMSClassLoader.createClassLoader(libs, this);
    }

    private void writeObject(ObjectOutputStream objOut) throws IOException {
        objOut.defaultWriteObject();
    }
}
