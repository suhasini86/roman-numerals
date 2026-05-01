package com.converter.romannumerals.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.Instant;

/**
 * Response DTO for error responses containing timestamp, status, error type, and message.
 */
@Getter
@Schema(description = "Error response returned for invalid requests or server errors")
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred", example = "2026-04-28T12:00:00Z")
    private Instant timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "HTTP error reason phrase", example = "Bad Request")
    private String error;

    @Schema(description = "Human-readable error message", example = "Query range must be between 1 and 255")
    private String message;

    /**
     * Constructs a new {@code ErrorResponse}.
     *
     * @param timestamp when the error occurred
     * @param status    HTTP status code
     * @param error     HTTP reason phrase (e.g., "Bad Request")
     * @param message   human-readable error description
     */
    public ErrorResponse(Instant timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }
}