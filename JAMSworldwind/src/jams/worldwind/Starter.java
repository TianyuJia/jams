/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jams.worldwind;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import gov.nasa.worldwind.Configuration;
import jams.worldwind.ui.MainFrame;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bigr
 */
public class Starter {

    final static String appName = "JAMS WorldWind";
    final static Logger logger = LoggerFactory.getLogger(Starter.class);

    static {
        System.setProperty("java.net.useSystemProxies", "true");
        if (Configuration.isMacOS()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "World Wind Application");
            System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
            System.setProperty("apple.awt.brushMetalLook", "true");
        } else if (Configuration.isWindowsOS()) {
            System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing
        }
    }

    public static void main(String[] args) {
        logger.info("Entering Starter application.");
        if (Configuration.isMacOS()) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
        }
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        // print logback's internal status
        StatusPrinter.print(lc);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                JFrame frame = new MainFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setName(appName);
                frame.pack();
                frame.setVisible(true);
            }
        });
        logger.info("Exiting Starter application.");
    }
}
