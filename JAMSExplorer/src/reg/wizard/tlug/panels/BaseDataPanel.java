/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * BaseDataPanel.java
 *
 * Created on 31.08.2009, 11:22:43
 */
package reg.wizard.tlug.panels;

import jams.data.JAMSTimeInterval;
import jams.JAMSFileFilter;
import jams.gui.input.InputComponent;
import jams.gui.input.InputComponentFactory;
import jams.gui.input.TimeintervalInput;
import jams.gui.input.ValueChangeListener;
import jams.tools.StringTools;
import java.io.File;
import java.util.Map;
import java.util.Vector;
import javax.swing.JFileChooser;
import org.h2.util.StringUtils;
import org.netbeans.spi.wizard.WizardController;

/**
 *
 * @author hbusch
 */
public class BaseDataPanel extends javax.swing.JPanel {

    private final WizardController controller;
    private final Map wizardData;
    private BaseDataPanel thisPanel;
    public static final String KEY_SHAPE_FILENAME = "shapeFileName";
    public static final String KEY_REGIONALIZATION = "regionalizationData";
    public static final String KEY_REGDATA_KEYS = "regDataKeys";
    public static final String KEY_REGDATA_DISPS = "regDataDisps";
    private Vector<String> regData_Display = new Vector<String>();
    private Vector<String> regData_Key = new Vector<String>();
    public static final String KEY_INTERVAL = "interval";

    // all field contents
    private String r_shapeFileName = null;
    private String r_region = null;
    private String r_interval = null;


    /** Creates new form  */
    public BaseDataPanel(WizardController controller, Map wizardData) {

        this.controller = controller;
        this.wizardData = wizardData;
        thisPanel = this;

        initComponents();
        setupComponents();

        initFromWizardData();
        checkProblems();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {

        //buttonGroup1 = new javax.swing.ButtonGroup();
        //buttonGroup2 = new javax.swing.ButtonGroup();
        jFileLabel = new javax.swing.JLabel();
        jFileName = new javax.swing.JTextField();
        jFileButton = new javax.swing.JButton();
        jRegLabel = new javax.swing.JLabel();
        jRegCombo = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jIntervall = InputComponentFactory.createInputComponent(JAMSTimeInterval.class, false);

        jIntervall.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChanged() {
                try {
                    checkProblems();
                } catch (Exception e) {
                    // do nothing
                }
            }
        });

        jFileLabel.setText("Shape-File");

        jFileName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jFileNameFocusLost(evt);
            }
        });

        jFileButton.setText("Auswahl");
        jFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileButtonActionPerformed(evt);
            }
        });

        jRegLabel.setText("Regionalisierung von");

        // will be done later in initFromWizardData
        //jRegCombo.setModel(new javax.swing.DefaultComboBoxModel(regData_Display));
        jRegCombo.addFocusListener(new java.awt.event.FocusListener() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jRegComboFocusLost(evt);
            }
            public void focusGained(java.awt.event.FocusEvent evt) {
            }
        });

        jLabel3.setText("Zeitintervall");


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jFileLabel)
                    .addComponent(jRegLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
//                    .addComponent(jLabel2)
//                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jFileButton))
                    .addComponent(jRegCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jIntervall.getComponent(), 300, 300, 400))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jFileLabel)
                    .addComponent(jFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFileButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRegLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRegCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jIntervall.getComponent(), 100, 150, 250))
                .addGap(18, 18, 18)
 .addContainerGap(50, Short.MAX_VALUE))
        );
    }

    private void jRegComboFocusLost(java.awt.event.FocusEvent evt) {
        int selIndex = jRegCombo.getSelectedIndex();
        //System.out.println("jRegCombo. " + jRegCombo.getSelectedItem() + ", selIndex=" + selIndex + ", -> " + regData_Key.elementAt(selIndex));
        r_region = regData_Key.elementAt(selIndex);
        checkProblems();

}


    private void jFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String fileName = null;
        int returnVal = -1;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(JAMSFileFilter.getShapeFilter());
        try {
            returnVal = chooser.showOpenDialog(thisPanel);
            File file = chooser.getSelectedFile();
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = file.getPath();
                jFileName.setText(fileName);
            }

        } catch (Exception fnfexc) {
            fileName = null;
        }
        r_shapeFileName = fileName;
        checkProblems();
    }


    private void jFileNameFocusLost(java.awt.event.FocusEvent evt) {
        r_shapeFileName = jFileName.getText();
        checkProblems();
    }

    private void setupComponents() {

        // group buttons
        //buttonGroup1.add(jRadioButtonPrim);
        //buttonGroup1.add(jRadioButtonSeku);

        //buttonGroup2.add(jRadioButtonTag);
        //buttonGroup2.add(jRadioButtonMonat);
        //buttonGroup2.add(jRadioButtonJahr);

        //jRadioButtonPrim.putClientProperty(KEY_DATA_ORIGIN, VALUE_PRIM);
        //jRadioButtonSeku.putClientProperty(KEY_DATA_ORIGIN, VALUE_SEK);

        //jRadioButtonTag.putClientProperty(KEY_AGGR, VALUE_TAG);
        //jRadioButtonMonat.putClientProperty(KEY_AGGR, VALUE_MON);
        //jRadioButtonJahr.putClientProperty(KEY_AGGR, VALUE_JAHR);

        // defaults
        //r_dataOrigin = VALUE_PRIM;
        //r_aggreg = VALUE_TAG;
        r_interval = "1999-01-01 7:30 2010-12-31 7:30 6 1";

    }


    private void checkProblems() {

        if (StringTools.isEmptyString(r_shapeFileName)) {
            r_shapeFileName = "";
            controller.setProblem("Bitte Shape-File auswählen.");
        } else {
            wizardData.put(KEY_SHAPE_FILENAME, r_shapeFileName);
            //wizardData.put(KEY_DATA_ORIGIN, r_dataOrigin);
            if (StringUtils.isNullOrEmpty(r_region)) {
                controller.setProblem("Bitte Regionalisierung festlegen.");
            } else {
                wizardData.put(KEY_REGIONALIZATION, r_region);

                int errorCode = jIntervall.getErrorCode();
                if (errorCode>0) {
                    controller.setProblem("Bitte Zeitintervall auswählen.");

                } else {
                    r_interval = jIntervall.getValue();
                    //System.out.println("r_interval:" + r_interval);
                    wizardData.put(KEY_INTERVAL, r_interval);
                    controller.setProblem(null);
                }
            }
        }
        return;
    }

    /**
     * init display data from wizard data
     */
    private void initFromWizardData() {
        
        String shapeFileName = (String) wizardData.get(KEY_SHAPE_FILENAME);
        if (!StringUtils.isNullOrEmpty(shapeFileName)) {
            r_shapeFileName = shapeFileName;
            jFileName.setText(r_shapeFileName);
        }

        regData_Key = (Vector<String>) wizardData.get(KEY_REGDATA_KEYS);
        regData_Display = (Vector<String>) wizardData.get(KEY_REGDATA_DISPS);
        jRegCombo.setModel(new javax.swing.DefaultComboBoxModel(regData_Display));
        String regionalizationData = (String) wizardData.get(KEY_REGIONALIZATION);
        if (!StringUtils.isNullOrEmpty(regionalizationData)) {
            r_region = regionalizationData;
        } else {
            r_region = regData_Key.elementAt(0);
        }
        if (!StringUtils.isNullOrEmpty(r_region)) {
            int theIndex = regData_Key.indexOf(r_region);
            if (theIndex < 0)
                System.out.println("Wert " + r_region + " nicht in regData_Key gefunden. System falsch initialisiert.");
            else
                jRegCombo.setSelectedIndex(theIndex);
        }

        String interval = (String) wizardData.get(KEY_INTERVAL);
        if (!StringUtils.isNullOrEmpty(interval)) {
            r_interval = interval;
        }
        if (!StringUtils.isNullOrEmpty(r_interval)) {
            ((TimeintervalInput)jIntervall).setValue(r_interval);
        }
    }


    // Variables declaration
    //private javax.swing.ButtonGroup buttonGroup1;
    //private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jFileButton;
    private javax.swing.JLabel jFileLabel;
    private javax.swing.JTextField jFileName;
    private InputComponent jIntervall;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox jRegCombo;
    private javax.swing.JLabel jRegLabel;
    // End of variables declaration
}
