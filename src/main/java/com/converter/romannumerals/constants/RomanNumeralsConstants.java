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

    public static final String QUERY = "query";
    public static final String MIN = "min";
    public static final String MAX = "max";
    /** Minimum accepted integer value for conversion. */
    public static final int MIN_VALUE = 1;
    /** Maximum accepted integer value for conversion. */
    public static final int MAX_VALUE = 3999;

    /** Generic internal server error message. */
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";

    /** Error message returned when the HTTP method is not supported for the endpoint. */
    public static final String METHOD_NOT_ALLOWED_MESSAGE = "HTTP method not supported";

    /** Error message returned when the requested resource does not exist. */
    public static final String NOT_FOUND_MESSAGE = "Requested resource not found";

    /** Error message returned when the input integer is outside the valid range. */
    public static final String INVALID_RANGE_ERR_MSG = " value must be a valid integer between "
            + MIN_VALUE + " and " + MAX_VALUE + ".";
    public static final String QUERY_EMPTY_ERR_MSG = "The 'query' parameter cannot be empty. Please provide a number "
            + "between " + MIN_VALUE + " and " + MAX_VALUE + ".";
    public static final String MIN_MAX_EMPTY_ERR_MSG = "Both 'min' and 'max' parameters cannot be empty. Please provide"
            + " the numbers between " + MIN_VALUE + " and " + MAX_VALUE + ".";
    public static final String INVALID_QUERY_PARAM_COMBINATION_ERR_MSG = "Invalid request. The 'query' and 'min'/'max' "
            + "parameters cannot be used together. Please use either 'query' for a single conversion or both 'min' and 'max' "
            + "for a range conversion.";
    public static final String INVALID_REQUEST_ERR_MSG = "Invalid request. Please provide either a 'query' parameter or both 'min' and 'max' parameters.";
}