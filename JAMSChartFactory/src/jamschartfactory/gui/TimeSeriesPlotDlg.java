/*
 * TimeSeriesPlotDlg.java
 *
 * Created on 25. April 2006, 15:58
 */

package jamschartfactory.gui;

/**
 *
 * @author  c0krpe
 */
public class TimeSeriesPlotDlg extends javax.swing.JDialog {
    public String plotType;
    public boolean returnval;
    
    /** Creates new form TimeSeriesPlotDlg */
    public TimeSeriesPlotDlg() {
        this.setModal(true);
        initComponents();
        this.LinePlotButton.setSelected(true);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        LinePlotButton = new javax.swing.JToggleButton();
        BarPlotButton = new javax.swing.JToggleButton();
        DotPlotButton = new javax.swing.JToggleButton();
        AreaPlotButton = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        OKButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        buttonGroup1.add(LinePlotButton);
        LinePlotButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jamschartfactory/resources/linePlot.jpg")));
        LinePlotButton.setSelected(true);
        LinePlotButton.setText("LinePlot");
        LinePlotButton.setMaximumSize(new java.awt.Dimension(69, 69));
        LinePlotButton.setMinimumSize(new java.awt.Dimension(69, 69));
        LinePlotButton.setPreferredSize(new java.awt.Dimension(69, 69));
        LinePlotButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jamschartfactory/resources/linePlot_s.jpg")));

        buttonGroup1.add(BarPlotButton);
        BarPlotButton.setText("BarPlot");
        BarPlotButton.setMaximumSize(new java.awt.Dimension(69, 69));
        BarPlotButton.setMinimumSize(new java.awt.Dimension(69, 69));
        BarPlotButton.setPreferredSize(new java.awt.Dimension(69, 69));

        buttonGroup1.add(DotPlotButton);
        DotPlotButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jamschartfactory/resources/dotPlot.jpg")));
        DotPlotButton.setText("DotPlot");
        DotPlotButton.setMaximumSize(new java.awt.Dimension(69, 69));
        DotPlotButton.setMinimumSize(new java.awt.Dimension(69, 69));
        DotPlotButton.setPreferredSize(new java.awt.Dimension(69, 69));
        DotPlotButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/jamschartfactory/resources/dotPlot_s.jpg")));

        buttonGroup1.add(AreaPlotButton);
        AreaPlotButton.setText("AreaPlot");
        AreaPlotButton.setMaximumSize(new java.awt.Dimension(69, 69));
        AreaPlotButton.setMinimumSize(new java.awt.Dimension(69, 69));
        AreaPlotButton.setPreferredSize(new java.awt.Dimension(69, 69));

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(LinePlotButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(BarPlotButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel3Layout.createSequentialGroup()
                .add(DotPlotButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(AreaPlotButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(LinePlotButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(DotPlotButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(AreaPlotButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .add(BarPlotButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(21, 21, 21)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(231, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(104, Short.MAX_VALUE))
        );
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        OKButton.setText("OK");
        OKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OKButtonActionPerformed(evt);
            }
        });

        jPanel2.add(OKButton);

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        jPanel2.add(CancelButton);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.returnval = false;
        this.dispose();
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void OKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKButtonActionPerformed
        if(this.LinePlotButton.isSelected())
            plotType = "line";
        else if(this.BarPlotButton.isSelected())
            plotType = "bar";
        else if(this.AreaPlotButton.isSelected())
            plotType = "area";
        else if(this.DotPlotButton.isSelected())
            plotType = "dot";
        this.returnval = true;
        this.dispose();
    }//GEN-LAST:event_OKButtonActionPerformed
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton AreaPlotButton;
    private javax.swing.JToggleButton BarPlotButton;
    private javax.swing.JButton CancelButton;
    private javax.swing.JToggleButton DotPlotButton;
    private javax.swing.JToggleButton LinePlotButton;
    private javax.swing.JButton OKButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
    
}
