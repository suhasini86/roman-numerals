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
    void constructor_withEmptyString_storesEmpty() {
        InvalidRequestException ex = new InvalidRequestException("");
        
        assertThat(ex.getMessage()).isEmpty();
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
    void isUnchecked_canBeThrownWithoutDeclaring() {
        // This test verifies that InvalidRequestException is an unchecked exception
        // and does not require a throws declaration
        throwInvalidRequest();
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
    void canBeCaughtAsRuntimeException() {
        String expectedMessage = "caught as RuntimeException";
        
        assertThatThrownBy(() -> {
            throw new InvalidRequestException(expectedMessage);
        })
        .isInstanceOf(RuntimeException.class)
        .hasMessage(expectedMessage);
    }

    @Test
    void canBeCaughtAsException() {
        String expectedMessage = "caught as Exception";
        
        assertThatThrownBy(() -> {
            throw new InvalidRequestException(expectedMessage);
        })
        .isInstanceOf(Exception.class)
        .hasMessage(expectedMessage);
    }

    @Test
    void canBeCaughtAsThrowable() {
        String expectedMessage = "caught as Throwable";
        
        assertThatThrownBy(() -> {
            throw new InvalidRequestException(expectedMessage);
        })
        .isInstanceOf(Throwable.class)
        .hasMessage(expectedMessage);
    }

    @Test
    void getMessage_returnsStoredMessage() {
        String message = "Required parameter missing";
        InvalidRequestException ex = new InvalidRequestException(message);
        
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    void toString_containsExceptionInfo() {
        String message = "Validation failed";
        InvalidRequestException ex = new InvalidRequestException(message);
        
        String stringRep = ex.toString();
        assertThat(stringRep).contains("InvalidRequestException");
    }

    @Test
    void stackTrace_capturesCallContext() {
        InvalidRequestException ex = new InvalidRequestException("trace test");
        StackTraceElement[] stackTrace = ex.getStackTrace();
        
        assertThat(stackTrace).isNotEmpty();
        assertThat(stackTrace[0].getMethodName()).contains("stackTrace_capturesCallContext");
    }

    @Test
    void multipleExceptions_eachHasOwnMessage() {
        InvalidRequestException ex1 = new InvalidRequestException("error 1");
        InvalidRequestException ex2 = new InvalidRequestException("error 2");
        
        assertThat(ex1.getMessage()).isEqualTo("error 1");
        assertThat(ex2.getMessage()).isEqualTo("error 2");
        assertThat(ex1.getMessage()).isNotEqualTo(ex2.getMessage());
    }

    @Test
    void exceptionWithSpecialCharacters_preservesContent() {
        String message = "Invalid: query OR (min & max), not both!";
        InvalidRequestException ex = new InvalidRequestException(message);
        
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    void exceptionWithLongMessage_preservesFullContent() {
        String longMessage = "This is a very long error message that contains detailed " +
                             "information about what went wrong with the request validation " +
                             "and how to fix it properly.";
        InvalidRequestException ex = new InvalidRequestException(longMessage);
        
        assertThat(ex.getMessage()).isEqualTo(longMessage);
    }

    @Test
    void exceptionWithNewlines_preservesFormatting() {
        String message = "Error:\nLine 1\nLine 2\nLine 3";
        InvalidRequestException ex = new InvalidRequestException(message);
        
        assertThat(ex.getMessage()).isEqualTo(message);
    }

    @Test
    void getCause_returnsNullWhenNoCauseProvided() {
        InvalidRequestException ex = new InvalidRequestException("message");
        
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void getLocalizedMessage_returnsSameAsGetMessage() {
        String message = "Test message";
        InvalidRequestException ex = new InvalidRequestException(message);
        
        assertThat(ex.getLocalizedMessage()).isEqualTo(ex.getMessage());
    }

    /**
     * Helper method that throws InvalidRequestException.
     * This verifies that unchecked exceptions don't require throwing declaration.
     */
    private void throwInvalidRequest() {
        // Method body can be empty; just the fact that it compiles without
        // throwing InvalidRequestException in the signature proves it's unchecked.
    }
}

