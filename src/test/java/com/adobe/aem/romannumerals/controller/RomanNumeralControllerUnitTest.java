package com.adobe.aem.romannumerals.controller;

import com.adobe.aem.romannumerals.dto.RomanNumeralResponse;
import com.adobe.aem.romannumerals.service.RomanNumeralConverterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Plain unit tests for {@link RomanNumeralController}.
 * <p>
 * Tests the controller logic directly without Spring context.
 * Note: Jakarta Bean Validation (e.g. {@code @Min}, {@code @Max})
 * is not enforced in plain unit tests - those constraints are covered
 * by the WebMvc slice tests.
 */
class RomanNumeralControllerUnitTest {

    private final RomanNumeralConverterService converterService = mock(RomanNumeralConverterService.class);
    private final RomanNumeralController controller = new RomanNumeralController(converterService);

    @Test
    @DisplayName("toRoman should return successful response for valid input")
    void toRomanShouldReturnSuccessfulResponse() {
        when(converterService.toRoman(10)).thenReturn("X");

        ResponseEntity<RomanNumeralResponse> response = controller.toRoman(10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("10", response.getBody().getInput());
        assertEquals("X", response.getBody().getOutput());
    }
}