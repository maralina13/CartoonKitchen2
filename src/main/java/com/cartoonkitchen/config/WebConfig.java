package com.cartoonkitchen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final IngredientByIdConverter ingredientByIdConverter;
    public WebConfig(IngredientByIdConverter ingredientByIdConverter) {
        this.ingredientByIdConverter = ingredientByIdConverter;
    }
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(ingredientByIdConverter);
    }
}
