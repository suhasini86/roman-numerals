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

    /**
     * Handles invalid parameter type conversion.
     *
     * @param ex the exception thrown when request parameter cannot be converted
     * @param request originating HTTP request whose parameters determine the error wording
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
                ? String.join(", ", invalidParams) + " values must be valid integers between " + MIN_VALUE + " and " + MAX_VALUE + "."
                : ex.getName() + INVALID_RANGE_ERR_MSG;

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Handles requests missing required servlet parameters implied by routing (e.g. wrong parameter combinations).
     *
     * @param ex unresolved parameter mapping exception from Spring MVC
     * @param request the request that triggered the failure
     * @return 400 Bad Request referring the client to supported parameter shapes
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleUnsatisfiedParams(
            UnsatisfiedServletRequestParameterException ex,
            HttpServletRequest request) {


        log.warn("Unsatisfied request [{}]: {}", request.getRequestURI(), ex.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, INVALID_REQUEST_ERR_MSG);
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
     * Maps domain validation failures from {@link InvalidRequestException} to HTTP 400.
     *
     * @param ex exception carrying a user-readable message describing the violation
     * @return 400 Bad Request with that message in the body
     */
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
        log.error("Unhandled exception occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
    }

    /**
     * Factory for standardized {@link ErrorResponse} payloads with RFC-style reason phrases.
     *
     * @param status HTTP status to return
     * @param message client-facing explanation
     * @return response entity with timestamp populated and uniform JSON shape
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Detects when a servlet parameter carries a blank or non-numeric value intended for integer parsing.
     *
     * @param values typically the singleton array from {@code ServletRequest#getParameterValues}
     * @return {@code true} when absent, blank, or not parseable as an {@code int}
     */
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