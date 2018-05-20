/*
 * Copyright 2018 Alvaro Stagg [alvarostagg@protonmail.com]
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

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Connection {
    private String dbURL;
    private String dbUserName;
    private String dbPassword;
    private String dbTable;
    private String sql;

    private java.sql.Connection conn;
    private Statement stmt;
    private ResultSet rs;

    private Configuration configuration;

    public Connection(Configuration configuration) throws Exception {
        this.configuration = configuration;

        dbURL = "jdbc:mariadb://" + configuration.getDbURL();
        dbUserName = configuration.getDbUser();
        dbPassword = configuration.getDbPassword();
        dbTable = configuration.getDbTable();

        Class.forName("org.mariadb.jdbc.Driver");
        conn = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
        stmt = conn.createStatement();
    }

    private String getHashFunction() {
        String function = "MD5(?)";

        switch (configuration.getHash().toUpperCase()) {
            case "SHA2":
            case "SHA256":
                function = "SHA2(?, 256)";
                break;
            case "SHA":
            case "SHA1":
                function = "SHA1(?)";
                break;
            case "MD5":
                break;
            default:
                System.out.println(String.format("[-] '%s' is not supported. Using MD5 Hash...", configuration.getHash()));
                break;
        }

        return function;
    }

    public boolean playerExists(String username) throws Exception {
        sql = "SELECT user_name FROM " + dbTable + " WHERE user_name=?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, username);
        rs = preparedStatement.executeQuery();

        return rs.next();
    }

    public boolean login(String username, String password) throws Exception {
        sql = "SELECT 1 FROM " + dbTable + " WHERE user_name=? AND password=" + getHashFunction();

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        rs = preparedStatement.executeQuery();

        boolean exists = false;
        if (rs.next()) {
            sql = "UPDATE " + dbTable + " SET last_login=NOW() WHERE user_name=?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
            exists = true;
        }

        return exists;
    }

    public void register(String username, String password) throws Exception {
        sql = "INSERT INTO " + dbTable + " (user_name, password) VALUES (?, " + getHashFunction() + ")";

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
