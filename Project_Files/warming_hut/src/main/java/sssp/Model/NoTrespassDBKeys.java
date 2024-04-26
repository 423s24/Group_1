package sssp.Model;

public enum NoTrespassDBKeys {
    GUEST_ID("GuestId", "Guest ID"),
    CONFLICT_ID("ConflictId", "Conflict ID"),
    DATE_OF_INCIDENT("DateOfIncident", "Date of Incident"),
    NO_TRESPASS_FROM("NoTrespassFrom", "No Trespass From"),
    BPD_STATUS("StatusBPD", "BPD Status"),
    STAFF_INITIALS("StaffInitials", "Staff Initials"),
    LPD_STATUS("StatusLPD", "LPD Status"),
    CW_ALERT("AlertToCW", "CW Alert"),
    NOTES("Notes", "Notes");

    private final String key;
    private final String prettyName;

    NoTrespassDBKeys(String key, String prettyName) {
        this.key = key;
        this.prettyName = prettyName;
    }

    public String getKey() {
        return key;
    }

    public String getPrettyName() {
        return prettyName;
    }
}
