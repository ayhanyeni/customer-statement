package nl.rabobank.assignment.exception;

public class InvalidFileException extends RuntimeException {

    public InvalidFileException() {}

    public InvalidFileException(final String message) {
        super(message);
    }
}
