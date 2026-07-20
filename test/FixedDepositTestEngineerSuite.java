import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Zero-dependency automated unit test suite for Audit Requirement 6.
 *
 * Run from the project root with:
 * javac -d out src/*.java test/*.java
 * java -cp out FixedDepositTestEngineerSuite
 */
public class FixedDepositTestEngineerSuite {
    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        runTest("normal case: calculates simple interest and maturity",
                FixedDepositTestEngineerSuite::testNormalCalculation);
        runTest("boundary case: accepts negative, zero and positive rates",
                FixedDepositTestEngineerSuite::testRateBoundaries);
        runTest("invalid input: retries text, negative and excessive values",
                FixedDepositTestEngineerSuite::testInvalidInputRecovery);
        runTest("name validation: supports common English name formats",
                FixedDepositTestEngineerSuite::testCustomerNameValidation);
        runTest("exception handling: rejects invalid deposit records",
                FixedDepositTestEngineerSuite::testDomainExceptions);
        runTest("interface: supports a replacement interest policy",
                FixedDepositTestEngineerSuite::testReplacementPolicy);
        runTest("interface: calculates annual compound interest",
                FixedDepositTestEngineerSuite::testCompoundInterestPolicy);
        runTest("boundary case: handles compound policy edge cases",
                FixedDepositTestEngineerSuite::testCompoundInterestBoundaries);
        runTest("report: supports long names and very large amounts",
                FixedDepositTestEngineerSuite::testStableReportFormat);

        System.out.println();
        System.out.println("Audit unit test result: "
                + testsPassed + " passed, "
                + (testsRun - testsPassed) + " failed, "
                + testsRun + " total.");

        if (testsPassed != testsRun) {
            System.exit(1);
        }
    }

    private static void testNormalCalculation() {
        FixedDeposit deposit = createDeposit(
                "T001", "Normal Customer", "10000.00", "3.50", "2");

        assertBigDecimalEquals("700.00", deposit.calculateInterest(),
                "interest should use the simple-interest formula");
        assertBigDecimalEquals("10700.00",
                deposit.calculateMaturityAmount(),
                "maturity should include principal and interest");
    }

    private static void testRateBoundaries() {
        FixedDeposit minimumRate = createDeposit(
                "T002", "Minimum Rate", "1000", "-20", "1");
        FixedDeposit zeroRate = createDeposit(
                "T003", "Zero Rate", "1000", "0", "1");
        FixedDeposit maximumRate = createDeposit(
                "T004", "Maximum Rate", "1000", "20", "1");

        assertBigDecimalEquals("-200.00",
                minimumRate.calculateInterest(),
                "negative 20 percent should be accepted");
        assertBigDecimalEquals("0.00", zeroRate.calculateInterest(),
                "zero rate should be accepted");
        assertBigDecimalEquals("200.00", maximumRate.calculateInterest(),
                "20 percent should be accepted as the maximum rate");
    }

    private static void testInvalidInputRecovery() {
        final BigDecimal[] values = new BigDecimal[2];
        final String[] names = new String[1];

        captureOutput(new Runnable() {
            @Override
            public void run() {
                Scanner amountInput =
                        new Scanner("not-a-number\n-100\n0\n1500.25\n");
                values[0] =
                        FixedDepositManagement.readValidatedDecimal(
                                amountInput,
                                "Deposit amount: ",
                                "Deposit amount",
                                BigDecimal.ZERO,
                                false,
                                null);

                Scanner rateInput =
                        new Scanner("-20.01\n1000\n-1.50\n");
                values[1] =
                        FixedDepositManagement.readValidatedDecimal(
                                rateInput,
                                "Annual rate: ",
                                "Annual rate",
                                FixedDeposit.MIN_ANNUAL_INTEREST_RATE,
                                true,
                                FixedDeposit.MAX_ANNUAL_INTEREST_RATE);

                Scanner nameInput = new Scanner(
                        "123\nJohn3\n  Anne-Marie O'Connor  \n");
                names[0] =
                        FixedDepositManagement.readCustomerName(nameInput);
            }
        });

        assertBigDecimalEquals("1500.25", values[0],
                "first valid positive amount should be returned");
        assertBigDecimalEquals("-1.50", values[1],
                "first rate inside -20 to 20 percent should be returned");
        assertEquals("Anne-Marie O'Connor", names[0],
                "invalid names should be retried and valid name trimmed");
    }

    private static void testCustomerNameValidation() {
        assertTrue(FixedDeposit.isValidCustomerName("Alice Tan"),
                "name with spaces should be accepted");
        assertTrue(FixedDeposit.isValidCustomerName(
                        "Anne-Marie O'Connor"),
                "hyphen and apostrophe should be accepted");
        assertTrue(FixedDeposit.isValidCustomerName("J. R. Smith"),
                "English initials should be accepted");
        assertTrue(FixedDeposit.isValidCustomerName("José Álvarez"),
                "accented letters should be accepted");
        assertTrue(FixedDeposit.isValidCustomerName("李明"),
                "Unicode letters should be accepted");
        assertFalse(FixedDeposit.isValidCustomerName("123"),
                "numeric name should be rejected");
        assertFalse(FixedDeposit.isValidCustomerName("John3"),
                "name containing a digit should be rejected");
        assertFalse(FixedDeposit.isValidCustomerName("Alice@Bank"),
                "name containing an unsupported symbol should be rejected");
    }

    private static void testDomainExceptions() {
        assertThrows(new Runnable() {
            @Override
            public void run() {
                createDeposit("T005", "Negative Amount",
                        "-1", "3", "1");
            }
        }, "negative deposit should throw InvalidDepositException");

        assertThrows(new Runnable() {
            @Override
            public void run() {
                createDeposit("T006", "Below Minimum Rate",
                        "1000", "-20.01", "1");
            }
        }, "rate below -20 percent should throw InvalidDepositException");

        assertThrows(new Runnable() {
            @Override
            public void run() {
                createDeposit("T007", "Excessive Rate",
                        "1000", "20.01", "1");
            }
        }, "rate above 20 percent should throw InvalidDepositException");

        assertThrows(new Runnable() {
            @Override
            public void run() {
                createDeposit("T008", "   ",
                        "1000", "3", "1");
            }
        }, "blank customer name should throw InvalidDepositException");

        assertThrows(new Runnable() {
            @Override
            public void run() {
                createDeposit("T009", "123",
                        "1000", "3", "1");
            }
        }, "numeric customer name should throw InvalidDepositException");
    }

    private static void testReplacementPolicy() {
        InterestCalculator auditTestPolicy =
                new InterestCalculator() {
                    @Override
                    public BigDecimal calculateInterest(
                            BigDecimal depositAmount,
                            BigDecimal annualInterestRate,
                            BigDecimal termInYears) {
                        return new BigDecimal("42.00");
                    }
                };

        FixedDeposit deposit = new FixedDeposit(
                "T010",
                "Policy Test",
                new BigDecimal("1000"),
                new BigDecimal("3"),
                new BigDecimal("1"),
                auditTestPolicy);

        assertBigDecimalEquals("42.00", deposit.calculateInterest(),
                "deposit should delegate to the supplied policy");
        assertBigDecimalEquals("1042.00",
                deposit.calculateMaturityAmount(),
                "maturity should include replacement-policy interest");
    }

    private static void testCompoundInterestPolicy() {
        FixedDeposit deposit = new FixedDeposit(
                "T012",
                "Compound Customer",
                new BigDecimal("10000"),
                new BigDecimal("3.5"),
                new BigDecimal("2"),
                new CompoundInterestCalculator());

        assertBigDecimalEquals("712.25", deposit.calculateInterest(),
                "compound interest should be P x (1 + r)^t - P");
        assertBigDecimalEquals("10712.25",
                deposit.calculateMaturityAmount(),
                "compound maturity amount should include compound interest");
    }

    private static void testCompoundInterestBoundaries() {
        InterestCalculator compoundPolicy =
                new CompoundInterestCalculator();
        FixedDeposit zeroRate = new FixedDeposit(
                "T013",
                "Zero Compound",
                new BigDecimal("1000"),
                BigDecimal.ZERO,
                new BigDecimal("3"),
                compoundPolicy);
        FixedDeposit negativeRate = new FixedDeposit(
                "T014",
                "Negative Compound",
                new BigDecimal("1000"),
                new BigDecimal("-10"),
                new BigDecimal("2"),
                compoundPolicy);
        FixedDeposit fractionalTerm = new FixedDeposit(
                "T015",
                "Fractional Compound",
                new BigDecimal("10000"),
                new BigDecimal("3.5"),
                new BigDecimal("0.5"),
                compoundPolicy);

        assertBigDecimalEquals("0.00", zeroRate.calculateInterest(),
                "zero compound rate should produce zero interest");
        assertBigDecimalEquals("-190.00",
                negativeRate.calculateInterest(),
                "negative compound rate should reduce maturity");
        assertBigDecimalEquals("173.49",
                fractionalTerm.calculateInterest(),
                "fractional term should use the compound formula");
    }

    private static void testStableReportFormat() {
        String longName =
                "Alexandria Catherine Tan With An Extremely Long Name";
        FixedDeposit[] deposits = {
                createDeposit(
                        "T011",
                        longName,
                        "123456789012345.67",
                        "0",
                        "1")
        };

        String output = captureOutput(new Runnable() {
            @Override
            public void run() {
                FixedDepositManagement.displayDepositReport(deposits);
            }
        });

        assertContains(output, longName,
                "report should preserve the complete customer name");
        assertContains(output, "RMB 123,456,789,012,345.67",
                "report should format a very large amount safely");
        assertContains(output, "SUMMARY / 汇总",
                "report should include a clear summary section");
    }

    private static FixedDeposit createDeposit(
            String id, String name, String amount,
            String rate, String term) {
        return new FixedDeposit(
                id,
                name,
                new BigDecimal(amount),
                new BigDecimal(rate),
                new BigDecimal(term));
    }

    private static String captureOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }

        return output.toString();
    }

    private static void runTest(String testName, Runnable test) {
        testsRun++;

        try {
            test.run();
            testsPassed++;
            System.out.println("[PASS] " + testName);
        } catch (AssertionError error) {
            System.out.println("[FAIL] " + testName);
            System.out.println("       " + error.getMessage());
        } catch (RuntimeException exception) {
            System.out.println("[FAIL] " + testName);
            System.out.println("       Unexpected exception: "
                    + exception.getMessage());
        }
    }

    private static void assertBigDecimalEquals(
            String expected, BigDecimal actual, String message) {
        BigDecimal expectedValue = new BigDecimal(expected);

        if (expectedValue.compareTo(actual) != 0) {
            throw new AssertionError(message + ". Expected <"
                    + expectedValue.toPlainString() + "> but was <"
                    + actual.toPlainString() + ">.");
        }
    }

    private static void assertEquals(
            String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ". Expected <"
                    + expected + "> but was <" + actual + ">.");
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message + ". Expected <true>.");
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message + ". Expected <false>.");
        }
    }

    private static void assertContains(
            String text, String expectedPart, String message) {
        if (!text.contains(expectedPart)) {
            throw new AssertionError(message + ". Missing <"
                    + expectedPart + ">.");
        }
    }

    private static void assertThrows(Runnable action, String message) {
        try {
            action.run();
        } catch (InvalidDepositException expected) {
            return;
        }

        throw new AssertionError(message
                + ". Expected InvalidDepositException.");
    }
}
