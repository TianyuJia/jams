/*
 * CalcPanel.java
 *
 * Created on 14. November 2005, 15:06
 */

package jamschartfactory.tableView;
import javax.swing.JTable;
/**
 *
 * @author  c0krpe
 */
public class CalcPanel extends javax.swing.JPanel {
    JTable table;
    /**
     * Creates new form CalcPanel 
     */
    public CalcPanel(JTable table) {
        initComponents();
        this.table = table;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tf_formula = new javax.swing.JTextField();
        plusButton = new javax.swing.JButton();
        minusButton = new javax.swing.JButton();
        timesButton = new javax.swing.JButton();
        divButton = new javax.swing.JButton();
        isButton = new javax.swing.JButton();
        calcButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        tf_formula.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tf_formulaKeyPressed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 159;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(tf_formula, gridBagConstraints);

        plusButton.setFont(new java.awt.Font("Arial", 0, 10));
        plusButton.setText("+");
        plusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plusButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(plusButton, gridBagConstraints);

        minusButton.setFont(new java.awt.Font("Arial", 0, 10));
        minusButton.setText("-");
        minusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minusButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(minusButton, gridBagConstraints);

        timesButton.setFont(new java.awt.Font("Arial", 0, 10));
        timesButton.setText("*");
        timesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timesButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(timesButton, gridBagConstraints);

        divButton.setFont(new java.awt.Font("Arial", 0, 10));
        divButton.setText("/");
        divButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                divButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(divButton, gridBagConstraints);

        isButton.setFont(new java.awt.Font("Arial", 0, 10));
        isButton.setText("=");
        isButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(isButton, gridBagConstraints);

        calcButton.setFont(new java.awt.Font("Arial", 0, 10));
        calcButton.setText("calc");
        calcButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        add(calcButton, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents

    private void tf_formulaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_formulaKeyPressed
        if(evt.getKeyCode() == evt.VK_ENTER){
            if(this.table.getSelectedColumnCount() == 0 && this.table.getSelectedRowCount() == 0)
                return;
            TableCalculator.calcEntries(this.table, this.tf_formula.getText());
            this.tf_formula.setText("");
        }
        
    }//GEN-LAST:event_tf_formulaKeyPressed

    private void calcButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcButtonActionPerformed
        if(this.table.getSelectedColumnCount() == 0 && this.table.getSelectedRowCount() == 0)
            return;
        TableCalculator.calcEntries(this.table, this.tf_formula.getText());
        this.tf_formula.setText("");
    }//GEN-LAST:event_calcButtonActionPerformed

    private void isButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isButtonActionPerformed
        this.tf_formula.setText("= ");
        this.tf_formula.grabFocus();
    }//GEN-LAST:event_isButtonActionPerformed

    private void divButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_divButtonActionPerformed
        this.tf_formula.setText("/ ");
        this.tf_formula.grabFocus();
    }//GEN-LAST:event_divButtonActionPerformed

    private void timesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timesButtonActionPerformed
        this.tf_formula.setText("* ");
        this.tf_formula.grabFocus();
    }//GEN-LAST:event_timesButtonActionPerformed

    private void minusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minusButtonActionPerformed
        this.tf_formula.setText("- ");
        this.tf_formula.grabFocus();
    }//GEN-LAST:event_minusButtonActionPerformed

    private void plusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plusButtonActionPerformed
        this.tf_formula.setText("+ ");
        this.tf_formula.grabFocus();
    }//GEN-LAST:event_plusButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton calcButton;
    private javax.swing.JButton divButton;
    private javax.swing.JButton isButton;
    private javax.swing.JButton minusButton;
    private javax.swing.JButton plusButton;
    private javax.swing.JTextField tf_formula;
    private javax.swing.JButton timesButton;
    // End of variables declaration//GEN-END:variables
    
}
