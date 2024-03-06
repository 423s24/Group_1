package sssp.Helper;
import java.util.UUID;
public class UUIDGenerator {
    public static String getNewUUID() {
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        return uuidAsString;
    }
}
