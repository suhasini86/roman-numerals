package com.adobe.aem.romannumerals.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RomanNumeralConverterServiceTest {

    private final RomanNumeralConverterService service = new RomanNumeralConverterService();

    @ParameterizedTest
    @CsvSource({
            "1,I",
            "4,IV",
            "9,IX",
            "40,XL",
            "58,LVIII",
            "90,XC",
            "199,CXCIX",
            "255,CCLV"
    })
    @DisplayName("toRoman should convert valid values")
    void toRomanShouldConvertValidValues(int input, String expected) {
        assertEquals(expected, service.toRoman(input));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 256, 1000})
    @DisplayName("toRoman should reject out of range values")
    void toRomanShouldRejectOutOfRange(int input) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.toRoman(input));
        assertEquals("Query range must be between 1 and 255", ex.getMessage());
    }

    @Test
    @DisplayName("performRomanConversion should handle repeated symbols")
    void performRomanConversionShouldHandleRepeatedSymbols() {
        assertEquals("CCLV", RomanNumeralConverterService.performRomanConversion(255));
    }
}
