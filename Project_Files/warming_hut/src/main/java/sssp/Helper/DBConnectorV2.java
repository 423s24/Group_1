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

import javax.xml.crypto.Data;

public class DBConnectorV2{
    //Initialize variables
    public Database database;
    public Database artifactDatabase;
    public Database freshDatabase;
    private String client;
    private String secret;
    private String endpoint = "https://hrdc-warming-hut-db-manager-default-rtdb.firebaseio.com/clients";

    private HttpStreamingManager streamingManager;

    // class constructor 
    public DBConnectorV2(String client, String secret){
        this.client = client;
        this.secret = secret;
        this.database = null;
        this.artifactDatabase = null;
        this.freshDatabase = null;
        originalDatabasePull();
    }


    public void push(){
        this.sortConflicts();
        pushTable("Attributes", this.convertToJson2(this.database.attributes));
        pushTable("Conflicts", this.convertToJson2(this.database.conflicts));
        pushTable("CubeStorage", this.convertToJson1(this.database.cubeStorage));
        pushTable("DayStorage", this.convertToJson2(this.database.dayStorage));
        pushTable("Equipment", this.convertToJson1(this.database.equipment));
        pushTable("GuestRoster", this.convertToJson2(this.database.guestRoster));
        pushTable("Guests", this.convertToJson1(this.database.guests));
        pushTable("Lockers", this.convertToJson2(this.database.lockers));
        pushTable("UnknownItems", this.convertToJson1(this.database.unknownItems));
        pushTable("WaitingList", this.convertToJson1(this.database.waitingList));
    }

    public String convertToJson2(Map<String, Map<String, Map<String, String>>> table){
        Gson gson = new Gson();
        return gson.toJson(table);
    }

    public String convertToJson1(Map<String, Map<String, String>> table){
        Gson gson = new Gson();
        return gson.toJson(table);
    }


    public void pushTable(String table, String json) {
        try {
            String urlString = endpoint +"/"+ client + "/Tables/" + table + "/.json?auth=" + secret;

            // Create URL object
            URL url = new URL(urlString);

            // Open connection
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Set request method to PUT
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            // Enable input and output
            con.setDoOutput(true);
            con.setDoInput(true); // Enable input for reading response

            // Write JSON string to the output stream
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get response code
            int responseCode = con.getResponseCode();

            // Check if the response code indicates success
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read response
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
                System.out.println("Local table successfully pushed to Firebase Realtime Database.");
            } else {
                // If response code indicates failure, print error message
                System.out.println("Failed to push local table to Firebase Realtime Database. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            // Print stack trace if an exception occurs
            e.printStackTrace();
        }
    }
    
    public void sortConflicts(){
        // update Fresh Database
        this.getFreshDatabase();

        
        this.findConflicts2(this.database.attributes, this.artifactDatabase.attributes, this.freshDatabase.attributes);
        this.findConflicts2(this.database.conflicts, this.artifactDatabase.conflicts, this.freshDatabase.conflicts);
        this.findConflicts1(this.database.cubeStorage, this.artifactDatabase.cubeStorage, this.freshDatabase.cubeStorage);
        this.findConflicts2(this.database.dayStorage, this.artifactDatabase.dayStorage, this.freshDatabase.dayStorage);
        this.findConflicts1(this.database.equipment, this.artifactDatabase.equipment, this.freshDatabase.equipment);
        this.findConflicts2(this.database.guestRoster, this.artifactDatabase.guestRoster, this.freshDatabase.guestRoster);
        this.findConflicts1(this.database.guests, this.artifactDatabase.guests, this.freshDatabase.guests);
        this.findConflicts2(this.database.lockers, this.artifactDatabase.lockers, this.freshDatabase.lockers);
        this.findConflicts1(this.database.unknownItems, this.artifactDatabase.unknownItems, this.freshDatabase.unknownItems);
        this.findConflicts1(this.database.waitingList, this.artifactDatabase.waitingList, this.freshDatabase.waitingList);

        this.database = this.freshDatabase.deepCopy();
        this.artifactDatabase = this.database.deepCopy();
        
    }

    public void findConflicts2( Map<String, Map<String, Map<String, String>>> table, Map<String, Map<String, Map<String, String>>> artifactTable, Map<String, Map<String, Map<String, String>>> freshTable  ) {
        for (Map.Entry<String, Map<String, Map<String, String>>> original : table.entrySet()){
            boolean found1 = false;
            for (Map.Entry<String, Map<String, Map<String, String>>> artifact : artifactTable.entrySet()){
                if(artifact.getKey().equals(original.getKey())){
                    found1 = true;
                    // Iterate through the fields, checking for the same values. if a value was changed then update 
                    for (Map.Entry<String, Map<String, String>> originalfield1 : original.getValue().entrySet()){
                        boolean found2 = false;
                        for (Map.Entry<String, Map<String, String>> artifactfield1 : artifact.getValue().entrySet()){
                            if(artifactfield1.getKey().equals(originalfield1.getKey())){
                                found2 = true;
                                for (Map.Entry<String, String> originalfield2 : originalfield1.getValue().entrySet()){
                                    for (Map.Entry<String, String> artifactfield2 : artifactfield1.getValue().entrySet()){
                                        if(artifactfield2.getKey().equals(originalfield2.getKey())){
                                            if(!artifactfield2.getValue().equals(originalfield2.getValue())){
                                                freshTable.get(original.getKey()).get(originalfield1.getKey()).put(originalfield2.getKey(), originalfield2.getValue());
                                            }
                                        }   
                                    }
                                }
                            }
                        }
                        if(!found2){
                            freshTable.get(original.getKey()).put(originalfield1.getKey(), originalfield1.getValue());
                        }                      
                    }
                }
            }
            if(!found1){
                freshTable.put(original.getKey(), original.getValue());
            }
        }
    }

    public void findConflicts1( Map<String, Map<String, String>> table, Map<String, Map<String, String>>artifactTable, Map<String, Map<String, String>>freshTable ) {
        for (Map.Entry<String, Map<String, String>> original : table.entrySet()){
            boolean found = false;
            for (Map.Entry<String, Map<String, String>> artifact : artifactTable.entrySet()){
                if(artifact.getKey().equals(original.getKey())){
                    found = true;
                    // Iterate through the fields, checking for the same values. if a value was changed then update 
                    for (Map.Entry<String, String> originalfield : original.getValue().entrySet()){
                        for (Map.Entry<String, String> artifactfield : artifact.getValue().entrySet()){
                            if(artifactfield.getKey().equals(originalfield.getKey())){
                                if(!artifactfield.getValue().equals(originalfield.getValue())){
                                    freshTable.get(original.getKey()).put(originalfield.getKey(), originalfield.getValue());
                                }
                            }   
                        }
                    }
                }
            }
            if(!found){
                freshTable.put(original.getKey(), original.getValue());
            }
        }
    }

    public void getFreshDatabase() {
        Database database = new Database();
        
        database.attributes = formatTable2(getTableJson("Attributes"));

        database.conflicts = formatTable2(getTableJson("Conflicts"));
        
        database.cubeStorage = formatTable1(getTableJson("CubeStorage"));

        database.dayStorage = formatTable2(getTableJson("DayStorage"));
        
        database.equipment = formatTable1(getTableJson("Equipment"));

        database.guestRoster = formatTable2(getTableJson("GuestRoster"));

        database.guests = formatTable1(getTableJson("Guests"));
        
        database.lockers = formatTable2(getTableJson("Lockers"));

        database.unknownItems = formatTable1(getTableJson("UnknownItems"));

        database.waitingList = formatTable1(getTableJson("WaitingList"));

        this.freshDatabase = database;
    }

    public void pull() {
        Database database = new Database();
        
        database.attributes = formatTable2(getTableJson("Attributes"));

        database.conflicts = formatTable2(getTableJson("Conflicts"));
        
        database.cubeStorage = formatTable1(getTableJson("CubeStorage"));

        database.dayStorage = formatTable2(getTableJson("DayStorage"));
        
        database.equipment = formatTable1(getTableJson("Equipment"));

        database.guestRoster = formatTable2(getTableJson("GuestRoster"));

        database.guests = formatTable1(getTableJson("Guests"));
        
        database.lockers = formatTable2(getTableJson("Lockers"));

        database.unknownItems = formatTable1(getTableJson("UnknownItems"));

        database.waitingList = formatTable1(getTableJson("WaitingList"));

        this.database = database;
    }

    public void originalDatabasePull() {
        Database database = new Database();
        
        database.attributes = formatTable2(getTableJson("Attributes"));

        database.conflicts = formatTable2(getTableJson("Conflicts"));
        
        database.cubeStorage = formatTable1(getTableJson("CubeStorage"));

        database.dayStorage = formatTable2(getTableJson("DayStorage"));
        
        database.equipment = formatTable1(getTableJson("Equipment"));

        database.guestRoster = formatTable2(getTableJson("GuestRoster"));

        database.guests = formatTable1(getTableJson("Guests"));
        
        database.lockers = formatTable2(getTableJson("Lockers"));

        database.unknownItems = formatTable1(getTableJson("UnknownItems"));

        database.waitingList = formatTable1(getTableJson("WaitingList"));

        this.database = database;
        this.artifactDatabase = this.database.deepCopy();
        this.freshDatabase = this.database.deepCopy();
    }


    public Map<String, Map<String, Map<String, String>>> formatTable2(String json) {
        
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, Map<String, String>>>>(){}.getType();
        Map<String, Map<String, Map<String, String>>> data = gson.fromJson(json, type);
        return data;
    }

    public Map<String, Map<String, String>> formatTable1(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
        Map<String, Map<String, String>> data = gson.fromJson(json, type);
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
                System.out.println("Failed to fetch client table. Response Code: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    

}
