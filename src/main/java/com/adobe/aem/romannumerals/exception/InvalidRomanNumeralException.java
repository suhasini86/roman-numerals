package com.adobe.aem.romannumerals.exception;

/**
 * Exception thrown when a request violates domain-specific rules
 * for Roman numeral conversion.
 * <p>
 * This exception represents business validation failures such as:
 * <ul>
 *   <li>Input value outside the supported range (e.g., less than 1 or greater than 3999)</li>
 *   <li>Invalid range inputs where {@code min >= max}</li>
 *   <li>Any other constraints specific to Roman numeral processing</li>
 * </ul>
 * <p>
 * This is an unchecked exception (extends {@link RuntimeException}) and is
 * typically thrown from the service or validation layer and handled globally
 * by {@code GlobalExceptionHandler} to return a structured API error response.
 */
public class InvalidRomanNumeralException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidRomanNumeralException} with the specified detail message.
     *
     * @param message the detail message describing the validation failure
     */
    public InvalidRomanNumeralException(String message) {
        super(message);
    }
}
