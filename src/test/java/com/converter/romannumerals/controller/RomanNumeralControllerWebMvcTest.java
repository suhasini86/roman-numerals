package com.converter.romannumerals.controller;

import com.converter.romannumerals.config.RateLimitProperties;
import com.converter.romannumerals.exception.InvalidRequestException;
import com.converter.romannumerals.service.RomanNumeralConverterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RomanNumeralController.class)
class RomanNumeralControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RomanNumeralConverterService converterService;

    @MockitoBean
    private RateLimitProperties rateLimitProperties;

    @Test
    void toRoman_Success() throws Exception {
        when(converterService.toRoman(42)).thenReturn("XLII");

        mockMvc.perform(get("/romannumeral").param("query", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.output").value("XLII"));
    }

    @Test
    void toRoman_missingParams_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/romannumeral"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void toRoman_queryOutOfRange_returnsBadRequest() throws Exception {
        // query is below MIN_VALUE (1)
        mockMvc.perform(get("/romannumeral").param("query", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("value must be a valid integer between")));
    }

    @Test
    void convertRange_minGreaterThanMax_returnsBadRequest() throws Exception {
        doThrow(new InvalidRequestException("'min' and 'max' invalid"))
                .when(converterService).convertRangeToRoman(5, 4);

        mockMvc.perform(get("/romannumeral").param("min", "5").param("max", "4"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("'min' and 'max' invalid"));
    }

    @Test
    void bothQueryAndMin_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/romannumeral").param("query", "1").param("min", "1").param("max", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("'query' and 'min'/'max' parameters cannot be used together")));
    }

    @Test
    void queryAndMaxOnly_returnsBadRequest() throws Exception {
        // Covers the missed branch: hasQuery=true && hasMin=false && hasMax=true
        mockMvc.perform(get("/romannumeral").param("query", "1").param("max", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("'query' and 'min'/'max' parameters cannot be used together")));
    }

    @Test
    void minOnly_missingMax_returnsBadRequest() throws Exception {
        // Covers the missed branch: hasQuery=false && hasMin=true && hasMax=false
        mockMvc.perform(get("/romannumeral").param("min", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid request. Please provide either a 'query' parameter or both 'min' and 'max' parameters.")));
    }
}

