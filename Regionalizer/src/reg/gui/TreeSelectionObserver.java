/*
 * TreeSelectionObserver.java
 * Created on 21. November 2008, 13:07
 *
 * This file is part of JAMS
 * Copyright (C) FSU Jena
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
package reg.gui;

import jams.workspace.stores.DataStore;
import java.util.Observable;
import java.util.Observer;
import reg.Regionalizer;
import reg.tree.DSTreeNode;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class TreeSelectionObserver implements Observer {

    public TreeSelectionObserver() {
        Regionalizer.getTree().addObserver(this);
    }

    public void update(Observable o, Object arg) {
        if (arg == null) {
            Regionalizer.getInfoPanel().updateDS(null);
//            TreeSelectionObserver.this.updateDS(null);
            return;
        }
        DSTreeNode node = (DSTreeNode) arg;
        if (node.getType() == DSTreeNode.INPUT_DS) {
            try {
                DataStore store = Regionalizer.getTree().getWorkspace().getInputDataStore(node.toString());
                Regionalizer.getInfoPanel().updateDS(store);
//                TreeSelectionObserver.this.updateDS(store);
            } catch (Exception e) {
                Regionalizer.getRuntime().sendErrorMsg(e.toString());
                e.printStackTrace();
            }
        } else if (node.getType() == DSTreeNode.OUTPUT_DS) {
//                    Regionalizer.getTree().getWorkspace().getO(node.toString());
        }

    }
}
