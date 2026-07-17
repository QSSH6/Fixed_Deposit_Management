import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculates interest using the bank's current simple-interest policy.
 */
public class SimpleInterestCalculator implements InterestCalculator {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Override
    public BigDecimal calculateInterest(BigDecimal depositAmount,
                                        BigDecimal annualInterestRate,
                                        BigDecimal termInYears) {
        return depositAmount
                .multiply(annualInterestRate)
                .multiply(termInYears)
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }
}
