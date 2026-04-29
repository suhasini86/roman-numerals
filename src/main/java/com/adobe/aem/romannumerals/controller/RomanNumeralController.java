package com.adobe.aem.romannumerals.controller;

import com.adobe.aem.romannumerals.constants.RomanNumeralsConstants;
import com.adobe.aem.romannumerals.dto.RomanNumeralResponse;
import com.adobe.aem.romannumerals.service.RomanNumeralConverterService;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Roman Numeral conversion.
 *
 * <p>Endpoint: {@code GET /romannumeral?query={integer}}</p>
 * Range: 1-255 (with provision for 1-3999 in future)
 *
 * <p><All validation and conversion exceptions are propagated to
 * @link com.adobe.aem.romannumerals.exception.GlobalExceptionHandler}.</p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/romannumeral")
public class RomanNumeralController {

    private final RomanNumeralConverterService converterService;

    /**
     * Convert an integer to its Roman numeral representation
     *
     * @param query the integer value as a string to convert (1-255)
     * @return JSON response with input and output
     */
    @Observed(name = "romannumeral.convert", contextualName = "convert-to-roman")
    @GetMapping
    public ResponseEntity<RomanNumeralResponse> toRoman(@RequestParam(name = "query", required = true) String query) {
        log.info("Received request to convert: {}", query);
            // validate and parse query param
            int number = parseQuery(query);

            // Convert to Roman numeral
            String romanNumeral = converterService.toRoman(number);

            // Build and return response
            RomanNumeralResponse response = RomanNumeralResponse.builder()
                    .input(query)
                    .output(romanNumeral)
                    .build();

            log.info("Successfully converted {} to {}", query, romanNumeral);
            return ResponseEntity.ok(response);
    }

    /**
     * Parse and validate the input query parameter
     *
     * @param query the query parameter value
     * @return parsed integer
     * @throws NumberFormatException if input is not a valid integer
     * @throws RuntimeException if input is out of range
     */
    private int parseQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException(RomanNumeralsConstants.EMPTY_QUERY);
        }

        try {
            return Integer.parseInt(query.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(RomanNumeralsConstants.INVALID_NUMBER_MESSAGE);
        }
    }
}
