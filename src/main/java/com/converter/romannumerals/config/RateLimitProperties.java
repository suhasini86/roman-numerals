package com.converter.romannumerals.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * Configurable rate limiting properties.
 * <p>
 * Supports per-IP fixed-window rate limiting with configurable capacity and refill rate.
 * Properties are bound from {@code rate-limit.*} in application.yaml.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    /** Whether rate limiting is enabled. Defaults to true. */
    private boolean enabled = true;

    /** Maximum number of requests allowed in the time window (bucket capacity). */
    private int maxRequests = 50;

    /** Time window in seconds for the refill. Defaults to 60 seconds (1 minute). */
    private int timeWindowSeconds = 60;
}