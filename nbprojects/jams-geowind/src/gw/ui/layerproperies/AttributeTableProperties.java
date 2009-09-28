/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AttributeTableProperties.java
 *
 * Created on Jan 9, 2009, 1:16:32 PM
 */
package gw.ui.layerproperies;

import gw.api.Events;
import gw.layers.SimpleFeatureLayer;
import gw.ui.util.ProxyTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import javax.swing.table.TableModel;
import ngmf.io.CSProperties;
import ngmf.io.DataIO;
import ngmf.util.Tables;

/**
 *
 * @author ShoverRobot
 */
public class AttributeTableProperties extends javax.swing.JPanel {

    SimpleFeatureLayer layer;
    ProxyTableModel tm;

    /** Creates new form AttributeTableProperties */
    public AttributeTableProperties(File attrData, SimpleFeatureLayer layer, ProxyTableModel table, int index) {
        initComponents();
        setupComponents(table, index);
        this.layer = layer;
        this.tm = table;
        addAttrData(attrData);
    }

    private void setupComponents(final ProxyTableModel table, final int index) {
        maxColorChooser.setColor(table.getMaxColor(index));

        maxColorChooser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    table.setMaxColor(maxColorChooser.getColor(), index);
                    table.setMinColor(minColorChooser.getColor(), index);
                    layer.setAttrMaxColor(maxColorChooser.getColor());
                    layer.setAttrMinColor(minColorChooser.getColor());
                    layer.redraw();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                update();
            }
        });

        minColorChooser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    table.setMaxColor(maxColorChooser.getColor(), index);
                    table.setMinColor(minColorChooser.getColor(), index);
                    layer.setAttrMaxColor(maxColorChooser.getColor());
                    layer.setAttrMinColor(minColorChooser.getColor());
                    layer.redraw();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                update();
            }
        });
    }

    public void addAttrData(File file) {
        if (file.getName().endsWith("csd")) {
            Reader r = null;
            try {
                r = new FileReader(file);
                CSProperties csp = DataIO.properties(r, java.util.ResourceBundle.getBundle("gw/resources/language").getString("L_data"));
                TableModel m = Tables.fromCSP(csp, layer.getFeatures().length);
                tm.addTableModel(m);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    r.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        tm.fireTableStructureChanged();
        update();
    }

    private void update() {
        firePropertyChange(Events.GF_UPDATE, null, layer);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        maxColorChooser = new net.java.dev.colorchooser.ColorChooser();
        jLabel3 = new javax.swing.JLabel();
        minColorChooser = new net.java.dev.colorchooser.ColorChooser();
        jLabel1 = new javax.swing.JLabel();

        javax.swing.GroupLayout maxColorChooserLayout = new javax.swing.GroupLayout(maxColorChooser);
        maxColorChooser.setLayout(maxColorChooserLayout);
        maxColorChooserLayout.setHorizontalGroup(
            maxColorChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );
        maxColorChooserLayout.setVerticalGroup(
            maxColorChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("gw/resources/language"); // NOI18N
        jLabel3.setText(bundle.getString("L_Max_Color")); // NOI18N

        minColorChooser.setColor(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout minColorChooserLayout = new javax.swing.GroupLayout(minColorChooser);
        minColorChooser.setLayout(minColorChooserLayout);
        minColorChooserLayout.setHorizontalGroup(
            minColorChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );
        minColorChooserLayout.setVerticalGroup(
            minColorChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        jLabel1.setText(bundle.getString("L_Min_Color")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(maxColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(minColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)))
                .addContainerGap(113, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(maxColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(minColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(40, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private net.java.dev.colorchooser.ColorChooser maxColorChooser;
    private net.java.dev.colorchooser.ColorChooser minColorChooser;
    // End of variables declaration//GEN-END:variables
}
