package nl.rabobank.assignment.exception;

public class ProcessingFailedException extends RuntimeException {

    public ProcessingFailedException() {}

    public ProcessingFailedException(final String message) {
        super(message);
    }
}
