package com.adobe.aem.romannumerals.controller;

import com.adobe.aem.romannumerals.constants.RomanNumeralsConstants;
import com.adobe.aem.romannumerals.dto.ErrorResponse;
import com.adobe.aem.romannumerals.dto.RomanNumeralResponse;
import com.adobe.aem.romannumerals.service.RomanNumeralConverterService;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/romannumeral")
@Tag(name = "Roman Numeral Converter", description = "Convert integers (1-255) to Roman numeral representation")
public class RomanNumeralController {

    private final RomanNumeralConverterService converterService;

    /**
     * Convert an integer to its Roman numeral representation.
     *
     * @param query integer value as string
     * @return JSON response with input and output
     */
    @Operation(
            summary = "Convert integer to Roman numeral",
            description = "Accepts an integer between 1 and 255 and returns its Roman numeral representation."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful conversion",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RomanNumeralResponse.class),
                            examples = @ExampleObject(value = "{\"input\":\"42\",\"output\":\"XLII\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - blank, non-numeric, or out of range (1-255)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-28T12:00:00Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid input\"}")
                    )
            )
    })
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
