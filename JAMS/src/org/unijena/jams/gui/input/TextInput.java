/*
 * TextInput.java
 * Created on 29. August 2006, 15:15
 *
 * This file is part of JAMS
 * Copyright (C) 2005 FSU Jena
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

package org.unijena.jams.gui.input;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.unijena.jams.gui.*;

/**
 *
 * @author S. Kralisch
 */
public class TextInput extends JPanel implements InputComponent {
    
    private static final long serialVersionUID = 784799996931992935L;
    JTextField text = new JTextField();
    
    public TextInput() {
        super();
        setLayout(new BorderLayout());
        add(text, BorderLayout.WEST);
    }
    
    public String getValue() {
        return text.getText();
    }
    
    public void setValue(String value) {
        text.setText(value);
    }
    
    public JComponent getComponent() {
        return text;
    }
    
    public void setRange(double lower, double upper){};
    
    public boolean verify() {
        return true;
    }
    
    public int getErrorCode() {
        return INPUT_OK;
    }
    
}
