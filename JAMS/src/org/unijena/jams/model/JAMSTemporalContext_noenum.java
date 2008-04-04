/*
 * JAMSTemporalContext.java
 * Created on 31. Juli 2005, 20:24
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
package org.unijena.jams.model;

import org.unijena.jams.data.*;

/**
 *
 * @author S. Kralisch
 */
@JAMSComponentDescription(title = "JAMS temporal context", author = "Sven Kralisch", date = "31. Juli 2005", description = "This component represents a JAMS context which can be used to " +
"represent temporal contexts in environmental models")
public class JAMSTemporalContext extends JAMSContext {

    @JAMSVarDescription(access = JAMSVarDescription.AccessType.READ, update = JAMSVarDescription.UpdateType.INIT, description = "Time interval of temporal context")
    public JAMSTimeInterval timeInterval;
    @JAMSVarDescription(access = JAMSVarDescription.AccessType.WRITE, update = JAMSVarDescription.UpdateType.RUN, description = "Current date of temporal context")
    public JAMSCalendar current;
    JAMSCalendar lastValue;

    public JAMSTemporalContext() {
        current = new JAMSCalendar();
    }

    public JAMSTemporalContext(JAMSTimeInterval timeInterval) {
        this();
        this.timeInterval = timeInterval;
    }

    public JAMSTemporalContext(JAMSCalendar start, JAMSCalendar end, int timeUnit, int timeUnitCount) {
        this.timeInterval = new JAMSTimeInterval(start, end, timeUnit, timeUnitCount);
    }

    @Override
    public void init() {
        super.init();
        lastValue = timeInterval.getEnd().clone();
        lastValue.add(timeInterval.getTimeUnit(), -timeInterval.getTimeUnitCount());
        lastValue.add(JAMSCalendar.MILLISECOND, 1);
    }

    public void run() {

        JAMSComponentEnumerator ce = getChildrenEnumerator();
        current.setValue(timeInterval.getStart().getValue());
        ce.reset();

        while ((current.before(lastValue) || ce.hasNext()) && doRun) {

            if (!ce.hasNext() && current.before(lastValue)) {
                current.add(timeInterval.getTimeUnit(), timeInterval.getTimeUnitCount());
                ce.reset();
            }

            JAMSComponent comp = ce.next();
            //comp.updateRun();
            try {
                comp.run();
            } catch (Exception e) {
                getModel().getRuntime().handle(e, comp.getInstanceName());
            }
        }

        updateEntityData();
    }

    @Override
    public JAMSComponentEnumerator getRunEnumerator() {
        return new RunEnumerator();
    }

    @Override
    public long getNumberOfIterations() {
        return timeInterval.getNumberOfTimesteps();
    }

    class RunEnumerator implements JAMSComponentEnumerator {

        JAMSComponentEnumerator ce = getChildrenEnumerator();

        @Override
        public boolean hasNext() {
            boolean nextTime = current.before(lastValue);
            boolean nextComp = ce.hasNext();
            return (nextTime || nextComp);
        }

        @Override
        public JAMSComponent next() {
            // check end of component elements list, if required switch to the next
            // timestep start with the new Component list again
            if (!ce.hasNext() && current.before(lastValue)) {
                current.add(timeInterval.getTimeUnit(), timeInterval.getTimeUnitCount());
                ce.reset();
            }
            return ce.next();
        }

        @Override
        public void reset() {
            current.setValue(timeInterval.getStart().getValue());
            ce.reset();
        }
    }
}
