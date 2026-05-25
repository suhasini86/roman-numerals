package com.converter.romannumerals.filter;


import com.converter.romannumerals.config.RateLimitProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitingFilterTest {

    private RateLimitProperties properties;
    private RateLimitingFilter filter;

    @BeforeEach
    void setUp() {
        properties = new RateLimitProperties();
        properties.setEnabled(true);
        properties.setMaxRequests(5);
        properties.setTimeWindowSeconds(60);
        filter = new RateLimitingFilter(properties);
    }

    @Test
    void requestsWithinLimit_shouldPass() throws Exception {
        for (int i = 0; i < 5; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/romannumeral");
            request.addParameter("query", "42");
            request.setRemoteAddr("192.168.1.1");
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain();

            filter.doFilterInternal(request, response, chain);

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getHeader("X-Rate-Limit-Limit")).isEqualTo("5");
            assertThat(response.getHeader("X-Rate-Limit-Remaining")).isNotNull();
        }
    }

    @Test
    void requestsExceedingLimit_shouldReturn429() throws Exception {
        // Exhaust the limit
        for (int i = 0; i < 5; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/romannumeral");
            request.setRemoteAddr("10.0.0.1");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilterInternal(request, response, new MockFilterChain());
        }

        // Next request should be rate limited
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/romannumeral");
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilterInternal(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getHeader("Retry-After")).isNotNull();
        assertThat(response.getHeader("X-Rate-Limit-Remaining")).isEqualTo("0");
        assertThat(response.getContentAsString()).contains("Rate limit exceeded");
    }

    @Test
    void differentIps_shouldHaveSeparateLimits() throws Exception {
        // Exhaust IP-1's limit
        for (int i = 0; i < 5; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/romannumeral");
            request.setRemoteAddr("10.0.0.1");
            filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());
        }

        // IP-2 should still be allowed
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/romannumeral");
        request.setRemoteAddr("10.0.0.2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilterInternal(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void rateLimitDisabled_shouldPassAll() throws Exception {
        properties.setEnabled(false);

        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/romannumeral");
            request.setRemoteAddr("10.0.0.1");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilterInternal(request, response, new MockFilterChain());

            assertThat(response.getStatus()).isEqualTo(200);
        }
    }

    @Test
    void actuatorEndpoints_shouldNotBeRateLimited() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void swaggerEndpoints_shouldNotBeRateLimited() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/swagger-ui.html");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void apiDocsEndpoints_shouldNotBeRateLimited() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v3/api-docs");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void apiEndpoints_shouldBeRateLimited() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/romannumeral");
        assertThat(filter.shouldNotFilter(request)).isFalse();
    }

    @Test
    void xForwardedForHeader_shouldBeUsedForClientIp() throws Exception {
        // Exhaust the forwarded IP's limit
        for (int i = 0; i < 5; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/romannumeral");
            request.setRemoteAddr("127.0.0.1");
            request.addHeader("X-Forwarded-For", "203.0.113.50, 70.41.3.18");
            filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());
        }

        // Same forwarded IP should be blocked
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/romannumeral");
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.50, 70.41.3.18");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilterInternal(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(429);

        // Different forwarded IP should pass
        MockHttpServletRequest request2 = new MockHttpServletRequest("GET", "/romannumeral");
        request2.setRemoteAddr("127.0.0.1");
        request2.addHeader("X-Forwarded-For", "203.0.113.99");
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        filter.doFilterInternal(request2, response2, new MockFilterChain());

        assertThat(response2.getStatus()).isEqualTo(200);
    }

    @Test
    void getClientIp_noForwardedHeader_usesRemoteAddr() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/romannumeral");
        request.setRemoteAddr("192.168.1.100");

        assertThat(filter.getClientIp(request)).isEqualTo("192.168.1.100");
    }

    @Test
    void getClientIp_blankForwardedHeader_usesRemoteAddr() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/romannumeral");
        request.setRemoteAddr("192.168.1.100");
        request.addHeader("X-Forwarded-For", " ");

        assertThat(filter.getClientIp(request)).isEqualTo("192.168.1.100");
    }
}
