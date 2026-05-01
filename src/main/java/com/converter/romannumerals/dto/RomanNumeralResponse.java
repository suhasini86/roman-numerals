package com.converter.romannumerals.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Response DTO for successful Roman numeral conversion.
 */
@Builder
@Getter
@Schema(description = "Successful conversion response containing the original integer and its Roman numeral representation")
public class RomanNumeralResponse {

    @Schema(description = "The original integer input", example = "42")
    private final String input;

    @Schema(description = "The Roman numeral representation", example = "XLII")
    private final String output;
}