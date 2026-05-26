package com.converter.romannumerals.controller;

import com.converter.romannumerals.dto.ErrorResponse;
import com.converter.romannumerals.dto.RangeConversionResponse;
import com.converter.romannumerals.dto.RomanNumeralResponse;
import com.converter.romannumerals.exception.InvalidRequestException;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.converter.romannumerals.constants.RomanNumeralsConstants.*;

/**
 * REST controller exposing integer-to-Roman numeral conversions.
 * <p>
 * Supports {@code GET /romannumeral?query=<number>} for a single value and
 * {@code GET /romannumeral?min=<n>&max=<m>} for an inclusive range. Accepted integers
 * are bounded by {@value RomanNumeralsConstants#MIN_VALUE} and {@value RomanNumeralsConstants#MAX_VALUE}.
 * Input validation combines Jakarta Bean Validation ({@code @Min}, {@code @Max}) with Spring {@code @Validated}.
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/romannumeral")
@Slf4j
@Tag(name = "Roman Numeral Converter", description = "Converts integers ("+MIN_VALUE+"-"+MAX_VALUE+") to Roman numeral representation")
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
            summary = "Convert a single integer to Roman numeral",
            description = "Accepts an integer between "+ MIN_VALUE+ " and "+ MAX_VALUE + "and returns its Roman numeral representation."
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
                    description = "Invalid input - blank, non-numeric, or out of range (1-3999)",
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
    @GetMapping(params = {QUERY})
    public ResponseEntity<RomanNumeralResponse> toRoman(
            @Parameter(description = "Integer to convert ("+MIN_VALUE+"-"+MAX_VALUE+")", example = "42")
            @RequestParam(name = QUERY, required = false)
            @Min(value = MIN_VALUE, message = QUERY + INVALID_RANGE_ERR_MSG)
            @Max(value = MAX_VALUE, message = QUERY + INVALID_RANGE_ERR_MSG)
            Integer query, HttpServletRequest request) {

        log.info("Received request to convert: {}", query);

        validateExclusiveParams(request);

        // Convert to Roman numeral
        String romanNumeral = converterService.toRoman(query);

        // Build and return response
        RomanNumeralResponse response = new RomanNumeralResponse(String.valueOf(query), romanNumeral);

        log.info("Successfully converted {} to {}", query, romanNumeral);
        return ResponseEntity.ok(response);
    }

    /*------------------------------------------------------------------------ */
    /* Range conversion: /romannumeral?min={integer}&max={integer}             */
    /*------------------------------------------------------------------------ */

    @Operation(
            summary = "Convert a range of integers to Roman numerals",
            description = "Accepts a range of integers (min and max) between "+ MIN_VALUE+" and "+ MAX_VALUE +
                    " and returns their Roman numeral representations in ascending order. Computed using Java 21 virtual threads."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful conversion of range",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema =  @Schema(implementation = RangeConversionResponse.class),
                            examples = @ExampleObject(value = """
                            {"conversions": [
                            {"input": "1","output": "I"},
                            {"input": "2","output": "II"},
                            {"input": "3","output": "III"},
                            {"input": "4","output": "IV"},
                            ]}
                            """))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - blank, non-numeric, or out of range (1-3999)",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"timestamp\":\"2026-04-28T12:00:00Z\",\"status\":400,\"error\":\"Bad Request\",\"message\":\"min and max must be a valid integer\"}")
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

    /**
     * Convert every integer in an inclusive {@code min}–{@code max} range to Roman numerals.
     * Results are ordered from {@code min} to {@code max}; conversion work is delegated to the service layer.
     *
     * @param min     start of the range (inclusive), between {@value RomanNumeralsConstants#MIN_VALUE}
     *                and {@value RomanNumeralsConstants#MAX_VALUE}
     * @param max     end of the range (inclusive); must be greater than {@code min}
     * @param request the incoming HTTP request, used for parameter exclusivity validation
     * @return JSON payload listing each conversion in range order
     */
    @Observed(name = "romannumeral.convertRange", contextualName = "convert-range-to-roman")
    @GetMapping(params = {MIN, MAX})
    public ResponseEntity<RangeConversionResponse> convertRangeToRoman(
            @Parameter(description = "Start of range (inclusive) ("+MIN_VALUE+"-"+MAX_VALUE+")", example = "1")
            @RequestParam(name = MIN, required = false)
            @Min(value = MIN_VALUE, message = MIN + INVALID_RANGE_ERR_MSG)
            @Max(value = MAX_VALUE, message = MIN + INVALID_RANGE_ERR_MSG)
            Integer min,

            @Parameter(description = "End of range (inclusive) ("+MIN_VALUE+"-"+MAX_VALUE+")", example = "4")
            @RequestParam(name = MAX, required = false)
            @Min(value = MIN_VALUE, message = MAX + INVALID_RANGE_ERR_MSG)
            @Max(value = MAX_VALUE, message = MAX + INVALID_RANGE_ERR_MSG)
            Integer max, HttpServletRequest request) {

        log.info("Received request to convert range: {} to {}", min, max);
        validateExclusiveParams(request);

        // Convert range to Roman numerals
        List<RomanNumeralResponse> conversions = converterService.convertRangeToRoman(min, max);

        log.info("Successfully converted range {} to {} to Roman numerals", min, max);
        return ResponseEntity.ok(new RangeConversionResponse(conversions));
    }

    /**
     * Ensures the client uses exactly one invocation style: either {@code query}, or both {@code min} and {@code max},
     * and that required parameters are present when chosen.
     *
     * @param request servlet request containing query parameter map
     * @throws InvalidRequestException if parameters are mutually exclusive or incomplete
     */
    private void validateExclusiveParams(HttpServletRequest request) {

        boolean hasQuery = StringUtils.hasText(request.getParameter(QUERY));
        boolean hasMin = StringUtils.hasText(request.getParameter(MIN));
        boolean hasMax = StringUtils.hasText(request.getParameter(MAX));

        // XOR rule enforcement
        if (hasQuery && (hasMin || hasMax)) {
            throw new InvalidRequestException(INVALID_QUERY_PARAM_COMBINATION_ERR_MSG);
        }
        if (!hasQuery && (!hasMin || !hasMax)) {

            String message = request.getParameterMap().containsKey(QUERY)
                    ? QUERY_EMPTY_ERR_MSG
                    : MIN_MAX_EMPTY_ERR_MSG;

                throw new InvalidRequestException(message);
        }
    }
}