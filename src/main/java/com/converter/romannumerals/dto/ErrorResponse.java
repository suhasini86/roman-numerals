package com.converter.romannumerals.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;


/**
 * Response DTO for error responses containing timestamp, status, error type, and message.
 * @param timestamp the time when the error occurred
 * @param status the HTTP status code
 * @param error the HTTP error reason phrase
 * @param message a human-readable error message describing the issue
 */
@Schema(description = "Error response returned for invalid requests or server errors")
public record ErrorResponse(

    @Schema(description = "Timestamp when the error occurred", example = "2026-04-28T12:00:00Z")
    Instant timestamp,

    @Schema(description = "HTTP status code", example = "400")
    int status,

    @Schema(description = "HTTP error reason phrase", example = "Bad Request")
    String error,

    @Schema(description = "Human-readable error message", example = "query/min/max values must be between 1 and 3999")
    String message) {

    }