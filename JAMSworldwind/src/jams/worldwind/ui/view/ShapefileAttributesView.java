package jams.worldwind.ui.view;

import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.SurfacePolygons;
import jams.worldwind.handler.SurfacePolygonClassCellEditor;
import jams.worldwind.ui.model.Globe;
import jams.worldwind.ui.model.ShapefileAttributesModel;
import jams.worldwind.ui.renderer.SurfacePolygonClassCellRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author Ronny Berndt <ronny.berndt@uni-jena.de>
 */
public class ShapefileAttributesView {

    private static ShapefileAttributesView instance = null;
    final private JFrame theFrame;
    final private JTable theTable;
    private ShapefileAttributesModel theTableModel;

    //Singleton pattern
    /**
     *
     * @return
     */
    public synchronized static ShapefileAttributesView getInstance() {
        if (instance == null) {
            instance = new ShapefileAttributesView("LAST SELECTED OBJECTS");
        }
        return instance;
    }

    private ShapefileAttributesView(String title) {
        this.theFrame = new JFrame(title);
        this.theTable = new JTable();

        this.theTable.setIntercellSpacing(new Dimension(6, 6));
        this.theTable.setRowHeight(this.theTable.getRowHeight() + 6);

        this.theTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                Globe.getInstance().getWorldWindow().redrawNow();
            }
        });

        JScrollPane scrollPane = new JScrollPane(this.theTable);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 0));
        mainPanel.add(scrollPane);

        theFrame.getContentPane().add(mainPanel);
        theFrame.setSize(theFrame.getPreferredSize());
    }

    /**
     *
     * @param objects
     */
    public void fillTableWithObjects(List<?> objects) {
        this.theTableModel = new ShapefileAttributesModel(objects);
        this.theTable.setModel(this.theTableModel);

        this.theTable.setAutoCreateRowSorter(true);
        this.theTable.setColumnSelectionAllowed(true);

        this.theTable.setDefaultEditor(SurfacePolygons.class, new SurfacePolygonClassCellEditor());
        this.theTable.setDefaultRenderer(SurfacePolygons.class, new SurfacePolygonClassCellRenderer());
        this.autoResizeColWidth(theTable, theTableModel);

        TableCellRenderer rendererFromHeader = this.theTable.getTableHeader().getDefaultRenderer();
        JLabel headerLabel = (JLabel) rendererFromHeader;
        headerLabel.setHorizontalAlignment(JLabel.CENTER); // Here you can set the alignment you want.
    }

    /*  Code from http://ieatbinary.com/2008/08/13/auto-resize-jtable-column-width/
     */
    /**
     *
     * @param table
     * @param model
     * @return
     */
    public JTable autoResizeColWidth(JTable table, DefaultTableModel model) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //table.setModel(model);

        int margin = 5;

        for (int i = 0; i < table.getColumnCount(); i++) {
            int vColIndex = i;
            DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
            TableColumn col = colModel.getColumn(vColIndex);
            int width = 0;

            // Get width of column header
            TableCellRenderer renderer = col.getHeaderRenderer();

            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }

            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);

            width = comp.getPreferredSize().width;

            // Get maximum width of column data
            for (int r = 0; r < table.getRowCount(); r++) {
                renderer = table.getCellRenderer(r, vColIndex);
                comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false,
                        r, vColIndex);
                Object o = table.getValueAt(r, vColIndex);
                if (!(o instanceof Material)) {
                    width = Math.max(width, comp.getPreferredSize().width);
                }
            }

            // Add margin
            width += 2 * margin;

            // Set the width
            col.setPreferredWidth(width);
        }

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(
                SwingConstants.LEFT);

        // table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);

        return table;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.theFrame.setTitle(title);
    }

    /**
     *
     * @param b
     */
    public void show(boolean b) {
        this.theFrame.pack();
        this.theFrame.setVisible(b);
    }
}