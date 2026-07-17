import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Stores one customer's fixed deposit information and performs
 * interest calculations through a replaceable InterestCalculator policy.
 */
public class FixedDeposit {
    public static final BigDecimal MIN_ANNUAL_INTEREST_RATE =
            new BigDecimal("-20.00");
    public static final BigDecimal MAX_ANNUAL_INTEREST_RATE =
            new BigDecimal("20.00");

    private final String customerId;
    private final String customerName;
    private final BigDecimal depositAmount;
    private final BigDecimal annualInterestRate;
    private final BigDecimal termInYears;
    private final InterestCalculator interestCalculator;

    /**
     * Keeps the original simple-interest behaviour for normal callers.
     */
    public FixedDeposit(String customerId, String customerName,
                        BigDecimal depositAmount,
                        BigDecimal annualInterestRate,
                        BigDecimal termInYears) {
        this(customerId, customerName, depositAmount, annualInterestRate,
                termInYears, new SimpleInterestCalculator());
    }

    /**
     * Allows the bank to replace its interest policy without changing this
     * customer data class.
     */
    public FixedDeposit(String customerId, String customerName,
                        BigDecimal depositAmount,
                        BigDecimal annualInterestRate,
                        BigDecimal termInYears,
                        InterestCalculator interestCalculator) {
        validate(customerId, customerName, depositAmount,
                annualInterestRate, termInYears, interestCalculator);

        this.customerId = customerId.trim();
        this.customerName = customerName.trim();
        this.depositAmount = depositAmount.setScale(2, RoundingMode.HALF_UP);
        this.annualInterestRate = annualInterestRate;
        this.termInYears = termInYears;
        this.interestCalculator = interestCalculator;
    }

    private void validate(String customerId, String customerName,
                          BigDecimal depositAmount,
                          BigDecimal annualInterestRate,
                          BigDecimal termInYears,
                          InterestCalculator interestCalculator) {
        if (customerId == null || customerId.trim().length() == 0) {
            throw new InvalidDepositException(
                    "Customer ID cannot be blank / 客户编号不能为空。");
        }
        if (!isValidCustomerName(customerName)) {
            throw new InvalidDepositException(
                    "Customer name contains invalid characters / "
                            + "客户姓名格式无效。");
        }
        if (depositAmount == null
                || depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositException(
                    "Deposit amount must be greater than 0 / 存款金额必须大于 0。");
        }
        if (annualInterestRate == null
                || annualInterestRate.compareTo(
                        MIN_ANNUAL_INTEREST_RATE) < 0
                || annualInterestRate.compareTo(
                        MAX_ANNUAL_INTEREST_RATE) > 0) {
            throw new InvalidDepositException(
                    "Annual rate must be between -20% and 20% / "
                            + "年利率必须介于 -20% 至 20%。");
        }
        if (termInYears == null
                || termInYears.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositException(
                    "Term must be greater than 0 / 存款期限必须大于 0。");
        }
        if (interestCalculator == null) {
            throw new InvalidDepositException(
                    "Interest calculator is required / 必须提供利息计算规则。");
        }
    }

    /**
     * Accepts letters from different languages and common English-name
     * separators, such as spaces, hyphens, apostrophes and initial periods.
     * Digits and other symbols are rejected.
     */
    public static boolean isValidCustomerName(String customerName) {
        if (customerName == null) {
            return false;
        }

        String name = customerName.trim();
        if (name.length() == 0 || !Character.isLetter(name.charAt(0))) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            char character = name.charAt(i);

            if (Character.isLetter(character)) {
                continue;
            }

            if (character == ' ') {
                if (i == 0 || i == name.length() - 1
                        || name.charAt(i - 1) == ' '
                        || name.charAt(i + 1) == ' ') {
                    return false;
                }
                continue;
            }

            if (character == '-' || character == '\''
                    || character == '\u2019') {
                if (i == 0 || i == name.length() - 1
                        || !Character.isLetter(name.charAt(i - 1))
                        || !Character.isLetter(name.charAt(i + 1))) {
                    return false;
                }
                continue;
            }

            if (character == '.') {
                if (i == 0
                        || !Character.isLetter(name.charAt(i - 1))
                        || (i < name.length() - 1
                        && name.charAt(i + 1) != ' ')) {
                    return false;
                }
                continue;
            }

            return false;
        }

        return true;
    }

    public BigDecimal calculateInterest() {
        return interestCalculator.calculateInterest(
                depositAmount, annualInterestRate, termInYears);
    }

    public BigDecimal calculateMaturityAmount() {
        return depositAmount.add(calculateInterest())
                .setScale(2, RoundingMode.HALF_UP);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    public BigDecimal getTermInYears() {
        return termInYears;
    }
}
