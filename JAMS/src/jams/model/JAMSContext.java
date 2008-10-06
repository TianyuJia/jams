/*
 * JAMSContext.java
 *
 * Created on 22. Juni 2005, 21:03
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
package jams.model;

/**
 *
 * @author S. Kralisch
 */
import jams.io.DataTracer.DataTracer;
import jams.workspace.stores.OutputDataStore;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import jams.JAMS;
import jams.data.*;
import jams.dataaccess.*;
import jams.dataaccess.CalendarAccessor;
import jams.io.DataTracer.NullTracer;
import jams.io.DataTracer.AbstractTracer;
import jams.runtime.JAMSRuntime;

@JAMSComponentDescription(title = "JAMS Component",
author = "Sven Kralisch",
date = "27. Juni 2005",
description = "This component represents a JAMS context which is the top level " +
"component of every component hierarchie in JAMS")
public class JAMSContext extends JAMSComponent {

    protected JAMSEntityCollection entities;
    protected JAMSEntity currentEntity;
    protected ArrayList<JAMSComponent> components = new ArrayList<JAMSComponent>();
    protected JAMSComponentEnumerator runEnumerator = null;
    protected JAMSComponentEnumerator initCleanupEnumerator = null;
    protected ArrayList<AccessSpec> accessSpecs = new ArrayList<AccessSpec>();
    protected ArrayList<AttributeSpec> attributeSpecs = new ArrayList<AttributeSpec>();
    protected DataAccessor[] dataAccessors = new DataAccessor[0];
    private HashMap<String, DataAccessor> daHash;
    protected HashMap<String, JAMSData> attribs;
    protected DataTracer[] dataTracers;
    protected boolean doRun = true;

    public JAMSContext() {
        //create an entity collection with one entity
        setCurrentEntity(JAMSDataFactory.createEntity());
        ArrayList<JAMSEntity> list = new ArrayList<JAMSEntity>();
        list.add(getCurrentEntity());
        setEntities(new JAMSEntityCollection());
        getEntities().setEntities(list);

        attribs = new HashMap<String, JAMSData>();

    }

    public void exchange(int i, int j) {
        JAMSComponent oi = components.get(i);
        JAMSComponent oj = components.get(j);
        components.set(i, oj);
        components.set(j, oi);
    }

    public void addComponent(JAMSComponent c) {
        components.add(c);
    }

    public void removeComponent(int index) {
        components.remove(index);
    }

    public ArrayList getComponents() {
        return components;
    }

    public void setComponents(ArrayList<JAMSComponent> components) {
        this.components = components;
        Iterator<JAMSComponent> i = components.iterator();
        while (i.hasNext()) {
            i.next().setContext(this);
        }
    }

    public JAMSComponentEnumerator getRunEnumerator() {
        return new RunEnumerator();
    }

    public JAMSComponentEnumerator getChildrenEnumerator() {
        return new ChildrenEnumerator();
    }

    public JAMSComponent[] getCompArray() {
        JAMSComponent[] comps = new JAMSComponent[components.size()];
        components.toArray(comps);
        return comps;
    }

    public void addAccess(JAMSComponent user, String varName, String attributeName, int accessType) {
        accessSpecs.add(new AccessSpec(user, varName, attributeName, accessType));
    }

    public void addAttribute(String attributeName, String clazz, String value) {
        attributeSpecs.add(new AttributeSpec(attributeName, clazz, value));
    }

    public void setModel(JAMSModel model) {
        super.setModel(model);
        JAMSRuntime rt = getModel().getRuntime();
        rt.addRunStateObserver(new Observer() {

            public void update(Observable obs, Object obj) {
                if (getModel().getRuntime().getRunState() != JAMS.RUNSTATE_RUN) {
                    JAMSContext.this.doRun = false;
                }
            }
        });
    }

    private Field getField(Class clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return getField(clazz.getSuperclass(), name);
        }
    }

    public void initAccessors() {

        attribs = new HashMap<String, JAMSData>();
        daHash = new HashMap<String, DataAccessor>();

        AccessSpec accessSpec;
        AttributeSpec attributeSpec;
        JAMSData dataObject;
        Class clazz;

        JAMSEntity[] entityArray = getEntities().getEntityArray();

        //handle all attribute declarations in this context
        Iterator<AttributeSpec> attributeIterator = attributeSpecs.iterator();
        while (attributeIterator.hasNext()) {
            attributeSpec = attributeIterator.next();

            try {

                JAMSData data = JAMSDataFactory.getData(attributeSpec.className);
                data.setValue(attributeSpec.value);
                attribs.put(attributeSpec.attributeName, data);

                //add attributes to "handle map"
                String id = this.getInstanceName() + "." + attributeSpec.attributeName;
                getModel().getRuntime().getDataHandles().put(id, data);

                for (JAMSEntity entity : entityArray) {
                    entity.setObject(attributeSpec.attributeName, data);
                }

            } catch (ClassNotFoundException cnfe) {
                getModel().getRuntime().handle(cnfe, false);
            } catch (InstantiationException ie) {
                getModel().getRuntime().handle(ie, false);
            } catch (IllegalAccessException iae) {
                getModel().getRuntime().handle(iae, false);
            } catch (Exception e) {
                getModel().getRuntime().handle(e, false);
            }
        }

        Iterator<AccessSpec> accessIterator = accessSpecs.iterator();
        while (accessIterator.hasNext()) {
            accessSpec = accessIterator.next();

            try {

                clazz = getField(accessSpec.component.getClass(), accessSpec.varName).getType();

                if (clazz == null) {
                    clazz = null;
                }
                if (clazz.isArray()) {

                    String className = clazz.getCanonicalName();
                    className = className.substring(0, className.length() - 2);

                    StringTokenizer tok = new StringTokenizer(accessSpec.attributeName, ";");
                    int count = tok.countTokens();

                    Class componentClass = Class.forName(className);
                    JAMSData[] array = (JAMSData[]) Array.newInstance(componentClass, count);

                    for (int i = 0; i < count; i++) {
                        array[i] = getDataObject(entityArray, componentClass, tok.nextToken(), accessSpec.accessType, null);
                    }

                    getField(accessSpec.component.getClass(), accessSpec.varName).set(accessSpec.component, array);

                } else {

                    /* 
                     * maybe the component's data object already has a value 
                     * assigned, so get it, but
                     * problem in case of a repeated execution of the whole
                     * model w/o newly creating the components with their initial
                     * attribute values -- they would keep their old attribute
                     * value introducing a model memory :/ -- better use null instead..
                     */
                    JAMSData componentObject = null;//(JAMSData) accessSpec.component.getClass().getDeclaredField(accessSpec.varName).get(accessSpec.component);
                    //JAMSData componentObject = (JAMSData) accessSpec.component.getClass().getDeclaredField(accessSpec.varName).get(accessSpec.component);

                    //get the data object belonging to the attribute
                    dataObject = getDataObject(entityArray, clazz, accessSpec.attributeName, accessSpec.accessType, componentObject);

                    //assign the dataObject to the component
                    getField(accessSpec.component.getClass(), accessSpec.varName).set(accessSpec.component, dataObject);


                }
            } catch (Exception e) {
                getModel().getRuntime().sendErrorMsg("Error occured in " + accessSpec.component.getInstanceName() + ": " + accessSpec.varName);
                getModel().getRuntime().handle(e, false);
            }

            // create DataAccessor array from DataAccessor list
            if (daHash.size() > 0) {
                this.dataAccessors = daHash.values().toArray(new DataAccessor[daHash.size()]);
            }
        }
    }

    public String getTraceMark() {
        return Long.toString(currentEntity.getId());
    }

    protected DataTracer createDataTracer(OutputDataStore store) {

        // create a DataTracer which is suited for this context
        if (store.getFilters().length == 0) {
            return new AbstractTracer(this, store, JAMSLong.class) {

                @Override
                public void trace() {
                    DataAccessor[] dataAccessors = this.accessorObjects;
                    JAMSEntity[] entities = context.getEntities().getEntityArray();
                    for (int j = 0; j < entities.length; j++) {

                        output(entities[j].getId());
                        output("\t");

                        for (int i = 0; i < dataAccessors.length; i++) {
                            dataAccessors[i].setIndex(j);
                            dataAccessors[i].read();
                            output(dataAccessors[i].getComponentObject());
                            output("\t");
                        }
                        output("\n");
                    }
                }
            };
        } else {
            return new AbstractTracer(this, store, JAMSLong.class) {

                @Override
                public void trace() {

                    // check for filters on other contexts first
                    for (OutputDataStore.Filter filter : store.getFilters()) {
                        if (filter.getContext() != JAMSContext.this) {
                            String s = filter.getContext().getTraceMark();
                            Matcher matcher = filter.getPattern().matcher(s);
                            if (!matcher.matches()) {
                                return;
                            }
                        }
                    }

                    DataAccessor[] dataAccessors = this.accessorObjects;
                    JAMSEntity[] entities = context.getEntities().getEntityArray();
                    for (int j = 0; j < entities.length; j++) {

                        boolean doBreak = false;

                        // take care of filters in this context
                        for (OutputDataStore.Filter filter : store.getFilters()) {
                            if (filter.getContext() == JAMSContext.this) {
                                String s = Long.toString(entities[j].getId());
                                Matcher matcher = filter.getPattern().matcher(s);
                                if (!matcher.matches()) {
                                    doBreak = true;
                                }
                            }
                        }

                        if (doBreak) {
                            continue;
                        }

                        output(entities[j].getId());
                        output("\t");

                        for (int i = 0; i < dataAccessors.length; i++) {
                            dataAccessors[i].setIndex(j);
                            dataAccessors[i].read();
                            output(dataAccessors[i].getComponentObject());
                            output("\t");
                        }
                        output("\n");
                    }
                }
            };
        }
    }

    private void setupDataTracer() {

        // get the output stores if existing
        OutputDataStore[] stores = getModel().getOutputDataStores(this.getInstanceName());

        this.dataTracers = new DataTracer[stores.length];
        if (stores.length == 0) {
            // if there is no store create a NullTracer (does nothing) and exit
            //this.dataTracers = new NullTracer();
            return;
        }

        // make sure there are accessors for all attributes        
        JAMSEntity[] entityArray = getEntities().getEntityArray();
        for (OutputDataStore store : stores) {
            for (String attributeName : store.getAttributes()) {

                try {
                    JAMSData attribute = (JAMSData) entityArray[0].getObject(attributeName);
                    if (attribute == null) {
                        continue;
                    }
                    Class clazz = attribute.getClass();
                    getDataObject(entityArray, clazz, attributeName, DataAccessor.READ_ACCESS, null);
                } catch (JAMSEntity.NoSuchAttributeException nsae) {
                    getModel().getRuntime().sendErrorMsg("Can't trace attribute \"" + attributeName +
                            "\" in context \"" + this.getInstanceName() + "\" (not found)!");
                // will do nothing here since this will be handled at 
                // the DataTracer's init method below..
                } catch (Exception e) {
                    getModel().getRuntime().sendErrorMsg("Error while trying to trace " + attributeName + ": " + this.getInstanceName());
                    getModel().getRuntime().handle(e, false);
                }
            }
        }

        // check if new dataAccessor objects where added
        // if so, create new array from list
        if (this.daHash.size() > this.dataAccessors.length) {
            this.dataAccessors = daHash.values().toArray(new DataAccessor[daHash.size()]);
        }

        int i = 0;
        for (OutputDataStore store : stores) {

            this.dataTracers[i] = createDataTracer(store);

            if (this.dataTracers[i].getAccessorObjects().length == 0) {
                this.dataTracers[i] = new NullTracer();
                return;
            }

            i++;
        }

    }

    @Override
    public void init() {

        attribs = new HashMap<String, JAMSData>();

        runEnumerator = null;

        if (!doRun) {
            return;
        }

        // setup accessors for data exchange between context attributes and
        // component attributes
        initAccessors();

        // setup data tracer -- needed for data output to output-datastores
        setupDataTracer();

        // create the init/cleanup enumerator (i.e. one invocation for every component)
        if (initCleanupEnumerator == null) {
            initCleanupEnumerator = getChildrenEnumerator();
        }

        // initialize init/cleanup enumerator and start iteration
        initCleanupEnumerator.reset();
        while (initCleanupEnumerator.hasNext() && doRun) {
            JAMSComponent comp = initCleanupEnumerator.next();
            //comp.updateInit();
            try {
                comp.init();
            } catch (Exception e) {
                getModel().getRuntime().handle(e, comp.getInstanceName());
            }
        }

        initEntityData();
    }

    public void registerAccessor(Class clazz, String attributeName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, JAMSEntity.NoSuchAttributeException {

        getDataObject(this.getEntities().getEntityArray(), clazz, attributeName, DataAccessor.READ_ACCESS, null);

        // check if new dataAccessor objects where added
        // if so, create new array from list
        if (this.daHash.size() > this.dataAccessors.length) {
            this.dataAccessors = daHash.values().toArray(new DataAccessor[daHash.size()]);
        }
    }

    protected JAMSData getDataObject(final JAMSEntity[] ea, final Class clazz, final String attributeName, final int accessType, JAMSData componentObject) throws InstantiationException, IllegalAccessException, ClassNotFoundException, JAMSEntity.NoSuchAttributeException {
        JAMSData dataObject;
        DataAccessor da = null;

        dataObject = attribs.get(attributeName);
        if (dataObject == null) {

            if (componentObject != null) {
                dataObject = componentObject;
            } else {
                if (clazz.getName().equals("jams.data.JAMSEntity")) {
                    dataObject = (JAMSData) JAMSDataFactory.createEntity();
                } else {
                    dataObject = (JAMSData) clazz.newInstance();
                }
            }

            attribs.put(attributeName, dataObject);

            if (clazz.equals(JAMSDouble.class)) {
                da = new DoubleAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSDoubleArray.class)) {
                da = new DoubleArrayAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSLong.class)) {
                da = new CalendarAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSLongArray.class)) {
                da = new LongArrayAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSString.class)) {
                da = new StringAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSStringArray.class)) {
                da = new StringArrayAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSBoolean.class)) {
                da = new BooleanAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSBooleanArray.class)) {
                da = new BooleanArrayAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSFloat.class)) {
                da = new FloatAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSFloatArray.class)) {
                da = new FloatArrayAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSInteger.class)) {
                da = new IntegerAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSIntegerArray.class)) {
                da = new IntegerArrayAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSEntity.class)) {
                da = new EntityAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSEntityCollection.class)) {
                da = new EntityCollectionAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSCalendar.class)) {
                da = new CalendarAccessor(ea, dataObject, attributeName, accessType);
            } else if (clazz.equals(JAMSDocument.class)) {
                da = new DocumentAccessor(ea, dataObject, attributeName, accessType);
            } else {
                getModel().getRuntime().sendHalt("Class " + clazz.getCanonicalName() + " not supported!");
            }

            if (da != null) {
                daHash.put(attributeName, da);
            }
        }
        return dataObject;
    }

    protected void initEntityData() {

        //in case the components want to write access the objects, trace the entity objects attributes
        for (int i = 0; i < dataAccessors.length; i++) {
            if (dataAccessors[i].getAccessType() == DataAccessor.WRITE_ACCESS) {
                for (int j = 0; j < getEntities().getEntities().size(); j++) {
                    dataAccessors[i].setIndex(j);
                    dataAccessors[i].write();
                }
            }
        }
    }

    public void run() {

        for (DataTracer dataTracer : dataTracers) {
            dataTracer.startMark();
        }

        //initEntityData();

        if (runEnumerator == null) {
            runEnumerator = getRunEnumerator();
        }

        runEnumerator.reset();
        while (runEnumerator.hasNext() && doRun) {
            JAMSComponent comp = runEnumerator.next();
            //comp.updateRun();
            try {
                comp.run();
            } catch (Exception e) {
                getModel().getRuntime().handle(e, comp.getInstanceName());
            }
        }

        updateEntityData();

        for (DataTracer dataTracer : dataTracers) {
            dataTracer.trace();
            dataTracer.endMark();
        }
    }

    public void cleanup() {

        if (initCleanupEnumerator == null) {
            initCleanupEnumerator = getChildrenEnumerator();
        }

        initCleanupEnumerator.reset();

        while (initCleanupEnumerator.hasNext() && doRun) {
            JAMSComponent comp = initCleanupEnumerator.next();
            try {
                comp.cleanup();
            } catch (Exception e) {
                getModel().getRuntime().handle(e, comp.getInstanceName());
            }
        }

        ArrayList<JAMSEntity> list = new ArrayList<JAMSEntity>();
        list.add(JAMSDataFactory.createEntity());

    }

    public HashMap<String, DataAccessor> getDaHash() {
        return daHash;
    }

    class ChildrenEnumerator implements JAMSComponentEnumerator {

        JAMSComponent[] compArray = getCompArray();
        int index = 0;

        public boolean hasNext() {
            return (index < compArray.length);
        }

        public JAMSComponent next() {
            return compArray[index++];
        }

        public void reset() {
            index = 0;
        }
    }

    class RunEnumerator implements JAMSComponentEnumerator {

        JAMSComponentEnumerator ce = getChildrenEnumerator();
        JAMSEntityEnumerator ee = getEntities().getEntityEnumerator();
        int index = 0;

        public boolean hasNext() {
            boolean nextComp = ce.hasNext();
            boolean nextEntity = ee.hasNext();
            return (nextEntity || nextComp);
        }

        public JAMSComponent next() {
            // check end of component elements list, if required switch to the next
            // entity and start with the new Component list again
            if (!ce.hasNext() && ee.hasNext()) {
                updateEntityData();
                setCurrentEntity(ee.next());
                index++;
                updateComponentData(index);
                ce.reset();
            }
            return ce.next();
        }

        public void reset() {
            ee.reset();
            setCurrentEntity(getEntities().getCurrent());
            ce.reset();
            index = 0;
            updateComponentData(index);
        }
    }

    public class ContextAttributeReadWriteSet {

        public Hashtable<String, HashSet<String>> writeAccessComponents = null;
        public Hashtable<String, HashSet<String>> readAccessComponents = null;
        JAMSEntityCollection entities_id;

        ContextAttributeReadWriteSet(JAMSEntityCollection entities) {
            this.entities_id = entities;

            writeAccessComponents = new Hashtable<String, HashSet<String>>();
            readAccessComponents = new Hashtable<String, HashSet<String>>();
        }
    }

    public Hashtable<String, HashSet<String>> getDependencyGraph() {
        Hashtable<String, HashSet<String>> edges = new Hashtable<String, HashSet<String>>();

        //for each independed context, get read/write access components
        //and create an edge from read to write component!
        HashSet<ContextAttributeReadWriteSet> CAttrRWSet = getAttributeReadWriteSet();

        Hashtable<String, HashSet<String>> writeAccessComponents = null;
        Hashtable<String, HashSet<String>> readAccessComponents = null;

        //process all independ contexts
        Iterator<ContextAttributeReadWriteSet> iter = CAttrRWSet.iterator();
        while (iter.hasNext()) {
            ContextAttributeReadWriteSet e = iter.next();
            writeAccessComponents = e.writeAccessComponents;
            readAccessComponents = e.readAccessComponents;
            Set<String> writeAccessKeys = writeAccessComponents.keySet();
            Iterator<String> writeAccessKeysIter = writeAccessKeys.iterator();

            //all attributes   
            writeAccessKeysIter = writeAccessKeys.iterator();
            while (writeAccessKeysIter.hasNext()) {
                String attr = writeAccessKeysIter.next();
                //all components which have write/read access to this attrib
                HashSet<String> writeAccess = writeAccessComponents.get(attr);
                HashSet<String> readAccess = readAccessComponents.get(attr);

                if (readAccess == null || writeAccess == null) {
                    continue;
                }
                Iterator<String> writerIterator = writeAccess.iterator();
                //iterate through write access components
                while (writerIterator.hasNext()) {
                    String writeAccessComponent = writerIterator.next();
                    //iterate through read access components
                    Iterator<String> readerIterator = readAccess.iterator();
                    while (readerIterator.hasNext()) {
                        String readAccessComponent = readerIterator.next();
                        //add edge from read to write access component
                        if (edges.containsKey(readAccessComponent)) {
                            edges.get(readAccessComponent).add(writeAccessComponent);
                        } else {
                            HashSet<String> list = new HashSet<String>();
                            list.add(writeAccessComponent);
                            edges.put(readAccessComponent, list);
                        }
                    }
                }
            }
        }

        return edges;
    }

    public Set<String> CollectAttributeWritingComponents(String attr) {
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < this.accessSpecs.size(); i++) {
            if (accessSpecs.get(i).attributeName.equals(attr)) {
                if (accessSpecs.get(i).accessType != DataAccessor.READ_ACCESS) {
                    set.add(accessSpecs.get(i).component.getInstanceName());
                }
            }
        }
        for (int i = 0; i < this.components.size(); i++) {
            if (components.get(i) instanceof JAMSContext) {
                JAMSContext c = (JAMSContext) components.get(i);
                set.addAll(c.CollectAttributeWritingComponents(attr));
            }
        }
        return set;
    }
    //create a list for each attribute containing all components which have
    //read and write access to

    public HashSet<ContextAttributeReadWriteSet> getAttributeReadWriteSet() {
        //this is necessary!
        this.initAccessors();


        HashSet<ContextAttributeReadWriteSet> ConAttrRWSets = new HashSet<ContextAttributeReadWriteSet>();
        ContextAttributeReadWriteSet ConAttrRWSet = new ContextAttributeReadWriteSet(this.getEntities());

        ConAttrRWSets.add(ConAttrRWSet);

        Hashtable<String, HashSet<String>> writeAccessComponents = ConAttrRWSet.writeAccessComponents;
        Hashtable<String, HashSet<String>> readAccessComponents = ConAttrRWSet.readAccessComponents;

        //for this context: collect all reading and writing components for each attribute
        for (int j = 0; j < accessSpecs.size(); j++) {
            AccessSpec as = accessSpecs.get(j);

            String attrName = null;
            StringTokenizer tok = new StringTokenizer(as.attributeName, ";");
            while (tok.hasMoreTokens()) {
                attrName = tok.nextToken();

                //if there is an entiy attribute, we dont know what attributes will be changed
                boolean isEntity = false;
                try {
                    Class clazz = as.component.getClass().getDeclaredField(as.varName).getType();
                    if (clazz.getName().equals("jams.data.JAMSEntity") || clazz.getName().equals("jams.data.JAMSEntityCollection")) {
                        isEntity = true;
                    }
                } catch (Exception e) {
                    //this is not a big issue
                    getModel().getRuntime().println("Cant get class of variable " + as.varName + " of component + " + as.component.instanceName + " because:" + e.toString());
                }

                if (as.accessType == DataAccessor.READ_ACCESS || as.accessType == DataAccessor.READWRITE_ACCESS || isEntity) {
                    if (readAccessComponents.containsKey(attrName)) {
                        readAccessComponents.get(attrName).add(as.component.instanceName);
                    } else {
                        HashSet<String> set = new HashSet<String>();
                        set.add(as.component.instanceName);
                        readAccessComponents.put(attrName, set);
                    }
                }
                if (as.accessType == DataAccessor.WRITE_ACCESS || as.accessType == DataAccessor.READWRITE_ACCESS || isEntity) {
                    if (writeAccessComponents.containsKey(attrName)) {
                        writeAccessComponents.get(attrName).add(as.component.instanceName);
                    } else {
                        HashSet<String> set = new HashSet<String>();
                        set.add(as.component.instanceName);
                        set.add(this.getInstanceName());
                        writeAccessComponents.put(attrName, set);
                    }
                }
            }
        }

        //process all children contexts
        for (int i = 0; i < this.components.size(); i++) {
            JAMSComponent c = components.get(i);
            if (c instanceof JAMSContext) {
                JAMSContext context = (JAMSContext) c;
                Set<ContextAttributeReadWriteSet> subMap = context.getAttributeReadWriteSet();
                ConAttrRWSets.addAll(subMap);
            }
        }

        //it is possible, that two contexts with different name are working on the
        //same data set iff the entity ids are equal
        //in this case the read/write sets have to be unified
        HashSet<ContextAttributeReadWriteSet> mergedCDGs = new HashSet<ContextAttributeReadWriteSet>();

        Iterator<ContextAttributeReadWriteSet> iter = ConAttrRWSets.iterator();
        while (iter.hasNext()) {
            ContextAttributeReadWriteSet e = iter.next();
            JAMSEntityCollection id = e.entities_id;

            Iterator<ContextAttributeReadWriteSet> iter2 = mergedCDGs.iterator();
            boolean EntityAdded = false;
            while (iter2.hasNext()) {
                ContextAttributeReadWriteSet e2 = iter2.next();
                JAMSEntityCollection id2 = e2.entities_id;
                if (id == id2) {
                    e2.readAccessComponents.putAll(e.readAccessComponents);
                    e2.writeAccessComponents.putAll(e.writeAccessComponents);
                    EntityAdded = true;
                }
            }
            if (!EntityAdded) {
                mergedCDGs.add(e);
            }
        }

        return mergedCDGs;
    }

    protected boolean componentInContext(JAMSComponent component) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).instanceName.equals(component.instanceName)) {
                return true;
            }
            if (components.get(i) instanceof JAMSContext) {
                if (((JAMSContext) components.get(i)).componentInContext(component)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected int componentAllreadyProcessed(JAMSComponent position, JAMSComponent component) {
        if (this.instanceName.equals(position.instanceName)) {
            return 0;
        }
        if (this == component) {
            return 1;
        }
        for (int j = 0; j < this.components.size(); j++) {
            JAMSComponent c = this.components.get(j);
            if (c == position) {
                return 0;
            }
            if (c == component) {
                return 1;
            }
            if (c instanceof JAMSContext) {
                JAMSContext context = (JAMSContext) c;
                int result = context.componentAllreadyProcessed(position, component);
                if (result == 0 || result == 1) {
                    return result;
                }
            }
        }
        return 2;
    }

    /*
     * looks if component object contains valid data
     * used when making a model snapshot
     */
    protected int containsValidData(JAMSComponent position, Object componentObj) {
        /* three steps:
         * 1. get name of attribute to componentObj in this context
         *    <context> attribName specifies attribute fully
         * 2. get name of components which use this attribute in write or read-write mode
         * 3. look one of these components has allready been executed
         * 
         */

        //search for attribname
        Iterator<Entry<String, JAMSData>> iter = this.attribs.entrySet().iterator();
        String attribName = null;
        while (iter.hasNext()) {
            Entry<String, JAMSData> e = iter.next();
            if ((Object) e.getValue() == componentObj) {
                attribName = e.getKey();
                break;
            }
        }

        //search in accessSpecs for components which use this attrib
        JAMSComponent component = null;
        if (attribName != null) {
            for (int i = 0; i < this.accessSpecs.size(); i++) {
                if (this.accessSpecs.get(i).attributeName.compareTo(attribName) == 0) {
                    //do they write this attrib?
                    if (this.accessSpecs.get(i).accessType != DataAccessor.READ_ACCESS) {
                        component = this.accessSpecs.get(i).component;
                        //has component been executed
                        if (componentAllreadyProcessed(position, component) == 1) {
                            return 1;
                        }
                    }
                }
            }
        }
        //look if there is an child context which uses this componentObj
        for (int j = 0; j < components.size(); j++) {
            JAMSComponent c = components.get(j);
            if (c.instanceName.equals(position.instanceName)) {
                return 0;
            }
            if (c instanceof JAMSContext) {
                if (((JAMSContext) components.get(j)).containsValidData(position, componentObj) == 1) {
                    return 1;
                }
            }
        }
        return 2;
    }

    /*
     * trace entity data, but trace data only, if data source component has already been executed,
     * which means that data source component is executed before currentComponent
     * used when making a model snapshot
     */
    protected void updateEntityData(JAMSComponent currentComponent) {
        //if this context has not been executed at all, exit                
        if (this.getModel().componentAllreadyProcessed(currentComponent, this) != 1) {
            return;
        //if this context is finished .. 
        }
        if (!componentInContext(currentComponent)) {
            return;
        }
        for (int i = 0; i < dataAccessors.length; i++) {
            //get pointer to component data
            Object componentObj = dataAccessors[i].getComponentObject();
            //look if component data is already up to date
            if (containsValidData(currentComponent, componentObj) == 1) {
                dataAccessors[i].write();
            }
        }
    }

    protected void updateEntityData() {
        //write entity data after execution
        for (int i = 0; i < dataAccessors.length; i++) {
            dataAccessors[i].write();
        }
    }

    protected void updateComponentData(int index) {
        for (int i = 0; i < dataAccessors.length; i++) {
            dataAccessors[i].setIndex(index);
            //read entity data before execution
            dataAccessors[i].read();
        }
    }

    public long getNumberOfIterations() {
        return getEntities().getEntities().size();
    }

    protected class AttributeSpec implements Serializable {

        String attributeName, className,
                value;

        public AttributeSpec(String attributeName, String className, String value) {
            this.attributeName = attributeName;
            this.className = className;
            this.value = value;


        }
    }

    protected class AccessSpec
            implements Serializable {

        JAMSComponent component;
        String varName;
        String attributeName;
        int accessType;

        public AccessSpec(JAMSComponent component, String varName, String attributeName, int accessType) {
            this.component = component;
            this.varName = varName;
            this.attributeName = attributeName;
            this.accessType = accessType;
        }
    }

    public JAMSEntityCollection getEntities() {
        return entities;
    }

    public void setEntities(JAMSEntityCollection entities) {
        this.entities = entities;
    }

    public JAMSEntity getCurrentEntity() {
        return currentEntity;
    }

    public void setCurrentEntity(JAMSEntity currentEntity) {
        this.currentEntity = currentEntity;
    }
}
