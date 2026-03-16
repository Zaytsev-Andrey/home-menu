package ru.homemenu.recipeservice.ingredient.http.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.homemenu.recipeservice.dto.PageResponse;
import ru.homemenu.recipeservice.dto.SingleResponse;
import ru.homemenu.recipeservice.ingredient.dto.IngredientCreateDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientReadDto;
import ru.homemenu.recipeservice.ingredient.dto.IngredientUpdateDto;
import ru.homemenu.recipeservice.ingredient.http.exception.IngredientNotFoundException;
import ru.homemenu.recipeservice.ingredient.service.IngredientService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public PageResponse<IngredientReadDto> findAll(Pageable pageable) {
        Page<IngredientReadDto> ingredientReadDtoPage = ingredientService.findAll(pageable);
        return PageResponse.of(ingredientReadDtoPage);
    }

    @GetMapping("/{ingredientId}")
    public SingleResponse<IngredientReadDto> findById(@PathVariable UUID ingredientId) {
        IngredientReadDto ingredientReadDto = ingredientService.findById(ingredientId)
                .orElseThrow(() -> new IngredientNotFoundException(ingredientId));
        return SingleResponse.of(ingredientReadDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SingleResponse<IngredientReadDto> create(@RequestBody @Validated IngredientCreateDto ingredientCreateDto) {
        IngredientReadDto ingredientReadDto = ingredientService.save(ingredientCreateDto);
        return SingleResponse.of(ingredientReadDto);
    }

    @PutMapping("/{ingredientId}")
    public SingleResponse<IngredientReadDto> update(@PathVariable UUID ingredientId,
                                                    @RequestBody @Validated IngredientUpdateDto ingredientUpdateDto) {
        IngredientReadDto ingredientReadDto = ingredientService.update(ingredientId, ingredientUpdateDto);
        return SingleResponse.of(ingredientReadDto);
    }

    @DeleteMapping("/{ingredientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID ingredientId,
                                                    @RequestParam @NotNull Long version) {
        ingredientService.delete(ingredientId, version);
    }
}
