package com.cartoonkitchen.config;

import com.cartoonkitchen.entity.Ingredient;
import com.cartoonkitchen.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IngredientByIdConverter implements Converter<String, Ingredient> {
    private final IngredientRepository repo;
    @Override
    public Ingredient convert(String source) {
        if (source == null || source.isBlank()) return null;
        return repo.findById(Long.valueOf(source)).orElse(null);
    }
}
