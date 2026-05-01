package com.adobe.aem.romannumerals.service;

import com.adobe.aem.romannumerals.exception.InvalidRomanNumeralException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.adobe.aem.romannumerals.constants.RomanNumeralsConstants.MAX_VALUE;
import static com.adobe.aem.romannumerals.constants.RomanNumeralsConstants.MIN_VALUE;
import static org.junit.jupiter.api.Assertions.*;

class RomanNumeralConverterServiceTest {

    private RomanNumeralConverterService service;

    @BeforeEach
    void setUp() {
        service = new RomanNumeralConverterService();
    }


    @Test
    void shouldConvertComplexNumbers() {
        assertEquals("IV", service.toRoman(4));
        assertEquals("IX", service.toRoman(9));
        assertEquals("XL", service.toRoman(40));
        assertEquals("XC", service.toRoman(90));
    }

    @Test
    void shouldConvertTypicalNumbers() {
        assertEquals("LVIII", service.toRoman(58));
        assertEquals("CCLV", service.toRoman(255));
        assertEquals("XLII", service.toRoman(42));
    }


    @Test
    void shouldConvertMinimumValue() {
        assertEquals("I", service.toRoman(MIN_VALUE));
    }

    @Test
    void shouldConvertMaximumValue() {
        assertEquals("CCLV", service.toRoman(MAX_VALUE));
    }

    @Test
    void shouldThrowExceptionWhenBelowMinimum() {
        InvalidRomanNumeralException ex = assertThrows(
                InvalidRomanNumeralException.class,
                () -> service.toRoman(MIN_VALUE - 1)
        );

        assertNotNull(ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAboveMaximum() {
        InvalidRomanNumeralException ex = assertThrows(
                InvalidRomanNumeralException.class,
                () -> service.toRoman(MAX_VALUE + 1)
        );

        assertNotNull(ex.getMessage());
    }


    @Test
    void shouldConvertUsingStaticMethod() {
        assertEquals("X", RomanNumeralConverterService.performRomanConversion(10));
        assertEquals("XLII", RomanNumeralConverterService.performRomanConversion(42));
    }


    @Test
    void shouldHandleSequentialNumbersCorrectly() {
        for (int i = 1; i <= 20; i++) {
            String result = service.toRoman(i);
            assertNotNull(result);
            assertFalse(result.isEmpty());
        }
    }
}