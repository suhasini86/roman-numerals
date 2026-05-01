package com.adobe.aem.romannumerals.exception;

import com.adobe.aem.romannumerals.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Set;

import static com.adobe.aem.romannumerals.constants.RomanNumeralsConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 * <p>
 * Verifies that each exception type is mapped to the correct HTTP status and message.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should map missing param to 400 Bad Request")
    void shouldHandleMissingQueryParam() throws MissingServletRequestParameterException {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("query", "String");

        ResponseEntity<ErrorResponse> response = handler.handleMissingParam(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(MISSING_QUERY_ERR_MSG, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should map type mismatch to 400 Bad Request")
    void shouldHandleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("query");
        when(ex.getValue()).thenReturn("abc");

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(INVALID_NUMBER_MESSAGE, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should map constraint violation to 400 Bad Request")
    void shouldHandleConstraintViolation() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn(INVALID_RANGE_ERR_MSG);

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(INVALID_RANGE_ERR_MSG, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should map illegal argument to 400 Bad Request")
    void shouldHandleIllegalArgument() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException(EMPTY_QUERY));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(EMPTY_QUERY, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should map method not supported to 405")
    void shouldHandleMethodNotAllowed() {
        HttpRequestMethodNotSupportedException ex =
                new HttpRequestMethodNotSupportedException("POST");

        ResponseEntity<ErrorResponse> response = handler.handleMethodNotAllowed(ex);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(METHOD_NOT_ALLOWED_MESSAGE, response.getBody().getMessage());
        assertEquals(405, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Should map no handler found to 404")
    void shouldHandleNotFound() {
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/dfgdfgdf",
                new org.springframework.http.HttpHeaders());

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(NOT_FOUND_MESSAGE, response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Should map unhandled exception to 500 Internal Server Error")
    void shouldHandleGenericException() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(INTERNAL_SERVER_ERROR_MESSAGE, response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Should map InvalidRomanNumeralException to 400 Bad Request")
    void shouldHandleInvalidRomanNumeralException() {
        InvalidRomanNumeralException ex =
                new InvalidRomanNumeralException("Invalid range");

        ResponseEntity<ErrorResponse> response =
                handler.handleInvalidRoman(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Invalid range", response.getBody().getMessage());
    }

}