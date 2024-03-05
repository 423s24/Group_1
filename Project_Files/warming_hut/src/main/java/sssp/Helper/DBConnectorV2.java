package sssp.Helper;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.concurrent.Future;

public class DBConnectorV2{
    //Initialize variables
    public Database database;
    public Database lastPull;
    private String client;
    private String secret;
    private String endpoint = "https://hrdc-warming-hut-db-manager-default-rtdb.firebaseio.com/clients";

    private HttpStreamingManager streamingManager;

    // class constructor 
    public DBConnectorV2(String client, String secret){
        this.client = client;
        this.secret = secret;
        this.database = null;
        this.lastPull = null;
        getClientDatabase();
        this.database.print();
    }

    public Database sortConflicts(Database database){
        return database;
    }

    public void getClientDatabase() {
        Database database = new Database();
        

        database.conflicts = formatTable2(getTableJson("Conflicts"));
        
        database.cubeStorage = formatTable1(getTableJson("CubeStorage"));
        System.out.println("HERE3");
        database.dayStorage = formatTable1(getTableJson("DayStorage"));
        
        database.equipment = formatTable1(getTableJson("Equipment"));

        database.guestRoster = formatTable2(getTableJson("GuestRoster"));

        database.guests = formatTable1(getTableJson("Guests"));
        
        database.lockers = formatTable2(getTableJson("Lockers"));
        
        this.database = sortConflicts(database);
    }


    public Map<String, Map<String, Map<String, String>>> formatTable2(String json) {
        
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, Map<String, String>>>>(){}.getType();
        System.out.println("HERE1\n");
        System.out.println(json);
        Map<String, Map<String, Map<String, String>>> data = gson.fromJson(json, type);
        System.out.println("HERE2\n");
        return data;
    }

    public Map<String, Map<String, String>> formatTable1(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
        System.out.println("HERE1\n");
        System.out.println(json);
        Map<String, Map<String, String>> data = gson.fromJson(json, type);
        System.out.println("HERE2\n");
        return data;
    }

    public String getTableJson(String table) {
        try {
            // Construct the URL to fetch client's data
            String urlString = endpoint +"/"+ client + "/Tables/" + table + "/.json?auth=" + secret;
    
            // Create URL object
            URL url = new URL(urlString);
            
            // Open connection
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
    
            // Set request method
            con.setRequestMethod("GET");
    
            // Get response code
            int responseCode = con.getResponseCode();
    
            // If response code is success (200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    String json = response.toString();
                    return json;
                }
            } else {
                // If response code is not success, print error
                System.out.println("Failed to fetch client database. Response Code: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    

}
