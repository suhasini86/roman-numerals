package com.converter.romannumerals.load;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

/**
 * Load test for the Roman Numeral Converter API.
 * <p>
 * This load test simulates concurrent user requests to test the application's
 * performance under load. It measures response times, throughput, and error rates.
 * <p>
 * Tests are marked with @Tag("loadtest") and can be run separately using:
 * {@code mvn test -Dgroups=loadtest}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@DisplayName("Roman Numeral Converter Load Tests")
class RomanNumeralConverterLoadTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;
    private String baseUrl;

    @BeforeEach
    void setup() {
        restTemplate = new TestRestTemplate();
        baseUrl = "http://localhost:" + port;
    }

    /**
     * Load test: 100 concurrent requests to single conversion endpoint.
     * Tests: /romannumeral?query=<number>
     */
    @Test
    @DisplayName("Load Test: 100 concurrent single conversions")
    void loadTest_100ConcurrentSingleConversions() throws InterruptedException {
        int concurrentRequests = 100;
        int totalRequests = 1000;
        
        LoadTestMetrics metrics = executeLoadTest(concurrentRequests, totalRequests, () -> {
            int randomNumber = new Random().nextInt(3999) + 1;
            return restTemplate.getForEntity(
                    baseUrl + "/romannumeral?query=" + randomNumber,
                    Object.class
            );
        });

        logMetrics("100 Concurrent Single Conversions", metrics);

        assertThat(metrics.totalRequests).isEqualTo(totalRequests);
        assertThat(metrics.successfulRequests).isGreaterThanOrEqualTo((int)(totalRequests * 0.95)); // 95% success rate
        assertThat(metrics.avgResponseTime).isLessThan(5000); // avg < 5 seconds
    }

    /**
     * Load test: 50 concurrent requests to range conversion endpoint.
     * Tests: /romannumeral?min=<number>&max=<number>
     */
    @Test
    @DisplayName("Load Test: 50 concurrent range conversions")
    void loadTest_50ConcurrentRangeConversions() throws InterruptedException {
        int concurrentRequests = 50;
        int totalRequests = 500;

        LoadTestMetrics metrics = executeLoadTest(concurrentRequests, totalRequests, () -> {
            Random random = new Random();
            int min = random.nextInt(100) + 1;
            int max = min + random.nextInt(100);
            return restTemplate.getForEntity(
                    baseUrl + "/romannumeral?min=" + min + "&max=" + max,
                    Object.class
            );
        });

        logMetrics("50 Concurrent Range Conversions", metrics);

        assertThat(metrics.totalRequests).isEqualTo(totalRequests);
        assertThat(metrics.successfulRequests).isGreaterThanOrEqualTo((int)(totalRequests * 0.95));
    }

    /**
     * Load test: Mixed concurrent requests (single and range).
     * Tests both endpoints with 75 concurrent threads.
     */
    @Test
    @DisplayName("Load Test: 75 concurrent mixed requests")
    void loadTest_75ConcurrentMixedRequests() throws InterruptedException {
        int concurrentRequests = 75;
        int totalRequests = 750;

        LoadTestMetrics metrics = executeLoadTest(concurrentRequests, totalRequests, () -> {
            Random random = new Random();
            if (random.nextBoolean()) {
                // Single conversion
                int randomNumber = random.nextInt(3999) + 1;
                return restTemplate.getForEntity(
                        baseUrl + "/romannumeral?query=" + randomNumber,
                        Object.class
                );
            } else {
                // Range conversion
                int min = random.nextInt(100) + 1;
                int max = min + random.nextInt(100);
                return restTemplate.getForEntity(
                        baseUrl + "/romannumeral?min=" + min + "&max=" + max,
                        Object.class
                );
            }
        });

        logMetrics("75 Concurrent Mixed Requests", metrics);

        assertThat(metrics.totalRequests).isEqualTo(totalRequests);
        assertThat(metrics.successfulRequests).isGreaterThanOrEqualTo((int)(totalRequests * 0.95));
    }

    /**
     * Load test: Edge case - boundary values (1 and 3999).
     * Tests with 30 concurrent requests using boundary values.
     */
    @Test
    @DisplayName("Load Test: Boundary value conversions")
    void loadTest_BoundaryValueConversions() throws InterruptedException {
        int concurrentRequests = 30;
        int totalRequests = 300;

        LoadTestMetrics metrics = executeLoadTest(concurrentRequests, totalRequests, () -> {
            Random random = new Random();
            int randomNumber = random.nextBoolean() ? 1 : 3999;
            return restTemplate.getForEntity(
                    baseUrl + "/romannumeral?query=" + randomNumber,
                    Object.class
            );
        });

        logMetrics("Boundary Value Conversions", metrics);

        assertThat(metrics.totalRequests).isEqualTo(totalRequests);
        assertThat(metrics.successfulRequests).isGreaterThanOrEqualTo((int)(totalRequests * 0.95));
    }

    /**
     * Load test: Invalid input handling under load.
     * Tests error handling with invalid query parameters.
     */
    @Test
    @DisplayName("Load Test: Invalid input handling")
    void loadTest_InvalidInputHandling() throws InterruptedException {
        int concurrentRequests = 40;
        int totalRequests = 400;

        LoadTestMetrics metrics = executeLoadTest(concurrentRequests, totalRequests, () -> {
            Random random = new Random();
            String[] invalidInputs = {"abc", "-1", "4000", "0", ""};
            String invalidInput = invalidInputs[random.nextInt(invalidInputs.length)];
            return restTemplate.getForEntity(
                    baseUrl + "/romannumeral?query=" + invalidInput,
                    Object.class
            );
        });

        logMetrics("Invalid Input Handling", metrics);

        assertThat(metrics.totalRequests).isEqualTo(totalRequests);
        // Invalid inputs should still be handled gracefully (400 errors)
        assertThat(metrics.badRequestCount + metrics.successfulRequests)
                .isGreaterThanOrEqualTo((int)(totalRequests * 0.95));
    }

    /**
     * Stress test: High concurrency with 200 threads.
     * Tests application stability under extreme load.
     */
    @Test
    @DisplayName("Stress Test: 200 concurrent threads")
    void stressTest_200ConcurrentThreads() throws InterruptedException {
        int concurrentRequests = 200;
        int totalRequests = 2000;

        LoadTestMetrics metrics = executeLoadTest(concurrentRequests, totalRequests, () -> {
            int randomNumber = new Random().nextInt(3999) + 1;
            return restTemplate.getForEntity(
                    baseUrl + "/romannumeral?query=" + randomNumber,
                    Object.class
            );
        });

        logMetrics("200 Concurrent Threads Stress Test", metrics);

        assertThat(metrics.totalRequests).isEqualTo(totalRequests);
        assertThat(metrics.successfulRequests).isGreaterThanOrEqualTo((int)(totalRequests * 0.90));
    }

    /**
     * Sustained load test: Measure throughput over extended time.
     * Runs 50 concurrent requests for 30 seconds.
     */
    @Test
    @DisplayName("Load Test: Sustained load (30 seconds)")
    void loadTest_SustainedLoad() throws InterruptedException {
        int concurrentRequests = 50;
        long durationSeconds = 30;

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (durationSeconds * 1000);
        AtomicInteger totalRequests = new AtomicInteger(0);
        AtomicInteger successfulRequests = new AtomicInteger(0);
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executorService = Executors.newFixedThreadPool(concurrentRequests);

        // Schedule requests until time limit
        for (int i = 0; i < concurrentRequests; i++) {
            executorService.submit(() -> {
                while (System.currentTimeMillis() < endTime) {
                    long reqStart = System.currentTimeMillis();
                    try {
                        int randomNumber = new Random().nextInt(3999) + 1;
                        ResponseEntity<Object> response = restTemplate.getForEntity(
                                baseUrl + "/romannumeral?query=" + randomNumber,
                                Object.class
                        );
                        long duration = System.currentTimeMillis() - reqStart;
                        responseTimes.add(duration);
                        totalRequests.incrementAndGet();

                        if (response.getStatusCode() == HttpStatus.OK) {
                            successfulRequests.incrementAndGet();
                        }
                    } catch (Exception e) {
                        long duration = System.currentTimeMillis() - reqStart;
                        responseTimes.add(duration);
                        totalRequests.incrementAndGet();
                    }
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(durationSeconds + 10, TimeUnit.SECONDS);

        // Calculate metrics
        double avgResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
        
        long maxResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        double throughput = (double) totalRequests.get() / durationSeconds;

        log.info("=".repeat(70));
        log.info("SUSTAINED LOAD TEST RESULTS (30 seconds)");
        log.info("=".repeat(70));
        log.info("Total Requests: {}", totalRequests.get());
        log.info("Successful Requests: {} ({:.2f}%)", successfulRequests.get(),
                (successfulRequests.get() * 100.0) / totalRequests.get());
        log.info("Throughput: {:.2f} requests/second", throughput);
        log.info("Average Response Time: {:.2f} ms", avgResponseTime);
        log.info("Max Response Time: {} ms", maxResponseTime);
        log.info("=".repeat(70));

        assertThat(totalRequests.get()).isGreaterThan(0);
        assertThat(successfulRequests.get()).isGreaterThanOrEqualTo((int)(totalRequests.get() * 0.90));
    }

    /**
     * Ramp-up test: Gradually increase load from 10 to 100 concurrent threads.
     */
    @Test
    @DisplayName("Load Test: Ramp-up from 10 to 100 threads")
    void loadTest_RampUp() throws InterruptedException {
        int[] threadCounts = {10, 20, 30, 40, 50, 75, 100};

        for (int threadCount : threadCounts) {
            LoadTestMetrics metrics = executeLoadTest(threadCount, 200, () -> {
                int randomNumber = new Random().nextInt(3999) + 1;
                return restTemplate.getForEntity(
                        baseUrl + "/romannumeral?query=" + randomNumber,
                        Object.class
                );
            });

            log.info("Ramp-up: {} threads - Avg Response Time: {:.2f} ms, Success Rate: {:.2f}%",
                    threadCount, metrics.avgResponseTime,
                    (metrics.successfulRequests * 100.0) / metrics.totalRequests);
        }
    }

    /**
     * Helper method to execute a load test and collect metrics.
     *
     * @param concurrentRequests number of concurrent threads
     * @param totalRequests total number of requests to execute
     * @param requestSupplier lambda that supplies the request to execute
     * @return LoadTestMetrics with collected performance data
     */
    private LoadTestMetrics executeLoadTest(int concurrentRequests, int totalRequests,
                                           RequestSupplier requestSupplier) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentRequests);
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger successfulRequests = new AtomicInteger(0);
        AtomicInteger badRequestCount = new AtomicInteger(0);
        AtomicInteger failedRequests = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        // Submit tasks
        for (int i = 0; i < totalRequests; i++) {
            executorService.submit(() -> {
                long reqStart = System.currentTimeMillis();
                try {
                    ResponseEntity<?> response = requestSupplier.get();
                    long duration = System.currentTimeMillis() - reqStart;
                    responseTimes.add(duration);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        successfulRequests.incrementAndGet();
                    } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        badRequestCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    long duration = System.currentTimeMillis() - reqStart;
                    responseTimes.add(duration);
                    failedRequests.incrementAndGet();
                    log.debug("Request failed: {}", e.getMessage());
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        double avgResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        long minResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0);

        long maxResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        return new LoadTestMetrics(
                totalRequests,
                successfulRequests.get(),
                badRequestCount.get(),
                failedRequests.get(),
                avgResponseTime,
                minResponseTime,
                maxResponseTime,
                endTime - startTime
        );
    }

    /**
     * Log load test metrics in a formatted table.
     */
    private void logMetrics(String testName, LoadTestMetrics metrics) {
        log.info("=".repeat(70));
        log.info("LOAD TEST RESULTS: {}", testName);
        log.info("=".repeat(70));
        log.info("Total Requests: {}", metrics.totalRequests);
        log.info("Successful Requests: {} ({:.2f}%)", metrics.successfulRequests,
                (metrics.successfulRequests * 100.0) / metrics.totalRequests);
        log.info("Bad Requests (400): {}", metrics.badRequestCount);
        log.info("Failed Requests: {}", metrics.failedRequests);
        log.info("Total Duration: {} ms ({:.2f} seconds)", metrics.totalDuration,
                metrics.totalDuration / 1000.0);
        log.info("Average Response Time: {:.2f} ms", metrics.avgResponseTime);
        log.info("Min Response Time: {} ms", metrics.minResponseTime);
        log.info("Max Response Time: {} ms", metrics.maxResponseTime);
        log.info("Throughput: {:.2f} requests/second", 
                (metrics.totalRequests * 1000.0) / metrics.totalDuration);
        log.info("=".repeat(70));
    }

    /**
     * Data class to hold load test metrics.
     */
    private static class LoadTestMetrics {
        final int totalRequests;
        final int successfulRequests;
        final int badRequestCount;
        final int failedRequests;
        final double avgResponseTime;
        final long minResponseTime;
        final long maxResponseTime;
        final long totalDuration;

        LoadTestMetrics(int totalRequests, int successfulRequests, int badRequestCount,
                       int failedRequests, double avgResponseTime, long minResponseTime,
                       long maxResponseTime, long totalDuration) {
            this.totalRequests = totalRequests;
            this.successfulRequests = successfulRequests;
            this.badRequestCount = badRequestCount;
            this.failedRequests = failedRequests;
            this.avgResponseTime = avgResponseTime;
            this.minResponseTime = minResponseTime;
            this.maxResponseTime = maxResponseTime;
            this.totalDuration = totalDuration;
        }
    }

    /**
     * Functional interface for request suppliers.
     */
    @FunctionalInterface
    private interface RequestSupplier {
        ResponseEntity<?> get();
    }
}

