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
        String client = "HRDC";
        String secret = "GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr";

        // Create an instance of ModuleTemplate
        ModuleTemplate moduleTemplate = new ModuleTemplate(client, secret);

        // Fetch the client's database
        moduleTemplate.getClientDatabase();

        

        moduleTemplate.database.enrollmentForm.get("testEntry1").put("FirstName", "Jane");

        moduleTemplate.pushClientDatabase();
        
        
        

    }

    
}
