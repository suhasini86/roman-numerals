package com.converter.romannumerals.controller;

import com.converter.romannumerals.exception.InvalidRequestException;
import com.converter.romannumerals.service.RomanNumeralConverterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RomanNumeralController.class)
class RomanNumeralControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RomanNumeralConverterService converterService;

    @Test
    void toRoman_success() throws Exception {
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
        // query=0 is below MIN_VALUE (1)
        mockMvc.perform(get("/romannumeral").param("query", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("must be between")));
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
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Provide either 'query' OR 'min & max'")));
    }
}

