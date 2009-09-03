/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AddCompsPanel.java
 *
 * Created on 28.08.2009, 15:01:28
 */

package reg.wizard.tlug.panels;

import jams.io.JAMSFileFilter;
import java.util.Map;
import org.h2.util.StringUtils;
import org.netbeans.spi.wizard.WizardController;

/**
 *
 * @author hbusch
 */
public class AddCompsPanel extends javax.swing.JPanel {

    private final WizardController controller;
    private final Map wizardData;

    /** Creates new form CatHairLengthPanel */
    public AddCompsPanel(WizardController controller, Map wizardData) {
        initComponents();

        this.controller = controller;
        this.wizardData = wizardData;
        String configFileName = (String) wizardData.get(DataDecisionPanel.KEY_CONFIG_FILENAME);
        if (!StringUtils.isNullOrEmpty(configFileName)) {
            jConfigFile.setText(configFileName);
        }
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jConfigFile = new javax.swing.JTextField();

        jLabel1.setText("Konfiguration speichern unter");

        jConfigFile.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jConfigFileFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel1)
                .addGap(26, 26, 26)
                .addComponent(jConfigFile, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(175, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(158, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jConfigFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(120, 120, 120))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jConfigFileFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jConfigFileFocusLost
        // TODO add your handling code here:

//        String fileName = jConfigFile.getText();
//        if (fileName != null && !fileName.endsWith(JAMSFileFilter.PROPERTY_EXTENSION)) {
//            fileName = fileName + JAMSFileFilter.PROPERTY_EXTENSION;
//        }
//        wizardData.put(DataDecisionPanel.KEY_CONFIG_FILENAME, fileName);

    }//GEN-LAST:event_jConfigFileFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jConfigFile;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

}
