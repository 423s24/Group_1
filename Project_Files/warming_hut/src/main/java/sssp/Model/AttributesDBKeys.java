package sssp.Model;

public enum AttributesDBKeys {
    CHECK_INS("Checkins");

    private final String key;

    AttributesDBKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
