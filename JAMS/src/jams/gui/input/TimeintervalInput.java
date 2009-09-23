/*
 * TimeintervalInput.java
 * Created on 5. September 2006, 23:43
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
package jams.gui.input;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jams.data.JAMSCalendar;
import jams.data.JAMSTimeInterval;
import jams.gui.*;
import java.awt.Color;
import jams.JAMSConstants;
import jams.JAMSTools;
import jams.data.Attribute;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author S. Kralisch
 */
public class TimeintervalInput extends JPanel implements InputComponent {

    private JTextField tuCount;

    private JComboBox timeUnit;

    private Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();

    private Map<Integer, Integer> fieldMap = new HashMap<Integer, Integer>();

    private JPanel panel;

    private ValueChangeListener l;

    private CalendarInput startDate, endDate;

    private Color oldColor;

    public TimeintervalInput() {
        this(false);
    }

    public TimeintervalInput(boolean intervalEdit) {

        GridBagLayout gbl = new GridBagLayout();
        this.setBorder(BorderFactory.createEtchedBorder());

        this.setLayout(gbl);

//        GUIHelper.addGBComponent(this, gbl, new JLabel(JAMSConstants.resources.getString("Date_(YYYY/MM/DD)")), 1, 0, 1, 1, 0, 0);
//        GUIHelper.addGBComponent(this, gbl, new JLabel(JAMSConstants.resources.getString("Time_(HH:MM)")), 11, 0, 1, 1, 0, 0);
        GUIHelper.addGBComponent(this, gbl, new JLabel(JAMSConstants.resources.getString("Start:_")), 0, 1, 1, 1, 0, 0);
        GUIHelper.addGBComponent(this, gbl, new JLabel(JAMSConstants.resources.getString("End:_")), 0, 2, 1, 1, 0, 0);

        startDate = new CalendarInput(false);
        GUIHelper.addGBComponent(this, gbl, startDate.getDatePanel(), 1, 1, 1, 1, 0, 0);
        GUIHelper.addGBComponent(this, gbl, startDate.getTimePanel(), 11, 1, 1, 1, 0, 0);
        startDate.getTimeText().setToolTipText(JAMSConstants.resources.getString("Starttime"));
        startDate.getDateText().setToolTipText(JAMSConstants.resources.getString("Startdate"));

        endDate = new CalendarInput(false);
        GUIHelper.addGBComponent(this, gbl, endDate.getDatePanel(), 1, 2, 1, 1, 0, 0);
        GUIHelper.addGBComponent(this, gbl, endDate.getTimePanel(), 11, 2, 1, 1, 0, 0);
        endDate.getTimeText().setToolTipText(JAMSConstants.resources.getString("Endtime"));
        endDate.getDateText().setToolTipText(JAMSConstants.resources.getString("Enddate"));

        indexMap.put(JAMSCalendar.YEAR, 0);
        indexMap.put(JAMSCalendar.MONTH, 1);
        indexMap.put(JAMSCalendar.DAY_OF_YEAR, 2);
        indexMap.put(JAMSCalendar.HOUR_OF_DAY, 3);
        indexMap.put(JAMSCalendar.MINUTE, 4);
        indexMap.put(JAMSCalendar.SECOND, 5);

        fieldMap.put(0, JAMSCalendar.YEAR);
        fieldMap.put(1, JAMSCalendar.MONTH);
        fieldMap.put(2, JAMSCalendar.DAY_OF_YEAR);
        fieldMap.put(3, JAMSCalendar.HOUR_OF_DAY);
        fieldMap.put(4, JAMSCalendar.MINUTE);
        fieldMap.put(5, JAMSCalendar.SECOND);

        timeUnit = new JComboBox();
        timeUnit.addItem(JAMSConstants.resources.getString("YEAR"));
        timeUnit.addItem(JAMSConstants.resources.getString("MONTH"));
        timeUnit.addItem(JAMSConstants.resources.getString("DAY"));
        timeUnit.addItem(JAMSConstants.resources.getString("HOUR"));
        timeUnit.addItem(JAMSConstants.resources.getString("MINUTE"));
        timeUnit.addItem(JAMSConstants.resources.getString("SECOND"));
        timeUnit.setPreferredSize(new Dimension(40, 20));

        tuCount = new JTextField();
        tuCount.setInputVerifier(new NumericIntervalVerifier(0, 1000));
        tuCount.setPreferredSize(new Dimension(40, 20));

        if (intervalEdit) {
            GUIHelper.addGBComponent(this, gbl, new JLabel(JAMSConstants.resources.getString("Unit:_")), 0, 3, 1, 1, 0, 0);
            GUIHelper.addGBComponent(this, gbl, new JLabel(JAMSConstants.resources.getString("Unit_Count:_")), 0, 4, 1, 1, 0, 0);
            GUIHelper.addGBComponent(this, gbl, timeUnit, 1, 3, 1, 1, 0, 0);
            GUIHelper.addGBComponent(this, gbl, tuCount, 1, 4, 1, 1, 0, 0);
        }

//        JButton test = new JButton("Value");
//        test.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println(getValue());
//                System.out.println(verify());
//            }
//        });
//        GUIHelper.addGBComponent(this, gbl, test, 1, 6, 1, 1, 0, 0);

    }

    public String getValue() {
        Attribute.TimeInterval ti = new JAMSTimeInterval();
        ti.setStart(startDate.getCalendarValue());
        ti.setEnd(endDate.getCalendarValue());
        ti.setTimeUnit(fieldMap.get(timeUnit.getSelectedIndex()));
        ti.setTimeUnitCount(Integer.parseInt(tuCount.getText()));
        if (!ti.getStart().before(ti.getEnd())) {
            return null;
        } else {
            return ti.toString();
        }
    }

    public void setValue(String value) {
        //1996-11-01 7:30 2000-10-31 7:30 6 1

        JAMSTimeInterval ti = new JAMSTimeInterval();
        if (!JAMSTools.isEmptyString(value)) {
            ti.setValue(value);
        }

        Attribute.Calendar start = ti.getStart();
        startDate.setValue(start);

        Attribute.Calendar end = ti.getEnd();
        endDate.setValue(end);

        timeUnit.setSelectedIndex(indexMap.get(ti.getTimeUnit()));
        tuCount.setText(Integer.toString(ti.getTimeUnitCount()));
    }

    public JComponent getComponent() {
        return this;
    }

    class NumericIntervalVerifier extends InputVerifier {

        double lower, upper;

        public NumericIntervalVerifier(double lower, double upper) {
            this.lower = lower;
            this.upper = upper;
        }

        public boolean verify(JComponent input) {

            boolean result = false;
            double value;

            try {
                value = Double.parseDouble(((JTextField) input).getText());
                if ((value >= lower) && (value <= upper)) {
                    result = true;
                }
            } catch (NumberFormatException nfe) {
            }

            return (result);
        }
    }

    public void setRange(double lower, double upper) {
    }

    public void setEnabled(boolean enabled) {
        startDate.setEnabled(enabled);
        endDate.setEnabled(enabled);
        timeUnit.setEnabled(enabled);
        tuCount.setEnabled(enabled);
    }

    public boolean verify() {

        try {
            if (this.getValue() != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public int getErrorCode() {
        return INPUT_OK;
    }

    public void setLength(int length) {
    }

    public void addValueChangeListener(ValueChangeListener l) {
        this.l = l;
        this.startDate.addValueChangeListener(l);
        this.endDate.addValueChangeListener(l);
        this.timeUnit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TimeintervalInput.this.l.valueChanged();
            }
        });
        this.tuCount.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                TimeintervalInput.this.l.valueChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                TimeintervalInput.this.l.valueChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                TimeintervalInput.this.l.valueChanged();
            }
        });
    }

    public void setMarked(boolean marked) {
        if (marked == true) {
            oldColor = getBackground();
            setBackground(new Color(255, 0, 0));
        } else {
            setBackground(oldColor);
        }
    }

    public void setHelpText(String text) {
        text = "<html>" + text + "</html>";
        getComponent().setToolTipText(text);
    }

    public static void main(String[] args) {
        InputComponent tii = new TimeintervalInput(true);
        tii.setValue("1996-11-01 07:30 2000-10-31 07:30 6 1");

        tii.setEnabled(true);
        //tii.setMarked(true);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(tii.getComponent());
        frame.pack();
        frame.setVisible(true);
    }
}
