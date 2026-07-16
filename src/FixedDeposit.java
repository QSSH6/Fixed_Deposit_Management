/**
 * Stores one customer's fixed deposit information and performs
 * the simple-interest calculations.
 */
public class FixedDeposit {
    private String customerId;
    private String customerName;
    private double depositAmount;
    private double annualInterestRate;
    private double termInYears;

    public FixedDeposit(String customerId, String customerName,
                        double depositAmount, double annualInterestRate,
                        double termInYears) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.depositAmount = depositAmount;
        this.annualInterestRate = annualInterestRate;
        this.termInYears = termInYears;
    }

    public double calculateInterest() {
        return depositAmount * (annualInterestRate / 100) * termInYears;
    }

    public double calculateMaturityAmount() {
        return depositAmount + calculateInterest();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public double getTermInYears() {
        return termInYears;
    }
}
