import java.util.Scanner;

/**
 * Group 3 - Fixed Deposit Management System
 *
 * This program deliberately uses only the fundamental Java features allowed
 * by the assignment: variables, Scanner, if/else, loops, arrays, classes,
 * objects and methods.
 */
public class FixedDepositManagement {
    private static final int CUSTOMER_COUNT = 5;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FixedDeposit[] deposits = new FixedDeposit[CUSTOMER_COUNT];

        displayWelcomeMessage();
        inputDepositRecords(scanner, deposits);
        displayDepositReport(deposits);

        scanner.close();
    }

    public static void displayWelcomeMessage() {
        System.out.println("============================================================");
        System.out.println("       ABC Bank Fixed Deposit Management System");
        System.out.println("             ABC 银行定期存款管理系统");
        System.out.println("============================================================");
        System.out.println("Simple interest formula / 简单利息公式:");
        System.out.println("Interest = Deposit Amount x Annual Rate x Term");
        System.out.println();
    }

    public static void inputDepositRecords(Scanner scanner,
                                           FixedDeposit[] deposits) {
        for (int i = 0; i < deposits.length; i++) {
            System.out.println("Customer " + (i + 1) + " of "
                    + deposits.length + " / 客户 " + (i + 1));
            System.out.println("------------------------------------------------------------");

            String customerId = "FD" + String.format("%03d", i + 1);
            String customerName = readCustomerName(scanner);
            double depositAmount = readPositiveNumber(
                    scanner,
                    "Deposit amount / 定期存款金额 (RM): ",
                    false
            );
            double annualInterestRate = readPositiveNumber(
                    scanner,
                    "Annual interest rate / 年利率 (%): ",
                    true
            );
            double termInYears = readPositiveNumber(
                    scanner,
                    "Term / 存款期限 (years): ",
                    false
            );

            deposits[i] = new FixedDeposit(
                    customerId,
                    customerName,
                    depositAmount,
                    annualInterestRate,
                    termInYears
            );

            // Clear the newline left by nextDouble before reading the next name.
            scanner.nextLine();
            System.out.println("Record saved / 资料已储存: " + customerId);
            System.out.println();
        }
    }

    public static String readCustomerName(Scanner scanner) {
        System.out.print("Customer name / 客户姓名: ");
        String name = scanner.nextLine();

        while (name.trim().length() == 0) {
            System.out.println("Name cannot be empty / 客户姓名不能为空。");
            System.out.print("Customer name / 客户姓名: ");
            name = scanner.nextLine();
        }

        return name.trim();
    }

    public static double readPositiveNumber(Scanner scanner, String prompt,
                                            boolean allowZero) {
        double number = -1;
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);

            if (scanner.hasNextDouble()) {
                number = scanner.nextDouble();

                if (allowZero && number >= 0) {
                    valid = true;
                } else if (!allowZero && number > 0) {
                    valid = true;
                } else {
                    System.out.println(
                            "Please enter a valid positive value / 请输入有效的正数。"
                    );
                }
            } else {
                System.out.println(
                        "Invalid input. Please enter a number / 输入无效，请输入数字。"
                );
                scanner.next();
            }
        }

        return number;
    }

    public static void displayDepositReport(FixedDeposit[] deposits) {
        System.out.println();
        System.out.println("===============================================================================================");
        System.out.println("FIXED DEPOSIT REPORT / 定期存款报告");
        System.out.println("===============================================================================================");
        System.out.printf("%-6s %-18s %14s %9s %9s %14s %17s%n",
                "ID", "Customer", "Deposit (RM)", "Rate", "Years",
                "Interest (RM)", "Maturity (RM)");
        System.out.println("-----------------------------------------------------------------------------------------------");

        double totalDeposit = 0;
        double totalInterest = 0;
        double totalMaturityAmount = 0;

        for (int i = 0; i < deposits.length; i++) {
            FixedDeposit deposit = deposits[i];
            double interest = deposit.calculateInterest();
            double maturityAmount = deposit.calculateMaturityAmount();

            System.out.printf("%-6s %-18s %,14.2f %8.2f%% %9.2f %,14.2f %,17.2f%n",
                    deposit.getCustomerId(),
                    shortenName(deposit.getCustomerName()),
                    deposit.getDepositAmount(),
                    deposit.getAnnualInterestRate(),
                    deposit.getTermInYears(),
                    interest,
                    maturityAmount);

            totalDeposit = totalDeposit + deposit.getDepositAmount();
            totalInterest = totalInterest + interest;
            totalMaturityAmount = totalMaturityAmount + maturityAmount;
        }

        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.printf("%-25s %,14.2f %19s %,14.2f %,17.2f%n",
                "TOTAL / 总计", totalDeposit, "", totalInterest,
                totalMaturityAmount);
        System.out.println("===============================================================================================");
    }

    public static String shortenName(String name) {
        if (name.length() > 18) {
            return name.substring(0, 15) + "...";
        } else {
            return name;
        }
    }
}
