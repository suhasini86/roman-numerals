package com.converter.romannumerals.exception;


public class InvalidRequestException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidRequestException} with the specified detail message.
     *
     * @param message the detail message describing the validation failure
     */
    public InvalidRequestException(String message) {
        super(message);
    }
}
