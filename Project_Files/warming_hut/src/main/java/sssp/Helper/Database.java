package sssp.Helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Database {
    public Map<String, Map<String, String>> enrollmentForm;
    public Map<String, Map<String, String>> guests;
    public Map<String, Map<String, String>> storage;

    public void printDatabase() {
        System.out.println("Enrollment Form:");
        printMap(enrollmentForm);
        
        System.out.println("Guests:");
        printMap(guests);
        
        System.out.println("Storage:");
        printMap(storage);
    }

    // Helper method to print a map to the console
    private void printMap(Map<String, Map<String, String>> map) {
        if (map != null) {
            for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
                System.out.println(entry.getKey() + ":");
                Map<String, String> innerMap = entry.getValue();
                if (innerMap != null) {
                    for (Map.Entry<String, String> innerEntry : innerMap.entrySet()) {
                        System.out.println("  " + innerEntry.getKey() + ": " + innerEntry.getValue());
                    }
                }
            }
        }
    }
}

