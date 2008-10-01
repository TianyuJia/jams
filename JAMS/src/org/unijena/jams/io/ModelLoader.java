/*
 * ModelLoader.java
 * Created on 26. September 2005, 16:55
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
 * GNU General Publiccc License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 *
 */
package jams.io;

import java.util.*;
import jams.dataaccess.DataAccessor;
import jams.model.*;
import org.w3c.dom.*;
import java.lang.reflect.*;
import jams.JAMS;
import jams.JAMSProperties;
import jams.data.*;
import jams.runtime.JAMSRuntime;

/**
 *
 * @author S. Kralisch
 */
public class ModelLoader {
    
    private HashMap<String, JAMSComponent> componentRepository = new HashMap<String, JAMSComponent>();
    private HashMap<String, String> constants = new HashMap<String, String>();
    private ClassLoader loader;
    private JAMSModel jamsModel;
    private JAMSProperties properties;

    public ModelLoader(Document modelDoc, String[] globvars, JAMSRuntime rt, JAMSProperties properties) {

        this.loader = rt.getClassLoader();
        this.properties = properties;

        if (globvars != null) {
            for (int i = 0; i < globvars.length; i++) {
                StringTokenizer tok = new StringTokenizer(globvars[i], "=");
                if (tok.countTokens() == 2) {
                    constants.put(tok.nextToken(), tok.nextToken());
                }
            }
        }

        jamsModel = new JAMSModel(rt);
        jamsModel.setModel(jamsModel);

        this.loadModel(modelDoc);
    }

    private void loadModel(Document modelDoc) {

        Element root, element;
        Node node;
        JAMSComponent topComponent;

        root = modelDoc.getDocumentElement();

        jamsModel.setName(root.getAttribute("name"));
        jamsModel.setInstanceName(root.getAttribute("name"));
        jamsModel.setAuthor(root.getAttribute("author"));
        jamsModel.setDate(root.getAttribute("date"));
        jamsModel.setWorkspaceDir(root.getAttribute("workspace"));
        
        jamsModel.getRuntime().println("*************************************", JAMS.STANDARD);
        jamsModel.getRuntime().println("model     : " + jamsModel.getName(), JAMS.STANDARD);
        jamsModel.getRuntime().println("workspace : " + jamsModel.getWorkspaceDirectory(), JAMS.STANDARD);
        jamsModel.getRuntime().println("author    : " + jamsModel.getAuthor(), JAMS.STANDARD);
        jamsModel.getRuntime().println("date      : " + jamsModel.getDate(), JAMS.STANDARD);
        jamsModel.getRuntime().println("*************************************", JAMS.STANDARD);

        NodeList childs = root.getChildNodes();

        // first check all childs for globvar nodes!
        for (int index = 0; index < childs.getLength(); index++) {
            node = childs.item(index);
            if (node.getNodeName().equals("globvar")) {
                element = (Element) node;
                if (!constants.containsKey(element.getAttribute("name"))) {
                    constants.put(element.getAttribute("name"), element.getAttribute("value"));
                }
            }

            if (node.getNodeName().equals("attribute")) {
                element = (Element) node;
                jamsModel.addAttribute(element.getAttribute("name"), element.getAttribute("class"), element.getAttribute("value"));
            }

        }

        // create the model
        ArrayList<JAMSComponent> childComponentList = new ArrayList<JAMSComponent>();
        for (int index = 0; index < childs.getLength(); index++) {
            node = childs.item(index);
            if (node.getNodeName().equals("contextcomponent") || node.getNodeName().equals("component")) {
                element = (Element) node; //3.

                try {

                    topComponent = loadComponent(element, jamsModel);
                    jamsModel.addComponent(topComponent);
                    childComponentList.add(topComponent);

                } catch (ModelSpecificationException iae) {

                    jamsModel.getRuntime().handle(iae, false);

                }
            }
        }
        jamsModel.setComponents(childComponentList);
    }

    /**
     * Recursively create all components used in the model and add them to the component repository for easy access
     */
    private JAMSComponent loadComponent(Element root, JAMSComponent rootComponent) throws ModelSpecificationException {

        String componentName,componentClassName ,varName ,varClassName  = "",varValue ;
        JAMSComponent component,childComponent ;
        JAMSData variable;
        Class componentClazz = null,varClazz  = null;
        ArrayList<JAMSComponent> childComponentList = new ArrayList<JAMSComponent>();

        componentName = root.getAttribute("name");
        componentClassName = root.getAttribute("class");
        jamsModel.getRuntime().println("Adding: " + componentName + " (" + componentClassName + ")", JAMS.STANDARD);

        component = null;
        try {

            // create the JAMSComponent object
            jamsModel.getRuntime().println(componentClassName, JAMS.VERBOSE);
            componentClazz = loader.loadClass(componentClassName);
            //componentClazz = Class.forName(componentClassName, true, loader);

            component = (JAMSComponent) componentClazz.newInstance();
            component.setModel(jamsModel);
            component.setInstanceName(componentName);

            if (component instanceof JAMSGUIComponent) {
                JAMSGUIComponent guiComponent = (JAMSGUIComponent) component;
                jamsModel.getRuntime().addGUIComponent(guiComponent);
            }

            // create Objects for component fields and set units and ranges
            createNumericMembers(component);
            //createMembers(component);

        } catch (ClassNotFoundException cnfe) {
            jamsModel.getRuntime().handle(cnfe, false);
            return null;
        } catch (InstantiationException ie) {
            jamsModel.getRuntime().handle(ie, false);
        } catch (IllegalAccessException iae) {
            jamsModel.getRuntime().handle(iae, false);
        }


        // put the JAMSComponent object into the component repository
        this.componentRepository.put(componentName, component);

        // get element child nodes
        NodeList childs = root.getChildNodes();

        for (int index = 0; index < childs.getLength(); index++) {

            Node node = childs.item(index);//6

            if (node.getNodeName().equals("contextcomponent") || node.getNodeName().equals("component")) {

                // process child components of context components
                childComponent = loadComponent((Element) node, component);
                if (childComponent != null) {
                    childComponentList.add(childComponent);
                }

                /*
                if (childComponent instanceof JAMSGUIComponent) {
                JAMSGUIComponent guiComponent = (JAMSGUIComponent) childComponent;
                jamsModel.getRuntime().addGUIComponent(guiComponent);
                }
                 */

            } else if (node.getNodeName().equals("var")) {

                // process components variable declarations
                Element element = (Element) node;

                varName = element.getAttribute("name");
                // varClassName = element.getAttribute("class");

                // check if component variable exists
                try {
                    Field field = componentClazz.getField(varName);
                    varClassName = field.getType().getName();

                    if (field.isAnnotationPresent(JAMSVarDescription.class)) {

                        JAMSVarDescription jvd = field.getAnnotation(JAMSVarDescription.class);

                        jamsModel.getRuntime().println("     " + componentName + " var declaration: " + varName + " (" + varClassName + ", " + jvd.access() + ")", JAMS.VERBOSE);

                        /*
                        if ((jvd.trace() == JAMSVarDescription.UpdateType.INIT) && ((jvd.access() != JAMSVarDescription.AccessType.READ) || element.hasAttribute("attribute"))) {
                        throw new ModelSpecificationException("Component " + componentName + ": Variable " + varName + " can only be set using \"value\" or \"globvar\"!");
                        }
                         */

                        // set the var object if value provided directly
                        if (element.hasAttribute("value")) {

                            // create the var object
                            varClazz = loader.loadClass(varClassName);

                            variable = (JAMSData) varClazz.newInstance();
                            // variable = getInstance(varClazz);

                            varValue = element.getAttribute("value");
                            variable.setValue(varValue);

                            // attach the variable to the component's field..
                            field.set(component, variable);

                            JAMSData data = (JAMSData) field.get(component);
                            String id = componentName + "." + varName;
                            jamsModel.getRuntime().getDataHandles().put(id, data);

                        }

                        // set the var object if value provided via globvar
                        // attribute
                        /*
                        if (element.hasAttribute("globvar")) {
                        // create the var object
                        varClazz = loader.loadClass(varClassName);
                        variable = (JAMSData) varClazz.newInstance();
                        // variable = getInstance(varClazz);
                        varValue = element.getAttribute("globvar");
                        variable.setValue(constants.get(varValue));
                        // attach the variable to the component's field..
                        field.set(component, variable);
                        }
                         */

                        if (element.hasAttribute("attribute")) {

                            // obtain providing context
                            JAMSComponent context = this.componentRepository.get(element.getAttribute("context"));

                            if (context == null) {
                                // throw new ModelSpecificationException("Component " + componentName + ": Component \"" + element.getAttribute("context") + "\" not available!");
                                context = jamsModel;
                            }

                            // check if providing context supplies specified variable
                            // ...

                            if (!(context instanceof JAMSContext)) {
                                throw new ModelSpecificationException("Component " + componentName + ": Component \"" + element.getAttribute("context") + "\" must be of type JAMSSpatialContext!");
                            }

                            JAMSContext sc = (JAMSContext) context;
                            String attributeName;

                            attributeName = element.getAttribute("attribute");

                            if (jvd.access() == JAMSVarDescription.AccessType.READ) {
                                sc.addAccess(component, varName, attributeName, DataAccessor.READ_ACCESS);
                            } else if (jvd.access() == JAMSVarDescription.AccessType.WRITE) {
                                sc.addAccess(component, varName, attributeName, DataAccessor.WRITE_ACCESS);
                            } else if (jvd.access() == JAMSVarDescription.AccessType.READWRITE) {
                                sc.addAccess(component, varName, attributeName, DataAccessor.READWRITE_ACCESS);
                            }

                        }

                        /*
                        if (jvd.trace() == JAMSVarDescription.UpdateType.INIT) {
                        JAMSData data = (JAMSData) field.get(component);
                        String id = componentName + "." + varName;
                        jamsModel.getRuntime().getDataHandles().put(id, data);
                        }
                         */
                    } else {
                        throw new ModelSpecificationException("Component " + componentName + ": variable " + varName + " can not be accessed (missing annotation)!");
                    }

                } catch (NoSuchFieldException nsfe) {
                    throw new ModelSpecificationException("Component " + componentName + ": variable " + varName + " not found!");
                } catch (ClassNotFoundException cnfe) {
                    throw new ModelSpecificationException("Component " + componentName + ": variable class " + varClassName + " not found!");
                } catch (IllegalArgumentException iae) {
                    throw new ModelSpecificationException("Component " + componentName + ": variable " + varName + ": wrong type!");
                } catch (InstantiationException ie) {
                    throw new ModelSpecificationException("Component " + componentName + ": variable " + varName + ": Instantiation exception!");
                } catch (IllegalAccessException iae) {
                    throw new ModelSpecificationException("Component " + componentName + ": variable " + varName + ": Access exception!");
                } catch (Exception ex) {
                    jamsModel.getRuntime().handle(ex);
                }

                // JAMS var declaration

            } else if (node.getNodeName().equals("attribute")) {

                if (!JAMSContext.class.isAssignableFrom(component.getClass())) {
                    throw new ModelSpecificationException("Attribute tag can only be used inside context components! (component " + componentName + ")");
                }

                Element element = (Element) node;
                ((JAMSContext) component).addAttribute(element.getAttribute("name"), element.getAttribute("class"), element.getAttribute("value"));

            } /*else if (node.getNodeName().equals("trace")) {
            
            if (!JAMSContext.class.isAssignableFrom(component.getClass())) {
            throw new ModelSpecificationException("Trace tag can only be used inside context components! (component " + componentName + ")");
            }
            
            Element element = (Element) node;
            ((JAMSContext) component).getDataTracer().registerAttribute(element.getAttribute("attribute"));
            jamsModel.getRuntime().println("Registering trace for " + component.getInstanceName() + "->" + element.getAttribute("attribute"), JAMS.STANDARD);
            
            }*/
        }
        if (component instanceof JAMSContext) {
            ((JAMSContext) component).setComponents(childComponentList);
        }

        return component;
    }

    class ModelSpecificationException extends Exception {

        public ModelSpecificationException(String errorMsg) {
            super(errorMsg);
        }
    }

    private void createMembers(JAMSComponent component) throws IllegalAccessException, InstantiationException {

        Object o;
        Class dataType;

        Field[] fields = component.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            o = fields[i].get(component);
            dataType = fields[i].getType();

            if (!dataType.isInterface() && JAMSData.class.isAssignableFrom(dataType) && fields[i].isAnnotationPresent(JAMSVarDescription.class)) {

                JAMSData dataObject = (JAMSData) o;

                // get variable object or create one if not existing
                if (dataObject == null) {
                    System.out.println(fields[i].getName());
                    dataObject = (JAMSData) dataType.newInstance();
                    fields[i].set(component, dataObject);
                }
            }
        }
    }

    private void createNumericMembers(JAMSComponent component) throws IllegalAccessException, InstantiationException, ModelSpecificationException {

        Class cClass = component.getClass();
        Object var;

        Field[] fields = cClass.getFields();
        for (int i = 0; i < fields.length; i++) {
            var = fields[i].get(component);
            if (JAMSNumeric.class.isAssignableFrom(fields[i].getType())) {
                // add ranges and units to JAMSNumeric objects

                JAMSNumeric numericObject = (JAMSNumeric) var;
                // get variable object or create one if not existing
                if (numericObject == null) {
                    numericObject = (JAMSNumeric) fields[i].getType().newInstance();
                    fields[i].set(component, numericObject);
                }

                JAMSVarDescription jvd = fields[i].getAnnotation(JAMSVarDescription.class);
                if (jvd.lowerBound() < jvd.upperBound()) {
                    //numericObject.setRange(jvd.lowerBound(), jvd.upperBound());
                }

                if (!jvd.unit().equals("")) {
                    try {
                        //numericObject.setUnit(jvd.unit());
                    } catch (IllegalArgumentException iaex) {
                        throw new ModelSpecificationException("Invalid unit string in component " + component.getClass().getName() + ": " + jvd.unit());
                    }
                }

                if (!jvd.defaultValue().equals("")) {
                    numericObject.setValue(jvd.defaultValue());
                }

            }
//            else if (JAMSData.class.isAssignableFrom(fields[i].getType())) {
//
//                JAMSData dataObject = (JAMSData) var; //get variable object or create one if not existing if (dataObject == null && !(JAMSEntity.class.isAssignableFrom(fields[i].getType()))) { dataObject = (JAMSData) fields[i].getType().newInstance(); fields[i].set(component, dataObject); } }
//            }
        }
    }

    public JAMSModel getModel() {
        return jamsModel;
    }

    public JAMSData getInstance(Class clazz) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        JAMSData data = null;

        String className = clazz.getSimpleName();

        if (className.equals("JAMSString")) {
            data = (JAMSData) clazz.newInstance();
        } else if (className.equals("JAMSInteger")) {
            data = (JAMSData) clazz.newInstance();
        } else if (className.equals("JAMSBoolean")) {
            data = (JAMSData) clazz.newInstance();
        } else if (className.equals("JAMSDouble")) {
            data = (JAMSData) Class.forName("jams.data.JAMSSimpleDouble").newInstance();
        } else {
            data = (JAMSData) clazz.newInstance();
        }

        return data;
    }
}
