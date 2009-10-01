/*
 * JAMSEntityCollection.java
 * Created on 2. August 2005, 21:03
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
package jams.data;

import jams.data.Attribute.Entity;
import java.util.*;

/**
 *
 * @author S. Kralisch
 */
public class JAMSEntityCollection implements Attribute.EntityCollection {

    protected ArrayList<Attribute.Entity> entities = new ArrayList<Attribute.Entity>();

    protected Attribute.Entity[] entityArray;

    protected Attribute.Entity current;

    JAMSEntityCollection() {
    }

    @Override
    public Attribute.Entity[] getEntityArray() {
        return this.entityArray;
    }

    @Override
    public EntityEnumerator getEntityEnumerator() {
        return new EntityEnumerator() {

            Attribute.Entity[] entityArray = getEntityArray();

            int index = 0;

            @Override
            public boolean hasNext() {
                return (index + 1 < entityArray.length);
            }

            @Override
            public Attribute.Entity next() {
                index++;
                JAMSEntityCollection.this.current = entityArray[index];
                return entityArray[index];
            }

            @Override
            public void reset() {
                index = 0;
                JAMSEntityCollection.this.current = entityArray[index];
            }
        };
    }

    @Override
    public ArrayList<Attribute.Entity> getEntities() {
        return entities;
    }

    @Override
    public void setEntities(ArrayList<Attribute.Entity> entities) {
        this.entities = entities;
        this.entityArray = entities.toArray(new JAMSEntity[entities.size()]);
        if (entityArray.length > 0) {
            this.current = entityArray[0];
        } else {
            this.current = null;
        }
    }

    @Override
    public Attribute.Entity getCurrent() {
        return current;
    }

    @Override
    public void setValue(String data) {
        //this makes no sense!
    }

    @Override
    public void setValue(ArrayList<Attribute.Entity> entities) {
        setEntities(entities);
    }

    @Override
    public ArrayList<Attribute.Entity> getValue() {
        return getEntities();
    }
}