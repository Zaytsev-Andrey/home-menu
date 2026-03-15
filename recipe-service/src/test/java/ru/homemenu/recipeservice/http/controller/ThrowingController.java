package ru.homemenu.recipeservice.http.controller;

import jakarta.persistence.OptimisticLockException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.homemenu.recipeservice.http.exception.BadRequestException;
import ru.homemenu.recipeservice.http.exception.NotFoundException;
import ru.homemenu.recipeservice.http.exception.OptimisticLockValidationException;

import java.rmi.UnexpectedException;
import java.sql.SQLException;
import java.util.UUID;

@Profile("test")
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class ThrowingController {

    record TestDto(@NotBlank String title) {}

    @GetMapping("/constraints")
    void  testConstraint(@RequestParam @NotBlank String constraintName) {
        ConstraintViolationException cause = new ConstraintViolationException("Error message", new SQLException(), constraintName);
        throw new DataIntegrityViolationException("Data integrity violation exception",  cause);
    }

    @PostMapping("/validations")
    void testValidation(@RequestBody @Validated TestDto testDto) {

    }

    @PostMapping("/parse")
    void testParse(@RequestBody @Validated TestDto testDto) {

    }

    @PostMapping("/badRequest")
    void testBadRequest() {
        throw new BadRequestException("Bad Request");
    }

    @PutMapping("/optimisticLock")
    void testOptimisticLock() {
        throw new OptimisticLockValidationException(UUID.randomUUID(), 1L, 0L);
    }

    @GetMapping
    void testNotFound() {
        throw new NotFoundException("Not Found");
    }

    @PostMapping("/unexpected")
    void testUnexpected() {
        throw new RuntimeException("Unexpected");
    }
}
