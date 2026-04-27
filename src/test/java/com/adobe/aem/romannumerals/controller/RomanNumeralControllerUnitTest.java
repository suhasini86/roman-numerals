package com.adobe.aem.romannumerals.controller;

import com.adobe.aem.romannumerals.service.RomanNumeralConverterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class RomanNumeralControllerUnitTest {

    private final RomanNumeralConverterService converterService = mock(RomanNumeralConverterService.class);
    private final RomanNumeralController controller = new RomanNumeralController(converterService);

    @Test
    @DisplayName("toRoman should reject null query")
    void toRomanShouldRejectNullQuery() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> controller.toRoman(null));
        assertEquals("Query parameter cannot be empty", ex.getMessage());
    }
}
