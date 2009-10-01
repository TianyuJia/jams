/*
 * Task.java
 * Created on 4. April 2008, 09:01
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
package jams.runtime.concurrent;

import jams.model.JAMSComponent;

/**
 *
 * @author nsk
 */
public class Task extends JAMSComponent {

    int id;

    public Task(int id) {
        this.id = id;
    }

    public void init() {
        System.out.print(this.id + " ");
    }

    public void run() {
        double d = Math.random() * 1000000;
        while (d > 0) {
            d--;
        }
        System.out.print(this.id + " ");
    }

    public void cleanup() {
        System.out.print(this.id + " ");
    }
}