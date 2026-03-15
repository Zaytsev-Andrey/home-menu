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
import ru.homemenu.recipeservice.recipe.database.entity.Recipe;
import ru.homemenu.recipeservice.recipe.dto.RecipeCreateDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeReadDto;
import ru.homemenu.recipeservice.recipe.dto.RecipeUpdateDto;
import ru.homemenu.recipeservice.recipe.mapper.RecipeMapper;
import ru.homemenu.recipeservice.recipe.service.RecipeService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    private final RecipeMapper recipeMapper;

    @GetMapping
    public PageResponse<RecipeReadDto> findAll(Pageable pageable) {
        Page<RecipeReadDto> recipeReadDtoPage = recipeService.findAll(pageable)
                .map(recipeMapper::toDto);
        return PageResponse.of(recipeReadDtoPage);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SingleResponse<RecipeReadDto> save(@RequestBody @Validated RecipeCreateDto recipeCreateDto) {
        Recipe sevedRecipe = recipeService.save(recipeCreateDto);
        RecipeReadDto recipeReadDto = recipeMapper.toDto(sevedRecipe);
        return SingleResponse.of(recipeReadDto);
    }

    @PutMapping("/{recipeId}")
    public SingleResponse<RecipeReadDto> update(@PathVariable UUID recipeId,
                                                @RequestBody @Validated RecipeUpdateDto recipeUpdateDto) {
        Recipe recipe = recipeService.update(recipeId, recipeUpdateDto);
        RecipeReadDto recipeReadDto = recipeMapper.toDto(recipe);
        return SingleResponse.of(recipeReadDto);
    }

    @DeleteMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID recipeId, @RequestParam @NotNull Long version) {
        recipeService.delete(recipeId, version);
    }
}
