package com.adobe.aem.romannumerals.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RomanNumeralsConstantsTest {

    @Test
    @DisplayName("Constants class should be instantiable")
    void constantsClassShouldBeInstantiable() {
        RomanNumeralsConstants constants = new RomanNumeralsConstants();
        assertNotNull(constants);
    }

    @Test
    @DisplayName("Constants should expose configured values")
    void constantsShouldExposeConfiguredValues() {
        assertEquals(1, RomanNumeralsConstants.MIN_VALUE);
        assertEquals(255, RomanNumeralsConstants.MAX_VALUE);
        assertEquals("Query must be a valid integer", RomanNumeralsConstants.INVALID_NUMBER_MESSAGE);
        assertEquals("Query parameter cannot be empty", RomanNumeralsConstants.EMPTY_QUERY);
        assertEquals("Internal Server Error", RomanNumeralsConstants.INTERNAL_SERVER_ERROR_MESSAGE);
        assertEquals("Malformed Query", RomanNumeralsConstants.MALFORMED_QUERY_MESSAGE);
        assertEquals("Query range must be between 1 and 255", RomanNumeralsConstants.INVALID_RANGE_ERR_MSG);
    }
}
