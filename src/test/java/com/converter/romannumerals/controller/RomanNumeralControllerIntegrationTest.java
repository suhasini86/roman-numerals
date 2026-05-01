package com.converter.romannumerals.controller;

import com.converter.romannumerals.RomanNumeralsApplication;
import com.converter.romannumerals.dto.RomanNumeralResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.converter.romannumerals.constants.RomanNumeralsConstants.*;
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
    void setup() {
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
    @DisplayName("Should return 400 for query parameter with only spaces")
    void testConvertOnlySpacesReturnsBadRequest() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(baseUrl + "?query=%20%20%20", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle various valid inputs")
    void testConvertVariousValidInputs() {
        int[] testCases = {
                1, 10, 50, 100, 150, 200, 255
        };

        for (int input : testCases) {
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
    @DisplayName("Should return 400 when query parameter is missing")
    void testMissingQueryParamReturnsBadRequest() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(baseUrl, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(MISSING_QUERY_ERR_MSG));
    }

    @Test
    @DisplayName("Should return range validation message for out-of-range values")
    void testOutOfRangeValidationMessage() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(baseUrl + "?query=999", String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(INVALID_RANGE_ERR_MSG));
    }

    @Test
    @DisplayName("Should return 405 for POST request")
    void testPostMethodReturns405() {
        ResponseEntity<String> response =
                restTemplate.postForEntity(baseUrl + "?query=42", null, String.class);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertTrue(response.getBody().contains(METHOD_NOT_ALLOWED_MESSAGE));
    }

    @Test
    @DisplayName("Should return 405 for PUT request")
    void testPutMethodReturns405() {
        ResponseEntity<String> response =
                restTemplate.exchange(baseUrl + "?query=42", HttpMethod.PUT, HttpEntity.EMPTY, String.class);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertTrue(response.getBody().contains(METHOD_NOT_ALLOWED_MESSAGE));
    }

    @Test
    @DisplayName("Should return 405 for DELETE request")
    void testDeleteMethodReturns405() {
        ResponseEntity<String> response =
                restTemplate.exchange(baseUrl + "?query=42", HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertTrue(response.getBody().contains(METHOD_NOT_ALLOWED_MESSAGE));
    }

    @Test
    @DisplayName("Should return 404 for unmapped URL")
    void testUnmappedUrlReturns404() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(baseUrl + "/dfgdfg", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains(NOT_FOUND_MESSAGE));
    }

    @Test
    @DisplayName("Should return 404 for sub-path of romannumeral")
    void testUnmappedSubPathReturns404() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(baseUrl + "/foo", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains(NOT_FOUND_MESSAGE));
    }
}