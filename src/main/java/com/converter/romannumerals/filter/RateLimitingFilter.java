package com.converter.romannumerals.filter;

import com.converter.romannumerals.config.RateLimitProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiter filter that enforces per-IP rate limiting using a fixed-window algorithm.
 * Each unique client IP receives its own counter that resets after the configured time window.
 * When the limit is exceeded, the filter returns a HTTP 429 Too Many Requests response
 * with Retry-After and rate limit headers.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitProperties rateLimitProperties;

    private final Map<String, ClientRateInfo> clientRateMap = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!rateLimitProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);

        ClientRateInfo rateInfo = clientRateMap.compute(clientIp, (ip, existing) -> {
            long now = System.currentTimeMillis();
            long windowMillis = rateLimitProperties.getTimeWindowSeconds() * 1000L;

            if (existing == null || now - existing.windowStart > windowMillis) {
                return new ClientRateInfo(now, new AtomicInteger(0));
            }

            return existing;
        });

        int currentCount = rateInfo.requestCount.incrementAndGet();
        int remaining = Math.max(0,
                rateLimitProperties.getMaxRequests() - currentCount);

        response.setHeader("X-Rate-Limit-Limit",
                String.valueOf(rateLimitProperties.getMaxRequests()));

        response.setHeader("X-Rate-Limit-Remaining",
                String.valueOf(remaining));

        if (currentCount > rateLimitProperties.getMaxRequests()) {

            long elapsedMillis = System.currentTimeMillis() - rateInfo.windowStart;

            long retryAfterSeconds = Math.max(1,
                    (rateLimitProperties.getTimeWindowSeconds() * 1000L - elapsedMillis) / 1000L);

            log.warn("Rate limit exceeded for IP={} requests={} retry={}s",
                    clientIp, currentCount, retryAfterSeconds);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

            String body = """
                    {
                      "timestamp": "%s",
                      "status": 429,
                      "error": "Too Many Requests",
                      "message": "Rate limit exceeded",
                      "path": "%s"
                    }
                    """.formatted(Instant.now(), request.getRequestURI());

            response.getWriter().write(body);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Exclude actuator, swagger, and OpenAPI endpoints from rate limiting.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/actuator")
                || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs");
    }

    String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * Holds the rate info for a single client.
     * windowStart = start of the current window.
     */
    static class ClientRateInfo {

        final long windowStart;
        final AtomicInteger requestCount;

        ClientRateInfo(long windowStart, AtomicInteger requestCount) {
            this.windowStart = windowStart;
            this.requestCount = requestCount;
        }
    }
}
