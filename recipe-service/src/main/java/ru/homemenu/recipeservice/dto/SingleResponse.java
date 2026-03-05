package ru.homemenu.recipeservice.dto;

public record SingleResponse<T>(
        T data

) {

    public static <T> SingleResponse<T> of(T dto) {
        return new SingleResponse<>(dto);
    }

}
