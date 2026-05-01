package com.converter.romannumerals.exception;

import com.converter.romannumerals.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
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

    /**
     * Handles missing required request parameters.
     *
     * @param ex the exception thrown by Spring when a required parameter is absent
     * @return 400 Bad Request with a descriptive message
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getParameterName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, MISSING_QUERY_ERR_MSG);
    }

    /**
     * Handles invalid parameter type conversion.
     *
     * @param ex the exception thrown when request parameter cannot be converted
     * @return 400 Bad Request with validation message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Invalid request parameter: {}={}", ex.getName(), ex.getValue());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, INVALID_NUMBER_MESSAGE);
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

    /**
     * Handles {@link InvalidRomanNumeralException} thrown when a request violates
     * domain-specific business rules related to Roman numeral conversion.
     * <p>
     * This includes scenarios such as:
     * <ul>
     *   <li>Input value outside the supported range (e.g., less than 1 or greater than 255)</li>
     *   <li>Invalid range inputs where {@code min >= max}</li>
     * </ul>
     *
     * @param ex the {@link InvalidRomanNumeralException} containing validation details
     * @return {@link ResponseEntity} with HTTP status {@code 400 Bad Request} and a
     *         structured {@link ErrorResponse} body describing the error
     */
    @ExceptionHandler(InvalidRomanNumeralException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRoman(InvalidRomanNumeralException ex) {
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
}