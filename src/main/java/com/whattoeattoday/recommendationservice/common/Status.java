package com.whattoeattoday.recommendationservice.common;

/**
 * Enum Types of Status Code
 * @author huanglijie
 */
public enum Status implements StatusCode {

    /**
     * Operation Success
     */
    SUCCESS(200, "Operation Success"),

    /**
     * Invalid parameters
     */
    PARAM_ERROR(400, "Invalid parameters"),

    /**
     * Resource not found
     */
    NOT_FOUND(404, "Resource not found"),

    /**
     * Application internal error
     */
    FAILURE(500, "Application internal error");

    private final int code;
    private final String message;

    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
