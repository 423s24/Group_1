package sssp.Helper;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import sssp.Control.SecretManager;

import java.lang.reflect.Type;
import java.util.concurrent.Future;

import javax.xml.crypto.Data;

public class DBConnectorV2{
    //Initialize variables

    // Initialize databases, database is the real database we work on. ArtifactDatabase is a copy of the database at time of last update, 
    // and is used in the merge methods to check what values are changes 
    // freshdatabase is meant to temporarilly hold new databases when pulled for appraisal and merging. 
    public Database database;
    public Database artifactDatabase;
    public Database freshDatabase;

    // For this use case, client should always be HRDC and secret is the firebase api key
    private String client;
    private String secret;
    private String endpoint = "https://hrdc-warming-hut-db-manager-default-rtdb.firebaseio.com/clients";




    // class constructor 
    public DBConnectorV2(String client, String secret){
        this.client = client;
        this.secret = secret;
        this.database = new Database();
        this.artifactDatabase = null;
        this.freshDatabase = null;
        originalDatabasePull();

        HttpStreamingManagerSingleton.subscribeRunnable("put", this::onDBUpdate);
    }

    // pushes the local database. First calls sortconflicts, and then with a conflict free database it pushes each table individually. 
    // it makes use of the pushTable method to push, and the convertToJson methods to convert the tables from the database before uploading. 
    public boolean push(){

        
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
        pushTable("BunkList", this.convertToJson1(this.database.bunkList));

        return true;
    }

    // push but asynchronous
    public CompletableFuture<Boolean> asyncPush() {
        return CompletableFuture.supplyAsync(() ->{
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
            pushTable("BunkList", this.convertToJson1(this.database.bunkList));

            return true;
        });
    }

    // pulls from the global database, mostly just a wrapper class as sortconflicts does all the work. 
    public boolean pull() {
        sortConflicts();

        return true;
    }

    // pull but asynchronous
    public CompletableFuture<Boolean> asyncPull() {
        return CompletableFuture.supplyAsync(() ->{
            sortConflicts();
            return true;
        });
    }


    // look up value in the a table
    public static Map<String, String> getValuesForKey(Map<String, Map<String, String>> table, String key) {
        if (table.containsKey(key)) {
            return table.get(key);
        } else {
            return null;
        }
    }

    // look up a list of values in a table. 
    public static List<String> getValuesForKeys(Map<String, Map<String, String>> table, List<String> keys) {
        List<String> values = new ArrayList<>();
        for (String key : keys) {
            for (Map.Entry<String, Map<String, String>> entry : table.entrySet()) {
                Map<String, String> innerMap = entry.getValue();
                if (innerMap.containsKey(key)) {
                    values.add(innerMap.get(key));
                }
            }
        }
        return values;
    }

    public static List<Map<String,String>> joinOnKey(Map<String, Map<String, String>> table, String joinColumn, String joinValue)
    {
        List<Map<String,String>> joinedRows = new ArrayList<>();
        for(Map.Entry<String, Map<String, String>> row : table.entrySet())
        {
            if(row.getValue().containsKey(joinColumn) && row.getValue().get(joinColumn).equals(joinValue))
            {
                joinedRows.add(row.getValue());
            }
        }
        return joinedRows;
    }


    // This method is one of the most important methods in this class, sortConflicts begins by pulling a database copy from 
    // firebase, and then based on that. THe current working values in database, and the previous pull from artifactDatabase, it 
    // calculates which values we changed since the last pull, and overwrites the freshdatabase values with those values when a conflict 
    // is found. It then takes this up to date freshdatabase and updates the values of this.database and this.artifactDatabase with a copy of 
    // the freshDatabase. 
    public void sortConflicts(){
        // update Fresh Database from remote
        this.getFreshDatabase();
        

        // merge each table into freshdatabase
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
        this.findConflicts1(this.database.bunkList, this.artifactDatabase.bunkList, this.freshDatabase.bunkList);
        
        // set database and artifact database based on freshdatabase
        this.database.deepReplace(this.freshDatabase);
        this.artifactDatabase = this.database.deepCopy();
        
    }

    // This method, and it's brother findConflicts1 are the methods which do the heavy lifting. The 2 after the method name indicates 
    // that this function works with depth schema 2. This is an indicator of the depth of the table that we are working with. 
    // a depth schema 2 table is a table of tables of tables with values at the bottom. depth schema 1 meanwhile is just a table of tables 
    // with values at the bottom. This method performs 2 main functions. 
    // 1. It finds any null values, and then based on those it removes the entries from the freshtable passed in 
    // 2. It locates any differences from the artifact table and the table, and if the table has changes compared to the artifact table
    //      It then pushes those changes to the freshtable. 
    public void findConflicts2( Map<String, Map<String, Map<String, String>>> table, Map<String, Map<String, Map<String, String>>> artifactTable, Map<String, Map<String, Map<String, String>>> freshTable  ) {
        for (Map.Entry<String, Map<String, Map<String, String>>> original : table.entrySet()){
            if(original.getValue() == null){
                freshTable.remove(original.getKey());
            } else{
                boolean found1 = false;
                for (Map.Entry<String, Map<String, Map<String, String>>> artifact : artifactTable.entrySet()){
                    if(artifact.getKey().equals(original.getKey())){
                        found1 = true;
                        for (Map.Entry<String, Map<String, String>> originalfield1 : original.getValue().entrySet()){
                            if(originalfield1.getValue() == null){
                                freshTable.get(original.getKey()).remove(originalfield1.getKey());
                            } else{
                                boolean found2 = false;
                                for (Map.Entry<String, Map<String, String>> artifactfield1 : artifact.getValue().entrySet()){
                                    if(artifactfield1.getKey().equals(originalfield1.getKey())){
                                        found2 = true;
                                        for (Map.Entry<String, String> originalfield2 : originalfield1.getValue().entrySet()){
                                            if(originalfield2.getValue() == null){
                                                freshTable.get(original.getKey()).get(originalfield1.getKey()).remove(originalfield2.getKey());
                                            } else{
                                                boolean found3 = false;
                                                for (Map.Entry<String, String> artifactfield2 : artifactfield1.getValue().entrySet()){
                                                    if(artifactfield2.getKey().equals(originalfield2.getKey())){
                                                        if(!artifactfield2.getValue().equals(originalfield2.getValue())){
                                                            freshTable.get(original.getKey()).get(originalfield1.getKey()).put(originalfield2.getKey(), originalfield2.getValue());
                                                        }
                                                    }   
                                                }
                                                if(!found3){
                                                    freshTable.get(original.getKey()).get(originalfield1.getKey()).put(originalfield2.getKey(), originalfield2.getValue());
                                                        
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
                }
                if(!found1){
                    freshTable.put(original.getKey(), original.getValue());
                }
            }
        }
    }

    // Much the same as findConflicts2, just works with depth schema 1 instead. 
    public void findConflicts1( Map<String, Map<String, String>> table, Map<String, Map<String, String>>artifactTable, Map<String, Map<String, String>>freshTable ) {
        for (Map.Entry<String, Map<String, String>> original : table.entrySet()){
            if(original.getValue() == null){
                freshTable.remove(original.getKey());
            } else{
                boolean found = false;
                for (Map.Entry<String, Map<String, String>> artifact : artifactTable.entrySet()){
                    if(artifact.getKey().equals(original.getKey())){
                        found = true;
                        for (Map.Entry<String, String> originalfield : original.getValue().entrySet()){
                            if((originalfield.getValue() == null)){
                                freshTable.get(original.getKey()).remove(originalfield.getKey());
                            } else{
                                boolean found2 = false;
                                for (Map.Entry<String, String> artifactfield : artifact.getValue().entrySet()){
                                    if(artifactfield.getKey().equals(originalfield.getKey())){
                                        found2 = true;
                                        if(!artifactfield.getValue().equals(originalfield.getValue())){
                                            freshTable.get(original.getKey()).put(originalfield.getKey(), originalfield.getValue());
                                        }
                                    } 
                                }
                                if(!found2){
                                    freshTable.get(original.getKey()).put(originalfield.getKey(), originalfield.getValue());
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
    }


    // This method converts a table to JSON in the format the firebase database can understand, works on schema depth 2. 
    public String convertToJson2(Map<String, Map<String, Map<String, String>>> table){
        Gson gson = new Gson();
        return gson.toJson(table);
    }

    // This method is the same as convertToJson2 but accepts tables with a schema depth of 1. 
    public String convertToJson1(Map<String, Map<String, String>> table){
        Gson gson = new Gson();
        return gson.toJson(table);
    }

    
    // runs the killoldvalues methods for each table. Follows the universal depth schema using 1 and 2 to represent the two levels of depth
    // This method is not currently in use, and has negligable value. Essentially it globally removes all null values in the database passed in. 
    // As you might imagine, that's somewhat inconvinient because of how sortconflicts processes them. They all get treated as changes made in the 
    // firebase server and merged back in. 
    // could have useful future applications, cancelling deletions, or even potentially being used to help clear out the remote database programatically 
    // as part of another method. Regardless, for the time being, it isn't in use. 
    public void runGarbageCollector(Database database){
        this.killOldValues2(database.attributes);
        this.killOldValues2(database.conflicts);
        this.killOldValues1(database.cubeStorage);
        this.killOldValues2(database.dayStorage);
        this.killOldValues1(database.equipment);
        this.killOldValues2(database.guestRoster);
        this.killOldValues1(database.guests);
        this.killOldValues2(database.lockers);
        this.killOldValues1(database.unknownItems);
        this.killOldValues1(database.waitingList);
        this.killOldValues1(database.bunkList);
    }


    // This method goes through a table, using schema depth 2, and removes any entry with a value equal to null. It is a framework that is used in
    // a handful of cases, largely in the depreciated runGarbageCollector method. However, it does have some potential utility, and additonally, 
    // serves as a respectable template for how to iterate through the table, checking for a value and making changes at that location. 
    // for future development it is likely a more simplified easy to understand template than the more used findConflicts2 method which performs more
    // complicated actions and uses more advanced location criteria. 
    public void killOldValues2( Map<String, Map<String, Map<String, String>>> table) {
        for (Map.Entry<String, Map<String, Map<String, String>>> original : table.entrySet()){
            if(!original.getValue().equals(null)){
                for (Map.Entry<String, Map<String, String>> originalfield1 : original.getValue().entrySet()){
                    if(!originalfield1.getValue().equals(null)){
                        for (Map.Entry<String, String> originalfield2 : originalfield1.getValue().entrySet()){
                            if(originalfield2.getValue().equals(null)){
                                table.get(original.getKey()).get(originalfield1.getKey()).remove(originalfield2.getKey());
                            }
                        }
                    } else {
                        table.get(original.getKey()).remove(originalfield1.getKey());
                    }
                }                    
            } else {
                table.remove(original.getKey());
            }   
        }
    }

    // This method operates much the same as killOldValues2 with the key difference being that it's configured to search tables that match
    // depth schema 1 instead of 2, and is similar to findConflicts1
    public void killOldValues1(  Map<String, Map<String, String>> table) {
        for (Map.Entry<String, Map<String, String>> original : table.entrySet()){
            if(!original.getValue().equals(null)){
                for (Map.Entry<String, String> originalfield1 : original.getValue().entrySet()){
                    if(originalfield1.getValue().equals(null)){
                        table.get(original.getKey()).remove(originalfield1.getKey());
                    } 
                }                    
            } else {
                table.remove(original.getKey());
            }   
        }
    }
    
    // uploads freshtable from firebase
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

        database.bunkList = formatTable1(getTableJson("BunkList"));

        this.freshDatabase = database;
    }


    // uploads all tables from firebase. Used on class initiation. 
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

        database.bunkList = formatTable1(getTableJson("BunkList"));

        database.bunkList = formatTable1(getTableJson("BunkList"));

        this.database.deepReplace(database);
        this.artifactDatabase = this.database.deepCopy();
        this.freshDatabase = this.database.deepCopy();
    }

    // this method breaks json down to the table format for depth schema 2. 
    public Map<String, Map<String, Map<String, String>>> formatTable2(String json) {
        
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, Map<String, String>>>>(){}.getType();
        Map<String, Map<String, Map<String, String>>> data = gson.fromJson(json, type);
        return data;
    }

    // this method breaks json down to the table format for depth schema 1. 
    public Map<String, Map<String, String>> formatTable1(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, String>>>(){}.getType();
        Map<String, Map<String, String>> data = gson.fromJson(json, type);
        return data;
    }

    /**
     * This method checks if the client's secret is valid by sending a GET request to the Firebase Realtime Database.
     * @return boolean indicating whether the client's secret is valid
     */
    public boolean setAndValidateSecret(String secret)
    {
        this.secret = secret;

        try {
            // Construct the URL to fetch client's data
            String urlString = endpoint +"/"+ client + "/.json?auth=" + secret;
    
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
                System.out.println("Connection to Firebase Realtime Database successful.");
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // Response code 401 indicates invalid credentials.
                return false;
            }  
            else {
                // If response code is not success, print error
                System.out.println("Failed to connect to Firebase Realtime Database. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            // Print stack trace if an exception occurs
            e.printStackTrace();
        }

        // Either the secret is valid or an unrelated exception occurred
        return true;
    }

    // This is the code to pull a table from the database. It does not account for schema depth, and just kicks back the raw JSON associated with 
    // the table. 
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
                // Response code 401 indicates invalid credentials. Re-fetch secret and retry
                if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    SecretManager.invalidateSecret();
                    SecretManager.invalidSecretPopup();
                    this.secret = SecretManager.getDBSecret();

                    // Retry
                    return getTableJson(table);
                }

                // If response code is not success, print error
                System.out.println("Failed to fetch client table. Response Code: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    // This is the code to push a table to the database. The table depth schema does not matter, but it must be read in as JSON processed 
    // using the convertToJson methods. It also needs the table name to push
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
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // Response code 401 indicates invalid credentials. Re-fetch secret and retry
                SecretManager.invalidateSecret();
                SecretManager.invalidSecretPopup();
                this.secret = SecretManager.getDBSecret();

                // Retry
                pushTable(table, json);
            } else {
                // If response code indicates failure, print error message
                System.out.println("Failed to push local table to Firebase Realtime Database. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            // Print stack trace if an exception occurs
            e.printStackTrace();
        }
    }

    List<Runnable> subscribers = new ArrayList<Runnable>();

    public void subscribeRunnableToDBUpdate(Runnable r){
        subscribers.add(r);
    }

    private void notifyDBUpdate(){
        for(Runnable r : subscribers){
            r.run();
        }
    }

    private void onDBUpdate()
    {
        this.pull();
        this.notifyDBUpdate();
    }
}
