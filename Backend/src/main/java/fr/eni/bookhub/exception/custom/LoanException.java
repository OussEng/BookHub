package fr.eni.bookhub.exception.custom;

public class LoanException extends RuntimeException {
    public LoanException(String message) {
        super(message);
    }
}
