package com.adobe.aem.romannumerals.controller;

import com.adobe.aem.romannumerals.exception.GlobalExceptionHandler;
import com.adobe.aem.romannumerals.service.RomanNumeralConverterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RomanNumeralController.class)
@Import(GlobalExceptionHandler.class)
class RomanNumeralControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RomanNumeralConverterService converterService;

    @Test
    @DisplayName("GET /romannumeral should return converted payload")
    void toRomanShouldReturnSuccessfulPayload() throws Exception {
        when(converterService.toRoman(42)).thenReturn("XLII");

        mockMvc.perform(get("/romannumeral").param("query", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.input").value("42"))
                .andExpect(jsonPath("$.output").value("XLII"));
    }

    @Test
    @DisplayName("GET /romannumeral should reject blank query")
    void toRomanShouldRejectBlankQuery() throws Exception {
        mockMvc.perform(get("/romannumeral").param("query", "   "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Query parameter cannot be empty"));
    }

    @Test
    @DisplayName("GET /romannumeral should reject non-numeric query")
    void toRomanShouldRejectNonNumericQuery() throws Exception {
        mockMvc.perform(get("/romannumeral").param("query", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Query must be a valid integer"));
    }

    @Test
    @DisplayName("GET /romannumeral should return service validation errors")
    void toRomanShouldReturnServiceValidationErrors() throws Exception {
        doThrow(new IllegalArgumentException("Query range must be between 1 and 255"))
                .when(converterService).toRoman(anyInt());

        mockMvc.perform(get("/romannumeral").param("query", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Query range must be between 1 and 255"));
    }
}
