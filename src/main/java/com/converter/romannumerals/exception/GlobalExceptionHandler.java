package com.converter.romannumerals.exception;

import com.converter.romannumerals.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.converter.romannumerals.constants.RomanNumeralsConstants.*;

/**
 * Global exception handler that intercepts exceptions thrown across all
 * {@code @RestController} classes and maps them to structured JSON error responses.
 * <p>
 * Each handler method converts a specific exception type into an appropriate
 * HTTP status code and builds {@link ErrorResponse} body.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//    /**
//     * Handles missing required request parameters.
//     *
//     * @param ex the exception thrown by Spring when a required parameter is absent
//     * @return 400 Bad Request with a descriptive message
//     */
//    @ExceptionHandler(MissingServletRequestParameterException.class)
//    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex, HttpServletRequest request) {
//        log.warn("Missing request parameter: {}", ex.getParameterName());
//
//        //Collect all parameters whose value is empty or missing
//        List<String> emptyParams = request.getParameterMap().entrySet().stream()
//                .filter(entry -> {
//
//                    String[] values = entry.getValue();
//                    return values == null || values.length == 0
//                            || (values.length == 1  && (values[0] == null || values[0].isBlank()));
//                    }).map(Map.Entry::getKey)
//                .sorted()
//                .toList();
//
//        String message;
//        if (emptyParams.size() > 1) {
//            message = emptyParams.stream().map( p -> "'" + p + "'")
//                    .collect(Collectors.joining(", "))
//                    + " must not be empty";
//        } else {
//            message =  ex.getParameterName() + " must not be empty";
//        }
//
//        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
//    }

    /**
     * Handles invalid parameter type conversion.
     *
     * @param ex the exception thrown when request parameter cannot be converted
     * @return 400 Bad Request with validation message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.warn("Invalid request parameter: {}={}", ex.getName(), ex.getValue());

        List<String> invalidParams = request.getParameterMap().entrySet().stream()
                .filter(entry -> isInvalidInteger(entry.getValue()))
                .map(Map.Entry::getKey)
                .sorted()
                .toList();

        String message = invalidParams.size() > 1
                ? String.join(", ", invalidParams) + " must be valid integers between 1 and 3999"
                : ex.getName() + " must be a valid integer between 1 and 3999";

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleUnsatisfiedParams(
            UnsatisfiedServletRequestParameterException ex,
            HttpServletRequest request) {

        String message =
                "Invalid request. Use query or (min and max) but not both" ;

        log.warn("Unsatisfied request [{}]: {}", request.getRequestURI(), message);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Handles Jakarta Bean Validation constraint violations.
     *
     * @param ex validation exception containing constraint violations
     * @return 400 Bad Request with validation message
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        log.warn("Constraint violation: {}", message);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Handles requests to unmapped URLs.
     *
     * @param ex no handler found exception
     * @return 404 Not Found
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        log.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return buildErrorResponse(HttpStatus.NOT_FOUND, NOT_FOUND_MESSAGE);
    }

    /**
     * Handles unsupported HTTP methods.
     *
     * @param ex HTTP request method not supported exception
     * @return 405 Method Not Allowed
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        log.warn("HTTP method not supported: {}", ex.getMethod());
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, METHOD_NOT_ALLOWED_MESSAGE);
    }


    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    /**
     * Handles all unhandled exceptions.
     *
     * @param ex generic exception
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception occurred", ex.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );

        return ResponseEntity.status(status).body(errorResponse);
    }
    private boolean isInvalidInteger(String[] values) {

        if (values == null || values.length == 0) {
            return false;
        }

        String value = values[0];

        if (value == null || value.isBlank()) {
            return true;
        }

        try {
            Integer.parseInt(value.trim());
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}