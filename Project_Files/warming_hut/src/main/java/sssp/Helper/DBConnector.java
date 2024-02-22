package sssp.Helper;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class DBConnector {
    //Initialize variables
    public Database syncdatabase;
    public Database database;
    public Database originalPull;
    private String client;
    private String secret;
    private String endpoint = "https://hrdc-warming-hut-db-manager-default-rtdb.firebaseio.com/clients";

    // class constructor 
    public DBConnector(String client, String secret){
        this.client = client;
        this.secret = secret;
        getClientDatabase();
    }

     // pull from database
     public Database getClientDatabase() {
        try {
            // Construct the URL to fetch client's data
            String urlString = endpoint +"/"+ client + ".json?auth=" + secret;
    
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
                    // Parse JSON response
                    Database clientData = parseJson(response.toString());
                    this.database = clientData.deepCopy();
                    this.syncdatabase = clientData.deepCopy();
                    this.originalPull = clientData.deepCopy();
                    return clientData;
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

    // pull from database
    public Database getClientSyncDatabase() {
        try {
            // Construct the URL to fetch client's data
            String urlString = endpoint +"/"+ client + ".json?auth=" + secret;
    
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
                    // Parse JSON response
                    Database clientData = parseJson(response.toString());
                    this.syncdatabase = clientData.deepCopy();
                    return clientData;
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

    public void pushClientDatabase() {
        try {
            // Construct the URL to push data to the Firebase Realtime Database
            String urlString = endpoint + "/" + client + ".json?auth=" + secret;

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

            // Convert the locally stored database to JSON string
            String json = databaseToJson();

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
                    // Print response
                    System.out.println("Server response: " + response.toString());
                }
                System.out.println("Local database successfully pushed to Firebase Realtime Database.");
            } else {
                // If response code indicates failure, print error message
                System.out.println("Failed to push local database to Firebase Realtime Database. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            // Print stack trace if an exception occurs
            e.printStackTrace();
        }
    }

    public Database getLocalClientDatabase(){
        this.database = this.syncdatabase.deepCopy();
        return this.database;
    }
    
    public void setLocalClientDatabase(Database database){
        this.database = database.deepCopy();
    }

    
    // Standalone method to parse JSON string into HashMap
    public Database parseJson(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Map<String, Map<String, Map<String, String>>>>>(){}.getType();
        Map<String, Map<String, Map<String, Map<String, String>>>> data = gson.fromJson(json, type);

        Database database = new Database();

        Map<String,Map<String,Map<String,String>>> tables = data.get("tables");

        database.enrollmentForm = tables.get("Enrollment Form");
        database.guests = tables.get("Guests");
        database.storage = tables.get("Storage");

        return database;
    }

    // Standalone method to convert Database object to JSON string
    public String databaseToJson() {
        getClientSyncDatabase();

        
        Database dbchanges = syncdatabase.deepCopy();
        //TODO: update dbchanges with each change between originalpull and syncdatabse

        // iterate though the entries in enrollmentForm if a new entry is found, then add it, otherwise check the entry for consistency
        for (Map.Entry<String, Map<String, String>> local : database.enrollmentForm.entrySet()){
            boolean found = false;
            for (Map.Entry<String, Map<String, String>> original : originalPull.enrollmentForm.entrySet()){
                if(local.getKey() == original.getKey()){
                    found = true;
                    // Iterate through the fields, checking for the same values. if a value was changed then update 
                    for (Map.Entry<String, String> localfield : local.getValue().entrySet()){
                        for (Map.Entry<String, String> originalfield : original.getValue().entrySet()){
                            if(localfield.getKey() == originalfield.getKey()){
                                if(localfield.getValue() != originalfield.getValue()){
                                    System.out.println("\n\nPROBLEM IDENTIFIED\n");
                                    System.out.println(localfield.getKey());
                                    System.out.println(localfield.getValue());
                                    System.out.println(originalfield.getKey());
                                    System.out.println(originalfield.getValue());
                                    System.out.println("\n////////////////////\n\n");
                                    dbchanges.enrollmentForm.get(local.getKey()).put(localfield.getKey(), localfield.getValue());
                                }
                            }   
                        }
                    }
                }
            }
            if(found == false){
                dbchanges.enrollmentForm.put(local.getKey(), local.getValue());
            }
            
        }
        for (Map.Entry<String, Map<String, String>> local : database.guests.entrySet()){
            boolean found = false;
            for (Map.Entry<String, Map<String, String>> original : originalPull.guests.entrySet()){
                if(local.getKey() == original.getKey()){
                    found = true;
                    // Iterate through the fields, checking for the same values. if a value was changed then update 
                    for (Map.Entry<String, String> localfield : local.getValue().entrySet()){
                        for (Map.Entry<String, String> originalfield : original.getValue().entrySet()){
                            if(localfield.getKey() == originalfield.getKey()){
                                if(localfield.getValue() != originalfield.getValue()){
                                    System.out.println("\n\nPROBLEM IDENTIFIED\n");
                                    System.out.println(localfield.getKey());
                                    System.out.println(localfield.getValue());
                                    System.out.println(originalfield.getKey());
                                    System.out.println(originalfield.getValue());
                                    System.out.println("\n////////////////////\n\n");
                                    dbchanges.enrollmentForm.get(local.getKey()).put(localfield.getKey(), localfield.getValue());
                                }
                            }
                        }
                    }
                }
            }
            if(found == false){
                dbchanges.guests.put(local.getKey(), local.getValue());
            }
            
        }
        for (Map.Entry<String, Map<String, String>> local : database.storage.entrySet()){
            boolean found = false;
            for (Map.Entry<String, Map<String, String>> original : originalPull.storage.entrySet()){
                if(local.getKey() == original.getKey()){
                    found = true;
                    // Iterate through the fields, checking for the same values. if a value was changed then update 
                    for (Map.Entry<String, String> localfield : local.getValue().entrySet()){
                        for (Map.Entry<String, String> originalfield : original.getValue().entrySet()){
                            if(localfield.getKey() == originalfield.getKey()){
                                if(localfield.getValue() != originalfield.getValue()){
                                    System.out.println("\n\nPROBLEM IDENTIFIED\n");
                                    System.out.println(localfield.getKey());
                                    System.out.println(localfield.getValue());
                                    System.out.println(originalfield.getKey());
                                    System.out.println(originalfield.getValue());
                                    System.out.println("\n////////////////////\n\n");
                                    dbchanges.enrollmentForm.get(local.getKey()).put(localfield.getKey(), localfield.getValue());
                                }
                            }
                        }
                    }
                }
            }
            if(found == false){
                dbchanges.storage.put(local.getKey(), local.getValue());
            }
            
        }


        originalPull = dbchanges.deepCopy();
        database = dbchanges.deepCopy();

        Map<String, Map<String, Map<String, String>>> tables = new HashMap<>();
        tables.put("Enrollment Form", database.enrollmentForm);
        tables.put("Guests", database.guests);
        tables.put("Storage", database.storage);

        Map<String, Map<String, Map<String, Map<String, String>>>> data = new HashMap<>();
        data.put("tables", tables);

        Gson gson = new Gson();
        return gson.toJson(data);
    }
    
    

}