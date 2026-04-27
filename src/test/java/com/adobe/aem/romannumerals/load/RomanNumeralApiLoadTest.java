package com.adobe.aem.romannumerals.load;

import com.adobe.aem.romannumerals.RomanNumeralsApplication;
import com.adobe.aem.romannumerals.dto.RomanNumeralResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = RomanNumeralsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RomanNumeralApiLoadTest {

    private static final String BASE_URL = "/romannumeral?query=";
    private static final int TOTAL_REQUESTS = 400;
    private static final int CONCURRENCY = 20;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("API should sustain concurrent conversion requests")
    void shouldHandleConcurrentLoad() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService pool = Executors.newFixedThreadPool(CONCURRENCY);
        List<Callable<Void>> tasks = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();
        AtomicLong cumulativeLatencyMs = new AtomicLong();

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            final int input = (i % 255) + 1;
            tasks.add(() -> {
                long start = System.nanoTime();
                ResponseEntity<RomanNumeralResponse> response =
                        restTemplate.getForEntity(BASE_URL + input, RomanNumeralResponse.class);
                long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                cumulativeLatencyMs.addAndGet(elapsedMs);

                if (response.getStatusCode() == HttpStatus.OK
                        && response.getBody() != null
                        && String.valueOf(input).equals(response.getBody().getInput())
                        && response.getBody().getOutput() != null
                        && !response.getBody().getOutput().isBlank()) {
                    successCount.incrementAndGet();
                } else {
                    failureCount.incrementAndGet();
                }
                return null;
            });
        }

        List<Future<Void>> futures = pool.invokeAll(tasks);
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));

        for (Future<Void> future : futures) {
            future.get(2, TimeUnit.SECONDS);
        }

        long averageLatencyMs = cumulativeLatencyMs.get() / TOTAL_REQUESTS;
        assertTrue(failureCount.get() == 0, "Expected zero failed requests, got " + failureCount.get());
        assertTrue(successCount.get() == TOTAL_REQUESTS,
                "Expected " + TOTAL_REQUESTS + " successful requests, got " + successCount.get());
        assertTrue(averageLatencyMs < 250, "Average latency too high: " + averageLatencyMs + "ms");
    }
}
