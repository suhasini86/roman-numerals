package com.adobe.aem.romannumerals.constants;

public class RomanNumeralsConstants {

    private RomanNumeralsConstants() {
        throw new UnsupportedOperationException("Utility class - can not be instantiated.");
    }
    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 255; //
    public static final String INVALID_NUMBER_MESSAGE = "Query must be a valid integer";
    public static final String EMPTY_QUERY = "Query parameter cannot be empty";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    public static final String MALFORMED_QUERY_MESSAGE = "Malformed Query";
    public static final String INVALID_RANGE_ERR_MSG =
            "Query range must be between " + MIN_VALUE + " and " + MAX_VALUE;
}
