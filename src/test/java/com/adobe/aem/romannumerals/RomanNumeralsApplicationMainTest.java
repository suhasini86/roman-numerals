package com.adobe.aem.romannumerals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class RomanNumeralsApplicationMainTest {

    @Test
    @DisplayName("main should delegate to SpringApplication.run")
    void mainShouldDelegateToSpringApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            RomanNumeralsApplication.main(new String[]{"--spring.main.web-application-type=none"});
            springApplication.verify(() -> SpringApplication.run(RomanNumeralsApplication.class,
                    new String[]{"--spring.main.web-application-type=none"}));
        }
    }
}
