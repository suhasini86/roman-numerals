package com.adobe.aem.romannumerals.controller;

import com.adobe.aem.romannumerals.service.RomanNumeralConverterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.adobe.aem.romannumerals.constants.RomanNumeralsConstants.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * WebMvc slice tests for {@link RomanNumeralController}.
 * <p>
 * Validates request mapping, Spring validation behaviour, and HTTP status codes
 * without starting the full application context.
 */
@WebMvcTest(RomanNumeralController.class)
class RomanNumeralControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RomanNumeralConverterService converterService;

    @Test
    @DisplayName("GET /romannumeral?query=42 should return converted payload")
    void toRomanShouldReturnConvertedPayload() throws Exception {
        when(converterService.toRoman(42)).thenReturn("XLII");

        mockMvc.perform(get("/romannumeral").param("query", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.input").value("42"))
                .andExpect(jsonPath("$.output").value("XLII"));
    }

    @Test
    @DisplayName("GET /romannumeral?query=abc should reject non-numeric query")
    void toRomanShouldRejectNonNumericQuery() throws Exception {
        mockMvc.perform(get("/romannumeral").param("query", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_NUMBER_MESSAGE));
    }

    @Test
    @DisplayName("GET /romannumeral?query= should reject blank query")
    void toRomanShouldRejectBlankQuery() throws Exception {
        mockMvc.perform(get("/romannumeral").param("query", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_NUMBER_MESSAGE));
    }

    @Test
    @DisplayName("GET /romannumeral?query=0 should reject below minimum")
    void toRomanShouldRejectBelowMinimum() throws Exception {
        mockMvc.perform(get("/romannumeral").param("query", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_RANGE_ERR_MSG));
    }

    @Test
    @DisplayName("GET /romannumeral?query=256 should reject above maximum")
    void toRomanShouldRejectAboveMaximum() throws Exception {
        mockMvc.perform(get("/romannumeral").param("query", "256"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_RANGE_ERR_MSG));
    }

    @Test
    @DisplayName("GET /romannumeral?query=-5 should reject negative numbers")
    void toRomanShouldRejectNegativeNumbers() throws Exception {
        mockMvc.perform(get("/romannumeral").param("query", "-5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_RANGE_ERR_MSG));
    }

    @Test
    @DisplayName("GET /romannumeral should return 400 when query param is missing")
    void toRomanShouldReturn400WhenQueryParamIsMissing() throws Exception {
        mockMvc.perform(get("/romannumeral"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(MISSING_QUERY_ERR_MSG));
    }

    @Test
    @DisplayName("POST /romannumeral should return 405")
    void postShouldReturn405() throws Exception {
        mockMvc.perform(post("/romannumeral").param("query", "42"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.message").value(METHOD_NOT_ALLOWED_MESSAGE));
    }

    @Test
    @DisplayName("PUT /romannumeral should return 405")
    void putShouldReturn405() throws Exception {
        mockMvc.perform(put("/romannumeral").param("query", "42"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.message").value(METHOD_NOT_ALLOWED_MESSAGE));
    }

    @Test
    @DisplayName("DELETE /romannumeral should return 405")
    void deleteShouldReturn405() throws Exception {
        mockMvc.perform(delete("/romannumeral").param("query", "42"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.message").value(METHOD_NOT_ALLOWED_MESSAGE));
    }

    @Test
    @DisplayName("PATCH /romannumeral should return 405")
    void patchShouldReturn405() throws Exception {
        mockMvc.perform(patch("/romannumeral").param("query", "42"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.message").value(METHOD_NOT_ALLOWED_MESSAGE));
    }
}