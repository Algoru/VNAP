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
        sql = "SELECT userName FROM " + dbTable + " WHERE userName=?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, username);
        rs = preparedStatement.executeQuery();

        return rs.next();
    }

    public boolean login(String username, String password) throws Exception {
        sql = "SELECT 1 FROM " + dbTable + " WHERE username=? AND password=MD5(?)";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        rs = preparedStatement.executeQuery();

        boolean exists = false;
        if (rs.next()) {
            sql = "UPDATE " + dbTable + " SET lastLogin=NOW() WHERE userName=?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
            exists = true;
        }

        return exists;
    }

    public void register(String username, String password) throws Exception {
        sql = "INSERT INTO " + dbTable + " (userName, password) VALUES (?, MD5(?))";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        rs = preparedStatement.executeQuery();
    }

    public void close() {
        try {
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
