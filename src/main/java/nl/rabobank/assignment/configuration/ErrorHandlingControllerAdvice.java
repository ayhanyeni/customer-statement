package nl.rabobank.assignment.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.assignment.exception.InvalidFileException;
import nl.rabobank.assignment.exception.InvalidStatementDataException;
import nl.rabobank.assignment.exception.ProcessingFailedException;
import nl.rabobank.assignment.exception.ProcessingNotReadyException;
import nl.rabobank.assignment.exception.ItemNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(Throwable.class)
    ResponseEntity<String> defaultExceptionHandler(
        final Throwable ex,
        final HttpServletRequest request,
        final HttpServletResponse response) {

        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        log.error(getStackTrace(ex));

        return new ResponseEntity<>("errorOccured", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<String> handleException(
        final DataIntegrityViolationException ex,
        final HttpServletRequest request,
        final HttpServletResponse response) {

        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        log.error(getStackTrace(ex));

        return new ResponseEntity<>("dataIntegrityViolation", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    ResponseEntity<String> handleException(
            final ItemNotFoundException ex,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        log.error(getStackTrace(ex));

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidStatementDataException.class)
    ResponseEntity<String> handleException(
            final InvalidStatementDataException ex,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        log.error(getStackTrace(ex));

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFileException.class)
    ResponseEntity<String> handleException(
            final InvalidFileException ex,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        log.error(getStackTrace(ex));

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ProcessingNotReadyException.class})
    ResponseEntity<String> handleException(
            final ProcessingNotReadyException ex,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.ACCEPTED);
    }

    @ExceptionHandler({ProcessingFailedException.class})
    ResponseEntity<String> handleException(
            final ProcessingFailedException ex,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        response.setContentType(MediaType.TEXT_PLAIN_VALUE);

        log.error(getStackTrace(ex));

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<String> handleException(
            final AccessDeniedException ex,
            final HttpServletRequest request,
            final HttpServletResponse response) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    private String getStackTrace(final Throwable t) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
