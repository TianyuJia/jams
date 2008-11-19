/*
 * InputPanel.java
 * Created on 19. November 2008, 10:46
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

package reg;

import jams.workspace.VirtualWorkspace;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import reg.tree.DatastoreTree;
import reg.tree.JAMSTree;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class InputPanel extends JPanel {

    private DatastoreTree tree = new DatastoreTree();
    
    public InputPanel() {
        super();
        JScrollPane scrollPane = new JScrollPane(tree);
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void updateContent() {
        
    }

    public void update(VirtualWorkspace ws) {
        tree.update(ws);
    }
    
}
