package com.converter.romannumerals.service;

import com.converter.romannumerals.dto.RomanNumeralResponse;
import com.converter.romannumerals.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RomanNumeralConverterServiceTest {

    private final RomanNumeralConverterService service = new RomanNumeralConverterService();

    @Test
    void performRomanConversion_basicValues() {
        assertEquals("I", RomanNumeralConverterService.performRomanConversion(1));
        assertEquals("IV", RomanNumeralConverterService.performRomanConversion(4));
        assertEquals("IX", RomanNumeralConverterService.performRomanConversion(9));
        assertEquals("XL", RomanNumeralConverterService.performRomanConversion(40));
        assertEquals("XLIV", RomanNumeralConverterService.performRomanConversion(44));
        assertEquals("CCLV", RomanNumeralConverterService.performRomanConversion(255));
        assertEquals("MMMCMXCIX", RomanNumeralConverterService.performRomanConversion(3999));
    }

    @Test
    void toRoman_valid_and_invalid() {
        // valid
        String roman = service.toRoman(42);
        assertEquals("XLII", roman);

        // invalid low
        assertThrows(InvalidRequestException.class, () -> service.toRoman(0));

        // invalid high
        assertThrows(InvalidRequestException.class, () -> service.toRoman(4000));
    }

    @Test
    void convertRangeToRoman_happyPath_and_invalid() {
        List<RomanNumeralResponse> list = service.convertRangeToRoman(1, 3);
        assertEquals(3, list.size());
        assertEquals("I", list.get(0).output());
        assertEquals("II", list.get(1).output());
        assertEquals("III", list.get(2).output());

        // invalid when min >= max
        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> service.convertRangeToRoman(5, 4));
        assertTrue(ex.getMessage().contains("must be less than"));
    }

    @Test
    void shutdownExecutor_runsWithoutError() {
        // ensure shutdownExecutor executes (coverage for @PreDestroy method)
        service.shutdownExecutor();
        // no assertions - just ensure no exception
    }
}

