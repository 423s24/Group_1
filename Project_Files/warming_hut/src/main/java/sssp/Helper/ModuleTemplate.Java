package sssp.Helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ModuleTemplate{
    //Initialize variables
    private HashMap<String, Object> database;
    private String client;
    private String secret;
    private String endpoint = "clients";

    // class constructor 
    public ModuleTemplate(String client, String secret){
        this.client = client;
        this.secret = secret;
        getClientDatabase();
    }

     // pull from database
     public HashMap<String, Object> getClientDatabase() {
        try {
            // Construct the URL to fetch client's data
            String urlString = "https://hrdc-warming-hut-db-manager-default-rtdb.firebaseio.com/" + client + ".json?auth=" + secret;
    
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
                    HashMap<String, Object> clientData = parseJson(response.toString());
                    this.database = clientData;
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

    public HashMap<String, Object> getLocalClientDatabase(){
        return this.database;
    }
    
    public void getLocalClientDatabase(HashMap<String, Object> database){
        this.database = database;
    }

    
    // Method to parse JSON string into HashMap
    private HashMap<String, Object> parseJson(String json) {
        try {
            // Initialize ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Parse JSON string into HashMap
            HashMap<String, Object> parsedJson = objectMapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});

            return parsedJson;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashMap getData(String Table){

    }
}
