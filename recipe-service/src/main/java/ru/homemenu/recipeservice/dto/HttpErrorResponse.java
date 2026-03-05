package ru.homemenu.recipeservice.dto;

import lombok.Builder;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
public record HttpErrorResponse(
        Instant timestamp,
        Integer status,
        HttpErrorCode errorCode,
        String error,
        Map<String, List<String>> errors,
        URI path
) {
}
