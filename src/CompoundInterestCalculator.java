import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Calculates interest using annual compound interest.
 *
 * Formula:
 * interest = principal × (1 + annualRate / 100) ^ years - principal
 */
public class CompoundInterestCalculator implements InterestCalculator {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    /**
     * Keep sufficient precision during intermediate calculations.
     */
    private static final MathContext MATH_CONTEXT =
            new MathContext(16, RoundingMode.HALF_UP);

    @Override
    public BigDecimal calculateInterest(BigDecimal depositAmount,
                                        BigDecimal annualInterestRate,
                                        BigDecimal termInYears) {
        validateParameters(depositAmount, annualInterestRate, termInYears);

        int years = termInYears.intValueExact();

        BigDecimal rate = annualInterestRate.divide(
                ONE_HUNDRED,
                MATH_CONTEXT
        );

        BigDecimal compoundFactor = BigDecimal.ONE
                .add(rate)
                .pow(years, MATH_CONTEXT);

        return depositAmount
                .multiply(compoundFactor, MATH_CONTEXT)
                .subtract(depositAmount)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void validateParameters(BigDecimal depositAmount,
                                    BigDecimal annualInterestRate,
                                    BigDecimal termInYears) {
        if (depositAmount == null
                || annualInterestRate == null
                || termInYears == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        }

        if (depositAmount.signum() < 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must not be negative."
            );
        }

        if (annualInterestRate.signum() < 0) {
            throw new IllegalArgumentException(
                    "Annual interest rate must not be negative."
            );
        }

        if (termInYears.signum() < 0) {
            throw new IllegalArgumentException(
                    "Term in years must not be negative."
            );
        }

        try {
            termInYears.intValueExact();
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "Annual compounding requires a whole-number term in years.",
                    exception
            );
        }
    }
}