package sssp.Model;

public enum WarningDBKeys {
    DATE("Date", "Date"),
    CONFLICT_ID("ConflictId", "Conflict ID"),
    GUEST_ID("GuestId", "Guest ID"),
    STAFF_INITIALS("StaffInitials", "Staff Initials"),
    NOTES("Notes", "Notes");

    private final String key;
    private final String prettyName;

    WarningDBKeys(String key, String prettyName) {
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
