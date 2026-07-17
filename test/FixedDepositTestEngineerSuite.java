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
        runTest("boundary case: accepts zero and maximum annual rates",
                FixedDepositTestEngineerSuite::testRateBoundaries);
        runTest("invalid input: retries text, negative and excessive values",
                FixedDepositTestEngineerSuite::testInvalidInputRecovery);
        runTest("exception handling: rejects invalid deposit records",
                FixedDepositTestEngineerSuite::testDomainExceptions);
        runTest("interface: supports a replacement interest policy",
                FixedDepositTestEngineerSuite::testReplacementPolicy);
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
        FixedDeposit zeroRate = createDeposit(
                "T002", "Zero Rate", "1000", "0", "1");
        FixedDeposit maximumRate = createDeposit(
                "T003", "Maximum Rate", "1000", "20", "1");

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
                                false,
                                null);

                Scanner rateInput =
                        new Scanner("-1\n1000\n3.75\n");
                values[1] =
                        FixedDepositManagement.readValidatedDecimal(
                                rateInput,
                                "Annual rate: ",
                                "Annual rate",
                                true,
                                FixedDeposit.MAX_ANNUAL_INTEREST_RATE);

                Scanner nameInput =
                        new Scanner("   \n  Alice Tan  \n");
                names[0] =
                        FixedDepositManagement.readCustomerName(nameInput);
            }
        });

        assertBigDecimalEquals("1500.25", values[0],
                "first valid positive amount should be returned");
        assertBigDecimalEquals("3.75", values[1],
                "first rate inside 0 to 20 percent should be returned");
        assertEquals("Alice Tan", names[0],
                "blank name should be retried and valid name trimmed");
    }

    private static void testDomainExceptions() {
        assertThrows(new Runnable() {
            @Override
            public void run() {
                createDeposit("T004", "Negative Amount",
                        "-1", "3", "1");
            }
        }, "negative deposit should throw InvalidDepositException");

        assertThrows(new Runnable() {
            @Override
            public void run() {
                createDeposit("T005", "Negative Rate",
                        "1000", "-0.01", "1");
            }
        }, "negative rate should throw InvalidDepositException");

        assertThrows(new Runnable() {
            @Override
            public void run() {
                createDeposit("T006", "Excessive Rate",
                        "1000", "20.01", "1");
            }
        }, "rate above 20 percent should throw InvalidDepositException");

        assertThrows(new Runnable() {
            @Override
            public void run() {
                createDeposit("T007", "   ",
                        "1000", "3", "1");
            }
        }, "blank customer name should throw InvalidDepositException");
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
                "T008",
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

    private static void testStableReportFormat() {
        String longName =
                "Alexandria Catherine Tan With An Extremely Long Name";
        FixedDeposit[] deposits = {
                createDeposit(
                        "T009",
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
        assertContains(output, "RM 123,456,789,012,345.67",
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
