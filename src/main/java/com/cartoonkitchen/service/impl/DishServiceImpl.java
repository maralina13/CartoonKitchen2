package com.cartoonkitchen.service.impl;

import com.cartoonkitchen.dto.DishRequestDTO;
import com.cartoonkitchen.entity.Category;
import com.cartoonkitchen.entity.Dish;
import com.cartoonkitchen.entity.Ingredient;
import com.cartoonkitchen.entity.Step;
import com.cartoonkitchen.entity.User;
import com.cartoonkitchen.repository.CategoryRepository;
import com.cartoonkitchen.repository.DishRepository;
import com.cartoonkitchen.repository.IngredientRepository;
import com.cartoonkitchen.repository.StepRepository;
import com.cartoonkitchen.repository.UserRepository;
import com.cartoonkitchen.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final StepRepository stepRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    @Override
    public void saveDish(Dish dish) {
        dishRepository.save(dish);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id);
    }

    @Override
    public void saveFullDish(DishRequestDTO dto) {
        Dish dish = new Dish();
        dish.setName(dto.getName());

        var catName = safe(dto.getCategoryName());
        if (catName != null) {
            Category category = categoryRepository.findByName(catName)
                    .orElseGet(() -> categoryRepository.save(new Category(catName)));
            dish.setCategory(category);
        } else {
            dish.setCategory(null);
        }

        dish.setImageUrl(safe(dto.getFinalImageUrl()));
        dish.setCartoonImageUrl(safe(dto.getCartoonImageUrl()));

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            userRepository.findByUsername(username).ifPresent(dish::setAuthor);
        }

        dish = dishRepository.save(dish);

        var normalizedSteps = normalizeSteps(dto.getStepDescriptions());
        persistStepsForDish(dish, normalizedSteps);
        dish.setRecipe(String.join("\n", normalizedSteps));

        List<Ingredient> ingredients = new ArrayList<>();
        if (dto.getIngredientIds() != null) {
            ingredients.addAll(ingredientRepository.findAllById(dto.getIngredientIds()));
        }
        if (dto.getNewIngredients() != null) {
            for (String name : dto.getNewIngredients()) {
                var norm = safe(name);
                if (norm == null) continue;
                Ingredient ingredient = ingredientRepository.findByName(norm)
                        .orElseGet(() -> ingredientRepository.save(new Ingredient(norm)));
                ingredients.add(ingredient);
            }
        }
        dish.setIngredients(ingredients);

        dishRepository.save(dish);
    }

    @Override
    public void deleteById(Long id) {
        stepRepository.deleteAllByDishId(id);
        dishRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void update(Long id, DishRequestDTO f) {
        Dish d = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Блюдо не найдено"));

        d.setName(f.getName());

        Category c = categoryRepository.findByName(f.getCategoryName())
                .orElseGet(() -> categoryRepository.save(new Category(f.getCategoryName())));
        d.setCategory(c);

        d.setImageUrl(f.getFinalImageUrl());
        d.setCartoonImageUrl(f.getCartoonImageUrl());

        List<String> steps = new ArrayList<>();
        if (f.getStepDescriptions() != null) {
            for (String s : f.getStepDescriptions()) {
                if (s != null) {
                    s = s.trim();
                    if (!s.isEmpty()) steps.add(s);
                }
            }
        }

        d.getSteps().clear();

        int pos = 1;
        for (String desc : steps) {
            Step s = new Step();
            s.setDish(d);
            s.setDescription(desc);
            d.getSteps().add(s);
        }

        d.setRecipe(String.join("\n", steps));

        dishRepository.saveAndFlush(d);
    }



    @Override
    @Transactional(readOnly = true)
    public List<Dish> findAllByAuthor(User user) {
        return dishRepository.findAllByAuthor(user);
    }

    private static String safe(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }

    private static List<String> normalizeSteps(List<String> raw) {
        if (raw == null) return List.of();
        return raw.stream()
                .map(s -> s == null ? null : s.trim())
                .filter(s -> s != null && !s.isEmpty())
                .toList();
    }

    private void persistStepsForDish(Dish dish, List<String> steps) {
        for (int i = 0; i < steps.size(); i++) {
            Step step = new Step();
            step.setDish(dish);
            step.setDescription(steps.get(i));
            stepRepository.save(step);
        }
    }
}
