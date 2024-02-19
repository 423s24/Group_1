package sssp.Helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class test {
    public static void main(String[] args) {
        // Provide client and secret
        String client = "your_client_id";
        String secret = "your_secret";

        // Create an instance of ModuleTemplate
        ModuleTemplate moduleTemplate = new ModuleTemplate(client, secret);

        // Fetch the client's database
        moduleTemplate.getClientDatabase();

        HashMap<String, Object> clientDatabase = moduleTemplate.getLocalClientDatabase();

        // Print the output of getLocalClientDatabase()
        System.out.println("Local Client Database:");
        if (clientDatabase != null) {
            for (Map.Entry<String, Object> entry : clientDatabase.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } else {
            System.out.println("Failed to fetch client database.");
        }
    }
}
