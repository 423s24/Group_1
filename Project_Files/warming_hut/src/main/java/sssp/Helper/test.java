package sssp.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;


public class test {
    public static void main(String[] args) {
        // Provide client and secret
        String client = "HRDC";
        String secret = "GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr";

        // Create an instance of ModuleTemplate
        DBConnectorV2 test = new DBConnectorV2(client, secret);

        System.out.println(test.convertToJson2(test.database.attributes));

        test.database.attributes.get("GuestRoster").get("Roster_1").put("Notes", "Today was a good day!");

        System.out.println(test.convertToJson2(test.database.attributes));
        System.out.println("BREAK");
        test.push();
    }
}
