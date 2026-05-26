package com.converter.romannumerals.dto;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for successful Roman numeral conversion.
 *
 * @param input  string rendering of the original integer argument
 * @param output Roman numeral string produced by the converter
 */

@Schema(description = "Successful conversion response containing the original integer and its Roman numeral representation")
public record RomanNumeralResponse(
        @Schema(description = "Original integer input for conversion", example = "2024")
        String input,
        @Schema(description = "Roman numeral representation of the input integer", example = "MMXXIV")
        String output
) {

}
