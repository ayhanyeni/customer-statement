package nl.rabobank.assignment.exception;

public class InvalidStatementDataException extends Exception {

    public InvalidStatementDataException() {}

    public InvalidStatementDataException(final String message) {
        super(message);
    }
}
