package jams.worldwind.ui.view;

import gov.nasa.worldwind.layers.Layer;
import jams.worldwind.handler.LayerListItemTransferHandler;
import jams.worldwind.events.Events;
import jams.worldwind.events.Observer;
import jams.worldwind.ui.model.Globe;
import jams.worldwind.ui.model.LayerListModel;
import jams.worldwind.ui.renderer.LayerListRenderer;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ronny Berndt <ronny.berndt@uni-jena.de>
 */
public class LayerListView implements PropertyChangeListener, ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(LayerListView.class);
    private JFrame theFrame;
    JScrollPane scrollPane;
    private JList layers;
    private LayerListModel layerModel;
    private Globe globeModel = Globe.getInstance();

    private int indexToRemove;

    /**
     *
     */
    public LayerListView() {
        Observer.getInstance().addPropertyChangeListener(this);
        this.layerModel = new LayerListModel();

        theFrame = new JFrame("LAYERS");
        layers = new JList(layerModel);

        layers.setDragEnabled(
                true);
        layers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        layers.setDropMode(DropMode.INSERT);

        layers.setTransferHandler(
                new LayerListItemTransferHandler(layerModel));

        theFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        theFrame.setLayout(
                new GridLayout(1, 1));

        scrollPane = new JScrollPane(layers);

        scrollPane.setBorder(
                new TitledBorder("Available Layers"));

        layers.setCellRenderer(
                new LayerListRenderer());
        layers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add a mouse listener to handle changing selection
        layers.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        JList list = (JList) event.getSource();

                        // Get index of item clicked
                        int index = list.locationToIndex(event.getPoint());
                        Layer item = (Layer) list.getModel().getElementAt(index);

                        if (SwingUtilities.isLeftMouseButton(event)) {

                            // Toggle selected state
                            item.setEnabled(!item.isEnabled());
                            Layer l = globeModel.getWorldWindow().getModel().getLayers().getLayerByName(item.getName());

                            if (l != null) {
                                l.setEnabled(item.isEnabled());
                            } else {
                                logger.error("Clicked layer not found at WorldWind model!");
                            }
                            Observer.getInstance().getPCS().firePropertyChange(Events.LAYER_CHANGED, null, null);
                            //globeModel.getWorldWindow().redraw();
                            // Repaint cell
                            list.repaint(list.getCellBounds(index, index));
                        } /*else if (SwingUtilities.isRightMouseButton(event)) {
                            if (event.isPopupTrigger()) {
                                JPopupMenu popup = createPopupMenu();

                                popup.show(event.getComponent(), event.getX(), event.getY());
                                indexToRemove = list.locationToIndex(event.getPoint());
                                System.out.println("Rechtsklick");
                            }
                        }*/
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        //super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.
                        if (e.isPopupTrigger()) {
                                JList list = (JList) e.getSource();
                                JPopupMenu popup = createPopupMenu();

                                popup.show(e.getComponent(), e.getX(), e.getY());
                                indexToRemove = list.locationToIndex(e.getPoint());
                                System.out.println("Rechtsklick");
                            }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        //super.mouseReleased(e); //To change body of generated methods, choose Tools | Templates.
                        System.out.println("mouseReleased()");
                        JPopupMenu popup = createPopupMenu();
                        JList list = (JList) e.getSource();

                        if (e.isPopupTrigger()) {
                            popup.show(e.getComponent(), e.getX(), e.getY());
                            indexToRemove = list.locationToIndex(e.getPoint());
                            System.out.println("Rechtsklick");
                        }
                    }

                });
        //layers.setComponentPopupMenu(createPopupMenu());

        theFrame.add(scrollPane);

        theFrame.setSize(
                200, 600);

        this.indexToRemove = -1;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Layer item = (Layer) this.layers.getModel().getElementAt(this.indexToRemove);
        Globe.getInstance().getModel().getLayers().remove(item);
        Observer.getInstance().getPCS().firePropertyChange(Events.LAYER_REMOVED, null, null);
        Observer.getInstance().getPCS().firePropertyChange(Events.LAYER_CHANGED, null, null);
    }

    public JPopupMenu createPopupMenu() {
        JMenuItem menuItem;

        //Create the popup menu.
        JPopupMenu popup = new JPopupMenu();
        menuItem = new JMenuItem("Remove");
        menuItem.addActionListener(this);
        popup.add(menuItem);
        //popup.addPopupMenuListener(new MyPopupMenuListener());
        return popup;
        //Add listener to the text area so the popup menu can come up.
        //MouseListener popupListener = new PopupListener(popup);
        //layers.addMouseListener(popupListener);
    }

    /**
     *
     */
    public void updateLayerListView() {
        layerModel.update();
    }

    /**
     *
     */
    public void updateModel() {
        layerModel.updateWorldWind();
    }

    public void setActiveLayerIndex(int activeLayerIndex) {
        this.layerModel.setActiveLayer(activeLayerIndex);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Events.LAYER_CHANGED)) {
            this.updateLayerListView();
            this.updateModel();
            this.layers.repaint();
        }
    }

    void show(boolean b) {
        theFrame.setVisible(b);
    }
}
