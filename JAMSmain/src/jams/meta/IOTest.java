/*
 * Context.java
 * Created on 20.09.2010, 12:03:58
 *
 * This file is part of JAMS
 * Copyright (C) FSU Jena
 *
 * JAMS is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * JAMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JAMS. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package jams.meta;

import jams.JAMSException;
import jams.JAMSProperties;
import jams.SystemProperties;
import jams.data.JAMSEntity;
import jams.runtime.JAMSClassLoader;
import jams.runtime.JAMSRuntime;
import jams.runtime.StandardRuntime;
import jams.tools.StringTools;
import jams.tools.XMLTools;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class IOTest {

    public static void main(String[] args) throws IOException, JAMSException {

        JAMSRuntime runtime = new StandardRuntime();
        SystemProperties properties = JAMSProperties.createProperties();
        properties.load("/home/nsk/jamsapplication/nsk.jap");
        String[] libs = StringTools.toArray(properties.getProperty("libs", ""), ";");
        ClassLoader classLoader = JAMSClassLoader.createClassLoader(libs, runtime);

        ModelIO io = new ModelIO(classLoader);

        Document doc = XMLTools.getDocument("/home/nsk/jamsapplication/JAMS-Gehlberg/j2k_gehlberg.jam");

        // get the model and access some meta data

        ModelDescriptor md = null;

        try {
            md = io.loadModel(doc, true);
        } catch (JAMSException jex) {
            System.out.println(jex);
        }
        System.out.println(md.getAuthor());
        System.out.println(md.getDescription());


        ArrayList<ComponentField> fields = md.getParameterFields();

        for (ComponentField field : fields) {
            System.out.println(field.getParent().getName() + "." + field.getName());
        }


        // rename a context attribute
        ComponentDescriptor cd = md.getComponentDescriptor("TmeanRegionaliser");
        ComponentField dataValueField = cd.getComponentFields().get("dataValue");
        ArrayList<ContextAttribute> tmean = dataValueField.getContextAttributes();
        ContextAttribute ca = tmean.get(0);
        ca.setName("mytmean");

        // output a node
        output(md.getRootNode(), 0);

        // get all attributes of a context (by type)
        ContextDescriptor context = (ContextDescriptor) md.getComponentDescriptor("HRULoop");
        HashMap<String, ContextAttribute> attribs = context.getDynamicAttributes(JAMSEntity.class);
        for (ContextAttribute attrib : attribs.values()) {
            System.out.println(attrib.getName() + " [" + attrib.getType() + "]");
        }

        ContextDescriptor ccopy = context.cloneNode();
        attribs = ccopy.getDynamicAttributes(JAMSEntity.class);
        for (ContextAttribute attrib : attribs.values()) {
            attrib.setName(attrib.getName() + "__");
            System.out.println(attrib.getName() + " [" + attrib.getType() + "]");
        }

        attribs = context.getDynamicAttributes(JAMSEntity.class);
        for (ContextAttribute attrib : attribs.values()) {
            System.out.println(attrib.getName() + " [" + attrib.getType() + "]");
        }

        Document doc2 = io.getModelDocument(md);

        XMLTools.writeXmlFile(doc, "/home/nsk/doc1.xml");
        XMLTools.writeXmlFile(doc2, "/home/nsk/doc2.xml");

        // output a component
//        cd = md.getComponentDescriptor("SpatialWeightedSumAggregator1");
//        output(cd, "");


    }

    static void output(ComponentDescriptor cd, String indent) {
        System.out.println(indent + cd.getName() + " [" + cd.getClazz() + "]");
        HashMap<String, ComponentField> fields = cd.getComponentFields();
        for (String fieldName : fields.keySet()) {
            ComponentField field = fields.get(fieldName);
            System.out.println(indent + "    " + fieldName + " [" + field.getContext() + "->" + field.getAttribute() + "] [" + field.getValue() + "]");
        }
    }

    static void output(JAMSNode node, int level) {
        String indent = "";
        for (int i = 0; i < level; i++) {
            indent += "    ";
        }
        output((ComponentDescriptor) node.getUserObject(), indent);
        for (int i = 0; i < node.getChildCount(); i++) {
            output(node.getChildAt(i), level + 1);
        }
    }
}
