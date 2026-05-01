package com.adobe.aem.romannumerals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Roman Numerals Converter Spring Boot application.
 * <p>
 * Bootstraps the Spring context, auto-configuration, and embedded web server.
 */
@SpringBootApplication
public class RomanNumeralsApplication {

    /**
     * Application main method.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RomanNumeralsApplication.class, args);
    }
}