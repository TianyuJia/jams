/*
 * JAMSClassLoader.java
 * Created on 4. Juli 2005, 15:47
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
package org.unijena.jams.runtime;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.unijena.jams.JAMS;

public class JAMSClassLoader extends URLClassLoader {
    
    private URL[] urls;
    
    public JAMSClassLoader(URL[] urls) {
        super(urls, ClassLoader.getSystemClassLoader());
        this.urls = urls;
    }
    
    public JAMSClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.urls = urls;
    }
    
    private void loadAll() {
        for (URL jar : urls) {
            System.out.println(jar.getFile());
            
            try {
                JarInputStream jis = new JarInputStream(new FileInputStream(jar.getFile()));
                JarEntry entry = jis.getNextJarEntry();
                
                while (entry != null) {
                    String name = entry.getName();
                    if (name.endsWith(".class")) {
                        name = name.substring(0, name.length() - 6);
                        name = name.replace('/', '.');
                        System.out.print("> " + name);
                        
                        try {
                            loadClass(name);
                            System.out.println("\t- loaded");
                        } catch (Throwable e) {
                            System.out.println("\t- not loaded");
                            System.out.println("\t " + e.getClass().getName() + ": " + e.getMessage());
                        }
                        
                    }
                    entry = jis.getNextJarEntry();
                }
                
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
    }
    
    
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, true);
    }
    
    public Class load(String name, byte[] data) {
        return defineClass(name, data, 0, data.length);
    }
    
    private static void addFile(Set<URL> urls, File f, JAMSRuntime rt) {
        try {
            URL url = f.toURI().toURL();
            if (!urls.add(url))
                rt.println("WARNING : The file " + f.getAbsolutePath() + " is already loaded");
        } catch (MalformedURLException murle) {
            rt.println("WARNING : The file " + f.getAbsolutePath() + " could not be converted to URL.");
        }
    }
    
    public static ClassLoader createClassLoader(String[] libs, JAMSRuntime rt) {
        Set<URL> urls = new HashSet<URL>();
        for (String lib : libs) {
            
            File dir = new File(lib);
            
            if (!dir.exists()) {
                rt.println("DANGER - directory " + dir.getAbsolutePath() + " does not exist", JAMS.STANDARD);
                continue;
            }
            
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File file : files) {
                    if (file.getName().endsWith(".jar")) {
                        addFile(urls, file, rt);
                    }
                }
            } else {
                
                addFile(urls, dir, rt);
                
            }
        }
        rt.println("created class loader using " + urls, JAMS.STANDARD);
        
        URL[] urlArray = urls.toArray(new URL[urls.size()]);
        
        JAMSClassLoader cl = new JAMSClassLoader(urlArray);
        return cl;
    }
    
    
}
