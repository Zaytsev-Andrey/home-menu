package ru.homemenu.recipeservice.log;

public final class StructuredLogEvent {

    private StructuredLogEvent() {
    }

    public static final String CONSTRAINT_VIOLATION = "constraint_violation";
    public static final String JSON_PARSE_ERROR = "json_parse_error";
    public static final String MISSING_REQUEST_PARAMETER = "missing_request_parameter";
    public static final String OPTIMISTIC_LOCK_ERROR = "optimistic_lock_error";
    public static final String REQUEST_VALIDATION_FAILED = "request_validation_failed";
    public static final String RESOURCE_NOT_FOUND = "resource_not_found";
    public static final String UNEXPECTED_ERROR = "unexpected_error";

}
