/*
 * InputComponentFactory.java
 * Created on 23. September 2009, 16:12
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

package jams.ui.gui.input;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import jams.data.JAMSBoolean;
import jams.data.JAMSCalendar;
import jams.data.JAMSDataFactory;
import jams.data.JAMSDirName;
import jams.data.JAMSDouble;
import jams.data.JAMSFileName;
import jams.data.JAMSFloat;
import jams.data.JAMSInteger;
import jams.data.JAMSLong;
import jams.data.JAMSTimeInterval;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class InputComponentFactory {

    private static final int JCOMP_HEIGHT = 20;

    private static final int NUMBERINPUT_WIDTH = 100;

    private static final int TEXTINPUT_WIDTH = 250;

    private static final int FILEINPUT_WIDTH = 250;

    /**
     * Create swing input component based on data type
     * @param type Data type
     * @return InputComponent object that provides an editor for the type
     */
    public static InputComponent createInputComponent(Class type) {
        return createInputComponent(type, false);
    }

    /**
     * Create swing input component based on data type
     * @param type Data type
     * @param extEdit A flag defining if the editors will provide extended or
     * limited access to data (e.g. step size for timeintervals)
     * @return InputComponent object that provides an editor for the type
     */
    public static InputComponent createInputComponent(Class type, boolean extEdit) {
        InputComponent ic;

        if (type.isInterface()) {
            type = JAMSDataFactory.getImplementingClass(type);
        }

        if (JAMSFileName.class.isAssignableFrom(type)) {
            ic = new FileInput(false);
        } else if (JAMSDirName.class.isAssignableFrom(type)) {
            ic = new FileInput(true);
        } else if (JAMSCalendar.class.isAssignableFrom(type)) {
            ic = new CalendarInput();
        } else if (JAMSTimeInterval.class.isAssignableFrom(type)) {
            ic = new TimeintervalInput(extEdit);
        } else if (JAMSBoolean.class.isAssignableFrom(type)) {
            ic = new BooleanInput();
        } else if ((JAMSInteger.class.isAssignableFrom(type)) || (JAMSLong.class.isAssignableFrom(type))) {
            ic = new IntegerInput();
            ic.getComponent().setPreferredSize(new Dimension(NUMBERINPUT_WIDTH, JCOMP_HEIGHT));
            ic.getComponent().setBorder(BorderFactory.createEtchedBorder());
        } else if ((JAMSFloat.class.isAssignableFrom(type)) || (JAMSDouble.class.isAssignableFrom(type))) {
            ic = new FloatInput();
            ic.getComponent().setPreferredSize(new Dimension(NUMBERINPUT_WIDTH, JCOMP_HEIGHT));
            ic.getComponent().setBorder(BorderFactory.createEtchedBorder());
        } else {
            ic = new TextInput();
            ic.getComponent().setPreferredSize(new Dimension(TEXTINPUT_WIDTH, JCOMP_HEIGHT));
            ic.getComponent().setBorder(BorderFactory.createEtchedBorder());
        }

        //ic.getComponent().setBorder(BorderFactory.createEtchedBorder());
        return ic;
    }

}
