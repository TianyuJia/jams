/*
 * Context.java
 * Created on 22.03.2010, 15:21:51
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

import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class OutputDSDescriptor {

    private ContextDescriptor context;
    private String name;
    private ArrayList<ContextAttribute> contextAttributes = new ArrayList<ContextAttribute>();
    private ArrayList<FilterDescriptor> filters = new ArrayList<FilterDescriptor>();

    public OutputDSDescriptor(ContextDescriptor context) {
        this.context = context;
    }

    public Document createDocument() throws ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element dsElement = (Element) document.createElement("outputdatastore");
        dsElement.setAttribute("context", this.context.getName());
        dsElement.setAttribute("name", this.getName());
        dsElement.appendChild(document.createTextNode("\n"));

        for (FilterDescriptor f : filters) {
            Element filterElement = (Element) document.createElement("filter");
            filterElement.setAttribute("context", f.context.getName());
            filterElement.setAttribute("expression", f.expression);
            dsElement.appendChild(filterElement);
        }

        Element traceElement = (Element) document.createElement("trace");
        traceElement.appendChild(document.createTextNode("\n"));
        dsElement.appendChild(traceElement);

        for (ContextAttribute ca : contextAttributes) {
            Element caElement = (Element) document.createElement("attribute");
            caElement.setAttribute("id", ca.getName());
            traceElement.appendChild(caElement);
        }

        document.appendChild(dsElement);

        return document;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the contextAttributes
     */
    public ArrayList<ContextAttribute> getContextAttributes() {
        return contextAttributes;
    }

    /**
     * @return the filters
     */
    public ArrayList<FilterDescriptor> getFilters() {
        return filters;
    }

    /**
     * @return the context
     */
    public ContextDescriptor getContext() {
        return context;
    }

    @Override
    public String toString() {
        return name + " [" + context.getName() + "]";
    }

    public FilterDescriptor addFilter(ContextDescriptor context, String expression) {
        FilterDescriptor f = new FilterDescriptor();
        f.context = context;
        f.expression = expression;
        filters.add(f);
        return f;
    }

    public void removeFilter(FilterDescriptor f) {
        filters.remove(f);
    }

    public class FilterDescriptor {
        public String expression;
        public ContextDescriptor context;

        public String toString() {
            return expression + " [" + context.getName() + "]";
        }
    }
}
