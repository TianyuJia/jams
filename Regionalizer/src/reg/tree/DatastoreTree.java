/*
 * DatastoreTree.java
 * Created on 19. November 2008, 17:58
 *
 * This file is part of JAMS
 * Copyright (C) 2008 FSU Jena
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 *
 */
package reg.tree;

import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import jams.model.JAMSComponent;
import jams.workspace.VirtualWorkspace;
import javax.swing.KeyStroke;

/**
 *
 * @author S. Kralisch
 */
public class DatastoreTree extends JAMSTree {

    private static final String ROOT_NAME = "Eingabe";
    private JPopupMenu popup;
    private String[] libsArray;
    private int contextCount,  componentCount;

    public DatastoreTree() {
        super();

        setEditable(false);
        new DefaultTreeTransferHandler(this, DnDConstants.ACTION_COPY);

        JMenuItem detailItem = new JMenuItem("Zeige Daten");
        detailItem.setAccelerator(KeyStroke.getKeyStroke('M'));
        detailItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                displayComponentDlg();
            }
        });
        popup = new JPopupMenu();
        popup.add(detailItem);

        addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON3) {
                    showPopup(evt);
                }
            }

            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() >= 2) {
                    displayComponentDlg();
                }
            }
        });
    }

    private void showPopup(MouseEvent evt) {
        TreePath p = this.getClosestPathForLocation(evt.getX(), evt.getY());
        this.setSelectionPath(p);
        JAMSNode node = (JAMSNode) this.getLastSelectedPathComponent();
        if (node != null) {
            popup.show(this, evt.getX(), evt.getY());
        }
    }

    private void displayComponentDlg() {



    }

    public void update(VirtualWorkspace workspace) {
/*
        libsArray = JAMSTools.toArray(libFileNames, ";");
        this.setModel(null);

        contextCount = 0;
        componentCount = 0;
        JUICE.setStatusText(JUICE.resources.getString("Loading_Libraries"));
        this.setVisible(false);
        JAMSNode root = LibTree.this.createLibTree(LibTree.this.libsArray);
        this.setModel(new DefaultTreeModel(root));
        this.collapseAll();
        this.setVisible(true);
        JUICE.setStatusText(JUICE.resources.getString("Contexts:") + contextCount + " " + JUICE.resources.getString("Components:") + componentCount);
*/

        System.out.println("Tree Rock'n'Roll");

    }

    private JAMSNode createLibTree(String[] libsArray) {

        JAMSNode root = new JAMSNode(ROOT_NAME, JAMSNode.LIBRARY_ROOT);
        JAMSNode jarNode;
/*
        for (int i = 0; i < libsArray.length; i++) {
            File file = new File(libsArray[i]);

            if (!file.exists()) {
                continue;
            }
            if (file.isDirectory()) {
                File[] f = file.listFiles();
                for (int j = 0; j < f.length; j++) {
                    if (f[j].getName().endsWith(".jar")) {
                        jarNode = createJARNode(f[j].toString(), JUICE.getLoader());
                        if (jarNode != null) {
                            root.add(jarNode);
                        }
                    }
                }
            } else {
                jarNode = createJARNode(file.toString(), JUICE.getLoader());
                if (jarNode != null) {
                    root.add(jarNode);
                }
            }

        }
*/

        return root;
    }

    private JAMSNode createJARNode(String jar, ClassLoader loader) {

        //System.out.println("loading " + jar);
        JAMSNode jarRoot = new JAMSNode(jar, JAMSNode.PACKAGE_NODE);
        ArrayList<Class> components = new ArrayList<Class>();
        JAMSNode compNode;
        String jarName = "", clazzName = "", clazzFullName = "";

        try {
            JarFile jfile = new JarFile(jar);
            File file = new File(jar);
            //URLClassLoader loader = new URLClassLoader(new URL[]{file.toURL()});
            jarName = file.getCanonicalFile().getName();
            //jarRoot = new JAMSNode(jarName, JAMSNode.PACKAGE_NODE);

            Enumeration jarentries = jfile.entries();
            while (jarentries.hasMoreElements()) {
                String entry = jarentries.nextElement().toString();
                if ((entry.endsWith(".class"))) {
                    String classString = entry.substring(0, entry.length() - 6);
                    classString = classString.replaceAll("/", ".");

                    try {

                        // try to load the class and check if it's a subclass of JAMSComponent
                        Class<?> clazz = loader.loadClass(classString);

                        if (JAMSComponent.class.isAssignableFrom(clazz)) {
                            components.add(clazz);
                        }

                    } catch (ClassNotFoundException cnfe) {


                    } catch (NoClassDefFoundError ncdfe) {
                        //LHelper.showErrorDlg(JUICE.getJuiceFrame(), "Missing class while loading component " + clazzFullName +
                        //        " in archive " + jarName + "!", "Error while loading archive");
                    } catch (Exception e) {
                        // other exception like e.g. java.lang.SecurityException
                        // won't be handled since they hopefully don't occur
                        // while loading JARs containing JAMS components
                    }
                }
            }

            String oldPackage = "", newPackage;
            JAMSNode packageNode = null;
            for (Class clazz : components) {
                newPackage = clazz.getPackage().getName();
                if (!newPackage.equals(oldPackage)) {
                    packageNode = new JAMSNode(newPackage, JAMSNode.PACKAGE_NODE);
                    jarRoot.add(packageNode);
                    oldPackage = newPackage;
                }

                clazzName = clazz.getSimpleName();
                clazzFullName = clazz.getName();

                if (!(clazzName.equals("JAMSComponent") || clazzName.equals("JAMSContext_") || clazzName.equals("JAMSGUIComponent") || clazzName.equals("JAMSModel"))) {
/*
                    try {

                        ComponentDescriptor no = new ComponentDescriptor(clazz, this);

                        if (JAMSContext.class.isAssignableFrom(clazz)) {
                            compNode = new JAMSNode(no, JAMSNode.CONTEXT_NODE);
                            contextCount++;
                        } else {
                            compNode = new JAMSNode(no, JAMSNode.COMPONENT_NODE);
                            componentCount++;
                        }

                        packageNode.add(compNode);

                    } catch (NoClassDefFoundError ncdfe) {

                        LHelper.showErrorDlg(JUICE.getJuiceFrame(), JUICE.resources.getString("Missing_class_while_loading_component_") + clazzFullName +
                                JUICE.resources.getString("_in_archive_") + jarName + "\"!", JUICE.resources.getString("Error_while_loading_archive"));

                    }*/
                }
            }


        } catch (IOException ioe) {

            //LHelper.showErrorDlg(JUICE.getJuiceFrame(), JUICE.resources.getString("File_") + jar + JUICE.resources.getString("_could_not_be_loaded."), JUICE.resources.getString("Error_while_loading_archive"));
            jarRoot = null;

        }

        if (components.size() > 0) {
            return jarRoot;
        } else {
            return null;
        }
    }
}
