/*
 * StandardDataStore.java
 * Created on 4. Februar 2008, 23:21
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
package rbis.virtualws.stores;

import rbis.virtualws.*;
import rbis.virtualws.datatypes.DataValue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rbis.virtualws.plugins.DataIO;

/**
 *
 * @author Sven Kralisch
 */
public abstract class StandardDataStore implements DataStore {

    protected Document doc;
    protected HashMap<String, DataIO> dataIO;
    protected VirtualWorkspace ws;
    protected DataSetDefinition dsd;
    protected int bufferSize = 0;
    private String id,  description = "",  respParty;

    public StandardDataStore(VirtualWorkspace ws, Document doc) {
        this.doc = doc;
        this.ws = ws;

        this.id = doc.getDocumentElement().getAttribute("id");
        this.respParty = doc.getDocumentElement().getAttribute("respparty");

        Node descriptionNode = doc.getDocumentElement().getElementsByTagName("description").item(0);
        if (descriptionNode != null) {
            this.description = descriptionNode.getTextContent();
        }

        Element parameterElement = (Element) doc.getDocumentElement().getElementsByTagName("parameter").item(0);

        Element bufferSizeElement = (Element) parameterElement.getElementsByTagName("buffersize").item(0);
        if (bufferSizeElement != null) {
            this.bufferSize = Integer.parseInt(bufferSizeElement.getAttribute("value"));
        }

        this.dataIO = createDataIO();
        this.dsd = createDataSetDefinition();
    }

    private DataSetDefinition createDataSetDefinition() {

        ArrayList<Class> dataTypes = new ArrayList<Class>();

        Element metadataElement = (Element) doc.getElementsByTagName("metadata").item(0);

        NodeList columnList = metadataElement.getElementsByTagName("column");
        for (int i = 0; i < columnList.getLength(); i++) {
            Element columnElement = (Element) columnList.item(i);
            try {
                Class type = Class.forName(columnElement.getAttribute("type"));
                dataTypes.add(type);
            } catch (ClassNotFoundException cnfe) {
                ws.getRuntime().handle(cnfe);
            }
        }

        DataSetDefinition def = new DataSetDefinition(dataTypes);

        NodeList rowList = metadataElement.getElementsByTagName("row");
        for (int i = 0; i < rowList.getLength(); i++) {
            Element rowElement = (Element) rowList.item(i);
            try {
                Class type = Class.forName(rowElement.getAttribute("type"));
                def.addAttribute(rowElement.getAttribute("id"), type);
            } catch (ClassNotFoundException cnfe) {
                ws.getRuntime().handle(cnfe);
            }
        }

        for (int i = 0; i < columnList.getLength(); i++) {
            Element columnElement = (Element) columnList.item(i);
            DataIO metadataIO = dataIO.get(columnElement.getAttribute("metadataio"));

            int result = metadataIO.init();
            if (result < 0) {
                ws.getRuntime().sendHalt("Initialization of data I/O component (" + this.getClass().getName() + ") failed..");
                return null;
            }
        }

        for (int i = 0; i < columnList.getLength(); i++) {
            Element columnElement = (Element) columnList.item(i);
            DataIO metadataIO = dataIO.get(columnElement.getAttribute("metadataio"));

            metadataIO.fetchValues(1);
            DataSet metadataSet = metadataIO.getValues()[0];

            ArrayList<Object> values = new ArrayList<Object>();
            for (DataValue value : metadataSet.getData()) {
                values.add(value.getObject());
            }
            def.setAttributeValues(i, values);

        }

        for (int i = 0; i < columnList.getLength(); i++) {
            Element columnElement = (Element) columnList.item(i);
            DataIO metadataIO = dataIO.get(columnElement.getAttribute("metadataio"));

            metadataIO.cleanup();
        }

        return def;
    }

    private HashMap<String, DataIO> createDataIO() {

        HashMap<String, DataIO> _dataIO = new HashMap<String, DataIO>();

        Element ioElement = (Element) doc.getElementsByTagName("dataio").item(0);

        if (ioElement == null) {
            return null;
        }

        HashMap<String, String> varMap = new HashMap<String, String>();
        Element variableElement = (Element) ioElement.getElementsByTagName("variables").item(0);
        NodeList varNodes = variableElement.getElementsByTagName("var");
        for (int n = 0; n < varNodes.getLength(); n++) {
            Element varNode = (Element) varNodes.item(n);
            varMap.put(varNode.getAttribute("id"), varNode.getAttribute("value"));
        }

        NodeList ioNodes = ioElement.getElementsByTagName("plugin");
        for (int n = 0; n < ioNodes.getLength(); n++) {

            Element ioNode = (Element) ioNodes.item(n);
            String className = ioNode.getAttribute("type");
            String id = ioNode.getAttribute("id");

            ClassLoader loader = ws.getClassLoader();

            try {

                Class<?> clazz = loader.loadClass(className);
                DataIO io = (DataIO) clazz.newInstance();

                NodeList parameterNodes = ioNode.getElementsByTagName("parameter");
                for (int i = 0; i < parameterNodes.getLength(); i++) {

                    Element parameterNode = (Element) parameterNodes.item(i);

                    String attributeName = parameterNode.getAttribute("id");
                    String attributeValue = "";
                    if (parameterNode.hasAttribute("value")) {
                        attributeValue = parameterNode.getAttribute("value");
                    } else {
                        String varID = parameterNode.getAttribute("varid");
                        attributeValue = varMap.get(varID);
                    }
                    String methodName = "set" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);

                    Method method = clazz.getMethod(methodName, String.class);

                    method.invoke(io, attributeValue);

                }

                _dataIO.put(id, io);

            } catch (ClassNotFoundException cnfe) {
                ws.getRuntime().handle(cnfe);
            } catch (InstantiationException ie) {
                ws.getRuntime().handle(ie);
            } catch (IllegalAccessException iae) {
                ws.getRuntime().handle(iae);
            } catch (NoSuchMethodException nsme) {
                ws.getRuntime().handle(nsme);
            } catch (InvocationTargetException ite) {
                ws.getRuntime().handle(ite);
            }
        }
        return _dataIO;
    }

    public String getID() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public DataSetDefinition getDataSetDefinition() {
        return this.dsd;
    }

    public DataIO getDataIO(String id) {
        return dataIO.get(id);
    }

    public String getRespParty() {
        return respParty;
    }
}
