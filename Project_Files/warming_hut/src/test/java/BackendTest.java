import static org.junit.Assert.*;
import org.junit.*;
import org.junit.jupiter.api.Test;

import sssp.Helper.DBConnectorV2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackendTest {
    public DBConnectorV2 connector;
    public String client = "HRDC";
    public String secret = "GHODuRVY3N2t2VfSzaEMEvVXN3iETl6pF6MeMXzr";

    @Test
    public void testPush() {
        connector = new DBConnectorV2(client, secret);
        connector.pull();
        String uniqueName = UUID.randomUUID().toString();
        
        assertNull(connector.database.unknownItems.get(uniqueName));
        connector.database.unknownItems.put(uniqueName, new HashMap<>());
        connector.database.unknownItems.get(uniqueName).put(uniqueName, "VALUE");
        connector.push();
        connector.pull();
        assertEquals("VALUE", connector.database.unknownItems.get(uniqueName).get(uniqueName));
        
    }

    

    @Test
    public void testFormatTable2() {
        connector = new DBConnectorV2(client, secret);
        String json = "{\"key1\":{\"subkey1\":{\"value1\":\"data1\"}},\"key2\":{\"subkey2\":{\"value2\":\"data2\"}}}";
        Map<String, Map<String, Map<String, String>>> table = connector.formatTable2(json);
        assertNotNull(table);
        assertTrue(table.containsKey("key1"));
        assertTrue(table.containsKey("key2"));
        assertEquals("data1", table.get("key1").get("subkey1").get("value1"));
        assertEquals("data2", table.get("key2").get("subkey2").get("value2"));
    }

    @Test
    public void testFormatTable1() {
        connector = new DBConnectorV2(client, secret);
        String json = "{\"key1\":{\"value1\":\"data1\"},\"key2\":{\"value2\":\"data2\"}}";
        Map<String, Map<String, String>> table = connector.formatTable1(json);
        assertNotNull(table);
        assertTrue(table.containsKey("key1"));
        assertTrue(table.containsKey("key2"));
        assertEquals("data1", table.get("key1").get("value1"));
        assertEquals("data2", table.get("key2").get("value2"));
    }
    @Test
    public void testConvertToJson2() {
        connector = new DBConnectorV2(client, secret);
        Map<String, Map<String, Map<String, String>>> table = Map.of(
            "key1", Map.of("subkey1", Map.of("value1", "data1")),
            "key2", Map.of("subkey2", Map.of("value2", "data2"))
        );
        String json = connector.convertToJson2(table);
        assertNotNull(json);
        assertEquals("{\"key2\":{\"subkey2\":{\"value2\":\"data2\"}},\"key1\":{\"subkey1\":{\"value1\":\"data1\"}}}", json);
    }

    @Test
    public void testConvertToJson1() {
        connector = new DBConnectorV2(client, secret);
        Map<String, Map<String, String>> table = Map.of(
            "key1", Map.of("value1", "data1"),
            "key2", Map.of("value2", "data2")
        );
        String json = connector.convertToJson1(table);
        assertNotNull(json);
        assertEquals("{\"key2\":{\"value2\":\"data2\"},\"key1\":{\"value1\":\"data1\"}}", json);
    }

    

    
}
