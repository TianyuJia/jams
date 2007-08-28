/*
 * BooleanInput.java
 * Created on 7. September 2006, 10:26
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

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import org.unijena.jams.gui.*;

/**
 *
 * @author S. Kralisch
 */
public class BooleanInput extends JCheckBox implements InputComponent {
    
	private static final long serialVersionUID = -4821261861964727164L;

	public String getValue() {
        if (isSelected())
            return "1";
        else
            return "0";
    }
    
    public void setValue(String value) {
        if ("1".equals(value))
            this.setSelected(true);
        else
            this.setSelected(false);
    }
    
    public JComponent getComponent() {
        return this;
    }
    
    public void setRange(double lower, double upper){};
    
    public boolean verify() {
        return true;
    }
    
    public int getErrorCode() {
        return INPUT_OK;
    }    
}
