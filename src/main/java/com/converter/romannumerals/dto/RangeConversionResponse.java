package com.converter.romannumerals.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Response DTO for successful range conversion of integers to Roman numerals.
 * @param conversions ordered list of individual conversion results
 */

@Schema(description = "Range conversion result containing an ordered list of conversions")
public record RangeConversionResponse (
    @Schema(description = "Ordered list of integer to Roman numeral conversions within the specified range")
    List<RomanNumeralResponse> conversions) {

}