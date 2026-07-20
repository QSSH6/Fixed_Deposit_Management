import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Calculates interest using annual compound interest.
 *
 * Formula:
 * interest = principal x (1 + annualRate / 100) ^ years - principal
 */
public class CompoundInterestCalculator implements InterestCalculator {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal NEGATIVE_ONE_HUNDRED =
            new BigDecimal("-100");
    private static final MathContext MATH_CONTEXT =
            new MathContext(16, RoundingMode.HALF_UP);

    @Override
    public BigDecimal calculateInterest(BigDecimal depositAmount,
                                        BigDecimal annualInterestRate,
                                        BigDecimal termInYears) {
        validateParameters(
                depositAmount, annualInterestRate, termInYears);

        BigDecimal annualGrowthFactor = BigDecimal.ONE.add(
                annualInterestRate.divide(ONE_HUNDRED, MATH_CONTEXT)
        );
        BigDecimal maturityAmount;

        if (termInYears.stripTrailingZeros().scale() <= 0) {
            int years = termInYears.intValueExact();
            maturityAmount = depositAmount.multiply(
                    annualGrowthFactor.pow(years, MATH_CONTEXT),
                    MATH_CONTEXT
            );
        } else {
            double growth = Math.pow(
                    annualGrowthFactor.doubleValue(),
                    termInYears.doubleValue()
            );
            maturityAmount = depositAmount.multiply(
                    BigDecimal.valueOf(growth),
                    MATH_CONTEXT
            );
        }

        return maturityAmount.subtract(depositAmount)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void validateParameters(BigDecimal depositAmount,
                                    BigDecimal annualInterestRate,
                                    BigDecimal termInYears) {
        if (depositAmount == null
                || annualInterestRate == null
                || termInYears == null) {
            throw new IllegalArgumentException(
                    "Parameters must not be null.");
        }
        if (depositAmount.signum() < 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must not be negative.");
        }
        if (annualInterestRate.compareTo(
                NEGATIVE_ONE_HUNDRED) <= 0) {
            throw new IllegalArgumentException(
                    "Annual interest rate must be greater than -100%.");
        }
        if (termInYears.signum() < 0) {
            throw new IllegalArgumentException(
                    "Term in years must not be negative.");
        }
    }
}
