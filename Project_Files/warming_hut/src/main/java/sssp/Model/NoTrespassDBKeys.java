package sssp.Model;

public enum NoTrespassDBKeys {
    DATE_OF_INCIDENT("DateOfIncident", "Date of Incident"),
    NO_TRESPASS_FROM("NoTrespassFrom", "No Trespass From"),
    BPD_STATUS("BPDStatus", "BPD Status"),
    STAFF_INITIALS("StaffInitials", "Staff Initials"),
    LBD_STATUS("LBDStatus", "LBD Status"),
    CW_ALERT("CWAlert", "CW Alert"),
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
