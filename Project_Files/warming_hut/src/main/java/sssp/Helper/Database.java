package sssp.Helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Database {
    public Map<String, Map<String, String>> guests;
    public Map<String, Map<String, Map<String, String>>> conflicts;
    public Map<String, Map<String, String>> cubeStorage;
    public Map<String, Map<String, String>> dayStorage;
    public Map<String, Map<String, String>> equipment;
    public Map<String, Map<String, Map<String, String>>> guestRoster;
    //         GuestLockers  Locker_1   AssignedStaff   NA
    public Map<String, Map<String, Map<String, String>>> lockers;

    // need to update deepcopy and printdatabase when additions are made
    

    public void printDatabase() {
        System.out.println("Enrollment Form:");
        printMap(enrollmentForm);
        
        System.out.println("Guests:");
        printMap(guests);
        
        System.out.println("Storage:");
        printMap(storage);
    }

    // Method to perform a deep copy of the entire database
    public Database deepCopy() {
        Database copy = new Database();
        copy.guests = deepCopyMap1(this.guests);
        copy.conflicts = deepCopyMap2(this.conflicts);
        copy.cubeStorage = deepCopyMap1(this.cubeStorage);
        copy.dayStorage = deepCopyMap1(this.dayStorage);
        copy.equipment = deepCopyMap1(this.equipment);
        copy.guestRoster = deepCopyMap2(this.guestRoster);
        copy.lockers = deepCopyMap2(this.lockers);
        return copy;
    }

    // Helper method for deep copying a map
    private Map<String, Map<String, String>> deepCopyMap1(Map<String, Map<String, String>> original) {
        Map<String, Map<String, String>> copy = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return copy;
    }

    // Helper method for deep copying a map
    private Map<String, Map<String, Map<String, String>>> deepCopyMap2(Map<String, Map<String, Map<String, String>>> original) {
        Map<String, Map<String, String>> copy = new HashMap<>();
        for (Map.Entry<String,Map<String, Map<String, String>>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return copy;
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

