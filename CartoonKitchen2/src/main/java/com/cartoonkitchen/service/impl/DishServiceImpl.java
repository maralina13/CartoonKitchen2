package com.cartoonkitchen.service.impl;

import com.cartoonkitchen.entity.Category;
import com.cartoonkitchen.entity.Dish;
import com.cartoonkitchen.entity.Step;
import com.cartoonkitchen.entity.Ingredient;
import com.cartoonkitchen.repository.DishRepository;
import com.cartoonkitchen.service.DishService;
import com.cartoonkitchen.repository.IngredientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.cartoonkitchen.repository.CategoryRepository;
import com.cartoonkitchen.repository.StepRepository;
import com.cartoonkitchen.dto.DishRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final StepRepository stepRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    @Override
    public void saveDish(Dish dish) {
        dishRepository.save(dish);
    }

    @Override
    public void saveDishWithSteps(String name,
                                  String categoryName,
                                  String recipe,
                                  byte[] finalImage,
                                  byte[] cartoonImage,
                                  List<String> stepDescriptions,
                                  List<byte[]> stepImages) {

        Category category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(categoryName);
                    return categoryRepository.save(newCategory);
                });

        Dish dish = Dish.builder()
                .name(name)
                .category(category)
                .image(finalImage)
                .cartoonImage(cartoonImage)
                .build();

        dish = dishRepository.save(dish);

        for (int i = 0; i < stepDescriptions.size(); i++) {
            Step step = Step.builder()
                    .description(stepDescriptions.get(i))
                    .image(stepImages.get(i))
                    .dish(dish)
                    .build();

            stepRepository.save(step);
        }
    }
    @Override
    @Transactional
    public void saveFullDish(DishRequestDTO dto) throws IOException {
        // 1. Проверяем/создаём категорию
        Category category = categoryRepository.findByName(dto.getCategoryName())
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(dto.getCategoryName());
                    return categoryRepository.save(newCategory);
                });

        // 2. Создаём объект блюда
        Dish dish = Dish.builder()
                .name(dto.getName())
                .category(category)
                .image(dto.getFinalImage() != null ? dto.getFinalImage().getBytes() : null)
                .cartoonImage(dto.getCartoonImage() != null ? dto.getCartoonImage().getBytes() : null)
                .build();

        List<Ingredient> selectedIngredients = ingredientRepository.findAllById(dto.getIngredientIds());
        dish.setIngredients(selectedIngredients);

        List<Ingredient> ingredients = new ArrayList<>();

// 1. существующие
        if (dto.getIngredientIds() != null) {
            ingredients.addAll(ingredientRepository.findAllById(dto.getIngredientIds()));
        }

// 2. новые
        if (dto.getNewIngredients() != null) {
            for (String name : dto.getNewIngredients()) {
                Ingredient existing = ingredientRepository.findByName(name).orElse(null);
                if (existing == null) {
                    existing = ingredientRepository.save(new Ingredient(name));
                }
                ingredients.add(existing);
            }
        }

        dish.setIngredients(ingredients);


        // 3. Преобразуем шаги
        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < dto.getStepDescriptions().size(); i++) {
            Step step = new Step();
            step.setDescription(dto.getStepDescriptions().get(i));
            if (dto.getStepImages() != null && i < dto.getStepImages().size()) {
                if (dto.getStepImages() != null && i < dto.getStepImages().size()) {
                    MultipartFile stepImage = dto.getStepImages().get(i);
                    if (stepImage != null && !stepImage.isEmpty()) {
                        step.setImage(stepImage.getBytes());
                    }
                }

            }
            step.setDish(dish);
            steps.add(step);
        }

        // Формируем текст рецепта из шагов
        StringBuilder recipeText = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            recipeText.append("Шаг ").append(i + 1).append(": ")
                    .append(steps.get(i).getDescription()).append("\n");
        }
        dish.setRecipe(recipeText.toString());

        // 4. Добавляем шаги и сохраняем всё
        dish.setSteps(steps);
        dishRepository.save(dish);
    }

}
