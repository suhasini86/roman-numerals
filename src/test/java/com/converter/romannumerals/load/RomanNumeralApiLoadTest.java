package com.converter.romannumerals.load;

import com.converter.romannumerals.RomanNumeralsApplication;
import com.converter.romannumerals.dto.RomanNumeralResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(
        classes = RomanNumeralsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class RomanNumeralApiLoadTest {

    private static final String BASE_URL = "/romannumeral?query=";
    private static final int TOTAL_REQUESTS = 400;
    private static final int CONCURRENCY = 20;
    private static final int TEST_TIMEOUT_SECONDS = 30;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("API should sustain concurrent conversion requests")
    void shouldHandleConcurrentLoad() throws InterruptedException {

        log.info("Starting load test: totalRequests={}, concurrency={}", TOTAL_REQUESTS, CONCURRENCY);

        ExecutorService pool = Executors.newFixedThreadPool(CONCURRENCY);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();
        AtomicLong cumulativeLatencyMs = new AtomicLong();

        long testStart = System.currentTimeMillis();

        try {
            List<Callable<Void>> tasks = new ArrayList<>();

            for (int i = 0; i < TOTAL_REQUESTS; i++) {
                final int input = (i % 255) + 1;

                tasks.add(() -> {
                    long start = System.nanoTime();

                    try {
                        ResponseEntity<RomanNumeralResponse> response =
                                restTemplate.getForEntity(BASE_URL + input, RomanNumeralResponse.class);

                        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                        cumulativeLatencyMs.addAndGet(elapsedMs);

                        if (isSuccessfulResponse(response, input)) {
                            successCount.incrementAndGet();

                            log.debug("SUCCESS | input={} | latency={}ms", input, elapsedMs);
                        } else {
                            failureCount.incrementAndGet();

                            log.warn("FAILURE | input={} | status={} | body={}",
                                    input,
                                    response.getStatusCode(),
                                    response.getBody());
                        }

                    } catch (Exception ex) {
                        failureCount.incrementAndGet();

                        log.error("ERROR | input={} | message={}", input, ex.getMessage(), ex);
                    }

                    return null;
                });
            }

            List<Future<Void>> futures =
                    pool.invokeAll(tasks, TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            for (Future<Void> future : futures) {
                if (!future.isCancelled()) {
                    try {
                        future.get();
                    } catch (ExecutionException ex) {
                        failureCount.incrementAndGet();
                        log.error("Execution exception: {}", ex.getMessage(), ex);
                    }
                } else {
                    failureCount.incrementAndGet();
                    log.warn("Task cancelled due to timeout");
                }
            }

        } finally {
            shutdownExecutor(pool);
        }

        long totalTime = System.currentTimeMillis() - testStart;
        long averageLatencyMs = cumulativeLatencyMs.get() / TOTAL_REQUESTS;

        log.info("Load test completed in {} ms", totalTime);
        log.info("Results: success={}, failure={}", successCount.get(), failureCount.get());
        log.info("Average latency: {} ms", averageLatencyMs);

        assertEquals(0, failureCount.get(),
                "Expected zero failed requests, got " + failureCount.get());

        assertEquals(TOTAL_REQUESTS, successCount.get(),
                "Expected " + TOTAL_REQUESTS + " successful requests, got " + successCount.get());

        assertTrue(averageLatencyMs < 250,
                "Average latency too high: " + averageLatencyMs + "ms");
    }

    private boolean isSuccessfulResponse(ResponseEntity<RomanNumeralResponse> response, int input) {
        return response.getStatusCode() == HttpStatus.OK
                && response.getBody() != null
                && String.valueOf(input).equals(response.getBody().getInput())
                && response.getBody().getOutput() != null
                && !response.getBody().getOutput().isBlank();
    }

    private void shutdownExecutor(ExecutorService pool) throws InterruptedException {
        log.info("Shutting down executor service");

        pool.shutdown();

        if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
            log.warn("Forcing shutdown of executor");
            pool.shutdownNow();

            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                log.error("Executor did not terminate cleanly");
                throw new IllegalStateException("Executor service did not terminate");
            }
        }

        log.info("Executor shutdown complete");
    }
}