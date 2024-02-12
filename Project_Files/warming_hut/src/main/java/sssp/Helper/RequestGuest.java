package sssp.Helper;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class RequestGuest extends HttpRequest {

    @Override
    public void makeRequest(){
        try{
            URL url = new URL("https://hrdc-warming-hut-db-manager-default-rtdb.firebaseio.com/testing.json?auth=GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            System.out.println(content);
            con.disconnect();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
