package sssp.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class DBHelper {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
    private static final DBConnectorV2 connector = DBConnectorV2Singleton.getInstance();
    private final Database db = connector.database;

    public String[] getCurrentRosterAndAttributeUUIDS(){
        Date newestDate = null;
        String newestRosterKey = null;
        Map<String, Map<String, String>> rosterAttributes = db.attributes.get("GuestRoster");
        for(String rosterDataKey : rosterAttributes.keySet()){
            try {
                Date rosterDate = simpleDateFormat.parse(rosterAttributes.get(rosterDataKey).get("Date"));
                if(newestDate == null || rosterDate.after(newestDate)){
                    newestDate = rosterDate;
                    newestRosterKey = rosterDataKey;
                }
            } catch (ParseException parseException){
                //TODO Solve exception
            }
        }
       return new String[] { rosterAttributes.get(newestRosterKey).get("Roster"), newestRosterKey};
    }
}
