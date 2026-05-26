package com.converter.romannumerals.filter;

import com.converter.romannumerals.config.RateLimitProperties;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Cache;
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
import java.time.Duration;
import java.time.Instant;
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

    // Use a bounded, evicting cache to avoid unbounded memory growth
    private final Cache<String, ClientRateInfo> clientRateCache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10))
            .maximumSize(10_000)
            .build();

    /**
     * Applies the configured per-IP request cap; on success attaches rate-limit headers and continues the chain.
     * When the cap is exceeded, writes a JSON 429 response and aborts further processing.
     *
     * @param request the incoming HTTP servlet request
     * @param response the servlet response (headers or error body may be set)
     * @param filterChain remaining filters and the target servlet
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!rateLimitProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);

        long now = System.currentTimeMillis();
        long windowMillis = rateLimitProperties.getTimeWindowSeconds() * 1000L;

        // Retrieve existing ClientRateInfo or create/reset when window expired
        ClientRateInfo rateInfo = clientRateCache.getIfPresent(clientIp);
        if (rateInfo == null || now - rateInfo.windowStart > windowMillis) {
            ClientRateInfo newInfo = new ClientRateInfo(now, new AtomicInteger(0));
            clientRateCache.put(clientIp, newInfo);
            rateInfo = newInfo;
        }

        int currentCount = rateInfo.requestCount.incrementAndGet();
        int remaining = Math.max(0, rateLimitProperties.getMaxRequests() - currentCount);

        response.setHeader("X-Rate-Limit-Limit", String.valueOf(rateLimitProperties.getMaxRequests()));
        response.setHeader("X-Rate-Limit-Remaining", String.valueOf(remaining));

        if (currentCount > rateLimitProperties.getMaxRequests()) {
            long elapsedMillis = System.currentTimeMillis() - rateInfo.windowStart;

            long retryAfterSeconds = Math.max(1,
                    (rateLimitProperties.getTimeWindowSeconds() * 1000L - elapsedMillis) / 1000L);

            log.warn("Rate limit exceeded for IP={} requests={} retry={s}",
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
     *
     * @param request incoming request whose path is inspected
     * @return {@code true} when this filter must not run for the given path
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/actuator")
                || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs");
    }

    /**
     * Resolves the client identity for counting; prefers the left-most address from {@code X-Forwarded-For} when present.
     *
     * @param request the current servlet request
     * @return client IP chosen for rate limiting (never {@code null} for typical servlet containers)
     */
    String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    /**
     * Mutable per-client rate limit snapshot for one fixed-width time window.
     */
    static class ClientRateInfo {
        /** Epoch millis when the current window started. */
        final long windowStart;
        /** Number of counted requests started in {@link #windowStart}'s window. */
        final AtomicInteger requestCount;

        /**
         * Creates a fresh window counter anchored at {@code windowStart}.
         *
         * @param windowStart millis when this window opens
         * @param requestCount mutable counter incremented per accepted request evaluation
         */
        ClientRateInfo(long windowStart, AtomicInteger requestCount) {
            this.windowStart = windowStart;
            this.requestCount = requestCount;
        }
    }
}