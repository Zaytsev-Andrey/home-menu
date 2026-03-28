package ru.homemenu.recipeservice.http.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.homemenu.recipeservice.config.property.HttpErrorMessageProperty;
import ru.homemenu.recipeservice.dto.HttpErrorCode;
import ru.homemenu.recipeservice.dto.HttpErrorResponse;
import ru.homemenu.recipeservice.log.StructuredLogEvent;
import ru.homemenu.recipeservice.log.StructuredLogField;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class RestControllerExceptionHandler {

    private final HttpErrorMessageProperty httpErrorMessageProperty;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<HttpErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.computeIfAbsent(error.getField(), _ -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        }

        HttpErrorResponse httpErrorResponse = errorResponseBuilder(HttpStatus.BAD_REQUEST, HttpErrorCode.REQUEST_VALIDATION_FAILED, request.getRequestURI())
                .errors(errors)
                .build();

        warnLogBuilder(StructuredLogEvent.REQUEST_VALIDATION_FAILED, HttpErrorCode.REQUEST_VALIDATION_FAILED)
                .addKeyValue(StructuredLogField.ERRORS, errors)
                .log("Request validation failed");

        return createResponseEntity(HttpStatus.BAD_REQUEST, httpErrorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<HttpErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        HttpErrorResponse.HttpErrorResponseBuilder errorResponseBuilder = errorResponseBuilder(HttpStatus.CONFLICT, HttpErrorCode.CONSTRAINT_VIOLATION, request.getRequestURI());

        String constraintName = getConstraintName(ex);
        Map<String, List<String>> errors = httpErrorMessageProperty.constraints().get(constraintName);
        if (errors != null) {
            errorResponseBuilder.errors(errors);
        } else {
            errorResponseBuilder.error(httpErrorMessageProperty.unknownConstraintMessage());
        }

        warnLogBuilder(StructuredLogEvent.CONSTRAINT_VIOLATION, HttpErrorCode.CONSTRAINT_VIOLATION)
                .addKeyValue(StructuredLogField.CONSTRAINT_NAME, constraintName)
                .log("Constraint violation");

        return createResponseEntity(HttpStatus.CONFLICT, errorResponseBuilder.build());
    }

    @ExceptionHandler(ConstraintConflictException.class)
    ResponseEntity<HttpErrorResponse> handleConstraintConflictException(ConstraintConflictException ex, HttpServletRequest request) {
        HttpErrorResponse.HttpErrorResponseBuilder errorResponseBuilder = errorResponseBuilder(HttpStatus.CONFLICT, HttpErrorCode.CONSTRAINT_VIOLATION, request.getRequestURI())
                .error(ex.getMessage());

        warnLogBuilder(StructuredLogEvent.CONSTRAINT_VIOLATION, HttpErrorCode.CONSTRAINT_VIOLATION)
                .log(ex.getMessage());

        return createResponseEntity(HttpStatus.CONFLICT, errorResponseBuilder.build());
    }

    private String getConstraintName(DataIntegrityViolationException ex) {
        String constraintName = null;
        if (ex.getCause() instanceof ConstraintViolationException constraintViolationException) {
            constraintName = constraintViolationException.getConstraintName();
        }

        return constraintName;
    }

    @ExceptionHandler(OptimisticLockValidationException.class)
    ResponseEntity<HttpErrorResponse> handleOptimisticLockValidationException(OptimisticLockValidationException ex, HttpServletRequest request) {
        HttpErrorResponse.HttpErrorResponseBuilder errorResponseBuilder = errorResponseBuilder(HttpStatus.CONFLICT, HttpErrorCode.OPTIMISTIC_LOCK_ERROR, request.getRequestURI())
                .error(ex.getMessage());

        warnLogBuilder(StructuredLogEvent.OPTIMISTIC_LOCK_ERROR, HttpErrorCode.OPTIMISTIC_LOCK_ERROR)
                .addKeyValue(StructuredLogField.ERROR, ex.getMessage())
                .log("Optimistic lock error");

        return createResponseEntity(HttpStatus.CONFLICT, errorResponseBuilder.build());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ResponseEntity<HttpErrorResponse> handleObjectOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        HttpErrorResponse.HttpErrorResponseBuilder errorResponseBuilder = errorResponseBuilder(HttpStatus.CONFLICT, HttpErrorCode.OPTIMISTIC_LOCK_ERROR, request.getRequestURI())
                .error("Optimistic lock error");

        warnLogBuilder(StructuredLogEvent.OPTIMISTIC_LOCK_ERROR, HttpErrorCode.OPTIMISTIC_LOCK_ERROR)
                .addKeyValue(StructuredLogField.ERROR, ex.getMessage())
                .log("Optimistic lock error");

        return createResponseEntity(HttpStatus.CONFLICT, errorResponseBuilder.build());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<HttpErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        HttpErrorResponse httpErrorResponse = errorResponseBuilder(HttpStatus.NOT_FOUND, HttpErrorCode.RESOURCE_NOT_FOUND, request.getRequestURI())
                .error("Resource not found")
                .build();

        warnLogBuilder(StructuredLogEvent.RESOURCE_NOT_FOUND, HttpErrorCode.RESOURCE_NOT_FOUND)
                .addKeyValue(StructuredLogField.ERROR, ex.getMessage())
                .log("Resource not found");

        return createResponseEntity(HttpStatus.NOT_FOUND, httpErrorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<HttpErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String errorMessage = ex.getMostSpecificCause().getMessage();
        HttpErrorResponse httpErrorResponse = errorResponseBuilder(HttpStatus.BAD_REQUEST, HttpErrorCode.JSON_PARSE_ERROR, request.getRequestURI())
                .error("Malformed JSON request")
                .build();

        warnLogBuilder(StructuredLogEvent.JSON_PARSE_ERROR, HttpErrorCode.JSON_PARSE_ERROR)
                .addKeyValue(StructuredLogField.ERROR, errorMessage)
                .log("JSON parse error");

        return createResponseEntity(HttpStatus.BAD_REQUEST, httpErrorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<HttpErrorResponse> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        HttpErrorResponse httpErrorResponse = errorResponseBuilder(HttpStatus.BAD_REQUEST, HttpErrorCode.REQUEST_VALIDATION_FAILED, request.getRequestURI())
                .error(ex.getMessage())
                .build();

        warnLogBuilder(StructuredLogEvent.REQUEST_VALIDATION_FAILED, HttpErrorCode.REQUEST_VALIDATION_FAILED)
                .addKeyValue(StructuredLogField.ERROR, ex.getMessage())
                .log("Request validation failed");

        return createResponseEntity(HttpStatus.BAD_REQUEST, httpErrorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<HttpErrorResponse> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        HttpErrorResponse httpErrorResponse = errorResponseBuilder(HttpStatus.NOT_FOUND, HttpErrorCode.RESOURCE_NOT_FOUND, request.getRequestURI())
                .error(ex.getMessage())
                .build();

        warnLogBuilder(StructuredLogEvent.RESOURCE_NOT_FOUND, HttpErrorCode.RESOURCE_NOT_FOUND)
                .addKeyValue(StructuredLogField.ERROR, ex.getMessage())
                .log("Resource not found");

        return createResponseEntity(HttpStatus.NOT_FOUND, httpErrorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<HttpErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        HttpErrorResponse httpErrorResponse = errorResponseBuilder(HttpStatus.BAD_REQUEST, HttpErrorCode.MISSING_REQUEST_PARAMETER, request.getRequestURI())
                .error("Missing request parameter: " + ex.getParameterName())
                .build();

        warnLogBuilder(StructuredLogEvent.MISSING_REQUEST_PARAMETER, HttpErrorCode.MISSING_REQUEST_PARAMETER)
                .addKeyValue(StructuredLogField.ERROR, ex.getMessage())
                .setCause(ex)
                .log("Missing request parameter");

        return createResponseEntity(HttpStatus.BAD_REQUEST, httpErrorResponse);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<HttpErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        HttpErrorResponse errorResponse = errorResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR, HttpErrorCode.UNEXPECTED_ERROR, request.getRequestURI())
                .error("Internal server error")
                .build();

        errorLogBuilder(StructuredLogEvent.UNEXPECTED_ERROR, HttpErrorCode.UNEXPECTED_ERROR)
                .setCause(ex)
                .log("Internal server error");

        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse);
    }

    private HttpErrorResponse.HttpErrorResponseBuilder errorResponseBuilder(HttpStatus status, HttpErrorCode errorCode, String path) {
        return HttpErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .errorCode(errorCode)
                .path(URI.create(path));
    }

    private LoggingEventBuilder errorLogBuilder(String event, HttpErrorCode errorCode) {
        return log.atError()
                .addKeyValue(StructuredLogField.EVENT, event)
                .addKeyValue(StructuredLogField.ERROR_CODE, errorCode);
    }

    private LoggingEventBuilder warnLogBuilder(String event, HttpErrorCode errorCode) {
        return log.atWarn()
                .addKeyValue(StructuredLogField.EVENT, event)
                .addKeyValue(StructuredLogField.ERROR_CODE, errorCode);
    }

    private ResponseEntity<HttpErrorResponse> createResponseEntity(HttpStatus status, HttpErrorResponse errorResponse) {
        return ResponseEntity.status(status)
                .body(errorResponse);
    }
}
