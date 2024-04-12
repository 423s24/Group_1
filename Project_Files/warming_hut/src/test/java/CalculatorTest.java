import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/*
 * THESE TESTS ARE JUST SIMPLE TOY EXAMPLES THAT ARE BEING USED TO
 * SET UP AUTOMATED TESTING AND FOR EXPERIMENTING WITH JUnit 5 FEATURES
 */
class CalculatorTest {

    @Test
    @DisplayName("Add two numbers")
    void add() {
        assertEquals(4, Calculator.add(2,2));
    }

    @Test
    @DisplayName("Multiply two numbers")
    void multiply() {
        assertAll(
                () -> assertEquals(4, Calculator.multiply(2,2)),
                () -> assertEquals( -4, Calculator.multiply(2,-2)),
                () -> assertEquals( 4, Calculator.multiply(-2,-2)),
                () -> assertEquals( 0, Calculator.multiply(1,0)));
    }
}