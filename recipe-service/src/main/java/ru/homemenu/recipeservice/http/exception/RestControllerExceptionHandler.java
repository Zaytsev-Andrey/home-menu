package ru.homemenu.recipeservice.http.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
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

        HttpErrorResponse httpErrorResponse = HttpErrorResponse.builder()
                .path(URI.create(request.getRequestURI()))
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(HttpErrorCode.REQUEST_VALIDATION_FAILED)
                .timestamp(Instant.now())
                .errors(errors)
                .build();

        logRequestValidationFailed(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(httpErrorResponse);
    }

    private static void logRequestValidationFailed(Object errors) {
        log.atWarn()
                .addKeyValue(StructuredLogField.EVENT, StructuredLogEvent.REQUEST_VALIDATION_FAILED)
                .addKeyValue(StructuredLogField.ERROR_CODE, HttpErrorCode.REQUEST_VALIDATION_FAILED)
                .addKeyValue(StructuredLogField.ERRORS, errors)
                .log("Request validation failed");
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

    @ExceptionHandler(OptimisticLockValidationException.class)
    ResponseEntity<HttpErrorResponse> handleOptimisticLockValidationException(OptimisticLockValidationException ex, HttpServletRequest request) {
        HttpErrorResponse.HttpErrorResponseBuilder errorResponseBuilder = HttpErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .errorCode(HttpErrorCode.OPTIMISTIC_LOCK_ERROR)
                .error(ex.getMessage())
                .path(URI.create(request.getRequestURI()));

        logOptimisticLockError(ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorResponseBuilder.build());
    }

    private void logOptimisticLockError(String error) {
        log.atWarn()
                .addKeyValue(StructuredLogField.EVENT, StructuredLogEvent.OPTIMISTIC_LOCK_ERROR)
                .addKeyValue(StructuredLogField.ERROR_CODE, HttpErrorCode.OPTIMISTIC_LOCK_ERROR)
                .addKeyValue(StructuredLogField.ERRORS, error)
                .log("Optimistic lock error");
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ResponseEntity<HttpErrorResponse> handleObjectOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        HttpErrorResponse.HttpErrorResponseBuilder errorResponseBuilder = HttpErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .errorCode(HttpErrorCode.OPTIMISTIC_LOCK_ERROR)
                .error("Optimistic lock error")
                .path(URI.create(request.getRequestURI()));

        logOptimisticLockError(ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorResponseBuilder.build());
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

    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<HttpErrorResponse> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        HttpErrorResponse httpErrorResponse = HttpErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(HttpErrorCode.REQUEST_VALIDATION_FAILED)
                .error(ex.getMessage())
                .path(URI.create(request.getRequestURI()))
                .build();

        logRequestValidationFailed(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(httpErrorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<HttpErrorResponse> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        HttpErrorResponse httpErrorResponse = HttpErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode(HttpErrorCode.RESOURCE_NOT_FOUND)
                .error(ex.getMessage())
                .path(URI.create(request.getRequestURI()))
                .build();

        log.atWarn()
                .addKeyValue(StructuredLogField.EVENT, StructuredLogEvent.RESOURCE_NOT_FOUND)
                .addKeyValue(StructuredLogField.ERROR_CODE, HttpErrorCode.RESOURCE_NOT_FOUND)
                .addKeyValue(StructuredLogField.ERRORS, ex.getMessage())
                .log("Resource not found");

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(httpErrorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<HttpErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, HttpServletRequest request) {
        HttpErrorResponse httpErrorResponse = HttpErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(HttpErrorCode.MISSING_REQUEST_PARAMETER)
                .error(ex.getMessage())
                .path(URI.create(request.getRequestURI()))
                .build();

        log.atWarn()
                .addKeyValue(StructuredLogField.EVENT, StructuredLogEvent.MISSING_REQUEST_PARAMETER)
                .addKeyValue(StructuredLogField.ERROR_CODE, HttpErrorCode.MISSING_REQUEST_PARAMETER)
                .addKeyValue(StructuredLogField.ERRORS, ex.getMessage())
                .setCause(ex)
                .log("Missing request parameter");

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
