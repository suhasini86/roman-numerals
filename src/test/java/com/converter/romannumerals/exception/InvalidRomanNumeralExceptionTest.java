package com.converter.romannumerals.exception;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InvalidRomanNumeralException}.
 *
 */

class InvalidRomanNumeralExceptionTest {

    /**
     * Verifies that {@link InvalidRomanNumeralException} can be instantiated with a custom message
     * and that it extends {@link RuntimeException}.
     * <p>
     * This test ensures the exception properly handles service-layer validation failures in the Roman numeral converter,
     * such as invalid input ranges or unsupported values, allowing the {@link GlobalExceptionHandler} to return
     * appropriate 400 Bad Request responses to clients.
     */
    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Invalid Roman numeral input";

        InvalidRomanNumeralException ex =
                new InvalidRomanNumeralException(message);

        assertEquals(message, ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}
