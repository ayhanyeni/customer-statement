package nl.rabobank.assignment.exception;

public class ProcessingNotReadyException extends RuntimeException {

    public ProcessingNotReadyException() {}

    public ProcessingNotReadyException(final String message) {
        super(message);
    }
}
