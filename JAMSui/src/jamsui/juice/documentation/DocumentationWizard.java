/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jamsui.juice.documentation;

import jams.JAMS;
import jams.JAMSProperties;
import jams.SystemProperties;
import jams.gui.ObserverWorkerDlg;
import jamsui.juice.JUICE;
import jamsui.juice.documentation.DocumentationException.DocumentationExceptionCause;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Observable;
import org.w3c.dom.Document;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.cli.CommandLineOptions;


/**
 *
 * @author chris
 */
public class DocumentationWizard extends Observable {

    final String DOCUMENTATION_DIRECTORY = "/documentation/";

    static void log(String msg) {

        int debug = Integer.parseInt(JUICE.getJamsProperties().getProperty(JAMSProperties.DEBUG_IDENTIFIER, "1"));

        if (debug >= JAMS.VVERBOSE) {
            JUICE.getJuiceFrame().getInfoDlg().appendText(msg);
            System.out.println(msg);
        }
    }

    private void stateMessage(String msg) {
        this.setChanged();
        this.notifyObservers(msg);
    }

    private void runXSLTProcessor(String docBookHome, String documentationHome, String outputXML) throws DocumentationException {
        stateMessage("running xsltproc");

        if (!(new File(docBookHome + "/docbook/fo/docbook.xsl")).exists()) {
            throw new DocumentationException(DocumentationExceptionCause.docBookXSLNotExisting);
        }

//            log(docBookHome + "/xsltproc.exe" + "--xinclude" + "--output" + documentationHome + "/tmp.fo" + docBookHome + "/docbook/fo/docbook.xsl" + outputXML);
        ProcessBuilder pb = new ProcessBuilder(docBookHome + "/xsltproc.exe", "--xinclude", "--output", documentationHome + "/tmp.fo",
                docBookHome + "/docbook/fo/docbook.xsl", outputXML);

        pb.redirectErrorStream(true);
        for (String s : pb.command()) {
            log("argument of xsltproc:" + s + "\n");
        }

        Process process = null;
        try {
            process = pb.start();

            try {
                process.exitValue();
            } catch (Exception e) {
                stateMessage("waiting on xsltproc");
                try {
                    Thread.sleep(300);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    throw new DocumentationException(DocumentationExceptionCause.unknownError, e2.toString());
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        InputStreamReader isr = new InputStreamReader(process.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        String line;

        log("xslt-proc messages:");
        try {
            while ((line = br.readLine()) != null) {
                log(line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    static class MyCommandLineOptions extends CommandLineOptions{
        public FOUserAgent getFOUserAgent(){
            return super.getFOUserAgent();
        }
        public String getOutputFormat() throws FOPException{
            return super.getOutputFormat();
        }
    }

     public static void startFOP(String[] args) throws DocumentationException {
        //System.out.println("static CCL: "
        //    + Thread.currentThread().getContextClassLoader().toString());
        //System.out.println("static CL: " + Fop.class.getClassLoader().toString());
        MyCommandLineOptions options = null;
        FOUserAgent foUserAgent = null;
        OutputStream out = null;

        try {
            options = new MyCommandLineOptions();
            options.parse(args);

            foUserAgent = options.getFOUserAgent();
            String outputFormat = options.getOutputFormat();

            try {
                if (options.getOutputFile() != null) {
                    out = new java.io.BufferedOutputStream(
                            new java.io.FileOutputStream(options.getOutputFile()));
                    foUserAgent.setOutputFile(options.getOutputFile());
                }
                if (!MimeConstants.MIME_XSL_FO.equals(outputFormat)) {
                    options.getInputHandler().renderTo(foUserAgent, outputFormat, out);
                } else {
                    options.getInputHandler().transformTo(out);
                }
            } finally {
                IOUtils.closeQuietly(out);
            }

            // System.exit(0) called to close AWT/SVG-created threads, if any.
            // AWTRenderer closes with window shutdown, so exit() should not
            // be called here
            if (!MimeConstants.MIME_FOP_AWT_PREVIEW.equals(outputFormat)) {
                
            }
        } catch (Exception e) {
            if (options != null) {
                options.getLogger().error("Exception", e);
                System.out.println(e.toString());
            }
            /*if (options.getOutputFile() != null) {
                options.getOutputFile().delete();
            }*/
            throw new DocumentationException(DocumentationExceptionCause.ApacheFOPFailed, e.toString());
        }
    }

    public static void startFOPWithDynamicClasspath(String[] args) throws DocumentationException{
        try {
            URL[] urls = org.apache.fop.cli.Main.getJARList();
            //System.out.println("CCL: "
            //    + Thread.currentThread().getContextClassLoader().toString());
            ClassLoader loader = new java.net.URLClassLoader(urls, null);
            Thread.currentThread().setContextClassLoader(loader);
            Class clazz = Class.forName("org.apache.fop.cli.Main", true, loader);
            //System.out.println("CL: " + clazz.getClassLoader().toString());
            Method mainMethod = clazz.getMethod("startFOP", new Class[]{String[].class});
            mainMethod.invoke(null, new Object[]{args});
        } catch (Exception e) {
            System.err.println("Unable to start FOP:");
            e.printStackTrace();
            throw new DocumentationException(DocumentationExceptionCause.ApacheFOPFailed, e.toString());            
        }
    }

    private String runApacheFOP(String inputFile, String outputFile, String optionalLibaries) throws DocumentationException{
        stateMessage("running Apache FOP");
        System.setProperty("fop.optional.lib", optionalLibaries);
        log(System.getProperty("java.class.path"));
        
        String args[] = new String[]{System.getProperty("java.class.path"), "-fo", inputFile, "-pdf", outputFile};

        String errorLog = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = System.err;
        //System.setErr(new PrintStream(baos));
        if (org.apache.fop.cli.Main.checkDependencies()) {
            log("startFOP");
            startFOP(args);
        }else{
            throw new DocumentationException(DocumentationExceptionCause.FOPDependenciesIncomplete);
            //startFOPWithDynamicClasspath(args);
        }
        System.out.println(baos.toString());
        //System.setErr(ps);
        return errorLog;
        /*System.setProperty("fop.optional.lib", optionalLibaries);
        try {
            forbidSystemExitCall();
            log(System.getProperty("java.class.path"));
            org.apache.fop.cli.Main.main(new String[]{System.getProperty("java.class.path"), "-fo", inputFile, "-pdf", outputFile});
        } catch (ExitTrappedException t) {            
            JOptionPane.showMessageDialog(null, Bundle.resources.getString("Your_documentation_was_created_successfully."));            
            return; //this means succsess
        } catch (Throwable t) {
            t.printStackTrace();
            throw new DocumentationException(DocumentationExceptionCause.ApacheFOPFailed, t.toString());
        } finally {
            enableSystemExitCall();
        }*/
    }    
    
    DocumentationException innerException = null;

    private void openPDF(final File f) throws DocumentationException {
        stateMessage("showing pdf");
        innerException = null;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + f.getAbsolutePath());
                } catch (IOException ex) {
                    innerException = new DocumentationException(DocumentationExceptionCause.ViewPDFError, ex.toString());
                    return;
                }
            }
        });

        thread.start();

        try {
            thread.isAlive();

        } catch (Exception e) {
            stateMessage("opening pdf");
            if (innerException != null) {
                throw innerException;
            }
            try {
                Thread.sleep(300);
            } catch (Exception e2) {
                e2.printStackTrace();
                throw new DocumentationException(DocumentationExceptionCause.unknownError, e2.toString());
            }
        }
    }

    public void runDocumentationProcess(File workspace, Document modelDocument, String docBookHome) throws DocumentationException {
        stateMessage("initializing");

        if (workspace == null) {
            throw new DocumentationException(DocumentationExceptionCause.workspaceNull);
        }

        File documentationHome = new File(workspace + DOCUMENTATION_DIRECTORY);
        File documentationOutputDir = new File(documentationHome, "out/");
        String documentationOutputXML = documentationOutputDir + "/" + Bundle.resources.getString("Filename") + ".xml";
        documentationOutputDir.mkdirs();

        log("docbook-home:" + docBookHome);

        if (docBookHome == null) {
            throw new DocumentationException(DocumentationExceptionCause.docBookPathNull);
        }

        if (!(new File(docBookHome + "/xsltproc.exe")).exists()) {
            throw new DocumentationException(DocumentationExceptionCause.xsltProcNotExisting);
        }

        if (documentationHome == null || !documentationHome.exists()) {
            throw new DocumentationException(DocumentationExceptionCause.documentationPathNull);
        }

        if (modelDocument == null) {
            throw new DocumentationException(DocumentationExceptionCause.docBookPathNull);
        }

        log("working in workspace:" + workspace);

        stateMessage("creating documentation");

        DocumentationGenerator generator = new DocumentationGenerator();
        generator.createDocumentation(documentationHome, documentationOutputDir, modelDocument);

        try {

            runXSLTProcessor(docBookHome, documentationOutputDir.getAbsolutePath(), documentationOutputXML);
            runApacheFOP(documentationOutputDir + "/tmp.fo", documentationHome + "/" + Bundle.resources.getString("Filename") + ".pdf", this.properties.getProperty("libs"));

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {

            // cleanup
//            String[] children = documentationOutputDir.list();
//            for (int i = 0; i < children.length; i++) {
//                new File(documentationOutputDir, children[i]).delete();
//            }
//            documentationOutputDir.delete();

        }

        openPDF(new File(documentationHome, Bundle.resources.getString("Filename") + ".pdf"));

        stateMessage("finished");
    }
    
    private JAMSProperties properties = null;
    private File workspace = null;
    private Document modelDocument = null;

    public void createDocumentation(Frame parent, Document doc, JAMSProperties props, File savePath) {
        properties = props;
        // ok hier gibt es mehrere möglichkeiten
        workspace = savePath.getParentFile();
        modelDocument = doc;

        ObserverWorkerDlg progress = new ObserverWorkerDlg(parent, Bundle.resources.getString("Generating_Documentation"));
        this.addObserver(progress);

        progress.setInderminate(true);
        progress.setTask(new Runnable() {

            @Override
            public void run() {
                try {
                    runDocumentationProcess(workspace, modelDocument, properties.getProperty(SystemProperties.DOCBOOK_HOME_PATH));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
            }
        });
        progress.execute();
    }
}
