package ru.homemenu.recipeservice.http.exception;

public class ConstraintConflictException extends RuntimeException {

    public ConstraintConflictException(String message) {
        super(message);
    }
}
