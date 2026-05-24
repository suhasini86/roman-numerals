package com.converter.romannumerals.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/romannumeral");
    }

    @Test
    void handleTypeMismatch_buildsBadRequest() {
        MethodArgumentTypeMismatchException ex = Mockito.mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("query");
        when(ex.getValue()).thenReturn("abc");

        // single invalid param
        request.addParameter("query", "abc");

        var resp = handler.handleTypeMismatch(ex, request);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().message()).containsIgnoringCase("valid integer");
    }

    @Test
    void handleTypeMismatch_multipleInvalidParams_buildsCombinedMessage() {
        MethodArgumentTypeMismatchException ex = Mockito.mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("query");
        when(ex.getValue()).thenReturn("abc");

        // add two invalid parameters to request
        request.addParameter("a", "x");
        request.addParameter("b", "y");

        var resp = handler.handleTypeMismatch(ex, request);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().message()).contains("a, b");
    }

    @Test
    void handleUnsatisfiedParams_returnsBadRequest() {
        UnsatisfiedServletRequestParameterException ex = Mockito.mock(UnsatisfiedServletRequestParameterException.class);
        var resp = handler.handleUnsatisfiedParams(ex, request);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().message()).contains("Invalid request");
    }

    @Test
    void handleConstraintViolation_returnsBadRequest() {
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("must be between 1 and 3999");
        ConstraintViolationException ex = new ConstraintViolationException(Collections.singleton(violation));

        var resp = handler.handleConstraintViolation(ex);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().message()).contains("must be between");
    }

    @Test
    void handleNotFound_returns404() throws Exception {
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/foo", null);
        var resp = handler.handleNotFound(ex);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody().message()).containsIgnoringCase("not found");
    }

    @Test
    void handleMethodNotAllowed_returns405() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");
        var resp = handler.handleMethodNotAllowed(ex);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(resp.getBody().message()).containsIgnoringCase("HTTP method not supported");
    }

    @Test
    void handleInvalidRequest_returnsBadRequest() {
        InvalidRequestException ex = new InvalidRequestException("bad params");
        var resp = handler.handleInvalidRequest(ex);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().message()).isEqualTo("bad params");
    }

    @Test
    void handleGeneric_returnsInternalServerError() {
        Exception ex = new RuntimeException("boom");
        var resp = handler.handleGeneric(ex);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody().message()).containsIgnoringCase("Internal Server Error");
    }

    @Test
    void isInvalidInteger_variousInputs() throws Exception {
        var method = GlobalExceptionHandler.class.getDeclaredMethod("isInvalidInteger", String[].class);
        method.setAccessible(true);

        // null -> false
        Boolean r1 = (Boolean) method.invoke(handler, new Object[]{null});
        assertThat(r1).isFalse();

        // empty array -> false
        Boolean r2 = (Boolean) method.invoke(handler, new Object[]{new String[0]});
        assertThat(r2).isFalse();

        // blank value -> true
        Boolean r3 = (Boolean) method.invoke(handler, new Object[]{new String[]{"   "}});
        assertThat(r3).isTrue();

        // non-numeric -> true
        Boolean r4 = (Boolean) method.invoke(handler, new Object[]{new String[]{"abc"}});
        assertThat(r4).isTrue();

        // numeric -> false
        Boolean r5 = (Boolean) method.invoke(handler, new Object[]{new String[]{"42"}});
        assertThat(r5).isFalse();
    }
}

