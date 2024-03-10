import java.util.stream.DoubleStream;

/*
 * THIS CLASS IS JUST A SIMPLE TOY EXAMPLE THAT IS BEING USED TO
 * SET UP AUTOMATED TESTING AND HAVE TWO SIMPLE METHODS TO EXPERIMENT
 * WRITING JUnit TESTS AGAINST
 */
public class Calculator {

    static double add(double... operands) {
        return DoubleStream.of(operands)
                .sum();
    }

    static double multiply(double... operands) {
        return DoubleStream.of(operands)
                .reduce(1, (a, b) -> a * b);
    }
}
