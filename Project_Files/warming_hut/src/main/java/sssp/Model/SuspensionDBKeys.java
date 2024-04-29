package sssp.Model;

public enum SuspensionDBKeys {
    GUEST_ID("GuestId", "Guest ID"),
    ISSUING_DATE("IssuingDate", "Issuing Date"),
    EXPIRY_DATE("ExpiryDate", "Expiry Date"),
    SERVICE_SUSPENDED("SuspendedFrom", "Suspended From"),
    STAFF_INITIALS("StaffInitials", "Staff Initials"),
    NOTES("Notes", "Notes");

    private final String key;
    private final String prettyName;

    SuspensionDBKeys(String key, String prettyName) {
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