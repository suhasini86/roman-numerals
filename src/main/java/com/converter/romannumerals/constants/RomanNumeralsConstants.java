package com.converter.romannumerals.constants;

/**
 * Central repository for all constant values used across the Roman Numerals application.
 * <p>
 * Contains validation boundaries, error messages, and other reusable string literals.
 * This is a utility class and cannot be instantiated.
 */
public class RomanNumeralsConstants {

    private RomanNumeralsConstants() {
        throw new UnsupportedOperationException("Utility class - can not be instantiated.");
    }

    /** Minimum accepted integer value for conversion. */
    public static final int MIN_VALUE = 1;

    /** Maximum accepted integer value for conversion. */
    public static final int MAX_VALUE = 255;

    /** Error message returned when the query parameter is not a valid integer. */
    public static final String INVALID_NUMBER_MESSAGE = "Query must be a valid integer";

    /** Generic internal server error message. */
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";

    /** Error message returned when the HTTP method is not supported for the endpoint. */
    public static final String METHOD_NOT_ALLOWED_MESSAGE = "HTTP method not supported";

    /** Error message returned when the requested resource does not exist. */
    public static final String NOT_FOUND_MESSAGE = "Requested resource not found";

    /** Error message returned when the input integer is outside the valid range. */
    public static final String INVALID_RANGE_ERR_MSG =
            "Query range must be between " + MIN_VALUE + " and " + MAX_VALUE;

    /** Error message returned when the required query parameter is not provided */
    public static final String MISSING_QUERY_ERR_MSG =
            "Query parameter is missing";
}