package com.converter.romannumerals.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger configuration for the Roman Numerals API.
 *
 * Defines API metadata (title, description, version, contact, license)
 * that is rendered on the Swagger UI page at /swagger-ui.html.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Builds the OpenAPI specification with project metadata.
     *
     * @return configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI romanNumeralsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Roman Numerals Converter API")
                        .description("""
                                Converts integers (1-3999) to Roman numeral strings.
                                Supports single-value queries and parallel range queries using Java 21 virtual threads.
                                Built with full observability, extensive test coverage, and comprehensive API documentation.""")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Suhasini")
                                .url("https://github.com/suhasini86/roman-numerals"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server")
                ));
    }
}