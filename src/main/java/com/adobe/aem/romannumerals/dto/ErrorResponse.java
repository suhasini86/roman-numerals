package com.adobe.aem.romannumerals.dto;

import lombok.Getter;

import java.time.Instant;

/**
 * Response DTO for error responses containing timne stamp, status, error type and message
 * */

@Getter
public class ErrorResponse {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;

    public ErrorResponse(Instant timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

}