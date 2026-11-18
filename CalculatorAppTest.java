public final class CalculatorAppTest {
    private CalculatorAppTest() {}

    public static void main(String[] args) {
        assertClose(14, CalculatorApp.evaluateExpression("2 + 3 * 4"));
        assertClose(20, CalculatorApp.evaluateExpression("(2 + 3) * 4"));
        assertClose(-2.5, CalculatorApp.evaluateExpression("-5 / 2"));
        assertClose(0.5, CalculatorApp.evaluateExpression("1 / 2"));

        try {
            CalculatorApp.evaluateExpression("1 / 0");
            throw new AssertionError("Expected division by zero to fail");
        } catch (ArithmeticException expected) {
            // Expected.
        }

        System.out.println("Calculator expression tests passed.");
    }

    private static void assertClose(double expected, double actual) {
        if (Math.abs(expected - actual) > 1e-10) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
}