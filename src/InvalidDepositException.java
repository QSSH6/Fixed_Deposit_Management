/**
 * Reports invalid fixed-deposit data without terminating the application.
 */
public class InvalidDepositException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public InvalidDepositException(String message) {
        super(message);
    }
}
