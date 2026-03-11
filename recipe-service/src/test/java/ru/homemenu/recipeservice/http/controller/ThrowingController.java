package ru.homemenu.recipeservice.http.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

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
}
