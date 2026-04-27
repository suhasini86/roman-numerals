package com.adobe.aem.romannumerals.dto;


import lombok.Builder;
import lombok.Getter;

/**
 * Response DTO for successful Roman numeral conversion
 */
@Builder
@Getter
public class RomanNumeralResponse {
    private final String input;
    private final String output;
}
