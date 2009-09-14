/*
 * CalendarInput.java
 * Created on 2. October 2007, 15:10
 *
 * This file is part of JAMS
 * Copyright (C) 2007 FSU Jena
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
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jams.data.JAMSCalendar;
import jams.gui.*;
import java.awt.Color;
import jams.JAMS;
import jams.data.JAMSDataFactory;

/**
 *
 * @author S. Kralisch
 */
public class CalendarInput extends JPanel implements InputComponent {

    private JTextField syear,  smonth,  sday,  shour,  sminute;
    private Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> fieldMap = new HashMap<Integer, Integer>();
    private JPanel panel;
    private ValueChangeListener l;

    /** Creates a new instance of TimeintervalInput */
    public CalendarInput() {

        GridBagLayout gbl = new GridBagLayout();
        this.setBorder(BorderFactory.createEtchedBorder());

        this.setLayout(gbl);

        GUIHelper.addGBComponent(this, gbl, new JLabel(JAMS.resources.getString("Date_(YYYY/MM/DD)")), 1, 0, 1, 1, 0, 0);
        GUIHelper.addGBComponent(this, gbl, new JLabel(JAMS.resources.getString("Time_(HH:MM)")), 11, 0, 1, 1, 0, 0);

        syear = new JTextField();
        syear.setInputVerifier(new NumericIntervalVerifier(1900, 2100));
        syear.setPreferredSize(new Dimension(40, 20));

        smonth = new JTextField();
        smonth.setInputVerifier(new NumericIntervalVerifier(1, 12));
        smonth.setPreferredSize(new Dimension(25, 20));

        sday = new JTextField();
        sday.setInputVerifier(new NumericIntervalVerifier(1, 31));
        sday.setPreferredSize(new Dimension(25, 20));

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.add(syear);
        panel.add(new JLabel("/"));
        panel.add(smonth);
        panel.add(new JLabel("/"));
        panel.add(sday);
        GUIHelper.addGBComponent(this, gbl, panel, 1, 1, 10, 1, 0, 0);

        shour = new JTextField();
        shour.setInputVerifier(new NumericIntervalVerifier(0, 23));
        shour.setPreferredSize(new Dimension(25, 20));

        sminute = new JTextField();
        sminute.setInputVerifier(new NumericIntervalVerifier(0, 59));
        sminute.setPreferredSize(new Dimension(25, 20));

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.add(shour);
        panel.add(new JLabel(":"));
        panel.add(sminute);
        GUIHelper.addGBComponent(this, gbl, panel, 11, 1, 1, 1, 0, 0);

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

        sday.setBorder(BorderFactory.createEtchedBorder());
        smonth.setBorder(BorderFactory.createEtchedBorder());
        syear.setBorder(BorderFactory.createEtchedBorder());
        shour.setBorder(BorderFactory.createEtchedBorder());
        sminute.setBorder(BorderFactory.createEtchedBorder());

    }

    public String getValue() {
        try {
            JAMSCalendar cal = JAMSDataFactory.createCalendar();
            cal.set(
                    Integer.parseInt(syear.getText()),
                    Integer.parseInt(smonth.getText()) - 1,
                    Integer.parseInt(sday.getText()),
                    Integer.parseInt(shour.getText()),
                    Integer.parseInt(sminute.getText()),
                    0);
            return cal.toString();
        } catch (NumberFormatException nfe) {
            return "";
        }
    }

    public void setValue(String value) {

        JAMSCalendar cal = JAMSDataFactory.createCalendar();

        if ((value != null) && !value.equals("")) {
            cal.setValue(value);
        }

        syear.setText(Integer.toString(cal.get(JAMSCalendar.YEAR)));
        smonth.setText(Integer.toString(cal.get(JAMSCalendar.MONTH) + 1));
        sday.setText(Integer.toString(cal.get(JAMSCalendar.DAY_OF_MONTH)));
        shour.setText(Integer.toString(cal.get(JAMSCalendar.HOUR_OF_DAY)));
        sminute.setText(Integer.toString(cal.get(JAMSCalendar.MINUTE)));

    }

    public JComponent getComponent() {
        return this;
    }


    public void setEnabled(boolean enabled) {
        syear.setEnabled(enabled);
        smonth.setEnabled(enabled);
        sday.setEnabled(enabled);
        shour.setEnabled(enabled);
        sminute.setEnabled(enabled);
    }

    public void setRange(double lower, double upper) {
    }

    public boolean verify() {
        try {
            this.getValue();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public int getErrorCode() {
        return INPUT_OK;
    }

    public void setLength(int length) {
    }

    public void addValueChangeListener(ValueChangeListener l) {
        this.l = l;
        this.syear.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }
        });
        this.smonth.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }
        });
        this.sday.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }
        });
        this.shour.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }
        });
        this.sminute.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                CalendarInput.this.l.valueChanged();
            }
        });
    }
    
    private Color oldColor;
    public void setMarked(boolean marked) {
        if (marked == true) {
            oldColor = getBackground();
            setBackground(new Color(255, 0, 0));
        } else {
            setBackground(oldColor);
        }
    }
}
