/*
 * Copyright 2017 Alvaro Stagg [alvarostagg@protonmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package VNAP;

import PluginReference.MC_Player;

import java.sql.*;

public class Connection {
    private String dbURL;
    private String dbUserName;
    private String dbPassword;
    private String dbTable;
    private String sql;

    private java.sql.Connection conn;
    private Statement stmt;
    private ResultSet rs;

    public Connection(Configuration cfg) throws Exception {
        dbURL = "jdbc:mariadb://" + cfg.getDbURL();
        dbUserName = cfg.getDbUser();
        dbPassword = cfg.getDbPassword();
        dbTable = cfg.getDbTable();

        Class.forName("org.mariadb.jdbc.Driver");
        conn = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
        stmt = conn.createStatement();
    }

    public boolean playerExists(String username) throws Exception {
        sql = "SELECT EXISTS(SELECT * FROM " + dbTable + " WHERE username=?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, username);
        rs = preparedStatement.executeQuery();

        return rs.first();
    }

    public String login(String username, String password) throws Exception {
        String userName = "";

        sql = "SELECT * FROM " + dbTable + " WHERE username=? AND password=?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        rs = preparedStatement.executeQuery();

        if (rs.next())
            userName = rs.getString("username");

        return userName;
    }

    public String register(String username, String password) throws Exception {
        String userName = "";

        sql = "INSERT INTO " + dbTable + " (username, password) VALUES (?,?)";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        rs = preparedStatement.executeQuery();

        if (rs.next())
            userName = username;

        return userName;
    }

    public void close() {
        try {
            if (conn != null)
                conn.close();
            if (stmt != null)
                stmt.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
