/*
 * PGSQLConnector.java
 * Created on 30. November 2007, 16:16
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
package jams.workspace.plugins;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Sven Kralisch
 */
public class PGSQLConnector {

    private String hostname,  database,  username,  passwd;
    private Connection con;

    public PGSQLConnector(String hostname, String database, String username, String passwd) {
        this.hostname = hostname;
        this.database = database;
        this.username = username;
        this.passwd = passwd;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    public void connect() throws SQLException {
        this.con = DriverManager.getConnection("jdbc:postgresql://" + hostname + "/" + database, username, passwd);
    }

    public ResultSet execQuery(String sqlQuery) throws SQLException {
        if (con == null) {
            return null;
        } else {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);
            return rs;
        }
    }

    public void close() throws SQLException {
        this.con.close();
    }

}
