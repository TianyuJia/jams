/*
 * TSDataStore.java
 * Created on 23. Januar 2008, 15:53
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
package jams.workspace.stores;

import jams.data.JAMSCalendar;
import jams.workspace.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import jams.workspace.datatypes.CalendarValue;
import jams.JAMS;
import jams.data.Attribute;
import jams.data.JAMSDataFactory;
import jams.runtime.JAMSRuntime;
import jams.workspace.datatypes.DoubleValue;
import jams.workspace.datatypes.LongValue;
import jams.workspace.datatypes.ObjectValue;
import jams.workspace.datatypes.StringValue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Sven Kralisch
 */
public class TSDataStore extends TableDataStore {

    protected CalendarValue calendar;

    protected Attribute.Calendar currentDate, startDate, endDate, stopDate;

    protected int timeUnit, timeUnitCount;

    protected String timeFormat;

    private BufferedReader dumpFileReader;

    private static final int DOUBLE = 0;

    private static final int LONG = 1;

    private static final int STRING = 2;

    private static final int OBJECT = 3;

    private int[] type;

    public TSDataStore(JAMSWorkspace ws) {
        super(ws);
        startDate = JAMSDataFactory.createCalendar();
        endDate = JAMSDataFactory.createCalendar();
        currentDate = JAMSDataFactory.createCalendar();
        calendar = new CalendarValue(currentDate);
    }

    public TSDataStore(JAMSWorkspace ws, String id, Document doc) throws IOException, ClassNotFoundException {
        super(ws, id, doc);

        Element tiNode = (Element) doc.getElementsByTagName("timeinterval").item(0);
        Element startElement = (Element) tiNode.getElementsByTagName("start").item(0);
        Element endElement = (Element) tiNode.getElementsByTagName("end").item(0);
        Element stepsizeElement = (Element) tiNode.getElementsByTagName("stepsize").item(0);
        Element timeFormatElement = (Element) tiNode.getElementsByTagName("dumptimeformat").item(0);

        timeFormat = JAMSCalendar.DATE_TIME_FORMAT;
        if (timeFormatElement != null) {
            timeFormat = timeFormatElement.getAttribute("value");
        }

        startDate = JAMSDataFactory.createCalendar();
        startDate.setValue(startElement.getAttribute("value"));

        timeUnit = Integer.parseInt(stepsizeElement.getAttribute("unit"));
        timeUnitCount = Integer.parseInt(stepsizeElement.getAttribute("count"));

        endDate = JAMSDataFactory.createCalendar();
        endDate.setValue(endElement.getAttribute("value"));
        stopDate = endDate.clone();
        stopDate.add(timeUnit, -1 * timeUnitCount);

        currentDate = JAMSDataFactory.createCalendar();
        currentDate.setDateFormat(timeFormat);
        currentDate.setValue(startDate);

        int oldBufferSize = bufferSize;
        if (bufferSize == 1) {
            bufferSize = 2;
        }

        if (this.accessMode != InputDataStore.USE_CACHE_MODE) {

            // check validity of the data, e.g. unique start dates

            fillBuffer();
            if (maxPosition >= 2) {

                // check interval size for all columns
                for (int i = 0; i < dataIOArray.length; i++) {

                    //get the timestamps of the first two rows
                    long timeStamp1 = dataIOArray[i].getData()[0].getData()[0].getLong();
                    long timeStamp2 = dataIOArray[i].getData()[1].getData()[0].getLong();

                    //compare the two time stamps
                    Attribute.Calendar cal1 = JAMSDataFactory.createCalendar();
                    cal1.setTimeInMillis(timeStamp1 * 1000);
                    JAMSCalendar cal2 = JAMSDataFactory.createCalendar();
                    cal2.setTimeInMillis(timeStamp2 * 1000);

                    cal1.add(timeUnit, timeUnitCount);
                    if (cal1.compareTo(cal2) != 0) {

                        Attribute.Calendar cal = cal1.clone();
                        cal.add(timeUnit, -1 * timeUnitCount);
                        long demandedSeconds = Math.abs(cal1.getTimeInMillis() - cal.getTimeInMillis()) / 1000;
                        long currentSeconds = Math.abs(cal.getTimeInMillis() - cal2.getTimeInMillis()) / 1000;

                        this.ws.getRuntime().sendErrorMsg(JAMS.resources.getString("Error_in_") + this.getClass().getName() + JAMS.resources.getString(":_wrong_time_interval_in_column_") + i + JAMS.resources.getString("_(demanded_interval_=_") + demandedSeconds + JAMS.resources.getString("_sec,_provided_interval_=_") + currentSeconds + JAMS.resources.getString("_sec)!"));

                        dataIOSet.clear();
                        currentPosition = maxPosition;
                    }

                }

                // check identical start date of all columns

                // for all but the first columns
                boolean shifted = false;
                for (int i = 0; i < dataIOArray.length; i++) {

                    long timeStamp2 = dataIOArray[i].getData()[0].getData()[0].getLong();

                    //compare the two time stamps
                    JAMSCalendar cal = JAMSDataFactory.createCalendar();
                    cal.setTimeInMillis(timeStamp2 * 1000);

                    if (cal.compareTo(startDate, timeUnit) != 0) {

                        this.ws.getRuntime().sendErrorMsg(JAMS.resources.getString("Error_in_") + this.getClass().getName() + JAMS.resources.getString(":_wrong_start_time_in_column_") + i + JAMS.resources.getString("_(demanded_=_") + startDate + JAMS.resources.getString(",_provided_=_") + cal + JAMS.resources.getString(")!"));

                        dataIOSet.clear();
                        currentPosition = maxPosition;
                    } else if (!shifted) {
                        if (cal.compareTo(currentDate) != 0) {
                            currentDate.setValue(cal);
                        }
                        shifted = true;
                    }
                }
            }

        } else {

            File file = new File(ws.getLocalDumpDirectory(), id + ".dump");
            if (!file.exists()) {
                throw new IOException("Dump file " + file.getPath() + " not found!");
            }

            this.dumpFileReader = new BufferedReader(new FileReader(file));

            this.dsd = getDSDFromDumpFile();

            if (ws.getRuntime().getState() != JAMSRuntime.STATE_RUN) {
                return;
            }
        }

        currentDate.add(timeUnit, -1 * timeUnitCount);
        calendar = new CalendarValue(currentDate);

        bufferSize = oldBufferSize;
    }

    private DefaultDataSetDefinition getDSDFromDumpFile() throws IOException {

        String str;
        while ((str = dumpFileReader.readLine()) != null) {
            if (str.startsWith("#$TYPE$")) {
                break;
            }
        }

        if (str == null) {
            return null;
        }

        StringTokenizer tok = new StringTokenizer(str, "\t");
        ArrayList<Class> dataTypes = new ArrayList<Class>();
        // drop the first token ("#$TYPE$")
        tok.nextToken();

        while (tok.hasMoreTokens()) {
            String className = tok.nextToken();
            try {
                dataTypes.add(Class.forName(className));
            } catch (ClassNotFoundException ex) {
                ws.getRuntime().sendErrorMsg("Referenced type in datastore " + id +
                        " could not be found: " + className);
            }
        }

        DefaultDataSetDefinition def = new DefaultDataSetDefinition(dataTypes);

        type = new int[dataTypes.size()];
        int i = 0;
        for (Class clazz : dataTypes) {
            if (clazz.equals(Long.class)) {
                type[i] = LONG;
            } else if (clazz.equals(Double.class)) {
                type[i] = DOUBLE;
            } else if (clazz.equals(String.class)) {
                type[i] = STRING;
            } else {
                type[i] = OBJECT;
            }
            i++;
        }

        while ((str = dumpFileReader.readLine()) != null) {
            if (str.startsWith(TSDumpProcessor.DATA_TAG)) {
                break;
            }

            tok = new StringTokenizer(str, "\t");

            String attributeName = tok.nextToken().substring(1);
            String className = tok.nextToken();
            ArrayList<Object> values = new ArrayList<Object>();
            Class clazz = null;

            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException ex) {
                ws.getRuntime().sendErrorMsg("Referenced type in datastore " + id +
                        " could not be found: " + className);
                return null;
            }
            def.addAttribute(attributeName, clazz);

            while (tok.hasMoreTokens()) {
                String valueString = tok.nextToken();
                values.add(getDataValue(clazz, valueString));
            }
            def.setAttributeValues(attributeName, values);
        }
        return def;
    }

    private Object getDataValue(Class clazz, String valueString) {

        Object o = null;

        if (clazz.equals(Double.class)) {
            o = new Double(valueString);
        } else if (clazz.equals(Long.class)) {
            o = new Long(valueString);
        } else if (clazz.equals(Attribute.Calendar.class)) {
            Attribute.Calendar cal = JAMSDataFactory.createCalendar();
            cal.setValue(valueString);
            o = cal;
        } else if (clazz.equals(String.class)) {
            o = new String(valueString);
        } else {
            o = new Object();
        }

        return o;
    }

    @Override
    public boolean hasNext() {

        if (currentDate.after(stopDate)) {
            return false;
        }

        if (this.accessMode != InputDataStore.USE_CACHE_MODE) {

            return super.hasNext();

        } else {

            return true;

        }
    }

    @Override
    public DefaultDataSet getNext() {

        if (!hasNext()) {
            return null;
        }

        currentDate.add(timeUnit, timeUnitCount);
        DefaultDataSet result;

        if (this.accessMode != InputDataStore.USE_CACHE_MODE) {

            result = new DefaultDataSet(positionArray.length + 1);
            result.setData(0, calendar);
            for (int i = 0; i < dataIOArray.length; i++) {

                DataSet ds = dataIOArray[i].getData()[currentPosition];
                DataValue[] values = ds.getData();
                result.setData(i + 1, values[positionArray[i]]);

            }

            currentPosition++;

        } else {

            try {

                String str = dumpFileReader.readLine();
                StringTokenizer tok = new StringTokenizer(str, "\t");

                int length = tok.countTokens();

                result = new DefaultDataSet(length);
                result.setData(0, calendar);

                // dump date since this is not evaluated!
                tok.nextToken();

                for (int i = 1; i < length; i++) {

                    DataValue value;
                    String valueString = tok.nextToken();
                    switch (type[i - 1]) {
                        case DOUBLE:
                            value = new DoubleValue(valueString);
                            break;
                        case LONG:
                            value = new LongValue(valueString);
                            break;
                        case STRING:
                            value = new StringValue(valueString);
                            break;
                        default:
                            value = new ObjectValue(valueString);
                    }

                    result.setData(i, value);
                }

            } catch (IOException ex) {
                ws.getRuntime().sendErrorMsg("Premature end of dump file for datastore" + id);
                return null;
            }
        }

        return result;
    }

    public Attribute.Calendar getStartDate() {
        return startDate;
    }

    public Attribute.Calendar getEndDate() {
        return endDate;
    }

    public int getTimeUnit() {
        return timeUnit;
    }

    public int getTimeUnitCount() {
        return timeUnitCount;
    }

    public String getTimeFormat() {
        return timeFormat;
    }
}
