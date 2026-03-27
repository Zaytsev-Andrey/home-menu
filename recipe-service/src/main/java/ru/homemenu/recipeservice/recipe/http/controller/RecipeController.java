package ru.homemenu.recipeservice.recipe.http.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.homemenu.recipeservice.dto.PageResponse;
import ru.homemenu.recipeservice.dto.SingleResponse;
import ru.homemenu.recipeservice.recipe.dto.RecipeCreateDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeFilter;
import ru.homemenu.recipeservice.recipe.dto.RecipeReadDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeUpdateDto;
import ru.homemenu.recipeservice.recipe.http.exception.RecipeNotFoundException;
import ru.homemenu.recipeservice.recipe.service.RecipeService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public PageResponse<RecipeReadDto> findAll(RecipeFilter filter, Pageable pageable) {
        Page<RecipeReadDto> recipeReadDtoPage = recipeService.findAll(filter, pageable);
        return PageResponse.of(recipeReadDtoPage);
    }

    @GetMapping("/{recipeId}")
    public SingleResponse<RecipeReadDto> findById(@PathVariable UUID recipeId) {
        RecipeReadDto recipeReadDto = recipeService.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));
        return SingleResponse.of(recipeReadDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SingleResponse<RecipeReadDto> save(@RequestBody @Validated RecipeCreateDto recipeCreateDto) {
        RecipeReadDto recipeReadDto = recipeService.save(recipeCreateDto);
        return SingleResponse.of(recipeReadDto);
    }

    @PutMapping("/{recipeId}")
    public SingleResponse<RecipeReadDto> update(@PathVariable UUID recipeId,
                                                @RequestBody @Validated RecipeUpdateDto recipeUpdateDto) {
        RecipeReadDto recipeReadDto = recipeService.update(recipeId, recipeUpdateDto);
        return SingleResponse.of(recipeReadDto);
    }

    @DeleteMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID recipeId, @RequestParam @NotNull Long version) {
        recipeService.delete(recipeId, version);
    }
}
