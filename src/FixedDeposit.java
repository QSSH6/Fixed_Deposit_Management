import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Stores one customer's fixed deposit information and performs
 * interest calculations through a replaceable InterestCalculator policy.
 */
public class FixedDeposit {
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
        if (customerName == null || customerName.trim().length() == 0) {
            throw new InvalidDepositException(
                    "Customer name cannot be blank / 客户姓名不能为空。");
        }
        if (depositAmount == null
                || depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositException(
                    "Deposit amount must be greater than 0 / 存款金额必须大于 0。");
        }
        if (annualInterestRate == null
                || annualInterestRate.compareTo(BigDecimal.ZERO) < 0
                || annualInterestRate.compareTo(
                        MAX_ANNUAL_INTEREST_RATE) > 0) {
            throw new InvalidDepositException(
                    "Annual rate must be between 0% and 20% / "
                            + "年利率必须介于 0% 至 20%。");
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
