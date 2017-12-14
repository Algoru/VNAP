package VNAP;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class Configuration {
    private String serverName;
    private String dbURL;
    private String dbUser;
    private String dbPassword;
    private String dbTable;

    private File configFile;

    private final String defaultConfiguration =
            "{\n"
            + "    \"server_name\": \"your server name\",\n"
            + "    \"db_url\"     : \"db_name(url)_here\",\n"
            + "    \"user\"       : \"db_user_here\",\n"
            + "    \"password\"   : \"db_password_here\",\n"
            + "    \"table\"      : \"db_table_here\"\n"
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
}
