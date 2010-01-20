/*
 * RunnableComponent.java
 * Created on 13. Januar 2010, 23:26
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
package jams.model.concurrent;

import jams.model.Component;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
class RunnableComponent implements Runnable {

    Component comp;

    public RunnableComponent(Component comp) {
        this.comp = comp;
    }

    public void run() {
        try {
            comp.run();
        } catch (Exception e) {
            comp.getModel().getRuntime().handle(e, comp.getInstanceName());
        }
    }
}