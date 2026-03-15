package ru.homemenu.recipeservice.http.exception;

import java.util.UUID;

public class OptimisticLockValidationException extends RuntimeException {

    private static final String MESSAGE_PATTERN = "Optimistic lock conflict for resource %s: expected version %s but was %s";

    public OptimisticLockValidationException(UUID resourceId, Long expectedVersion, Long actualVersion) {
        super(MESSAGE_PATTERN.formatted(resourceId, expectedVersion, actualVersion));
    }
}
