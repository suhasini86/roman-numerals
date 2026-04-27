package com.adobe.aem.romannumerals.service;

import com.adobe.aem.romannumerals.constants.RomanNumeralsConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.adobe.aem.romannumerals.constants.RomanNumeralsConstants.*;

/**
 * Service for converting integers to Roman numerals
 *
 * Supports range 1-255 (default).
 * Uses a clean, maintainable approach with mapping of values to Roman symbols.
 */
@Slf4j
@Service
public class RomanNumeralConverterService {

    private static final int[] VALUES = {
            1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1
    };

    private static final String[] SYMBOLS = {
            "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"
    };

    /**
     * Convert an integer to Roman numeral
     *
     * @param number the integer to convert
     * @return Roman numeral representation
     * @throws IllegalArgumentException if input is outside valid range
     */
    public String toRoman(int number) {
        validateRange(number);
        return performRomanConversion(number);
    }

    /**
     * Perform the actual conversion using the greedy algorithm
     *
     * @param number the integer to convert
     * @return Roman numeral representation
     */
    public static String performRomanConversion(int number) {
        StringBuilder romanNumeral = new StringBuilder();
        int remaining = number;

        for (int i = 0; i < VALUES.length; i++) {
            while (remaining >= VALUES[i]) {
                romanNumeral.append(SYMBOLS[i]);
                remaining -= VALUES[i];
            }
        }

        log.debug("Converted {} to {}", number, romanNumeral.toString());
        return romanNumeral.toString();
    }

    /**
     * Validate the input number is within acceptable range
     *
     * @param number the integer to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateRange(int number) {
        if (number < MIN_VALUE || number > MAX_VALUE) {
            throw new IllegalArgumentException(INVALID_RANGE_ERR_MSG);
        }
    }

}
