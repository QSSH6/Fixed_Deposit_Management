import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Group 3 - Fixed Deposit Management System
 *
 * Audit hotfix:
 * - Requirement 2: InterestCalculator interface
 * - Requirement 4: Exception handling and safe input validation
 * - Requirement 6: Automated unit tests
 */
public class FixedDepositManagement {
    private static final int CUSTOMER_COUNT = 5;
    private static final String REPORT_SEPARATOR =
            "----------------------------------------------------------------";
    private static final DecimalFormat MONEY_FORMAT = createMoneyFormat();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FixedDeposit[] deposits = new FixedDeposit[CUSTOMER_COUNT];

        try {
            displayWelcomeMessage();
            inputDepositRecords(scanner, deposits);
            displayDepositReport(deposits);
        } catch (NoSuchElementException exception) {
            System.out.println();
            System.out.println(
                    "Input ended. Session closed safely / 输入已结束，系统安全退出。");
        } finally {
            scanner.close();
        }
    }

    public static void displayWelcomeMessage() {
        System.out.println("============================================================");
        System.out.println("       ABC Bank Fixed Deposit Management System");
        System.out.println("             ABC 银行定期存款管理系统");
        System.out.println("============================================================");
        System.out.println("Simple interest formula / 简单利息公式:");
        System.out.println(
                "Interest = Deposit Amount x (Annual Rate / 100) x Term");
        System.out.println("Allowed annual rate / 合理年利率范围: 0% - 20%");
        System.out.println();
    }

    public static void inputDepositRecords(Scanner scanner,
                                           FixedDeposit[] deposits) {
        InterestCalculator interestCalculator =
                new SimpleInterestCalculator();

        for (int i = 0; i < deposits.length; i++) {
            System.out.println("Customer " + (i + 1) + " of "
                    + deposits.length + " / 客户 " + (i + 1));
            System.out.println("------------------------------------------------------------");

            String customerId = "FD" + String.format("%03d", i + 1);
            String customerName = readCustomerName(scanner);
            BigDecimal depositAmount = readValidatedDecimal(
                    scanner,
                    "Deposit amount / 定期存款金额 (RM): ",
                    "Deposit amount / 存款金额",
                    false,
                    null
            );
            BigDecimal annualInterestRate = readValidatedDecimal(
                    scanner,
                    "Annual interest rate / 年利率 (%): ",
                    "Annual interest rate / 年利率",
                    true,
                    FixedDeposit.MAX_ANNUAL_INTEREST_RATE
            );
            BigDecimal termInYears = readValidatedDecimal(
                    scanner,
                    "Term / 存款期限 (years): ",
                    "Term / 存款期限",
                    false,
                    null
            );

            try {
                deposits[i] = new FixedDeposit(
                        customerId,
                        customerName,
                        depositAmount,
                        annualInterestRate,
                        termInYears,
                        interestCalculator
                );
            } catch (InvalidDepositException exception) {
                System.out.println(
                        "Unable to save record / 无法储存资料: "
                                + exception.getMessage());
                i--;
                continue;
            }

            System.out.println("Record saved / 资料已储存: " + customerId);
            System.out.println();
        }
    }

    public static String readCustomerName(Scanner scanner) {
        while (true) {
            System.out.print("Customer name / 客户姓名: ");
            String name = scanner.nextLine();

            try {
                if (name.trim().length() == 0) {
                    throw new InvalidDepositException(
                            "Name cannot be empty / 客户姓名不能为空。");
                }
                return name.trim();
            } catch (InvalidDepositException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    public static BigDecimal readValidatedDecimal(
            Scanner scanner, String prompt, String fieldName,
            boolean allowZero, BigDecimal maximumValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                if (input.length() == 0) {
                    throw new InvalidDepositException(
                            fieldName + " cannot be blank / 输入不能为空。");
                }
                BigDecimal number = new BigDecimal(input);

                if (allowZero
                        && number.compareTo(BigDecimal.ZERO) < 0) {
                    throw new InvalidDepositException(
                            fieldName + " cannot be negative / 不能为负数。");
                }
                if (!allowZero
                        && number.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidDepositException(
                            fieldName + " must be greater than 0 / "
                                    + "必须大于 0。");
                }
                if (maximumValue != null
                        && number.compareTo(maximumValue) > 0) {
                    throw new InvalidDepositException(
                            fieldName + " cannot exceed "
                                    + maximumValue.stripTrailingZeros()
                                    .toPlainString()
                                    + " / 超出合理范围。");
                }

                return number;
            } catch (NumberFormatException exception) {
                System.out.println(
                        "Invalid input. Please enter a number / 输入无效，请输入数字。"
                );
            } catch (InvalidDepositException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    public static void displayDepositReport(FixedDeposit[] deposits) {
        System.out.println();
        System.out.println(REPORT_SEPARATOR);
        System.out.println("FIXED DEPOSIT REPORT / 定期存款报告");
        System.out.println(REPORT_SEPARATOR);

        BigDecimal totalDeposit = BigDecimal.ZERO;
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalMaturityAmount = BigDecimal.ZERO;

        for (int i = 0; i < deposits.length; i++) {
            FixedDeposit deposit = deposits[i];
            BigDecimal interest = deposit.calculateInterest();
            BigDecimal maturityAmount =
                    deposit.calculateMaturityAmount();

            System.out.println("Customer ID / 客户编号: "
                    + deposit.getCustomerId());
            System.out.println("Customer / 客户姓名: "
                    + deposit.getCustomerName());
            System.out.println("Deposit / 定期存款金额: RM "
                    + formatMoney(deposit.getDepositAmount()));
            System.out.println("Annual Rate / 年利率: "
                    + formatPlainNumber(
                    deposit.getAnnualInterestRate()) + "%");
            System.out.println("Term / 存款期限: "
                    + formatPlainNumber(deposit.getTermInYears())
                    + " year(s)");
            System.out.println("Interest / 利息: RM "
                    + formatMoney(interest));
            System.out.println("Maturity / 到期金额: RM "
                    + formatMoney(maturityAmount));
            System.out.println(REPORT_SEPARATOR);

            totalDeposit = totalDeposit.add(
                    deposit.getDepositAmount());
            totalInterest = totalInterest.add(interest);
            totalMaturityAmount = totalMaturityAmount.add(
                    maturityAmount);
        }

        System.out.println("SUMMARY / 汇总");
        System.out.println("Total Deposit / 总存款: RM "
                + formatMoney(totalDeposit));
        System.out.println("Total Interest / 总利息: RM "
                + formatMoney(totalInterest));
        System.out.println("Total Maturity / 总到期金额: RM "
                + formatMoney(totalMaturityAmount));
        System.out.println(REPORT_SEPARATOR);
    }

    /**
     * Retained for compatibility with the original program. The revised
     * multi-line report displays full customer names and no longer needs it.
     */
    public static String shortenName(String name) {
        if (name.length() > 18) {
            return name.substring(0, 15) + "...";
        } else {
            return name;
        }
    }

    public static String formatMoney(BigDecimal amount) {
        return MONEY_FORMAT.format(amount);
    }

    public static String formatPlainNumber(BigDecimal number) {
        return number.stripTrailingZeros().toPlainString();
    }

    private static DecimalFormat createMoneyFormat() {
        DecimalFormatSymbols symbols =
                DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat format =
                new DecimalFormat("#,##0.00", symbols);
        format.setParseBigDecimal(true);
        return format;
    }
}
