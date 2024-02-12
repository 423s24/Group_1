package sssp.Helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public abstract class HttpRequestBuilder {

    private final String SECRET = "GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr";

    public void getRequest(String path){
        try{

            URL url = new URL(String.format("https://hrdc-warming-hut-db-manager-default-rtdb.firebaseio.com/%s.json?auth=%s", path, SECRET));
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

    public void postRequest(String path, HashMap<String, String> data){
        try{

            URL url = new URL(String.format("https://hrdc-warming-hut-db-manager-default-rtdb.firebaseio.com/%s/.json?auth=%s", path, SECRET));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);

            byte[] out = mapToJSON(data).getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            con.setFixedLengthStreamingMode(length);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            con.connect();
            try(OutputStream os = con.getOutputStream()) {
                os.write(out);
            }

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

    protected String mapToJSON(HashMap<String, String> data){
        StringBuilder JSON = new StringBuilder().append("{");
        for (String key : data.keySet()) {
            String value = data.get(key);
            if(value.charAt(0) == '{' && value.charAt(value.length()-1) == '}'){ //if the value is a JSON object do not surround with double quotes.
                JSON.append(String.format("\"%s\":%s,", key, data.get(key)));
            } else { //if the value is not a json object treat it like a string and surround it with double quotes.
                JSON.append(String.format("\"%s\":\"%s\",", key, data.get(key)));
            }
        }
        JSON.deleteCharAt(JSON.length()-1).append("}");
        return JSON.toString();
    }
}
