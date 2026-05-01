package com.converter.romannumerals.controller;

import com.converter.romannumerals.dto.ErrorResponse;
import com.converter.romannumerals.dto.RomanNumeralResponse;
import com.converter.romannumerals.service.RomanNumeralConverterService;
import com.converter.romannumerals.constants.RomanNumeralsConstants;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.converter.romannumerals.constants.RomanNumeralsConstants.MAX_VALUE;
import static com.converter.romannumerals.constants.RomanNumeralsConstants.MIN_VALUE;
import static com.converter.romannumerals.constants.RomanNumeralsConstants.INVALID_RANGE_ERR_MSG;

/**
 * REST controller that exposes an endpoint for converting integers to Roman numerals.
 * <p>
 * The single {@code GET /romannumeral?query=<number>} endpoint accepts an integer
 * between 1 and 255 and returns its Roman numeral representation.
 * Input validation is handled declaratively via Jakarta Bean Validation annotations
 * ({@code @Min}, {@code @Max}) combined with Spring's {@code @Validated}.
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/romannumeral")
@Slf4j
@Tag(name = "Roman Numeral Converter", description = "Converts integers (1-255) to Roman numeral representation")
public class RomanNumeralController {

    private final RomanNumeralConverterService converterService;

    /**
     * Convert an integer to its Roman numeral representation.
     *
     * @param query integer value between {@value RomanNumeralsConstants#MIN_VALUE}
     *              and {@value RomanNumeralsConstants#MAX_VALUE}
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
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-28T12:00:00Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"Query must be a valid integer\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Requested resource not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-28T12:00:00Z\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Requested resource not found\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "405",
                    description = "HTTP method not supported",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-28T12:00:00Z\",\"status\":405,\"error\":\"Method Not Allowed\",\"message\":\"HTTP method not supported\"}")
                    )
            )
    })
    @Observed(name = "romannumeral.convert", contextualName = "convert-to-roman")
    @GetMapping
    public ResponseEntity<RomanNumeralResponse> toRoman(
            @Parameter(description = "Integer to convert (1-255)", required = true, example = "42")
            @RequestParam(name = "query")
            @Min(value = MIN_VALUE, message = INVALID_RANGE_ERR_MSG)
            @Max(value = MAX_VALUE, message = INVALID_RANGE_ERR_MSG)
            int query) {

        log.info("Received request to convert: {}", query);

        // Convert to Roman numeral
        String romanNumeral = converterService.toRoman(query);

        // Build and return response
        RomanNumeralResponse response = RomanNumeralResponse.builder()
                .input(String.valueOf(query))
                .output(romanNumeral)
                .build();

        log.info("Successfully converted {} to {}", query, romanNumeral);
        return ResponseEntity.ok(response);
    }
}