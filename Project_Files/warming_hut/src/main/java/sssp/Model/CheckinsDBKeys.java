package sssp.Model;

public enum CheckinsDBKeys {
    GUEST_ID("GuestId"),
    DATE("Date"),
    EMERGENCY_SHELTER("EmergencyShelter"),
    LAUNDRY("Laundry"),
    SERVICES_ONLY("ServicesOnly"),
    CASEWORTHY_ENTERED("CaseworthyInfoEnteredInDB"),
    HMIS_ENTERED("HMISInfoEnteredInDB"),
    SHOULD_DISPLAY("ShouldDisplay");

    private final String key;

    CheckinsDBKeys(String key) { this.key = key; }

    public String getKey() {
        return key;
    }
}
