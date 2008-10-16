/*
 * J2KTSDataStore.java
 * Created on 13. Oktober 2008, 17:22
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

import jams.JAMS;
import jams.data.JAMSCalendar;
import jams.workspace.DataSet;
import jams.workspace.DataSetDefinition;
import jams.workspace.TSDumpProcessor;
import jams.workspace.VirtualWorkspace;
import jams.workspace.datatypes.DoubleValue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class J2KTSDataStore extends TSDataStore {

    private String cache = null;
    private int columnCount = 0;
    private BufferedReader dumpReader;

    public J2KTSDataStore(VirtualWorkspace ws, String id, Document doc) throws IOException {

        super(ws);
        this.id = id;

        Element sourceElement = (Element) doc.getElementsByTagName("source").item(0);
        Element tiNode = (Element) doc.getElementsByTagName("timeinterval").item(0);
        Element timeFormatElement = (Element) tiNode.getElementsByTagName("dumptimeformat").item(0);
        timeFormat = JAMSCalendar.DATE_TIME_FORMAT;
        if (timeFormatElement != null) {
            timeFormat = timeFormatElement.getAttribute("value");
        }
        
        // set sourceFile to the default
        File sourceFile = new File(ws.getLocalInputDirectory(), id + ".dat");
        if (sourceElement != null) {
            String sourceFileName = sourceElement.getAttribute("value");
            if (sourceFileName != null) {
                sourceFile = new File(sourceFileName);
            }
        }

        this.dumpReader = new BufferedReader(new FileReader(sourceFile));

        readJ2KFile();

        this.columnCount = this.getDataSetDefinition().getColumnCount();

        if (ws.getRuntime().getRunState() != JAMS.RUNSTATE_RUN) {
            return;
        }
    }

    private void readJ2KFile() throws IOException {

        // read header information from the J2K time series file

        String line = dumpReader.readLine();
        //skip comment lines
        while (line.charAt(0) == '#') {
            this.description += line.substring(1) + "\n";
            line = dumpReader.readLine();
        }

        StringBuffer dataValueAttribs = new StringBuffer();
        while (!line.startsWith("@dataSetAttribs")) {
            dataValueAttribs.append(line + "\n");
            line = dumpReader.readLine();
        }

        StringBuffer dataSetAttribs = new StringBuffer();
        while (!line.startsWith("@statAttribVal")) {
            dataSetAttribs.append(line + "\n");
            line = dumpReader.readLine();
        }

        StringBuffer statAttribVal = new StringBuffer();
        while (!line.startsWith("@dataVal")) {
            statAttribVal.append(line + "\n");
            line = dumpReader.readLine();
        }

        // create a DataSetDefinition object

        StringTokenizer tok1 = new StringTokenizer(statAttribVal.toString(), "\n");
        tok1.nextToken();
        StringTokenizer tok2 = new StringTokenizer(tok1.nextToken());

        int attributeCount = tok2.countTokens() - 1;
        ArrayList<Class> dataTypes = new ArrayList<Class>();
        for (int i = 0; i < attributeCount; i++) {
            dataTypes.add(Double.class);
        }
        DataSetDefinition def = new DataSetDefinition(dataTypes);

        while (tok1.hasMoreTokens()) {

            String attributeName = tok2.nextToken();
            def.addAttribute(attributeName, String.class);
            ArrayList<Object> values = new ArrayList<Object>();
            while (tok2.hasMoreTokens()) {
                values.add(tok2.nextToken());
            }
            def.setAttributeValues(attributeName, values);
            tok2 = new StringTokenizer(tok1.nextToken());
        }

        // process dataValueAttribs
        tok1 = new StringTokenizer(dataValueAttribs.toString());
        tok1.nextToken(); // skip the "@"-tag
        String parameterName = tok1.nextToken();
        String parameterString = "PARAMETER";
        def.addAttribute(parameterString, String.class);
        def.setAttributeValues(parameterString, parameterName);

        // process dataSetAttribs
        tok1 = new StringTokenizer(dataSetAttribs.toString(), "\n");
        tok1.nextToken(); // skip the "@"-tag

        while (tok1.hasMoreTokens()) {
            tok2 = new StringTokenizer(tok1.nextToken());
            String key = tok2.nextToken();
            if (key.equals("missingDataVal")) {
                missingDataValue = tok2.nextToken();
            } else if (key.equals("dataStart")) {
                String startString = tok2.nextToken() + " " + tok2.nextToken();
                try {
                    startDate.setValue(startString, "dd.MM.yyyy HH:mm");
                } catch (ParseException pe) {
                    ws.getRuntime().sendErrorMsg("Could not parse date \"" + startString + "\" - date kept unchanged!");
                }
            } else if (key.equals("dataEnd")) {
                String endString = tok2.nextToken() + " " + tok2.nextToken();
                try {
                    endDate.setValue(endString, "dd.MM.yyyy HH:mm");
                } catch (ParseException pe) {
                    ws.getRuntime().sendErrorMsg("Could not parse date \"" + endString + "\" - date kept unchanged!");
                }
            } else if (key.equals("tres")) {
                String tres = tok2.nextToken();
                if (tres.equals("d")) {
                    timeUnit = JAMSCalendar.DAY_OF_YEAR;
                } else if (tres.equals("m")) {
                    timeUnit = JAMSCalendar.MONTH;
                } else if (tres.equals("h")) {
                    timeUnit = JAMSCalendar.HOUR_OF_DAY;
                }
            }
        }
        timeUnitCount = 1;
        currentDate.setValue(startDate);
        currentDate.setDateFormat(timeFormat);


        this.dsd = def;

    }

    @Override
    public boolean hasNext() {
        if (cache != null) {
            return true;
        } else {
            try {
                cache = dumpReader.readLine();
                return ((cache != null) && (!cache.startsWith("#")));
            } catch (IOException ioe) {
                return false;
            }
        }
    }

    @Override
    public DataSet getNext() {

        if (!hasNext()) {
            return null;
        }

        DataSet result = new DataSet(columnCount + 1);
        StringTokenizer tok = new StringTokenizer(cache, "\t");

        String dateTimeString = tok.nextToken() + " " + tok.nextToken();
        try {
            currentDate.setValue(dateTimeString, "dd.MM.yyyy HH:mm");
        } catch (ParseException pe) {
            ws.getRuntime().sendErrorMsg("Could not parse date \"" + dateTimeString + "\" - date kept unchanged!");
        }
        result.setData(0, calendar);

        int i = 1;
        while (tok.hasMoreTokens()) {
            double d = Double.parseDouble(tok.nextToken());
            DoubleValue dValue = new DoubleValue(d);
            result.setData(i, dValue);
            i++;
        }

        cache = null;
        return result;
    }

    @Override
    public void close() {
        try {
            dumpReader.close();
        } catch (IOException ioe) {
            ws.getRuntime().handle(ioe);
        }
    }
}
