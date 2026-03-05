package ru.homemenu.recipeservice.http.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.homemenu.recipeservice.config.property.HttpErrorMessageProperty;
import ru.homemenu.recipeservice.dto.HttpErrorCode;
import ru.homemenu.recipeservice.dto.HttpErrorResponse;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                .errorCode(HttpErrorCode.VALIDATION_ERROR)
                .timestamp(Instant.now())
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(httpErrorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<HttpErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        HttpErrorResponse.HttpErrorResponseBuilder errorResponseBuilder = HttpErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .errorCode(HttpErrorCode.CONSTRAINT_VIOLATION_ERROR)
                .path(URI.create(request.getRequestURI()));

        Map<String, List<String>> errors = httpErrorMessageProperty.constraints().get(ex.getConstraintName());
        if (errors != null) {
            errorResponseBuilder.errors(errors);
        } else {
            errorResponseBuilder.error(httpErrorMessageProperty.unknownConstraintMessage());
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorResponseBuilder.build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<HttpErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        HttpErrorResponse httpErrorResponse = HttpErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(HttpErrorCode.JSON_PARSE_ERROR)
                .error(ex.getMessage())
                .path(URI.create(request.getRequestURI()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(httpErrorResponse);
    }
}
