package com.adobe.aem.romannumerals.exception;

import com.adobe.aem.romannumerals.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should map missing param to bad request")
    void shouldHandleMissingQueryParam() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("query", "String");

        ResponseEntity<ErrorResponse> response = handler.handleMissingParam(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("query parameter is missing", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should map number format to bad request")
    void shouldHandleNumberFormatException() {
        ResponseEntity<ErrorResponse> response = handler.handleNumberFormat(new NumberFormatException("bad"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Query must be a valid integer", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should map type mismatch to bad request")
    void shouldHandleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("query");

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid value for parameter: query", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should map illegal argument to bad request")
    void shouldHandleIllegalArgument() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException("Query parameter cannot be empty"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Query parameter cannot be empty", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should map malformed body to bad request")
    void shouldHandleMalformedPayload() {
        ResponseEntity<ErrorResponse> response = handler.handleNotReadable(
                new HttpMessageNotReadableException("malformed"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Malformed Query", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Should map unhandled exception to internal server error")
    void shouldHandleGenericException() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
        assertEquals(500, response.getBody().getStatus());
    }
}
