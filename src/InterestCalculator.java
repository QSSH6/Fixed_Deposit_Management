import java.math.BigDecimal;

/**
 * Separates the bank's interest policy from fixed-deposit customer data.
 * A future policy can be introduced through another implementation.
 */
public interface InterestCalculator {
    BigDecimal calculateInterest(BigDecimal depositAmount,
                                 BigDecimal annualInterestRate,
                                 BigDecimal termInYears);
}
