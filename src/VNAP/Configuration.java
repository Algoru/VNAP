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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileWriter;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileReader;

public class Configuration {
    private String serverName;
    private String dbURL;
    private String dbUser;
    private String dbPassword;
    private String dbTable;
    private String hash;

    private File configFile;

    private final String defaultConfiguration =
            "{\n"
            + "    \"server_name\": \"your server name\",\n"
            + "    \"db_url\"     : \"db_name(url)_here\",\n"
            + "    \"user\"       : \"db_user_here\",\n"
            + "    \"password\"   : \"db_password_here\",\n"
            + "    \"table\"      : \"db_table_here\",\n"
            + "    \"hash\"       : \"MD5\""
            + "}";

    public Configuration(String filePath) throws Exception {
        configFile = new File(filePath);

        if (!configFile.exists()) {
            System.out.println(" [+] Configuration file not found. Creating...");

            FileWriter fileWriter = new FileWriter(filePath);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(defaultConfiguration);

            if (fileWriter != null)
                fileWriter.close();
            System.out.println(" [+] Configuration file created !");
        }
    }

    public void readConfiguration() throws Exception {
        System.out.println(" [+] Reading configuration file...");

        FileReader fileReader = new FileReader(configFile);
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(fileReader);
        JSONObject jsonObject = (JSONObject)obj;

        serverName = (String)jsonObject.get("server_name");
        dbURL = (String)jsonObject.get("db_url");
        dbUser = (String)jsonObject.get("user");
        dbPassword = (String)jsonObject.get("password");
        dbTable = (String)jsonObject.get("table");
        hash = (String)jsonObject.get("hash");

        fileReader.close();
    }

    public String getServerName() {
        return serverName;
    }

    public String getDbURL() {
        return dbURL;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbTable() {
        return dbTable;
    }

    public String getHash() { return hash; }
}
