/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DataDecisionPanel.java
 *
 * Created on 02.09.2009, 11:02:36
 */
package reg.wizard.tlug.panels;

import jams.JAMSFileFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import org.netbeans.spi.wizard.WizardController;

/**
 *
 * @author hbusch
 */
public class DataDecisionPanel extends javax.swing.JPanel {

    public static final String KEY_DATA = "data";
    public static final String VALUE_SPATIAL = "spatial";
    public static final String VALUE_STATION = "station";
    private DataDecisionPanel thisPanel;
    public static final String KEY_CONFIG_FILENAME = "configFileName";
    private final WizardController controller;
    private final Map wizardData;

    /** Creates new form SpeciesPanel */
    public DataDecisionPanel(WizardController controller, Map wizardData) {

        this.controller = controller;
        this.wizardData = wizardData;

        initComponents();
        setupComponents();

        //By default, nothing is selected
        controller.setProblem("keine Datenart ausgew�hlt");

        //Associate the values with the radio buttons, so the event handler
        //can be very simple
        spatialButton.putClientProperty(KEY_DATA, VALUE_SPATIAL);
        stationButton.putClientProperty(KEY_DATA, VALUE_STATION);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLoadButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        spatialButton = new javax.swing.JRadioButton();
        stationButton = new javax.swing.JRadioButton();

        jLabel1.setText("Konfiguration laden");

        jLoadButton.setText("Ausw�hlen");
        jLoadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLoadButtonActionPerformed(evt);
            }
        });

        spatialButton.setText("r�umliche Daten regionalisieren");
        spatialButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spatialButtonActionPerformed(evt);
            }
        });

        stationButton.setText("Stationsdaten generieren");
        stationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stationButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stationButton)
                    .addComponent(spatialButton)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(54, 54, 54)
                        .addComponent(jLoadButton)))
                .addContainerGap(91, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLoadButton))
                .addGap(31, 31, 31)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(spatialButton)
                .addGap(18, 18, 18)
                .addComponent(stationButton)
                .addContainerGap(101, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void spatialButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spatialButtonActionPerformed
        speciesSelected(evt);
    }//GEN-LAST:event_spatialButtonActionPerformed

    private void stationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stationButtonActionPerformed
        speciesSelected(evt);
    }//GEN-LAST:event_stationButtonActionPerformed

    private void jLoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLoadButtonActionPerformed
        String fileName = null;
        int returnVal = -1;
        JFileChooser chooser = new JFileChooser();
        File currentDir = new File(".");
        chooser.setCurrentDirectory(currentDir);
        chooser.setFileFilter(JAMSFileFilter.getPropertyFilter());
        try {
            returnVal = chooser.showOpenDialog(thisPanel);
            File file = chooser.getSelectedFile();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = file.getPath();
                System.out.println("Load Config from " + fileName);
                loadConfigFile(fileName);
                initFromWizardData();
            }

        } catch (Exception fnfexc) {
            fileName = null;
        }
    }//GEN-LAST:event_jLoadButtonActionPerformed

    /**
     * load the config file and put data into wizardData
     * @param theFileName
     */
    private void loadConfigFile(String theFileName) {

        File file = new File(theFileName);
        if (file.exists()) {
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(theFileName));

                System.out.println(properties.size() + " properties found.");
                Set<String> names = properties.stringPropertyNames();
                for (String propertyName : names) {
                    wizardData.put(propertyName, properties.get(propertyName));
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * init display data from wizard data
     */
    private void initFromWizardData() {
        System.out.println("DataDecisionPanel. initFromWizardData");
        String dataOrigin = (String) wizardData.get(KEY_DATA);
        if (dataOrigin != null && dataOrigin.equals(VALUE_SPATIAL)) {
            spatialButton.setSelected(true);
            buttonSelected(spatialButton);
        }
        if (dataOrigin != null && dataOrigin.equals(VALUE_STATION)) {
            stationButton.setSelected(true);
            buttonSelected(stationButton);
        }
    }

    private void setupComponents() {
        buttonGroup1.add(spatialButton);
        buttonGroup1.add(stationButton);
    }

    private void speciesSelected(java.awt.event.ActionEvent evt) {
        JRadioButton button = (JRadioButton) evt.getSource();
        buttonSelected(button);
    }

    /**
     * clear problems, because button was selected
     * @param theButton
     */
    private void buttonSelected(JRadioButton theButton) {
        wizardData.put(KEY_DATA, theButton.getClientProperty(KEY_DATA));
        controller.setProblem(null);
        controller.setForwardNavigationMode(WizardController.MODE_CAN_CONTINUE);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton jLoadButton;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JRadioButton spatialButton;
    private javax.swing.JRadioButton stationButton;
    // End of variables declaration//GEN-END:variables
}
