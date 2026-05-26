package com.converter.romannumerals.exception;


/**
 * Thrown when a client request violates business rules beyond bean validation,
 * such as incompatible query parameters or an invalid numeric range boundary.
 */
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
