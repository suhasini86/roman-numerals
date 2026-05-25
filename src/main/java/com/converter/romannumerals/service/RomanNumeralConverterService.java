package com.converter.romannumerals.service;

import com.converter.romannumerals.dto.RomanNumeralResponse;
import com.converter.romannumerals.exception.InvalidRequestException;
import com.converter.romannumerals.constants.RomanNumeralsConstants;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.converter.romannumerals.constants.RomanNumeralsConstants.INVALID_RANGE_ERR_MSG;
import static com.converter.romannumerals.constants.RomanNumeralsConstants.MAX_VALUE;
import static com.converter.romannumerals.constants.RomanNumeralsConstants.MIN_VALUE;

/**
 * Service responsible for converting integers to their Roman numeral representation.
 * <p>
 * Supports the range {@value RomanNumeralsConstants#MIN_VALUE}
 * to {@value RomanNumeralsConstants#MAX_VALUE}.
 * Uses a greedy algorithm with a pre-defined mapping of decimal values to Roman symbols.
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

    private final ExecutorService rangeExecutor =  Executors.newVirtualThreadPerTaskExecutor();

    /**
     * Convert an integer to Roman numeral
     *
     * @param number the integer to convert
     * @return Roman numeral representation
     * @throws IllegalArgumentException if input is outside valid range
     */
    @Observed(name = "romannumeral.converter", contextualName = "roman-conversion")
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
     * @throws InvalidRequestException if validation fails
     */
    private void validateRange(int number) {
        if (number < MIN_VALUE || number > MAX_VALUE) {
            throw new InvalidRequestException(INVALID_RANGE_ERR_MSG);
        }
    }

    @Observed(name = "romannumeral.range.converter", contextualName = "range-roman-conversion")
    public List<RomanNumeralResponse> convertRangeToRoman(int min, int max) {

        if (min >= max) {
            throw new InvalidRequestException("Invalid range: 'min'("+ min +") value must be less than " +
                    "'max' ("+max +"). Please ensure 'min' is smaller than 'max'.");
        }

        List<CompletableFuture<RomanNumeralResponse>> futures = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            final int value = i;
            futures.add(CompletableFuture.supplyAsync(() -> new RomanNumeralResponse(
                    String.valueOf(value),
                    performRomanConversion(value))
                    , rangeExecutor));
        }

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    @PreDestroy
    void shutdownExecutor() {
        rangeExecutor.shutdown();
    }
}