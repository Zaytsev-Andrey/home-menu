package ru.homemenu.recipeservice.http.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

        HttpErrorResponse httpErrorResponse = HttpErrorResponse.builder()
                .path(URI.create(request.getRequestURI()))
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(HttpErrorCode.REQUEST_VALIDATION_FAILED)
                .timestamp(Instant.now())
                .errors(errors)
                .build();

        log.atWarn()
                .addKeyValue(StructuredLogField.EVENT, StructuredLogEvent.REQUEST_VALIDATION_FAILED)
                .addKeyValue(StructuredLogField.ERROR_CODE, HttpErrorCode.REQUEST_VALIDATION_FAILED)
                .addKeyValue(StructuredLogField.ERRORS, errors)
                .log("Request validation failed");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(httpErrorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<HttpErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        HttpErrorResponse.HttpErrorResponseBuilder errorResponseBuilder = HttpErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .errorCode(HttpErrorCode.CONSTRAINT_VIOLATION)
                .path(URI.create(request.getRequestURI()));

        String constraintName = getConstraintName(ex);
        Map<String, List<String>> errors = httpErrorMessageProperty.constraints().get(constraintName);
        if (errors != null) {
            errorResponseBuilder.errors(errors);
        } else {
            errorResponseBuilder.error(httpErrorMessageProperty.unknownConstraintMessage());
        }

        log.atWarn()
                .addKeyValue(StructuredLogField.EVENT, StructuredLogEvent.CONSTRAINT_VIOLATION)
                .addKeyValue(StructuredLogField.ERROR_CODE, HttpErrorCode.CONSTRAINT_VIOLATION)
                .addKeyValue(StructuredLogField.CONSTRAINT_NAME, constraintName)
                .log("Constraint violation");

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorResponseBuilder.build());
    }

    private String getConstraintName(DataIntegrityViolationException ex) {
        String constraintName = null;
        if (ex.getCause() instanceof ConstraintViolationException constraintViolationException) {
            constraintName = constraintViolationException.getConstraintName();
        }

        return constraintName;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<HttpErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        HttpErrorResponse httpErrorResponse = HttpErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode(HttpErrorCode.RESOURCE_NOT_FOUND)
                .error("Resource not found")
                .path(URI.create(request.getRequestURI()))
                .build();

        log.atWarn()
                .addKeyValue(StructuredLogField.EVENT, StructuredLogEvent.RESOURCE_NOT_FOUND)
                .addKeyValue(StructuredLogField.ERROR_CODE, HttpErrorCode.RESOURCE_NOT_FOUND)
                .log("Resource not found");

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(httpErrorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<HttpErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String errorMessage = ex.getMostSpecificCause().getMessage();
        HttpErrorResponse httpErrorResponse = HttpErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(HttpErrorCode.JSON_PARSE_ERROR)
                .error(errorMessage)
                .path(URI.create(request.getRequestURI()))
                .build();

        log.atWarn()
                .addKeyValue(StructuredLogField.EVENT, StructuredLogEvent.JSON_PARSE_ERROR)
                .addKeyValue(StructuredLogField.ERROR_CODE, HttpErrorCode.JSON_PARSE_ERROR)
                .addKeyValue(StructuredLogField.ERRORS, errorMessage)
                .log("JSON parse error");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(httpErrorResponse);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<HttpErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        HttpErrorResponse errorResponseBuilder = HttpErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode(HttpErrorCode.UNEXPECTED_ERROR)
                .error("Internal server error")
                .path(URI.create(request.getRequestURI()))
                .build();

        log.atError()
                .addKeyValue(StructuredLogField.EVENT, StructuredLogEvent.UNEXPECTED_ERROR)
                .addKeyValue(StructuredLogField.ERROR_CODE, HttpErrorCode.UNEXPECTED_ERROR)
                .setCause(ex)
                .log("Internal server error");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponseBuilder);
    }

}
