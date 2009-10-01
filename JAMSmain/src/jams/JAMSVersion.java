/*
 * JAMSVersion.java
 * Created on 4. Dezember 2008, 16:30
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
package jams;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Sven Kralisch <sven.kralisch at uni-jena.de>
 */
public class JAMSVersion {

    private String dateString;
    private int major, minor, revision;
    private static JAMSVersion instance;

    private JAMSVersion() {

        InputStream is = ClassLoader.getSystemResourceAsStream("resources/text/version.txt");
        Properties p = new Properties();
        try {
            p.load(is);
        } catch (IOException ioe) {
        }

        major = Integer.parseInt(p.getProperty("main.majorversion"));
        minor = Integer.parseInt(p.getProperty("main.minorversion"));
        revision = Integer.parseInt(p.getProperty("main.revision"));
        dateString = p.getProperty("date");
    }

    /**
     * Returns a singleton JAMSVersion object
     * @return A JAMSVersion object
     */
    public static JAMSVersion getInstance() {
        if (instance == null) {
            instance = new JAMSVersion();
        }
        return instance;
    }

    /**
     * Creates a string representation of the version including date
     * @return The string representation
     */
    public String getVersionDateString() {
        return String.format("%d.%d_%02d (%s)", major, minor, revision, dateString);
    }

    /**
     * Creates a string representation of the version
     * @return The string representation
     */
    public String getVersionString() {
        return String.format("%d.%d", major, minor);
    }

    /**
     * @return the dateString
     */
    public String getDateString() {
        return dateString;
    }

    /**
     * @return the major
     */
    public int getMajor() {
        return major;
    }

    /**
     * @return the minor
     */
    public int getMinor() {
        return minor;
    }

    /**
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }
}
