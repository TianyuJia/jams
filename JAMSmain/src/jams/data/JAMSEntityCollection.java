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

import java.util.*;

/**
 *
 * @author S. Kralisch
 */
public class JAMSEntityCollection implements Attribute.EntityCollection {

    private ArrayList<Attribute.Entity> entities = new ArrayList<Attribute.Entity>();
    private Attribute.Entity[] entityArray;
    private Attribute.Entity current;
    private EntityEnumerator ee = null;
    private HashMap<Long, Attribute.Entity> idMap;

    @Override
    public Attribute.Entity[] getEntityArray() {
        return this.entityArray;
    }

    @Override
    public EntityEnumerator getEntityEnumerator() {

//        if (ee == null) {

            ee = new EntityEnumerator() {

                Attribute.Entity[] entityArray = getEntityArray();
                int index = 0;

                @Override
                public boolean hasNext() {
                    return (index + 1 < entityArray.length);
                }

                @Override
                public boolean hasPrevious() {
                    return index > 0;
                }

                @Override
                public Attribute.Entity next() {
                    index++;
                    JAMSEntityCollection.this.current = entityArray[index];
                    return JAMSEntityCollection.this.current;
                }

                @Override
                public Attribute.Entity previous() {
                    index--;
                    JAMSEntityCollection.this.current = entityArray[index];
                    return JAMSEntityCollection.this.current;
                }

                @Override
                public void reset() {
                    index = 0;
                    JAMSEntityCollection.this.current = entityArray[index];
                }
            };
//        }

        return ee;
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

    @Override
    public Attribute.Entity getEntity(long id) {
        if (idMap == null) {
             idMap = new HashMap<Long, Attribute.Entity>();
             for (Attribute.Entity e : entities) {
                 idMap.put(e.getId(), e);
             }
        }
        return idMap.get(id);
    }
}
