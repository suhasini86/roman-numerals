package com.adobe.aem.romannumerals.controller;

import com.adobe.aem.romannumerals.RomanNumeralsApplication;
import com.adobe.aem.romannumerals.dto.RomanNumeralResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Roman Numeral API Controller
 */
@SpringBootTest(classes = RomanNumeralsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Roman Numeral API Integration Tests")
class RomanNumeralControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "/romannumeral";
    }

    @Test
    @DisplayName("Should successfully convert valid integer 1 to I")
    void testConvertOne() {
        ResponseEntity<RomanNumeralResponse> response = 
                restTemplate.getForEntity(baseUrl + "?query=1", RomanNumeralResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getInput());
        assertEquals("I", response.getBody().getOutput());
    }

    @Test
    @DisplayName("Should successfully convert valid integer 255")
    void testConvert255() {
        ResponseEntity<RomanNumeralResponse> response = 
                restTemplate.getForEntity(baseUrl + "?query=255", RomanNumeralResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("255", response.getBody().getInput());
        assertEquals("CCLV", response.getBody().getOutput());
    }

    @Test
    @DisplayName("Should successfully convert 42")
    void testConvert42() {
        ResponseEntity<RomanNumeralResponse> response = 
                restTemplate.getForEntity(baseUrl + "?query=42", RomanNumeralResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("42", response.getBody().getInput());
        assertEquals("XLII", response.getBody().getOutput());
    }

    @Test
    @DisplayName("Should return 400 for zero")
    void testConvertZeroReturnsBadRequest() {
        ResponseEntity<String> response = 
                restTemplate.getForEntity(baseUrl + "?query=0", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 for negative number")
    void testConvertNegativeReturnsBadRequest() {
        ResponseEntity<String> response = 
                restTemplate.getForEntity(baseUrl + "?query=-5", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 for number exceeding maximum range")
    void testConvertBeyondMaxReturnsBadRequest() {
        ResponseEntity<String> response = 
                restTemplate.getForEntity(baseUrl + "?query=256", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 for invalid number format")
    void testConvertInvalidFormatReturnsBadRequest() {
        ResponseEntity<String> response = 
                restTemplate.getForEntity(baseUrl + "?query=abc", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 for empty query parameter")
    void testConvertEmptyQueryReturnsBadRequest() {
        ResponseEntity<String> response = 
                restTemplate.getForEntity(baseUrl + "?query=", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle various valid inputs")
    void testConvertVariousValidInputs() {
        int[][] testCases = {
                {1, 1}, {10, 1}, {50, 1}, {100, 1}, {150, 1}, {200, 1}, {255, 1}
        };

        for (int[] testCase : testCases) {
            int input = testCase[0];
            ResponseEntity<RomanNumeralResponse> response = 
                    restTemplate.getForEntity(baseUrl + "?query=" + input, RomanNumeralResponse.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(String.valueOf(input), response.getBody().getInput());
            assertNotNull(response.getBody().getOutput());
        }
    }

    @Test
    @DisplayName("Should return JSON response with correct structure")
    void testResponseStructure() {
        ResponseEntity<RomanNumeralResponse> response = 
                restTemplate.getForEntity(baseUrl + "?query=1", RomanNumeralResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getInput());
        assertNotNull(response.getBody().getOutput());
    }

    @Test
    @DisplayName("Should return 400 for query parameter with only spaces")
    void testConvertOnlySpacesReturnsBadRequest() {
        ResponseEntity<String> response = 
                restTemplate.getForEntity(baseUrl + "?query=%20%20%20", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 when query parameter is missing")
    void testMissingQueryParamReturnsBadRequest() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(baseUrl, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("query parameter is missing"));
    }

    @Test
    @DisplayName("Should return range validation message for out-of-range values")
    void testOutOfRangeValidationMessage() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(baseUrl + "?query=999", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Query range must be between 1 and 255"));
    }
}

