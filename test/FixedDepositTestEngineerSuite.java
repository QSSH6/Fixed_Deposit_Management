import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Manual test suite owned by the test engineer.
 *
 * Run from the project root with:
 * javac -d out src/*.java test/*.java
 * java -cp out FixedDepositTestEngineerSuite
 */
public class FixedDepositTestEngineerSuite {
    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        runTest("calculates simple interest and maturity amount",
                FixedDepositTestEngineerSuite::testInterestAndMaturityAmount);
        runTest("keeps original fixed deposit customer data",
                FixedDepositTestEngineerSuite::testCustomerDataIsStored);
        runTest("accepts zero when reading interest rate",
                FixedDepositTestEngineerSuite::testReadPositiveNumberAllowsZeroRate);
        runTest("rejects invalid and non-positive deposit amounts",
                FixedDepositTestEngineerSuite::testReadPositiveNumberRejectsInvalidDeposit);
        runTest("trims customer name after blank retry",
                FixedDepositTestEngineerSuite::testReadCustomerNameRetriesBlankInput);
        runTest("shortens long customer names for report display",
                FixedDepositTestEngineerSuite::testShortenName);
        runTest("prints report totals for test engineer sample data",
                FixedDepositTestEngineerSuite::testDisplayDepositReportTotals);

        System.out.println();
        System.out.println("Test engineer suite result: "
                + testsPassed + " passed, "
                + (testsRun - testsPassed) + " failed, "
                + testsRun + " total.");

        if (testsPassed != testsRun) {
            System.exit(1);
        }
    }

    private static void testInterestAndMaturityAmount() {
        FixedDeposit deposit = new FixedDeposit(
                "T001", "Test Engineer", 10000.00, 3.50, 2.00);

        assertDoubleEquals(700.00, deposit.calculateInterest(), 0.001,
                "interest should use simple interest formula");
        assertDoubleEquals(10700.00, deposit.calculateMaturityAmount(), 0.001,
                "maturity amount should include principal and interest");
    }

    private static void testCustomerDataIsStored() {
        FixedDeposit deposit = new FixedDeposit(
                "T002", "QA Analyst", 2500.00, 4.25, 1.50);

        assertEquals("T002", deposit.getCustomerId(), "customer id");
        assertEquals("QA Analyst", deposit.getCustomerName(), "customer name");
        assertDoubleEquals(2500.00, deposit.getDepositAmount(), 0.001,
                "deposit amount");
        assertDoubleEquals(4.25, deposit.getAnnualInterestRate(), 0.001,
                "annual interest rate");
        assertDoubleEquals(1.50, deposit.getTermInYears(), 0.001,
                "term in years");
    }

    private static void testReadPositiveNumberAllowsZeroRate() {
        Scanner scanner = new Scanner("0\n");

        double value = FixedDepositManagement.readPositiveNumber(
                scanner, "Annual interest rate / 年利率 (%): ", true);

        assertDoubleEquals(0.00, value, 0.001,
                "zero interest rate should be accepted when allowZero is true");
    }

    private static void testReadPositiveNumberRejectsInvalidDeposit() {
        Scanner scanner = new Scanner("abc\n0\n-5\n1500\n");

        double value = FixedDepositManagement.readPositiveNumber(
                scanner, "Deposit amount / 定期存款金额: ", false);

        assertDoubleEquals(1500.00, value, 0.001,
                "deposit amount should accept the first valid positive number");
    }

    private static void testReadCustomerNameRetriesBlankInput() {
        Scanner scanner = new Scanner("   \n  Alice Tan  \n");

        String name = FixedDepositManagement.readCustomerName(scanner);

        assertEquals("Alice Tan", name,
                "customer name should be retried and trimmed");
    }

    private static void testShortenName() {
        assertEquals("Short Name",
                FixedDepositManagement.shortenName("Short Name"),
                "short names should stay unchanged");
        assertEquals("Alexandria Cath...",
                FixedDepositManagement.shortenName("Alexandria Catherine Tan"),
                "long names should be shortened to fit report column");
    }

    private static void testDisplayDepositReportTotals() {
        FixedDeposit[] deposits = {
                new FixedDeposit("T101", "Tester One", 1000.00, 5.00, 1.00),
                new FixedDeposit("T102", "Tester Two", 2000.00, 2.50, 2.00)
        };

        String output = captureOutput(new Runnable() {
            @Override
            public void run() {
                FixedDepositManagement.displayDepositReport(deposits);
            }
        });

        assertContains(output, "FIXED DEPOSIT REPORT",
                "report title should be printed");
        assertContains(output, "TOTAL / 总计",
                "report total row should be printed");
        assertContains(output, "3,000.00",
                "total deposit should be 3,000.00");
        assertContains(output, "150.00",
                "total interest should be 150.00");
        assertContains(output, "3,150.00",
                "total maturity amount should be 3,150.00");
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
        }
    }

    private static void assertEquals(String expected, String actual,
                                     String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ". Expected <"
                    + expected + "> but was <" + actual + ">.");
        }
    }

    private static void assertDoubleEquals(double expected, double actual,
                                           double tolerance, String message) {
        if (Math.abs(expected - actual) > tolerance) {
            throw new AssertionError(message + ". Expected <"
                    + expected + "> but was <" + actual + ">.");
        }
    }

    private static void assertContains(String text, String expectedPart,
                                       String message) {
        if (!text.contains(expectedPart)) {
            throw new AssertionError(message + ". Missing <"
                    + expectedPart + ">.");
        }
    }
}
