package com.converter.romannumerals.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the {@link InvalidRequestException} custom exception class.
 * Tests validate that the exception properly stores and retrieves error messages,
 * inherits from RuntimeException, and can be thrown and caught correctly.
 */
class InvalidRequestExceptionTest {

    @Test
    void constructor_withMessage_storesMessage() {
        String message = "Invalid request parameters";
        InvalidRequestException ex = new InvalidRequestException(message);
        
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    void constructor_withNull_storesNull() {
        InvalidRequestException ex = new InvalidRequestException(null);
        
        assertThat(ex.getMessage()).isNull();
    }

    @Test
    void extendsRuntimeException() {
        InvalidRequestException ex = new InvalidRequestException("test");
        
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void canBeThrownAndCaught() {
        String expectedMessage = "test error message";
        
        assertThatThrownBy(() -> {
            throw new InvalidRequestException(expectedMessage);
        })
        .isInstanceOf(InvalidRequestException.class)
        .hasMessage(expectedMessage);
    }

    @Test
    void getCause_returnsNullWhenNoCauseProvided() {
        InvalidRequestException ex = new InvalidRequestException("message");
        
        assertThat(ex.getCause()).isNull();
    }

}

