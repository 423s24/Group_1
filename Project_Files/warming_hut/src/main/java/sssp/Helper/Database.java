package sssp.Helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Database {
    public Map<String, Map<String, Map<String, String>>> attributes;
    public Map<String, Map<String, Map<String, String>>> conflicts;
    public Map<String, Map<String, String>> cubeStorage;
    public Map<String, Map<String, Map<String, String>>> dayStorage;
    public Map<String, Map<String, String>> equipment;
    public Map<String, Map<String, Map<String, String>>> guestRoster;
    public Map<String, Map<String, String>> guests;
    public Map<String, Map<String, Map<String, String>>> lockers;
    public Map<String, Map<String, String>> unknownItems;
    public Map<String, Map<String, String>> waitingList;
    
    
    

    public void print() {
        if (this != null) {
            System.out.println("\nAttributes:");
            printMap2(this.attributes);
    
            System.out.println("\nConflicts:");
            printMap2(this.conflicts);
    
            System.out.println("\nCube Storage:");
            printMap1(this.cubeStorage);
    
            System.out.println("\nDay Storage:");
            printMap2(this.dayStorage);
    
            System.out.println("\nEquipment:");
            printMap1(this.equipment);
    
            System.out.println("\nGuest Roster:");
            printMap2(this.guestRoster);
            
            System.out.println("Guests:");
            printMap1(this.guests);

            System.out.println("\nLockers:");
            printMap2(this.lockers);
    
            System.out.println("\nUnknown Items:");
            printMap1(this.unknownItems);
    
            System.out.println("\nWaiting List:");
            printMap1(this.waitingList);
        }
    }

    // Method to perform a deep copy of the entire database
    public Database deepCopy() {
        Database copy = new Database();
        copy.attributes = deepCopyMap2(this.attributes);
        copy.conflicts = deepCopyMap2(this.conflicts);
        copy.cubeStorage = deepCopyMap1(this.cubeStorage);
        copy.dayStorage = deepCopyMap2(this.dayStorage);
        copy.equipment = deepCopyMap1(this.equipment);
        copy.guestRoster = deepCopyMap2(this.guestRoster);
        copy.guests = deepCopyMap1(this.guests);
        copy.lockers = deepCopyMap2(this.lockers);
        copy.unknownItems = deepCopyMap1(this.unknownItems);
        copy.waitingList = deepCopyMap1(this.waitingList);
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
        Map<String, Map<String, Map<String, String>>> copy = new HashMap<>();
    
        for (Map.Entry<String, Map<String, Map<String, String>>> entry : original.entrySet()) {
            Map<String, Map<String, String>> innerMap = new HashMap<>();
    
            for (Map.Entry<String, Map<String, String>> innerEntry : entry.getValue().entrySet()) {
                Map<String, String> innermostMap = new HashMap<>(innerEntry.getValue());
                innerMap.put(innerEntry.getKey(), innermostMap);
            }
    
            copy.put(entry.getKey(), innerMap);
        }
    
        return copy;
    }
    
    // Helper method to print a map to the console
    private void printMap1(Map<String, Map<String, String>> map) {
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

    private void printMap2(Map<String, Map<String, Map<String, String>>> map) {
        if (map != null) {
            for (Map.Entry<String, Map<String, Map<String, String>>> entry : map.entrySet()) {
                System.out.println(entry.getKey() + ":");
                Map<String, Map<String, String>> innerMap = entry.getValue();
                if (innerMap != null) {
                    for (Map.Entry<String, Map<String, String>> innerEntry : innerMap.entrySet()) {
                        System.out.println("  " + innerEntry.getKey() + ":");
                        Map<String, String> innermostMap = innerEntry.getValue();
                        if (innermostMap != null) {
                            for (Map.Entry<String, String> innermostEntry : innermostMap.entrySet()) {
                                System.out.println("    " + innermostEntry.getKey() + ": " + innermostEntry.getValue());
                            }
                        }
                    }
                }
            }
        }
    }


}

