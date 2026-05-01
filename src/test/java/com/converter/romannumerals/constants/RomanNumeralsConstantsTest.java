package com.converter.romannumerals.constants;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class RomanNumeralsConstantsTest {

    @Test
    @DisplayName("Constants class should not be instantiable")
    void constantsClassShouldNotBeInstantiable() throws NoSuchMethodException {
        Constructor<RomanNumeralsConstants> constructor = RomanNumeralsConstants.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("Constants should expose configured values")
    void constantsShouldExposeConfiguredValues() {
        assertEquals(1, RomanNumeralsConstants.MIN_VALUE);
        assertEquals(255, RomanNumeralsConstants.MAX_VALUE);
        assertEquals("Query must be a valid integer", RomanNumeralsConstants.INVALID_NUMBER_MESSAGE);
        assertEquals("Internal Server Error", RomanNumeralsConstants.INTERNAL_SERVER_ERROR_MESSAGE);
        assertEquals("HTTP method not supported", RomanNumeralsConstants.METHOD_NOT_ALLOWED_MESSAGE);
        assertEquals("Requested resource not found", RomanNumeralsConstants.NOT_FOUND_MESSAGE);
        assertEquals("Query range must be between 1 and 255", RomanNumeralsConstants.INVALID_RANGE_ERR_MSG);
    }
}