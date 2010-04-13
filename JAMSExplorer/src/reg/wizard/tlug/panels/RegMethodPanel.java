/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RegMethodPanel.java
 *
 * Created on 28.08.2009, 15:00:46
 */

package reg.wizard.tlug.panels;

import java.util.Map;
import javax.swing.JSlider;
import javax.swing.JTextField;
import org.h2.util.StringUtils;
import org.netbeans.spi.wizard.WizardController;

/**
 *
 * @author hbusch
 */
public class RegMethodPanel extends javax.swing.JPanel {


    private final WizardController controller;
    private final Map wizardData;

    public static final String KEY_REG = "regVerfahren";
    public static final String KEY_STATION = "numberStations";
    public static final String KEY_GEWICHTUNG = "gewichtung";
    public static final String KEY_UMKREIS = "umkreis";
    public static final String KEY_SCHWELLENWERT = "threshold";

    // all field contents
    private String r_regVerfahren;
    private String r_numberstations;
    private String r_gewichtung;
    private String r_umkreis;
    private String r_schwellenwert;

    /** Creates new form CatHairLengthPanel */
    public RegMethodPanel(WizardController controller, Map wizardData) {
        initComponents();
        setupComponents();
        this.controller = controller;
        this.wizardData = wizardData;

        initFromWizardData();
        checkProblems();

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
        jLabel2 = new javax.swing.JLabel();
        jNumberStations = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jGewichtung = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jSliderSchwellenwert = new javax.swing.JSlider();
        jSchwellenwert = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        jLabel1.setText("Regionalisierungsverfahren");

        jLabel2.setText("Anzahl Stationen");

        jNumberStations.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jNumberStationsFocusLost(evt);
            }
        });

        jLabel4.setText("Gewichtung");

        jGewichtung.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jGewichtungFocusLost(evt);
            }
        });

        jLabel5.setText("Schwellenwert r2");

        jSliderSchwellenwert.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSliderSchwellenwertMouseReleased(evt);
            }
        });

        jSchwellenwert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSchwellenwertActionPerformed(evt);
            }
        });
        jSchwellenwert.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jSchwellenwertFocusLost(evt);
            }
        });

        jLabel3.setText("IDW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(70, 70, 70)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jSliderSchwellenwert, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jNumberStations, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jGewichtung, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)))))
                        .addGap(18, 18, 18)
                        .addComponent(jSchwellenwert, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(162, 162, 162))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jNumberStations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jGewichtung, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSchwellenwert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jSliderSchwellenwert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(118, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jSliderSchwellenwertMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderSchwellenwertMouseReleased
        int schwellenwert = ((JSlider) evt.getSource()).getValue();
        r_schwellenwert = Integer.toString(schwellenwert);
        // System.out.println("jSliderSchwellenwertMouseReleased. r_schwellenwert:" + r_schwellenwert);
        jSchwellenwert.setText(r_schwellenwert);
        checkProblems();

    }//GEN-LAST:event_jSliderSchwellenwertMouseReleased

    private void jNumberStationsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jNumberStationsFocusLost
        r_numberstations = ((JTextField) evt.getSource()).getText();
        checkProblems();


    }//GEN-LAST:event_jNumberStationsFocusLost

    private void jGewichtungFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jGewichtungFocusLost
        r_gewichtung = ((JTextField) evt.getSource()).getText();
        checkProblems();

    }//GEN-LAST:event_jGewichtungFocusLost

    private void jSchwellenwertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSchwellenwertActionPerformed
}//GEN-LAST:event_jSchwellenwertActionPerformed

    private void jSchwellenwertFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jSchwellenwertFocusLost
        String s = ((JTextField) evt.getSource()).getText();
        try {
            int sInt = Integer.parseInt(s);
            if (sInt> 100)
                throw new NumberFormatException("Wert ist zu groß.");
            r_schwellenwert = s;
            // System.out.println("jSchwellenwertFocusLost. r_schwellenwert:" + r_schwellenwert);
            jSliderSchwellenwert.setValue(sInt);
            checkProblems();
        } catch (NumberFormatException e) {
            controller.setProblem("Bitte Wert zwischen 1 .. 100 eingeben.");
        }
    }//GEN-LAST:event_jSchwellenwertFocusLost

    private void checkProblems() {
        if (StringUtils.isNullOrEmpty(r_regVerfahren)) {
            controller.setProblem("Bitte Regionalisierungsverfahren auswählen.");
        } else {
            wizardData.put(KEY_REG, r_regVerfahren);
            if (StringUtils.isNullOrEmpty(r_numberstations)) {
                controller.setProblem("Bitte Anzahl der Stationen eingeben.");
            } else {
                wizardData.put(KEY_STATION, r_numberstations);
                if (StringUtils.isNullOrEmpty(r_umkreis)) {
                    controller.setProblem("Bitte Umkreis festlegen.");
                } else {
                    wizardData.put(KEY_UMKREIS, r_umkreis);
                    if (StringUtils.isNullOrEmpty(r_gewichtung)) {
                        controller.setProblem("Bitte Gewichtung auswählen.");
                    } else {
                        wizardData.put(KEY_GEWICHTUNG, r_gewichtung);
                        if (r_schwellenwert == null || Integer.parseInt(r_schwellenwert) == 0) {
                            controller.setProblem("Bitte Schwellenwert definieren.");
                        } else {
                            float f = Float.parseFloat(r_schwellenwert) / 100;
                            wizardData.put(KEY_SCHWELLENWERT, Float.toString(f));
                            controller.setProblem(null);
                        }
                    }
                }
            }
        }
        return;
    }

    private void setupComponents() {

        // defaults
        r_regVerfahren = "IDW";
        r_numberstations = "5";
        r_gewichtung = "2";
        r_umkreis = "10";
        r_schwellenwert = "75";
    }

    /**
     * init display data from wizard data
     */
    private void initFromWizardData() {

        String regVerfahren = (String) wizardData.get(KEY_REG);
        if (!StringUtils.isNullOrEmpty(regVerfahren)) {
            r_regVerfahren = regVerfahren;
        }

        String station = (String) wizardData.get(KEY_STATION);
        if (!StringUtils.isNullOrEmpty(station)) {
            r_numberstations = station;
        }
        if (!StringUtils.isNullOrEmpty(r_numberstations)) {
            jNumberStations.setText(r_numberstations);
        }

        String umkreis = (String) wizardData.get(KEY_UMKREIS);
        if (!StringUtils.isNullOrEmpty(umkreis)) {
            r_umkreis = umkreis;
        }

        String gewichtung = (String) wizardData.get(KEY_GEWICHTUNG);
        if (!StringUtils.isNullOrEmpty(gewichtung)) {
            r_gewichtung = gewichtung;
        }
        if (!StringUtils.isNullOrEmpty(r_gewichtung)) {
            jGewichtung.setText(r_gewichtung);
        }

        if (wizardData.containsKey(KEY_SCHWELLENWERT)) {
            String sSchwellenwert = (String) wizardData.get(KEY_SCHWELLENWERT);
            if (!StringUtils.isNullOrEmpty(sSchwellenwert)) {
                int i = Math.round(Float.parseFloat(sSchwellenwert) * 100);
                sSchwellenwert = Integer.toString(i);
                int schwellenwert = Integer.parseInt(sSchwellenwert);
                if (schwellenwert >= 0) {
                    r_schwellenwert = sSchwellenwert;
                }
            }
        }
        if (!StringUtils.isNullOrEmpty(r_schwellenwert)) {
                int schwellenwert = Integer.parseInt(r_schwellenwert);
            if (schwellenwert >= 0) {
                jSliderSchwellenwert.setValue(schwellenwert);
                jSchwellenwert.setText(r_schwellenwert);
            }
        }

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jGewichtung;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField jNumberStations;
    private javax.swing.JTextField jSchwellenwert;
    private javax.swing.JSlider jSliderSchwellenwert;
    // End of variables declaration//GEN-END:variables

}
