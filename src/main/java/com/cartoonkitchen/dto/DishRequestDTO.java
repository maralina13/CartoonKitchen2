package com.cartoonkitchen.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DishRequestDTO {

    @NotBlank(message = "Название обязательно")
    private String name;

    @NotBlank(message = "Категория обязательна")
    private String categoryName;

    private List<Long> ingredientIds;

    private List<String> newIngredients;

    @NotBlank(message = "URL итогового изображения обязателен")
    private String finalImageUrl;

    private String cartoonImageUrl;

    @NotEmpty(message = "Нужен хотя бы один шаг рецепта")
    private List<@NotBlank(message = "Шаг не может быть пустым") String> stepDescriptions;
}
