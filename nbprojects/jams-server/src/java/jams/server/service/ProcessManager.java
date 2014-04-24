/*
 * ProcessManager.java
 * Created on 23.04.2014, 18:11:51
 *
 * This file is part of JAMS
 * Copyright (C) FSU Jena
 *
 * JAMS is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * JAMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JAMS. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package jams.server.service;

import jams.server.entities.Job;
import jams.server.entities.JobState;
import java.io.IOException;

/**
 *
 * @author christian
 */
public interface ProcessManager {    
    Job deploy(Job job) throws IOException;
    JobState state(Job job) throws IOException;
}
