package com.adobe.aem.romannumerals.exception;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidRomanNumeralExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "Invalid Roman numeral input";

        InvalidRomanNumeralException ex =
                new InvalidRomanNumeralException(message);

        assertEquals(message, ex.getMessage());
        assertTrue(ex instanceof RuntimeException);
    }
}
