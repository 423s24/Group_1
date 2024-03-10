import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicTestTests {

    @Test
    public void shouldMigrateASimpleTest() {
        assertEquals("expected", "expected");
    }
/*    @Test
    @Ignore
    public void shouldMigrateIgnoreTestToDisabledTest() {
// This test is Ignored in JUnit 4 and should be Disabled in JUnit 5"
    }*/
}
