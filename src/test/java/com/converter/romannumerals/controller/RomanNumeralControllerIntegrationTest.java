package com.converter.romannumerals.controller;

import com.converter.romannumerals.dto.RangeConversionResponse;
import com.converter.romannumerals.dto.RomanNumeralResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RomanNumeralControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getSingleConversion_success() {
        ResponseEntity<RomanNumeralResponse> resp = restTemplate.getForEntity(
                "/romannumeral?query=42", RomanNumeralResponse.class);

        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().output()).isEqualTo("XLII");
    }

    @Test
    void getSingleConversion_invalidType_returnsBadRequest() {
        ResponseEntity<Map> resp = restTemplate.getForEntity(
                "/romannumeral?query=abc", Map.class);

        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
        assertThat(resp.getBody()).containsKey("message");
        String msg = (String) resp.getBody().get("message");
        assertThat(msg).containsIgnoringCase("value must be a valid integer between");
    }

    @Test
    void getRangeConversion_success_and_invalid() {
        ResponseEntity<RangeConversionResponse> resp = restTemplate.exchange(
                "/romannumeral?min=1&max=3",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<RangeConversionResponse>() {
                }
        );

        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().conversions()).hasSize(3);
        assertThat(resp.getBody().conversions().get(0).output()).isEqualTo("I");

        // min > max should return 400
        ResponseEntity<Map> bad = restTemplate.getForEntity(
                "/romannumeral?min=5&max=4", Map.class);
        assertThat(bad.getStatusCode().is4xxClientError()).isTrue();
        assertThat(bad.getBody()).containsKey("message");
        assertThat((String) bad.getBody().get("message")).contains("must be less than");
    }

    @Test
    void missingParams_returnsBadRequest() {
        ResponseEntity<Map> resp = restTemplate.getForEntity(
                "/romannumeral", Map.class);

        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
        assertThat(resp.getBody()).containsKey("message");
        String message = (String) resp.getBody().get("message");

        // Spring may return MissingServletRequestParameterException or controller's own InvalidRequestException
        assertThat(message).satisfiesAnyOf(
                msg -> assertThat(msg).contains("Missing required parameters"),
                msg -> assertThat(msg).contains("Please provide either a 'query' parameter or both 'min' and 'max' parameters")
        );
    }
}

