package sssp.Model;

import java.util.Map;

public class RosterData {
    private String Date;
    private Map<String, RosterGuest> Guests;
    private String StaffNotes;
    public String getStaffNotes() { return StaffNotes; }
}

class RosterGuest {
    private String BunkAssigned;
    private String Laundry;
    private String Services;
}
