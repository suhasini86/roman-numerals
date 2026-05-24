package com.converter.romannumerals.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    void romanNumeralsOpenAPI_containsMetadata() {
        OpenApiConfig cfg = new OpenApiConfig();
        OpenAPI api = cfg.romanNumeralsOpenAPI();

        assertThat(api).isNotNull();
        assertThat(api.getInfo()).isNotNull();
        assertThat(api.getInfo().getTitle()).contains("Roman Numerals Converter");
        assertThat(api.getServers()).isNotEmpty();
    }
}

